package com.wix.reactnativenotifications.core.notificationdrawer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.wix.reactnativenotifications.core.AppLaunchHelper;
import com.wix.reactnativenotifications.core.helpers.ScheduleNotificationHelper;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class PushNotificationsDrawer implements IPushNotificationsDrawer {

    final protected Context mContext;
    final protected AppLaunchHelper mAppLaunchHelper;

    public static IPushNotificationsDrawer get(Context context) {
        return PushNotificationsDrawer.get(context, new AppLaunchHelper());
    }

    public static IPushNotificationsDrawer get(Context context, AppLaunchHelper appLaunchHelper) {
        final Context appContext = context.getApplicationContext();
        if (appContext instanceof INotificationsDrawerApplication) {
            return ((INotificationsDrawerApplication) appContext).getPushNotificationsDrawer(context, appLaunchHelper);
        }

        return new PushNotificationsDrawer(context, appLaunchHelper);
    }

    protected PushNotificationsDrawer(Context context, AppLaunchHelper appLaunchHelper) {
        mContext = context;
        mAppLaunchHelper = appLaunchHelper;
    }

    @Override
    public void onAppInit() {
    }

    @Override
    public void onAppVisible() {
    }

    @Override
    public void onNewActivity(Activity activity) {
    }

    @Override
    public void onNotificationOpened() {
    }

    @Override
    public void onNotificationClearRequest(int id) {
        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    @Override
    public void onNotificationClearRequest(String tag, int id) {
        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(tag, id);
    }

    @Override
    public void onAllNotificationsClearRequest() {
        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onCancelAllLocalNotifications() {
        onAllNotificationsClearRequest();
        cancelAllScheduledNotifications();
    }

    protected void cancelAllScheduledNotifications() {
        Log.i(LOGTAG, "Cancelling all scheduled notifications");
        ScheduleNotificationHelper helper = ScheduleNotificationHelper.getInstance(mContext);

        for (String notificationId : helper.getPreferencesKeys()) {
            cancelScheduledNotification(notificationId);
        }
    }

    protected void cancelScheduledNotification(String notificationId) {
        Log.i(LOGTAG, "Cancelling scheduled notification: " + notificationId);

        ScheduleNotificationHelper helper = ScheduleNotificationHelper.getInstance(mContext);

        // Remove it from the alarm manger schedule
        Bundle bundle = new Bundle();
        bundle.putString("id", notificationId);
        PendingIntent pendingIntent = helper.createPendingNotificationIntent(Integer.parseInt(notificationId), bundle);
        helper.cancelScheduledNotificationIntent(pendingIntent);

        helper.removePreference(notificationId);

        // Remove it from the notification center
        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(notificationId));
    }
}
