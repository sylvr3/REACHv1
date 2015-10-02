package asu.reach;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;


public class AdminFeatures extends Activity{

    ListView mainListView;
    String feature_array[]={"Protocol Settings","Start Date","Notification time","Schedule for Trick Release","Pin Change","Export Data"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_features);

        final ListAdapter adapter= new ArrayAdapter<String>(this,R.layout.row_layout,R.id.row_id,feature_array);
        mainListView=(ListView) findViewById(R.id.AdminFeatureslistView);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new
                                                AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        switch(position){
                                                            case 0:
                                                                Intent intent=new Intent(AdminFeatures.this,ProtocolSettings.class);
                                                                startActivity(intent);
                                                                break;
                                                            case 1:
                                                                showDatePickerDialog();
                                                                break;
                                                            case 2:
                                                                showTimePickerDialog();
                                                                break;
                                                            case 3:
                                                                Intent trick_intent=new Intent(AdminFeatures.this,TrickRelease.class);
                                                                startActivity(trick_intent);
                                                                break;
                                                            default:
                                                                String rowSelected="You selected"+String.valueOf(adapter.getItem(position));
                                                                Toast.makeText(AdminFeatures.this,rowSelected,Toast.LENGTH_SHORT).show();
                                                                break;
                                                        };

                                                    }
                                                });
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Check if the time is set already, if yes, display that time. Otherwise show current time.
            int hour,minute;
            Calendar c;
            DBHelper helper= new DBHelper(this.getActivity().getApplicationContext());
            c=helper.getTimeForNotifications();
            if(c!=null){
                hour=c.get(Calendar.HOUR_OF_DAY);
                minute=c.get(Calendar.MINUTE);
            }else{
                //Calendar is null. Use the current time as the default values for the picker
                c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            DBHelper helper= new DBHelper(this.getActivity().getApplicationContext());
            boolean result=helper.setTimeForNotifications(hourOfDay,minute);
            if(result==true)
                System.out.println("Time is set for notifications.");
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Check if the date is set already, if yes, display that date. Otherwise show current date.
            int year,month,day;
            Calendar c;
            DBHelper helper= new DBHelper(this.getActivity().getApplicationContext());
            c=helper.getStartDateForProtocol();
            if(c!=null){
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }else{
                //Calendar is null. Use the current date as the default date in the picker
                c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            DBHelper helper= new DBHelper(this.getActivity().getApplicationContext());
            boolean result=helper.setStartDateForProtocol(year,month,day);
        }
    }

}
