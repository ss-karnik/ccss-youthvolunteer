package com.ccss.youthvolunteer.model;

public class ResourceModel {
    String title;
    String description;
    String extraInformation;
    String objectId;
    String imageUri;
    boolean active;
    boolean selected;

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String value) {
        this.extraInformation = value;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String value) {
        this.objectId = value;
    }

    public String getImageUri() {
        return imageUri.trim();
    }

    public void setImageUri(String value) {
        this.imageUri = value;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public ResourceModel(String title, String description, String extraInformation, String objectId, String imageUri, boolean active) {
        this.title = title;
        this.description = description;
        this.extraInformation = extraInformation;
        this.objectId = objectId;
        this.imageUri = imageUri;
        this.active = active;
        this.selected = false;
    }
}
