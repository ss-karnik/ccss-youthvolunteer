package com.ccss.youthvolunteer.model;


import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("InterestGroup")
public class InterestGroup extends ParseObject {

    public String getGroupName(){
        return getString("groupName");
    }

    public void setGroupName(String value){
        put("groupName", value);
    }

    public String getGroupDescription(){
        return getString("description");
    }

    public void setGroupDescription(String value){
        put("description", value);
    }

    public ParseFile getGroupLogo() {
        return getParseFile("logoImage");
    }

    public void setImage(ParseFile value) {
        put("logoImage", value);
    }

    public List<VolunteerUser> getGroupMembers() {
        return getList("groupMembers");
    }

    public void setGroupMembers(List<VolunteerUser>  members) {
        put("groupMembers", members);
    }

    public void addGroupMember(VolunteerUser newMember) {
        List<VolunteerUser> currentGroupMembers = getGroupMembers();
        currentGroupMembers.add(newMember);
    }

    public List<VolunteerUser> getGroupAdmins() {
        return getList("groupAdmins");
    }

    public void setGroupAdmins(List<VolunteerUser>  members) {
        put("groupAdmins", members);
    }

    public void addGroupMemberAsAdmin(VolunteerUser newMember) {
        List<VolunteerUser> currentAdmins = getGroupAdmins();
        currentAdmins.add(newMember);
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.GROUP_RESOURCE, this.getGroupName(), this.getGroupDescription(),
                "", "", "Member count: " + this.getGroupMembers().size(), "", this.getObjectId(),
                this.getGroupLogo() == null ? "" : this.getGroupLogo().getUrl(), this.isActive(), false);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class GroupFindCallback implements FindCallback<InterestGroup> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<InterestGroup> objects, ParseException e) {
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
         * Override this method with the callback that should only be called once.
         */
        protected abstract void doneOnce(List<InterestGroup> objects, ParseException e);
    }

    public static void findInBackground(final FindCallback<InterestGroup> callback) {
        ParseQuery<InterestGroup> query = new ParseQuery<>(InterestGroup.class);
        query.orderByAscending("groupName");

        query.findInBackground(new GroupFindCallback() {
            @Override
            protected void doneOnce(List<InterestGroup> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveGroup(InterestGroup groupData, final SaveCallback onSave){
        groupData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}
