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
import java.util.Calendar;
import java.util.Set;

public class Preferences extends PreferenceActivity /*implements SharedPreferences.OnSharedPreferenceChangeListener */ {

    private MultiSelectListPreference DDProtocolChange, STOPProtocolChange, STICProtocolChange, scheduleTricks;
    private DatePreference datePref;
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
        datePref = (DatePreference)findPreference("initialStartDate");
        Calendar cal = Calendar.getInstance();
        int hour_of_day = cal.get(Calendar.HOUR_OF_DAY);
        int min_of_day = cal.get(Calendar.MINUTE);
        long systemTime = (hour_of_day * 3600) + (min_of_day * 60);

        DBHelper helper1 = new DBHelper(getApplicationContext());
        db = helper1.getDB();
        Cursor c1 = db.rawQuery("select start_date from DATE_TIME_SET where id=1", null);
        c1.moveToFirst();
        String ALLprotocolStartDate = c1.getString(0);
        c1.close();
        db.close();
        /*try {
            if (!ALLprotocolStartDate.equals("default")) {
                DateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                Date ALLProtocolInDateFormat = format.parse(ALLprotocolStartDate);
                Calendar c = Calendar.getInstance();
                Date currDate = c.getTime();
                if (ALLProtocolInDateFormat.getTime() < currDate.getTime())
                    datePref.setEnabled(false);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }*/

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
                } else if (s.equals("STOP_worryhead_week_setting")) {
                    StopWorryHeadProtocolChange(sharedPreferences, s);
                } else if (s.equals("Week1_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 1);
                } else if (s.equals("Week2_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 2);
                } else if (s.equals("Week3_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 3);
                } else if (s.equals("Week4_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 4);
                } else if (s.equals("Week5_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 5);
                } else if (s.equals("Week6_STIC_protocol_setting")) {
                    SticProtocolChange(sharedPreferences, s, 6);
                } else if (s.equals("teacherPIN")) {
                    updateTeacherPIN(sharedPreferences, s);
                } else if(s.equals("scheduled_release_tricks")){
                    setScheduleForTricks(sharedPreferences,s);
                } else if(s.equals("Relaxation_week_setting")){
                    setScheduleForRelaxation(sharedPreferences, s);
                }
            }
        });
    }

    public void setScheduleForRelaxation(SharedPreferences pref, String s){
        Set<String> stringSet = pref.getStringSet("Relaxation_week_setting", null);
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
            v.put("RELAXATION", 1);
            while (i < stringArr.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v,"WEEK_NO = \"" + Integer.parseInt(stringArr[i++].toString())+ "\"", null);
            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            ContentValues v1 = new ContentValues();
            v1.put("RELAXATION", 0);
            while (i < stringArrNotContains.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v1, "WEEK_NO = \"" + Integer.parseInt(stringArrNotContains[i++].toString()) + "\"", null);
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setScheduleForTricks(SharedPreferences pref, String s){
        Set<String> stringSet = pref.getStringSet("scheduled_release_tricks", null);
        String[] stringArr = stringSet.toArray(new String[stringSet.size()]);
        try {
            if(stringArr.length == 2) {
                DBHelper helper = new DBHelper(getApplicationContext());
                db = helper.getDB();
                ContentValues v = new ContentValues();
                v.put("start_date", stringArr[0]);
                db.update("DATE_TIME_SET", v, "id=2", null);
                v = new ContentValues();
                v.put("start_date", stringArr[1]);
                db.update("DATE_TIME_SET", v, "id=3", null);
                db.close();
                helper.close();
            }else{
                Toast.makeText(this, "Please select\ntwo days ONLY.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.i("Exception occured", "Exception occured");
            e.printStackTrace();
        }
    }

//    public void updateSAFETimerSetting(SharedPreferences pref,String s) {
//        String newTime = pref.getString("SAFEEyeTrackingTimer","5");
//
//    }

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
            Toast.makeText(getApplicationContext(),"Try again with 40XX Series",
                    Toast.LENGTH_LONG).show();
    }

    public void callExportCSVFunction(/*SharedPreferences preferences, String s*/) {
        try {
            DBHelper helper = new DBHelper(this);
//            helper.trackEvent(helper,"RELAXATION","LANDING_PAGE");
            db = helper.getDB();
            Cursor EVENT_TRACKER = db.rawQuery("select * from EVENT_TRACKER;", null);
            helper.exportToCSV(EVENT_TRACKER, "REACH_DATA_EVENT_TRACKER.csv"/*, getApplicationContext()*/);

            Cursor DAILY_DIARY_COMPLETION = db.rawQuery("select * from DAILY_DIARY_COMPLETION;", null);
            helper.exportToCSV(DAILY_DIARY_COMPLETION, "DAILY_DIARY_COMPLETION.csv"/*, getApplicationContext()*/);

            Cursor STIC_COMPLETION = db.rawQuery("select * from STIC_COMPLETION;", null);
            helper.exportToCSV(STIC_COMPLETION, "STIC_COMPLETION.csv"/*, getApplicationContext()*/);

            Cursor WORRYHEADS_COMPLETION = db.rawQuery("select * from WORRYHEADS_COMPLETION;", null);
            helper.exportToCSV(WORRYHEADS_COMPLETION, "WORRYHEADS_COMPLETION.csv"/*, getApplicationContext()*/);

            Cursor STOP = db.rawQuery("select * from STOP;", null);
            helper.exportToCSV(STOP, "STOP.csv"/*, getApplicationContext()*/);
            helper.sendData();

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
            String listToPopulate = pref.getString("Week" + week_no + "_STIC_protocol_setting", null);
            v = new ContentValues();
            v.put("STIC", Integer.parseInt(listToPopulate));
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
                Cursor cursor = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set " +
                        "DIARY_EVENT1=1 where (DIARY_EVENT1=1 or DIARY_EVENT2=1) and WEEK_NO="
                        + Integer.parseInt(stringArr[i].toString()), null);
                cursor.close();

                i += 1;
            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            while (i < stringArrNotContains.length) {
                Cursor cursor1 = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set " +
                        "DIARY_EVENT1=0 where WEEK_NO="
                        + Integer.parseInt(stringArrNotContains[i].toString()), null);
                cursor1.close();
                i += 1;
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void StopWorryHeadProtocolChange(SharedPreferences pref,String s){
        Set<String> stringSet = pref.getStringSet("STOP_worryhead_week_setting", null);
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
            v.put("STOP_WORRYHEADS", 1);
            while (i < stringArr.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v,"WEEK_NO = \"" + Integer.parseInt(stringArr[i++].toString())+ "\"", null);
            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            ContentValues v1 = new ContentValues();
            v1.put("STOP_WORRYHEADS", 0);
            while (i < stringArrNotContains.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER", v1, "WEEK_NO = \"" + Integer.parseInt(stringArrNotContains[i++].toString()) + "\"", null);
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
                db.update("ADMIN_ACTIVITY_SCHEDULER", v, "DAY not in(1,8,15,22,29,36,42) " +
                        "and WEEK_NO = \"" + Integer.parseInt(stringArr[i++].toString())
                        + "\"", null);
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
