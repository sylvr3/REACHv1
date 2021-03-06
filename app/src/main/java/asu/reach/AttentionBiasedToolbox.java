package asu.reach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.DecimalFormat;
import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener {
    private ImageView imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, sadImgs, disguiseImgs, angryImgs, traingActorNeutral1, traingActorEmotional1, traingActorNeutral2, traingActorEmotional2;
    private Bitmap[] bmap;
    private int neutral, count, index, totalAttempts, indexSad, indexDisguise, indexAngry, indexNeutral, divisionId, trainingBlockCount;
    private CountDownTimer countDownTimer, blankScreenTimer, responseTimer, transitionTimer;
    private ImageView plusImage;
    private ImageView plusBtwImageView;
    private ViewFlipper viewFlipper;
    private Button leftButton, rightButton, restartButton, goButton, nextButton, previousButton, goToTrialButton;
    private TextView resultText, speedText;
    private long timeDiff, startTime, blockStart;
    private double avgTime;
    private int[] blockArray, sadArray, neutralArray, disguiseArray, angryArray, neutralSadArray, neutralDisguiseArray, neutralAngryArray;
    private static int blockArraySize, imageArraySize = 15;
    private boolean status, wrongAnswer = false;
    private SharedPreferences sharedPref;
    private final String SHARED_PREF_KEY = "ABMT";
    private final String ABMT_CORRECT_COUNT = "ABMT_CORRECT_COUNT";
    private ProgressBar progressBar;
    private TextView instructionsText;
    private int blankScreenTimerValue, countDownTimerValue, responseTimerValue, transitionTimeValue;
    private int[] trainingActor1Array, trainingActor2Array;
    private int actor1Index, actor2Index;
    private Typeface texttype;
    ABMTStartScreen abmtss = new ABMTStartScreen();
    ReachExceptionLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            logger = new ReachExceptionLogger(this);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_attention_biased_toolbox);
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.getExtras() != null) {
                    status = getIntent().getStringExtra("status").equals("trial");
                }
            }
            startTrialOrTutorial();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void startTrialOrTutorial() {
        try {
            random = new Random();
            bmap = new Bitmap[2];
            blockArraySize = status ? 240 : 40;
            blockArray = new int[blockArraySize];
            goToTrialButton = (Button) findViewById(R.id.goToTrial);
            if (status) {
                sadArray = new int[imageArraySize];
                neutralSadArray = new int[imageArraySize];
                angryArray = new int[imageArraySize];
                neutralAngryArray = new int[imageArraySize];
                disguiseArray = new int[imageArraySize];
                neutralDisguiseArray = new int[imageArraySize];
                neutralArray = new int[imageArraySize];
                goToTrialButton.setVisibility(View.INVISIBLE);
            } else {
                trainingActor1Array = new int[3];
                trainingActor2Array = new int[8];
                goToTrialButton.setVisibility(View.VISIBLE);
            }
            indexAngry = 0;
            indexDisguise = 0;
            indexNeutral = 0;
            indexNeutral = 0;
            actor1Index = 0;
            actor2Index = 0;
            trainingBlockCount = 0;
            index = 0;
            neutral = 0;
            totalAttempts = 0;
            System.out.println("status here " + status);
            //default blank screen timer value -> 500 : 1000
            blankScreenTimerValue = status ? 500 : 1000;
            //default count down timer value -> 500 : 1000
            countDownTimerValue = status ? 500 : 1000;
            //default response timer value -> 200 : 4000
            responseTimerValue = status ? getResponseTime() : 4000;
            //default transition screen timer value -> 1800 : 6000
            transitionTimeValue = status ? getTransitionTime() : 6000;
            progressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
            progressBar.setVisibility(View.GONE);
            initSharedPref();
            ArrayCounterInitialization();
            UIInitialization();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void UIInitialization() {
        try {
            if (status) {
                neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
                sadImgs = getResources().obtainTypedArray(R.array.sad_images);
                disguiseImgs = getResources().obtainTypedArray(R.array.disguise_imgaes);
                angryImgs = getResources().obtainTypedArray(R.array.angry_images);
            } else {
                traingActorNeutral1 = getResources().obtainTypedArray(R.array.training_neutral_actor_1);
                traingActorEmotional1 = getResources().obtainTypedArray(R.array.training_emotional_actor_1);
                traingActorNeutral2 = getResources().obtainTypedArray(R.array.training_neutral_actor_2);
                traingActorEmotional2 = getResources().obtainTypedArray(R.array.training_emotional_actor_2);
            }
            imgTop = (ImageView) findViewById(R.id.imgTop);
            imgBottom = (ImageView) findViewById(R.id.imgBottom);
            Typeface font = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
            leftButton = (Button) findViewById(R.id.leftButton);
            restartButton = (Button) findViewById(R.id.restartButton);

            goButton = (Button) findViewById(R.id.goButton);
            nextButton = (Button) findViewById(R.id.nextButton);
            previousButton = (Button) findViewById(R.id.previousButton);
            leftButton.setTypeface(font);
            rightButton = (Button) findViewById(R.id.rightButton);
            rightButton.setTypeface(font);
            plusImage = (ImageView) findViewById(R.id.plus);
            plusBtwImageView = (ImageView) findViewById(R.id.plusBtw);
            viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

            texttype = Typeface.createFromAsset(getAssets(), "Average-Regular.ttf");
            resultText = (TextView) findViewById(R.id.resultText);
            speedText = (TextView) findViewById(R.id.speedText);
            speedText.setTypeface(texttype);

            instructionsText = (TextView) findViewById(R.id.instructionsText);
            imgTop.setOnClickListener(this);
            imgBottom.setOnClickListener(this);
            leftButton.setOnClickListener(this);
            rightButton.setOnClickListener(this);
            restartButton.setOnClickListener(this);
            goToTrialButton.setOnClickListener(this);
            goButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
            previousButton.setOnClickListener(this);
            plusImage.setVisibility(View.INVISIBLE);
            plusBtwImageView.setVisibility(View.INVISIBLE);
            imgTop.setVisibility(View.INVISIBLE);
            imgBottom.setVisibility(View.INVISIBLE);
            restartButton.setVisibility(View.INVISIBLE);
            if (!status) {
                if (!abmtss.disableTrial) {
                    instructionsText.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    previousButton.setVisibility(View.INVISIBLE);
                    goButton.setVisibility(View.INVISIBLE);
                } else {
                    instructionsText.setVisibility(View.INVISIBLE);
                    nextButton.setVisibility(View.INVISIBLE);
                    previousButton.setVisibility(View.INVISIBLE);
                    goButton.setVisibility(View.INVISIBLE);
                }
                instructionsText.setText("If you see the arrow point right, tap the right arrow button \n\n > \n\n If you see the arrow point left, tap the left arrow button \n\n < \n");
            } else blockStart();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void ArrayCounterInitialization() {
        try {
            for (int i = 0; i < blockArraySize; i++) blockArray[i] = i;
            if (status) {
                for (int i = 0; i < imageArraySize; i++) {
                    sadArray[i] = i;
                    neutralSadArray[i] = i;
                    angryArray[i] = i;
                    neutralAngryArray[i] = i;
                    neutralArray[i] = i;
                    disguiseArray[i] = i;
                    neutralDisguiseArray[i] = i;
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    trainingActor2Array[i] = i;
                    if (i < 3) trainingActor1Array[i] = i;
                }
            }

            responseTimer = new CountDownTimer(responseTimerValue, responseTimerValue / 2) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    transitionScreen();
                }
            };
            transitionTimer = new CountDownTimer(transitionTimeValue, transitionTimeValue / 2) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    showBlankScreen();
                }
            };
            blankScreenTimer = new CountDownTimer(blankScreenTimerValue, blankScreenTimerValue / 2) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    if (status) fetchImages();
                    else fetchImagesTraining();
                }
            };
            countDownTimer = new CountDownTimer(countDownTimerValue, countDownTimerValue / 2) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    showProbes();
                }
            };
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void shuffleBlockArray() {
        try {
            for (int i = 0; i < blockArraySize; i++) {
                int ind = random.nextInt(blockArraySize - i) + i;
                swapIndex(blockArray, ind, i);
            }
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void measureSpeed() {
        timeDiff = (timeDiff < responseTimerValue) ? timeDiff : responseTimerValue;
        avgTime += timeDiff;
    }

    public void transitionScreen() {
        try {
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
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        blankScreenTimer.cancel();
        responseTimer.cancel();
        transitionTimer.cancel();

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
        try {
            countDownTimer.cancel();
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
            viewFlipper.setDisplayedChild(1);
            startTime = System.currentTimeMillis();
            responseTimer.start();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void blockStart() {
        try {
            totalAttempts = 0;
            actor1Index = 0;
            actor2Index = 0;
            if (!status) trainingBlockCount++;
            shuffleBlockArray();
            blockStart = System.currentTimeMillis();
            showBlankScreen();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    //Wipe images and show the + sign
    public void showBlankScreen() {
        try {
            restartButton.setVisibility(View.INVISIBLE);
            countDownTimer.cancel();
            responseTimer.cancel();
            transitionTimer.cancel();
            imgTop.setVisibility(View.INVISIBLE);
            imgBottom.setVisibility(View.INVISIBLE);
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
            goButton.setVisibility(View.INVISIBLE);
            instructionsText.setVisibility(View.INVISIBLE);

//        viewFlipper.setDisplayedChild(0);
//        plusImage.setVisibility(View.VISIBLE);
            plusBtwImageView.setVisibility(View.VISIBLE);
            if (totalAttempts == blockArraySize) {
                viewFlipper.setDisplayedChild(2);
                avgTime = avgTime / totalAttempts;
                avgTime = avgTime / 1000;
                String speed1 = ": " + new DecimalFormat("###.##").format(avgTime) + "seconds";
                String result1 = ": " + count + " / " + totalAttempts;
                String speed = "Please start again";
                String result = "Attempts Over" + "\n" + speed1 + "\n" + result1;
                count = 0;
                resultText.setText(result);
                speedText.setText(speed);
                setUpAgain();
            } else if (System.currentTimeMillis() - blockStart > 720000) {
                count = 0;
                viewFlipper.setDisplayedChild(2);
                String speed = "Please start again";
                String result = "Time Over";
                resultText.setText(result);
                speedText.setText(speed);
                setUpAgain();
            } else blankScreen();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    //Load images
    public void fetchImages() {
        try {
            blankScreenTimer.cancel();
            wrongAnswer = false;
            int rndIndex = random.nextInt(blockArraySize - index) + index;
            neutral = (blockArray[rndIndex] & 1) == 1 ? 1 : 0;
            divisionId = blockArray[rndIndex] / 60;
            int topImg = 0, bottomImg = 0;
            swapIndex(blockArray, rndIndex, index);
            index = (index + 1) % blockArraySize;
            if (divisionId == 0) {
                int rndNeutralInd = random.nextInt(imageArraySize - indexNeutral) + indexNeutral;
                topImg = neutralImgs.getResourceId(neutralArray[rndNeutralInd], 0);
                bottomImg = neutralImgs.getResourceId(neutralArray[rndNeutralInd], 0);
                swapIndex(neutralArray, rndNeutralInd, indexNeutral);
                indexNeutral = (indexNeutral + 1) % imageArraySize;
            } else if (divisionId == 1) {
                int rndSadInd = random.nextInt(imageArraySize - indexSad) + indexSad;
                topImg = neutralImgs.getResourceId(neutralSadArray[rndSadInd], 0);
                bottomImg = sadImgs.getResourceId(sadArray[rndSadInd], 0);
                swapIndex(sadArray, rndSadInd, indexSad);
                swapIndex(neutralSadArray, rndSadInd, indexSad);
                indexSad = (indexSad + 1) % imageArraySize;
            } else if (divisionId == 2) {
                int rndDisguiseInd = random.nextInt(imageArraySize - indexDisguise) + indexDisguise;
                topImg = neutralImgs.getResourceId(neutralDisguiseArray[rndDisguiseInd], 0);
                bottomImg = disguiseImgs.getResourceId(disguiseArray[rndDisguiseInd], 0);
                swapIndex(disguiseArray, rndDisguiseInd, indexDisguise);
                swapIndex(neutralDisguiseArray, rndDisguiseInd, indexDisguise);
                indexDisguise = (indexDisguise + 1) % imageArraySize;
            } else if (divisionId == 3) {
                int rndAngryInd = random.nextInt(imageArraySize - indexAngry) + indexAngry;
                topImg = neutralImgs.getResourceId(neutralAngryArray[rndAngryInd], 0);
                bottomImg = angryImgs.getResourceId(angryArray[rndAngryInd], 0);
                swapIndex(angryArray, rndAngryInd, indexAngry);
                swapIndex(neutralAngryArray, rndAngryInd, indexAngry);
                indexAngry = (indexAngry + 1) % imageArraySize;
            }
            bmap[0] = (neutral == 0) ? BitmapFactory.decodeResource(getResources(), topImg) : BitmapFactory.decodeResource(getResources(), bottomImg);
            bmap[1] = (neutral == 1) ? BitmapFactory.decodeResource(getResources(), topImg) : BitmapFactory.decodeResource(getResources(), bottomImg);
            viewFlipper.setDisplayedChild(1);
            imgTop.setVisibility(View.VISIBLE);
            plusBtwImageView.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.VISIBLE);
            rightButton.setEnabled(true);
            leftButton.setEnabled(true);
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            plusImage.setVisibility(View.INVISIBLE);
            goButton.setVisibility(View.INVISIBLE);
            instructionsText.setVisibility(View.INVISIBLE);
            imgTop.setImageBitmap(bmap[0]);
            imgBottom.setImageBitmap(bmap[1]);
            totalAttempts++;
            startTimer();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void fetchImagesTraining() {
        try {
            blankScreenTimer.cancel();
            wrongAnswer = false;
            int rndIndex = random.nextInt(blockArraySize - index) + index;
            neutral = (blockArray[rndIndex] & 1) == 1 ? 1 : 0;
            int topImg = 0, bottomImg = 0;
            swapIndex(blockArray, rndIndex, index);
            if ((index & 1) == 1) {
                int rndActor1Ind = random.nextInt(3 - actor1Index) + actor1Index;
                topImg = traingActorNeutral1.getResourceId(trainingActor1Array[rndActor1Ind], 0);
                bottomImg = traingActorEmotional1.getResourceId(trainingActor1Array[rndActor1Ind], 0);
                swapIndex(trainingActor1Array, rndActor1Ind, actor1Index);
                actor1Index = (actor1Index + 1) % 3;
            } else {
                int rndActor2Ind = random.nextInt(8 - actor2Index) + actor2Index;
                topImg = traingActorNeutral2.getResourceId(trainingActor2Array[rndActor2Ind], 0);
                bottomImg = traingActorEmotional2.getResourceId(trainingActor2Array[rndActor2Ind], 0);
                swapIndex(trainingActor2Array, rndActor2Ind, actor2Index);
                actor2Index = (actor2Index + 1) % 8;
            }
            index = (index + 1) % blockArraySize;
            bmap[0] = (neutral == 0) ? BitmapFactory.decodeResource(getResources(), topImg) : BitmapFactory.decodeResource(getResources(), bottomImg);
            bmap[1] = (neutral == 1) ? BitmapFactory.decodeResource(getResources(), topImg) : BitmapFactory.decodeResource(getResources(), bottomImg);
            viewFlipper.setDisplayedChild(1);
            imgTop.setVisibility(View.VISIBLE);
            plusBtwImageView.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.VISIBLE);
            rightButton.setEnabled(true);
            leftButton.setEnabled(true);
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            plusImage.setVisibility(View.INVISIBLE);
            goButton.setVisibility(View.INVISIBLE);
            instructionsText.setVisibility(View.INVISIBLE);
            imgTop.setImageBitmap(bmap[0]);
            imgBottom.setImageBitmap(bmap[1]);
            totalAttempts++;
            startTimer();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void setUpAgain() {
        try {
            countDownTimer.cancel();
            blankScreenTimer.cancel();
            responseTimer.cancel();
            transitionTimer.cancel();
            restartButton.setVisibility(View.VISIBLE);
            goButton.setVisibility(View.INVISIBLE);
            instructionsText.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    //Swap the indices to populate the images which is not generated in previous hits.
    public void swapIndex(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void trueResponse() {
        try {
            count++;
            MediaPlayer mediaplayer;
            mediaplayer = MediaPlayer.create(this, R.raw.new_ding);
            mediaplayer.start();
            if (status) this.setCorrectCount();
            measureSpeed();
            if ((status && count == 128) || (!status && totalAttempts == 40)) {
                viewFlipper.setDisplayedChild(2);
                avgTime = avgTime / totalAttempts;
                avgTime = avgTime / 1000;
                String speed = ": " + new DecimalFormat("###.##").format(avgTime) + " seconds";
                String result = ": " + count + " / " + totalAttempts;
                resultText.setText(result);
                speedText.setText(speed);
                count = 0;
                setUpAgain();
                if (status && count == 128) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(getCorrectCount() + count / 7500);
                }
            } else {
                showBlankScreen();
                progressBar.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == leftButton.getId()) {
                timeDiff = System.currentTimeMillis() - startTime;
                if (neutral == 0 && !wrongAnswer) {
                    if (!status || divisionId != 0) trueResponse();
                } else {
                    wrongAnswer = true;
                }
                if (status && divisionId == 0) {
                    showBlankScreen();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            if (v.getId() == rightButton.getId()) {
                timeDiff = System.currentTimeMillis() - startTime;
                if (neutral == 1 && !wrongAnswer) {
                    if (!status || divisionId != 0) trueResponse();
                } else {
                    wrongAnswer = true;
                }
                if (status && divisionId == 0) {
                    showBlankScreen();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            if (v.getId() == restartButton.getId()) {
                makeTrialAvailable();
                if (!status) {
                    Intent intent = new Intent(getBaseContext(), ABMTStartScreen.class);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else blockStart();
            }
            if (v.getId() == goToTrialButton.getId()) {
            /*Intent intent = new Intent(getBaseContext(),ABMTStartScreen.class);
            intent.putExtra("Enable","Enable");
            setResult(Activity.RESULT_OK, intent);
//            startActivity(intent);
            finish();*/
                this.status = true;
                this.onStop();
                makeTrialAvailable();
                startTrialOrTutorial();
                Intent intent = new Intent(getBaseContext(), ABMTStartScreen.class);
                setResult(Activity.RESULT_OK, intent);
                finish();
                //this.onCreate(new Bundle());
            }

            if (v.getId() == nextButton.getId()) {
                nextButton.setVisibility(View.INVISIBLE);
                previousButton.setVisibility(View.VISIBLE);
                instructionsText.setText("Ready?\n\nTap GO!");
                goButton.setVisibility(View.VISIBLE);
            }
            if (v.getId() == previousButton.getId()) {
                nextButton.setVisibility(View.VISIBLE);
                previousButton.setVisibility(View.INVISIBLE);
                instructionsText.setText("If you see the arrow point right, tap the right arrow button \n\n > \n\n If you see the arrow point left, tap the left arrow button \n\n < \n");
                goButton.setVisibility(View.INVISIBLE);
            }
            if (v.getId() == goButton.getId()) {
                goButton.setVisibility(View.INVISIBLE);
                instructionsText.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.INVISIBLE);
                previousButton.setVisibility(View.INVISIBLE);
                blockStart();
            }
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void initSharedPref() {
        this.sharedPref = getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, MODE_PRIVATE);
    }

    public int getCorrectCount() {
        int correctCount = this.sharedPref.getInt(ABMT_CORRECT_COUNT, 0);
        return correctCount;
    }

    public void setCorrectCount() {
        try {
            int correctCount = this.getCorrectCount();
            SharedPreferences.Editor edit = this.sharedPref.edit();
            if (correctCount >= 7500) {
                edit.putInt(ABMT_CORRECT_COUNT, 0);
                count = 0;
            } else {
                edit.putInt(ABMT_CORRECT_COUNT, correctCount + 1);
            }
            edit.commit();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    public void makeTrialAvailable() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("abmt_shared", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("disableTrial", false);
            editor.commit();
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
    }

    private int getResponseTime() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("abmt_shared", Context.MODE_PRIVATE);
            return sharedPreferences.getInt("faces_response_time", 200);
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
        return 200;
    }

    private int getTransitionTime() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("abmt_shared", Context.MODE_PRIVATE);
            return 2000 - sharedPreferences.getInt("faces_response_time", 200);
        } catch (Exception e) {
            logger.logError(this.getClass().getCanonicalName(), e);
        }
        return 200;
    }
}