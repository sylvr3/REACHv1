package asu.reach;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.Toast;

public class Landing extends Activity implements View.OnClickListener {

    private SQLiteDatabase db;
    private int stopPosition=0;
    private ImageButton dd,stic,stop,relax,wh;
    private Button admin;
    private ImageView blob;
    private Button okDialogButton,cancelDialogButton;
    public AlarmManager aManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_landing);

        if(savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position",0);
        }
        dd = (ImageButton)findViewById(R.id.ddBtn);
        stic = (ImageButton)findViewById(R.id.sticBtn);
        stop = (ImageButton)findViewById(R.id.stopBtn);
        wh = (ImageButton)findViewById(R.id.whBtn);
        relax = (ImageButton)findViewById(R.id.relaxBtn);
        blob = (ImageView)findViewById(R.id.whiteBGView);

        relax.setOnClickListener(this);
        dd.setOnClickListener(this);
        stic.setOnClickListener(this);
        stop.setOnClickListener(this);
        blob.setOnClickListener(this);
        wh.setOnClickListener(this);

        DBHelper helper = new DBHelper(this);
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();
        AnimationDrawable anim = (AnimationDrawable) blob.getBackground();
        anim.start();

        /* Chinmay Dhekne edit starts*/
        admin = (Button) findViewById(R.id.admin_button);
        admin.setOnClickListener(this);
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
        if(v.getId()==admin.getId()){
            final Context context = this;
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.admin_pwd_pop_up);
            dialog.setTitle("Enter Admin Password");
            okDialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            cancelDialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
            okDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAdminPwdDialog();
                    /*SQLiteDatabase db = this.getWritableDatabase();
                    Cursor cursor = db.rawQuery(selectQuery, null);
                    EditText pwdText = (EditText) findViewById(R.id.pwd_editText);
                    Intent intent = new Intent(Landing.this, Preferences.class);
                    startActivity(intent);*/
                    /*if(pwdText.getText().toString().equals("73224")){
                        Intent intent = new Intent(Landing.this, Preferences.class);
                        startActivity(intent);
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Invalid Password. Please Try Again",Toast.LENGTH_SHORT);
                        toast.show();
                    }*/
                }
            });
            cancelDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    dialog.dismiss();
                }
            });
            dialog.show();
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

    public void openAdminPwdDialog()
    {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.admin_pwd_pop_up);
        dialog.setTitle("Enter Admin Password");
        okDialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        cancelDialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        okDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pwdText = (EditText) findViewById(R.id.pwd_editText);
                Intent intent = new Intent(Landing.this, Preferences.class);
                DBHelper db = new DBHelper(getApplicationContext());
                boolean pwdMatch = db.checkAdminPwd(pwdText.getText().toString());
                if(pwdMatch)
                    startActivity(intent);
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid Password. Please Try Again",Toast.LENGTH_SHORT);
                    toast.show();
                }

                    /*if(pwdText.getText().equals("73224")){
                        Intent intent = new Intent(Landing.this, Preferences.class);
                        startActivity(intent);
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Invalid Password. Please Try Again",Toast.LENGTH_SHORT);
                        toast.show();
                    }*/
            }
        });
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        dialog.show();
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
}
