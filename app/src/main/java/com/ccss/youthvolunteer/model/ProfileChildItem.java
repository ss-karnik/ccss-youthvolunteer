package com.ccss.youthvolunteer.model;

import android.media.Image;

public class ProfileChildItem {
    private String mTitle;
    private String mDescription;
    private Image mImage;
    private boolean mSelected;

    public ProfileChildItem(String mTitle, String mDescription, Image mImage, boolean mSelected) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mImage = mImage;
        this.mSelected = mSelected;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String value){
        mTitle = value;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String value){
        mDescription = value;
    }

    public Image getImage() {
        return mImage;
    }

    public void setImage(Image value) {
        mImage = value;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
