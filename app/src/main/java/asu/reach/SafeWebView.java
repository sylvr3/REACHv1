package asu.reach;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class SafeWebView extends Activity implements DialogInterface.OnClickListener {

    private WebView mWebView;
    private SQLiteDatabase db;
    private Situation situation;

    private MediaRecorder mediaRecorder;
    private String outputFile;
    private File mediaStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getDB();

        situation = retrieveSituationFromDB();
        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "REACH");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.safe_web_view);
        mWebView = (WebView) findViewById(R.id.safe_web_view);
        mWebView.setInitialScale(1);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setBackgroundResource(R.drawable.background_space);
        mWebView.setBackgroundColor(0x00000000);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getSituationDescription() {
                return situation.description;
            }

            @JavascriptInterface
            public String getSituationRightAnswer() {
                return situation.rightAnswer;
            }

            @JavascriptInterface
            public String getSituationWrongAnswer1() {
                return situation.wrongAnswer1;
            }

            @JavascriptInterface
            public String getSituationWrongAnswer2() {
                return situation.wrongAnswer2;
            }

            @JavascriptInterface
            public void goToHomeScreen(boolean force) {
                goToHomepage(force);
            }

            @JavascriptInterface
            public void startRecording() {
                sw_startRecording();
            }

            @JavascriptInterface
            public void stopRecording() {
                sw_stopRecording();
            }
        }, "safe");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        mWebView.loadUrl("file:///android_asset/www/html/safe.html");
    }

    private Situation retrieveSituationFromDB() {
        Calendar ca = Calendar.getInstance();
        Cursor c = db.rawQuery("SELECT SITUATION FROM SAFE_COMPLETION where TIMESTAMP < "
                + ca.getTimeInMillis()
                + " AND SITUATION not in (SELECT SITUATION FROM SAFE_COMPLETION where TIMESTAMP > "
                + ca.getTimeInMillis() + ")", null);
        c.moveToFirst();
        ContentValues v;
        for (int x = 0; x < c.getCount(); x++) {
            v = new ContentValues();
            v.put("COMPLETED_FLAG", 0);
            db.update("SAFE", v, "SITUATION = \"" + c.getString(c.getColumnIndex("SITUATION")) + "\"", null);
            c.moveToNext();
        }
        c.close();
        c = db.rawQuery("SELECT * from SAFE where COMPLETED_FLAG = 0", null); // works
        Random num = new Random(System.currentTimeMillis());
        int position = (int) (c.getCount() * num.nextDouble());
        c.moveToPosition(position);
        return new Situation(c);
    }

    private void goToHomepage(boolean force) {
        if (!force) {
            FragmentManager fm = getFragmentManager();
            DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this);
            dialog.show(fm, "frag");
        } else {
            finish();
        }
    }

    private void sw_startRecording() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);
        String fileName = "audio_" + timeStampFormat.format(new Date()) + ".3gp";
        outputFile = mediaStorageDir.getAbsolutePath() + "/" + fileName;
        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
        }
    }

    private void sw_stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
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

    private class Situation {
        String description, rightAnswer, wrongAnswer1, wrongAnswer2;

        public Situation(Cursor c) {
            this.description = c.getString(c.getColumnIndex("SITUATION"));
            this.rightAnswer = c.getString(c.getColumnIndex("F1_RIGHT"));
            this.wrongAnswer1 = c.getString(c.getColumnIndex("F2_WRONG"));
            this.wrongAnswer2 = c.getString(c.getColumnIndex("F3_WRONG"));
        }
    }
}