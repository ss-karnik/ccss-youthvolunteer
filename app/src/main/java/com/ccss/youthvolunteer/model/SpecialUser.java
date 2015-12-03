package com.ccss.youthvolunteer.model;


import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("SpecialUser")
public class SpecialUser extends ParseObject {
    static List<SpecialUser> specialUsers;

    public String getEmailId(){
        return getString("emailId");
    }

    public void setEmailId(String value){
        put("emailId", value);
    }

    public String getOrganizationName(){
        return getString("organizationName");
    }

    public void setOrganizationName(String value){
        put("organizationName", value);
    }

    public String getRole(){
        return getString("role");
    }

    public void setRole(String value){
        put("role", value);
    }
    public boolean isSiteAdmin(){
        return getBoolean("siteAdmin");
    }

    public void setSiteAdmin(boolean value){
        put("siteAdmin", value);
    }

    public boolean isActive(){
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public static List<SpecialUser> getAllSpecialUsers() {

        ParseQuery<SpecialUser> specialUserQuery = ParseQuery.getQuery(SpecialUser.class);
        specialUserQuery.whereEqualTo("isActive", true);
        specialUserQuery.findInBackground(new FindCallback<SpecialUser>() {
            @Override
            public void done(List<SpecialUser> objects, ParseException e) {
                if (e == null) {
                    specialUsers = objects;
                    try {
                        pinAll("SpecialUsers", objects);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        return Lists.newArrayList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SpecialUser other = (SpecialUser) obj;
        return this.getEmailId().equalsIgnoreCase(other.getEmailId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(this.getEmailId(), this.getOrganizationName(), this.getRole(),
                this.getObjectId(), "", this.isActive());
    }
}
