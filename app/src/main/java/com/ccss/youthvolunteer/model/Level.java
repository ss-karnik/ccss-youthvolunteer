package com.ccss.youthvolunteer.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Level")
public class Level extends ParseObject {
    public String getTitle(){
        return getString("title");
    }

    public void setTitle(String value){
        put("title", value);
    }

    public int getOrder(){
        return getInt("order");
    }

    public void setOrder(int value){
        put("order", value);
    }

    public int getPointsRequired(){
        return getInt("pointsRequired");
    }

    public void setPointsRequired(int value){
        put("pointsRequired", value);
    }

    public String getDescription(){
        return getString("description");
    }

    public void setDescription(String value){
        put("description", value);
    }
}

