package asu.reach;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Calendar;
import java.util.Random;


public class SAFE extends Activity implements View.OnClickListener, DialogInterface.OnClickListener{
    private SQLiteDatabase db;
    private RelativeLayout oLayout,msgLayout;
    private ImageView sView, tView,o1,o2,o3,o4,title,gjView, answerImageView;
    private TextView oOne, oTwo, oThree, oFour, message, answerTextView;
    private ImageButton back, again, done, next;
    private String sText, tText, pText;
    private VideoView gj;
    private LinearLayout complete;

    //SAFE
    private RelativeLayout rLayout;
    private ImageView safePRMImageView, safeEyeContactImageView, safeBlob, safeBlobEyes;
    private ImageButton safeRecordImageButton, safeDoneImageButton;

    private int wrongO;  // which 0 is incorrect
    private boolean choice = false; // to remove "TRY AGAIN"
    private boolean wrong = false; // to save wrong O in DB
    private boolean s = true;    //  S is currently showing
    private boolean intro = true;  // intro is showing
    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_safe);

        oLayout = (RelativeLayout)findViewById(R.id.oLayout);
        msgLayout = (RelativeLayout)findViewById(R.id.msgLayout);
        sView = (ImageView)findViewById(R.id.sView);
        tView = (ImageView)findViewById(R.id.tView);
        o1 = (ImageView)findViewById(R.id.oOne);
        o2 = (ImageView)findViewById(R.id.oTwo);
        o3 = (ImageView)findViewById(R.id.oThree);
        o4 = (ImageView)findViewById(R.id.oFour);
        oOne = (TextView)findViewById(R.id.oOneTxt);
        oTwo = (TextView)findViewById(R.id.oTwoTxt);
        oThree = (TextView)findViewById(R.id.oThreeTxt);
        oFour = (TextView)findViewById(R.id.oFourTxt);
        message = (TextView)findViewById(R.id.message);
        back = (ImageButton)findViewById(R.id.whBackBtn);
        again = (ImageButton)findViewById(R.id.againBtn);
        done = (ImageButton)findViewById(R.id.whDoneBtn);
        next = (ImageButton)findViewById(R.id.whNextBtn);
        complete = (LinearLayout)findViewById(R.id.completeLayout);
        title = (ImageView)findViewById(R.id.whMessage);
        gj = (VideoView)findViewById(R.id.gjVid);
        gjView = (ImageView)findViewById(R.id.gjView);


        //SAFE

        rLayout = (RelativeLayout) findViewById(R.id.recordLayout);
        safePRMImageView = (ImageView) findViewById(R.id.recordMsg);
        safeEyeContactImageView = (ImageView) findViewById(R.id.lookEyesMsg);
        safeRecordImageButton = (ImageButton) findViewById(R.id.recordButton);
        safeDoneImageButton = (ImageButton) findViewById(R.id.recordDone);
        safeBlob = (ImageView)findViewById(R.id.safeBlob);
        answerImageView = (ImageView) findViewById(R.id.answer);
        answerTextView = (TextView) findViewById(R.id.answerTxt);

        safeRecordImageButton.setOnClickListener(this);
        safeDoneImageButton.setOnClickListener(this);

        rLayout.setVisibility(View.GONE);
        safePRMImageView.setVisibility(View.GONE);
        safeEyeContactImageView.setVisibility(View.GONE);
        safeRecordImageButton.setVisibility(View.GONE);
        safeDoneImageButton.setVisibility(View.GONE);
        safeBlob.setVisibility(View.GONE);


        sView.setOnClickListener(this);
        tView.setOnClickListener(this);
        oOne.setOnClickListener(this);
        oTwo.setOnClickListener(this);
        oThree.setOnClickListener(this);
        oFour.setOnClickListener(this);
        back.setOnClickListener(this);
        again.setOnClickListener(this);
        done.setOnClickListener(this);
        next.setOnClickListener(this);

        sView.setActivated(true);
        tView.setActivated(true);

        Typeface t = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        oOne.setTypeface(t);
        oTwo.setTypeface(t);
        oThree.setTypeface(t);
        oFour.setTypeface(t);
        message.setTypeface(t);
        answerTextView.setTypeface(t);

        DBHelper helper = new DBHelper(this);
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();

        oLayout.setVisibility(View.GONE);
        msgLayout.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        sView.setBackgroundResource(R.drawable.s_yellow);
        tView.setBackgroundResource(R.drawable.t_white);

        try {
            Calendar ca = Calendar.getInstance();
            ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH),ca.get(Calendar.DAY_OF_MONTH),0,0,0);

            Cursor c = db.rawQuery("SELECT SITUATION FROM SAFE_COMPLETION where TIMESTAMP < "
                    + ca.getTimeInMillis()
                    + " AND SITUATION not in (SELECT SITUATION FROM SAFE_COMPLETION where TIMESTAMP > "
                    + ca.getTimeInMillis() + ")", null);
            c.moveToFirst();
            ContentValues v;
            for(int x = 0; x < c.getCount(); x++){
                v = new ContentValues();
                v.put("COMPLETED_FLAG", 0);
                db.update("SAFE",v,"SITUATION = \"" + c.getString(c.getColumnIndex("SITUATION")) + "\"",null);
                c.moveToNext();
            }
            c.close();
            c = db.rawQuery("SELECT * from SAFE where COMPLETED_FLAG = 0", null);
            // Random seed
            if(c.getCount() > 0) {
                Random num = new Random(System.currentTimeMillis());
                int position = (int) (c.getCount() * num.nextDouble());
                c.moveToPosition(position);
                wrongO = (int)(num.nextDouble()*3);
                sText = c.getString(c.getColumnIndex("SITUATION"));
                tText = c.getString(c.getColumnIndex("A"));
                pText = c.getString(c.getColumnIndex("E"));
                String[] o = new String[3];
                o[0] = c.getString(c.getColumnIndex("F1_RIGHT"));
                o[1] = c.getString(c.getColumnIndex("F2_WRONG"));
                o[2] = c.getString(c.getColumnIndex("F3_WRONG"));
                System.out.println(o[0]);
                System.out.println(o[1]);
                System.out.println(o[2]);
                populateO(o);
                message.setText("Situation:\n\n"+sText);

            }else{
                message.setText("You've completed all of them!");
                next.setVisibility(View.GONE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void populateO(String[] o){
        switch(wrongO){
            case 0:{
                oOne.setText(o[2]);
                oTwo.setText(o[0]);
                oThree.setText(o[1]);
                resize();
                break;
            }
            case 1:{
                oTwo.setText(o[2]);
                oThree.setText(o[0]);
                oOne.setText(o[1]);
                resize();
                break;
            }
            case 2:{
                oThree.setText(o[2]);
                oTwo.setText(o[0]);
                oOne.setText(o[1]);
                resize();
                break;
            }

        }
    }

    private void resize(){
        if(oOne.getText().length() > 80){
            oOne.setTextSize(11);
        }
        if(oTwo.getText().length() > 80){
            oTwo.setTextSize(11);
        }
        if(oThree.getText().length() > 80){
            oThree.setTextSize(11);
        }

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == sView.getId()){
            if(!sView.isActivated()) {
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper, "SAFE_SITUATION_SHOWED", "INSIDE_SAFE_ACTIVITY");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                s = true;
                intro = true;
                sView.setBackgroundResource(R.drawable.s_yellow);
                tView.setBackgroundResource(R.drawable.t_white);
                title.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                oLayout.setVisibility(View.GONE);
                msgLayout.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                message.setText("Situation:\n\n" + sText);
            }
        }
        if (v.getId() == tView.getId()){
            if(!tView.isActivated()) {
                try {
                    DBHelper helper = new DBHelper(this);
                    helper.trackEvent(helper, "SAFE_A_SHOWED", "INSIDE_SAFE_ACTIVITY");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                s = false;
                intro = true;
                sView.setBackgroundResource(R.drawable.s_white);
                tView.setBackgroundResource(R.drawable.t_yellow);
                title.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                oLayout.setVisibility(View.GONE);
                msgLayout.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                message.setText("Speak Your Mind:\n\n" + tText);
            }
        }
        if (v.getId() == oOne.getId()){
            if(!oOne.isActivated()) {
                if (wrongO == 0) {
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"SAFE_F_WRONG","INSIDE_SAFE_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    wrongSelection();
                    oOne.setActivated(true);
                    o1.setActivated(true);
                }else{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"SAFE_F_RIGHT","INSIDE_SAFE_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    oLayout.setVisibility(View.GONE);
                    message.setText("Praise Yourself:\n\n" + pText);
//                    complete(oOne.getText().toString());
                    speakAnswer(oOne.getText().toString());
                }
            }
        }
        if (v.getId() == oTwo.getId()){
            if(!oTwo.isActivated()) {
                if (wrongO == 1) {
                    wrongSelection();
                    oTwo.setActivated(true);
                    o2.setActivated(true);
                }else{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"SAFE_F_RIGHT","INSIDE_SAFE_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    oLayout.setVisibility(View.GONE);
                    message.setText("Praise Yourself:\n\n" + pText);
//                    complete(oTwo.getText().toString());
                    speakAnswer(oTwo.getText().toString());
                }
            }
        }
        if (v.getId() == oThree.getId()){
            if(!oThree.isActivated()) {
                if (wrongO == 2) {
                    wrongSelection();
                    oThree.setActivated(true);
                    o3.setActivated(true);
                }else{
                    try {
                        DBHelper helper = new DBHelper(this);
                        helper.trackEvent(helper,"SAFE_F_RIGHT","INSIDE_SAFE_ACTIVITY");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    oLayout.setVisibility(View.GONE);
                    message.setText("Praise Yourself:\n\n" + pText);
//                    complete(oThree.getText().toString());
                    speakAnswer(oThree.getText().toString());
                }
            }
        }

        if (v.getId() == back.getId()){
            if(!choice) {
                if (s) {
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this);
                    dialog.show(fm, "frag");
                } else {
                    if (intro) {
                        s = true;
                        back.setBackgroundResource(R.drawable.home_selector);
                        message.setText("Situation:\n\n" + sText);
                        sView.setBackgroundResource(R.drawable.s_yellow);
                        tView.setBackgroundResource(R.drawable.t_white);
                    } else {
                        intro = true;
                        message.setText("Speak Your Mind:\n\n" + tText);
                        oLayout.setVisibility(View.GONE);
                        msgLayout.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        title.setVisibility(View.GONE);
                        sView.setBackgroundResource(R.drawable.s_white);
                        tView.setBackgroundResource(R.drawable.t_yellow);
                    }

                }
            }else{
                oLayout.setVisibility(View.VISIBLE);
                msgLayout.setVisibility(View.GONE);
                choice = false;
            }
        }
        if(v.getId() == again.getId()){

            Intent intent = new Intent(this, this.getClass());
            startActivity(intent);
            finish();
        }
        if(v.getId() == done.getId()){

            finish();
        }
        if(v.getId() == next.getId()){

            if(s){
                message.setText("Speak Your Mind:\n\n"+tText);
                s = false;
                back.setBackgroundResource(R.drawable.back_selector);
                sView.setBackgroundResource(R.drawable.s_white);
                tView.setBackgroundResource(R.drawable.t_yellow);
            }else{
                tView.setBackgroundResource(R.drawable.t_white);
                intro = false;
                sView.setActivated(false);
                tView.setActivated(false);
                msgLayout.setVisibility(View.GONE);
                oLayout.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
            }
        }

        //SAFE
        if(v.getId() == safeRecordImageButton.getId()){
            safeRecordImageButton.setVisibility(View.GONE);

            int id = getResources().getIdentifier("safe_blob_eyes", "drawable", getPackageName());
            safeBlob.setImageResource(id);

            safePRMImageView.setVisibility(View.GONE);
            safeEyeContactImageView.setVisibility(View.VISIBLE);
            safeRecordImageButton.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            answerImageView.setVisibility(View.VISIBLE);
        }

        if(v.getId() == safeDoneImageButton.getId()){
            safeEyeContactImageView.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.GONE);
            again.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            complete("test");
        }
    }

    private void wrongSelection(){
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"SAFE_F_WRONG","INSIDE_SAFE_ACTIVITY");
        }catch(Exception e){
            e.printStackTrace();
        }
        oLayout.setVisibility(View.GONE);
        msgLayout.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        message.setText("Try again!");
        wrong = true;
        choice = true;
    }

    private void speakAnswer(String msg){
        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        sView.setVisibility(View.GONE);
        tView.setVisibility(View.GONE);
        sView.setClickable(false);
        tView.setClickable(false);

        rLayout.setVisibility(View.VISIBLE);
        safePRMImageView.setVisibility(View.VISIBLE);
        safeEyeContactImageView.setVisibility(View.GONE);
        safeBlob.setVisibility(View.VISIBLE);
        int id = getResources().getIdentifier("safe_blob", "drawable", getPackageName());
        safeBlob.setImageResource(id);
        safeRecordImageButton.setVisibility(View.VISIBLE);
        answerTextView.setText(msg);
        answerTextView.setVisibility(View.GONE);
        answerImageView.setVisibility(View.GONE);
    }

    private void complete(String msg){
        gj.setVideoURI(Uri.parse("android.resource://asu.reach/" + R.raw.stars));
        gj.start();
        gj.setVisibility(View.VISIBLE);
        gj.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                gjView.setVisibility(View.VISIBLE);
                complete.setVisibility(View.VISIBLE);
                title.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                sView.setVisibility(View.GONE);
                tView.setVisibility(View.GONE);
                sView.setClickable(false);
                tView.setClickable(false);
                rLayout.setVisibility(View.GONE);
            }
        });

        ContentValues c = new ContentValues();
        c.put("TIMESTAMP", System.currentTimeMillis());
        c.put("SITUATION", sText);
        c.put("SELECTED_F", msg);
        if(wrong) {
            c.put("WRONG_FLAG", 1);
        }
        db.insert("SAFE_COMPLETION", "TIMESTAMP,SITUATION,SELECTED_F", c);
        c = new ContentValues();
        c.put("COMPLETED_FLAG", 1);
        int foo = db.update("SAFE",c,"SITUATION = \""+sText+"\"",null);
        if(foo > 0){
            System.out.println("Successful update");
        }
        try {
            DBHelper helper = new DBHelper(this);
            helper.trackEvent(helper,"SAFE_COMPLETED","INSIDE_SAFE_ACTIVITY");
            File file=helper.getFile();
            Log.i("File Path",file.getAbsolutePath());
            db = helper.getDB();
            currentDay = helper.getCurrentDay();
            if (currentDay < 43 && currentDay > 0) {
                ContentValues v = new ContentValues();
                v.put("SAFE", 1);
                db.update("USER_ACTIVITY_TRACK", v, "DAY = " + currentDay, null);
            } else {
                Toast.makeText(this, "Invalid day,\nplease change\nstart date",
                        Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_safe, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                finish();
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE: {
                break;
            }
        }
    }
}
