package asu.reach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;


public class Landing extends Activity implements View.OnClickListener {

    private SQLiteDatabase db;
    private int stopPosition=0;
    private ImageButton dd,stic,stop,relax,wh;
    private ImageView blob;

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
        if(v.getId() == wh.getId()){
            Intent intent = new Intent(this, WorryHeads.class);
            startActivity(intent);
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
}
