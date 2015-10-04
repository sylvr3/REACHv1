package asu.reach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WeekListViewCheckboxActivity extends Activity {

    MyCustomAdapter dataAdapter = null;
    String protocol_name;
    ArrayList<Weeks> week_list;
    boolean weekSelectionArray[]= new boolean[6];
    Map mapOfWeekSelected=new HashMap<Integer,Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list_view_checkbox);
        Bundle extras= getIntent().getExtras();
        if (extras != null) {
            protocol_name = extras.getString("protocol");
        }

        //Set the week_list by getting predefined values from the database.
        DBHelper helper=new DBHelper(getApplicationContext());
        week_list=helper.getWeeksSelectedListForProtocol(protocol_name);
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
                        helper.setWeeksSelectedForProtocol(protocol_name,mapOfWeekSelected);
                        WeekListViewCheckboxActivity.super.onBackPressed();

                    }
                }).create().show();
    }

    private void displayListView() {
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.check_box_row, week_list);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Weeks week = (Weeks) parent.getItemAtPosition(position);
                /*Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + week.getWeek_number(),
                        Toast.LENGTH_SHORT).show();*/
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Weeks> {

        private ArrayList<Weeks> weekList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Weeks> weekList) {
            super(context, textViewResourceId, weekList);
            this.weekList = new ArrayList<Weeks>();
            this.weekList.addAll(weekList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            //Log.v("ConvertView", String.valueOf(position));

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
                        Weeks week = (Weeks) cb.getTag();
                        /*Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked() ,
                                Toast.LENGTH_SHORT).show();*/
                        if(cb.isChecked()==true){
                            mapOfWeekSelected.put(position+1,1);
                        }else if(cb.isChecked()==false){
                            mapOfWeekSelected.put(position+1,0);
                        }

                        week.setSelected(cb.isChecked());
                        //Log.i("is Selected", String.valueOf((Boolean) cb.isChecked()));
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Weeks week = weekList.get(position);
            holder.name.setText(week.getWeek_number());
            holder.name.setChecked(week.isSelected());
            holder.name.setTag(week);
            return convertView;

        }

    }
}


