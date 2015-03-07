package asu.reach;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DailyDiary extends Activity implements View.OnClickListener, View.OnTouchListener,
        ThermScrollView.OnScrollStoppedListener,DialogInterface.OnClickListener
        ,DatePickerDialog.OnDateSetListener{

    private ImageButton respond,back,next,done,cancel,clear,voice,complete,arrowLeft,arrowRight;
    private LinearLayout nav,respBtns;
    private RelativeLayout blobLayout,resp,gjLayout;
    private ImageView message,gjView,title,numView,blob;
    private TextView today, date;
    private EditText response;
    private VideoView gj;
    private ThermScrollView therm;
    private int state = 0;
    private final int ONE_STATE = 0;
    private final int TWO_STATE = 1;
    private final int THREE_STATE = 2;
    private final int FOUR_STATE = 3;
    private final int ZERO_X = 8;
    private final int ONE_X = 177;
    private final int TWO_X = 315;
    private final int THREE_X = 474;
    private final int FOUR_X = 622;
    private final int FIVE_X = 768;
    private final int SIX_X = 923;
    private final int SEVEN_X = 1063;
    private final int EIGHT_X = 1210;
    private final int ZERO_M = 88;
    private final int ONE_M = 240;
    private final int TWO_M = 388;
    private final int THREE_M = 533;
    private final int FOUR_M = 683;
    private final int FIVE_M = 836;
    private final int SIX_M = 990;
    private final int SEVEN_M = 1140;
    private int currentPos = 0;
    private static final int SPEECH_REQUEST_CODE = 0;
    private String sit, act, think;
    private boolean end = false;
    private boolean dateChoose = false;
    private SQLiteDatabase db;
    private RelativeLayout.LayoutParams thermParams, normParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_daily_diary);

        respond = (ImageButton)findViewById(R.id.respondBtn);
        back = (ImageButton)findViewById(R.id.backBtn);
        next = (ImageButton)findViewById(R.id.nextBtn);
        message = (ImageView)findViewById(R.id.messageView);
        blob = (ImageView)findViewById(R.id.blobView);
        response = (EditText)findViewById(R.id.responseTxt);
        today = (TextView)findViewById(R.id.todayView);
        date = (TextView)findViewById(R.id.dateInput);
        done = (ImageButton)findViewById(R.id.doneBtn);
        cancel = (ImageButton)findViewById(R.id.cancelBtn);
        clear = (ImageButton)findViewById(R.id.clearBtn);
        voice = (ImageButton)findViewById(R.id.voiceBtn);
        nav = (LinearLayout)findViewById(R.id.navLayout);
        respBtns = (LinearLayout)findViewById(R.id.respBtnLayout);
        resp = (RelativeLayout)findViewById(R.id.respLayout);
        blobLayout = (RelativeLayout)findViewById(R.id.blobLayout);
        gjView = (ImageView)findViewById(R.id.gjView);
        gjLayout = (RelativeLayout)findViewById(R.id.gjLayout);
        complete = (ImageButton)findViewById(R.id.completeBtn);
        title = (ImageView)findViewById(R.id.titleView);
        gj = (VideoView)findViewById(R.id.gjVid);
        therm = (ThermScrollView)findViewById(R.id.thermView);
        numView = (ImageView)findViewById(R.id.numView);
        arrowLeft = (ImageButton)findViewById(R.id.arrowLeft);
        arrowRight = (ImageButton)findViewById(R.id.arrowRight);

        response.setTypeface(Typeface.createFromAsset(getAssets(), "agentorange.ttf"));
        date.setTypeface(Typeface.createFromAsset(getAssets(), "agentorange.ttf"));
        today.setTypeface(Typeface.createFromAsset(getAssets(), "agentorange.ttf"));
        respond.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        clear.setOnClickListener(this);
        voice.setOnClickListener(this);
        complete.setOnClickListener(this);
        arrowLeft.setOnClickListener(this);
        arrowRight.setOnClickListener(this);
        date.setOnClickListener(this);

        thermParams = new RelativeLayout.LayoutParams(convertToPixels(35),convertToPixels(35));
        normParams = new RelativeLayout.LayoutParams(convertToPixels(200),convertToPixels(200));
        thermParams.setMargins(convertToPixels(75),convertToPixels(180),0,0);
        normParams.setMargins(0,convertToPixels(150),0,0);
        normParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        sit = "";
        act = "";
        think = "";

        therm.setOnTouchListener(this);
        therm.setOnScrollStoppedListener(this);

        try {
            DBHelper helper = new DBHelper(this);
            //helper.copyDataBase();
            //helper.openDataBase();
            db = helper.getDB();
        }catch(Exception e){
            e.printStackTrace();
        }

        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        date.setText(f.format(now));
    }

    private int convertToPixels(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_diary, menu);
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
    public void onClick(View v) {
        if(v.getId() == respond.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"DAILY_DIARY_RESPOND_CLICKED","INSIDE_DAILY_DIARY");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            nav.setVisibility(View.GONE);
            blobLayout.setVisibility(View.GONE);
            resp.setVisibility(View.VISIBLE);
            respBtns.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        }
        if(v.getId() == done.getId()){

            if(response.getText().length()>0) {
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper,"DAILY_DIARY_DONE_CHECKED","INSIDE_DAILY_DIARY");
                    helper.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                nav.setVisibility(View.VISIBLE);
                blobLayout.setVisibility(View.VISIBLE);
                resp.setVisibility(View.GONE);
                respBtns.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                respond.setActivated(true);
            }else{
                Toast.makeText(this, "Please enter a\nresponse first.", Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == cancel.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"DAILY_DIARY_CANCEL_CLICKED","INSIDE_DAILY_DIARY");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            nav.setVisibility(View.VISIBLE);
            blobLayout.setVisibility(View.VISIBLE);
            resp.setVisibility(View.GONE);
            respBtns.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }
        if(v.getId() == clear.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"DAILY_DIARY_CLEAR_CLICKED","INSIDE_DAILY_DIARY");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            response.setText("");
        }
        if(v.getId() == voice.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"DAILY_DIARY_VOICE_INPUT","INSIDE_DAILY_DIARY");
                helper.close();
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
                case ONE_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_ONE_NEXT_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        message.setBackgroundResource(R.drawable.dd_2_message);
                        today.setVisibility(View.GONE);
                        date.setVisibility(View.GONE);
                        therm.setVisibility(View.VISIBLE);
                        arrowRight.setVisibility(View.VISIBLE);
                        arrowLeft.setVisibility(View.VISIBLE);
                        respond.setVisibility(View.GONE);
                        blob.setLayoutParams(thermParams);
                        sit = response.getText().toString();
                        state = TWO_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case TWO_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_TWO_NEXT_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        message.setBackgroundResource(R.drawable.dd_3_message);
                        therm.setVisibility(View.GONE);
                        arrowRight.setVisibility(View.GONE);
                        arrowLeft.setVisibility(View.GONE);
                        respond.setVisibility(View.VISIBLE);
                        blob.setLayoutParams(normParams);
                        if (!(act.length() > 0)){
                            respond.setActivated(false);
                        }
                        response.setText(act);
                        state=THREE_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case THREE_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_THREE_NEXT_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(respond.isActivated()) {
                        message.setBackgroundResource(R.drawable.dd_4_message);
                        if (!(think.length() > 0)){
                            respond.setActivated(false);
                        }
                        act = response.getText().toString();
                        response.setText(think);
                        state=FOUR_STATE;
                    }else{
                        Toast.makeText(this, "Please respond first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case FOUR_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_FOUR_NEXT_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    end = true;
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this, end,false);
                    dialog.show(fm, "frag");
                    break;
                }
            }
        }
        if(v.getId() == back.getId()){
            switch(state){
                case ONE_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_ONE_BACK_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    end = false;
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this, end,false);
                    dialog.show(fm, "frag");
                    break;
                }
                case TWO_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_TWO_BACK_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    message.setBackgroundResource(R.drawable.dd_1_message);
                    today.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    therm.setVisibility(View.GONE);
                    arrowRight.setVisibility(View.GONE);
                    arrowLeft.setVisibility(View.GONE);
                    respond.setVisibility(View.VISIBLE);
                    blob.setLayoutParams(normParams);
                    respond.setActivated(true);
                    state=ONE_STATE;
                    response.setText(sit);
                    break;
                }
                case THREE_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_THREE_BACK_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    message.setBackgroundResource(R.drawable.dd_2_message);
                    therm.setVisibility(View.VISIBLE);
                    blob.setLayoutParams(thermParams);
                    arrowRight.setVisibility(View.VISIBLE);
                    arrowLeft.setVisibility(View.VISIBLE);
                    respond.setVisibility(View.GONE);
                    respond.setActivated(true);
                    act = response.getText().toString();
                    state=TWO_STATE;
                    break;
                }
                case FOUR_STATE:{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"DAILY_DIARY_STATE_FOUR_BACK_CLICKED","INSIDE_DAILY_DIARY");
                        helper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    message.setBackgroundResource(R.drawable.dd_3_message);
                    respond.setActivated(true);
                    state=THREE_STATE;
                    think = response.getText().toString();
                    response.setText(act);
                    break;
                }
            }
        }
        if(v.getId()==complete.getId()){
            try {
                DBHelper helper = new DBHelper(this);
                helper.trackEvent(helper,"DAILY_DIARY_COMPLETED","INSIDE_DAILY_DIARY");
                helper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            finish();
        }
        if(v.getId()==arrowLeft.getId()){
            moveLeft();
        }
        if(v.getId()==arrowRight.getId()){
            moveRight();
        }
        if(v.getId()==date.getId()){
            dateChoose = true;
            FragmentManager fm = getFragmentManager();
            DialogBuilder dialog = DialogBuilder.newInstance("What day?",this,false,dateChoose);
            dialog.show(fm, "frag");
        }
    }

    private void moveLeft(){
        switch (currentPos) {
            case 1: {
                therm.setScrollX(ZERO_X);
                currentPos = 0;
                break;
            }
            case 2: {
                therm.setScrollX(ONE_X);
                currentPos = 1;
                break;
            }
            case 3: {
                therm.setScrollX(TWO_X);
                currentPos = 2;
                break;
            }
            case 4: {
                therm.setScrollX(THREE_X);
                currentPos = 3;
                break;
            }
            case 5: {
                therm.setScrollX(FOUR_X);
                currentPos = 4;
                break;
            }
            case 6: {
                therm.setScrollX(FIVE_X);
                currentPos = 5;
                break;
            }
            case 7: {
                therm.setScrollX(SIX_X);
                currentPos = 6;
                break;
            }
            case 8: {
                therm.setScrollX(SEVEN_X);
                currentPos = 7;
                break;
            }
        }
    }

    private void moveRight() {
        switch (currentPos) {
            case 0: {
                therm.setScrollX(ONE_X);
                currentPos = 1;
                break;
            }
            case 1: {
                therm.setScrollX(TWO_X);
                currentPos = 2;
                break;
            }
            case 2: {
                therm.setScrollX(THREE_X);
                currentPos = 3;
                break;
            }
            case 3: {
                therm.setScrollX(FOUR_X);
                currentPos = 4;
                break;
            }
            case 4: {
                therm.setScrollX(FIVE_X);
                currentPos = 5;
                break;
            }
            case 5: {
                therm.setScrollX(SIX_X);
                currentPos = 6;
                break;
            }
            case 6: {
                therm.setScrollX(SEVEN_X);
                currentPos = 7;
                break;
            }
            case 7: {
                therm.setScrollX(EIGHT_X);
                currentPos = 8;
                break;
            }
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
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            int pos = therm.getScrollX();
            numView.setVisibility(View.VISIBLE);
            if(pos < ZERO_M){
                numView.setBackgroundResource(R.drawable.thermo_0);
            } else if (pos < ONE_M) {
                numView.setBackgroundResource(R.drawable.thermo_1);
            } else if (pos < TWO_M) {
                numView.setBackgroundResource(R.drawable.thermo_2);
            } else if (pos < THREE_M) {
                numView.setBackgroundResource(R.drawable.thermo_3);
            } else if (pos < FOUR_M) {
                numView.setBackgroundResource(R.drawable.thermo_4);
            } else if (pos < FIVE_M) {
                numView.setBackgroundResource(R.drawable.thermo_5);
            } else if (pos < SIX_M) {
                numView.setBackgroundResource(R.drawable.thermo_6);
            } else if (pos < SEVEN_M) {
                numView.setBackgroundResource(R.drawable.thermo_7);
            } else {
                numView.setBackgroundResource(R.drawable.thermo_8);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            numView.setVisibility(View.GONE);
            int pos = therm.getScrollX();
            if (pos < ZERO_M) {
                therm.setScrollX(ZERO_X);
                currentPos = 0;
            } else if (pos < ONE_M) {
                therm.setScrollX(ONE_X);
                currentPos = 1;
            } else if (pos < TWO_M) {
                therm.setScrollX(TWO_X);
                currentPos = 2;
            } else if (pos < THREE_M) {
                therm.setScrollX(THREE_X);
                currentPos = 3;
            } else if (pos < FOUR_M) {
                therm.setScrollX(FOUR_X);
                currentPos = 4;
            } else if (pos < FIVE_M) {
                therm.setScrollX(FIVE_X);
                currentPos = 5;
            } else if (pos < SIX_M) {
                therm.setScrollX(SIX_X);
                currentPos = 6;
            } else if (pos < SEVEN_M) {
                therm.setScrollX(SEVEN_X);
                currentPos = 7;
            } else {
                therm.setScrollX(EIGHT_X);
                currentPos = 8;
            }
        }
        return false;
    }

    @Override
    public void onScrollStopped() {
        //numView.setVisibility(View.GONE);
        int pos = therm.getScrollX();
        if (pos < ZERO_M) {
            therm.setScrollX(ZERO_X);
        } else if (pos < ONE_M) {
            therm.setScrollX(ONE_X);
        } else if (pos < TWO_M) {
            therm.setScrollX(TWO_X);
        } else if (pos < THREE_M) {
            therm.setScrollX(THREE_X);
        } else if (pos < FOUR_M) {
            therm.setScrollX(FOUR_X);
        } else if (pos < FIVE_M) {
            therm.setScrollX(FIVE_X);
        } else if (pos < SIX_M) {
            therm.setScrollX(SIX_X);
        } else if (pos < SEVEN_M) {
            therm.setScrollX(SEVEN_X);
        } else {
            therm.setScrollX(EIGHT_X);
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:{
                if(end) {
                    think = response.getText().toString();
                    response.setText("");
                    ContentValues c = new ContentValues();
                    c.put("TIMESTAMP", System.currentTimeMillis());
                    c.put("WHAT_HAPPENED", sit);
                    c.put("SCARED", currentPos);
                    c.put("ACTION", act);
                    c.put("THINK", think);
                    c.put("DATE", date.getText().toString());
                    db.insert("DAILY_DIARY_COMPLETION", "TIMESTAMP,WHAT_HAPPENED,SCARED,ACTION,THINK,DATE", c);



                    /* TODO : Track user activity

                    c = new ContentValues();
                    c.put("COMPLETED_FLAG", 1);
                    int foo = db.update("STOP_WORRYHEADS",c,"S = \""+sText+"\"",null);
                    if(foo > 0){
                        System.out.println("Successful update");
                    }
                    */

                    title.setVisibility(View.GONE);
                    blobLayout.setVisibility(View.GONE);
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
                            title.setVisibility(View.GONE);
                            blobLayout.setVisibility(View.GONE);
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        int y = current.get(Calendar.YEAR);
        int m = current.get(Calendar.MONTH);
        int d = current.get(Calendar.DAY_OF_MONTH);
        if(year <= y && monthOfYear <= m && dayOfMonth <= d) {
            date.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
        }else{
            Toast.makeText(this, "The Entry cannot be \nfor a future date.",Toast.LENGTH_SHORT).show();
        }
    }
}
