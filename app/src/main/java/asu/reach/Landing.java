package asu.reach;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Calendar;

public class Landing extends Activity implements View.OnClickListener,DialogInterface.OnClickListener, GestureDetector.OnGestureListener {

    private SQLiteDatabase db;
    private RelativeLayout topLeftLayout, topRightLayout, bottomLeftLayout, bottomRightLayout;
    private ImageButton mb,stic,abmt,relax,wh,standUp;
    private ImageView blob,abmtGlow,sticGlow,whGlow,mbGlow,relaxGlow, standUpGlow;
    private ViewSwitcher viewSwitcher;
    private GestureDetector gestureDetector;
    private EditText pin;
    private boolean topLeft,topRight,bottomRight;
    private long time;
    private final long TWO_SECONDS = 2000;
    private int currentDay;
    public AlarmManager aManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_landing);

       /* try{
            DBHelper helper = new DBHelper(this);
            db = helper.getDB();
            Cursor c = db.rawQuery("select * from DATE_TIME_SET",null);
            if(c.moveToNext()==null) {
                ContentValues v = new ContentValues();
                v.put("start_date", "default");
                v.put("start_time", "01:00");
                v.put("trick_day1", "default");
                v.put("trick_day2", "default");
                db.insert("DATE_TIME_SET", null, v);
                db.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/

        /*Intent intent = new Intent(this,NotifyService.class);
        startService(intent);*/
        Intent ii = new Intent(this, NotifyService.class);
        PendingIntent pii = PendingIntent.getService(this, 2222, ii,PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 0);
//registering our pending intent with alarmmanager
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),60000, pii);


        mb = (ImageButton)findViewById(R.id.mbBtn);
        stic = (ImageButton)findViewById(R.id.sticBtn);
        abmt = (ImageButton)findViewById(R.id.abmtBtn);
        wh = (ImageButton)findViewById(R.id.whBtn);
        relax = (ImageButton)findViewById(R.id.relaxBtn);
        standUp = (ImageButton)findViewById(R.id.standupBtn);         //safe
        blob = (ImageView)findViewById(R.id.whiteBGView);
        topLeftLayout = (RelativeLayout)findViewById(R.id.topLeft);
        topRightLayout = (RelativeLayout)findViewById(R.id.topRight);
        bottomLeftLayout = (RelativeLayout)findViewById(R.id.bottomLeft);
        bottomRightLayout = (RelativeLayout)findViewById(R.id.bottomRight);
        abmtGlow = (ImageView)findViewById(R.id.abmtGlow);
        sticGlow = (ImageView)findViewById(R.id.sticGlow);
        whGlow = (ImageView)findViewById(R.id.whGlow);
        mbGlow = (ImageView)findViewById(R.id.mbGlow);
        relaxGlow = (ImageView)findViewById(R.id.relaxGlow);
        standUpGlow = (ImageView)findViewById(R.id.standupGlow);      //safe
        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        topLeft = false;
        topRight = false;
        bottomRight = false;
        time = System.currentTimeMillis();

        relax.setOnClickListener(this);
        mb.setOnClickListener(this);
        stic.setOnClickListener(this);
        abmt.setOnClickListener(this);
        blob.setOnClickListener(this);
        wh.setOnClickListener(this);
        standUp.setOnClickListener(this);
        topLeftLayout.setOnClickListener(this);
        topRightLayout.setOnClickListener(this);
        bottomLeftLayout.setOnClickListener(this);
        bottomRightLayout.setOnClickListener(this);
        viewSwitcher.setInAnimation(this, android.R.anim.fade_in);
        viewSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        gestureDetector = new GestureDetector(this, this);

        try {
            DBHelper helper = new DBHelper(this);
            currentDay = helper.getCurrentDay();
            if(currentDay < 0){
                Toast.makeText(this, "Please enter a start date.", Toast.LENGTH_SHORT).show();
            }else{
                AnimationDrawable anim1 = (AnimationDrawable) blob.getBackground();
                anim1.start();

                if(helper.checkActivity(DBHelper.Action.STOP)){
                    abmtGlow.setVisibility(View.VISIBLE);
                    AnimationDrawable stopAnim = (AnimationDrawable) abmtGlow.getBackground();
                    stopAnim.start();
                }else{
                    abmtGlow.setVisibility(View.GONE);
                }
                if(helper.checkActivity(DBHelper.Action.STIC)) {
                    sticGlow.setVisibility(View.VISIBLE);
                    AnimationDrawable sticAnim = (AnimationDrawable) sticGlow.getBackground();
                    sticAnim.start();
                }else{
                    sticGlow.setVisibility(View.GONE);
                }
                if(helper.checkActivity(DBHelper.Action.WORRYHEADS)) {
                    whGlow.setVisibility(View.VISIBLE);
                    AnimationDrawable whAnim = (AnimationDrawable) whGlow.getBackground();
                    whAnim.start();
                }else{
                    whGlow.setVisibility(View.GONE);
                }
                if(helper.checkActivity(DBHelper.Action.DAILY_DIARY)) {
                    mbGlow.setVisibility(View.VISIBLE);
                    AnimationDrawable ddAnim = (AnimationDrawable) mbGlow.getBackground();
                    ddAnim.start();
                }else{
                    mbGlow.setVisibility(View.GONE);
                }
                if(helper.checkActivity(DBHelper.Action.RELAXATION)) {
                    relaxGlow.setVisibility(View.VISIBLE);
                    AnimationDrawable relaxAnim = (AnimationDrawable) relaxGlow.getBackground();
                    relaxAnim.start();
                }else{
                    relaxGlow.setVisibility(View.GONE);
                }
            }
            helper.trackEvent(helper,"APP_STARTED","LANDING_PAGE");
            helper.close();
            /*Intent nInt = new Intent(this, HelperService.class);
            startService(nInt);*/
        }catch(Exception e){
            e.printStackTrace();
        }
        /*aManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setRepeatingAlarm();*/
    }



    public void setRepeatingAlarm() {
        Intent intent = new Intent(this, NotifManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                (5 * 1000), pendingIntent);
    }


    @Override
    public void onClick(View v) {
        if((System.currentTimeMillis() - time) > TWO_SECONDS){
            topLeft = false;
            topRight = false;
            bottomRight = false;
        }
        if(v.getId() == relax.getId()){
            Intent intent = new Intent(this, Relaxation.class);
            startActivity(intent);
        }

        if(v.getId() == mb.getId()){
        }
        if(v.getId() == stic.getId()){
            Intent intent = new Intent(this, STIC.class);
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STIC_STARTED","LANDING_PAGE");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if(v.getId() == abmt.getId()){
            Intent intent = new Intent(this, AttentionBiasedToolbox.class);
            startActivity(intent);
        }
        if(v.getId() == blob.getId()){
            Intent intent = new Intent(this, Blob.class);
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"BLOB_TRICKS","LANDING_PAGE");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if(v.getId() == wh.getId()) {
            Intent intent = new Intent(this, WorryHeads.class);
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"WORRY_HEADS","LANDING_PAGE");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            startActivity(intent);
        }

        // Safe
        if(v.getId() == standUp.getId()) {
            Intent intent = new Intent(this, Safe.class);

            startActivity(intent);
        }

        if(v.getId() == topLeftLayout.getId()){
            topLeft = true;
            time = System.currentTimeMillis();
        }
        if(v.getId() == topRightLayout.getId() && topLeft
                && (System.currentTimeMillis() - time) < TWO_SECONDS){
            topRight = true;
            time = System.currentTimeMillis();
        }
        if(v.getId() == bottomRightLayout.getId() && topRight
                && (System.currentTimeMillis() - time) < TWO_SECONDS){
            bottomRight = true;
            time = System.currentTimeMillis();
        }
        if(v.getId() == bottomLeftLayout.getId() && bottomRight
                && (System.currentTimeMillis() - time) < TWO_SECONDS){
            pin = new EditText(this);
            pin.setHint("Please Enter Your PIN");
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"ADMIN_SETTINGS","LANDING_PAGE");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            FragmentManager fm = getFragmentManager();
            DialogBuilder dialog = DialogBuilder.newInstance("ADMIN", this, pin);
            dialog.show(fm, "frag");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
