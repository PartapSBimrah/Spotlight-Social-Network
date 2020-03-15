package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.services.UploadService.UploadPost;

public class SQLUploads extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";

    private static final String tableNameNeedToUpload = "Posts";
    private static final String column_ID = "_ID";
    private static final String column_type = "Type"; //Text, Image, Or Video
    private static final String column_rawDataHQ = "ImageDataOriginal";
    private static final String column_PID = "ID";
    private static final String column_time = "Time";
    private static final String column_uploaded = "Uploaded";
    private static final String column_sendTo = "Retrievers"; // CALLED AS WAMP
    private static final String column_HashtagsJsonArray = "HT";
    private static final String column_Description = "Description";

    private static final String queryCreateTableNeedToUpload = "create table if not exists " + tableNameNeedToUpload + "(" +
            column_ID + " INTEGER PRIMARY KEY autoincrement, " +
            column_type + " SHORT, " +
            column_rawDataHQ + " TEXT, " +
            column_PID + " TEXT, " +
            column_HashtagsJsonArray + " TEXT, " +
            column_uploaded + " INTEGER, " +
            column_time + " INTEGER, " +
            column_sendTo + " TEXT, " +
            column_Description + " TEXT)";

    public SQLUploads(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(queryCreateTableNeedToUpload);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }

    public void onCreateDatabasesExternalCall(SQLiteDatabase db)
    {
        db.execSQL(queryCreateTableNeedToUpload);
    }

    public long preparePostVideo(
            String PID,
            File rawVideoFile,
            long timeInMillis,
            JSONArray WAMP,
            String Beschreibung,
            ArrayList<EsaphHashtag> selectingListHashtags)
    {
        try
        {
            ContentValues cv = new ContentValues();
            cv.put(column_type, CMTypes.FVID);
            cv.put(column_rawDataHQ, rawVideoFile.getAbsolutePath());
            cv.put(column_PID, PID);
            cv.put(column_time, timeInMillis);

            JSONArray jsonArray = new JSONArray();
            int selectedSize = selectingListHashtags.size();
            for(int counter = 0; counter < selectedSize; counter++)
            {
                jsonArray.put(selectingListHashtags.get(counter).getHashtagName());
            }

            cv.put(column_HashtagsJsonArray, jsonArray.toString());

            cv.put(column_uploaded, 0);

            cv.put(column_sendTo, WAMP.toString());
            cv.put(column_Description, Beschreibung);

            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            long postID_ = db.insert(tableNameNeedToUpload, null, cv);

            Log.i(getClass().getName(), "Post prepared.");

            return postID_;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "preparePostVideo() failed: " + ec);
            if(rawVideoFile != null)
            {
                StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT, context, PID);
            }
            return -1;
        }
    }

    public long preparePostImage(String PID,
                                 File rawImageFile,
                                   long timeinmillis,
                                 JSONArray WAMP,
                                   String Beschreibung,
                                   ArrayList<EsaphHashtag> selectingListHashtags)
    {
        try
        {
            ContentValues cv = new ContentValues();
            cv.put(column_type, CMTypes.FPIC);

            cv.put(column_rawDataHQ, rawImageFile.getAbsolutePath());

            cv.put(column_PID, PID);
            cv.put(column_time, timeinmillis);
            cv.put(column_uploaded, 0);

            cv.put(column_sendTo, WAMP.toString());
            cv.put(column_Description, Beschreibung);
            JSONArray jsonArray = new JSONArray();
            int selectedSize = selectingListHashtags.size();
            for(int counter = 0; counter < selectedSize; counter++)
            {
                jsonArray.put(selectingListHashtags.get(counter).getHashtagName());
            }
            cv.put(column_HashtagsJsonArray, jsonArray.toString());

            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            long postID_ = db.insert(tableNameNeedToUpload, null, cv);
            Log.i(getClass().getName(), "Post prepared.");
            return postID_;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "preparePostImage() failed: " + ec);

            if(rawImageFile != null)
            {
                StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT, context, rawImageFile.getName());
            }

            return -1;
        }
    }

    public UploadPost getPostByID(long ID)
    {
        try
        {
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableNameNeedToUpload + " WHERE " + column_ID + "=" + ID,null);

            if(cursor.moveToFirst())
            {
                ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(column_HashtagsJsonArray)));
                for(int countArr = 0; countArr < jsonArray.length(); countArr++)
                {
                    esaphHashtags.add(new EsaphHashtag(jsonArray.getString(countArr),
                            null,
                            0));
                }

                UploadPost uploadPost = new UploadPost(
                        cursor.getLong(cursor.getColumnIndex(column_ID)),
                        cursor.getShort(cursor.getColumnIndex(column_type)),
                    cursor.getString(cursor.getColumnIndex(column_PID)),
                        getNamesToSend(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                    cursor.getString(cursor.getColumnIndex(column_Description)),
                    cursor.getLong(cursor.getColumnIndex(column_time)),
                    new JSONArray(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                        esaphHashtags);
                cursor.close();
                return uploadPost;
            }

            Log.i(getClass().getName(), "getPostByID() failed: null object");
            db.close();
            cursor.close();
            return null;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLifeCloudUploadByID() failed: " + ec);
            return null;
        }
    }



    public List<UploadPost> getPostsThatNotBeenUploadedYet(int startFrom)
    {
        try
        {
            List<UploadPost> uploadPosts = new ArrayList<>();
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableNameNeedToUpload + " WHERE " + column_uploaded + "=0" + " ORDER BY " + column_time + " LIMIT " +
                    startFrom + ", 10", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(column_HashtagsJsonArray)));
                    for(int countArr = 0; countArr < jsonArray.length(); countArr++)
                    {
                        esaphHashtags.add(new EsaphHashtag(jsonArray.getString(countArr),
                                null,
                                0));
                    }

                    uploadPosts.add(new UploadPost(
                            cursor.getLong(cursor.getColumnIndex(column_ID)),
                            cursor.getShort(cursor.getColumnIndex(column_type)),
                            cursor.getString(cursor.getColumnIndex(column_PID)),
                            getNamesToSend(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                            cursor.getString(cursor.getColumnIndex(column_Description)),
                            cursor.getLong(cursor.getColumnIndex(column_time)),
                            new JSONArray(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                            esaphHashtags));
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return uploadPosts;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLifeCloudPostsThatNotBeenUploadedYet() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public List<UploadPost> getPostsThatNotBeenUploadedYetLimitedByUsername(int startFrom, String Username)
    {
        try
        {
            List<UploadPost> uploadPosts = new ArrayList<>();
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT "
                    + column_type + ", "
                    + column_PID + ", "
                    + column_Description + ", "
                    + column_sendTo + ", "
                    + column_time +
                    " FROM " + tableNameNeedToUpload + " WHERE " + column_uploaded + "=" + 0 + " ORDER BY " + column_time + " LIMIT " + startFrom + ",5", null);

            if (cursor.moveToFirst())
            {
                do
                {
                    if(cursor.getString(cursor.getColumnIndex(column_sendTo)).contains(Username))
                    {
                        ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(column_HashtagsJsonArray)));
                        for(int countArr = 0; countArr < jsonArray.length(); countArr++)
                        {
                            esaphHashtags.add(new EsaphHashtag(jsonArray.getString(countArr),
                                    null,
                                    0));
                        }

                        uploadPosts.add(new UploadPost(
                                cursor.getLong(cursor.getColumnIndex(column_ID)),
                                cursor.getShort(cursor.getColumnIndex(column_type)),
                                cursor.getString(cursor.getColumnIndex(column_PID)),
                                getNamesToSend(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                                cursor.getString(cursor.getColumnIndex(column_Description)),
                                cursor.getLong(cursor.getColumnIndex(column_time)),
                                new JSONArray(cursor.getString(cursor.getColumnIndex(column_sendTo))),
                                esaphHashtags));
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return uploadPosts;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getPostsThatNotBeenUploadedYetLimitedByUsername() failed: " + ec);
            return new ArrayList<>();
        }
    }

    private String getNamesToSend(String s) throws JSONException
    {
        SQLFriends sqlWatcher = new SQLFriends(context);
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = new JSONArray(s);

        if(jsonArray.length() > 0)
        {
            JSONArray jsonArrayUsers = jsonArray.getJSONArray(0);

            if(jsonArrayUsers.length() > 0)
            {
                for(int counter = 0; counter < jsonArrayUsers.length(); counter++) //FÜR JEDEN EINZELNEN NUTZER
                {
                    JSONObject jsonObjectReceiver = jsonArrayUsers.getJSONObject(counter);
                    stringBuilder.append(sqlWatcher.lookUpUsername(jsonObjectReceiver.getLong("REC_ID")));
                    if((counter+1) < jsonArrayUsers.length())
                    {
                        stringBuilder.append(", ");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }



    public File getFileToUploadHQ(String PID)
    {
        try
        {
            File file = null;
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + column_rawDataHQ + " FROM " + tableNameNeedToUpload + " WHERE " + column_PID + "='" + PID + "'", null);

            if(cursor.moveToFirst())
            {
                file = new File(cursor.getString(cursor.getColumnIndex(column_rawDataHQ)));
            }
            cursor.close();

            return file;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getFileToUploadHQ() failed: " + ec);
            return null;
        }
    }

    public void removeSinglePostCompletly(String PID) //Löscht komplett den eintrag inklusive bild.
    {
        try
        {
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT, context, PID);
            db.delete(tableNameNeedToUpload, column_PID + "='" + PID + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removeSinglePostCompletly() failed: " + ec);
        }
    }

    public void removePreparedPostsFromDataBaseAndStorage()
    {
        try
        {
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + tableNameNeedToUpload + " WHERE " + column_uploaded + "=?", new String[]{Integer.toString(-1)});
            if(cursor.moveToFirst())
            {
                do {
                    try
                    {
                        String PID = cursor.getString(cursor.getColumnIndex(column_PID));
                        StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT,
                                context, PID);
                    }
                    catch (Exception ec)
                    {
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();

            db.delete(tableNameNeedToUpload, column_uploaded + "=" + -1, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePreparedPostsFromDataBaseAndStorage() failed: " + ec);
        }
    }


    public void dropTables()
    {
        try
        {
            SQLiteDatabase db = SQLUploads.super.getWritableDatabase();
            db.delete(tableNameNeedToUpload, null, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTables() failed: " + ec);
        }
    }

}
