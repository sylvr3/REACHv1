package asu.reach;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper{
    //The Android's default system path of your application database.
    private static String DB_PATH;

    private static String DB_NAME = "REACH_DB";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     */

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = context.getFilesDir().toString() + "/";
        try {
            System.out.println("checking");
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath,
                    null, SQLiteDatabase.OPEN_READWRITE);
            Cursor c = myDataBase.rawQuery("SELECT * FROM STIC",null);
            if(!(c.getCount() > 0)){
                copyDataBase();
            }
        }catch(Exception e){
            e.printStackTrace();
            if(myDataBase == null) {
                try {
                    copyDataBase();
                }catch(Exception ex){
                    e.printStackTrace();
                }
            }
        }
        try {
            openDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void trackEvent(DBHelper helper,String EVENT_TYPE,String EVENT_PLACE){
        try {
            //Log.i("inserted","inserted");
            //helper.copyDataBase();
            //helper.openDataBase();
            SQLiteDatabase db =  helper.getDB();
            ContentValues v1 = new ContentValues();
            v1.put("EVENT_TIMESTAMP",System.currentTimeMillis());
            v1.put("EVENT_TYPE",EVENT_TYPE);
            v1.put("EVENT_PLACE",EVENT_PLACE);
            db.insert("EVENT_TRACKER","EVENT_TIMESTAMP,EVENT_TYPE,EVENT_PLACE", v1);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getCurrentDay(){
        Cursor dayFind = myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE ID = 1",null);
        dayFind.moveToFirst();
        String start = dayFind.getString(dayFind.getColumnIndex("start_date"));
        int currentDay;
        if(start.equals("default")){
            currentDay = -1;
        }else{
            String[] split = start.split("[.]");
            if(split.length == 3) {
                Calendar startDay = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                today.setTimeInMillis(System.currentTimeMillis());
                startDay.set(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1])-1,
                        Integer.parseInt(split[2]), 0, 0, 0);
                currentDay = today.get(Calendar.DAY_OF_YEAR) - startDay.get(Calendar.DAY_OF_YEAR);
                if (currentDay < 0) {
                    currentDay = -currentDay;
                }
            }else{
                currentDay = -1;
            }
        }
        return currentDay;
    }

    public boolean checkAdminPwd(String pwdToBeChecked){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from Administrator where Admin_pwd="+pwdToBeChecked,null);
        while(c.moveToNext()){
            if(c.getString(0).equals(pwdToBeChecked))
                return true;

        }
        return false;
    }


    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    public void copyDataBase() throws IOException{
        System.out.println("Copying DB");
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        File file = new File(outFileName);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        OutputStream myOutput = new FileOutputStream(file);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException{
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    public SQLiteDatabase getDB(){
        return myDataBase;
    }

    public File getFile(){
        try {
            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            InputStream myInput = new FileInputStream(outFileName);

            //Open the empty db as the output stream
            File file = new File(myContext.getExternalFilesDir(null), DB_NAME);

            OutputStream myOutput = new FileOutputStream(file);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

            return file;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void exportToCSV(Cursor c, String fileName/*, Landing parent*/) {
        Log.i("path", "path");
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        File sdCardDir = Environment.getExternalStorageDirectory();
        File saveFile = new File(sdCardDir, fileName);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                c.moveToFirst();
                //
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                //
                bfw.newLine();
                //
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
                    // Toast.makeText(mContext, ""+(i+1)+"",
                    // Toast.LENGTH_SHORT).show();
                    //Log.v("", "" + (i + 1) + "");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            if(j==1){
                                bfw.write(DBHelper.getDate(Long.parseLong(c.getString(j)),"dd/MM/yyyy hh:mm:ss.SSS") + ',');
                            }else {
                                bfw.write(c.getString(j) + ',');
                            }
                        else
                            bfw.write(c.getString(j));
                    }
                    //
                    bfw.newLine();
                }
            }
            //
            Log.i("path", saveFile.getAbsolutePath());
            bfw.flush();
            //
            bfw.close();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"heal@asu.edu"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Here's the Data ! <<TESTING>>>");

            // ENTER THE FILE YOU WANT TO SEND BELOW
            Toast.makeText(myContext,"Exported "+saveFile.getName()+" to "+saveFile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(saveFile));
            myContext.startActivity(Intent.createChooser(intent, "Share using"));

            // Toast.makeText(mContext, "?", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            c.close();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
