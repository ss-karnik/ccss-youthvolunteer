package com.ccss.youthvolunteer.model;

public class Location {
    public double latitude;
    public double longitude;

    public Location(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }
}