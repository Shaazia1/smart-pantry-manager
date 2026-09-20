package com.richfield.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Expiry dates are saved as yyyy-MM-dd so they sort properly as text, and
// shown to the user as dd MMM yyyy.
public class DateUtils {

    private static final String STORAGE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_PATTERN = "dd MMM yyyy";

    public static String toStorage(Calendar calendar) {
        return new SimpleDateFormat(STORAGE_PATTERN, Locale.UK).format(calendar.getTime());
    }

    // "2026-09-30" turns into "30 Sep 2026", empty string if there is no date
    public static String toDisplay(String storedDate) {
        Date date = parse(storedDate);
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DISPLAY_PATTERN, Locale.UK).format(date);
    }

    public static Date parse(String storedDate) {
        if (storedDate == null || storedDate.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(STORAGE_PATTERN, Locale.UK).parse(storedDate);
        } catch (ParseException e) {
            return null;
        }
    }

    // How many days from today. Negative means it has already passed and
    // Integer.MAX_VALUE means no date was set.
    public static int daysUntil(String storedDate) {
        Date date = parse(storedDate);
        if (date == null) {
            return Integer.MAX_VALUE;
        }
        long difference = startOfDay(date).getTimeInMillis()
                - startOfDay(new Date()).getTimeInMillis();
        return (int) Math.round(difference / (1000d * 60 * 60 * 24));
    }

    private static Calendar startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