//            openAdminPwdDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBHelper helper = new DBHelper(this);
        db = helper.getDB();
        try {
            if (helper.checkActivity(DBHelper.Action.STOP)) {
                abmtGlow.setVisibility(View.VISIBLE);
                AnimationDrawable stopAnim = (AnimationDrawable) abmtGlow.getBackground();
                stopAnim.start();
            } else {
                abmtGlow.setVisibility(View.GONE);
            }
            if (helper.checkActivity(DBHelper.Action.STIC)) {
                sticGlow.setVisibility(View.VISIBLE);
                AnimationDrawable sticAnim = (AnimationDrawable) sticGlow.getBackground();
                sticAnim.start();
            } else {
                sticGlow.setVisibility(View.GONE);
            }
            if (helper.checkActivity(DBHelper.Action.WORRYHEADS)) {
                whGlow.setVisibility(View.VISIBLE);
                AnimationDrawable whAnim = (AnimationDrawable) whGlow.getBackground();
                whAnim.start();
            } else {
                whGlow.setVisibility(View.GONE);
            }
            if (helper.checkActivity(DBHelper.Action.DAILY_DIARY)) {
                mbGlow.setVisibility(View.VISIBLE);
                AnimationDrawable ddAnim = (AnimationDrawable) mbGlow.getBackground();
                ddAnim.start();
            } else {
                mbGlow.setVisibility(View.GONE);
            }
            if (helper.checkActivity(DBHelper.Action.RELAXATION)) {
                relaxGlow.setVisibility(View.VISIBLE);
                AnimationDrawable relaxAnim = (AnimationDrawable) relaxGlow.getBackground();
                relaxAnim.start();
            } else {
                relaxGlow.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:{
                if(pin.getText().length() > 0){
                    Cursor c = db.rawQuery("SELECT * FROM PINS WHERE PIN = "
                            + pin.getText().toString(),null);
                    if(c.getCount() > 0){
                        c.moveToFirst();
                        if("admin".equals(c.getString(c.getColumnIndex("OWNER")))){
                            Intent intent = new Intent(Landing.this, Preferences.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }else {
                            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }else {
                    Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:{
                dialog.dismiss();
                break;
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getRawY() > e2.getRawY()) {
            viewSwitcher.showNext();
        } else {
            viewSwitcher.showPrevious();
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
