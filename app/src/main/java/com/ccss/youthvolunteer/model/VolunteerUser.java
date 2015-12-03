package com.ccss.youthvolunteer.model;

import android.content.Context;
import android.text.format.DateFormat;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ParseClassName("_User")
public class VolunteerUser extends ParseUser {

    public static VolunteerUser getCurrentUser() {
        return ParseUser.getCurrentUser() != null ? (VolunteerUser)(ParseUser.getCurrentUser()) : null;
    }

    public static VolunteerUser getCurrentUserInformation(ParseUser currUser) {
    ParseQuery<VolunteerUser> userQuery = ParseQuery.getQuery(VolunteerUser.class);
        userQuery.whereEqualTo("email", currUser.getEmail());
        try {
            return userQuery.getFirst();
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

    public List<String> getUserSkills() {
        String skills = getString("skills");
        if(!Strings.isNullOrEmpty(skills)){
            return (Arrays.asList(skills.split("\\s*,\\s*")));
        }
        return Lists.newArrayList();
    }

    public void setUserSkills(String value){
        put("skills", value);
    }

    public List<String> getUserInterests() {
        String interests = getString("interests");
        if(!Strings.isNullOrEmpty(interests)){
            return (Arrays.asList(interests.split("\\s*,\\s*")));
        }
        return Lists.newArrayList();
    }

    public void setUserInterests(String value){
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
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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
    public static void findUsersRanked(final FindCallback<VolunteerUser> callback){
        ParseQuery<VolunteerUser> userQuery = createQuery();
        userQuery.include("lastName");
        userQuery.include("firstName");
        userQuery.include("pointsAccrued");
        userQuery.include("profileImage");
        userQuery.include("schoolName");
        userQuery.include("userLevel");

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