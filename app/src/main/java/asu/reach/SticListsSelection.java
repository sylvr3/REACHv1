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
                                                                    sticListSelectionDialog(1);
                                                                    break;
                                                                case 1:
                                                                    sticListSelectionDialog(2);
                                                                    break;
                                                                case 2:
                                                                    sticListSelectionDialog(3);
                                                                    break;
                                                                case 3:
                                                                    sticListSelectionDialog(4);
                                                                    break;
                                                                case 4:
                                                                    sticListSelectionDialog(5);
                                                                    break;
                                                                case 5:
                                                                    sticListSelectionDialog(6);
                                                                    break;
                                                                default:

                                                            };

                                                        }
                                                    });
    }

    protected void sticListSelectionDialog(final int week_no){
        AlertDialog levelDialog;

        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {" None "," List 1 "," List 2 "," Both "};

        //Get the preselected STIC List already stored in the database.
        final DBHelper helper= new DBHelper(getApplicationContext());
        int list_choice=helper.getSticListValueForGivenWeek(week_no);

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The List for this week:");
        builder.setSingleChoiceItems(items, list_choice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        //Set None for that particular week.
                        helper.setSticListValueForGivenWeek(week_no,0);
                        break;
                    case 1:
                        //Set List 1 for that particular week.
                        helper.setSticListValueForGivenWeek(week_no,1);
                        break;
                    case 2:
                        //Set List 2 for that particular week.
                        helper.setSticListValueForGivenWeek(week_no,2);
                        break;
                    case 3:
                        //Set Both for that particular week.
                        helper.setSticListValueForGivenWeek(week_no,3);
                        break;

                }
                dialog.dismiss();
            }

        });
        levelDialog = builder.create();
        levelDialog.show();
    }
}
