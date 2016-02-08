package com.ccss.youthvolunteer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.List;

@ParseClassName("Interest")
public class Interest extends ParseObject implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;
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

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public ParseFile getLogo() {
        return getParseFile("image");
    }

    public void setLogo(ParseFile value) {
        put("image", value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Interest other = (Interest) obj;
        return !((this.getObjectId() == null)
                ? (other.getObjectId() != null)
                : !this.getObjectId().equals(other.getObjectId()))
                && this.getInterestTitle().equals(other.getInterestTitle());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.getObjectId() != null ? this.getObjectId().hashCode() : 0);
        hash = 53 * hash + (this.getInterestTitle() != null ? this.getInterestTitle().hashCode() : 0);
        return hash;
    }

    /**
     * Creates a query for Interest with all the includes
     */
    private static ParseQuery<Interest> createQuery() {
        ParseQuery<Interest> query = new ParseQuery<>(Interest.class);
        query.orderByAscending("title");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.INTEREST_RESOURCE, this.getInterestTitle(), this.getDescription(),
                "", this.getObjectId(), this.getLogo() == null ? "" : this.getLogo().getUrl(), true);
    }


    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class InterestsFindCallback implements FindCallback<Interest> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Interest> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Interest> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Interest, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Interest> callback) {
        ParseQuery<Interest> query = Interest.createQuery();

        query.findInBackground(new InterestsFindCallback() {
            @Override
            protected void doneOnce(List<Interest> objects, ParseException e) {
//                if (objects != null) {
//                    Collections.sort(objects, new Comparator<Interest>() {
//                        @Override
//                        public int compare(Interest first, Interest second) {
//                            return first.getInterestTitle().compareTo(second.getInterestTitle());
//                        }
//                    });
//                }
                callback.done(objects, e);
            }
        });
    }

    public static void saveInterest(Interest interestData, final SaveCallback onSave){
        interestData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}