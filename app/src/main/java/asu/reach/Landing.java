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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Landing extends Activity implements View.OnClickListener,DialogInterface.OnClickListener {

    private SQLiteDatabase db;
    private RelativeLayout topLeftLayout, topRightLayout, bottomLeftLayout, bottomRightLayout;
    private int stopPosition=0;
    private ImageButton dd,stic,stop,relax,wh;
    private Button admin;
    private ImageView blob;
    private Button okDialogButton,cancelDialogButton;
    private EditText pin;
    private boolean topLeft,topRight,bottomLeft,bottomRight;
    private long time;
    private final long TWO_SECONDS = 2000;
    public AlarmManager aManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_landing);

        dd = (ImageButton)findViewById(R.id.ddBtn);
        stic = (ImageButton)findViewById(R.id.sticBtn);
        stop = (ImageButton)findViewById(R.id.stopBtn);
        wh = (ImageButton)findViewById(R.id.whBtn);
        relax = (ImageButton)findViewById(R.id.relaxBtn);
        blob = (ImageView)findViewById(R.id.whiteBGView);
        topLeftLayout = (RelativeLayout)findViewById(R.id.topLeft);
        topRightLayout = (RelativeLayout)findViewById(R.id.topRight);
        bottomLeftLayout = (RelativeLayout)findViewById(R.id.bottomLeft);
        bottomRightLayout = (RelativeLayout)findViewById(R.id.bottomRight);
        topLeft = false;
        topRight = false;
        bottomLeft = false;
        bottomRight = false;
        time = System.currentTimeMillis();

        relax.setOnClickListener(this);
        dd.setOnClickListener(this);
        stic.setOnClickListener(this);
        stop.setOnClickListener(this);
        blob.setOnClickListener(this);
        wh.setOnClickListener(this);
        topLeftLayout.setOnClickListener(this);
        topRightLayout.setOnClickListener(this);
        bottomLeftLayout.setOnClickListener(this);
        bottomRightLayout.setOnClickListener(this);

        try {
            DBHelper helper = new DBHelper(this);
            //helper.copyDataBase();
            //helper.openDataBase();
            db = helper.getDB();
        }catch(Exception e){
            e.printStackTrace();
        }
        AnimationDrawable anim = (AnimationDrawable) blob.getBackground();
        anim.start();

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
            bottomLeft = false;
        }
        if(v.getId() == relax.getId()){
            Intent intent = new Intent(this, Relaxation.class);
            startActivity(intent);
        }
        if(v.getId() == dd.getId()){
            Intent intent = new Intent(this, DailyDiary.class);
            startActivity(intent);
        }
        if(v.getId() == stic.getId()){
            Intent intent = new Intent(this, STIC.class);
            startActivity(intent);
        }
        if(v.getId() == stop.getId()){
            Intent intent = new Intent(this, STOP.class);
            startActivity(intent);
        }
        if(v.getId() == blob.getId()){
            Intent intent = new Intent(this, Blob.class);
            startActivity(intent);
        }
        if(v.getId() == wh.getId()) {
            Intent intent = new Intent(this, WorryHeads.class);
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
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();
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
}
