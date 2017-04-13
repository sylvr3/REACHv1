package asu.reach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class ABMTStartScreen extends Activity implements View.OnClickListener{
    private Button trialButton, tutorailButton;
    private ImageButton homeImageButton;
    public Intent intent;
    private static boolean disableTrial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_abmtstart_screen);
        trialButton = (Button)findViewById(R.id.trialButton);
        tutorailButton = (Button)findViewById(R.id.tutorialButton);
        homeImageButton = (ImageButton) findViewById(R.id.abmtHomeButton);
        homeImageButton.setOnClickListener(this);
        trialButton.setOnClickListener(this);
        tutorailButton.setOnClickListener(this);
        intent = new Intent(getBaseContext(),AttentionBiasedToolbox.class);

       if (getIntent().getStringExtra("completed").equals("true")) {
           trialButton.setClickable(true);
       }
        else {
           trialButton.setClickable(false);
       }


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == trialButton.getId()) {
            intent.putExtra("status","trial");
            startActivity(intent);
        }
        if(view.getId() == tutorailButton.getId()) {
            intent.putExtra("status","abmt");
            startActivity(intent);
        }

        if(view.getId() == homeImageButton.getId()){
            this.finish();
        }
    }
}
