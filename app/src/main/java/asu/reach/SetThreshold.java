package asu.reach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SetThreshold extends Activity {

    ListView mainListView;
    String protocol_feature_array[]={"STIC","STOP","WORRY HEADS","DAILY DIARY","RELAXATION"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_threshold);

        final ListAdapter adapter= new ArrayAdapter<String>(this,R.layout.row_layout,R.id.row_id,protocol_feature_array);
        mainListView=(ListView) findViewById(R.id.setThresholdListView);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new
                                                    AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            switch(position){
                                                                case 0:
                                                                    Intent sticIntent=new Intent(SetThreshold.this,SetThresholdListActivity.class);
                                                                    sticIntent.putExtra("protocol","STIC");
                                                                    startActivity(sticIntent);
                                                                    break;
                                                                case 1:
                                                                    Intent stopIntent=new Intent(SetThreshold.this,SetThresholdListActivity.class);
                                                                    stopIntent.putExtra("protocol","STOP");
                                                                    startActivity(stopIntent);
                                                                    break;
                                                                case 2:
                                                                    Intent whIntent=new Intent(SetThreshold.this,SetThresholdListActivity.class);
                                                                    whIntent.putExtra("protocol","WORRYHEADS");
                                                                    startActivity(whIntent);
                                                                    break;
                                                                case 3:
                                                                    Intent ddIntent=new Intent(SetThreshold.this,SetThresholdListActivity.class);
                                                                    ddIntent.putExtra("protocol","DAILY_DIARY");
                                                                    startActivity(ddIntent);
                                                                    break;
                                                                case 4:
                                                                    Intent relaxIntent=new Intent(SetThreshold.this,SetThresholdListActivity.class);
                                                                    relaxIntent.putExtra("protocol","RELAXATION");
                                                                    startActivity(relaxIntent);
                                                                    break;
                                                            };

                                                        }
                                                    });
    }
}
