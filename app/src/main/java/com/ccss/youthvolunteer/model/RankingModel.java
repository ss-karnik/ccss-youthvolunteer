package com.ccss.youthvolunteer.model;

import java.util.List;

public class RankingModel {

    String name;
    String userLevel;
    String userSchool;
    String pointsAndRank;
    String profilePhotoUri;
    private List<RankingModel> persons;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String value) {
        this.userLevel = value;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String value) {
        this.userSchool = value;
    }

    public String getPointsAndRank() {
        return pointsAndRank;
    }

    public void setPointsAndRank(String pointsAndRank) {
        this.pointsAndRank = pointsAndRank;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri.trim();
    }

    public void setProfilePhotoUri(String value) {
        this.profilePhotoUri = value;
    }

    public List<RankingModel> getPersons() {
        return persons;
    }

    public void setPersons(List<RankingModel> persons) {
        this.persons = persons;
    }

    public RankingModel(String name, String userLevel, String userSchool, String pointsAndRank, String profilePhotoUri) {
        this.name = name;
        this.userLevel = userLevel;
        this.userSchool = userSchool;
        this.pointsAndRank = pointsAndRank;
        this.profilePhotoUri = profilePhotoUri;
    }
}
