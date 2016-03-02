package com.ccss.youthvolunteer.util;

import android.text.format.DateFormat;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormat.forPattern(Constants.DATE_FORMAT);

    public static boolean isValidDate(String dateToValidate, boolean checkForMinimumAge) {
        try {
            DateTime enteredDate = DOB_FORMAT.parseDateTime(dateToValidate);
            return !checkForMinimumAge || DateTime.now().getYear() - enteredDate.getYear() >= Constants.MINIMUM_AGE;
        }
        catch (IllegalArgumentException ex){
            return false;
        }
    }

    /* Date to dd/MM/yyyy string */
    public static CharSequence formattedDateString(Date dateInput){
        return DateFormat.format(Constants.DATE_FORMAT, dateInput);
    }

    public static LocalDate stringToLocalDate(String input){
        DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
        return dtf.parseDateTime(input).toLocalDate();
    }

    public static String convertedDate(int year, int month, int dayOfMonth) {
        int monthOfYear = month + 1;
        String formattedMonth = "" + monthOfYear;
        String formattedDayOfMonth = "" + dayOfMonth;

        if(monthOfYear < 10){
            formattedMonth = "0" + monthOfYear;
        }

        if(dayOfMonth < 10){
            formattedDayOfMonth = "0" + dayOfMonth;
        }
        return formattedDayOfMonth + "/" + formattedMonth + "/" + year;
    }

    public static String minutesToHoursRepresentation(int min){
        long hours = TimeUnit.MINUTES.toHours(min);
        long remainMinutes = min - TimeUnit.HOURS.toMinutes(hours);
        return String.format("%02d:%02d", hours, remainMinutes);
    }

    public static double minutesAsHours(int min){
        long hours = TimeUnit.MINUTES.toHours(min);
        long remainMinutes = min - TimeUnit.HOURS.toMinutes(hours);
        return hours + (remainMinutes * 0.0166667);
    }
}
