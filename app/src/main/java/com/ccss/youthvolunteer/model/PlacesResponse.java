package com.ccss.youthvolunteer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResponse {

    @SerializedName("html_attributions")
    public List<String> html_attributions;

    @SerializedName("next_page_token")
    public String next_page_token;

    @SerializedName("results")
    public List<PlaceInfo> results;

    @SerializedName("result")
    public PlaceInfo result;

    @SerializedName("status")
    public String status;

    @SerializedName("error_message")
    public String error_message;
}
