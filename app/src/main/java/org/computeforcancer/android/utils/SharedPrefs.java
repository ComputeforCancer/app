package org.computeforcancer.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Maksym Shpyl on 06.12.2016.
 */
public class SharedPrefs {

    private SharedPreferences mSharedPreferences;
    private static volatile SharedPrefs sInstance;

    private static final String DONATION_TIME = "donation_time";
    private static final String LAST_SESSION_TIME = "last_session_time";
    private static final String START_SESSION_TIME = "start_session_time";
    private static final String CURRENT_EMAIL = "current_email";
    private static final String AUTO_START = "auto_start";
    private static final Object lock = new Object();

    private SharedPrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences("org.computeforcancer.android", Context.MODE_PRIVATE);
    }

    public static SharedPrefs getSharedPrefs(Context context) {
        SharedPrefs localInstance = sInstance;
        if (localInstance == null) {
            synchronized (SharedPreferences.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new SharedPrefs(context);
                }
            }
        }
        return localInstance;
    }

    public void putEmail(String email) {
        mSharedPreferences.edit().putString(CURRENT_EMAIL, email).commit();
    }

    public String getEmail() {
        return mSharedPreferences.getString(CURRENT_EMAIL, "");
    }

    public void putDonationTime(long time) {
        mSharedPreferences.edit().putLong(DONATION_TIME + getEmail(), time).commit();
    }

    public long getDonationTime() {
        return mSharedPreferences.getLong(DONATION_TIME + getEmail(), 0);
    }

    public void putLastSessionTime(long time) {
        mSharedPreferences.edit().putLong(LAST_SESSION_TIME + getEmail(), time).commit();
    }

    public long getLastSessionTime() {
        return mSharedPreferences.getLong(LAST_SESSION_TIME + getEmail(), 0);
    }

    public void putStartSessionTime(long time) {
        mSharedPreferences.edit().putLong(START_SESSION_TIME + getEmail(), time).commit();
    }

    public long getStartSessionTime() {
        return mSharedPreferences.getLong(START_SESSION_TIME + getEmail(), 0);
    }

    public void putAutoStart(boolean autoStart) {
        mSharedPreferences.edit().putBoolean(AUTO_START, autoStart).commit();
    }

    public boolean getAutoStart() {
        return mSharedPreferences.getBoolean(AUTO_START, true);
    }

}
