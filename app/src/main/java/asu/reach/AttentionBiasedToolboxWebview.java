package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
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
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap = new Bitmap[2];
    private Random random = new Random();
    int index = 0;
    private int[] imageIndArray;
    MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getImage() {
                return getImages();
            }

            @JavascriptInterface
            public void ding() {
                mp.start();
            }
        }, "abmt");

        mWebView.loadUrl("file:///android_asset/www/html/abmt.html");

        neutralImgs = getResources().obtainTypedArray(R.array.neutral_images);
        threatImgs = getResources().obtainTypedArray(R.array.threat_images);
        imageIndArray = new int[neutralImgs.length()];
        for (int i = 0; i < imageIndArray.length; i++) imageIndArray[i] = i;
        mp = MediaPlayer.create(this, R.raw.ding);
    }

    private String getImages() {
        int bitMapInd = random.nextInt(2);
        int rndInt = random.nextInt(neutralImgs.length() - index) + index;
        int neutralId = neutralImgs.getResourceId(rndInt, 0);
        int threatId = threatImgs.getResourceId(rndInt, 0);
        swapIndex(index, rndInt);
        index = (index + 1) % neutralImgs.length();

        bmap[0] = (bitMapInd == 0) ? BitmapFactory.decodeResource(getResources(), neutralId) : BitmapFactory.decodeResource(getResources(), threatId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmap[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String image1base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        bmap[1] = (bitMapInd == 1) ? BitmapFactory.decodeResource(getResources(), neutralId) : BitmapFactory.decodeResource(getResources(), threatId);
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        bmap[1].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
        byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
        String image2base64 = Base64.encodeToString(byteArray2, Base64.DEFAULT);

        String correctAnswer = (bitMapInd == 0) ? "left" : "right";
        return "data:image/png;base64," + image1base64 + "||" + "data:image/png;base64," + image2base64 + "||" + correctAnswer;
    }

    public void swapIndex(int i, int j) {
        int temp = imageIndArray[i];
        imageIndArray[i] = imageIndArray[j];
        imageIndArray[j] = temp;
    }

}
