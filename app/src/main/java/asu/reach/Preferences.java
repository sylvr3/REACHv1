package asu.reach;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;

public class Preferences extends PreferenceActivity /*implements SharedPreferences.OnSharedPreferenceChangeListener */ {

    private MultiSelectListPreference DDpref;
    private SharedPreferences sharedPrefs;
    private SQLiteDatabase db;
    private int i = 0;
    private int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        DDpref = (MultiSelectListPreference) findPreference("DD_week_setting");
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Set<String> stringSet = sharedPreferences.getStringSet("DD_week_setting", null);
                String[] stringArr = stringSet.toArray(new String[stringSet.size()]);
                String[] stringArrNotContains = new String[6-stringArr.length];
                int chk=0;
                int chk1=0;
                while(chk<6) {
                    if (!Arrays.asList(stringArr).contains((chk+1)+"")) {
                        stringArrNotContains[chk1++] = (chk + 1) + "";
                    }
                    chk++;
                }
                try {
                    DBHelper helper = new DBHelper(getBaseContext());
                    db = helper.getDB();
                    i=0;
                    while (i < stringArr.length) {
                        Cursor cursor = db.rawQuery("update ADMIN_ACTIVITY_SCHEDULER set DIARY_EVENT1=1 where (DIARY_EVENT1=1 or DIARY_EVENT2=1) and WEEK_NO=" + Integer.parseInt(stringArr[i].toString()), null);
                        cursor.close();

                        i += 1;
                    }
                    db.close();
                    helper = new DBHelper(getBaseContext());
                    db = helper.getDB();
                    i=0;
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
        });
    }

}
