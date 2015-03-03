package asu.reach;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;


public class NotifManager extends Activity {

    public void createNotification(View view)
    {
        Intent intent = new Intent(this,Landing.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notif = new Notification.Builder(this)
                .setContentTitle("Hey There !")
                .setContentText("Come Play with me !")
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        nManager.notify(0,notif);
    }

}
