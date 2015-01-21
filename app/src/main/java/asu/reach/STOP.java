package asu.reach;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class STOP extends Activity implements View.OnClickListener{

    private ImageButton respond,back,next;
    private ImageView s,t,o,p,message;
    private int state = 0;
    private final int S_STATE = 0;
    private final int T_STATE = 1;
    private final int O_STATE = 2;
    private final int P_STATE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_stop);

        respond = (ImageButton)findViewById(R.id.respondBtn);
        back = (ImageButton)findViewById(R.id.backBtn);
        next = (ImageButton)findViewById(R.id.nextBtn);

        s = (ImageView)findViewById(R.id.sView);
        t = (ImageView)findViewById(R.id.tView);
        o = (ImageView)findViewById(R.id.oView);
        p = (ImageView)findViewById(R.id.pView);
        message = (ImageView)findViewById(R.id.messageView);

        respond.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == respond.getId()){
            respond.setActivated(true);
        }
        if(v.getId() == next.getId()){
            switch(state){
                case S_STATE:{
                    if(respond.isActivated()) {
                        s.setBackgroundResource(R.drawable.s_white);
                        t.setBackgroundResource(R.drawable.t_yellow);
                        message.setBackgroundResource(R.drawable.t_message);
                        respond.setActivated(false);
                        state = T_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case T_STATE:{
                    if(respond.isActivated()) {
                        t.setBackgroundResource(R.drawable.t_white);
                        o.setBackgroundResource(R.drawable.o_yellow);
                        message.setBackgroundResource(R.drawable.o_message);
                        respond.setActivated(false);
                        state=O_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case O_STATE:{
                    if(respond.isActivated()) {
                        o.setBackgroundResource(R.drawable.o_white);
                        p.setBackgroundResource(R.drawable.p_yellow);
                        message.setBackgroundResource(R.drawable.p_message);
                        respond.setActivated(false);
                        state=P_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case P_STATE:{
                    finish();
                    break;
                }
            }
        }
        if(v.getId() == back.getId()){
            switch(state){
                case S_STATE:{
                    finish();
                    break;
                }
                case T_STATE:{
                    t.setBackgroundResource(R.drawable.t_white);
                    s.setBackgroundResource(R.drawable.s_yellow);
                    message.setBackgroundResource(R.drawable.s_message);
                    respond.setActivated(true);
                    state=S_STATE;
                    break;
                }
                case O_STATE:{
                    o.setBackgroundResource(R.drawable.o_white);
                    t.setBackgroundResource(R.drawable.t_yellow);
                    message.setBackgroundResource(R.drawable.t_message);
                    respond.setActivated(true);
                    state=T_STATE;
                    break;
                }
                case P_STATE:{
                    p.setBackgroundResource(R.drawable.p_white);
                    o.setBackgroundResource(R.drawable.o_yellow);
                    message.setBackgroundResource(R.drawable.o_message);
                    respond.setActivated(true);
                    state=O_STATE;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sto, menu);
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
}
