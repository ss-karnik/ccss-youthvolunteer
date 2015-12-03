package com.ccss.youthvolunteer.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Themes")
public class Theme extends ParseObject {

    public String getThemeName(){
        return getString("name");
    }

    public void setThemeName(String value){
        put("name", value);
    }

    public String getStyleClassName(){
        return getString("styleClassName");
    }

    public void setStyleClassName(String value){
        put("styleClassName", value);
    }
}

