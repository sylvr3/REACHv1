package asu.reach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;


public class SetThresholdListActivity extends Activity {

    ListView mainListView;
    String protocol_name;
    String protocol_feature_array[]={"Week 1 ","Week 2 ","Week 3 ","Week 4 ","Week 5 ","Week 6 "};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_threshold_list);
        Bundle extras= getIntent().getExtras();
        if (extras != null) {
            protocol_name = extras.getString("protocol");
        }

        final ListAdapter adapter= new ArrayAdapter<String>(this,R.layout.row_layout,R.id.row_id,protocol_feature_array);
        mainListView=(ListView) findViewById(R.id.thresholdWeekListView);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new
                                                    AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            DBHelper helper=new DBHelper(getApplicationContext());
                                                            switch(position){
                                                                case 0:
                                                                    dialogForModifyingThreshold(protocol_name,1,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,1));
                                                                    break;
                                                                case 1:
                                                                    dialogForModifyingThreshold(protocol_name,2,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,2));
                                                                    break;
                                                                case 2:
                                                                    dialogForModifyingThreshold(protocol_name,3,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,3));
                                                                    break;
                                                                case 3:
                                                                    dialogForModifyingThreshold(protocol_name,4,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,4));
                                                                    break;
                                                                case 4:
                                                                    dialogForModifyingThreshold(protocol_name,5,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,5));
                                                                    break;
                                                                case 5:
                                                                    dialogForModifyingThreshold(protocol_name,6,helper.getActivityThresholdCountOfProtocolForAWeek(protocol_name,6));
                                                                    break;
                                                            };
                                                            helper.close();
                                                        }
                                                    });
    }


    public void dialogForModifyingThreshold(final String protocol, final int weekNumber,Integer alreadySetValue){
        final EditText textVal = new EditText(this);
        textVal.setText(alreadySetValue.toString());
        final DBHelper helper=new DBHelper(getApplicationContext());
        new AlertDialog.Builder(this)
                .setTitle("Set Threshold")
                .setMessage("Set new threshold")
                .setView(textVal)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newTextVal = textVal.getText().toString();
                        helper.setActivityProgressCountToSpecificValue(protocol,Integer.parseInt(newTextVal),weekNumber);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


}
