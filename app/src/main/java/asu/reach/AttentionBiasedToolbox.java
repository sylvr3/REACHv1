package asu.reach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

public class AttentionBiasedToolbox extends Activity implements View.OnClickListener{
    private ImageButton imgTop, imgBottom;
    private Random random;
    private TypedArray neutralImgs, threatImgs;
    private Bitmap[] bmap;
    private int neutral,count;
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
        fetchImages();
    }
    public void fetchImages() {
        int bitMapInd = random.nextInt(2);
        int rndInt = random.nextInt(neutralImgs.length());
        int neutralId = neutralImgs.getResourceId(rndInt,0);
        int threatId = threatImgs.getResourceId(rndInt,0);
        bmap[0] = (bitMapInd == 0)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        bmap[1] = (bitMapInd == 1)? BitmapFactory.decodeResource(getResources(),neutralId):BitmapFactory.decodeResource(getResources(),threatId);
        neutral = (bitMapInd == 0)?0:1;
        imgTop.setImageBitmap(bmap[0]);
        imgBottom.setImageBitmap(bmap[1]);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == imgTop.getId()) {
            if(neutral == 0) count++;
            fetchImages();
        }
        if(v.getId() == imgBottom.getId()) {
            if(neutral == 1) count++;
            fetchImages();
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(count),Toast.LENGTH_LONG).show();
        System.out.println(count);
    }
}
