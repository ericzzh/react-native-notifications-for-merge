package com.wix.reactnativenotifications.core.helpers;

import android.app.AlarmManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.util.Log;

import com.wix.reactnativenotifications.core.notification.PushNotificationProps;
import com.wix.reactnativenotifications.core.notification.PushNotificationPublisher;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class ScheduleNotificationHelper {
    public static ScheduleNotificationHelper sInstance;
    public static final String PREFERENCES_KEY = "rn_push_notification";
    static final String NOTIFICATION_ID = "notificationId";

    private final SharedPreferences scheduledNotificationsPersistence;
    protected final Context mContext;

    private ScheduleNotificationHelper(Context context) {
        this.mContext = context;
        this.scheduledNotificationsPersistence = context.getSharedPreferences(ScheduleNotificationHelper.PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static ScheduleNotificationHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScheduleNotificationHelper(context);
        }
        return sInstance;
    }

    public PendingIntent createPendingNotificationIntent(Integer notificationId, Bundle bundle) {
        Intent notificationIntent = new Intent(mContext, PushNotificationPublisher.class);
        notificationIntent.putExtra(ScheduleNotificationHelper.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtras(bundle);
        return PendingIntent.getBroadcast(mContext, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void schedulePendingNotificationIntent(PendingIntent intent, long fireDate) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, fireDate, intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, fireDate, intent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, fireDate, intent);
        }
    }

    public void cancelScheduledNotificationIntent(PendingIntent intent) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(intent);
    }

    public boolean savePreferences(String notificationId, PushNotificationProps notificationProps) {
        SharedPreferences.Editor editor = scheduledNotificationsPersistence.edit();
        editor.putString(notificationId, notificationProps.toString());
        commit(editor);

        return scheduledNotificationsPersistence.contains(notificationId);
    }

    public void removePreference(String notificationId) {
        if (scheduledNotificationsPersistence.contains(notificationId)) {
            // remove it from local storage
            SharedPreferences.Editor editor = scheduledNotificationsPersistence.edit();
            editor.remove(notificationId);
            commit(editor);
        } else {
            Log.w(LOGTAG, "Unable to find notification " + notificationId);
        }
    }

    public java.util.Set<String> getPreferencesKeys() {
        return scheduledNotificationsPersistence.getAll().keySet();
    }

    private static void commit(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }
    }
}
