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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUploadFoto;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUploadVideo;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphDays;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphWeeks;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class SQLLifeCloud extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";


    private static final String tableName = "LifeCloud";
    private static final String cloud_id = "_ID"; // TODO: 26.06.2019 add this in table creating, and in the whole system
    private static final String cloud_time = "Uhrzeit";
    private static final String cloud_pid = "PID";
    private static final String cloud_post_status = "Status";
    private static final String cloud_post_type = "TYPE"; //OB BILD ODER VIDEO ODER TYPE_TEXT
    private static final String cloud_lifecloud_type = "LIFEC_TYPE"; //OB BILD ODER VIDEO ODER TYPE_TEXT
    private static final String cloud_post_Beschreibung = "Description"; //OB BILD ODER VIDEO ODER TYPE_TEXT

    private static final String createTable = "create table if not exists " + tableName +
            " (" +
            cloud_post_type + " SHORT, " +
            cloud_lifecloud_type + " SHORT, " +
            cloud_post_Beschreibung + " TEXT, " +
            cloud_pid + " TEXT, " +
            cloud_post_status + " SHORT, " +
            cloud_time + " INTEGER)";

    public SQLLifeCloud(Context context)
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
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }

    public void dropTableLifeCloud()
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            Log.i(getClass().getName(), "DROPPING DOWN");
            db.delete(tableName, null, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTableLifeCloud() failed: " + ec);
        }
    }

    public void insertNewLifeCloudUpload(LifeCloudUpload lifeCloudUpload)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(cloud_time, lifeCloudUpload.getCLOUD_TIME_UPLOADED());
            contentValues.put(cloud_pid, lifeCloudUpload.getCLOUD_PID());
            contentValues.put(cloud_post_status, lifeCloudUpload.getCLOUD_MESSAGE_STATUS());
            contentValues.put(cloud_post_type, lifeCloudUpload.getCLOUD_POST_TYPE());
            contentValues.put(cloud_post_Beschreibung, lifeCloudUpload.getCLOUD_POST_DESCRIPTION());

            db.insert(SQLLifeCloud.tableName, null, contentValues);

            SQLHashtags sqlHashtags = new SQLHashtags(context);
            try
            {
                // TODO: 07.03.2019 make this avaiable
               // sqlHashtags.addNewHashtag(new , lifeCloudUpload.getCLOUD_PID(), lifeCloudUpload.getEsaphHashtag());
            }
            catch (Exception ec)
            {

            }
            finally
            {
                sqlHashtags.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewLifeCloudUpload() failed: " + ec);
        }
        finally
        {
        }
    }



    public LifeCloudUpload getLatestLifeCloudUploadFromToday()
    {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        long timeCompare = calendar.getTimeInMillis();

        SQLiteDatabase db = SQLLifeCloud.super.getReadableDatabase();
        Cursor cursor = null;
        LifeCloudUpload lifeCloudUpload = null;
        try
        {
            cursor = db.rawQuery("SELECT * FROM " +
                    tableName + " WHERE " + "(" + cloud_time + " >= " + timeCompare + ")"
                    + " ORDER BY " + cloud_time + " DESC LIMIT 1", null);

            if(cursor.moveToFirst())
            {
                short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                if(TYPE == (CMTypes.FPIC))
                {
                    lifeCloudUpload = new LifeCloudUploadFoto(
                            getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                            cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                            cursor.getString(cursor.getColumnIndex(cloud_pid)),
                            cursor.getLong(cursor.getColumnIndex(cloud_time)),
                            cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                    cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type)));
                }
                else if(TYPE == (CMTypes.FVID))
                {
                    lifeCloudUpload = new LifeCloudUploadVideo(
                            getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                            cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                            cursor.getString(cursor.getColumnIndex(cloud_pid)),
                            cursor.getLong(cursor.getColumnIndex(cloud_time)),
                            cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                            cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type)));
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLatestLifeCloudUploadFromToday() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return lifeCloudUpload;
    }

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    public List<Object> getAllLifeCloudUploadsWithDatumHolder(int limit, long endOfLastday)
    {
        long cachedTime = System.currentTimeMillis();
        List<Object> messages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM "
                    + tableName +
                    " WHERE 1 ORDER BY " + cloud_time + " DESC LIMIT " + limit + ", 20", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    if(endOfLastday >= cursor.getLong(cursor.getColumnIndex(cloud_time)) || endOfLastday <= -1)
                    {
                        Date dateTime1 = new Date(cursor.getLong(cursor.getColumnIndex(cloud_time)));
                        Date dateTime2 = new Date(cachedTime);

                        long days = EsaphDays.daysBetween(dateTime1, dateTime2);
                        long weeks = EsaphWeeks.weeksBetween(dateTime1, dateTime2);
                        StringBuilder stringBuilder = new StringBuilder();

                        if(days == 0)
                        {
                            stringBuilder.append(context.getResources().getString(R.string.txt_heute));
                        }
                        else if(days == 1)
                        {
                            stringBuilder.append(context.getResources().getString(R.string.txt_gestern));
                        }
                        else
                        {
                            if(days < 7)
                            {
                                stringBuilder.append(context.getResources().getString(R.string.txt_days_ago, days));
                            }
                            else if(weeks < 12)
                            {
                                stringBuilder.append(context.getResources().getString(R.string.txt_weeks_ago, weeks));
                            }
                            else
                            {
                                stringBuilder.append(simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(cloud_time))));
                            }
                        }

                        messages.add(new DatumList(stringBuilder.toString(), simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(cloud_time))), cursor.getLong(cursor.getColumnIndex(cloud_time))));
                    }

                    short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                    if(TYPE == (CMTypes.FPIC))
                    {
                        messages.add(new LifeCloudUploadFoto(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                    else if(TYPE == (CMTypes.FVID))
                    {
                        messages.add(new LifeCloudUploadVideo(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }


                    final Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(cloud_time)));
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    endOfLastday = calendar.getTimeInMillis();
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return messages;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SQLChats(getAllLifeCloudUploadsWithDatumHolder()): " + ec);
            return new ArrayList<>();
        }
    }


    public int getCountOfAllLifeCloudPosts()
    {
        SQLiteDatabase db = SQLLifeCloud.super.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT 1 FROM "
                + tableName +
                " WHERE 1", null);

        int count = 0;

        if(cursor.moveToFirst())
        {
            count = cursor.getCount();
        }

        cursor.close();
        return count;
    }


    public List<LifeCloudUpload> getLatestLifeCloudPost()
    {
        List<LifeCloudUpload> messages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM "
                    + tableName +
                    " WHERE 1 ORDER BY " + cloud_time + " DESC LIMIT 1", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                    if(TYPE == (CMTypes.FPIC))
                    {
                        messages.add(new LifeCloudUploadFoto(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                    else if(TYPE == (CMTypes.FVID))
                    {
                        messages.add(new LifeCloudUploadVideo(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return messages;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SQLChats(getLatestLifeCloudPost()): " + ec);
            return new ArrayList<>();
        }
    }

    public List<LifeCloudUpload> getAllLatestLifeCloudUploadFromTodayLimited(int startFrom)
    {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        long timeCompare = calendar.getTimeInMillis();

        SQLiteDatabase db = SQLLifeCloud.super.getReadableDatabase();
        Cursor cursor = null;
        List<LifeCloudUpload> list = new ArrayList<>();
        try
        {
            cursor = db.rawQuery("SELECT * FROM " +
                    tableName + " WHERE " + "(" + cloud_time + " >= " + timeCompare + ")"
                    + " ORDER BY " + cloud_time + " DESC LIMIT " + startFrom + ", 10", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                    if(TYPE == (CMTypes.FPIC))
                    {
                        list.add(new LifeCloudUploadFoto(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                    else if(TYPE == (CMTypes.FVID))
                    {
                        list.add(new LifeCloudUploadVideo(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLatestLifeCloudUploadFromToday() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return list;
    }

    private ArrayList<EsaphHashtag> getHashTagForCreatingLifeCloudUpload(long ID)
    {
        SQLHashtags sqlHashtags = new SQLHashtags(context);
        ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
        try
        {
            esaphHashtags = sqlHashtags.getHashtagsForPost(ID);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getHashTagForCreatingLifeCloudUpload() failed: " + ec);
        }
        finally
        {
            sqlHashtags.close();
        }

        return esaphHashtags;
    }


    public LifeCloudUpload getLifeCloudUploadByID(long ID)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + cloud_id + "=" + ID, null);

            LifeCloudUpload lifeCloudUpload = null;

            if(cursor.moveToFirst())
            {
                short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                if(TYPE == (CMTypes.FPIC))
                {
                    lifeCloudUpload = new LifeCloudUploadFoto(
                            getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                            cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                            cursor.getString(cursor.getColumnIndex(cloud_pid)),
                            cursor.getLong(cursor.getColumnIndex(cloud_time)),
                            cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                            cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type)));
                }
                else if(TYPE == (CMTypes.FVID))
                {
                    lifeCloudUpload = new LifeCloudUploadVideo(
                            getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                            cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                            cursor.getString(cursor.getColumnIndex(cloud_pid)),
                            cursor.getLong(cursor.getColumnIndex(cloud_time)),
                            cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                            cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type)));
                }
                cursor.close();
                return lifeCloudUpload;
            }

            Log.i(getClass().getName(), "getLifeCloudUploadByID() failed: null object");
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

    public List<LifeCloudUpload> getLifeCloudPostsThatNotBeenUploadedYet()
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + cloud_post_status + "=" + LifeCloudUpload.LifeCloudStatus.STATE_FAILED_NOT_UPLOADED
                    + " ORDER BY " + cloud_time, null);

            List<LifeCloudUpload> lifeCloudUploadList = new ArrayList<>();

            int counter = 0;
            if(cursor.moveToFirst())
            {
                do
                {
                    short TYPE = cursor.getShort(cursor.getColumnIndex(cloud_post_type));
                    if(TYPE == (CMTypes.FPIC))
                    {
                        lifeCloudUploadList.add(new LifeCloudUploadFoto(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                    else if(TYPE == (CMTypes.FVID))
                    {
                        lifeCloudUploadList.add(new LifeCloudUploadVideo(
                                getHashTagForCreatingLifeCloudUpload(cursor.getLong(cursor.getColumnIndex(cloud_pid))),
                                cursor.getString(cursor.getColumnIndex(cloud_post_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(cloud_pid)),
                                cursor.getLong(cursor.getColumnIndex(cloud_time)),
                                cursor.getShort(cursor.getColumnIndex(cloud_post_status)),
                                cursor.getShort(cursor.getColumnIndex(cloud_lifecloud_type))));
                    }
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return lifeCloudUploadList;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLifeCloudPostsThatNotBeenUploadedYet() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public void updateLifeCloudPID(LifeCloudUpload lifeCloudUpload, String NEW_PID)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(cloud_pid, NEW_PID);

            db.update(tableName,
                    contentValues,
                    cloud_pid + "='" + lifeCloudUpload.getCLOUD_PID() + "'",
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateLifeCloudPID() failed: " + ec);
        }
    }


    public void updateLifeCloudPidAndStatusSetUploaded(LifeCloudUpload lifeCloudUpload, String NEW_PID)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(cloud_pid, NEW_PID);
            contentValues.put(cloud_post_status, LifeCloudUpload.LifeCloudStatus.STATE_UPLOADED);

            db.update(tableName,
                    contentValues,
                    cloud_pid + "='" + lifeCloudUpload.getCLOUD_PID() + "'",
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateLifeCloudPidAndStatusSetUploaded() failed: " + ec);
        }
    }


    public void updateLifeCloudPostStatus(LifeCloudUpload lifeCloudUpload, short NEW_STATUS)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(cloud_post_status, NEW_STATUS);

            db.update(tableName,
                    contentValues,
                    cloud_pid + "='" + lifeCloudUpload.getCLOUD_PID() + "'",
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateLifeCloudPostStatus() failed: " + ec);
        }
    }


    public void removePostsPassedDeadline(long timeMinusDay)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + cloud_pid + " FROM " + tableName + " WHERE " + cloud_time + "<=" + timeMinusDay, null);
            if(cursor.moveToFirst())
            {
                do
                {
                    StorageHandler.removeImageData(StorageHandler.FOLDER__LIFECLOUD, context, cursor.getString(cursor.getColumnIndex(cloud_pid)));
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            db.delete(tableName, cloud_time + "<=" + timeMinusDay, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePostsPassedDeadline() failed: " + ec);
        }
    }

    public void deletePostLifeCloud(LifeCloudUpload lifeCloudUpload)
    {
        try
        {
            SQLiteDatabase db = SQLLifeCloud.super.getWritableDatabase();
            db.delete(tableName, cloud_pid + "='" + lifeCloudUpload.getCLOUD_PID() + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePostsPassedDeadline() failed: " + ec);
        }
    }

}
