package com.ccss.youthvolunteer.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ParseClassName("UserCategoryPoints")
public class UserCategoryPoints extends ParseObject {

    public String getActionMonthYear() {
        return getString("monthYear");
    }

    /*
    Should be in the format MMM-yyyy
     */
    public void setActionMonthYear(String value){
        put("monthYear", value);
    }

    public Category getActionCategory(){
        return (Category)getParseObject("category");
    }

    public void setActionCategory(String value){
        put("category", Category.createWithoutData(Category.class, value));
    }

    public ParseUser getActionUser() {
        return getParseUser("user");
    }

    public void setActionUser(ParseUser value){
        put("user", ParseUser.getCurrentUser());
    }


    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class VolunteerCategoryPointsFindCallback implements FindCallback<UserCategoryPoints> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<UserCategoryPoints> objects, ParseException e) {
            if (!calledCallback) {
                if (objects != null) {
                    // We got a result, use it.
                    calledCallback = true;
                    doneOnce(objects, null);
                } else if (!isCachedResult) {
                    // We got called back twice, but got a null result both
                    // times. Pass on the latest error.
                    doneOnce(null, e);
                }
            }
            isCachedResult = false;
        }

        /**
         * Override this method with the callback that should only be called
         * once.
         */
        protected abstract void doneOnce(List<UserCategoryPoints> objects, ParseException e);
    }

    public static void findCurrentUserPointsInBackground(ParseUser user, int count, final FindCallback<UserCategoryPoints> callback){
        ParseQuery<UserCategoryPoints> userPointsQuery = ParseQuery.getQuery(UserCategoryPoints.class);
        userPointsQuery.whereEqualTo("user", user);
        userPointsQuery.addDescendingOrder("createdAt");
        userPointsQuery.include("category");
        userPointsQuery.setLimit(count);
        userPointsQuery.findInBackground(new VolunteerCategoryPointsFindCallback() {
            @Override
            protected void doneOnce(List<UserCategoryPoints> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void findAllUsersPointsInBackground(final FindCallback<UserCategoryPoints> callback){
        ParseQuery<UserCategoryPoints> userPointsQuery = ParseQuery.getQuery(UserCategoryPoints.class);
        userPointsQuery.addDescendingOrder("createdAt");
        userPointsQuery.include("category");
        userPointsQuery.findInBackground(new VolunteerCategoryPointsFindCallback() {
            @Override
            protected void doneOnce(List<UserCategoryPoints> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void findAllUsersPointsForCurrentMonthYearInBackground(final FindCallback<UserCategoryPoints> callback){
        ParseQuery<UserCategoryPoints> userPointsQuery = ParseQuery.getQuery(UserCategoryPoints.class);
        userPointsQuery.whereEqualTo("monthYear", DateTime.now().toString("MMyyyy"));
        userPointsQuery.addDescendingOrder("createdAt");
        userPointsQuery.include("category.categoryName");
        userPointsQuery.findInBackground(new VolunteerCategoryPointsFindCallback() {
            @Override
            protected void doneOnce(List<UserCategoryPoints> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }
}
