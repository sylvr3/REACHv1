package asu.reach;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;

public class NotifyService extends Service {

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    private SQLiteDatabase db;

    NotifyServiceReceiver notifyServiceReceiver;

    private static final int MY_NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Notification myNotification;
//    private final String myBlog = "http://android-er.blogspot.com/";

    @Override
    public void onCreate() {
// TODO Auto-generated method stub
        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
// TODO Auto-generated method stub
        Log.i("Notif Service","Inside onStartCommand of Service");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);
        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select start_time from DATE_TIME_SET where id=1",null);
        c.moveToFirst();
        String notificationTime = c.getString(0);
        String[] data = notificationTime.split(":");

        int hours  = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        long time = (hours*60*60) + (minutes*60);
        Calendar cal = Calendar.getInstance();
        int hour_of_day = cal.get(Calendar.HOUR_OF_DAY);
        int min_of_day = cal.get(Calendar.MINUTE);
        long systemTime = (hour_of_day*3600) + (min_of_day*60);
        if(systemTime==time) {
// Send Notification
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            myNotification = new Notification(R.drawable.ic_launcher,
                    "REACH Application!",
                    System.currentTimeMillis());
            Context context = getApplicationContext();
            String notificationTitle = "What's up !";
            String notificationText = "You haven't seen me in a while !";
            Intent myIntent = new Intent(getApplicationContext(), Landing.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0, myIntent,0);
            myNotification.defaults |= Notification.DEFAULT_SOUND;
            myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            myNotification.setLatestEventInfo(context,
                    notificationTitle,
                    notificationText,
                    pendingIntent);
//            startForeground(1,myNotification);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);


        }
        return (START_STICKY);
    }


    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
       Log.i("destroy","in destroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
// TODO Auto-generated method stub
        return null;
    }

    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            /*// TODO Auto-generated method stub

            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == Intent.ACTION_BOOT_COMPLETED) {*/
                Intent intent = new Intent(arg0,NotifyService.class);
                startService(intent);
        }
    }
}
