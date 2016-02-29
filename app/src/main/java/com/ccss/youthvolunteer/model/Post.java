package com.ccss.youthvolunteer.model;

import android.content.Context;
import android.text.format.DateFormat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {

    public VolunteerUser getPostBy() {
        return (VolunteerUser)getParseUser("postBy");
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
}
