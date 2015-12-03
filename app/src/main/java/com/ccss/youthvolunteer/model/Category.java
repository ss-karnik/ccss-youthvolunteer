package com.ccss.youthvolunteer.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ParseClassName("Category")
public class Category extends ParseObject {

    public String getCategoryName(){
        return getString("categoryName");
    }

    public void setCategoryName(String value){
        put("categoryName", value);
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

    public String getCategoryColor(){
        return getString("categoryColor");
    }

    public void setCategoryColor(String value){
        put("categoryColor", value);
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public void setIsActive(boolean value){
        put("isActive", value);
    }

    public static List<String> getAllCategories(){
        ParseQuery<Category> categoryQuery = ParseQuery.getQuery(Category.class);
        categoryQuery.whereEqualTo("isActive", true);
        categoryQuery.selectKeys(Collections.singletonList("categoryName"));
        try {
            return Lists.transform(categoryQuery.find(), new Function<Category, String>() {
                @Override
                public String apply(Category input) {
                    return input.getCategoryName();
                }
            });
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    /**
     * Creates a query for Interests with all the includes
     */
    private static ParseQuery<Category> createQuery() {
        ParseQuery<Category> query = new ParseQuery<>(Category.class);
        query.orderByAscending("categoryName");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class CategoryFindCallback implements FindCallback<Category> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Category> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Category> objects, ParseException e);
    }

    /**
     * Retrieves the set of all Categories, ordered by title. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Category> callback) {
        ParseQuery<Category> query = Category.createQuery();

        query.findInBackground(new CategoryFindCallback() {
            @Override
            protected void doneOnce(List<Category> objects, ParseException e) {
                callback.done(objects, e);
            }
        });
    }

    public static void saveCategory(Category categoryData, final SaveCallback onSave){
        categoryData.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onSave.done(e);
            }
        });
    }

    public ResourceModel convertToResourceModel(){
        return new ResourceModel(this.getCategoryName(), this.getDescription(),
                this.getCategoryColor(), this.getObjectId(), "", this.isActive());
    }

}
