package com.ccss.youthvolunteer.model;

import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("Recognition")
public class Recognition extends ParseObject {

    public String getRecognitionType(){
        return getString("recognitionType");
    }

    public void setRecognitionType(String value) {
        put("recognitionType", value);
    }

    public int getPointsRequired(){
        return getInt("pointsRequired");
    }

    public void setPointsRequired(int value){
        put("pointsRequired", value);
    }

    public String getTitle(){
        return getString("title");
    }

    public void setTitle(String value){
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

    public ParseFile getImage(){
        return getParseFile("image");
    }

    public void setImage(ParseFile value){
        put("image", value);
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public boolean isHoursBased() {
        return getBoolean("isHoursBased");
    }

    public void setIsHoursBased(boolean value){
        put("isHoursBased", value);
    }

    public int getMaxUnits() {
        return getInt("maxUnits");
    }

    public void setMaxUnits(int value){
        put("maxUnits", value);
    }

    public String getForOpportunity(){
        return getString("forAction");
    }

    public void setForOpportunity(String value){
        put("forAction", value);
    }

    /**
     * Creates a query for Interest with all the includes
     */
    private static ParseQuery<Recognition> createQuery() {
        ParseQuery<Recognition> query = new ParseQuery<>(Recognition.class);
        query.orderByAscending("categoryName");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class RecognitionFindCallback implements FindCallback<Recognition> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Recognition> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Recognition> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Recognitions, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Recognition> callback) {
        ParseQuery<Recognition> query = Recognition.createQuery();

        query.findInBackground(new RecognitionFindCallback() {
            @Override
            protected void doneOnce(List<Recognition> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveRecognition(Recognition recognitionData, final SaveCallback onSave){
        recognitionData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }

    public ResourceModel convertToResourceModel(){
        return new ResourceModel(Constants.RECOGNITION_RESOURCE, this.getTitle(), this.getDescription(), "",
                this.getRecognitionType(), "Points req,:" + this.getPointsRequired() + ". ", "", this.getObjectId(),
                this.getImage() == null ? "" : this.getImage().getUrl(), this.isActive(), false);
    }
}