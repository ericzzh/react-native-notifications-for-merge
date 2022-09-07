package com.wix.reactnativenotifications.fcm;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;
import com.wix.reactnativenotifications.BuildConfig;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(BuildConfig.DEBUG) Log.d(LOGTAG, "New message from Pushy: " + bundle);

        try {
            final IPushNotification notification = PushNotification.get(context, bundle);
            notification.onReceived();
        } catch (IPushNotification.InvalidNotificationException e) {
            // An FCM message, yes - but not the kind we know how to work with.
            if(BuildConfig.DEBUG) Log.v(LOGTAG, "Pushy message handling aborted", e);
        }
    }
}
