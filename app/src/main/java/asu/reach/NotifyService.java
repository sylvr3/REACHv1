package asu.reach;

// BackgroundService.java
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class NotifyService extends Service
{
    private static final String TAG = "BackgroundService";
    private NotificationManager notificationMgr;
    private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");

    NotifyServiceReceiver notifyServiceReceiver;
    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    private SQLiteDatabase db;

    private static int MY_NOTIFICATION_ID = 1;
    //private NotificationManager notificationManager;
    private Notification myNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        notifyServiceReceiver = new NotifyServiceReceiver();
        Log.v(TAG, "in onCreate()");
        notificationMgr =(NotificationManager)getSystemService(
                NOTIFICATION_SERVICE);
        //displayNotificationMessage("Background Service is running",STIC.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        int counter = intent.getExtras().getInt("counter");
        Log.v(TAG, "in onStartCommand(), counter = " + counter +
                ", startId = " + startId);

        new Thread(myThreads, new ServiceWorker(counter), "BackgroundService")
                .start();

        return START_STICKY;
    }

    class ServiceWorker implements Runnable
    {
        private int counter = -1;
        public ServiceWorker(int counter) {
            this.counter = counter;
        }

        public void run() {
            final String TAG2 = "ServiceWorker:" + Thread.currentThread().getId();
            // do background processing here...
            try {
                Log.v(TAG2, "sleeping for 20 seconds. counter = " + counter);
                Thread.sleep(20000);
                checkValuesInService();
                Log.v(TAG2, "... waking up");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Log.v(TAG2, "... sleep interrupted");
            }
        }
    }

    public void checkValuesInService(){
        try {
            String message="";
            DBHelper helper=new DBHelper(getApplicationContext());
            Calendar protocolDate=helper.getStartDateForProtocol();
            Calendar protocolTime=helper.getTimeForNotifications();
            Calendar c = Calendar.getInstance();
            if(protocolDate!=null && protocolTime!=null){
                Date storedDate=protocolDate.getTime();
                long currentDayOfProtocol=currentDayOfProtocol(storedDate);
                System.out.println(currentDayOfProtocol);
                if(c.get(Calendar.HOUR_OF_DAY)==protocolTime.get(Calendar.HOUR_OF_DAY) && c.get(Calendar.MINUTE)==protocolTime.get(Calendar.MINUTE)) {
                    if (checkForNotification("STIC", currentDayOfProtocol) == true) {
                        message = "Practice STIC today.";
                        displayNotificationMessage(message, STIC.class);
                    }  if (checkForNotification("DIARY_EVENT1", currentDayOfProtocol) == true || checkForNotification("DIARY_EVENT2", currentDayOfProtocol) == true) {
                        message = "Practice DAILY DIARY today.";
                        displayNotificationMessage(message, DailyDiary.class);
                    }  if (checkForNotification("RELAXATION", currentDayOfProtocol) == true) {
                        message = "Practice RELAXATION today.";
                        displayNotificationMessage(message, Relaxation.class);
                    }  if (checkForNotification("STOP", currentDayOfProtocol) == true) {
                        message = "Practice STOP today.";
                        displayNotificationMessage(message, STOP.class);
                    }  if (checkForNotification("STOP_WORRYHEADS", currentDayOfProtocol) == true) {
                        message = "Practice WORRYHEADS today.";
                        displayNotificationMessage(message, WorryHeads.class);
                    }
                }

                //Check if its a day/7. ie. the last day of the week.
                if(currentDayOfProtocol%7==0){
                    helper.setActivityProgressCountToZero();
                }
                helper.releaseTrick();
            }else{
                System.out.println("Not set.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public long currentDayOfProtocol(Date storedDate){
        long dayCount=0;
        Calendar c = Calendar.getInstance();
        Date currDate = c.getTime();
        long dayCountInMS = currDate.getTime() - storedDate.getTime();
        long diffHours = dayCountInMS / (60 * 60 * 1000);
        dayCount = diffHours / 24;

        dayCount++;
        return dayCount;
    }

    public boolean checkForNotification(String protocolName, long currentDayOfProtocol) {
        try {
            DBHelper helper=new DBHelper(getApplicationContext());
            if(currentDayOfProtocol>0){
                int status=helper.statusOfTheDayForGivenProtocol(protocolName,currentDayOfProtocol);
                if(status==1){
                    return true;
                }else{
                    return false;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void onDestroy()
    {
        Log.v(TAG, "in onDestroy(). Interrupting threads and cancelling notifications");
        myThreads.interrupt();
        notificationMgr.cancelAll();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "in onBind()");
        return null;
    }

    /*private void displayNotificationMessage(String message)
    {
        Notification notification = new Notification(R.drawable.emo_im_winking,
                message, System.currentTimeMillis());

        notification.flags = Notification.FLAG_NO_CLEAR;

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        notification.setLatestEventInfo(this, TAG, message, contentIntent);

        notificationMgr.notify(0, notification);
    }*/

    public void displayNotificationMessage(String message, Class activityNotDone) {
        // Send Notification
        //notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        notificationMgr.notify(MY_NOTIFICATION_ID++, myNotification);
        Log.i("Notification","NEW NOTIF FIRED: "+message);

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

