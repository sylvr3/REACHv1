package asu.reach;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;


public class WorryHeads extends Activity implements View.OnClickListener{
    private SQLiteDatabase db;
    private RelativeLayout oLayout,msgLayout;
    private ImageView sView, tView,o1,o2,o3,o4;
    private TextView oOne, oTwo, oThree, oFour, message;
    private ImageButton back, again, done;
    private String sText, tText, pText;
    private LinearLayout complete;

    private int wrongO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_worry_heads);

        oLayout = (RelativeLayout)findViewById(R.id.oLayout);
        msgLayout = (RelativeLayout)findViewById(R.id.msgLayout);
        sView = (ImageView)findViewById(R.id.sView);
        tView = (ImageView)findViewById(R.id.tView);
        o1 = (ImageView)findViewById(R.id.oOne);
        o2 = (ImageView)findViewById(R.id.oTwo);
        o3 = (ImageView)findViewById(R.id.oThree);
        o4 = (ImageView)findViewById(R.id.oFour);
        oOne = (TextView)findViewById(R.id.oOneTxt);
        oTwo = (TextView)findViewById(R.id.oTwoTxt);
        oThree = (TextView)findViewById(R.id.oThreeTxt);
        oFour = (TextView)findViewById(R.id.oFourTxt);
        message = (TextView)findViewById(R.id.message);
        back = (ImageButton)findViewById(R.id.whBackBtn);
        again = (ImageButton)findViewById(R.id.againBtn);
        done = (ImageButton)findViewById(R.id.whDoneBtn);
        complete = (LinearLayout)findViewById(R.id.completeLayout);

        sView.setOnClickListener(this);
        tView.setOnClickListener(this);
        oOne.setOnClickListener(this);
        oTwo.setOnClickListener(this);
        oThree.setOnClickListener(this);
        oFour.setOnClickListener(this);
        back.setOnClickListener(this);
        again.setOnClickListener(this);
        done.setOnClickListener(this);

        Typeface t = Typeface.createFromAsset(getAssets(), "agentorange.ttf");
        oOne.setTypeface(t);
        oTwo.setTypeface(t);
        oThree.setTypeface(t);
        oFour.setTypeface(t);
        message.setTypeface(t);

        DBHelper helper = new DBHelper(this);
        //helper.copyDataBase();
        //helper.openDataBase();
        db = helper.getDB();

        try {
            Cursor c = db.rawQuery("SELECT * from STOP_WORRYHEADS where COMPLETED_FLAG = 0", null);
            // Random seed
            if(c.getCount() > 0) {
                Random num = new Random(System.currentTimeMillis());
                int position = (int) (c.getCount() * num.nextDouble());
                c.moveToPosition(position);
                wrongO = (int)(num.nextDouble()*3);
                sText = c.getString(c.getColumnIndex("S"));
                tText = c.getString(c.getColumnIndex("T"));
                pText = c.getString(c.getColumnIndex("P"));
                String[] o = new String[4];
                o[0] = c.getString(c.getColumnIndex("O1"));
                o[1] = c.getString(c.getColumnIndex("O2"));
                o[2] = c.getString(c.getColumnIndex("O3"));
                o[3] = c.getString(c.getColumnIndex("O_WRONG"));
                populateO(o);

            }else{

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void populateO(String[] o){
        switch(wrongO){
            case 0:{
                oOne.setText(o[3]);
                oTwo.setText(o[0]);
                oThree.setText(o[1]);
                oFour.setText(o[2]);
                resize();
                break;
            }
            case 1:{
                oTwo.setText(o[3]);
                oThree.setText(o[0]);
                oOne.setText(o[1]);
                oFour.setText(o[2]);
                resize();
                break;
            }
            case 2:{
                oThree.setText(o[3]);
                oTwo.setText(o[0]);
                oOne.setText(o[1]);
                oFour.setText(o[2]);
                resize();
                break;
            }
            case 3:{
                oFour.setText(o[3]);
                oTwo.setText(o[0]);
                oThree.setText(o[1]);
                oOne.setText(o[2]);
                resize();
                break;
            }
        }
    }

    private void resize(){
        if(oOne.getText().length() > 110){
            oOne.setTextSize(10);
        }
        if(oTwo.getText().length() > 110){
            oOne.setTextSize(10);
        }
        if(oThree.getText().length() > 110){
            oOne.setTextSize(10);
        }
        if(oFour.getText().length() > 110){
            oOne.setTextSize(10);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == sView.getId()){
            oLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            message.setText("Situation:\n\n"+sText);
        }
        if (v.getId() == tView.getId()){
            oLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            message.setText("Thoughts:\n\n"+tText);
        }
        if (v.getId() == oOne.getId()){
            if(!oOne.isActivated()) {
                if (wrongO == 0) {
                    wrongSelection();
                    oOne.setActivated(true);
                    o1.setActivated(true);
                }else{
                    oLayout.setVisibility(View.GONE);
                    msgLayout.setVisibility(View.VISIBLE);
                    message.setText("Praise Yourself:\n\n" + pText);
                    complete(oOne.getText().toString());
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
                    oLayout.setVisibility(View.GONE);
                    msgLayout.setVisibility(View.VISIBLE);
                    message.setText("Praise Yourself:\n\n" + pText);
                    complete(oTwo.getText().toString());
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
                    oLayout.setVisibility(View.GONE);
                    msgLayout.setVisibility(View.VISIBLE);
                    message.setText("Praise Yourself:\n\n" + pText);
                    complete(oThree.getText().toString());
                }
            }
        }
        if (v.getId() == oFour.getId()){
            if(!oFour.isActivated()) {
                if (wrongO == 3) {
                    wrongSelection();
                    oFour.setActivated(true);
                    o4.setActivated(true);
                }else{
                    oLayout.setVisibility(View.GONE);
                    msgLayout.setVisibility(View.VISIBLE);
                    message.setText("Praise Yourself:\n\n" + pText);
                    complete(oFour.getText().toString());
                }
            }
        }
        if (v.getId() == back.getId()){
            oLayout.setVisibility(View.VISIBLE);
            msgLayout.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
        }
        if(v.getId() == again.getId()){
            Intent intent = new Intent(this, this.getClass());
            startActivity(intent);
            finish();
        }
        if(v.getId() == done.getId()){
            finish();
        }
    }

    private void wrongSelection(){
        oLayout.setVisibility(View.GONE);
        msgLayout.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        message.setText("Try again!");
    }

    private void complete(String msg){
        ContentValues c = new ContentValues();
        c.put("TIMESTAMP", System.currentTimeMillis());
        c.put("S", sText);
        c.put("SELECTED_O", msg);
        db.insert("WORRYHEADS_COMPLETION", "TIMESTAMP,S,SELECTED_O", c);
        c = new ContentValues();
        c.put("COMPLETED_FLAG", 1);
        int foo = db.update("STOP_WORRYHEADS",c,"S = \""+sText+"\"",null);
        if(foo > 0){
            System.out.println("Successful update");
        }
        complete.setVisibility(View.VISIBLE);
        sView.setClickable(false);
        tView.setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_worry_heads, menu);
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
}
