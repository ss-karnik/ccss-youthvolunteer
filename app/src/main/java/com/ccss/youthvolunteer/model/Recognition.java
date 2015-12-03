package com.ccss.youthvolunteer.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Recognition")
public class Recognition extends ParseObject {

    public RecognitionType getRecognitionType(){
        return RecognitionType.valueOf(getString("recognitionType"));
    }

    public void setRecognitionType(RecognitionType value) {
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

}