package com.ccss.youthvolunteer.model;

import android.provider.ContactsContract;

import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ParseClassName("Interest")
public class Interests extends ParseObject {
    public String getInterestTitle(){
        return getString("title");
    }

    public void setInterestTitle(String value){
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
     * Creates a query for Interests with all the includes
     */
    private static ParseQuery<Interests> createQuery() {
        ParseQuery<Interests> query = new ParseQuery<>(Interests.class);
        query.orderByAscending("title");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.INTEREST_RESOURCE, this.getInterestTitle(), this.getDescription(),
                "", this.getObjectId(), this.getImage() == null ? "" : this.getImage().getUrl(), true);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class InterestsFindCallback implements FindCallback<Interests> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Interests> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Interests> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Interests, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Interests> callback) {
        ParseQuery<Interests> query = Interests.createQuery();

        query.findInBackground(new InterestsFindCallback() {
            @Override
            protected void doneOnce(List<Interests> objects, ParseException e) {
//                if (objects != null) {
//                    Collections.sort(objects, new Comparator<Interests>() {
//                        @Override
//                        public int compare(Interests first, Interests second) {
//                            return first.getInterestTitle().compareTo(second.getInterestTitle());
//                        }
//                    });
//                }
                callback.done(objects, e);
            }
        });
    }
}