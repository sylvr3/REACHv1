package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener{
    private ImageView imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap;
    private int neutral,count, index;
    private CountDownTimer countDownTimer, blankScreenTimer;
    private ImageView plusImage;
    private ImageView plusBtwImageView;
    private ViewFlipper viewFlipper;
    private Button leftButton, rightButton;
    private int[] imageIndArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_biased_toolbox);
        random = new Random();
        bmap = new Bitmap[2];
        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        threatImgs = getResources().obtainTypedArray(R.array.threat_images);
        imageIndArray = new int[neutralImgs.length()];
        for(int i = 0; i < imageIndArray.length; i++) imageIndArray[i] = i;
        index = 0;
        neutral = 0;
        imgTop= (ImageView)findViewById(R.id.imgTop);
        imgBottom = (ImageView)findViewById(R.id.imgBottom);
        Typeface font = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        leftButton = (Button)findViewById(R.id.leftButton);
        leftButton.setTypeface(font);

        rightButton = (Button)findViewById(R.id.rightButton);
        rightButton.setTypeface(font);
        plusImage = (ImageView)findViewById(R.id.plus);
        plusBtwImageView = (ImageView) findViewById(R.id.plusBtw);
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.showNext();
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        blankScreenTimer = new CountDownTimer(500,250) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                fetchImages();
            }
        };
        countDownTimer = new CountDownTimer(500,250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                //showBlankScreen();
                showProbes();
            }
        };
        //blankScreen();
        showBlankScreen();
    }

    // Initiate timer for first fixation with + sign, at the end it start the fetching images
    public void blankScreen() {
        blankScreenTimer.start();
    }

    // Initiate timer for second fixation with images, at the end it generate probes and wait for user response
    public void startTimer() {
        countDownTimer.start();
    }

    // Wipe images and generate probes
    public void showProbes() {
        int leftProbeInd = R.drawable.left;
        int rightProbeInd = R.drawable.right;
        imgTop.setVisibility(View.INVISIBLE);
        plusBtwImageView.setVisibility(View.GONE);
        imgBottom.setVisibility(View.INVISIBLE);
        bmap[0] = BitmapFactory.decodeResource(getResources(),leftProbeInd);
        bmap[1] = BitmapFactory.decodeResource(getResources(),rightProbeInd);
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
        if(neutral == 0) imgTop.setVisibility(View.VISIBLE);
        else imgBottom.setVisibility(View.VISIBLE);
        //viewFlipper.showPrevious();
        countDownTimer.cancel();
    }

    //Wipe images and show the + sign
    public void showBlankScreen() {
        //countDownTimer.cancel();
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        viewFlipper.showNext();
        plusImage.setVisibility(View.VISIBLE);
        blankScreen();
    }

    //Load images
    public void fetchImages() {
        int bitMapInd = random.nextInt(2);
        int rndInt = random.nextInt(neutralImgs.length()-index) + index;
        int neutralId = neutralImgs.getResourceId(rndInt,0);
        int threatId = threatImgs.getResourceId(rndInt,0);
        swapIndex(index,rndInt);
        index = (index + 1)%neutralImgs.length();
        bmap[0] = (bitMapInd == 0)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        bmap[1] = (bitMapInd == 1)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        neutral = (bitMapInd == 0)?0:1;
        viewFlipper.showPrevious();
        imgTop.setVisibility(View.VISIBLE);
        plusBtwImageView.setVisibility(View.VISIBLE);
        imgBottom.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        plusImage.setVisibility(View.INVISIBLE);
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
        blankScreenTimer.cancel();
        startTimer();
    }

    //Swap the indices to populate the images which is not generated in previous hits.
    public void swapIndex(int i, int j) {
        int temp = imageIndArray[i];
        imageIndArray[i] = imageIndArray[j];
        imageIndArray[j] = temp;
    }

    @Override
    public void onClick(View v) {
        /*if(v.getId() == imgTop.getId()) {
            if(neutral == 0) count++;
            showBlankScreen();
        }
        if(v.getId() == imgBottom.getId()) {
            if(neutral == 1) count++;
            showBlankScreen();
        }*/
        if(v.getId() == leftButton.getId()) {
            if(neutral == 0 )showBlankScreen();
        }
        if(v.getId() == rightButton.getId()) {
            if(neutral == 1) showBlankScreen();
        }
        System.out.println(count);
    }
}
