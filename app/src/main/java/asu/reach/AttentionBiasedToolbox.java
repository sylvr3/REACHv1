package asu.reach;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener{
    private ImageView imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, sadImgs, disguiseImgs, angryImgs;
    private Bitmap[] bmap;
    private int neutral,count, index, totalAttempts, indexSad, indexDisguise, indexAngry, indexNeutral, divisionId;
    private CountDownTimer countDownTimer, blankScreenTimer, responseTimer, transitionTimer;
    private ImageView plusImage;
    private ImageView plusBtwImageView;
    private ViewFlipper viewFlipper;
    private Button leftButton, rightButton, restartButton;
    private int[] imageIndArray;
    private EditText resultText, speedText;
    private long timeDiff, startTime, blockStart;
    private double avgTime;
    private int[] blockArray, sadArray, neutralArray, disguiseArray, angryArray, neutralSadArray, neutralDisguiseArray, neutralAngryArray;
    private final static int blockArraySize = 240, imageArraySize = 15;
    private MediaPlayer mediaplayer;
    private boolean status;
    private SharedPreferences sharedPref;
    private final String SHARED_PREF_KEY = "ABMT";
    private final String ABMT_CORRECT_COUNT = "ABMT_CORRECT_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_biased_toolbox);
        random = new Random();
        bmap = new Bitmap[2];
        blockArray = new int[blockArraySize];
        sadArray = new int[imageArraySize];
        neutralSadArray = new int[imageArraySize];
        angryArray = new int[imageArraySize];
        neutralAngryArray = new int[imageArraySize];
        disguiseArray = new int[imageArraySize];
        neutralDisguiseArray = new int[imageArraySize];
        neutralArray = new int[imageArraySize];
        indexAngry = 0;
        indexDisguise = 0;
        indexNeutral = 0;
        indexNeutral = 0;
        //threatImgs = getResources().obtainTypedArray(R.array.threat_images);
        //imageIndArray = new int[neutralImgs.length()];
        index = 0;
        neutral = 0;
        totalAttempts = 0;
        mediaplayer = MediaPlayer.create(this,R.raw.ding);
        status = !getIntent().getStringExtra("status").equals("trial");
        System.out.println("status here"+status);
        //blankScreen();
        //showBlankScreen();
        initSharedPref();
        UIInitialization();
        ArrayCounterInitialization();
        blockStart();
    }

    public void UIInitialization() {
        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        sadImgs = getResources().obtainTypedArray(R.array.sad_images);
        disguiseImgs = getResources().obtainTypedArray(R.array.disguise_imgaes);
        angryImgs = getResources().obtainTypedArray(R.array.angry_images);
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
        speedText = (EditText)findViewById(R.id.speedText);
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        restartButton.setOnClickListener(this);
        imgTop.setVisibility(View.INVISIBLE);
        imgBottom.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
    }
    public void ArrayCounterInitialization() {
        //for(int i = 0; i < imageIndArray.length; i++) imageIndArray[i] = i;
        for(int i = 0; i < blockArraySize; i++) blockArray[i] = i;
        for(int i = 0; i < imageArraySize; i++) {
            sadArray[i] = i;
            neutralSadArray[i] = i;
            angryArray[i] = i;
            neutralAngryArray[i] = i;
            neutralArray[i] = i;
            disguiseArray[i] = i;
            neutralDisguiseArray[i] = i;
        }

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
                //screenWithBlankImages();
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
    }
    public void shuffleBlockArray() {
        for(int i = 0; i < blockArraySize; i++) {
            int ind = random.nextInt(blockArraySize-i) + i;
            swapIndex(blockArray,ind,i);
        }
    }
    public void measureSpeed() {
        timeDiff = (timeDiff < 200)?timeDiff:200;
        avgTime += timeDiff;
    }

    public void transitionScreen() {
        measureSpeed();
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
   /* public void screenWithBlankImages() {
        //System.out.println("+ sign is removed");
        //plusBtwImageView.setVisibility(View.INVISIBLE);
    }*/
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
        startTime = System.currentTimeMillis();
        responseTimer.start();
    }
    public void blockStart() {
        shuffleBlockArray();
        blockStart = System.currentTimeMillis();
        showBlankScreen();
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
        if(totalAttempts == 240) {
            count = 0;
            viewFlipper.setDisplayedChild(2);
            String speed = "Please start again";
            String result = "Attemps Over";
            resultText.setText(result);
            totalAttempts = 0;
            speedText.setText(speed);
            setUpAgain();
        }
        else if(System.currentTimeMillis()-blockStart > 720000) {
            count = 0;
            viewFlipper.setDisplayedChild(2);
            String speed = "Please start again";
            String result = "Time Over";
            resultText.setText(result);
            totalAttempts = 0;
            speedText.setText(speed);
            setUpAgain();
        }
        else blankScreen();

    }

    //Load images
    public void fetchImages() {
        blankScreenTimer.cancel();
        //int bitMapInd = random.nextInt(2);
        int rndIndex = random.nextInt(blockArraySize-index) + index;
        neutral = (blockArray[rndIndex] & 1) == 1 ? 1 : 0;
        divisionId = blockArray[rndIndex] / 60;
        int topImg = 0, bottomImg = 0;
        swapIndex(blockArray,rndIndex,index);
        index = (index + 1)%blockArraySize;
        if(divisionId == 0) {
            int rndNeutralInd = random.nextInt(imageArraySize-indexNeutral)+indexNeutral;
            topImg = neutralImgs.getResourceId(neutralArray[rndNeutralInd],0);
            bottomImg = neutralImgs.getResourceId(neutralArray[rndNeutralInd],0);
            swapIndex(neutralArray,rndNeutralInd,indexNeutral);
            indexNeutral = (indexNeutral+1)%imageArraySize;
        }
        else if(divisionId == 1) {
            int rndSadInd = random.nextInt(imageArraySize-indexSad)+indexSad;
            topImg = neutralImgs.getResourceId(neutralSadArray[rndSadInd],0);
            bottomImg = sadImgs.getResourceId(sadArray[rndSadInd],0);
            swapIndex(sadArray,rndSadInd,indexSad);
            swapIndex(neutralSadArray,rndSadInd,indexSad);
            indexSad = (indexSad+1)%imageArraySize;
            }
        else if(divisionId == 2) {
            int rndDisguiseInd = random.nextInt(imageArraySize-indexDisguise)+indexDisguise;
            topImg = neutralImgs.getResourceId(neutralDisguiseArray[rndDisguiseInd],0);
            bottomImg = disguiseImgs.getResourceId(disguiseArray[rndDisguiseInd],0);
            swapIndex(disguiseArray,rndDisguiseInd,indexDisguise);
            swapIndex(neutralDisguiseArray,rndDisguiseInd,indexDisguise);
            indexDisguise = (indexDisguise+1)%imageArraySize;
            }
        else if(divisionId == 3) {
            int rndAngryInd = random.nextInt(imageArraySize-indexAngry)+indexAngry;
            topImg = neutralImgs.getResourceId(neutralAngryArray[rndAngryInd],0);
            bottomImg = angryImgs.getResourceId(angryArray[rndAngryInd],0);
            swapIndex(angryArray,rndAngryInd,indexAngry);
            swapIndex(neutralAngryArray,rndAngryInd,indexAngry);
            indexAngry = (indexAngry+1)%imageArraySize;
            }



        //int rndInt = random.nextInt(neutralImgs.length()-index) + index;
        //int neutralId = neutralImgs.getResourceId(imageIndArray[rndInt],0);
        //int threatId = threatImgs.getResourceId(imageIndArray[rndInt],0);
        //int threatId = neutralImgs.getResourceId(imageIndArray[rndInt],0);
        //swapIndex(imageIndArray,index,rndInt);
        //index = (index + 1)%neutralImgs.length();
        //System.out.println(Arrays.toString(imageIndArray));
        bmap[0] = (neutral == 0)? BitmapFactory.decodeResource(getResources(),topImg):BitmapFactory.decodeResource(getResources(),bottomImg);
        bmap[1] = (neutral == 1)? BitmapFactory.decodeResource(getResources(),topImg):BitmapFactory.decodeResource(getResources(),bottomImg);
        //neutral = (bitMapInd == 0)?0:1;
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
    public void swapIndex(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == leftButton.getId()) {
            timeDiff = System.currentTimeMillis() - startTime;
            if(neutral == 0 && divisionId != 0) {
                count++;
                mediaplayer.start();
                this.setCorrectCount();
                measureSpeed();
                showBlankScreen();
            }

            //avgTime += timeDiff;
            //showBlankScreen();
        }
        if(v.getId() == rightButton.getId()) {
            timeDiff = System.currentTimeMillis() - startTime;
            if(neutral == 1 && divisionId != 0) {
                mediaplayer.start();
                count++;
                this.setCorrectCount();
                measureSpeed();
                showBlankScreen();
            }

            //System.out.println(timeDiff);
            //avgTime += timeDiff;
            //System.out.println(avgTime);
            //showBlankScreen();
        }
        if(v.getId() == restartButton.getId()) {
            //viewFlipper.showNext();
            //viewFlipper.showPrevious();
            totalAttempts = 0;
            //showBlankScreen();
            blockStart();

        }


        if(count == 128) {
            count = 0;
            viewFlipper.setDisplayedChild(2);
            avgTime = avgTime / totalAttempts;
            String speed = "Speed: " + avgTime;
            String result = "Score: " + totalAttempts;
            totalAttempts = 0;
            resultText.setText(result);
            speedText.setText(speed);
            setUpAgain();
        }
        System.out.println(count);
    }

    public void initSharedPref(){
        this.sharedPref = getApplicationContext().getSharedPreferences(SHARED_PREF_KEY,MODE_PRIVATE);
    }

    public int getCorrectCount(){
        int correctCount = this.sharedPref.getInt(ABMT_CORRECT_COUNT,0);
        return correctCount;
    }

    public void setCorrectCount(){
        int correctCount = this.getCorrectCount();
        SharedPreferences.Editor edit = this.sharedPref.edit();
        if (correctCount >= 7500){
            edit.putInt(ABMT_CORRECT_COUNT,0);
            count = 0;
        } else {
            edit.putInt(ABMT_CORRECT_COUNT,correctCount + 1);
        }
    }
}
