package com.richfield.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

// The three settings the user can change. These are single values rather than
// records, so SharedPreferences fits better than another database table.
public class Prefs {

    private static final String FILE_NAME = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_ALMOST_THERE = "almost_there";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_IMPERIAL = "imperial";

    private final SharedPreferences preferences;

    public Prefs(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public boolean isAlmostThereEnabled() {
        return preferences.getBoolean(KEY_ALMOST_THERE, true);
    }

    public void setAlmostThereEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_ALMOST_THERE, enabled).apply();
    }

    public String getUnitSystem() {
        return preferences.getString(KEY_UNIT_SYSTEM, UNITS_METRIC);
    }

    public void setUnitSystem(String unitSystem) {
        preferences.edit().putString(KEY_UNIT_SYSTEM, unitSystem).apply();
    }

    // which unit the add screen starts on
    public String getDefaultUnit() {
        return UNITS_IMPERIAL.equals(getUnitSystem()) ? "oz" : "g";
    }
}
