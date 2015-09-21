package asu.reach;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.VideoView;


public class Blob extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

    private ImageButton one,two,three,four,five,six,seven,eight,nine,ten,eleven,twelve;
    private ScrollView trickView;
    private ImageView title;
    private VideoView vid;
    private SQLiteDatabase db;
    private int currentDay;
    private boolean twoOpen,threeOpen,fourOpen,fiveOpen,sixOpen,sevenOpen,
            eightOpen,nineOpen,tenOpen,elevenOpen,twelveOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_blob);
        init(); //initialize boolean values
        one = (ImageButton)findViewById(R.id.trickOneBtn);
        try {
            DBHelper helper = new DBHelper(this);
            //helper.releaseTrick();
            two = (ImageButton) findViewById(R.id.trickTwoBtn);
            if (helper.checkIfLocked(2)) {
                two.setBackgroundResource(R.drawable.lock_selector);
                twoOpen = false;
            }
            three = (ImageButton) findViewById(R.id.trickThreeBtn);
            if (helper.checkIfLocked(3)) {
                three.setBackgroundResource(R.drawable.lock_selector);
                threeOpen = false;
            }
            four = (ImageButton) findViewById(R.id.trickFourBtn);
            if (helper.checkIfLocked(4)) {
                four.setBackgroundResource(R.drawable.lock_selector);
                fourOpen = false;
            }
            five = (ImageButton) findViewById(R.id.trickFiveBtn);
            if (helper.checkIfLocked(5)) {
                five.setBackgroundResource(R.drawable.lock_selector);
                fiveOpen = false;
            }
            six = (ImageButton) findViewById(R.id.trickSixBtn);
            if (helper.checkIfLocked(6)) {
                six.setBackgroundResource(R.drawable.lock_selector);
                sixOpen = false;
            }
            seven = (ImageButton) findViewById(R.id.trickSevenBtn);
            if (helper.checkIfLocked(7)){
                seven.setBackgroundResource(R.drawable.lock_selector);
                sevenOpen = false;
            }
            eight = (ImageButton)findViewById(R.id.trickEightBtn);
            if(helper.checkIfLocked(8)) {
                eight.setBackgroundResource(R.drawable.lock_selector);
                eightOpen = false;
            }
            nine = (ImageButton)findViewById(R.id.trickNineBtn);
            if(helper.checkIfLocked(9)) {
                nine.setBackgroundResource(R.drawable.lock_selector);
                nineOpen = false;
            }
            ten = (ImageButton)findViewById(R.id.trickTenBtn);
            if(helper.checkIfLocked(10)) {
                ten.setBackgroundResource(R.drawable.lock_selector);
                tenOpen = false;
            }
            eleven = (ImageButton)findViewById(R.id.trickElevenBtn);
            if(helper.checkIfLocked(11)) {
                eleven.setBackgroundResource(R.drawable.lock_selector);
                elevenOpen = false;
            }
            twelve = (ImageButton)findViewById(R.id.trickTwelveBtn);
            if(helper.checkIfLocked(12)) {
                twelve.setBackgroundResource(R.drawable.lock_selector);
                twelveOpen = false;
            }
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }



        trickView = (ScrollView)findViewById(R.id.trickView);
        vid = (VideoView)findViewById(R.id.videoView);
        title = (ImageView)findViewById(R.id.titleView);

        vid.setOnCompletionListener(this);
        vid.setOnPreparedListener(this);

        try {
            DBHelper helper = new DBHelper(this);
            db = helper.getDB();
            currentDay = helper.getCurrentDay();
            if (currentDay < 43 && currentDay > 0) {
                ContentValues v = new ContentValues();
                v.put("BLOB", 1);
                db.update("USER_ACTIVITY_TRACK", v, "DAY = " + currentDay, null);
            } else {
                Toast.makeText(this, "Invalid day,\nplease change\nstart date",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blob, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void oneClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_ONE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.warble_n));
        vid.start();
    }
    public void twoClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_TWO","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(twoOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.jump_n));
            vid.start();
        }else{locked();}
    }
    public void threeClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_THREE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(threeOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.dance_n));
            vid.start();
        }else{locked();}
    }
    public void fourClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_FOUR","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(fourOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.flip_n));
            vid.start();
        }else{locked();}
    }
    public void fiveClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_FIVE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(fiveOpen){
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.bounce_n));
            vid.start();
        }else{locked();}
    }
    public void sixClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_SIX","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(sixOpen){
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.shrink_n));
            vid.start();
        }else{locked();}
    }
    public void sevenClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_SEVEN","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(sevenOpen){
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.grow_n));
            vid.start();
        }else{locked();}
    }
    public void eightClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_EIGHT","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(eightOpen){
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.splat_n));
            vid.start();
        }else{locked();}
    }
    public void nineClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_NINE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(nineOpen){
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.fly_n));
            vid.start();
        }else{locked();}
    }
    public void tenClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_TEN","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(tenOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.double_bounce_n));
            vid.start();
        }else{locked();}
    }
    public void elevenClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_ELEVEN","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(elevenOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.crater_jump_n));
            vid.start();
        }else{locked();}
    }
    public void twelveClick(View v){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_TWELVE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(twelveOpen) {
            vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.glow_n));
            vid.start();
        }else{locked();}
    }

    private void locked(){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_LOCKED","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "SORRY! Not unlocked yet.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"BLOB_TRICK_COMPLETE","INSIDE_BLOB_TRICKS");
            helper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        vid.setBackgroundResource(R.drawable.background_space);
        title.setVisibility(View.VISIBLE);
        trickView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        vid.setBackgroundResource(0);
        title.setVisibility(View.INVISIBLE);
        trickView.setVisibility(View.INVISIBLE);
    }

    public void backClicked(View v){
        if(vid.isPlaying()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"BLOB_TRICK_BACK_PRESSED","INSIDE_BLOB_TRICKS");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            vid.stopPlayback();
            vid.setBackgroundResource(R.drawable.background_space);
            title.setVisibility(View.VISIBLE);
            trickView.setVisibility(View.VISIBLE);

        }else{
            super.onBackPressed();
        }
    }
    private void init(){
        twoOpen = true;
        threeOpen = true;
        fourOpen = true;
        fiveOpen = true;
        sixOpen = true;
        sevenOpen = true;
        eightOpen = true;
        nineOpen = true;
        tenOpen = true;
        elevenOpen = true;
        twelveOpen = true;
    }
}
