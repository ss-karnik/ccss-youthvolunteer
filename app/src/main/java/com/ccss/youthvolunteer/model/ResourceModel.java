package com.ccss.youthvolunteer.model;

@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
public class ResourceModel {
    String title;
    String description;
    String extraInformationTopRight;
    String extraInformationBottom;
    String extraInformationBelowDesc;
    String borderColor;
    String objectId;
    String imageUri;
    String resourceType;
    boolean active;
    boolean selected;
    boolean starred;

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

    public String getExtraInformationTopRight() {
        return extraInformationTopRight;
    }

    public void setExtraInformationTopRight(String value) {
        this.extraInformationTopRight = value;
    }

    public String getExtraInformationBottom() {
        return extraInformationBottom;
    }

    public void setExtraInformationBottom(String value) {
        this.extraInformationBottom = value;
    }

    public String getExtraInformationBelowDesc() {
        return extraInformationBelowDesc;
    }

    public void setExtraInformationBelowDesc(String value) {
        this.extraInformationBelowDesc = value;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String value) {
        this.borderColor = value;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String value) {
        this.objectId = value;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
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

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean value) {
        this.starred = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public ResourceModel(String resourceType, String title, String description, String borderColor,
                         String extraInformationTopRight, String extraInformationBelowDesc, String extraInformationBottom,
                         String objectId, String imageUri, boolean active, boolean starred) {
        this.setResourceType(resourceType);
        this.title = title;
        this.description = description;
        this.borderColor = borderColor;
        this.extraInformationTopRight = extraInformationTopRight;
        this.extraInformationBelowDesc = extraInformationBelowDesc;
        this.extraInformationBottom = extraInformationBottom;
        this.objectId = objectId;
        this.imageUri = imageUri;
        this.active = active;
        this.starred = starred;
        this.selected = false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && getResourceType().equalsIgnoreCase(((ResourceModel) obj).getResourceType())
                && this.getObjectId().equals(((ResourceModel) obj).getObjectId())
                && getTitle().equalsIgnoreCase(((ResourceModel) obj).getTitle());

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.getResourceType() != null ? this.getResourceType().hashCode() : 0);
        hash = 53 * hash + (this.getObjectId() != null ? this.getObjectId().hashCode() : 0);
        return hash;
    }
}
