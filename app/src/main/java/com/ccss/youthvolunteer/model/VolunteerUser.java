package com.ccss.youthvolunteer.model;

import android.content.Context;
import android.text.format.DateFormat;

import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ParseClassName("_User")
public class VolunteerUser extends ParseUser implements Serializable {

    public static VolunteerUser getCurrentUser() {
        return ParseUser.getCurrentUser() != null ? (VolunteerUser)(ParseUser.getCurrentUser()) : null;
    }

    public static VolunteerUser getCurrentUserInformation(ParseUser currUser) {
        ParseQuery<VolunteerUser> userQuery = ParseQuery.getQuery(VolunteerUser.class);
        userQuery.include("skills");
        userQuery.include("interests");
        userQuery.whereEqualTo("email", currUser.getEmail());
        try {
            return userQuery.getFirst();
        } catch (ParseException e) {
            return getCurrentUser();
        }
    }

    public static VolunteerUser getVolunteerUser(ParseUser currUser) {
        ParseQuery<VolunteerUser> userQuery = ParseQuery.getQuery(VolunteerUser.class);
        userQuery.whereEqualTo("email", currUser.getEmail());
        try {
            return userQuery.getFirst();
        } catch (ParseException e) {
            return getCurrentUser();
        }
    }

    public static VolunteerUser getCurrentUserInformationFromLocalStore() {
        ParseQuery<VolunteerUser> userQuery = ParseQuery.getQuery(VolunteerUser.class).fromLocalDatastore();
        try {
            VolunteerUser currentUser = userQuery.getFirst();
            if(currentUser == null){
                return getCurrentUserInformation(ParseUser.getCurrentUser());
            }
            return currentUser;
        } catch (ParseException e) {
            return getCurrentUser();
        }
    }

    public boolean isEmailVerified() {
        return getBoolean("emailVerified");
    }

    public void setEmailVerified(boolean value){
        put("emailVerified", value);
    }

    public boolean isFbLogin() {
        return getBoolean("facebookLogin");
    }

    public void setFbLogin(boolean value){
        put("facebookLogin", value);
    }

    public boolean isProfileComplete() {
        return getBoolean("profileComplete");
    }

    public void setProfileComplete(boolean value){
        put("profileComplete", value);
    }

    public String getSpecialRole() {
        return getString("specialRole");
    }

    public void setSpecialRole(String value){
        put("specialRole", value);
    }

    public Date getDateOfBirth(){
        return getDate("dateOfBirth");
    }

    public String formattedDateOfBirth(Context context) {
        return DateFormat.getMediumDateFormat(context).format(getDateOfBirth());
    }

    public void setDateOfBirth(Date value){
        put("dateOfBirth", value);
    }

    public String getGender(){
        return getString("gender");
    }

    public void setGender(String value){
        put("gender", value);
    }

    public String getMobileNumber(){
        return getString("mobileNumber");
    }

    public void setMobileNumber(String value){
        put("mobileNumber", value);
    }

    public int getPointsAccrued(){
        return getInt("pointsAccrued");
    }

    /* Use incrementPointsBy instead */
    public void setPointsAccrued(int value){
        put("pointsAccrued", value);
    }

    public void incrementPointsBy(int value) { increment("pointsAccrued", value);}

    public int getMonthlyGoal(){
        return getInt("monthlyGoal");
    }

    public void setMonthlyGoal(int value){
        put("monthlyGoal", value);
    }

    public String getSchoolName(){
        return getString("schoolName");
    }

    public void setSchoolName(String value){
        put("schoolName", value);
    }

    public String getFirstName(){
        return getString("firstName");
    }

    public void setFirstName(String value){
        put("firstName", value);
    }

    public String getLastName(){
        return getString("lastName");
    }

    public void setLastName(String value){
        put("lastName", value);
    }

    public boolean isPublishToFbPermission() { return getBoolean("publishToFbPermission"); }

    public void setPublishToFbPermission(boolean value){
        put("publishToFbPermission", value);
    }

    public Theme getUserTheme(){
        return (Theme)getParseObject("theme");
    }

    public void setUserTheme(Theme value){
        put("theme", value);
    }

    public Level getUserLevel() {
        return (Level)getParseObject("userlevel");
    }

    public void setUserLevel(Level value){
        put("userlevel", value);
    }

    public List<Skill> getUserSkills() {
        ArrayList<Skill> userSkills =  (ArrayList<Skill>) get("skills");
        if(userSkills == null || userSkills.isEmpty()){
            return Lists.newArrayList();
        }

        return userSkills;
    }

    public void setUserSkills(List<Skill> value){
        put("skills", value);
    }

    public List<Interest> getUserInterests() {
        ArrayList<Interest> userInterests =  (ArrayList<Interest>) get("interests");
        if(userInterests == null || userInterests.isEmpty()){
            return Lists.newArrayList();
        }

        return userInterests;
    }

    public void setUserInterests(List<Interest> value){
        put("interests", value);
    }

    public String getFullName() {
        return getLastName() + ", " + getFirstName();
    }

    public String getOrganizationName(){
        return getString("organizationName");
    }

    public void setOrganizationName(String value){
        put("organizationName", value);
    }

