package asu.reach;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;



public class STOP extends Activity implements View.OnClickListener, DialogInterface.OnClickListener{

    private ImageButton respond,back,next,done,close,clear,voice,complete;
    private LinearLayout nav,respBtns,stopLayout;
    private RelativeLayout blob,resp,gjLayout;
    private ImageView s,t,o,p,message,gjView;
    private VideoView gj;
    private EditText response;
    private int state = 0;
    private final int S_STATE = 0;
    private final int T_STATE = 1;
    private final int O_STATE = 2;
    private final int P_STATE = 3;
    private static final int SPEECH_REQUEST_CODE = 0;
    private boolean end = false;
    private SQLiteDatabase db;
    private int currentDay;

    private String sr,tr,or,pr;

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
        response = (EditText)findViewById(R.id.responseTxt);
        done = (ImageButton)findViewById(R.id.doneBtn);
        close = (ImageButton)findViewById(R.id.cancelBtn);
        clear = (ImageButton)findViewById(R.id.clearBtn);
        voice = (ImageButton)findViewById(R.id.voiceBtn);
        nav = (LinearLayout)findViewById(R.id.navLayout);
        respBtns = (LinearLayout)findViewById(R.id.respBtnLayout);
        resp = (RelativeLayout)findViewById(R.id.respLayout);
        blob = (RelativeLayout)findViewById(R.id.blobLayout);
        gjView = (ImageView)findViewById(R.id.gjView);
        gjLayout = (RelativeLayout)findViewById(R.id.gjLayout);
        gj = (VideoView)findViewById(R.id.gjVid);
        stopLayout = (LinearLayout)findViewById(R.id.stopLayout);
        complete = (ImageButton)findViewById(R.id.completeBtn);

        response.setTypeface(Typeface.createFromAsset(getAssets(), "agentorange.ttf"));
        respond.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        done.setOnClickListener(this);
        close.setOnClickListener(this);
        clear.setOnClickListener(this);
        voice.setOnClickListener(this);
        complete.setOnClickListener(this);

        sr = "";
        tr = "";
        or = "";
        pr = "";

