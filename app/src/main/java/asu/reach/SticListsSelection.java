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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SticListsSelection extends Activity {

    ListView mainListView;
    String week_array[]={"Week 1","Week 2","Week 3","Week 4","Week 5","Week 6"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stic__lists__selection);
        final ListAdapter adapter= new ArrayAdapter<String>(this,R.layout.row_layout,R.id.row_id,week_array);
        mainListView=(ListView) findViewById(R.id.sticListSelectionView);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new
                                                    AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            switch(position){
                                                                case 0:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                case 1:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                case 2:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                case 3:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                case 4:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                case 5:
                                                                    sticListSelectionDialog();
                                                                    break;
                                                                default:

                                                            };

                                                        }
                                                    });
    }

    protected void sticListSelectionDialog(){
        AlertDialog levelDialog;

// Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {" None "," List 1 "," List 2 "," Both "};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The List for this week:");
        builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        // Your code when first option seletced

                        break;
                    case 1:
                        // Your code when 2nd  option seletced

                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        break;
                    case 3:
                        // Your code when 4th  option seletced
                        break;

                }
                dialog.dismiss();
            }

        });
        levelDialog = builder.create();
        levelDialog.show();
    }
}
