package com.ccss.youthvolunteer.model;


import com.ccss.youthvolunteer.util.Constants;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

@ParseClassName("SpecialUser")
public class SpecialUser extends ParseObject {
    private static List<SpecialUser> specialUsers;

    public String getEmailId(){
        return getString("emailId");
    }

    public void setEmailId(String value){
        put("emailId", value);
    }

    public String getDescription() {
        String description = getString("description");
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String value){
        put("description", value);
    }

    public String getOrganizationName(){
        return getString("organizationName");
    }

    public void setOrganizationName(String value){
        put("organizationName", value);
    }

    public String getRole(){
        return getString("role");
    }

    public void setRole(String value){
        put("role", value);
    }

    public boolean isSiteAdmin(){
        return getBoolean("siteAdmin");
    }

    public void setSiteAdmin(boolean value){
        put("siteAdmin", value);
    }

    public boolean isActive(){
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    /**
     * Retrieves the set of all Interest, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<SpecialUser> callback) {
        ParseQuery<SpecialUser> query = new ParseQuery<>(SpecialUser.class);
        query.orderByAscending("role");
        query.findInBackground(new SpecialUserFindCallback() {
            @Override
            protected void doneOnce(List<SpecialUser> objects, ParseException e) {
                try {
                    pinAll("SpecialUsers", objects);
                    callback.done(objects, e);
                } catch (ParseException ex) {
                    specialUsers = Lists.newArrayList();
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class SpecialUserFindCallback implements FindCallback<SpecialUser> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<SpecialUser> objects, ParseException e) {
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
         * Override this method with the callback that should only be called once.
         */
        protected abstract void doneOnce(List<SpecialUser> objects, ParseException e);
    }

    public static List<SpecialUser> getAllSpecialUsers() {

        ParseQuery<SpecialUser> specialUserQuery = ParseQuery.getQuery(SpecialUser.class);
        specialUserQuery.whereEqualTo("isActive", true);
        specialUserQuery.findInBackground(new FindCallback<SpecialUser>() {
            @Override
            public void done(List<SpecialUser> objects, ParseException e) {
                if (e == null) {
                    specialUsers = objects;
                    try {
                        pinAll("SpecialUsers", objects);
                    } catch (ParseException ex) {
                        specialUsers = Lists.newArrayList();
                        ex.printStackTrace();
                    }
                } else {
                    specialUsers = Lists.newArrayList();
                }
            }
        });

        return specialUsers != null ? specialUsers : Collections.EMPTY_LIST;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SpecialUser other = (SpecialUser) obj;
        return this.getEmailId().equalsIgnoreCase(other.getEmailId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.USER_RESOURCE, this.getEmailId(), this.getOrganizationName(), this.getRole(),
                this.getObjectId(), "", this.isActive());
    }

    public static void saveSpecialUser(SpecialUser userData, final SaveCallback onSave){
        userData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}
