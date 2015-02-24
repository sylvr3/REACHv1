package asu.reach;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Chinmay on 2/18/2015.
 * Copyright 2015 Chinmay Dhekne
 * Right to Use: College Town, Upalms
 *
 * @author Chinmay Dhekne, chinmay.dhekne@asu.edu
 *         MS Software Engineering, ASU
 * @version 18 Feb 2015
 */
public class NotifManager extends BroadcastReceiver {

    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence from = "Nithin";
        CharSequence message = "Crazy About Android...";
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(), 0);
        Notification notif = new Notification(R.drawable.ic_launcher,
                "Crazy About Android...", System.currentTimeMillis());
        notif.setLatestEventInfo(context, from, message, contentIntent);
        nm.notify(1, notif);
    }
}
