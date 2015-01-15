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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;


public class Landing extends Activity implements View.OnClickListener {

    private VideoView vv;
    private int stopPosition=0;
    private RelativeLayout dd,stic,stop,vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_landing);

        if(savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position",0);
        }
        vv = (VideoView)findViewById(R.id.videoView);
        dd = (RelativeLayout)findViewById(R.id.ddLayout);
        stic = (RelativeLayout)findViewById(R.id.sticLayout);
        stop = (RelativeLayout)findViewById(R.id.stopLayout);
        vid = (RelativeLayout)findViewById(R.id.vidLayout);

        vv.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.idle2));
        vv.requestFocus();
        vv.start();

        vid.setOnClickListener(this);
        dd.setOnClickListener(this);
        stic.setOnClickListener(this);
        stop.setOnClickListener(this);

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == vid.getId()){
            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == dd.getId()){

            Toast.makeText(this, "DD", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == stic.getId()){

            Toast.makeText(this, "STIC", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == stop.getId()){

            Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        vv.seekTo(stopPosition);
        vv.start();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopPosition = vv.getCurrentPosition();
        vv.pause();
        outState.putInt("position", stopPosition);
    }
}
