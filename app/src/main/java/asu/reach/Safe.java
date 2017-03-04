package asu.reach;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import asu.reach.googleVision.CameraSourcePreview;
import asu.reach.googleVision.GraphicOverlay;

import static asu.reach.R.id.recordLayout;


public class Safe extends Activity implements View.OnClickListener, DialogInterface.OnClickListener{
    private SQLiteDatabase db;
    private RelativeLayout oLayout,msgLayout;
    private ImageView o1,o2,o3,title,gjView, answerImageView,sView,aView,fView,eView, thumbsUp;
    private TextView oOne, oTwo, oThree, message, answerTextView;
    private ImageButton back, again, done, next, nextFirm, doneRecording;
    private String sText;
    private VideoView gj;
    private LinearLayout complete;

    //Safe
    private RelativeLayout rLayout;
    private LinearLayout safeAcronymLayout;
    private RelativeLayout safeActivityLayout, situationSpeakLayout;
    private ImageView safePRMImageView, safeEyeContactImageView, safeBlob, situationImageView, speakYourMindImageView;
    private TextView situationTextView, speakYourMindTextView;
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
    private boolean onSpeakYourMind = true;
    private boolean onAskNicely = false;
    // Recording
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private File mediaStorageDir;

    //Safe Eye Tracking
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_HANDLE_MICROPHONE= 3;

    //Left and Right Eye probability
    private float leftEyeOpenProbability = 0;
    private float rightEyeOpenProbability = 0;
    private int probabilityCount = 0;
    //Final values to store in the DB
    private float finalLeftEyeProbablity = 0;
    private float finalRightEyeProbablity = 0;
    private int finalProbablityCount = 0;

    private final String eyeTrackingTimer = "SAFEEyeTrackingTimer";

    private Typeface t;
    private Typeface textType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_safe);
        oLayout = (RelativeLayout)findViewById(R.id.fLayout);
        msgLayout = (RelativeLayout)findViewById(R.id.msgLayout);

        safeActivityLayout = (RelativeLayout) findViewById(R.id.SafeActivityLayout);
        safeAcronymLayout = (LinearLayout)findViewById(R.id.safeLayout);
        sView = (ImageView)findViewById(R.id.sView);
        aView = (ImageView)findViewById(R.id.aView);
        fView = (ImageView)findViewById(R.id.fView);
        eView = (ImageView)findViewById(R.id.eView);

        o1 = (ImageView)findViewById(R.id.fOne);
        o2 = (ImageView)findViewById(R.id.fTwo);
        o3 = (ImageView)findViewById(R.id.fThree);
        oOne = (TextView)findViewById(R.id.fOneTxt);
        oTwo = (TextView)findViewById(R.id.fTwoTxt);
        oThree = (TextView)findViewById(R.id.fThreeTxt);
        message = (TextView)findViewById(R.id.message);
        back = (ImageButton)findViewById(R.id.safeBackButton);
        again = (ImageButton)findViewById(R.id.againBtn);
        done = (ImageButton)findViewById(R.id.safeDoneButton);
        next = (ImageButton)findViewById(R.id.safeNextButton);
        complete = (LinearLayout)findViewById(R.id.completeLayout);
        title = (ImageView)findViewById(R.id.anMessage);
        gj = (VideoView)findViewById(R.id.gjVid);
        gjView = (ImageView)findViewById(R.id.gjView);
        nextFirm = (ImageButton)findViewById(R.id.nextFirm);
        doneRecording = (ImageButton)findViewById(R.id.recordDone);
        thumbsUp = (ImageView)findViewById(R.id.thumbsUp);

        situationImageView = (ImageView) findViewById(R.id.situationImageView);
        situationTextView = (TextView) findViewById(R.id.situationTextView);
        speakYourMindImageView = (ImageView) findViewById(R.id.speakYourMindImageView);
        speakYourMindTextView = (TextView) findViewById(R.id.speakYourMindTextView);

        //SAFE

        rLayout = (RelativeLayout) findViewById(recordLayout);
        situationSpeakLayout = (RelativeLayout) findViewById(R.id.SituationSpeakLayout);
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


        t = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        textType = Typeface.createFromAsset(getAssets(), "Average-Regular.ttf");
        oOne.setTypeface(textType);
        oTwo.setTypeface(textType);
        oThree.setTypeface(textType);
        message.setTypeface(textType);
        answerTextView.setTypeface(textType);

        DBHelper helper = new DBHelper(this);
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();

        oLayout.setVisibility(View.GONE);
        msgLayout.setVisibility(View.GONE);
        situationSpeakLayout.setVisibility(View.VISIBLE);

        title.setVisibility(View.GONE);
        thumbsUp.setVisibility(View.GONE);

        //Safe Eye tracking setup
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);


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
                activateS();
                situationTextView.setTypeface(textType);
                situationTextView.setText("Situation:\n"+sText);
                speakYourMindTextView.setTypeface(textType);
                speakYourMindTextView.setText("Speak your mind");
            }else{
                message.setText("You've completed all of them!");
                next.setVisibility(View.GONE);
            }

            // Safe Setup Related Code
            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                createCameraSource();
            } else {
                requestCameraPermission();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private void startEyeTracking(){
        //TODO: start new recording activity
        Log.d("Vision","Going to start Recording Activity");
        mPreview.setVisibility(View.INVISIBLE);
        startCameraSource();
        leftEyeOpenProbability = 0;
        rightEyeOpenProbability = 0;
        probabilityCount = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String eyeTrackginTimerString = preferences.getString(eyeTrackingTimer,"5");
        Log.d("EyeTracking","Going to start eye tracking for "+eyeTrackginTimerString);
        int timer = 5;
        try{
            timer = Integer.parseInt(eyeTrackginTimerString);
        } catch (Exception e) {
            timer = 5;
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                finalProbablityCount = probabilityCount;
//                finalLeftEyeProbablity = leftEyeOpenProbability / finalRightEyeProbablity;
//                finalRightEyeProbablity = rightEyeOpenProbability / finalProbablityCount;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createMicrophone();
                        speakAnswer();
                    }
                });

            }
        }, timer * 1000);
    }


    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // plus a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that plus has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // plus completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_HANDLE_CAMERA_PERM) {
