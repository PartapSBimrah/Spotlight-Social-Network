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

import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model.PublicPost;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class SQLPublicPosts extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";

    private static final String tablePublicPosts = "PublicPosts";

    private static final String pub_PRIMARY_KEY_ID = "_ID";
    private static final String pub_PID = "PID";
    private static final String pub_SERVER_POST_ID = "_PPID";
    private static final String pub_State = "State";
    private static final String pub_UID_ABSENDER = "UID_ABS";
    private static final String pub_BESCHREIBUNG = "Description";
    private static final String pub_HASHTAGS = "Hashtags";
    private static final String pub_TYPE = "Type";
    private static final String pub_Time = "Time";


    private static final String tablePublicHashtags = "PublicHashtags";
    private static final String hashtag_PRIMARY_KEY = "_ID";
    private static final String hashtag_pub_PRIMARY_KEY = "h_ID";
    private static final String hashtags_TAG_CONTENT = "tag";


    private static final String createTablePublicPosts = "create table if not exists " +
            tablePublicPosts + " ("
            + pub_PRIMARY_KEY_ID + " INTEGER PRIMARY KEY autoincrement, " +
            pub_PID + " TEXT, " +
            pub_SERVER_POST_ID + " INTEGER, " +
            pub_UID_ABSENDER + " INTEGER, " +
            pub_BESCHREIBUNG + " TEXT, " +
            pub_HASHTAGS + " TEXT, " +
            pub_TYPE + " INTEGER, " +
            pub_State + " INTEGER, " +
            pub_Time + " INTEGER)";

    private static final String createTableHashtags = "create table if not exists " +
            tablePublicHashtags + " ("
            + hashtag_PRIMARY_KEY + " INTEGER PRIMARY KEY autoincrement, " +
            hashtag_pub_PRIMARY_KEY + " INTEGER, " +
            hashtags_TAG_CONTENT + " TEXT)";

    public SQLPublicPosts(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTablePublicPosts);
        db.execSQL(createTableHashtags);
    }

    public void insertNew(PublicPost publicPost)
    {
        try
        {
            SQLiteDatabase db = SQLPublicPosts.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(pub_UID_ABSENDER, publicPost.getUID());
            contentValues.put(pub_BESCHREIBUNG, publicPost.getBeschreibung());
            contentValues.put(pub_PID, publicPost.getPID());
            contentValues.put(pub_SERVER_POST_ID, publicPost.getSERVER_ID_POST());
            contentValues.put(pub_State, publicPost.getState());
            contentValues.put(pub_Time, publicPost.getUhrzeit());
            contentValues.put(pub_TYPE, publicPost.getType());
            long ID = db.insert(tablePublicPosts, null, contentValues);
            publicPost.set_ID(ID);
            insertHashtags(publicPost);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNew() failed: " + ec);
        }
    }


    private void insertHashtags(PublicPost publicPost)
    {
        try
        {
            SQLiteDatabase db = SQLPublicPosts.super.getWritableDatabase();

            List<EsaphHashtag> list = publicPost.getEsaphHashtag();
            int size = list.size();

            ContentValues contentValues = new ContentValues();
            for(int counter = 0; counter < size; counter++)
            {
                contentValues.clear();
                contentValues.put(hashtag_pub_PRIMARY_KEY, publicPost.get_ID());
                contentValues.put(hashtags_TAG_CONTENT, list.get(counter).getHashtagName());
                db.insert(tablePublicHashtags, null, contentValues);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertHashtags() failed: " + ec);
        }
    }

    private static final String query_GetAllMyPostsLimited =
            "SELECT * FROM (SELECT * FROM " + tablePublicPosts
                    + " WHERE 1 ORDER BY " + pub_Time + " DESC LIMIT ?,20) AS PUBLICPOSTS"
                    + " LEFT JOIN "
            + tablePublicHashtags + " ON PUBLICPOSTS."
            + pub_PRIMARY_KEY_ID + "=" + tablePublicHashtags + "."
            + hashtag_pub_PRIMARY_KEY;

    public List<PublicPost> getMyPublicPostsLimited(int index)
    {
        Cursor cursor = null;
        try
        {
            String LOGGED_USERNAME = SpotLightLoginSessionHandler.getLoggedUsername();
            SQLiteDatabase db = SQLPublicPosts.super.getReadableDatabase();
            cursor = db.rawQuery(query_GetAllMyPostsLimited, new String[]{Integer.toString(index)});
            List<PublicPost> list = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                PublicPost publicPost = null;
                ArrayList<EsaphHashtag> hashtagList = null;
                do {
                    if(publicPost == null || publicPost.get_ID() != cursor.getLong(cursor.getColumnIndex(pub_PRIMARY_KEY_ID)))
                    {
                        if(publicPost != null)
                        {
                            publicPost.setEsaphHashtag(hashtagList);
                            list.add(publicPost);
                        }


                        publicPost = new PublicPost(
                                cursor.getLong(cursor.getColumnIndex(pub_PRIMARY_KEY_ID)),
                                cursor.getLong(cursor.getColumnIndex(pub_UID_ABSENDER)),
                                cursor.getLong(cursor.getColumnIndex(pub_SERVER_POST_ID)),
                                LOGGED_USERNAME,
                                cursor.getString(cursor.getColumnIndex(pub_BESCHREIBUNG)),
                                cursor.getString(cursor.getColumnIndex(pub_PID)),
                                cursor.getLong(cursor.getColumnIndex(pub_Time)),
                                cursor.getShort(cursor.getColumnIndex(pub_TYPE)),
                                cursor.getShort(cursor.getColumnIndex(pub_State)),
                                false);
                        hashtagList = new ArrayList<>();
                    }

                    if(!cursor.isNull(cursor.getColumnIndex(hashtag_PRIMARY_KEY)))
                    {
                        hashtagList.add(new EsaphHashtag(cursor.getString(cursor.getColumnIndex(hashtags_TAG_CONTENT)),
                                null,
                                0));
                    }
                }
                while(cursor.moveToNext()); //Iterating hashtags.
            }

            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getMyPublicPostsLimited() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }

}
