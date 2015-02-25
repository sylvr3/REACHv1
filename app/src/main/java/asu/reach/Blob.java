package asu.reach;

import android.app.Activity;
import android.content.pm.ActivityInfo;
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

    ImageButton one,two,three,four,five,six,seven;
    ScrollView trickView;
    ImageView title;
    VideoView vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_blob);

        one = (ImageButton)findViewById(R.id.trickOneBtn);
        two = (ImageButton)findViewById(R.id.trickTwoBtn);
        three = (ImageButton)findViewById(R.id.trickThreeBtn);
        four = (ImageButton)findViewById(R.id.trickFourBtn);
        five = (ImageButton)findViewById(R.id.trickFiveBtn);
        six = (ImageButton)findViewById(R.id.trickSixBtn);
        seven = (ImageButton)findViewById(R.id.trickSevenBtn);
        trickView = (ScrollView)findViewById(R.id.trickView);
        vid = (VideoView)findViewById(R.id.videoView);
        title = (ImageView)findViewById(R.id.titleView);

        vid.setOnCompletionListener(this);
        vid.setOnPreparedListener(this);
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
        vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.warble_n));
        vid.start();
    }
    public void twoClick(View v){
        vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.jump_n));
        vid.start();
    }
    public void threeClick(View v){
        vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.dance_n));
        vid.start();
    }
    public void fourClick(View v){
        vid.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.flip_n));
        vid.start();
    }
    public void fiveClick(View v){
        locked();
    }
    public void sixClick(View v){
        locked();
    }
    public void sevenClick(View v){
        locked();
    }

    private void locked(){
        Toast.makeText(this, "SORRY! Not unlocked yet.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
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
            vid.stopPlayback();
            vid.setBackgroundResource(R.drawable.background_space);
            title.setVisibility(View.VISIBLE);
            trickView.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }
}
