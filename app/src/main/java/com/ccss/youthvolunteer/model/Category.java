package com.ccss.youthvolunteer.model;

import com.ccss.youthvolunteer.util.Constants;
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
        String name = getString("categoryName");
        if (name == null) {
            name = "";
        }
        return name;
    }

    @Override
    public String toString() {
        return getCategoryName();
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
        String color = getString("categoryColor");
        if (color == null) {
            color = "#FFFFFF";
        }
        return color;
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

    public static List<String> getAllCategoryNames(){
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
     * Creates a query for Category
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
     * Retrieves the set of all Categories.
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

    public static List<Category> getAllCategories(boolean activeOnly) {
        ParseQuery<Category> query = Category.createQuery();
        if(activeOnly){
            query.whereEqualTo("isActive", true);
        }
        try {
            return query.find();
        } catch (ParseException e) {
            return Lists.newArrayList();
        }
    }

    public static Category getFromName(String categoryTitle) {
        ParseQuery<Category> query = Category.createQuery();
        query.whereEqualTo("categoryName", categoryTitle);
        try {
            return query.getFirst();
        } catch (ParseException e) {
            return null;
        }
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
        return new ResourceModel(Constants.CATEGORY_RESOURCE, this.getCategoryName(), this.getDescription(),
                this.getCategoryColor(), this.getCategoryColor(), "", "", this.getObjectId(), "", this.isActive(), false);
    }

}
