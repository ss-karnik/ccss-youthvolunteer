package com.ccss.youthvolunteer.model;

import android.content.Context;
import android.text.format.DateFormat;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    public ParseUser getPostBy() {
        return getParseUser("postBy");
    }

    public void setPostBy(ParseUser value){
        put("postBy", value);
    }

    public Post getParentPost() {
        return (Post)getParseObject("parentPost");
    }

    public void setParentPost(Post value){
        put("parentPost", value);
    }

    public String getPostText() {
        return getString("postText");
    }

    public void setPostText(String value) {
        put("postText", value);
    }

    public int getCommentsCount() {
        return getInt("commentsCount");
    }

    public void setCommentsCount(int value){
        put("commentsCount", value);
    }

    public void incrementCommentsCountBy(int value) {
        increment("commentsCount", value);
    }

    public int getLikesCount() {
        return getInt("likesCount");
    }

    public void setLikesCount(int value){
        put("likesCount", value);
    }

    public void incrementLikesCountBy(int value) {
        increment("likesCount", value);
    }

    /**
     * Returns a string representation of the time slot suitable for use in the
     * UI.
     */
    public String formattedCreatedTime(Context context) {
        return DateFormat.getTimeFormat(context).format(getCreatedAt());
    }

    public String formattedEditedTime(Context context) {
        return DateFormat.getTimeFormat(context).format(getUpdatedAt());
    }

    public boolean isEdited() {
        return !(getCreatedAt().compareTo(getUpdatedAt()) == 0);
    }

    /**
     * Creates a query for Post
     */
    private static ParseQuery<Post> createQuery() {
        ParseQuery<Post> query = new ParseQuery<>(Post.class);
        query.orderByAscending("createdDate");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class PostFindCallback implements FindCallback<Post> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Post> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Post> objects, ParseException e);
    }

    public static void findInBackground(final FindCallback<Post> callback) {
        ParseQuery<Post> query = Post.createQuery();

        query.findInBackground(new PostFindCallback() {
            @Override
            protected void doneOnce(List<Post> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }
}
