package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener{
    private ImageButton imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap;
    private int neutral,count;
    private CountDownTimer countDownTimer, blankScreenTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_biased_toolbox);
        random = new Random();
        bmap = new Bitmap[2];
        neutral = 0;
        imgTop= (ImageButton)findViewById(R.id.imgTop);
        imgBottom = (ImageButton)findViewById(R.id.imgBottom);
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        threatImgs = getResources().obtainTypedArray(R.array.threat_images);
        blankScreen();

    }
    public void blankScreen() {
        blankScreenTimer = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long l) {


            }

            @Override
            public void onFinish() {
                fetchImages();
            }
        }.start();
    }
    public void startTimer() {
        countDownTimer = new CountDownTimer(1000,500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                showBlankScreen();
            }
        }.start();

    }
    public void showBlankScreen() {
        countDownTimer.cancel();
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        blankScreen();
    }
    public void fetchImages() {
        int bitMapInd = random.nextInt(2);
        int rndInt = random.nextInt(neutralImgs.length());
        int neutralId = neutralImgs.getResourceId(rndInt,0);
        int threatId = threatImgs.getResourceId(rndInt,0);
        bmap[0] = (bitMapInd == 0)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        bmap[1] = (bitMapInd == 1)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        neutral = (bitMapInd == 0)?0:1;
        imgTop.setVisibility(View.VISIBLE);
        imgBottom.setVisibility(View.VISIBLE);
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
        blankScreenTimer.cancel();
        startTimer();
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == imgTop.getId()) {
            if(neutral == 0) count++;
            showBlankScreen();
        }
        if(v.getId() == imgBottom.getId()) {
            if(neutral == 1) count++;
            showBlankScreen();
        }

        System.out.println(count);
    }
}
