package com.ccss.youthvolunteer.util;

import com.ccss.youthvolunteer.model.UserAction;

import java.util.Comparator;

public class UserActionComparator implements Comparator<UserAction> {

    final private static UserActionComparator instance = new UserActionComparator();

    public static UserActionComparator get() {
        return instance;
    }

    @Override
    public int compare(UserAction lhs, UserAction rhs) {
        int startCompare = lhs.getActionDate().compareTo(rhs.getActionDate());
        if (startCompare != 0) {
            return startCompare;
        }
        return lhs.getAction().compareTo(rhs.getAction());
    }
}