    public ParseFile getProfileImage(){
        return getParseFile("profileImage");
    }

    public void setProfileImage(ParseFile value){
        put("profileImage", value);
    }

    public String getProfileImageUri(){
        return getParseFile("profileImage").getUrl();
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public ResourceModel convertToResourceModel(){
        return new ResourceModel(Constants.VOLUNTEER_USER_RESOURCE, this.getFullName(), this.getEmail(),
                "", "", this.getSchoolName(), "User points: " + this.getPointsAccrued(), this.getObjectId(), this.getProfileImageUri(), this.isActive(), false);
    }

    public static ResourceModel convertToResourceModel(ParseUser user){
        VolunteerUser volunteerUser = getVolunteerUser(user);
        return new ResourceModel(Constants.VOLUNTEER_USER_RESOURCE, volunteerUser.getFullName(),
                volunteerUser.getEmail(), "", "", volunteerUser.getSchoolName(), "", volunteerUser.getObjectId(),
                volunteerUser.getProfileImageUri(), volunteerUser.isActive(), false);
    }

    public static int getTotalUserCount(){
        final int[] totalUsers = {0};
        ParseQuery<VolunteerUser> query = ParseQuery.getQuery(VolunteerUser.class);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    totalUsers[0] = count;
                } else {
                    totalUsers[0] = 1;
                }
            }
        });

        return totalUsers[0];
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class UsersFindCallback implements FindCallback<VolunteerUser> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<VolunteerUser> objects, ParseException e) {
            if (!calledCallback) {
                if (objects != null) {
                    calledCallback = true;
                    doneOnce(objects, null);
                } else if (!isCachedResult) {
                    // We got called back twice, but got a null result both
                    // times. Pass on the latest error.
                    doneOnce(null, e);
                }
            }
            isCachedResult = false;
        }

        /**
         * Override this method with the callback that should only be called
         * once.
         */
        protected abstract void doneOnce(List<VolunteerUser> objects, ParseException e);
    }

    /**
     * Creates a query for talks with all the includes
     */
    private static ParseQuery<VolunteerUser> createQuery() {
        ParseQuery<VolunteerUser> query = new ParseQuery<VolunteerUser>(VolunteerUser.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

//    public static void getOpportunitiesNearMe(ParseGeoPoint userLocation , final FindCallback<VolunteerOpportunity> callback){
//        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(true);
//        actionQuery.whereExists("exactLocation").whereNear("exactLocation", userLocation);
//        actionQuery.findInBackground(new VolunteerOpportunityFindCallback() {
//            @Override
//            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
//                if (objects != null) {
//                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
//                }
//                callback.done(objects, e);
//            }
//        });
//    }

    public static void findUsersWithAllSkills(List<Skill> requiredSkills, final FindCallback<VolunteerUser> callback){
//        ParseQuery<VolunteerUser> userQuery = createQuery();
//        userQuery.whereEqualTo()
    }

    public static void findUsersWithAnySkills(List<Skill> requiredSkills, final FindCallback<VolunteerUser> callback){
//        ParseQuery<VolunteerUser> userQuery = createQuery();
//        userQuery.whereEqualTo()
    }

    public static void findUsersWithSkill(Skill requiredSkill, final FindCallback<VolunteerUser> callback){
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.whereEqualTo("skills", requiredSkill);

        userQuery.findInBackground(new VolunteerUser.UsersFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerUser> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void findUsersWithInterest(Interest requiredInterest, final FindCallback<VolunteerUser> callback){
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.whereEqualTo("interests", requiredInterest);

        userQuery.findInBackground(new VolunteerUser.UsersFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerUser> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void findUsersFromSchool(String schoolName, final FindCallback<VolunteerUser> callback){
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.whereEqualTo("schoolName", schoolName);

        userQuery.findInBackground(new VolunteerUser.UsersFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerUser> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void findUsersFromOrganization(String orgName, final FindCallback<VolunteerUser> callback){
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.whereEqualTo("organizationName", orgName);

        userQuery.findInBackground(new VolunteerUser.UsersFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerUser> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static List<VolunteerUser> findUsersRanked() throws ParseException {
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.include("email");
        userQuery.include("pointsAccrued");
        //TODO: Exclude special users from ranking?
        //userQuery.whereEqualTo("specialRole", null);

        return userQuery.find();
    }

    public static void findInBackgroundUsersRanked(final FindCallback<VolunteerUser> callback) {
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.include("lastName");
        userQuery.include("firstName");
        userQuery.include("pointsAccrued");
        userQuery.include("profileImage");
        userQuery.include("schoolName");
        userQuery.include("userLevel");
        //TODO: Exclude special users from ranking?
        //userQuery.whereEqualTo("specialRole", null);

        userQuery.findInBackground(new VolunteerUser.UsersFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerUser> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, new Comparator<VolunteerUser>() {
                        @Override
                        public int compare(VolunteerUser lhs, VolunteerUser rhs) {
                            return lhs.getPointsAccrued() > rhs.getPointsAccrued() ? 1
                                    : lhs.getPointsAccrued() < rhs.getPointsAccrued()
                                    ? -1 : 0;
                        }
                    });
                }
                callback.done(objects, e);
            }
        });
    }

}