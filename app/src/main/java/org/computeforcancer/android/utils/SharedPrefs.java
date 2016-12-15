package org.computeforcancer.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Maksym Shpyl on 06.12.2016.
 */
public class SharedPrefs {

    private SharedPreferences mSharedPreferences;
    //private static volatile SharedPrefs sInstance;

    public static final String DONATION_TIME = "donation_time";
    public static final String LAST_SESSION_TIME = "last_session_time";
    //public static final String START_SESSION_TIME = "start_session_time";
    public static final String CURRENT_EMAIL = "current_email";
    public static final String AUTO_START = "auto_start";
    public static final String LAST_NOTIFICATION = "last_notification";
    public static final String NOTIFICATION_DELAY = "notification_delay";

    public SharedPrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences("org.computeforcancer.android",
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }
/*
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
*/
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
/*
    public void putStartSessionTime(long time) {
        mSharedPreferences.edit().putLong(START_SESSION_TIME + getEmail(), time).commit();
    }

    public long getStartSessionTime() {
        return mSharedPreferences.getLong(START_SESSION_TIME + getEmail(), 0);
    }
*/
    public void putAutoStart(boolean autoStart) {
        mSharedPreferences.edit().putBoolean(AUTO_START, autoStart).commit();
    }

    public boolean getAutoStart() {
        return mSharedPreferences.getBoolean(AUTO_START, true);
    }

    public void putLastNotification(long lastNotification) {
        mSharedPreferences.edit().putLong(LAST_NOTIFICATION, lastNotification).commit();
    }

    public long getLastNotification() {
        return mSharedPreferences.getLong(LAST_NOTIFICATION, 0);
    }

    public void putNotificationDelay(long notificationDelay) {
        //Log.d("TEST", "putNotificationDelay " + notificationDelay);
        mSharedPreferences.edit().putLong(NOTIFICATION_DELAY, notificationDelay).commit();
    }

    public long getNotificationDelay() {
        //Log.d("TEST", "getNotificationDelay " + mSharedPreferences.getLong(NOTIFICATION_DELAY, Long.MAX_VALUE));
        return mSharedPreferences.getLong(NOTIFICATION_DELAY, Long.MAX_VALUE);
    }

}
