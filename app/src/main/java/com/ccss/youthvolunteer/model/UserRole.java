package com.ccss.youthvolunteer.model;

import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

public class UserRole {
    private static List<ParseRole> parseRoles;

    public static void clear() {
        parseRoles = null;
    }

    public static void refresh() {
        ParseQuery<ParseRole> query = ParseRole.getQuery();
        query.whereEqualTo("users", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseRole>() {
            @Override
            public void done(List<ParseRole> roles, ParseException e) {
                if (e == null) {
                    parseRoles = roles;
                } else {
                    Log.e("UserRole", e.getLocalizedMessage());
                }
            }
        });
    }

    public static List<ParseRole> getParseRoles() {
        if (parseRoles == null) {
            ParseQuery<ParseRole> query = ParseRole.getQuery();
            query.whereEqualTo("users", ParseUser.getCurrentUser());
            try {
                parseRoles = query.find();
            } catch (Exception e) {
                Log.e("UserRole", e.getLocalizedMessage());
                return Collections.emptyList();
            }
        }
        return parseRoles;
    }

    public static boolean isUserModerator() {
        return Iterables.tryFind(getParseRoles(), new Predicate<ParseRole>() {
            @Override
            public boolean apply(ParseRole input) {
                return "Moderator".equals(input.getName());
            }
        }).isPresent();
    }

    public static boolean isUserAdmin() {
        return Iterables.tryFind(getParseRoles(), new Predicate<ParseRole>() {
            @Override
            public boolean apply(ParseRole input) {
                return "Admin".equalsIgnoreCase(input.getName());
            }
        }).isPresent();
    }
}