        try {
            DBHelper helper = new DBHelper(this);
            //helper.copyDataBase();
            //helper.openDataBase();
            db = helper.getDB();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == respond.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STOP_RESPOND_BUTTON_CLICKED","INSIDE_STOP_ACTIVITY");
            }catch(Exception e){
                e.printStackTrace();
            }
            nav.setVisibility(View.GONE);
            blob.setVisibility(View.GONE);
            resp.setVisibility(View.VISIBLE);
            respBtns.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);
        }
        if(v.getId() == done.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STOP_DONE_BUTTON_CLICKED","INSIDE_STOP_ACTIVITY");
            }catch(Exception e){
                e.printStackTrace();
            }
            if(response.getText().length()>0) {
                nav.setVisibility(View.VISIBLE);
                blob.setVisibility(View.VISIBLE);
                resp.setVisibility(View.GONE);
                respBtns.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                respond.setActivated(true);
            }else{
                Toast.makeText(this, "Please enter a\nresponse first.", Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == close.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STOP_CANCEL_CLICKED","INSIDE_STOP_ACTIVITY");
            }catch(Exception e){
                e.printStackTrace();
            }
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(response.getWindowToken(), 0);
            nav.setVisibility(View.VISIBLE);
            blob.setVisibility(View.VISIBLE);
            resp.setVisibility(View.GONE);
            respBtns.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
        }
        if(v.getId() == clear.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STOP_CLEAR_CLICKED","INSIDE_STOP_ACTIVITY");
            }catch(Exception e){
                e.printStackTrace();
            }
            response.setText("");
        }
        if(v.getId() == voice.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"STOP_VOICE_CLICKED","INSIDE_STOP_ACTIVITY");
            }catch(Exception e){
                e.printStackTrace();
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        if(v.getId() == next.getId()){
            switch(state){
                case S_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_S_NEXT_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        s.setBackgroundResource(R.drawable.s_white);
                        t.setBackgroundResource(R.drawable.t_yellow);
                        message.setBackgroundResource(R.drawable.t_message);
                        sr = response.getText().toString();
                        if(!(tr.length() > 0)){
                            respond.setActivated(false);
                        }
                        response.setText(tr);
                        state = T_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case T_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_T_NEXT_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        t.setBackgroundResource(R.drawable.t_white);
                        o.setBackgroundResource(R.drawable.o_yellow);
                        message.setBackgroundResource(R.drawable.o_message);
                        tr = response.getText().toString();
                        if(!(or.length() > 0)){
                            respond.setActivated(false);
                        }
                        response.setText(or);
                        state=O_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case O_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_O_NEXT_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        o.setBackgroundResource(R.drawable.o_white);
                        p.setBackgroundResource(R.drawable.p_yellow);
                        message.setBackgroundResource(R.drawable.p_message);
                        or = response.getText().toString();
                        if(!(pr.length() > 0)){
                            respond.setActivated(false);
                        }
                        response.setText(pr);
                        state=P_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case P_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_P_NEXT_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    end = true;
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this, end);
                    dialog.show(fm, "frag");
                    break;
                }
            }
        }
        if(v.getId() == back.getId()){
            switch(state){
                case S_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_S_BACK_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    end = false;
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this, end);
                    dialog.show(fm, "frag");
                    break;
                }
                case T_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_T_BACK_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    t.setBackgroundResource(R.drawable.t_white);
                    s.setBackgroundResource(R.drawable.s_yellow);
                    message.setBackgroundResource(R.drawable.s_message);
                    tr = response.getText().toString();
                    if(sr.length() > 0){
                        respond.setActivated(true);
                    }
                    response.setText(sr);
                    state=S_STATE;
                    break;
                }
                case O_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_O_BACK_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    o.setBackgroundResource(R.drawable.o_white);
                    t.setBackgroundResource(R.drawable.t_yellow);
                    message.setBackgroundResource(R.drawable.t_message);
                    or = response.getText().toString();
                    if(tr.length() > 0){
                        respond.setActivated(true);
                    }
                    response.setText(tr);
                    state=T_STATE;
                    break;
                }
                case P_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"STOP_P_BACK_CLICKED","INSIDE_STOP_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    p.setBackgroundResource(R.drawable.p_white);
                    o.setBackgroundResource(R.drawable.o_yellow);
                    message.setBackgroundResource(R.drawable.o_message);
                    respond.setActivated(true);
                    pr = response.getText().toString();
                    if(or.length() > 0){
                        respond.setActivated(true);
                    }
                    response.setText(or);
                    state=O_STATE;
                    break;
                }
            }
        }
        if(v.getId()==complete.getId()){
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            response.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:{
                if(end) {
                    DBHelper helper = new DBHelper(this);
                    pr = response.getText().toString();
                    response.setText("");
                    ContentValues c = new ContentValues();
                    c.put("TIMESTAMP", System.currentTimeMillis());
                    c.put("S_RESPONSE", sr);
                    c.put("T_RESPONSE", tr);
                    c.put("O_RESPONSE", or);
                    c.put("P_RESPONSE", pr);
                    db.insert("STOP",
                            "TIMESTAMP,S_RESPONSE,T_RESPONSE,O_RESPONSE,P_RESPONSE", c);
                    currentDay = helper.getCurrentDay();
                    if (currentDay < 43 && currentDay > 0) {
                        ContentValues v = new ContentValues();
                        v.put("STOP", 1);
                        db.update("USER_ACTIVITY_TRACK", v, "DAY = " + currentDay, null);
                    } else {
                        Toast.makeText(this, "Invalid day,\nplease change\nstart date",
                                Toast.LENGTH_SHORT).show();
                    }

                    stopLayout.setVisibility(View.GONE);
                    blob.setVisibility(View.GONE);
                    nav.setVisibility(View.GONE);
                    gjLayout.setVisibility(View.VISIBLE);
                    gjView.setVisibility(View.VISIBLE);
                    complete.setVisibility(View.VISIBLE);
                    gj.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.stars));
                    gj.start();
                    gj.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            stopLayout.setVisibility(View.GONE);
                            blob.setVisibility(View.GONE);
                            nav.setVisibility(View.GONE);
                            gjLayout.setVisibility(View.VISIBLE);
                            gjView.setVisibility(View.VISIBLE);
                            complete.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    finish();
                }
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:{
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
