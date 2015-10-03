package asu.reach;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class DBHelper extends SQLiteOpenHelper{
    //The Android's default system path of your application database.
    private static String DB_PATH;

    private static String DB_NAME = "REACH_DB";

    private static List<File> filepaths=new ArrayList<File>();

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public static enum Action {
        STOP,
        STIC,
        DAILY_DIARY,
        WORRYHEADS,
        RELAXATION
    }

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


    public void changeProgressBarLevel(Activity activity){
        try {
            /*This code resets the Bar layouts to zero*/
            RelativeLayout r11 = (RelativeLayout) activity.findViewById(R.id.ddBarLayout);
            r11.getLayoutParams().height = 0 ;
            RelativeLayout r22 = (RelativeLayout) activity.findViewById(R.id.sticBarLayout);
            r22.getLayoutParams().height = 0 ;
            RelativeLayout r33 = (RelativeLayout) activity.findViewById(R.id.stopBarLayout);
            r33.getLayoutParams().height = 0 ;
            RelativeLayout r44 = (RelativeLayout) activity.findViewById(R.id.whBarLayout);
            r44.getLayoutParams().height = 0 ;
            RelativeLayout r55 = (RelativeLayout) activity.findViewById(R.id.relaxBarLayout);
            r55.getLayoutParams().height = 0 ;

            /* This code is written to get the count of activities completed during the week.*/
            Cursor c=myDataBase.rawQuery("SELECT * from PROGRESS_BAR_DATA WHERE ROWID='1'",null);
            c.moveToFirst();
            int stic=c.getInt(0);
            int stop=c.getInt(1);
            int worry=c.getInt(2);
            int relax=c.getInt(3);
            int dd=c.getInt(4);


            Cursor week=myDataBase.rawQuery("SELECT * FROM WEEK_NUMBER_OF_PROTOCOL;",null);
            week.moveToFirst();
            int weekNumber=week.getInt(0);

            /* This code is written to get the threshold count of activities required during the week.
            * */
            Cursor ct=myDataBase.rawQuery("SELECT * from PROGRESS_THRESHOLDS WHERE WEEK_NUMBER="+weekNumber+"",null);
            ct.moveToFirst();
            int sticT=ct.getInt(1);
            int stopT=ct.getInt(2);
            int worryT=ct.getInt(4);
            int relaxT=ct.getInt(5);
            int ddT=ct.getInt(3);

            RelativeLayout rl = (RelativeLayout) activity.findViewById(R.id.ddBarLayout);
            rl.getLayoutParams().height = (dd>0 && ddT> 0) ? 660*dd/ddT : 0 ;
            RelativeLayout r2 = (RelativeLayout) activity.findViewById(R.id.sticBarLayout);
            r2.getLayoutParams().height = (stic > 0 && sticT>0) ? 660*stic/sticT : 0 ;
            RelativeLayout r3 = (RelativeLayout) activity.findViewById(R.id.stopBarLayout);
            r3.getLayoutParams().height = (stop > 0 && stopT>0) ? 660*stop/stopT : 0 ;
            RelativeLayout r4 = (RelativeLayout) activity.findViewById(R.id.whBarLayout);
            r4.getLayoutParams().height = (worry > 0 && worryT>0) ? 660*worry/worryT : 0 ;
            RelativeLayout r5 = (RelativeLayout) activity.findViewById(R.id.relaxBarLayout);
            r5.getLayoutParams().height = (relax > 0 && relaxT>0) ? 660*relax/relaxT : 0 ;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*This function increases the count of the activity when it is completed.*/
    public void setActivityProgressCount(String activityName){
        try {
            Cursor c=myDataBase.rawQuery("SELECT "+activityName+" from PROGRESS_BAR_DATA WHERE ROWID='1'",null);
            c.moveToFirst();
            int count=c.getInt(0);
            count=count+1;
            myDataBase.execSQL("update PROGRESS_BAR_DATA set "+activityName+"="+count+" where ROWID=1");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public Integer getActivityProgressCount(String activityName){
            Cursor c=myDataBase.rawQuery("SELECT * from PROGRESS_BAR_DATA WHERE ROWID='1'",null);
            Integer count=c.getInt(c.getColumnIndex(activityName));
            return count;
    }*/

    /*Formatter functions used to specify the format of the date and time*/
    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("yyyy.MM.dd");
    }
    public static SimpleDateFormat timeFormatter() { return new SimpleDateFormat("HH:mm");
    }
    /*Function to set the protocol date*/
    public boolean setStartDateForProtocol(int year,int month,int day){
        try {
            Calendar selected = new GregorianCalendar(year, month, day);
            String dateString = dateFormatter().format(selected.getTime());
            ContentValues valueMap = new ContentValues();
            valueMap.put("start_date",dateString);
            int rowsAffected=myDataBase.update("DATE_TIME_SET",valueMap,"id=1",null);
            if(rowsAffected>0)
                return true;
            else
                return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*Function to get the preselected value of protocol date*/
    public Calendar getStartDateForProtocol(){
        Calendar cal=null;
        try {
            Cursor dateOfProtocol=myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE id=1;;",null);
            dateOfProtocol.moveToFirst();
            String date=dateOfProtocol.getString(0);
            //Convert the string date returned by cursor to Date and then to calendar which will be returned.
            if(date.equalsIgnoreCase("default")){
                return cal;
            }else{
                DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                cal  = Calendar.getInstance();
                cal.setTime(df.parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    /*Function to set the notification time*/
    public boolean setTimeForNotifications(int hour,int minute){
        try {
            Calendar selected = new GregorianCalendar(1970,0,1,hour,minute);
            String notificationTimeString = timeFormatter().format(selected.getTime());
            ContentValues valueMap = new ContentValues();
            valueMap.put("start_time",notificationTimeString);
            int rowsAffected=myDataBase.update("DATE_TIME_SET",valueMap,"id=1",null);
            if(rowsAffected>0)
                return true;
            else
                return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*Function to get the preselected value of protocol date*/
    public Calendar getTimeForNotifications(){
        Calendar cal=null;
        try {
            Cursor timeOfNotification=myDataBase.rawQuery("SELECT start_time FROM DATE_TIME_SET WHERE id=1;;",null);
            timeOfNotification.moveToFirst();
            String time=timeOfNotification.getString(0);
            //Convert the string time returned by cursor to Date and then to calendar which will be returned.
            if(time.equalsIgnoreCase("default")){
                return cal;
            }else{
                DateFormat df = new SimpleDateFormat("HH:mm");
                cal  = Calendar.getInstance();
                cal.setTime(df.parse(time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }
    public void setTrickReleaseDays(HashSet<String> setOfDays){
        try {
            if(setOfDays.size()==2) {
                String arrayOfTwoDays[]= new String[2];
                int i=0;
                for(String s:setOfDays){
                    arrayOfTwoDays[i]=s;
                    i++;
                }
                ContentValues v = new ContentValues();
                v.put("start_date", arrayOfTwoDays[0]);
                myDataBase.update("DATE_TIME_SET", v, "id=2", null);
                v = new ContentValues();
                v.put("start_date", arrayOfTwoDays[1]);
                myDataBase.update("DATE_TIME_SET", v, "id=3", null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<Days> getSelectedTrickReleaseDays(){
        ArrayList<Days> daysArrayList= new ArrayList<Days>();
        try {
            String dayArray[]={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
            Cursor day_one = myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE id =2;",null);
            day_one.moveToFirst();
            Cursor day_two = myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE id =3;",null);
            day_two.moveToFirst();

            for(int index=0;index<dayArray.length;index++){
                if(dayArray[index].equalsIgnoreCase(day_one.getString(0))){
                    Days day= new Days(dayArray[index],true);
                    daysArrayList.add(day);
                }else if(dayArray[index].equalsIgnoreCase(day_two.getString(0))){
                    Days day= new Days(dayArray[index],true);
                    daysArrayList.add(day);
                }else{
                    Days day= new Days(dayArray[index],false);
                    daysArrayList.add(day);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  daysArrayList;
    }

    public void releaseTrick(){
        try{
            Cursor dayFind = myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE id =2;",null);
            Cursor dayFind2 = myDataBase.rawQuery("SELECT start_date FROM DATE_TIME_SET WHERE id =3;",null);
            dayFind.moveToFirst();
            dayFind2.moveToFirst();
            String startDay = dayFind.getString(dayFind.getColumnIndex("start_date"));
            String startDay2 = dayFind2.getString(dayFind2.getColumnIndex("start_date"));
            String weekDay;
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

            Calendar calendar = Calendar.getInstance();

            weekDay = dayFormat.format(calendar.getTime());
            if(weekDay.equalsIgnoreCase(startDay) || weekDay.equalsIgnoreCase(startDay2)){
                Cursor dayFromDb= myDataBase.rawQuery("SELECT TRICK_ID FROM BLOB_TRICK_LOCKED_CHECK;",null);
                SimpleDateFormat saveTodayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar cal = Calendar.getInstance();
                String today;
                today = saveTodayFormat.format(cal.getTime());
                Cursor dateUnlocked= myDataBase.rawQuery("SELECT DATE FROM LAST_TRICK_UNLOCK;",null);
                if(dayFromDb.moveToFirst()) {
                    dateUnlocked.moveToFirst();
                    String lastUnlocked=dateUnlocked.getString(dateUnlocked.getColumnIndex("DATE"));
                    Log.i("lastUnlocked",lastUnlocked);
                    Log.i("today",today);
                    if(lastUnlocked.equalsIgnoreCase(today)){
                        Log.i("Already unlocked today","true");
                    }else{
                        int rowId = dayFromDb.getInt(dayFromDb.getColumnIndex("TRICK_ID"));
                        myDataBase.delete("BLOB_TRICK_LOCKED_CHECK", "TRICK_ID" +  "=" + rowId, null);
                        ContentValues v = new ContentValues();
                        v.put("DATE",today);
                        myDataBase.update("LAST_TRICK_UNLOCK", v, null, null);
                    }
                }
                //Log.i("Status","Released Trick");
                dayFromDb= myDataBase.rawQuery("SELECT TRICK_ID FROM BLOB_TRICK_LOCKED_CHECK;",null);
                Log.i("ID",Integer.toString(dayFromDb.getCount()));
            }else{
                Log.i("Day1",startDay);
                Log.i("Day2",startDay2);
                Log.i("weekDay",weekDay);
                Log.i("Status","Do Not Release Trick");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkIfLocked(int id){
        Cursor find = myDataBase.rawQuery("SELECT * FROM BLOB_TRICK_LOCKED_CHECK WHERE TRICK_ID ="+id+";",null);
        if(find.getCount()>0){
            return true;
        }else{
            return false;
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
            //db.insert("EVENT_TRACKER","EVENT_TIMESTAMP,EVENT_TYPE,EVENT_PLACE", v1);
            if(EVENT_TYPE.equalsIgnoreCase("APP_STARTED") || EVENT_TYPE.contains("STOP_P_DONE") || EVENT_TYPE.contains("STOP_O_DONE") || EVENT_TYPE.contains("STOP_T_DONE")  || EVENT_TYPE.contains("STOP_S_DONE") ||  EVENT_TYPE.contains("WHAT") || EVENT_TYPE.contains("RATING") || EVENT_TYPE.contains("DID") || EVENT_TYPE.contains("THOUGHTS") || EVENT_TYPE.contains("COMPLETED") || EVENT_TYPE.contains("RELAXATION") || EVENT_TYPE.contains("ADMIN_SETTINGS") || EVENT_TYPE.equalsIgnoreCase("WORRY_HEADS")   || EVENT_TYPE.equalsIgnoreCase("DAILY_DIARY") || EVENT_TYPE.equalsIgnoreCase("BLOB_TRICKS") || EVENT_TYPE.equalsIgnoreCase("STIC_STARTED") || EVENT_TYPE.equalsIgnoreCase("STOP_STARTED")){
                //db.insert("HIGH_LEVEL_EVENT_TRACKER","EVENT_TIMESTAMP,EVENT_TYPE,EVENT_PLACE", v1);
                db.insert("EVENT_TRACKER","EVENT_TIMESTAMP,EVENT_TYPE,EVENT_PLACE", v1);
            }
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
                    currentDay = -currentDay; // new year
                }
                currentDay++; // add a day so today = day 1
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
        int columnIndexForTimeStamp=0;
        FileWriter fw;
        BufferedWriter bfw;
        File sdCardDir = Environment.getExternalStorageDirectory();
        File saveFile = new File(sdCardDir, fileName);
        DBHelper.filepaths.add(saveFile);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                c.moveToFirst();
                //
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1) {
                        if (c.getColumnName(i).equalsIgnoreCase("TIMESTAMP") || c.getColumnName(i).equalsIgnoreCase("EVENT_TIMESTAMP")) {
                            columnIndexForTimeStamp = i;
                            bfw.write(c.getColumnName(i) + ',');
                        }else {
                            if (!c.getColumnName(i).equalsIgnoreCase("WRONG_FLAG"))
                                bfw.write(c.getColumnName(i) + ',');
                        }
                    }
                    else {
                        bfw.write(c.getColumnName(i));
                    }
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
                            if(j==columnIndexForTimeStamp){
                                bfw.write(DBHelper.getDate(Long.parseLong(c.getString(j)),"dd/MM/yyyy hh:mm") + ',');
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


            // Toast.makeText(mContext, "?", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    public void sendData(){
        /*
        * Intent intent = new Intent();
          intent.setAction(Intent.ACTION_SEND_MULTIPLE);
          intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
          intent.setType("image/jpeg"); /* This example is sharing jpeg images.

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String path : filesToSend /* List of the files you want to send ) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        * */

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(myContext).getAccounts();
        String senderEmailId="";
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                senderEmailId = account.name;
            }
        }

        TelephonyManager tm = (TelephonyManager) myContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{senderEmailId});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here's the Data- Device ID:"+deviceId);
        // ENTER THE FILE YOU WANT TO SEND BELOW
        //Toast.makeText(myContext,"Exported "+saveFile.getName()+" to "+saveFile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();

        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(saveFile));

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(File file : DBHelper.filepaths ) {
            //File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        myContext.startActivity(Intent.createChooser(intent, "Share using"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkActivity(Action a){
        int day = getCurrentDay();
        Cursor c = myDataBase.rawQuery("SELECT * FROM ADMIN_ACTIVITY_SCHEDULER WHERE DAY = "
                + day,null);
        Cursor d = myDataBase.rawQuery("SELECT * FROM USER_ACTIVITY_TRACK WHERE DAY = "
                + day,null);
        c.moveToFirst();
        d.moveToFirst();
        switch (a){
            case DAILY_DIARY:{
                if(c.getInt(c.getColumnIndex("DIARY_EVENT1")) == 1
                        && d.getInt(d.getColumnIndex("DIARY_EVENT1")) != 1){
                    return true;
                }
                if(c.getInt(c.getColumnIndex("DIARY_EVENT2")) == 1
                        && d.getInt(d.getColumnIndex("DIARY_EVENT2")) != 1){
                    return true;
                }
                if(c.getInt(c.getColumnIndex("SUD_SCALE_EVENT")) == 1
                        && d.getInt(d.getColumnIndex("SUD_SCALE_EVENT")) != 1){
                    return true;
                }
                break;
            }
            case RELAXATION:{
                if(c.getInt(c.getColumnIndex("RELAXATION")) == 1
                        && d.getInt(d.getColumnIndex("RELAXATION")) != 1){
                    return true;
                }
                break;
            }
            case WORRYHEADS:{
                if(c.getInt(c.getColumnIndex("STOP_WORRYHEADS")) == 1
                        && d.getInt(d.getColumnIndex("STOP_WORRYHEADS")) != 1){
                    return true;
                }
                break;
            }
            case STIC:{
                if(c.getInt(c.getColumnIndex("STIC")) > 0
                        && d.getInt(d.getColumnIndex("STIC")) != 1){
                    return true;
                }
                break;
            }
            case STOP:{
                if(c.getInt(c.getColumnIndex("STOP")) == 1
                        && d.getInt(d.getColumnIndex("STOP")) != 1){
                    return true;
                }
                break;
            }
        }
        return false;
    }

}
