package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.w3c.dom.Text;

import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener {
    private ImageView imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap;
    private int neutral, count, index, numOfTrials;
    private CountDownTimer countDownTimer, blankScreenTimer, loopTrialTimer, showScoreTimer;
    private ImageView plusImage;
    private ImageView plusBtwImageView;
    private ViewFlipper viewFlipper;
    private Button leftButton, rightButton, tutorialButton, trialButton;
    private int[] imageIndArray;
    private TextView scoreText, progressText, playAgainText, speedText;
    private long startTime, stopTime;
    private static final int NUM_OF_TRIALS = 160;
    private static final int NUM_OF_TUTORIAL_TRIALS = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_attention_biased_toolbox);
        random = new Random();
        bmap = new Bitmap[2];
        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        threatImgs = getResources().obtainTypedArray(R.array.threat_images);
        imageIndArray = new int[neutralImgs.length()];
        for (int i = 0; i < imageIndArray.length; i++) imageIndArray[i] = i;
        index = 0;
        neutral = 0;
        imgTop = (ImageView) findViewById(R.id.imgTop);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);
        Typeface font = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setTypeface(font);
        rightButton = (Button) findViewById(R.id.rightButton);
        rightButton.setTypeface(font);
        Typeface textType = Typeface.createFromAsset(getAssets(), "Average-Regular.ttf");
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        tutorialButton.setTypeface(textType);
        trialButton = (Button) findViewById(R.id.trialButton);
        trialButton.setTypeface(textType);
        plusImage = (ImageView) findViewById(R.id.plus);
        plusBtwImageView = (ImageView) findViewById(R.id.plusBtw);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.showNext();
        scoreText = (TextView) findViewById(R.id.scoreText);
        progressText = (TextView) findViewById(R.id.progressText);
     //   playAgainText = (TextView) findViewById(R.id.playAgainText);
     //   speedText = (TextView) findViewById(R.id.speedText);
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        tutorialButton.setOnClickListener(this);
        trialButton.setOnClickListener(this);
        //trialButton.setClickable(false);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        blankScreenTimer = new CountDownTimer(500, 200) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                fetchImages();
            }
        };
        countDownTimer = new CountDownTimer(200, 200) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                //showBlankScreen();
                showProbes();
            }
        };

        loopTrialTimer = new CountDownTimer(1800, 200) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                showBlankScreen();
            }
        };

        showScoreTimer = new CountDownTimer(3000, 200) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (count < NUM_OF_TRIALS)
                    showPlayAgainScreen();
                if (count == NUM_OF_TRIALS)
                    showScoreTimer.cancel();

            }
        };
        // blankScreen();
        //showBlankScreen();
        showMainScreen();
    }

    // Initiate timer for first fixation with + sign, at the end it start the fetching images
    public void blankScreen() {
        blankScreenTimer.start();
        startTime = System.currentTimeMillis();
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
        bmap[0] = BitmapFactory.decodeResource(getResources(), leftProbeInd);
        bmap[1] = BitmapFactory.decodeResource(getResources(), rightProbeInd);
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
        if (neutral == 0) imgTop.setVisibility(View.VISIBLE);
        else imgBottom.setVisibility(View.VISIBLE);
        //viewFlipper.showPrevious();
        //  countDownTimer.cancel();
    }

    // Main screen that allows user to select tutorial or trial
    public void showMainScreen() {
        tutorialButton.setVisibility(View.VISIBLE);
        trialButton.setVisibility(View.VISIBLE);
        plusBtwImageView.setVisibility(View.GONE);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
      //  playAgainText.setVisibility(View.INVISIBLE);
        plusImage.setVisibility(View.INVISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
      //  playAgainText.setVisibility(View.INVISIBLE);
      //  speedText.setVisibility(View.INVISIBLE);
    }

    //Wipe images and show the + sign
    public void showBlankScreen() {
        //countDownTimer.cancel();
        tutorialButton.setVisibility(View.INVISIBLE);
        trialButton.setVisibility(View.INVISIBLE);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        viewFlipper.showNext();
        //playAgainText.setVisibility(View.INVISIBLE);
        plusImage.setVisibility(View.VISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
     //   playAgainText.setVisibility(View.INVISIBLE);
     //   speedText.setVisibility(View.INVISIBLE);
        blankScreen();
    }

    // Display student's score and speed
    public void showScoreScreen() {
        tutorialButton.setVisibility(View.INVISIBLE);
        trialButton.setVisibility(View.INVISIBLE);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        // viewFlipper.showNext();
       // playAgainText.setVisibility(View.INVISIBLE);
        plusImage.setVisibility(View.GONE);
        plusBtwImageView.setVisibility(View.GONE);
       // scoreText.setVisibility(View.VISIBLE);
       // scoreText.setText("Score: " + count + " out of " + numOfTrials);
        System.out.println("Score: " + count + " out of " + numOfTrials);
        long millis = stopTime - startTime;
        int seconds = (int) (millis / 1000 % 60);
        int minutes = (int) ((millis / (1000 * 60)) % 60);
      //  speedText.setVisibility(View.VISIBLE);
     //   speedText.setText("Speed: " + millis + " ms");
        System.out.println("Speed: " + millis + " ms"); // test this!!
        showScoreTimer.start();

    }

    public void showPlayAgainScreen() {

        tutorialButton.setVisibility(View.INVISIBLE);
        trialButton.setVisibility(View.INVISIBLE);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        //viewFlipper.showNext();
        plusImage.setVisibility(View.GONE);
        plusBtwImageView.setVisibility(View.GONE);
      //  playAgainText.setVisibility(View.VISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
       // speedText.setVisibility(View.INVISIBLE);
        loopTrialTimer.start();

        //scoreText.setText("Score: " + count + " out of " + numOfTrials);
        //  System.out.println("Score: " + count + " out of " + numOfTrials);

    }

    //Load images
    public void fetchImages() {
        progressText.setVisibility(View.VISIBLE);
        progressText.setText(numOfTrials + " out of " + NUM_OF_TRIALS);
        if (numOfTrials < NUM_OF_TRIALS) {
            int bitMapInd = random.nextInt(2);
            int rndInt = random.nextInt(neutralImgs.length() - index) + index;
            int neutralId = neutralImgs.getResourceId(rndInt, 0);
            int threatId = threatImgs.getResourceId(rndInt, 0);
            swapIndex(index, rndInt);
            index = (index + 1) % neutralImgs.length();
            bmap[0] = (bitMapInd == 0) ? BitmapFactory.decodeResource(getResources(), neutralId) : BitmapFactory.decodeResource(getResources(), threatId);
            bmap[1] = (bitMapInd == 1) ? BitmapFactory.decodeResource(getResources(), neutralId) : BitmapFactory.decodeResource(getResources(), threatId);
            neutral = (bitMapInd == 0) ? 0 : 1;
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
        if (numOfTrials == NUM_OF_TRIALS) { // trial is over and score is presented
            showScoreScreen();
            stopTime = System.currentTimeMillis();
        }
    }

    //Swap the indices to populate the images which is not generated in previous hits.
    public void swapIndex(int i, int j) {
        int temp = imageIndArray[i];
        imageIndArray[i] = imageIndArray[j];
        imageIndArray[j] = temp;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == tutorialButton.getId()) {
            tutorialButton.setVisibility(View.INVISIBLE);
            trialButton.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == trialButton.getId()) {
            tutorialButton.setVisibility(View.INVISIBLE);
            trialButton.setVisibility(View.INVISIBLE);
            blankScreen();
            showBlankScreen();
        }
        if (v.getId() == leftButton.getId()) {
            if (neutral == 0) {
                count++;
                MediaPlayer mp = MediaPlayer.create(this, R.raw.ding);
                mp.start();
            }
            numOfTrials++;
            showBlankScreen();
        }
        if (v.getId() == rightButton.getId()) {
            if (neutral == 1) {
                count++;
                MediaPlayer mp = MediaPlayer.create(this, R.raw.ding);
                mp.start();
            }
            numOfTrials++;
            showBlankScreen();

        }
        System.out.println(count);
        System.out.println(numOfTrials);

    }
}
