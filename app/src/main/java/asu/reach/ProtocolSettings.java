package asu.reach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class ProtocolSettings extends Activity {

    ListView mainListView;
    String protocol_feature_array[]={"STIC","STOP","WORRY HEADS","DAILY DIARY","RELAXATION"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol_settings);

        final ListAdapter adapter= new ArrayAdapter<String>(this,R.layout.row_layout,R.id.row_id,protocol_feature_array);
        mainListView=(ListView) findViewById(R.id.protocolListView);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new
                                                    AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            switch(position){
                                                                case 0:
                                                                    Intent stic_intent=new Intent(ProtocolSettings.this,SticListsSelection.class);
                                                                    startActivity(stic_intent);
                                                                    break;
                                                                case 1:
                                                                    Intent stop_intent=new Intent(ProtocolSettings.this,WeekListViewCheckboxActivity.class);
                                                                    stop_intent.putExtra("protocol","STOP");
                                                                    startActivity(stop_intent);
                                                                    break;
                                                                case 2:
                                                                    Intent worry_heads_intent=new Intent(ProtocolSettings.this,WeekListViewCheckboxActivity.class);
                                                                    worry_heads_intent.putExtra("protocol","WH");
                                                                    startActivity(worry_heads_intent);
                                                                    break;
                                                                case 3:
                                                                    Intent dd_intent=new Intent(ProtocolSettings.this,WeekListViewCheckboxActivity.class);
                                                                    dd_intent.putExtra("protocol","DD");
                                                                    startActivity(dd_intent);
                                                                    break;
                                                                case 4:
                                                                    Intent relax_intent=new Intent(ProtocolSettings.this,WeekListViewCheckboxActivity.class);
                                                                    relax_intent.putExtra("protocol","DD");
                                                                    startActivity(relax_intent);
                                                                    break;
                                                                default:
                                                                    String rowSelected="You selected"+String.valueOf(adapter.getItem(position));
                                                                    Toast.makeText(ProtocolSettings.this, rowSelected, Toast.LENGTH_SHORT).show();
                                                                    break;
                                                            };

                                                        }
                                                    });
    }

}
