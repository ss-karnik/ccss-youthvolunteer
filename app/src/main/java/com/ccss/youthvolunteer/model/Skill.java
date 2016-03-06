package com.ccss.youthvolunteer.model;

import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@ParseClassName("Skill")
public class Skill extends ParseObject implements Serializable {

    private static final long serialVersionUID = -7060210544600475592L;

    public String getSkillTitle(){
        return getString("title");
    }

    public void setSkillTitle(String value){
        put("title", value);
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

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public ParseFile getLogo() {
        return getParseFile("image");
    }

    public void setLogo(ParseFile value) {
        put("image", value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Skill other = (Skill) obj;
        return !((this.getObjectId() == null)
                ? (other.getObjectId() != null)
                : !this.getObjectId().equals(other.getObjectId()))
                    && this.getSkillTitle().equals(other.getSkillTitle());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.getObjectId() != null ? this.getObjectId().hashCode() : 0);
        hash = 53 * hash + (this.getSkillTitle() != null ? this.getSkillTitle().hashCode() : 0);
        return hash;
    }

    /**
     * Creates a query for Skill with all the includes
     */
    private static ParseQuery<Skill> createQuery() {
        ParseQuery<Skill> query = new ParseQuery<>(Skill.class);
        query.orderByAscending("title");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public ResourceModel convertToResourceModel() {
        return new ResourceModel(Constants.SKILL_RESOURCE, this.getSkillTitle(), this.getDescription(),
                "", "", "", "", this.getObjectId(), this.getLogo() == null ? "" : this.getLogo().getUrl(), true, false);
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class SkillsFindCallback implements FindCallback<Skill> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Skill> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Skill> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Skill, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Skill> callback) {
        ParseQuery<Skill> query = Skill.createQuery();

        query.findInBackground(new SkillsFindCallback() {
            @Override
            protected void doneOnce(List<Skill> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveSkill(Skill skillData, final SaveCallback onSave){
        skillData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }
}
