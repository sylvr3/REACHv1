package asu.reach;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Set;

public class Preferences extends PreferenceActivity /*implements SharedPreferences.OnSharedPreferenceChangeListener */ {

    private MultiSelectListPreference DDProtocolChange, STOPProtocolChange, STICProtocolChange, scheduleTricks;
    private SharedPreferences sharedPrefs;
    private SQLiteDatabase db;
    private int i = 0;
    private int j = 0;
    private PreferenceScreen exportToCSV;
    private EditTextPreference teacherPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        addPreferencesFromResource(R.xml.prefs);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
        DDProtocolChange = (MultiSelectListPreference) findPreference("DD_week_setting");
        STOPProtocolChange = (MultiSelectListPreference) findPreference("STOP_week_setting");
        teacherPin = (EditTextPreference) findPreference("teacherPIN");
        exportToCSV = (PreferenceScreen) findPreference("exportDataMenu");
        scheduleTricks = (MultiSelectListPreference) findPreference("scheduled_release_tricks");
        exportToCSV.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                callExportCSVFunction();
                return true;
            }
        });
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spf.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals("DD_week_setting")) {
                    dailyDiaryProtocolChange(sharedPreferences, s);
                } else if (s.equals("STOP_week_setting")) {
                    StopProtocolChange(sharedPreferences, s);
                } else if (s.equals("Week1_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 1);
                } else if (s.equals("Week2_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 2);
                } else if (s.equals("Week3_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 3);
                } else if (s.equals("Week4_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 4);
                } else if (s.equals("Week5_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 5);
                } else if (s.equals("Week6_STIC_protool_setting")) {
                    SticProtocolChange(sharedPreferences, s, 6);
                } else if (s.equals("teacherPIN")) {
                    updateTeacherPIN(sharedPreferences,s);
                }
                else if(s.equals("scheduled_release_tricks")){
                    setScheduleForTricks(sharedPreferences,s);
                }
            }
        });
    }

    public void setScheduleForTricks(SharedPreferences pref, String s){
        Set<String> stringSet = pref.getStringSet("scheduled_release_tricks", null);
        String[] stringArr = stringSet.toArray(new String[stringSet.size()]);
        try {
            DBHelper helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            ContentValues v = new ContentValues();
            v.put("trick_day1", stringArr[0]);
            v.put("trick_day2", stringArr[1]);
            int rowUpdateCount = db.update("DATE_TIME_SET", v, "id=1", null);
            Log.i("Trick Days row Updation count",rowUpdateCount+"");
            db.close();
            helper.close();

        } catch (Exception e) {
            Log.i("Exception occured", "Exception occured");
            e.printStackTrace();
        }
    }

    public void updateTeacherPIN(SharedPreferences pref,String s){
        String newTeacherPIN = pref.getString("teacherPIN", null);
        int compareNewPin = Integer.parseInt(newTeacherPIN);
        if(compareNewPin>4000 && compareNewPin < 4100) {
            try {
                DBHelper helper = new DBHelper(getApplicationContext());
//            helper.trackEvent(helper,"RELAXATION","LANDING_PAGE");
                db = helper.getDB();
                ContentValues v = new ContentValues();
                v.put("PIN", newTeacherPIN);
                int rowUpdateCount = db.update("PINS", v, "OWNER='teacher'", null);
                db.close();
                helper.close();

            } catch (Exception e) {
                Log.i("Exception occured", "Exception occured");
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"Try again with 40XX Series",Toast.LENGTH_LONG).show();
    }

    public void callExportCSVFunction(/*SharedPreferences preferences, String s*/) {
        try {
            DBHelper helper = new DBHelper(this);
//            helper.trackEvent(helper,"RELAXATION","LANDING_PAGE");
            db = helper.getDB();
            Cursor c = db.rawQuery("select * from EVENT_TRACKER;", null);
            helper.exportToCSV(c, "REACH_DATA.csv"/*, getApplicationContext()*/);
            db.close();
            helper.close();

        } catch (Exception e) {
            Log.i("Exception occured", "Exception occured");
            e.printStackTrace();
        }
    }

    public void SticProtocolChange(SharedPreferences pref, String s, int week_no) {
        try {
            DBHelper helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            ContentValues v;
            String listToPopulate = pref.getString("Week" + week_no + "_STIC_protool_setting", null);
            v = new ContentValues();
            v.put("STIC", listToPopulate);
            int noOfRowsUpdated = db.update("ADMIN_ACTIVITY_SCHEDULER", v, "WEEK_NO=" + week_no, null);
            Log.d("Rows Updated for STIC_WEEK" + week_no + "", noOfRowsUpdated + "");
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dailyDiaryProtocolChange(SharedPreferences pref, String s) {
        Set<String> stringSet = pref.getStringSet("DD_week_setting", null);
        String[] stringArr = stringSet.toArray(new String[stringSet.size()]);
        String[] stringArrNotContains = new String[6 - stringArr.length];
        int chk = 0;
        int chk1 = 0;
        while (chk < 6) {
            if (!Arrays.asList(stringArr).contains((chk + 1) + "")) {
                stringArrNotContains[chk1++] = (chk + 1) + "";
            }
            chk++;
        }
        try {
            DBHelper helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            while (i < stringArr.length) {
                Cursor cursor = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set DIARY_EVENT1=1 where (DIARY_EVENT1=1 or DIARY_EVENT2=1) and WEEK_NO=" + Integer.parseInt(stringArr[i].toString()), null);
                cursor.close();

                i += 1;
            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            while (i < stringArrNotContains.length) {
                Cursor cursor1 = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set DIARY_EVENT1=0 where WEEK_NO=" + Integer.parseInt(stringArrNotContains[i].toString()), null);
                cursor1.close();
                i += 1;
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void StopProtocolChange(SharedPreferences pref, String s) {
        Set<String> stringSet = pref.getStringSet("STOP_week_setting", null);
        String[] stringArr = stringSet.toArray(new String[stringSet.size()]);
        String[] stringArrNotContains = new String[6 - stringArr.length];
        int chk = 0;
        int chk1 = 0;
        while (chk < 6) {
            if (!Arrays.asList(stringArr).contains((chk + 1) + "")) {
                stringArrNotContains[chk1++] = (chk + 1) + "";
            }
            chk++;
        }
        try {
            DBHelper helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            ContentValues v = new ContentValues();
            v.put("STOP", 1);
            while (i < stringArr.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v, "DAY not in(1,8,15,22,29,36,42) and WEEK_NO = \"" + Integer.parseInt(stringArr[i++].toString()) + "\"", null);
//                Cursor cursor = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set STOP=1 where DAY not in(1,8,15,22,29,36,42) and WEEK_NO=" + Integer.parseInt(stringArr[i].toString()), null);
//                cursor.close();

            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            ContentValues v1 = new ContentValues();
            v1.put("STOP", 0);
            while (i < stringArrNotContains.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v1, "WEEK_NO = \"" + Integer.parseInt(stringArrNotContains[i++].toString()) + "\"", null);
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
