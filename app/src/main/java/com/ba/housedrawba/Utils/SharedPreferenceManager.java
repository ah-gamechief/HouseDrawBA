package com.ba.housedrawba.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
public class SharedPreferenceManager {

    protected Context mContext;
    protected SharedPreferences mSettings;
    protected SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferenceManager(Context ctx, String prefFileName) {
        mContext = ctx;
        mSettings = mContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }

    public void setValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void setIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void setDoubleValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    public void setLongValue(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public String getValue(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {
        return mSettings.getInt(key, defaultValue);
    }

    public long getLongValue(String key, long defaultValue) {
        return mSettings.getLong(key, defaultValue);
    }


    public boolean getBooleanValue(String key, boolean defValue) {
        return mSettings.getBoolean(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public boolean clear() {
        try {
            mEditor.clear().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeValue(String key) {
        if (mEditor != null) {
            mEditor.remove(key).commit();
        }
    }
}
