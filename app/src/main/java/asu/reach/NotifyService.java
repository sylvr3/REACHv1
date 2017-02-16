package asu.reach;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotifyService extends Service {

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    private SQLiteDatabase db;

    NotifyServiceReceiver notifyServiceReceiver;

    private static int MY_NOTIFICATION_ID = 1;
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
        try {
            Log.i("Notif Service", "Inside onStartCommand of Service");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            registerReceiver(notifyServiceReceiver, intentFilter);
            DBHelper helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            Cursor c = db.rawQuery("select start_time from DATE_TIME_SET where id=1", null);
            c.moveToFirst();
            String notificationTime = c.getString(0);
            c.close();
            db.close();

            String[] data = notificationTime.split(":");
            int hours = Integer.parseInt(data[0]);
            int minutes = Integer.parseInt(data[1]);
            long time = (hours * 60 * 60) + (minutes * 60);
            Calendar cal = Calendar.getInstance();
            int hour_of_day = cal.get(Calendar.HOUR_OF_DAY);
            int min_of_day = cal.get(Calendar.MINUTE);
            long systemTime = (hour_of_day * 3600) + (min_of_day * 60);

            DBHelper helper1 = new DBHelper(getApplicationContext());
            db = helper1.getDB();
            Cursor c1 = db.rawQuery("select start_date from DATE_TIME_SET where id=1", null);
            c1.moveToFirst();
            String ALLprotocolStartDate = c1.getString(0);
            c1.close();
            db.close();

            int currentDayofProtocol = 0;
            try{
                currentDayofProtocol  = calculateDayofProtocol(ALLprotocolStartDate);
            } catch (Exception e){
                Log.d("excep", e.getLocalizedMessage());
            }


            if (currentDayofProtocol > 0) {
                DBHelper helper2 = new DBHelper(getApplicationContext());
                db = helper2.getDB();
                Cursor c2 = db.rawQuery("select * from ADMIN_ACTIVITY_SCHEDULER where DAY=" + currentDayofProtocol, null);
                c2.moveToFirst();
                boolean DDNotificationCheck = false;
                boolean STICNotificationCheck = false;
                boolean STOPNotificationCheck = false;
                boolean WORRYHEADnotificationCheck = false;
                boolean RelaxationNotificationCheck = false;
                boolean SAFENotificationCheck = false;
                if (c2.getInt(3) == 1 /*|| c2.getInt(4)==1*/)                                                             //Are we updating DIARY_EVENT2 when user completes DIARY EVENT_2 anywhere ??
                    DDNotificationCheck = checkForDDNotification(currentDayofProtocol, systemTime, time);
                if (c2.getInt(5) == 1)
                    STOPNotificationCheck = checkForSTOPNotification(currentDayofProtocol, systemTime, time);
                if (c2.getInt(6) == 1)
                    WORRYHEADnotificationCheck = checkForWORRYHEADNotification(currentDayofProtocol, systemTime, time);
                if (c2.getInt(7) == 1)
                    STICNotificationCheck = checkForSTICNotification(currentDayofProtocol, systemTime, time);
                if (c2.getInt(7) == 1)
                    RelaxationNotificationCheck = checkForRelaxationNotification(currentDayofProtocol, systemTime, time);
                if (c2.getInt(8) == 1)
                    RelaxationNotificationCheck = checkForSAFENotification(currentDayofProtocol, systemTime, time);

                Log.i("DD NOTIF DONE", DDNotificationCheck + "");
                Log.i("WORRY HEAD NOTIF DONE", WORRYHEADnotificationCheck + "");
                Log.i("STOP NOTIF DONE", STOPNotificationCheck + "");
                Log.i("STIC NOTIF DONE", STICNotificationCheck + "");
                Log.i("RELAX NOTIF DONE", RelaxationNotificationCheck + "");
                Log.i("SAFE NOTIF DONE", SAFENotificationCheck + "");
                c2.close();
                db.close();

            }
            return (START_STICKY);
        }catch(Exception e){
            e.printStackTrace();
            return (START_STICKY);
        }


    }

    public boolean checkForDDNotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select DIARY_EVENT1,DIARY_EVENT2 from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        //Are we updating DIARY_EVENT2 when user completes DIARY EVENT_2 anywhere ??
        if (c.getInt(0) == 0) {
            Log.i("DD Notif Status","ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message="Practice Daily Diary to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message,DailyDiary.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }

    public boolean checkForSTOPNotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select STOP from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("STOP Notif Status","ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message="Practice STOP to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message,STOP.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }

    public boolean checkForSTICNotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select STIC from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("STIC Notif Status","ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message="Practice STIC to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message,STIC.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }

    public boolean checkForWORRYHEADNotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select STOP_WORRYHEADS from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("Worryhead Notif Status","ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message="Practice Worryheads to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message,WorryHeads.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }

    public boolean checkForRelaxationNotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select RELAXATION from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("RELAX Notif Status","ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message="Practice Relaxation to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message,Relaxation.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }

    public boolean checkForSAFENotification(int currentDayofProtocol, long systemTime, long currTime) {

        DBHelper helper = new DBHelper(getApplicationContext());
        db = helper.getDB();
        Cursor c = db.rawQuery("select SAFE from USER_ACTIVITY_TRACK where DAY=" + currentDayofProtocol, null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("SAFE Notif Status", "ABOUT TO BE FIRED");
            if (systemTime == currTime) {
                String message = "Practice SAFE to help Bob the Blob learn new tricks to show you later";
                fireNotifications(message, Safe.class);
                c.close();
                db.close();
                helper.close();
                return true;
            }
        }
        c.close();
        db.close();
        helper.close();
        return false;
    }



    public void fireNotifications(String message, Class activityNotDone) {
        // Send Notification
        notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Context context = getApplicationContext();
        String notificationTitle = "Are you forgetting something ?";
        String notificationText = message;
        Intent myIntent = new Intent(getApplicationContext(), activityNotDone);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, 0);
        myNotification = new Notification.Builder(this)
                .setStyle(new Notification.BigTextStyle().bigText(notificationText))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(notificationTitle)
                /*.setContentText(notificationText).setSmallIcon(R.drawable.ic_launcher)*/
                .setContentIntent(pendingIntent)
                .build();
        /*myNotification = new Notification(R.drawable.ic_launcher,
                "REACH",
                System.currentTimeMillis());*/

        myNotification.defaults |= Notification.DEFAULT_SOUND;
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        /*myNotification.setLatestEventInfo(context,
                notificationTitle,
                notificationText,
                pendingIntent);*/
//            startForeground(1,myNotification);
        notificationManager.notify(MY_NOTIFICATION_ID++, myNotification);
        Log.i("Notification","NEW NOTIF FIRED: "+message);

    }

    public int calculateDayofProtocol(String ALLprotocolStartDate) {
        long dayCount = 0;
        try {
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
            Date ALLProtocolInDateFormat = format.parse(ALLprotocolStartDate);
            Calendar c = Calendar.getInstance();
            Date currDate = c.getTime();
            long dayCountInMS = currDate.getTime() - ALLProtocolInDateFormat.getTime();
            long diffHours = dayCountInMS / (60 * 60 * 1000);
            dayCount = diffHours / 24;
            Log.i("ascsa", dayCount + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dayCount++;
        return Integer.parseInt(dayCount + "");
    }

    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        Log.i("destroy", "in destroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
// TODO Auto-generated method stub
        return null;
    }

    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            /*
            * As application is mounted on external storage, it doesn't receive the BOOT_COMPLETED Flag.
            * */


           /* Intent ii = new Intent(arg0, NotifyService.class);
            PendingIntent pii = PendingIntent.getService(arg0, 2222, ii, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 5);
//registering our pending intent with alarmmanager
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60000, pii);
//                startService(intent);*/
        }
    }
}