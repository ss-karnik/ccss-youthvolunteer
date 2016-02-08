package com.ccss.youthvolunteer.model;

import com.google.common.base.Strings;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ParseClassName("UserAction")
public class UserAction extends ParseObject {

    public static interface Listener {
        void onActionPerformed(UserAction userAction);

        void onPointsAllocated(UserAction userAction);
    }

    public boolean isPointsAllocated() {
        return getBoolean("pointsAllocated");
    }

    public void setPointsAllocated(boolean value){
        put("pointsAllocated", value);
    }

    public Date getActionDate() {
        return getDate("actionDate");
    }

    public void setActionDate(Date value){
        put("actionDate", value);
    }

    public int getActionDuration() {
        return getInt("duration");
    }

    public void setActionDuration(int value){
        put("duration", value);
    }

    public String getActionMonthYear() {
        return getString("monthYear");
    }

    /*
    Should be in the format MMM-yyyy
     */
    public void setActionMonthYear(String value){
        put("monthYear", value);
    }

    public ParseUser getActionBy() {
        return getParseUser("actionBy");
    }

    public void setActionBy(ParseUser value){
        put("actionBy", value);
    }

    public VolunteerOpportunity getAction() {
        return (VolunteerOpportunity)getParseObject("actionPerformed");
    }

    public void setAction(String value){
        put("actionPerformed", UserAction.createWithoutData(UserAction.class, value));
    }

    public boolean isVerified() {
        return !Strings.isNullOrEmpty(getVerifiedBy());
    }

    public String getVerifiedBy() {
        return getString("verifiedBy");
    }

    public void setVerifiedBy(String value){
        put("verifiedBy", value);
    }

    public Date getVerifiedDate() {
        return getDate("verifiedDate");
    }

    public void setVerifiedDate(Date value){
        put("verifiedDate", value);
    }

    public String getComments() {
        String comments = getString("comments");
        if (comments == null) {
            comments = "";
        }
        return comments;
    }

    public void seComments(String value){
        put("comments", value);
    }

    public int getRating() {
        return getInt("rating");
    }

    public void seRating(String value){
        put("rating", value);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class VolunteerActionFindCallback implements FindCallback<UserAction> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<UserAction> objects, ParseException e) {
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
        protected abstract void doneOnce(List<UserAction> objects, ParseException e);
    }

    public static void findLatestUserActionInBackground(ParseUser user, final GetCallback<UserAction> callback) {
        ParseQuery<UserAction> userActionsQuery = ParseQuery.getQuery(UserAction.class);
        userActionsQuery.include("actionPerformed.actionCategory.categoryColor");
        userActionsQuery.whereEqualTo("actionBy", user);
        userActionsQuery.addDescendingOrder("actionDate");
        userActionsQuery.getFirstInBackground(new GetCallback<UserAction>() {
            @Override
            public void done(UserAction userAction, ParseException e) {
                if (e == null) {
                    callback.done(userAction, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void findAllUserActionsInBackground(ParseUser user, final FindCallback<UserAction> callback){
        ParseQuery<UserAction> userActionsQuery = ParseQuery.getQuery(UserAction.class);
        userActionsQuery.include("actionPerformed");
        userActionsQuery.include("actionPerformed.actionCategory.categoryColor");
        userActionsQuery.whereEqualTo("actionBy", user);
        userActionsQuery.findInBackground(new VolunteerActionFindCallback() {
            @Override
            protected void doneOnce(List<UserAction> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, new Comparator<UserAction>() {
                        @Override
                        public int compare(UserAction lhs, UserAction rhs) {
                            int startCompare = lhs.getActionDate().compareTo(rhs.getActionDate());
                            if (startCompare != 0) {
                                return startCompare;
                            }
                            return lhs.getAction().getTitle().compareTo(rhs.getAction().getTitle());
                        }
                    });
                }
                callback.done(objects, e);
            }
        });
    }

    public static void findUsersForAction(VolunteerOpportunity action, final FindCallback<UserAction> callback){
        ParseQuery<UserAction> userActionsQuery = ParseQuery.getQuery(UserAction.class);
        userActionsQuery.include("actionBy");
        userActionsQuery.whereEqualTo("actionPerformed", action);

        userActionsQuery.findInBackground(new VolunteerActionFindCallback() {
            @Override
            protected void doneOnce(List<UserAction> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, new Comparator<UserAction>() {
                        @Override
                        public int compare(UserAction lhs, UserAction rhs) {
                            return ((VolunteerUser.getVolunteerUser(lhs.getActionBy())).getLastName()
                                    .compareTo((VolunteerUser.getVolunteerUser(rhs.getActionBy())).getLastName()));
                        }
                    });
                }
                callback.done(objects, e);
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserAction other = (UserAction) obj;
        if ((this.getAction() == null) ? (other.getAction() != null) : !this.getAction().equals(other.getAction())) {
            return false;
        }
        return this.getActionBy() == other.getActionBy();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.getActionBy() != null ? this.getActionBy().hashCode() : 0);
        hash = 53 * hash + (this.getAction() != null ? this.getAction().hashCode() : 0);
        return hash;
    }


    public boolean shouldAssignPoints(){
        return false;
    }

    public static class UserActionComparator implements Comparator<UserAction> {

        final private static UserActionComparator instance = new UserActionComparator();

        public static UserActionComparator get() {
            return instance;
        }

        @Override
        public int compare(UserAction lhs, UserAction rhs) {
            int startCompare = lhs.getActionDate().compareTo(rhs.getActionDate());
            if (startCompare != 0) {
                return startCompare;
            }
            return lhs.getAction().compareTo(rhs.getAction());
        }
    }
}