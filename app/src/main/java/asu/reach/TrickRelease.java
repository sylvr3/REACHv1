package asu.reach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;


public class TrickRelease extends Activity {

    MyCustomAdapter dataAdapter = null;
    int daySelectedCount=0;
    boolean uncheckFlag=false;
    ArrayList<Days> dayList=null;
    HashSet<String> setOfDays = new HashSet<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trick_release);
        //Array list of countries

        try {
            DBHelper helper= new DBHelper(getApplicationContext());
            dayList=helper.getSelectedTrickReleaseDays();
            daySelectedCount=0;
            for(int loopIndex=0;loopIndex<dayList.size();loopIndex++){
                Days day= dayList.get(loopIndex);
                if(day.isSelected()==true){
                    daySelectedCount++;
                    setOfDays.add(day.getDayName());
                }
            }
            System.out.println("The daySelectedCount is:"+String.valueOf(daySelectedCount));
        }catch (Exception e){
            e.printStackTrace();
        }
        //Generate list View from ArrayList
        displayListView();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        DBHelper helper= new DBHelper(getApplicationContext());
                        helper.setTrickReleaseDays(setOfDays);
                        TrickRelease.super.onBackPressed();

                    }
                }).create().show();
    }

    private void displayListView() {
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.check_box_row, dayList);
        ListView listView = (ListView) findViewById(R.id.trickReleaseListView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Days day = (Days) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + day.getDayName(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Days> {

        private ArrayList<Days> dayList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Days> dayList) {
            super(context, textViewResourceId, dayList);
            this.dayList = new ArrayList<Days>();
            this.dayList.addAll(dayList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.check_box_row, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Days day = (Days) cb.getTag();
                        if(daySelectedCount<2){
                            if(cb.isChecked()==true){
                                daySelectedCount++;
                            }else{
                                daySelectedCount--;
                            }

                            setOfDays.add((String)cb.getText());
                            if(cb.isChecked()==false){
                                setOfDays.remove(cb.getText());
                            }
                            Toast.makeText(getApplicationContext(),
                                    "Clicked on Checkbox: " + cb.getText() +
                                            " is " + cb.isChecked() +" ArrayLength "+String.valueOf(setOfDays.size()),
                                    Toast.LENGTH_SHORT).show();

                            day.setSelected(cb.isChecked());
                        }else{
                            if(cb.isChecked()==true){
                                daySelectedCount++;
                            }else{
                                daySelectedCount--;
                            }
                            if(daySelectedCount>2){
                                Toast.makeText(getApplicationContext(),"Please select only 2 days for trick release.",Toast.LENGTH_SHORT).show();
                                cb.toggle();
                                daySelectedCount--;
                                System.out.println("The daySelectedCount is:"+String.valueOf(daySelectedCount));
                            }else{
                                if(cb.isChecked()==false){
                                    setOfDays.remove(cb.getText());
                                }
                                Toast.makeText(getApplicationContext(),
                                        "Clicked on Checkbox: " + cb.getText() +
                                                " is " + cb.isChecked(),
                                        Toast.LENGTH_SHORT).show();
                                System.out.println("The daySelectedCount is:"+String.valueOf(daySelectedCount));

                            }
                        }
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            Days day = dayList.get(position);
            holder.name.setText(day.getDayName());
            holder.name.setChecked(day.isSelected());
            holder.name.setTag(day);
            return convertView;

        }

    }
}


