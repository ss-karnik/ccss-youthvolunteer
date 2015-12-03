package com.ccss.youthvolunteer.model;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@ParseClassName("UserRecognition")
public class UserRecognition extends ParseObject {
    public boolean isPublishedToFb() {
        return getBoolean("publishedToFb");
    }

    public void setPublishedToFb(boolean value){
        put("publishedToFb", value);
    }

    public boolean isUserRejectedFbPublish() {
        return getBoolean("userRejectedFbPublish");
    }

    public void setUserRejectedFbPublish(boolean value){
        put("userRejectedFbPublish", value);
    }

    public Date getDateAchieved() {
        return getDate("dateAchieved");
    }

    public void setDateAchieved(Date value){
        put("dateAchieved", value);
    }

    public ParseUser getAchievedBy() {
        return getParseUser("achievedBy");
    }

    public void setAchievedBy(ParseUser value){
        put("achievedBy", value);
    }

    public Recognition getRecognition() {
        return (Recognition)getParseObject("recognition");
    }

    public void setRecognition(Recognition value){
        put("recognition", value);
    }

    public static void findLatestUserRecognitionInBackground(ParseUser user, final GetCallback<UserRecognition> callback) {
        ParseQuery<UserRecognition> userRecognitionQuery = ParseQuery.getQuery(UserRecognition.class);
        userRecognitionQuery.whereEqualTo("achievedBy", user);
        userRecognitionQuery.addDescendingOrder("dateAchieved");
        userRecognitionQuery.getFirstInBackground(new GetCallback<UserRecognition>() {
            @Override
            public void done(UserRecognition userRecognition, ParseException e) {
                if (e == null) {
                    callback.done(userRecognition, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }
}
