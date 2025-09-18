package com.ba.housedrawba.models;
import android.content.Context;
import android.content.SharedPreferences;

public class DataSaving {

    // Shared preferences file name
    private String PREF_NAME = "mpPref";
    public static final String PREF_IS_FIRST_RUN = "prefIsFirstRun";
    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    public boolean setStringPrefValue(Context context, String key, String value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getStringPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(key, "");
    }

    public int getIntPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        int defValue = 0;
//        switch (key) {
//            case MEASURES_LISTING_SORTING_MODE:
//                defValue = MEASURES_LISTING_SORTING_A_TO_Z;
//                break;
//            case MAIN_SCREEN_MAP_TYPE:
//                defValue = GoogleMap.MAP_TYPE_SATELLITE;
//                break;
//            case AUTO_HISTORY_INTERVAL:
//                defValue = 1;
//                break;
//        }
        return pref.getInt(key, defValue);
    }

    public boolean setIntPrefValue(Context context, String key, int value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public long getLongPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        long defValue = 0;
        switch (key) {
        }
        return pref.getLong(key, defValue);
    }

    public boolean setLongPrefValue(Context context, String key, long value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public boolean setBooleanPrefValue(Context context, String key, boolean value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getBooleanPrefValue(Context context, String key) {
        boolean defaultValue = false;
        if (PREF_IS_FIRST_RUN.equals(key)) {
            defaultValue = true;
        }
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getBoolean(key, defaultValue);
    }

}
