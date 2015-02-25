package asu.reach;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

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
        if(!checkDataBase()){
            try {
                String myPath = DB_PATH + DB_NAME;
                myDataBase = SQLiteDatabase.openDatabase(myPath,
                        null, SQLiteDatabase.OPEN_READWRITE);
                if(myDataBase == null) {
                    System.out.println("Copying DB");
                    copyDataBase();
                }
            }catch(Exception e){
                e.printStackTrace();
                if(myDataBase == null) {
                    System.out.println("Copying DB");
                    try {
                        copyDataBase();
                    }catch(Exception ex){
                        e.printStackTrace();
                    }

                }
            }
        }
        try {
            openDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // MOVE THIS SOMEWHERE ELSE

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

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
