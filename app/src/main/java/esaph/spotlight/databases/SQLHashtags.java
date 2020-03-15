/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemImage;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemMainMoments;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemVideo;

public class SQLHashtags extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";
    private static final String TABLE_NAME = "HASHTAGS";


    private static final String TAG_HASHTAG_NAME = "TAG_NAME";
    private static final String TAG_ID = "ID";
    private static final String TAG_POST_FROM = "PF";


    private static final String createTableHashtags = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME +
            "(" +
            TAG_HASHTAG_NAME + " TEXT, " +
            TAG_ID + " INTEGER, " +
            TAG_POST_FROM + " INTEGER)";


    public SQLHashtags(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }

    public void dropTableHashtags()
    {
        try
        {
            SQLiteDatabase db = SQLHashtags.super.getWritableDatabase();
            Log.i(getClass().getName(), "DROPPING DOWN");
            db.delete(TABLE_NAME, null, null);
            db.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTableHashtags(): failed: " + ec);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableHashtags);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }

    public List<Object> getMostUsedHashtags()
    {
        SQLChats sqlChats = new SQLChats(context);
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            List<Object> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(" + TAG_HASHTAG_NAME + "), " + TAG_HASHTAG_NAME +
                    ", " + TAG_ID +
                    " FROM " + TABLE_NAME
                    + " WHERE 1 GROUP BY " + TAG_HASHTAG_NAME + " ORDER BY COUNT(" + TAG_HASHTAG_NAME + ") DESC"  + " LIMIT 0,5", null);

            if(cursor.moveToFirst())
            {
                do {
                    list.add(new EsaphHashtag(cursor.getString(cursor.getColumnIndex(TAG_HASHTAG_NAME)),
                            sqlChats.getPostByInternId(cursor.getLong(cursor.getColumnIndex(TAG_ID))),
                            cursor.getInt(0)));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getMostUsedHashtags() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            sqlChats.close();
        }
    }

    private static final String QUERY_GetLatestHashtag =
            "SELECT COUNT(" + TAG_HASHTAG_NAME + "), " + TAG_HASHTAG_NAME +
                    ", " + TAG_ID +
                    " FROM " + TABLE_NAME
                    + " WHERE 1 GROUP BY " + TAG_HASHTAG_NAME + " ORDER BY COUNT(" + TAG_HASHTAG_NAME + ") DESC"  + " LIMIT 1";

    public List<EsaphHashtag> getLatestHashtag()
    {
        List<EsaphHashtag> list = new ArrayList<>();
        //Must return a list, for the loading algorythm, so its preventing that a null object is been passed.
        SQLChats sqlChats = new SQLChats(context);
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(QUERY_GetLatestHashtag, null);

            if(cursor.moveToFirst())
            {
                list.add(new EsaphHashtag(cursor.getString(cursor.getColumnIndex(TAG_HASHTAG_NAME)),
                        sqlChats.getPostByInternId(cursor.getLong(cursor.getColumnIndex(TAG_ID))),
                        cursor.getInt(0)));
            }

            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLatestHashtag() failed: " + ec);
        }
        finally
        {
            sqlChats.close();
        }

        return list;
    }

    public List<Object> getAllHashtagLimited(int startFrom)
    {
        SQLChats sqlChats = new SQLChats(context);
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            List<Object> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(" + TAG_HASHTAG_NAME + "), " + TAG_HASHTAG_NAME +
                    ", " + TAG_ID +
                    " FROM " + TABLE_NAME
            + " WHERE 1 GROUP BY " + TAG_HASHTAG_NAME + " ORDER BY COUNT(" + TAG_HASHTAG_NAME + ") DESC"  + " LIMIT " + startFrom + ", 10", null);

            if(cursor.moveToFirst())
            {
                do {
                    list.add(new EsaphHashtag(cursor.getString(cursor.getColumnIndex(TAG_HASHTAG_NAME)),
                            sqlChats.getPostByInternId(cursor.getLong(cursor.getColumnIndex(TAG_ID))),
                            cursor.getInt(0)));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllHashtagLimited() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            sqlChats.close();
        }
    }


    public int getCountHashtags()
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            List<Object> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT 1 FROM " + TABLE_NAME
                    + " WHERE 1 GROUP BY " + TAG_HASHTAG_NAME, null);

            int count = 0;

            if(cursor.moveToFirst())
            {
                count = cursor.getCount();
            }

            cursor.close();
            return count;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getCountHashtags() failed: " + ec);
            return 0;
        }
    }


    public List<Object> getAllPostingsContainsHashtag(int startFrom, String HashtagName)
    {
        SQLChats sqlChats = new SQLChats(context);
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            List<Object> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + TAG_ID +
                    " FROM " + TABLE_NAME
                    + " WHERE " + TAG_HASHTAG_NAME + "='" + HashtagName + "' LIMIT " + startFrom + ", 10", null);

            if(cursor.moveToFirst())
            {
                do {
                    //No group by is needed, because it gets only a single post.
                    ConversationMessage conversationMessage = sqlChats.getPostByInternId(cursor.getLong(cursor.getColumnIndex(TAG_ID)));
                    if(conversationMessage != null)
                    {
                        list.add(conversationMessage);
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllPostingsContainsHashtag() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            sqlChats.close();
        }
    }

    public ArrayList<EsaphHashtag> getHashtagsForPost(long ID)
    {
        try
        {
            ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + TAG_HASHTAG_NAME +
                            " FROM " + TABLE_NAME
                            + " WHERE " + TAG_ID + "=" + ID + " GROUP BY " + TAG_HASHTAG_NAME, null);

            if(cursor.moveToFirst())
            {
                do {
                    System.out.println("HASHTAG FOUND: " + cursor.getString(cursor.getColumnIndex(TAG_HASHTAG_NAME)));

                    esaphHashtags.add(new EsaphHashtag(cursor.getString(cursor.getColumnIndex(TAG_HASHTAG_NAME)),
                            null,
                            0));
                }
                while (cursor.moveToNext());
            }

            cursor.close();

            return esaphHashtags;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getHashtagsForPost() failed: " + ec);
            return new ArrayList<>();
        }
    }

    public void addNewHashtag(long POSTFROM, long ID, List<EsaphHashtag> esaphHashtags) //A new hashtag is added, if new post come, or the user is uploading a new post.
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getWritableDatabase();
            for(int counter = 0; counter < esaphHashtags.size(); counter++)
            {
                EsaphHashtag esaphHashtag = esaphHashtags.get(counter);
                if(!esaphHashtag.getHashtagName().isEmpty())
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TAG_POST_FROM, POSTFROM);
                    contentValues.put(TAG_ID, ID);
                    contentValues.put(TAG_HASHTAG_NAME, esaphHashtag.getHashtagName());
                    sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
                }
            }

        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "addNewHashtag(ArrayList) failed: " + ec);
        }
    }

    public void addNewHashtag(int POSTFROM, int ID, EsaphHashtag esaphHashtags) //A new hashtag is added, if new post come, or the user is uploading a new post.
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_POST_FROM, POSTFROM);
            contentValues.put(TAG_ID, ID);
            contentValues.put(TAG_HASHTAG_NAME, esaphHashtags.getHashtagName());
            sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "addNewHashtag(SingleObject) failed: " + ec);
        }
    }

    public void removeHashtagWithid(long ID)
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getWritableDatabase();
            sqLiteDatabase.delete(TABLE_NAME, TAG_ID + "=" + ID, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removeHashtagWithid() failed: " + ec);
        }
    }



    private static final String queryFilterHashtags = "SELECT * FROM " + TABLE_NAME +
            " AS H JOIN " + SQLChats.tableNamePostings + " AS P ON P." +
            SQLChats.MESSAGE_ID + "=H." + TAG_POST_FROM + " AND " + TAG_HASHTAG_NAME + " LIKE ?";

    public List<SearchItemMainMoments> filterHashtags(String searchString)
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLHashtags.super.getReadableDatabase();
            List<SearchItemMainMoments> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery(SQLHashtags.queryFilterHashtags, new String[]{searchString});

            if(cursor.moveToFirst())
            {
                do {
                    short DATA_TYPE = cursor.getShort(cursor.getColumnIndex(SQLChats.chatGlobalType));

                    if(DATA_TYPE == CMTypes.FPIC)
                    {
                        list.add(new SearchItemImage(cursor.getString(cursor.getColumnIndex(SQLChats.chat_PID)),
                                        cursor.getString(cursor.getColumnIndex("%" + TAG_HASHTAG_NAME + "%")),
                                ""));
                    }
                    else if(DATA_TYPE == CMTypes.FVID)
                    {
                        list.add(new SearchItemVideo(cursor.getString(cursor.getColumnIndex(SQLChats.chat_PID)),
                                cursor.getString(cursor.getColumnIndex("%" + TAG_HASHTAG_NAME + "%")),
                                ""));
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "filterHashtags() failed: " + ec);
            return new ArrayList<>();
        }
    }
}
