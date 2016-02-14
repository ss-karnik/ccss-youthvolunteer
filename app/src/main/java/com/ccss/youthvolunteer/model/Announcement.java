package com.ccss.youthvolunteer.model;

import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

@ParseClassName("Announcement")
public class Announcement extends ParseObject {

    public String getAnnouncementText(){
        return getString("text");
    }

    public void setAnnouncementText(String value){
        put("text", value);
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public static List<String> getAllAnnouncementText(){
        ParseQuery<Announcement> query = ParseQuery.getQuery(Announcement.class);
        query.whereEqualTo("isActive", true);
        query.selectKeys(Collections.singletonList("text"));
        try {
            List<Announcement> result = query.find();
            if(result.isEmpty()){
                return Lists.newArrayList();
            }
            return Lists.transform(query.find(), new Function<Announcement, String>() {
                @Override
                public String apply(Announcement input) {
                    return input.getAnnouncementText();
                }
            });
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class AnnouncementFindCallback implements FindCallback<Announcement> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Announcement> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Announcement> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Announcement.
     */
    public static void findInBackground(final FindCallback<Announcement> callback) {
        ParseQuery<Announcement> query = ParseQuery.getQuery(Announcement.class);

        query.findInBackground(new AnnouncementFindCallback() {
            @Override
            protected void doneOnce(List<Announcement> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveAnnouncement(Announcement announcementData, final SaveCallback onSave){
        announcementData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }

    public ResourceModel convertToResourceModel(){
        return new ResourceModel(Constants.ANNOUNCEMENT_RESOURCE, "", this.getAnnouncementText(),
                "", this.getObjectId(), "", this.isActive());
    }
}
