package com.ccss.youthvolunteer.model;

import android.support.annotation.NonNull;

import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.VolunteerOpportunityByDateComparator;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@ParseClassName("Action")
public class VolunteerOpportunity extends ParseObject implements Comparable<VolunteerOpportunity> {

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

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.OPPORTUNITY_RESOURCE, this.getTitle(), this.getDescription(),
                this.getOrganizationName(), this.getObjectId(), "", this.isActive());
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
        if(!getAll) {
            query.whereEqualTo("isActive", true);
        }
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
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

    public boolean isVirtual() {
        return getBoolean("isVirtual");
    }

    public void setIsVirtual(boolean value){
        put("isVirtual", value);
    }

    @Override
    public String toString() {
        return String.format("Category: %s; Title: %s", this.getActionCategory(), this.getTitle()) ;
    }

    public static List<String> getOpportunityForCategory(String category, boolean getAll) {
        ParseQuery<VolunteerOpportunity> actionQuery = ParseQuery.getQuery(VolunteerOpportunity.class);
        if(!getAll) {
            actionQuery.whereEqualTo("isActive", true);
        }
        actionQuery.whereMatches("categoryName", category);
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

    public static VolunteerOpportunity getOpportunityByNameAndCategory(String actionName, String category) {
        final VolunteerOpportunity[] singleVolunteerOpportunity = new VolunteerOpportunity[1];
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(false);
        actionQuery.whereEqualTo("title", actionName).whereEqualTo("categoryName", category);
        actionQuery.getFirstInBackground(new GetCallback<VolunteerOpportunity>() {
            @Override
            public void done(VolunteerOpportunity volunteerOpportunity, ParseException e) {
                if (e == null) {
                    singleVolunteerOpportunity[0] = volunteerOpportunity;
                } else {
                    singleVolunteerOpportunity[0] = new VolunteerOpportunity();
                }
            }
        });

        return singleVolunteerOpportunity[0];
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

    public static void getOpportunitiesNearMe(ParseGeoPoint userLocation , final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> actionQuery = createQuery(true);
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

    public static void getActiveOpportunities(final FindCallback<VolunteerOpportunity> callback){
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

    public static void getAllLogEligibleOpportunities(final FindCallback<VolunteerOpportunity> callback){
        ParseQuery<VolunteerOpportunity> datedOpportunity = createQuery(true);
        datedOpportunity.whereExists("startDate").whereLessThan("startDate", LocalDate.now());

        ParseQuery<VolunteerOpportunity> virtualOpportunity = createQuery(true);
        virtualOpportunity.whereDoesNotExist("startDate");

        ParseQuery<VolunteerOpportunity> mainQuery = datedOpportunity.or(Lists.newArrayList(virtualOpportunity));
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


    public static void saveOpportunity(VolunteerOpportunity opportunityData, final SaveCallback onSave){
        opportunityData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}
