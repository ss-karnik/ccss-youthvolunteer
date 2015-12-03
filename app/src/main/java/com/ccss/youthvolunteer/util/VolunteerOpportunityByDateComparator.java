package com.ccss.youthvolunteer.util;

import com.ccss.youthvolunteer.model.VolunteerOpportunity;

import java.util.Comparator;

public class VolunteerOpportunityByDateComparator implements Comparator<VolunteerOpportunity> {
    /*
     * This is a Singleton, because it has no state, so it's silly to make new
     * ones.
     */
    final private static VolunteerOpportunityByDateComparator instance = new VolunteerOpportunityByDateComparator();

    public static VolunteerOpportunityByDateComparator get() {
        return instance;
    }

    @Override
    public int compare(VolunteerOpportunity lhs, VolunteerOpportunity rhs) {
        int startCompare = lhs.getActionStartDate().compareTo(rhs.getActionStartDate());
        if (startCompare != 0) {
            return startCompare;
        }
        return lhs.getTitle().compareTo(rhs.getTitle());
    }
}
