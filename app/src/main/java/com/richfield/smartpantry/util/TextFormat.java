package com.richfield.smartpantry.util;

import java.util.Locale;

// Small helper so quantities read as "2 g" instead of "2.0 g".
public class TextFormat {

    public static String quantity(double value) {
        if (value == Math.floor(value)) {
            return String.format(Locale.UK, "%d", (long) value);
        }
        return String.format(Locale.UK, "%s", value);
    }

    public static String amount(double value, String unit) {
        String number = quantity(value);
        if (unit == null || unit.trim().isEmpty()) {
            return number;
        }
        return number + " " + unit;
    }
}