//            Log.d(TAG, "Got unexpected permission result: " + requestCode);
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted - initialize the camera source");
                // we have permission, so create the camerasource
                requestMicrophonePermission();
                createCameraSource();
                return;
            }

            Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                    " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            };


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Face Tracker sample")
//                .setMessage(R.string.no_camera_permission)
//                .setPositiveButton(R.string.ok, listener)
//                .show();

        }
        else if (requestCode == RC_HANDLE_MICROPHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                createMicrophone();
            }
        }
    }

    private  void requestMicrophonePermission(){
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int mc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int sc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (mc == PackageManager.PERMISSION_GRANTED && sc == PackageManager.PERMISSION_GRANTED) {
            createMicrophone();
        } else {
            String [] permissionsSound = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            ActivityCompat.requestPermissions(this,permissionsSound,RC_HANDLE_MICROPHONE);
        }
    }

    private void createMicrophone(){
        //Recording
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
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            Log.d("vision",String.valueOf(face.getIsLeftEyeOpenProbability()));
            Log.d("vision",String.valueOf(face.getIsRightEyeOpenProbability()));
            leftEyeOpenProbability += face.getIsLeftEyeOpenProbability();
            rightEyeOpenProbability += face.getIsRightEyeOpenProbability();
            probabilityCount += 1;
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
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

    private void resize() {
        if (oOne.getText().length() > 10) {
            oOne.setTextSize(25);
        }
        if (oTwo.getText().length() > 10) {
            oTwo.setTextSize(25);
        }
        if (oThree.getText().length() > 10) {
            oThree.setTextSize(25);

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
                    thumbsUp.setVisibility(View.VISIBLE);
                    answerTextView.setText(oOne.getText().toString());
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
                    thumbsUp.setVisibility(View.VISIBLE);
                    answerTextView.setText(oTwo.getText().toString());
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
                    thumbsUp.setVisibility(View.VISIBLE);
                    answerTextView.setText(oThree.getText().toString());
                    firmButKindVoice(oThree.getText().toString());
                    chosenAnswer = 3;
                }
            }
        }

        if (v.getId() == back.getId()){
            if(!choice ) {
                if (onSpeakYourMind || onRecord) {
                    FragmentManager fm = getFragmentManager();
                    DialogBuilder dialog = DialogBuilder.newInstance("Confirm", this);
                    dialog.show(fm, "frag");
                } else {
                    intro = true;
                    situationSpeakLayout.setVisibility(View.VISIBLE);
                    back.setBackgroundResource(R.drawable.home_selector);
                    activateS();
                    s = true;
                    onSpeakYourMind = true;
                    next.setVisibility(View.VISIBLE);
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

            onSpeakYourMind = false;
            onAskNicely = true;
            situationSpeakLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.GONE);
            oLayout.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            back.setBackgroundResource(R.drawable.back_selector);
            activateA();
        }


        if(v.getId() == nextFirm.getId()){
            onRecord = true;
            activateE();
            msgLayout.setVisibility(View.GONE);
            thumbsUp.setVisibility(View.GONE);
            safeRecordImageButton.setVisibility(View.GONE);
            safePRMImageView.setVisibility(View.GONE);
            safeEyeContactImageView.setVisibility(View.VISIBLE);
            safeRecordImageButton.setVisibility(View.GONE);
            answerTextView.setVisibility(View.GONE);
            answerImageView.setVisibility(View.GONE);
            nextFirm.setVisibility(View.GONE);
            safeActivityLayout.setBackgroundResource(R.drawable.safe_blob_eye_contact_bg);
            startEyeTracking();
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
            answerImageView.setVisibility(View.GONE);
            doneRecording.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            answerImageView.setVisibility(View.VISIBLE);
            answerTextView.setTextSize(25);

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

            //Get the eye tracking values to store in the DB
            finalProbablityCount = probabilityCount;
            finalLeftEyeProbablity = leftEyeOpenProbability / finalRightEyeProbablity;
            finalRightEyeProbablity = rightEyeOpenProbability / finalProbablityCount;

            complete("test");
            safeEyeContactImageView.setVisibility(View.GONE);
            safeDoneImageButton.setVisibility(View.GONE);
            doneRecording.setVisibility(View.GONE);
            again.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            mediaRecorder.stop();
            mediaRecorder.release();
            Toast.makeText(getApplicationContext(),"Recorded Successfully", Toast.LENGTH_LONG).show();
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


    public String getAns() {
        switch(chosenAnswer)
        {
            case 1:
                return oOne.getText().toString();
            case 2:
                return oTwo.getText().toString();
            case 3:
                return oThree.getText().toString();
            default: return "";
        }
    }


    private void firmButKindVoice(String msg) {

        // First get rid of the camera
        mPreview.setVisibility(View.GONE);
        onRecord = true;
        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        rLayout.setVisibility(View.VISIBLE);
        safePRMImageView.setVisibility(View.GONE);
        safeEyeContactImageView.setVisibility(View.GONE);
        safeBlob.setVisibility(View.GONE);
        msgLayout.setVisibility(View.VISIBLE);
        message.setTypeface(t);
        message.setText("Firm But Kind Voice\n\n");
        activateF();
        title.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        thumbsUp.setVisibility(View.VISIBLE);
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
        rLayout.removeAllViews();
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
                safeAcronymLayout.setVisibility(View.GONE);
                safeActivityLayout.setBackgroundResource(R.drawable.safe_background);
            }
        });

        ContentValues c = new ContentValues();
        c.put("TIMESTAMP", System.currentTimeMillis());
        c.put("SITUATION", sText);
        c.put("SELECTED_F", msg);
        if(wrong) {
            c.put("WRONG_FLAG", 1);
        }
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

    public void activateS(){
        sView.setBackgroundResource(R.drawable.s_yellow);
        aView.setBackgroundResource(R.drawable.a_white);
        fView.setBackgroundResource(R.drawable.f_white);
        eView.setBackgroundResource(R.drawable.e_white);
    }

    public void activateA(){
        sView.setBackgroundResource(R.drawable.s_white);
        aView.setBackgroundResource(R.drawable.a_yellow);
        fView.setBackgroundResource(R.drawable.f_white);
        eView.setBackgroundResource(R.drawable.e_white);
    }

    public void activateF(){
        sView.setBackgroundResource(R.drawable.s_white);
        aView.setBackgroundResource(R.drawable.a_white);
        fView.setBackgroundResource(R.drawable.f_yellow);
        eView.setBackgroundResource(R.drawable.e_white);
    }
    public void activateE(){
        sView.setBackgroundResource(R.drawable.s_white);
        aView.setBackgroundResource(R.drawable.a_white);
        fView.setBackgroundResource(R.drawable.f_white);
        eView.setBackgroundResource(R.drawable.e_yellow);
    }
}