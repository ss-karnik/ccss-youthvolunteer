package com.ccss.youthvolunteer.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormat.forPattern("dd/MM/yyyy");

    public static boolean isValidDate(String dateToValidate, boolean checkForMinimumAge) {
        try {
            DateTime enteredDate = DOB_FORMAT.parseDateTime(dateToValidate);
            return checkForMinimumAge && DateTime.now().getYear() - enteredDate.getYear() >= Constants.MINIMUM_AGE;
        }
        catch (IllegalArgumentException ex){
            return false;
        }
    }
}
