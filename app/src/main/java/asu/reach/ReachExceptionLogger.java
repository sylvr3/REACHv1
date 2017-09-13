package asu.reach;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.Date;

import asu.reach.DBHelper;


public class ReachExceptionLogger {

    private DBHelper helper;
    private SQLiteDatabase db;

    public ReachExceptionLogger(Context context) {
        helper = new DBHelper(context);
        db = helper.getDB();
        db.execSQL("CREATE TABLE IF NOT EXISTS ERROR (ACTIVITY TEXT, INSERT_DATE TEXT, STACK_TRACE TEXT);");
    }

    public void logError(String activity, Exception e) {
        try {
            String hardwareInfo = "Build Model " + Build.MODEL + "\n" + "Manufacturer " + Build.MANUFACTURER + "\n" + "SDK Version " + Build.VERSION.SDK_INT + "\n" + "";
            String stackTrace = hardwareInfo + e.getMessage() + "\n" + e.getStackTrace().toString();
            db.execSQL("INSERT INTO ERROR (ACTIVITY, INSERT_DATE , STACK_TRACE ) VALUES ('" + activity + "', '" + new Date().toString() + "', '" + stackTrace + "');");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getMessage() + "\n" + ex.getStackTrace().toString());
        }
    }
}
