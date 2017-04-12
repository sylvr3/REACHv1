package asu.reach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ABMTStartScreen extends Activity implements View.OnClickListener{
    private Button trialButton, abmtButton;
    public Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_abmtstart_screen);
        trialButton = (Button)findViewById(R.id.trialButton);
        abmtButton = (Button)findViewById(R.id.abmtButton);
        trialButton.setOnClickListener(this);
        abmtButton.setOnClickListener(this);
        intent = new Intent(getBaseContext(),AttentionBiasedToolbox.class);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == trialButton.getId()) {
            intent.putExtra("status","trial");
            startActivity(intent);
        }
        if(view.getId() == abmtButton.getId()) {
            intent.putExtra("status","abmt");
            startActivity(intent);
        }
    }
}
