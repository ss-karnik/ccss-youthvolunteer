package com.ccss.youthvolunteer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceInfo {
    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("icon")
    public String icon;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("rating")
    public Double rating;

    @SerializedName("reference")
    public String reference;

    @SerializedName("types")
    public List<String> types;

    @SerializedName("vicinity")
    public String vicinity;

    @SerializedName("place_id")
    public String placeId;

    @SerializedName("formatted_address")
    public String formattedAddress;

    @SerializedName("international_phone_number")
    public String internationalPhoneNumber;

    @SerializedName("scope")
    public String scope;
}
