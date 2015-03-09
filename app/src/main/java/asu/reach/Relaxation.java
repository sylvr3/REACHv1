package asu.reach;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;

/**
 * Much of this was adapted from StackOverflow answer given by user229487
 * found at http://stackoverflow.com/questions/3747139/how-can-i-show-a-mediacontroller-while-playing-audio-in-android
 *
 */

public class Relaxation extends Activity implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{

    private ImageButton intro,worrybox,bb,tgt,review;
    private MediaPlayer player;
    private MediaController control;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_relaxation);

        intro = (ImageButton)findViewById(R.id.introBtn);
        worrybox = (ImageButton)findViewById(R.id.worryboxBtn);
        bb = (ImageButton)findViewById(R.id.bbBtn);
        tgt = (ImageButton)findViewById(R.id.tgtBtn);
        review = (ImageButton)findViewById(R.id.reviewBtn);

        intro.setOnClickListener(this);
        worrybox.setOnClickListener(this);
        bb.setOnClickListener(this);
        tgt.setOnClickListener(this);
        review.setOnClickListener(this);

        player = new MediaPlayer();
        player.setOnPreparedListener(this);

        control = new MediaController(this);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == intro.getId()) {
                if(player.isPlaying()){
                    player.stop();
                }
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"RELAXATION_INTRO","INSIDE_WORRY_HEADS_ACTIVITY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                player.reset();
                player.setDataSource(this, Uri.parse("android.resource://asu.reach/raw/" + R.raw.introduction));
                player.prepare();
                player.start();
            }
            if (v.getId() == worrybox.getId()) {
                if(player.isPlaying()){
                    player.stop();
                }
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"RELAXATION_WORRYBOX","INSIDE_WORRY_HEADS_ACTIVITY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                player.reset();
                player.setDataSource(this, Uri.parse("android.resource://asu.reach/raw/" + R.raw.worrybox));
                player.prepare();
                player.start();
            }
            if (v.getId() == bb.getId()) {
                if(player.isPlaying()){
                    player.stop();
                }
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"RELAXATION_BELLY_BREATHING","INSIDE_WORRY_HEADS_ACTIVITY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                player.reset();
                player.setDataSource(this, Uri.parse("android.resource://asu.reach/raw/" + R.raw.belly_breathing));
                player.prepare();
                player.start();
            }
            if (v.getId() == tgt.getId()) {
                if(player.isPlaying()){
                    player.stop();
                }
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"RELAXATION_THE_GROWING_TREE","INSIDE_WORRY_HEADS_ACTIVITY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                player.reset();
                player.setDataSource(this, Uri.parse("android.resource://asu.reach/raw/" + R.raw.the_growing_tree));
                player.prepare();
                player.start();
            }
            if (v.getId() == review.getId()) {
                if(player.isPlaying()){
                    player.stop();
                }
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"RELAXATION_REVIEW","INSIDE_WORRY_HEADS_ACTIVITY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                player.reset();
                player.setDataSource(this, Uri.parse("android.resource://asu.reach/raw/" + R.raw.review));
                player.prepare();
                player.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        control.hide();
        player.stop();
        player.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        control.show();
        return false;
    }

    @Override
    public void start() {
        player.start();
    }
    @Override
    public void pause() {
        player.pause();
    }
    @Override
    public int getDuration() {
        return player.getDuration();
    }
    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }
    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }
    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return 0;
    }
    @Override
    public boolean canPause() {
        return true;
    }
    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }
    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        control.setMediaPlayer(this);
        control.setAnchorView(findViewById(R.id.control_layout));
        handler.post(new Runnable() {
            public void run() {
                control.setEnabled(true);
                control.show();
            }
        });
    }

    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_relaxation, menu);
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

    @Override
    public void onBackPressed() {}

    public void onBackClick(View v){
        finish();
    }
}
