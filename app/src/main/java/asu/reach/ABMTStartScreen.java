package asu.reach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class ABMTStartScreen extends Activity implements View.OnClickListener {
    private Button trialButton, tutorailButton;
    private ImageButton homeImageButton;
    public Intent intent, intent1;
    private SharedPreferences sharedPref;
    public boolean disableTrial, disableTrial2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_abmtstart_screen);
        trialButton = (Button) findViewById(R.id.trialButton);
        tutorailButton = (Button) findViewById(R.id.tutorialButton);
        homeImageButton = (ImageButton) findViewById(R.id.abmtHomeButton);
        homeImageButton.setOnClickListener(this);
        trialButton.setOnClickListener(this);
        tutorailButton.setOnClickListener(this);
        intent = new Intent(getBaseContext(), AttentionBiasedToolbox.class);
        disableTrial = false;
        intent1 = getIntent();
        if (intent1 != null) {
            if (intent1.getExtras() != null) {
                disableTrial = getIntent().getStringExtra("Enable").equals("Enable");
            }
        }
        /*if(getIntent().getExtras().containsKey("Enable"))
        disableTrial =  getIntent().getStringExtra("Enable").equals("Enable");*/
        if (!disableTrial) trialButton.setEnabled(false);
        else trialButton.setEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("abmt_shared", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("disableTrial", true)) {
            trialButton.setEnabled(false);
            disableTrial = true;
        } else {
            trialButton.setEnabled(true);
            disableTrial = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == trialButton.getId()) {
            intent.putExtra("status", "trial");
            startActivityForResult(intent, 5);
        }
        if (view.getId() == tutorailButton.getId()) {
            intent.putExtra("status", "abmt");
            startActivityForResult(intent, 5);
        }

        if (view.getId() == homeImageButton.getId()) {
            this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 5: {
//                if (resultCode == Activity.RESULT_OK) {
//                    if (data.getExtras() != null) {
//                        disableTrial = data.getStringExtra("Enable").equals("Enable");
//                    }
//
//                    System.out.println("disbleTrial " + disableTrial);
//                    if (!disableTrial)
//                        trialButton.setEnabled(false);
//                    else
//                        trialButton.setEnabled(true);
//                }
//                break;
//            }
//        }
        SharedPreferences sharedPreferences = getSharedPreferences("abmt_shared", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("disableTrial", true)) {
            trialButton.setEnabled(false);
            disableTrial = true;
        } else {
            trialButton.setEnabled(true);
            disableTrial = false;
        }
    }
}
