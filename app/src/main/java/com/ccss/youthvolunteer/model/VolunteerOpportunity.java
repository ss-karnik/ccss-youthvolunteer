package com.ccss.youthvolunteer.model;

import android.support.annotation.NonNull;

import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.ccss.youthvolunteer.util.VolunteerOpportunityByDateComparator;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ParseClassName("Action")
public class VolunteerOpportunity extends ParseObject implements Comparable<VolunteerOpportunity>, Serializable {

    private boolean mSelected;

    @Override
    public int compareTo(@NonNull VolunteerOpportunity other) {
        int activeCompare = this.isActive() == other.isActive() ? 0 : -1;
        if(activeCompare != 0){
            return activeCompare;
        }

        int startCompare = this.getActionStartDate().compareTo(other.getActionStartDate());
        if(startCompare != 0){
            return startCompare;
        }

        int nameCompare = this.getTitle().compareTo(other.getTitle());
        if(nameCompare != 0){
            return nameCompare;
        }

        int organizationCompare = this.getOrganizationName().compareTo(other.getOrganizationName());
        if(organizationCompare != 0){
            return organizationCompare;
        }

        return this.getLocationName().compareTo(other.getLocationName());
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setIsSelected(boolean value){
        mSelected = value;
    }

    public String getActionLink(){
        return getString("actionLink");
    }

    public void setActionLink(String value){
        put("actionLink", value);
    }

    public int getActionPoints(){
        return getInt("points");
    }

    public void setActionPoints(int value){
        put("points", value);
    }

    public String getDescription() {
        String description = getString("description");
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String value){
        put("description", value);
    }

    public String getTitle(){
        return getString("title");
    }

    public void setTitle(String value){
        put("title", value);
    }

    public String getLocationName(){
        return getString("locationName");
    }

    public void setLocationName(String value){
        put("locationName", value);
    }

    public String getImpact(){
        return getString("impact");
    }

    public void setImpact(String value){
        put("impact", value);
    }

    public String getSpecialFeature(){
        return getString("specialFeature");
    }

    public void setSpecialFeature(String value){
        put("specialFeature", value);
    }

    public List<ParseUser> getInterestedUsers() {
        ArrayList<ParseUser> userInterests =  (ArrayList<ParseUser>) get("interestedUsers");
        if(userInterests == null || userInterests.isEmpty()){
            return Lists.newArrayList();
        }

        return userInterests;
    }

    public void setInterestedUsers(List<ParseUser> value){
        put("interestedUsers", value);
    }

    public ParseGeoPoint getExactLocation(){
        return getParseGeoPoint("exactLocation");
    }

    public void setExactLocation(ParseGeoPoint value){
        put("exactLocation", value);
    }

    public Date getActionStartDate() {
        return getDate("startDate");
    }

    public void setActionStartDate(Date value){
        put("startDate", value);
    }

    public Date getActionEndDate() {
        return getDate("endDate");
    }

    public void setActionEndDate(Date value){
        put("endDate", value);
    }

    public int getActionDuration() {
        return getInt("duration");
    }

    public void setActionDuration(int value){
        put("duration", value);
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public Category getActionCategory(){
        return (Category)getParseObject("actionCategory");
    }

    public void setActionCategory(Category value){
        put("actionCategory", value);
    }

    public String getOrganizationName(){
        return getString("organizationName");
    }

    public void setOrganizationName(String value){
        put("organizationName", value);
    }

    public List<Skill> getRequiredSkills() {
        ArrayList<Skill> requiredSkills =  (ArrayList<Skill>) get("requiredSkills");
        if(requiredSkills == null || requiredSkills.isEmpty()){
            return Lists.newArrayList();
        }

        return requiredSkills;
    }

    public void setRequiredSkills(List<Skill> value){
        put("requiredSkills", value);
    }

    public List<Interest> getRequiredInterests() {
        ArrayList<Interest> requiredInterests =  (ArrayList<Interest>) get("requiredInterests");
        if(requiredInterests == null || requiredInterests.isEmpty()){
            return Lists.newArrayList();
        }

        return requiredInterests;
    }

    public void setRequiredInterests(List<Interest> value){
        put("requiredInterests", value);
    }

    public boolean isVirtual() {
        return getBoolean("isVirtual");
    }

    public void setIsVirtual(boolean value){
        put("isVirtual", value);
    }

    public boolean isPastActivity(){
        return this.getActionStartDate().before(LocalDate.now().toDate());
    }

    @Override
    public String toString() {
        return String.format("Category: %s; Title: %s", this.getActionCategory(), this.getTitle()) ;
    }

    public static String cloneOpportunity(VolunteerOpportunity original){
        VolunteerOpportunity cloned = new VolunteerOpportunity();
        cloned.setIsVirtual(original.isVirtual());
        cloned.setIsActive(true);
        cloned.setOrganizationName(original.getOrganizationName());
        cloned.setActionCategory(original.getActionCategory());
        cloned.setActionDuration(original.getActionDuration());
        cloned.setActionPoints(original.getActionPoints());
        cloned.setActionLink(original.getActionLink());
        cloned.setDescription(original.getDescription());
        cloned.setImpact(original.getImpact());
        cloned.setRequiredInterests(original.getRequiredInterests());
        cloned.setRequiredSkills(original.getRequiredSkills());
        cloned.setExactLocation(original.getExactLocation());
        cloned.setLocationName(original.getLocationName());
        cloned.setSpecialFeature(original.getSpecialFeature());

        try {
            cloned.save();
        } catch (ParseException e) {
            return "";
        }

        return cloned.getObjectId();
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.OPPORTUNITY_RESOURCE.concat("|" + this.getActionCategory().getCategoryName()),
                this.getTitle(), this.getDescription(), this.getActionCategory().getCategoryColor(),
                this.isVirtual() ? Constants.VIRTUAL : DateUtils.formattedDateString(this.getActionStartDate()).toString(),
                "By: ".concat(this.getOrganizationName()), "At: ".concat(this.getLocationName()), this.getObjectId(), "", this.isActive(), false);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class VolunteerOpportunityFindCallback implements FindCallback<VolunteerOpportunity> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<VolunteerOpportunity> objects, ParseException e) {
            if (!calledCallback) {
                if (objects != null) {
                    // We got a result, use it.
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
        protected abstract void doneOnce(List<VolunteerOpportunity> objects, ParseException e);
    }

    /**
     * Creates a query for VolunteerOpportunity with all the includes
     */
    private static ParseQuery<VolunteerOpportunity> createQuery(boolean getAll) {
        ParseQuery<VolunteerOpportunity> query = new ParseQuery(VolunteerOpportunity.class);
        query.include("actionCategory");
        if(!getAll) {
            query.whereEqualTo("isActive", true);
        }
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }
    
    public static List<String> getOpportunitiesForCategory(Category category, boolean getAll) {
        ParseQuery<VolunteerOpportunity> actionQuery = ParseQuery.getQuery(VolunteerOpportunity.class);
        actionQuery.include("actionCategory");
        if(!getAll) {
            actionQuery.whereEqualTo("isActive", true);
        }
        actionQuery.whereEqualTo("actionCategory", category);
        try {
            return Lists.transform(actionQuery.find(), new Function<VolunteerOpportunity, String>() {
                @Override
                public String apply(VolunteerOpportunity input) {
                    return input.getTitle();
                }
            });
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    public static VolunteerOpportunity getOpportunityByNameAndCategory(String actionName, Category category) throws ParseException {
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(false);
        actionQuery.include("actionCategory");
        actionQuery.whereEqualTo("title", actionName).whereEqualTo("actionCategory", category);
        return actionQuery.getFirst();
    }

    public static void getOpportunitiesForOrganization(String organizationName,
                           boolean activeOnly, final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(activeOnly);
        actionQuery.whereMatches("organizationName", organizationName);
        actionQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static VolunteerOpportunity getInterestedVolunteers(String opportunityId){
        ParseQuery<VolunteerOpportunity> opportunityQuery = createQuery(true);
        opportunityQuery.include("interestedUsers");
        opportunityQuery.whereEqualTo("objectId", opportunityId);
        try {
            return opportunityQuery.getFirst();
        } catch (ParseException e) {
            return new VolunteerOpportunity();
        }
    }

    public static void getInterestedOpportunitiesForUser(ParseUser user, final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> opportunityQuery = createQuery(true);
        opportunityQuery.whereEqualTo("interestedUsers", user);
        opportunityQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static VolunteerOpportunity getUpcomingOpportunity(){
        ParseQuery<VolunteerOpportunity> opportunityQuery = createQuery(false);
        opportunityQuery.include("title");
        opportunityQuery.whereGreaterThan("startDate", LocalDate.now().toDate());
        opportunityQuery.orderByAscending("startDate");
        try {
            return opportunityQuery.getFirst();
        } catch (ParseException e) {
            return new VolunteerOpportunity();
        }
    }

    public static void getOpportunitiesNearMe(ParseGeoPoint userLocation , final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(false);
        actionQuery.whereExists("exactLocation").whereNear("exactLocation", userLocation);
        actionQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static void getAllOpportunities(final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(true);
        actionQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static void getActiveOpportunities(final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(false);
        actionQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static void getAllLogEligibleOpportunities(final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> nonVirtualOpportunitiesQuery = createQuery(false);
        nonVirtualOpportunitiesQuery.whereExists("startDate").whereGreaterThan("startDate", LocalDate.now().minusDays(30).toDate());

        ParseQuery<VolunteerOpportunity> virtualOpportunitiesQuery = createQuery(false);
        virtualOpportunitiesQuery.whereEqualTo("isVirtual", true);

        ParseQuery<VolunteerOpportunity> mainQuery = nonVirtualOpportunitiesQuery.or(Lists.newArrayList(virtualOpportunitiesQuery));
        mainQuery.findInBackground(new VolunteerOpportunityFindCallback() {
            @Override
            protected void doneOnce(List<VolunteerOpportunity> objects, ParseException e) {
                if (objects != null) {
                    Collections.sort(objects, VolunteerOpportunityByDateComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    public static VolunteerOpportunity getOpportunityDetails(String opportunityId){
        ParseQuery<VolunteerOpportunity> opportunityQuery = createQuery(true);
        opportunityQuery.include("interestedUsers");
        opportunityQuery.include("requiredSkills");
        opportunityQuery.include("requiredInterests");
        opportunityQuery.whereEqualTo("objectId", opportunityId);
        try {
            return opportunityQuery.getFirst();
        } catch (ParseException e) {
            return new VolunteerOpportunity();
        }
    }

    public static void saveOpportunity(VolunteerOpportunity opportunityData, final SaveCallback onSave){
        opportunityData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}
