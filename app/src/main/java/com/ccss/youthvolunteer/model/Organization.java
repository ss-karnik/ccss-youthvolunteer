package com.ccss.youthvolunteer.model;

import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

@ParseClassName("Organization")
public class Organization extends ParseObject {

    public String getOrganizationName(){
        return getString("name");
    }

    public void setOrganizationName(String value){
        put("name", value);
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

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public ParseFile getLogo() {
        return getParseFile("logoImage");
    }

    public void setLogo(ParseFile value) {
        put("logoImage", value);
    }

    public static List<String> getAllActiveOrganizationNames(){
        ParseQuery<Organization> orgQuery = ParseQuery.getQuery(Organization.class);
        orgQuery.whereEqualTo("isActive", true);
        orgQuery.selectKeys(Collections.singletonList("name"));
        try {
            return Lists.transform(orgQuery.find(), new Function<Organization, String>() {
                @Override
                public String apply(Organization input) {
                    return input.getOrganizationName();
                }
            });
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.ORGANIZATION_RESOURCE, this.getOrganizationName(), "", "", this.getObjectId(),
                this.getLogo() == null ? "" : this.getLogo().getUrl(), this.isActive());
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class OrganizationFindCallback implements FindCallback<Organization> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Organization> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Organization> objects, ParseException e);
    }

    public static void findInBackground(final FindCallback<Organization> callback) {
        ParseQuery<Organization> query = new ParseQuery<>(Organization.class);
        query.orderByAscending("name");

        query.findInBackground(new OrganizationFindCallback() {
            @Override
            protected void doneOnce(List<Organization> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveOrganization(Organization organizationData, final SaveCallback onSave){
        organizationData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}