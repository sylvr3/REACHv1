package asu.reach;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.Set;

public class Preferences extends PreferenceActivity /*implements SharedPreferences.OnSharedPreferenceChangeListener */ {

    private MultiSelectListPreference DDProtocolChange,STOPProtocolChange,STICProtocolChange;
    private SharedPreferences sharedPrefs;
    private SQLiteDatabase db;
    private int i = 0;
    private int j = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        addPreferencesFromResource(R.xml.prefs);
        DDProtocolChange = (MultiSelectListPreference) findPreference("DD_week_setting");
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spf.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("DD_week_setting")){
                    dailyDiaryProtocolChange(sharedPreferences,s);
                }
                else if (s.equals("STOP_week_setting")){
                    StopProtocolChange(sharedPreferences,s);
                }
            }
        });

/*
        STOPProtocolChange = (MultiSelectListPreference)findPreference("STOP_week_setting");
        STOPProtocolChange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                StopProtocolChange();
                return true;
            }
        });
*/
    }

    public void dailyDiaryProtocolChange(SharedPreferences pref,String s) {
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

    public void StopProtocolChange(SharedPreferences pref,String s){
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
            v.put("STOP",1);
            while (i < stringArr.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER",v,"DAY not in(1,8,15,22,29,36,42) and WEEK_NO = \"" + Integer.parseInt(stringArr[i++].toString())+ "\"",null);
//                Cursor cursor = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set STOP=1 where DAY not in(1,8,15,22,29,36,42) and WEEK_NO=" + Integer.parseInt(stringArr[i].toString()), null);
//                cursor.close();

            }
            db.close();
            helper = new DBHelper(getApplicationContext());
            db = helper.getDB();
            i = 0;
            ContentValues v1 = new ContentValues();
            v1.put("STOP",0);
            while (i < stringArrNotContains.length) {
                db.update("ADMIN_ACTIVITY_SCHEDULER",v1,"WEEK_NO = \"" + Integer.parseInt(stringArrNotContains[i++].toString())+ "\"",null);
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
