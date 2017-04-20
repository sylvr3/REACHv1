package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class AttentionBiasedToolboxWebview extends Activity {

    private WebView mWebView;
    private TypedArray neutralImgs, sadImgs, disguiseImgs, angryImgs, traingActorNeutral1, traingActorEmotional1, traingActorNeutral2, traingActorEmotional2;
    private Bitmap[] bmap = new Bitmap[2];
    private Random random = new Random();
    private int index = 0, blockArraySize, indexSad, neutral, divisionId, imageArraySize = 15, indexNeutral = 0, indexDisguise = 0, indexAngry = 0;
    private int[] blockArray, sadArray, neutralArray, disguiseArray, angryArray, neutralSadArray, neutralDisguiseArray, neutralAngryArray;
    MediaPlayer mp;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Window settings
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.abmt_web_view);
        mWebView = (WebView) findViewById(R.id.abmt_web_view);
        mWebView.setInitialScale(1);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setBackgroundColor(0x69696969);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getImage() {
                return getImages();
            }

            @JavascriptInterface
            public void ding() {
                mp.start();
            }

            @JavascriptInterface
            public boolean isTrialAvailable() {
                return sharedPreferences.getBoolean("disableTrial", false);
            }

            @JavascriptInterface
            public void makeTrialAvailable() {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("disableTrial", false);
            }

            @JavascriptInterface
            public void setTrialVars() {
                blockArraySize = 240;
                blockArray = new int[blockArraySize];
                sadArray = new int[imageArraySize];
                neutralSadArray = new int[imageArraySize];
                angryArray = new int[imageArraySize];
                neutralAngryArray = new int[imageArraySize];
                disguiseArray = new int[imageArraySize];
                neutralDisguiseArray = new int[imageArraySize];
                neutralArray = new int[imageArraySize];

                for(int i = 0; i < blockArraySize; i++) blockArray[i] = i;

                for (int i = 0; i < imageArraySize; i++) {
                    sadArray[i] = i;
                    neutralSadArray[i] = i;
                    angryArray[i] = i;
                    neutralAngryArray[i] = i;
                    neutralArray[i] = i;
                    disguiseArray[i] = i;
                    neutralDisguiseArray[i] = i;
                }

                shuffleBlockArray();
            }

            @JavascriptInterface
            public void setTutorialVars() {
                blockArraySize = 40;
                blockArray = new int[blockArraySize];
                sadArray = new int[imageArraySize];
                neutralSadArray = new int[imageArraySize];
                angryArray = new int[imageArraySize];
                neutralAngryArray = new int[imageArraySize];
                disguiseArray = new int[imageArraySize];
                neutralDisguiseArray = new int[imageArraySize];
                neutralArray = new int[imageArraySize];

                for(int i = 0; i < blockArraySize; i++) blockArray[i] = i;

                for (int i = 0; i < imageArraySize; i++) {
                    sadArray[i] = i;
                    neutralSadArray[i] = i;
                    angryArray[i] = i;
                    neutralAngryArray[i] = i;
                    neutralArray[i] = i;
                    disguiseArray[i] = i;
                    neutralDisguiseArray[i] = i;
                }

                shuffleBlockArray();
            }
        }, "abmt");

        mWebView.loadUrl("file:///android_asset/www/html/abmt.html");

        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        sadImgs = getResources().obtainTypedArray(R.array.sad_images);
        disguiseImgs = getResources().obtainTypedArray(R.array.disguise_imgaes);
        angryImgs = getResources().obtainTypedArray(R.array.angry_images);

        mp = MediaPlayer.create(this, R.raw.ding);
    }

    private String getImages() {
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmap[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String image1base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        bmap[1] = (neutral == 1) ? BitmapFactory.decodeResource(getResources(), topImg) : BitmapFactory.decodeResource(getResources(), bottomImg);
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        bmap[1].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
        byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
        String image2base64 = Base64.encodeToString(byteArray2, Base64.DEFAULT);

        String correctAnswer = (neutral == 0) ? "left" : "right";

        return "data:image/png;base64," + image1base64 + "||" + "data:image/png;base64," + image2base64 + "||" + correctAnswer;
    }

    public void swapIndex(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void shuffleBlockArray() {
        for(int i = 0; i < blockArraySize; i++) {
            int ind = random.nextInt(blockArraySize-i) + i;
            swapIndex(blockArray,ind,i);
        }
    }

}
