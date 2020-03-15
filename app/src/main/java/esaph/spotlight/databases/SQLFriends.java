package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.UserSiteItem;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SQLFriends extends SQLSpotlight
{
    //VERSION CODES, FOR DATABASE UPDATES----

    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";





    public static final String tableFriends = "Watcher";
    private static final String FR_PROFILBILD_RAW = "ProfilBildRaw";
    private static final String FR_PROFILBILD_HIGH_QUALITY_PATH = "ProfilbildHQPath";
    public static final String FR_UID="_uid";
    public static final String FR_BENUTZERNAME ="Benutzername";
    public static final String FR_VORNAME="Vorname";
    private static final String FR_ALTER="AlterMillis";
    public static final String FR_DESCRIPTION_PLOPP="DESCPLOPP";
    private static final String FR_REGION="Region";
    public static final String FR_FRIENDSHIP_DIED = "FSDIED";
    public static final String FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE = "NSA";
    public static final String FR_FRIENDSHIP_NEED_SYNCH_SAVED = "NSS";
    private static final String FR_NOTIFY_ID="NID";

    private static final String TABLE_NOTIFY_POST_ID = "NotifyPostIds";
    private static final String NOTIFY_ID = "ID";
    private static final String NOTIFY_MESSAGE_ID = "PID";

    private static final String tableFollowAnfragen = "WatcherAusstehend";
    private static final String FR_ANFRAGEN_STATUS = "AST";
    private static final String FR_ANFRAGEN_UHRZEIT = "TIME";

    public SQLFriends(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }

    private static final String createTableFollowerAnfragen = "create table if not exists " + tableFollowAnfragen +
            " (" +
            FR_UID + " INTEGER, " +
            FR_BENUTZERNAME + " TEXT, " +
            FR_REGION + " TEXT, " +
            FR_VORNAME + " TEXT, " +
            FR_ANFRAGEN_UHRZEIT + " LONG, " +
            FR_ANFRAGEN_STATUS + " INTEGER)";

    private static final String createTableNotificationIDsForMessageID = "create table if not exists " + TABLE_NOTIFY_POST_ID +
            " (" +
            NOTIFY_ID + " INTEGER, " +
            NOTIFY_MESSAGE_ID + " INTEGER)";


    private static final String createTableFriends = "create table if not exists " + tableFriends +
            " (" +
            FR_UID + " INTEGER PRIMARY KEY, " +
            FR_BENUTZERNAME + " TEXT, " +
            FR_VORNAME + " TEXT, " +
            FR_ALTER + " INTEGER, " +
            FR_DESCRIPTION_PLOPP + " TEXT, " +
            FR_NOTIFY_ID + " INTEGER, " +
            FR_FRIENDSHIP_DIED + " INTEGER, " +
            FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE + " INTEGER, " +
            FR_FRIENDSHIP_NEED_SYNCH_SAVED + " INTEGER, " +
            FR_PROFILBILD_RAW + " BLOB, " +
            FR_PROFILBILD_HIGH_QUALITY_PATH + " TEXT, " +
            FR_REGION + " TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableFriends);
        db.execSQL(createTableFollowerAnfragen);
        db.execSQL(createTableNotificationIDsForMessageID);
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
        db.execSQL(createTableFriends);
        db.execSQL(createTableFollowerAnfragen);
        db.execSQL(createTableNotificationIDsForMessageID);
    }


    public void dropTableWatcher()
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
            db.delete(tableFriends, null, null);
            db.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTableWatcher() failed: " + ec);
        }
    }

    public boolean updateWatcher(ArrayList<SpotLightUser> tenChatUsers)
    {
        try
        {
            dropTableWatcher();
            Iterator<SpotLightUser> iterator = tenChatUsers.iterator();
            while(iterator.hasNext())
            {
                insertWatcher(iterator.next());
            }
            return true;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "UPDATE WATCHER() failed: " + ec);
            return false;
        }
    }


    public boolean updateAllSocialFriendAnfragen(List<SocialFriendNegotiation> list)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        db.delete(tableFollowAnfragen, null, null);

        ListIterator<SocialFriendNegotiation> listIterator = list.listIterator();
        while(listIterator.hasNext())
        {
            SocialFriendNegotiation socialFriendAnfrage = listIterator.next();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FR_BENUTZERNAME, socialFriendAnfrage.getUsername());
            contentValues.put(FR_VORNAME, socialFriendAnfrage.getVorname());
            contentValues.put(FR_REGION, socialFriendAnfrage.getRegion());
            contentValues.put(FR_UID, socialFriendAnfrage.getUID());
            contentValues.put(FR_ANFRAGEN_STATUS, socialFriendAnfrage.getAnfragenStatus());
            contentValues.put(FR_ANFRAGEN_UHRZEIT, System.currentTimeMillis());
            db.insert(tableFollowAnfragen, null, contentValues);
        }
        return true;
    }


    public List<SocialFriendNegotiation> loadMoreFollowAnfragenToMe()
    {
        try
        {
            List<SocialFriendNegotiation> list = new ArrayList<>();
            SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_ANFRAGEN_STATUS + "=" + ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT
                    + " ORDER BY " + FR_ANFRAGEN_UHRZEIT + " DESC", null);
            if(cursor.moveToFirst())
            {
                do
                {
                    list.add(new SocialFriendNegotiation(
                            cursor.getLong(cursor.getColumnIndex(FR_UID)),
                            cursor.getString(cursor.getColumnIndex(FR_BENUTZERNAME)),
                            cursor.getString(cursor.getColumnIndex(FR_VORNAME)),
                            cursor.getShort(cursor.getColumnIndex(FR_ANFRAGEN_STATUS)),
                            cursor.getString(cursor.getColumnIndex(FR_REGION))
                    ));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "loadMoreFollowAnfragenToMe() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public boolean hasNewFriendAnfragen()
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_ANFRAGEN_STATUS + "=" + ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT + " LIMIT 1", null);
            boolean hasNewFriends = false;

            if(cursor.moveToFirst())
            {
                hasNewFriends = true;
            }
            cursor.close();
            return hasNewFriends;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "loadMoreFollowAnfragenToMe() failed: " + ec);
            return false;
        }
    }

    public void removeAllUserData(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        db.delete(tableFollowAnfragen,FR_UID + "=" + UID, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_FRIENDSHIP_DIED, true);
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE, true);
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_SAVED, true);
        db.update(tableFriends, contentValues, FR_UID + "=" + UID, null);
    }

    public void deleteWatcherStatusBecauseFriendAddedOrFriendshipDied(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        db.delete(SQLFriends.tableFollowAnfragen, FR_UID + "=" + UID, null);
    }

    public void setFriendStatusIstrue(long FUID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_FRIENDSHIP_DIED, false);
        db.update(SQLFriends.tableFriends, contentValues, FR_UID + "=" + FUID, null);
    }



    public void updateFollowNegotiation(long UID, short STATUS)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_UID, UID);
        contentValues.put(FR_ANFRAGEN_STATUS, STATUS);


        if(STATUS == ServerPolicy.POLICY_DETAIL_CASE_NOTHING
                || STATUS == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS)
        {
            deleteWatcherStatusBecauseFriendAddedOrFriendshipDied(UID);
        }
        else
        {
            try
            {
                if(!checkIfAnfragenStatusExists(UID))
                {
                    db.insert(SQLFriends.tableFollowAnfragen, null, contentValues);
                    Log.i(getClass().getName(), "Anfragen status wurde hinzugefügt.");
                }
                else
                {
                    db.update(SQLFriends.tableFollowAnfragen, contentValues, FR_UID + "=" + UID, null);
                    Log.i(getClass().getName(), "Anfragen status wurde geupdated.");
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "Anfragen status konnte nicht hinzugefügt werden: " + ec);
            }
        }
    }



    public void insertNewFollowNegotiation(SocialFriendNegotiation socialFriendAnfrage)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_UID, socialFriendAnfrage.getUID());
        contentValues.put(FR_VORNAME, socialFriendAnfrage.getVorname());
        contentValues.put(FR_REGION, socialFriendAnfrage.getRegion());
        contentValues.put(FR_BENUTZERNAME, socialFriendAnfrage.getUsername());
        contentValues.put(FR_ANFRAGEN_STATUS, socialFriendAnfrage.getAnfragenStatus());
        contentValues.put(FR_ANFRAGEN_UHRZEIT, System.currentTimeMillis());


        if(socialFriendAnfrage.getAnfragenStatus() == ServerPolicy.POLICY_DETAIL_CASE_NOTHING
                || socialFriendAnfrage.getAnfragenStatus() == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS)
        {
            deleteWatcherStatusBecauseFriendAddedOrFriendshipDied(socialFriendAnfrage.getUID());
        }
        else
        {
            try
            {
                if(!checkIfAnfragenStatusExists(socialFriendAnfrage.getUID()))
                {
                    db.insert(SQLFriends.tableFollowAnfragen, null, contentValues);
                    Log.i(getClass().getName(), "Anfragen status wurde hinzugefügt.");
                }
                else
                {
                    db.update(SQLFriends.tableFollowAnfragen, contentValues, FR_UID + "=" + socialFriendAnfrage.getUID(), null);
                    Log.i(getClass().getName(), "Anfragen status wurde geupdated.");
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "Anfragen status konnte nicht hinzugefügt werden: " + ec);
            }
        }
    }



    private boolean checkIfAnfragenStatusExists(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_UID + "=" + UID, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }


    public short getAnfragenStatus(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_BENUTZERNAME + "=" + UID, null);
        short result = -1;
        if(cursor.moveToFirst())
        {
            result = cursor.getShort(cursor.getColumnIndex(FR_ANFRAGEN_STATUS));
        }
        cursor.close();
        return result;
    }

    public short getAnfragenStatusAndCheckIfFriendship(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_UID + "=" + UID, null);
        short result = -1;
        if(cursor.moveToFirst())
        {
            result = cursor.getShort(cursor.getColumnIndex(FR_ANFRAGEN_STATUS));
        }
        cursor.close();

        if(result == -1)
        {
            if(isFriendshipDied(UID))
            {
                result = ServerPolicy.POLICY_DETAIL_CASE_NOTHING; //Eh keine verbindung zwischen beiden nutzern.
            }
            else
            {
                result = ServerPolicy.POLICY_DETAIL_CASE_FRIENDS; //Eh status das befreundet ist
            }
        }
        return result;
    }


    public int getNumberOfNewFriendAnfragen()
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableFollowAnfragen + " WHERE " + FR_ANFRAGEN_STATUS + "=" + ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT, null);
            int result = 0;
            if(cursor.moveToFirst())
            {
                result = cursor.getCount();
            }
            cursor.close();
            return result;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getNumberOfNewFriendAnfragen() failed: " + ec);
            return 0;
        }
    }

    private int generateRandomUniqueNumber()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=1; i<11; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        return list.get(0);
    }

    public void insertWatcher(SpotLightUser spotLightUser)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_BENUTZERNAME, spotLightUser.getBenutzername());
        contentValues.put(FR_UID, spotLightUser.getUID());
        contentValues.put(FR_VORNAME, spotLightUser.getVorname());
        contentValues.put(FR_ALTER, spotLightUser.getAlter());
        contentValues.put(FR_FRIENDSHIP_DIED, spotLightUser.wasFriends());
        contentValues.put(FR_NOTIFY_ID, generateRandomUniqueNumber());
        contentValues.put(FR_DESCRIPTION_PLOPP, spotLightUser.getDescriptionPlopp().toString());
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE, false);
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_SAVED, false);

        if(spotLightUser.getWatchingStatus() != -1)
        {
            insertNewFollowNegotiation(new SocialFriendNegotiation(spotLightUser.getUID(),
                    spotLightUser.getBenutzername(),
                    spotLightUser.getVorname(),
                    spotLightUser.getWatchingStatus(),
                    spotLightUser.getRegion()));
        }

        SQLChats sqlChats = new SQLChats(context);
        try
        {
            if(db.update(SQLFriends.tableFriends, contentValues, FR_BENUTZERNAME + "='" + spotLightUser.getBenutzername() + "'", null) <= 0)
            {
                db.insert(SQLFriends.tableFriends, null, contentValues);

                if(!spotLightUser.wasFriends())
                {
                    sqlChats.insertNewChatConversation(spotLightUser);
                }
            }
            Log.i(getClass().getName(), "Freund wurde hinzugefügt.");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Freund konnte nicht hinzugefügt werden: " + ec);
        }
        finally
        {
            if(sqlChats != null)
            {
                sqlChats.close();
            }
        }
    }


    public long lookUpUid(String Username)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FR_UID + " FROM " + tableFriends + " WHERE " + FR_BENUTZERNAME + "='" + Username + "' LIMIT 1", null);
        long returnMent = -1;
        try
        {
            if(cursor.moveToFirst())
            {
                returnMent = cursor.getLong(cursor.getColumnIndex(FR_UID));
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "lookUpUid() failed: " + ec);
        }
        finally {
            cursor.close();
        }

        return returnMent;
    }

    public String lookUpUsername(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FR_BENUTZERNAME + " FROM " + tableFriends + " WHERE " + FR_UID + "=" + UID + " LIMIT 1", null);
        String returnMent = "";
        try
        {
            if(cursor.moveToFirst())
            {
                returnMent = cursor.getString(cursor.getColumnIndex(FR_BENUTZERNAME));
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "lookUpUsername(UID) failed: " + ec);
        }
        finally {
            cursor.close();
        }

        return returnMent;
    }


    public int getFriendNotifyId(long UID)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FR_NOTIFY_ID + " FROM " + tableFriends + " WHERE " + FR_UID + "=" + UID + " LIMIT 1", null);
        int returnMent = -1;
        try
        {
            if(cursor.moveToFirst())
            {
                returnMent = cursor.getInt(cursor.getColumnIndex(FR_NOTIFY_ID));
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getFriendNotifyId() failed: " + ec);
        }
        finally {
            cursor.close();
        }

        return returnMent;
    }


    public ArrayList<Object> getAllWatchers()
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableFriends + " WHERE " + FR_FRIENDSHIP_DIED + "=0 ORDER BY " + FR_BENUTZERNAME, null);

        try
        {
            ArrayList<Object> tenChatUsers = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                if(cursor.getCount() == 0)
                {
                    return tenChatUsers;
                }
                do
                {
                    tenChatUsers.add(new SpotLightUser(
                            cursor.getLong(cursor.getColumnIndex(FR_UID)),
                            cursor.getString(cursor.getColumnIndex(FR_BENUTZERNAME)),
                            cursor.getString(cursor.getColumnIndex(FR_VORNAME)),
                            (cursor.getLong(cursor.getColumnIndex(FR_ALTER))),
                            cursor.getString(cursor.getColumnIndex(FR_REGION)),
                            cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_DIED)) > 0,
                            cursor.getString(cursor.getColumnIndex(FR_DESCRIPTION_PLOPP))));
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return tenChatUsers;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllWatchers() failed: " + ec);
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

    public SpotLightUser lookUpWatcher(long UID)
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableFriends + " WHERE " + FR_UID + "=" + UID + " LIMIT 1", null);
            cursor.moveToFirst();
            SpotLightUser tenChatUser = new SpotLightUser(
                    cursor.getLong(cursor.getColumnIndex(FR_UID)),
                    cursor.getString(cursor.getColumnIndex(FR_BENUTZERNAME)),
                    cursor.getString(cursor.getColumnIndex(FR_VORNAME)),
                    (cursor.getLong(cursor.getColumnIndex(FR_ALTER))),
                    cursor.getString(cursor.getColumnIndex(FR_REGION)),
                    cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_DIED)) > 0,
                    cursor.getString(cursor.getColumnIndex(FR_DESCRIPTION_PLOPP)));

            cursor.close();
            db.close();
            return tenChatUser;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Cant look up friend: " + ec);
            return null;
        }
    }


    public SpotLightUser lookUpWatcherAndFriendshipDiedWatcher(String benutzername)
    {
        try
        {
            Log.i(getClass().getName(), "Look up friend name: " + benutzername);
            SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableFriends + " WHERE " + FR_BENUTZERNAME + "='" + benutzername + "' LIMIT 1", null);
            cursor.moveToFirst();
            SpotLightUser tenChatUser = new SpotLightUser(
                    cursor.getLong(cursor.getColumnIndex(FR_UID)),
                    benutzername,
                    cursor.getString(cursor.getColumnIndex(FR_VORNAME)),
                    (cursor.getLong(cursor.getColumnIndex(FR_ALTER))),
                    cursor.getString(cursor.getColumnIndex(FR_REGION)),
                    cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_DIED)) > 0,
                    cursor.getString(cursor.getColumnIndex(FR_DESCRIPTION_PLOPP)));

            cursor.close();
            db.close();
            return tenChatUser;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Cant look up friend (ignoring if blocked or no friendship): " + ec);
            return null;
        }
    }


    public boolean isFriendshipDied(String Username)
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + FR_FRIENDSHIP_DIED + " FROM " + tableFriends + " WHERE " + FR_BENUTZERNAME + "='" + Username + "' LIMIT 1", null);
            boolean died = true;
            if(cursor.moveToFirst())
            {
                died = cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_DIED)) > 0;
            }
            cursor.close();
            return died;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "isFriendshipDied: " + ec);
            return true;
        }
    }


    public boolean isFriendshipDied(long ID)
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + FR_FRIENDSHIP_DIED + " FROM " + tableFriends + " WHERE " + FR_UID + "=" + ID + " LIMIT 1", null);
            boolean died = true;
            if(cursor.moveToFirst())
            {
                died = cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_DIED)) > 0;
            }
            cursor.close();
            return died;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "isFriendshipDied(ID): " + ec);
            return true;
        }
    }


    public boolean needFriendSynchAktuelle(long UID_CHAT_PARTNER)
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE + " FROM " + tableFriends + " WHERE " + FR_UID + "=" + UID_CHAT_PARTNER + " LIMIT 1", null);
            boolean died = true;
            if(cursor.moveToFirst())
            {
                died = cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE)) > 0;
            }
            cursor.close();
            return died;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "needFriendSynchAktuelle: " + ec);
            return true;
        }
    }


    public boolean needFriendSynchSaved(String Username)
    {
        try
        {
            SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + FR_FRIENDSHIP_NEED_SYNCH_SAVED + " FROM " + tableFriends + " WHERE " + FR_BENUTZERNAME + "='" + Username + "' LIMIT 1", null);
            boolean died = true;
            if(cursor.moveToFirst())
            {
                died = cursor.getInt(cursor.getColumnIndex(FR_FRIENDSHIP_NEED_SYNCH_SAVED)) > 0;
            }
            cursor.close();
            return died;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "needFriendSynchSaved: " + ec);
            return true;
        }
    }

    public void setFriendNeedSynchAktuelle(long UID, boolean need)
    {
        SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_AKTUELLE, need);
        db.update(tableFriends, contentValues, FR_UID + "=" + UID, null);
    }


    public void setFriendNeedSynchSaved(long UID, boolean need)
    {
        SQLiteDatabase db = SQLFriends.super.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FR_FRIENDSHIP_NEED_SYNCH_SAVED, need);
        db.update(tableFriends, contentValues, FR_UID + "=" + UID, null);
    }

    public File getProfilbildFromUser(String Username)
    {
        return null;
    }

    public int insertNewNotifyPostId(ConversationMessage conversationMessage)
    {
        int number = generateRandomUniqueNumber();
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFY_MESSAGE_ID, conversationMessage.getMESSAGE_ID());
        contentValues.put(NOTIFY_ID, number);
        db.insert(TABLE_NOTIFY_POST_ID, null, contentValues);
        return number;
    }

    public void onNotificationPostRemoved(ConversationMessage conversationMessage)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        db.delete(TABLE_NOTIFY_POST_ID, NOTIFY_MESSAGE_ID + "=" + conversationMessage.getMESSAGE_ID(), null);
    }

    public int getPostNotifyId(ConversationMessage conversationMessage)
    {
        SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + NOTIFY_ID + " FROM " + TABLE_NOTIFY_POST_ID + " WHERE "
                + NOTIFY_MESSAGE_ID + "=" + conversationMessage.getMESSAGE_ID()
                + " LIMIT 1", null);
        int returnMent = -1;
        try
        {
            if(cursor.moveToFirst())
            {
                returnMent = cursor.getInt(cursor.getColumnIndex(NOTIFY_ID));
            }
            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getPostNotifyId() failed: " + ec);
            cursor.close();
        }

        return returnMent;
    }


    public ConversationMessage getDescriptionPlopp(long UID)
    {
        ConversationMessage conversationMessage = null;
        try
        {
            SQLiteDatabase db = SQLFriends.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + FR_DESCRIPTION_PLOPP + " FROM " + tableFriends + " WHERE "
                    + FR_UID + "=? LIMIT 1", new String[]{Long.toString(UID)});

            if(cursor.moveToFirst())
            {
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(FR_DESCRIPTION_PLOPP)));
                conversationMessage = new ChatTextMessage(
                        SpotTextDefinitionBuilder.getText(jsonObject),
                        -1,
                        -1,
                        -1,
                        -1,
                        (short) -1,
                        "",
                        jsonObject.toString());
            }
            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getDescriptionPlopp() failed: " + ec);
        }
        return conversationMessage;
    }
}
