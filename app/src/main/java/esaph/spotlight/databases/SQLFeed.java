package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLFeed extends SQLiteOpenHelper
{
    private Context context;
    private static final String TYPE_LIKE = "TL";
    private static final String TYPE_SHARED = "TS";
    private static final String TYPE_WATCHER = "TW";
    private static final String TYPE_COMMENT = "TC";
    private static final String DATABASE_NAME = "Feed.db";

    private static final String tableName = "Feed";
    private static final String feed_Username = "USERNAME"; //Der benutzername vom nutzer der irgendwas in meinem feed zu suchen hat.
    private static final String feed_RequestCode = "RQ";
    private static final String feed_Time = "TIME";
    private static final String feed_Type = "TYPE";
    private static final String feed_PID = "PID";

    private static final String createTable = "create table if not exists " + tableName +
            "(" +
            feed_Username + " TEXT, " +
            feed_RequestCode + " TEXT, " +
            feed_Time + " INTEGER, " +
            feed_Type + " TEXT, " +
            feed_PID + " TEXT)";


    public SQLFeed(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createTable);
    }


    public void removeUserData(String Username)
    {
        try
        {
            SQLiteDatabase db = SQLFeed.super.getWritableDatabase();
            db.delete(tableName, feed_Username + "='" + Username + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropAllData() failed: " + ec);
        }
    }

    public void dropAllData()
    {
        try
        {
            SQLiteDatabase db = SQLFeed.super.getWritableDatabase();
            db.delete(tableName, null, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropAllData() failed: " + ec);
        }
    }



    public void insertNewWatcherAnfrage(String Username, String RequestCode)
    {
        try
        {
            SQLiteDatabase db = SQLFeed.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(feed_Username, (Username));
            contentValues.put(feed_Type, TYPE_WATCHER);
            contentValues.put(feed_RequestCode, RequestCode);
            db.insert(tableName, null, contentValues);
            db.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewWatcherAnfrage: " + ec);
        }
    }



    public void insertNewHot(String username, String PID)
    {
        try
        {
            SQLiteDatabase db = SQLFeed.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(feed_Username, (username));
            contentValues.put(feed_Type, TYPE_LIKE);
            contentValues.put(feed_PID, PID);
            db.insert(tableName, null, contentValues);
            db.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewHot failed: " + ec);
        }
    }



}
