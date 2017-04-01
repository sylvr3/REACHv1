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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Arrays;
import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener{
    private ImageView imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap;
    private int neutral,count, index, totalAttempts;
    private CountDownTimer countDownTimer, blankScreenTimer, responseTimer, transitionTimer;
    private ImageView plusImage;
    private ImageView plusBtwImageView;
    private ViewFlipper viewFlipper;
    private Button leftButton, rightButton, restartButton;
    private int[] imageIndArray;
    private EditText resultText;

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
        totalAttempts = 0;
        imgTop= (ImageView)findViewById(R.id.imgTop);
        imgBottom = (ImageView)findViewById(R.id.imgBottom);
        Typeface font = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        leftButton = (Button)findViewById(R.id.leftButton);
        restartButton = (Button)findViewById(R.id.restartButton);
        leftButton.setTypeface(font);
        rightButton = (Button)findViewById(R.id.rightButton);
        rightButton.setTypeface(font);
        plusImage = (ImageView)findViewById(R.id.plus);
        plusBtwImageView = (ImageView) findViewById(R.id.plusBtw);
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        resultText = (EditText)findViewById(R.id.resultText);
        //viewFlipper.showNext();
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        restartButton.setOnClickListener(this);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        responseTimer = new CountDownTimer(200,100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                transitionScreen();
            }
        };
        transitionTimer = new CountDownTimer(1800,900) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                showBlankScreen();
            }
        };
        blankScreenTimer = new CountDownTimer(500,250) {
            @Override
            public void onTick(long l) {
                screenWithBlankImages();
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

    public void transitionScreen() {
        viewFlipper.setDisplayedChild(1);
        responseTimer.cancel();
        transitionTimer.start();
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);
    }

    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        blankScreenTimer.cancel();
        responseTimer.cancel();
        transitionTimer.cancel();
    }
    public void screenWithBlankImages() {
        //System.out.println("+ sign is removed");
        //plusBtwImageView.setVisibility(View.INVISIBLE);
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
        countDownTimer.cancel();
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
        viewFlipper.setDisplayedChild(1);
        responseTimer.start();
    }

    //Wipe images and show the + sign
    public void showBlankScreen() {
        restartButton.setVisibility(View.INVISIBLE);
        countDownTimer.cancel();
        responseTimer.cancel();
        transitionTimer.cancel();
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        //viewFlipper.showNext();
        viewFlipper.setDisplayedChild(0);
        plusImage.setVisibility(View.VISIBLE);
        blankScreen();

    }

    //Load images
    public void fetchImages() {
        blankScreenTimer.cancel();
        int bitMapInd = random.nextInt(2);
        int rndInt = random.nextInt(neutralImgs.length()-index) + index;
        int neutralId = neutralImgs.getResourceId(imageIndArray[rndInt],0);
        int threatId = threatImgs.getResourceId(imageIndArray[rndInt],0);
        swapIndex(index,rndInt);
        index = (index + 1)%neutralImgs.length();
        System.out.println(Arrays.toString(imageIndArray));
        bmap[0] = (bitMapInd == 0)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        bmap[1] = (bitMapInd == 1)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        neutral = (bitMapInd == 0)?0:1;
        //viewFlipper.showPrevious();
        viewFlipper.setDisplayedChild(1);
        imgTop.setVisibility(View.VISIBLE);
        plusBtwImageView.setVisibility(View.VISIBLE);
        imgBottom.setVisibility(View.VISIBLE);
        rightButton.setEnabled(true);
        leftButton.setEnabled(true);
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        plusImage.setVisibility(View.INVISIBLE);
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
        totalAttempts++;
        startTimer();
    }

    public void setUpAgain() {
        countDownTimer.cancel();
        blankScreenTimer.cancel();
        responseTimer.cancel();
        transitionTimer.cancel();
        //viewFlipper.showNext();
        restartButton.setVisibility(View.VISIBLE);

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
            if(neutral == 0 ) count++;
            //showBlankScreen();
        }
        if(v.getId() == rightButton.getId()) {
            if(neutral == 1) count++;
            //showBlankScreen();
        }
        if(v.getId() == restartButton.getId()) {
            //viewFlipper.showNext();
            //viewFlipper.showPrevious();
            totalAttempts = 0;
            showBlankScreen();

        }
        if(count == 5) {
            count = 0;
            viewFlipper.setDisplayedChild(2);
            String result = "Score: " + totalAttempts;
            resultText.setText(result);
            setUpAgain();
        }
        System.out.println(count);
    }
}
