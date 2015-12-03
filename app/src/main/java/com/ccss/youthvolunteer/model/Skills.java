package com.ccss.youthvolunteer.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ParseClassName("Skill")
public class Skills extends ParseObject {

    public String getSkillTitle(){
        return getString("title");
    }

    public void setSkillTitle(String value){
        put("title", value);
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

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public void setImage(ParseFile value) {
        put("image", value);
    }

    /**
     * Creates a query for Skills with all the includes
     */
    private static ParseQuery<Skills> createQuery() {
        ParseQuery<Skills> query = new ParseQuery<>(Skills.class);
        query.orderByAscending("title");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(this.getSkillTitle(), this.getDescription(),
                "", this.getObjectId(), this.getImage() == null ? "" : this.getImage().getUrl(), true);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class SkillsFindCallback implements FindCallback<Skills> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Skills> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Skills> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Skills, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Skills> callback) {
        ParseQuery<Skills> query = Skills.createQuery();

        query.findInBackground(new SkillsFindCallback() {
            @Override
            protected void doneOnce(List<Skills> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }
}
