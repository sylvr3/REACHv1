package asu.reach;

import android.app.Activity;
import android.content.Context;
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



public class TrickRelease extends Activity {

    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trick_release);
        //Generate list View from ArrayList
        displayListView();
    }

    private void displayListView() {


        //Array list of countries
        ArrayList<Days> dayList = new ArrayList<Days>();
        Days monday= new Days("Monday",false);
        dayList.add(monday);
        Days tuesday= new Days("Tuesday",false);
        dayList.add(tuesday);
        Days wednesday= new Days("Wednesday",false);
        dayList.add(wednesday);
        Days thursday= new Days("Thursday",false);
        dayList.add(thursday);
        Days friday= new Days("Friday",false);
        dayList.add(friday);
        Days saturday= new Days("Saturday",false);
        dayList.add(saturday);
        Days sunday= new Days("Sunday",false);
        dayList.add(sunday);


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
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_SHORT).show();
                        day.setSelected(cb.isChecked());
                        Log.i("is Selected", String.valueOf((Boolean) cb.isChecked()));
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


