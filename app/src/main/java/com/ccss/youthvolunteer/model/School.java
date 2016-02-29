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

@ParseClassName("School")
public class School extends ParseObject {

    public String getSchoolName(){
        return getString("schoolName");
    }

    public void setSchoolName(String value){
        put("schoolName", value);
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

    public static List<String> getAllActiveSchools(){
        ParseQuery<School> schoolQuery = ParseQuery.getQuery(School.class);
        schoolQuery.whereEqualTo("isActive", true);
        schoolQuery.selectKeys(Collections.singletonList("schoolName"));
        try {
            return Lists.transform(schoolQuery.find(), new Function<School, String>() {
                @Override
                public String apply(School input) {
                    return input.getSchoolName();
                }
            });
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.SCHOOL_RESOURCE, this.getSchoolName(), this.getDescription(), "", this.getObjectId(),
                this.getLogo() == null ? "" : this.getLogo().getUrl(), this.isActive());
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class SchoolFindCallback implements FindCallback<School> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<School> objects, ParseException e) {
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
        protected abstract void doneOnce(List<School> objects, ParseException e);
    }

    public static void findInBackground(final FindCallback<School> callback) {
        ParseQuery<School> query = new ParseQuery<>(School.class);
        query.orderByAscending("schoolName");

        query.findInBackground(new SchoolFindCallback() {
            @Override
            protected void doneOnce(List<School> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveSchool(School schoolData, final SaveCallback onSave){
        schoolData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}

