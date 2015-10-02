package asu.reach;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list_view_checkbox);
        Bundle extras= getIntent().getExtras();
        if (extras != null) {
            protocol_name = extras.getString("protocol");
        }
        //Generate list View from ArrayList
        displayListView();
    }

    private void displayListView() {


        //Array list of countries
        ArrayList<Weeks> weekList = new ArrayList<Weeks>();
        Weeks one= new Weeks("Week 1",true);
        weekList.add(one);
        Weeks two= new Weeks("Week 2",false);
        weekList.add(two);
        Weeks three= new Weeks("Week 3",true);
        weekList.add(three);
        Weeks four= new Weeks("Week 4",false);
        weekList.add(four);
        Weeks five= new Weeks("Week 5",false);
        weekList.add(five);
        Weeks six= new Weeks("Week 6",true);
        weekList.add(six);


        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.check_box_row, weekList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Weeks week = (Weeks) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + week.getWeek_number(),
                        Toast.LENGTH_SHORT).show();
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
                        Weeks week = (Weeks) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_SHORT).show();
                        week.setSelected(cb.isChecked());
                        Log.i("is Selected", String.valueOf((Boolean) cb.isChecked()));
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


