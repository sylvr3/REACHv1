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
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class Safe extends Activity implements View.OnClickListener, DialogInterface.OnClickListener{
    private SQLiteDatabase db;
    private RelativeLayout oLayout,msgLayout;
    private ImageView o1,o2,o3,title,gjView, answerImageView;
    private TextView oOne, oTwo, oThree, message, answerTextView;
    private ImageButton back, again, done, next, nextFirm, doneRecording;
    private String sText;
    private VideoView gj;
    private LinearLayout complete;

    //Safe
    private RelativeLayout rLayout;
    private ImageView safePRMImageView, safeEyeContactImageView, safeBlob;
    private ImageButton safeRecordImageButton, safeDoneImageButton;

    private int wrongO;  // which 0 is incorrect
    private boolean choice = false; // to remove "TRY AGAIN"
    private boolean wrong = false; // to save wrong O in DB
    private boolean s = true;    //  S is currently showing
    private boolean intro = true;  // intro is showing
    private int currentDay;
    private int chosenAnswer = 0; // correct choice they selected


    //Safe
    private boolean onRecord = false; // Record screens are showing

    // Recording
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private File mediaStorageDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_safe);
        oLayout = (RelativeLayout)findViewById(R.id.fLayout);
        msgLayout = (RelativeLayout)findViewById(R.id.msgLayout);
        o1 = (ImageView)findViewById(R.id.fOne);
        o2 = (ImageView)findViewById(R.id.fTwo);
        o3 = (ImageView)findViewById(R.id.fThree);
        oOne = (TextView)findViewById(R.id.fOneTxt);
        oTwo = (TextView)findViewById(R.id.fTwoTxt);
        oThree = (TextView)findViewById(R.id.fThreeTxt);
        message = (TextView)findViewById(R.id.message);
        back = (ImageButton)findViewById(R.id.whBackBtn);
        again = (ImageButton)findViewById(R.id.againBtn);
        again = (ImageButton)findViewById(R.id.againBtn);
        done = (ImageButton)findViewById(R.id.whDoneBtn);
        next = (ImageButton)findViewById(R.id.whNextBtn);
        complete = (LinearLayout)findViewById(R.id.completeLayout);
        title = (ImageView)findViewById(R.id.whMessage);
        gj = (VideoView)findViewById(R.id.gjVid);
        gjView = (ImageView)findViewById(R.id.gjView);
        nextFirm = (ImageButton)findViewById(R.id.nextFirm);
        doneRecording = (ImageButton)findViewById(R.id.recordDone);

        //Recording
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/recording.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        // create a new directory with name REACH in internal storage if the directory is not exist
        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "REACH");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        //SAFE

        rLayout = (RelativeLayout) findViewById(R.id.recordLayout);
        safePRMImageView = (ImageView) findViewById(R.id.recordMsg);
        safeEyeContactImageView = (ImageView) findViewById(R.id.lookEyesMsg);
        safeRecordImageButton = (ImageButton) findViewById(R.id.recordButton);
        safeDoneImageButton = (ImageButton) findViewById(R.id.eyeContactDone);
        safeBlob = (ImageView)findViewById(R.id.safeBlob);

        safeRecordImageButton.setOnClickListener(this);
        safeDoneImageButton.setOnClickListener(this);

        rLayout.setVisibility(View.GONE);
        safePRMImageView.setVisibility(View.GONE);
        safeEyeContactImageView.setVisibility(View.GONE);
        safeRecordImageButton.setVisibility(View.GONE);
        safeDoneImageButton.setVisibility(View.GONE);
        safeBlob.setVisibility(View.GONE);
        answerImageView = (ImageView) findViewById(R.id.answer);
        answerTextView = (TextView) findViewById(R.id.answerTxt);
        done.setVisibility(View.GONE);
        doneRecording.setVisibility(View.GONE);

        oOne.setOnClickListener(this);
        oTwo.setOnClickListener(this);
        oThree.setOnClickListener(this);
        back.setOnClickListener(this);
        again.setOnClickListener(this);
        done.setOnClickListener(this);
        next.setOnClickListener(this);
        nextFirm.setOnClickListener(this);
        doneRecording.setOnClickListener(this);


        Typeface t = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        oOne.setTypeface(t);
        oTwo.setTypeface(t);
        oThree.setTypeface(t);
        message.setTypeface(t);
        answerTextView.setTypeface(t);

        DBHelper helper = new DBHelper(this);
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();

        oLayout.setVisibility(View.GONE);
        msgLayout.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);

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
            c = db.rawQuery("SELECT * from SAFE where COMPLETED_FLAG = 0", null); // works
            // Random seed
            if(c.getCount() > 0) {
                Random num = new Random(System.currentTimeMillis());
                int position = (int) (c.getCount() * num.nextDouble());
                c.moveToPosition(position);
                wrongO = (int)(num.nextDouble()*2);
                sText = c.getString(c.getColumnIndex("SITUATION"));
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
        if(oOne.getText().length() > 10){
            oOne.setTextSize(10);
        }
        if(oTwo.getText().length() > 10){
            oTwo.setTextSize(11);
        }
        if(oThree.getText().length() > 10){
            oThree.setTextSize(11);
        }

    }
    @Override
    public void onClick(View v) {
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
                    message.setText("Firm But Kind Voice:\n\n");
                    //  complete(oOne.getText().toString());
                    //   speakAnswer(oOne.getText().toString());
                    firmButKindVoice(oOne.getText().toString());
                    chosenAnswer = 1;

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
                    message.setText("Firm But Kind Voice:\n\n");
                    // complete(oTwo.getText().toString());
                    //  speakAnswer(oTwo.getText().toString());
                    firmButKindVoice(oTwo.getText().toString());
                    chosenAnswer = 2;

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
                    message.setText("Firm But Kind Voice:\n\n");
                    //  complete(oThree.getText().toString());
                    // speakAnswer(oThree.getText().toString());
                    firmButKindVoice(oThree.getText().toString());
                    chosenAnswer = 3;

                }
            }
        }

        if (v.getId() == back.getId()){

            if(!choice) {
                if (s || onRecord) {
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this);
                    dialog.show(fm, "frag");
                } else {
                    if (intro) {
                        s = true;
                        back.setBackgroundResource(R.drawable.home_selector);
                        message.setText("Situation:\n\n" + sText);
                    } else {
                        intro = true;
                        message.setText("Speak Your Mind\n\n");
                        oLayout.setVisibility(View.GONE);
                        msgLayout.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        title.setVisibility(View.GONE);
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
                message.setText("Speak Your Mind\n\n");
                s = false;
                back.setBackgroundResource(R.drawable.back_selector);
            }else{


                intro = false;
                msgLayout.setVisibility(View.GONE);
                oLayout.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
            }
        }


        if(v.getId() == nextFirm.getId()){

            safeRecordImageButton.setVisibility(View.GONE);
            safeBlob.setVisibility(View.VISIBLE);
            safeBlob.setBackgroundResource(R.drawable.safe_blob_eye_contact);
            AnimationDrawable anim1 = (AnimationDrawable) safeBlob.getBackground();
            anim1.start();

            safePRMImageView.setVisibility(View.GONE);
            safeEyeContactImageView.setVisibility(View.VISIBLE);
            safeRecordImageButton.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            answerImageView.setVisibility(View.VISIBLE);
            nextFirm.setVisibility(View.GONE);



        }

        if(v.getId() == safeDoneImageButton.getId()){

            safeEyeContactImageView.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.GONE);
            safeRecordImageButton.setVisibility(View.GONE);

          speakAnswer();

        }

        //SAFE
        if(v.getId() == safeRecordImageButton.getId()){

            rLayout.setVisibility(View.VISIBLE);
            msgLayout.setVisibility(View.GONE);
            safePRMImageView.setVisibility(View.GONE);
            safeEyeContactImageView.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            // safeBlob.setBackgroundResource(R.drawable.safe_blob);

            //  answerTextView.setText(msg);
            answerTextView.setVisibility(View.GONE);
            answerImageView.setVisibility(View.GONE);
            doneRecording.setVisibility(View.VISIBLE);

         //   again.setVisibility(View.VISIBLE);
         //   done.setVisibility(View.VISIBLE);

            // Create file name with the timeStamp
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);
            String fileName = "audio_" + timeStampFormat.format(new Date())+ ".3gp";
            //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
            outputFile = mediaStorageDir.getAbsolutePath()+"/"+fileName;
            mediaRecorder.setOutputFile(outputFile);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(getApplicationContext(),"Recording Started", Toast.LENGTH_LONG).show();
                safeRecordImageButton.setVisibility(View.GONE);
            }
            catch(IOException ie) {
                //System.out.println(ie.fillInStackTrace());
                Toast.makeText(getApplicationContext(),"Exception happend", Toast.LENGTH_LONG).show();
            }

            onRecord = true;


        }


        if (v.getId() == doneRecording.getId()) {

            complete("test");

            safeEyeContactImageView.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.GONE);
            doneRecording.setVisibility(View.GONE);
            again.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            mediaRecorder.stop();
            mediaRecorder.release();
            Toast.makeText(getApplicationContext(),"Recorded Successfully", Toast.LENGTH_LONG).show();
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Exception in MediaPlayer", Toast.LENGTH_LONG).show();
            }
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

    public String getAns() {
        String answer = "";
        switch(chosenAnswer)
        {

            case 1:
                answer = oOne.getText().toString();
            case 2:
                answer = oTwo.getText().toString();
            case 3:
                answer = oThree.getText().toString();

        }
        return answer;
    }

    private void firmButKindVoice(String msg) {

        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        next.setVisibility(View.GONE);

        rLayout.setVisibility(View.VISIBLE);
        safePRMImageView.setVisibility(View.GONE);
        safeEyeContactImageView.setVisibility(View.GONE);
        safeBlob.setVisibility(View.GONE);

        msgLayout.setVisibility(View.VISIBLE);

        message.setText("Firm But Kind Voice\n\n");

        title.setVisibility(View.GONE);
        next.setVisibility(View.GONE);

        safeRecordImageButton.setVisibility(View.GONE);
        nextFirm.setVisibility(View.VISIBLE);
        answerTextView.setVisibility(View.GONE);
        answerImageView.setVisibility(View.GONE);
        back.setBackgroundResource(R.drawable.home_selector);
        back.setVisibility(View.VISIBLE);
    }



    private void speakAnswer(){
        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        nextFirm.setVisibility(View.GONE);

        rLayout.setVisibility(View.VISIBLE);

        safePRMImageView.setVisibility(View.VISIBLE);
        safeEyeContactImageView.setVisibility(View.GONE);

        safeBlob.setVisibility(View.VISIBLE);
        safeBlob.setBackgroundResource(R.drawable.safe_blob);

        msgLayout.setVisibility(View.GONE);

        safeRecordImageButton.setVisibility(View.VISIBLE);
        done.setVisibility(View.GONE);

        answerTextView.setText(getAns());
        answerTextView.setVisibility(View.GONE);
        answerImageView.setVisibility(View.GONE);

        back.setBackgroundResource(R.drawable.home_selector);
        back.setVisibility(View.VISIBLE);

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
                back.setBackgroundResource(R.drawable.back_selector);
                back.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                rLayout.setVisibility(View.GONE);
                onRecord = false;
            }
        });



        ContentValues c = new ContentValues();
        c.put("TIMESTAMP", System.currentTimeMillis());
        c.put("SITUATION", sText);
        c.put("SELECTED_F", msg);
        if(wrong) {
            c.put("WRONG_FLAG", 1);
        }
        //db.insert("SAFE_COMPLETION", "TIMESTAMP,SITUATION,SELECTED_F", c);
        c = new ContentValues();
        c.put("COMPLETED_FLAG", 1);
        int foo = db.update("SAFE",c,"SITUATION = \""+sText.replace("\"", "\"\"")+"\"",null);
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
