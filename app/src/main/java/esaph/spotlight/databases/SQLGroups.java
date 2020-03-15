package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import esaph.spotlight.R;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.GroupChats.GroupConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.GalleryItem;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.AktuelleGruppe;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.InternMomentMember;
import esaph.spotlight.navigation.spotlight.Moments.MomentPost;

public class SQLGroups extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";
    public static final String DATABASE_NAME_PATH = "Groups";

    private static final String tableMemoryPostsSaved = "SM";
    private static final String tableMemoryPostsAktuelle = "AM";
    private static final String memorys_GIID = "GIID";
    private static final String memorys_PID = "PID";
    private static final String memorys_MessageText = "MTEX";
    private static final String memorys_MessageHash = "MHASH";
    private static final String memorys_Username = "Username";
    private static final String memorys_Time = "Time";
    private static final String memorys_Format = "Format";
    private static final String memorys_GroupName = "GroupName";
    private static final String memorys_Beschreibung = "Beschreibung";

    public static final String tableGruppen = "AMT"; //DAS HIER SIND DIE KARTEN DIE AKTUELLEN.
    private static final String group_topic_Title = "Title";
    private static final String group_topic_MIID = "MIID";
    private static final String group_topic_CreatedTime = "CreateTime";
    private static final String group_topic_LastActionTime = "LAT";
    private static final String group_topic_ADMIN = "Admin";
    private static final String group_topic_creator = "CC";
    private static final String group_topic_leaved = "Leaved";
    private static final String group_topic_need_synch = "NS";

    private static final String memorys_status = "mSTATUS";

    private static final String tableNameSeenPosts = "SPOST"; //Hier wird eingetrage, wenn man ein post gesehen hat. Damit es später übertrage wird.
    private static final String createTableSeenPosts = "create table if not exists " + tableNameSeenPosts +
            " (" +
            memorys_Username + " TEXT, " +
            memorys_PID + " TEXT, " +
            memorys_GIID + " TEXT, " +
            memorys_Time + " INTEGER)";



    private static final String tablePartnerSavedOrSeenYourPostings = "PSOSYP";
    private static final String createTableSeenOrSavedPostsFromPartners = "create table if not exists " + tablePartnerSavedOrSeenYourPostings +
            "(" +
            memorys_Username + " TEXT, " +
            memorys_GIID + " TEXT, " +
            memorys_PID + " TEXT, " +
            memorys_status + " INTEGER, " +
            memorys_Time + " INTEGER)";


    private static final String createTableAktuelleMemoryTopics = "create table if not exists " + tableGruppen +
            "(" +
            group_topic_Title + " TEXT, " +
            group_topic_MIID + " TEXT, " +
            group_topic_creator + " TEXT, " +
            group_topic_ADMIN + " TEXT, " +
            group_topic_leaved + " INTEGER, " +
            group_topic_LastActionTime + " INTEGER, " +
            group_topic_need_synch + " INTEGER, " +
            group_topic_CreatedTime + " INTEGER)";


    private static final String tableMemoryUsersIn = "UsersInMemory";
    private static final String memorys_members_Username = "Username";
    private static final String memorys_members_MIID = "MIID";
    private static final String memorys_members_hasNewPostings = "HNP";
    private static final String memorys_members_needSynch = "MNS";
    private static final String memorys_members_needSynchSavedPostings = "MNSS";

    private static final String createTableMemorysMembers = "create table if not exists " + tableMemoryUsersIn +
            "(" +
            memorys_members_MIID + " TEXT, " +
            memorys_members_hasNewPostings + " INTEGER, " +
            memorys_members_needSynch + " INTEGER, " +
            memorys_members_needSynchSavedPostings + " INTEGER, " +
            memorys_members_Username + " TEXT)";

    private static final String createTableSavedMemorys = "create table if not exists " + tableMemoryPostsSaved + //Hier wird die chats für die liste ausgelesen
            "(" +
            memorys_PID + " TEXT, " +
            memorys_GIID + " TEXT, " +
            memorys_Beschreibung + " TEXT, " +
            memorys_Username + " TEXT, " +
            memorys_Format + " TEXT, " +
            memorys_status + " INTEGER, " +
            memorys_Time + " INTEGER, " +
            memorys_GroupName + " TEXT)";


    private static final String createTableAktuelleMemorys = "create table if not exists " + tableMemoryPostsAktuelle + //Hier wird die chats für die liste ausgelesen
            "(" +
            memorys_PID + " TEXT, " +
            memorys_GIID + " TEXT, " +
            memorys_Beschreibung + " TEXT, " +
            memorys_Username + " TEXT, " +
            memorys_Format + " TEXT, " +
            memorys_Time + " INTEGER, " +
            memorys_status + " INTEGER, " +
            memorys_MessageHash + " INTEGER, " +
            memorys_MessageText + " TEXT, " +
            memorys_GroupName + " TEXT)";


    public SQLGroups(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }


    public void removePostsPassedDeadline(long timeMinusDay)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorTopics = db.rawQuery("SELECT *" + " FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_Time + "<=" + timeMinusDay + " AND " + memorys_Format + "!='FTEX'", null);
            if(cursorTopics.moveToFirst())
            {
                do
                {
                    if(!doPostExistsInSaved(cursorTopics.getString(cursorTopics.getColumnIndex(memorys_PID)), cursorTopics.getString(cursorTopics.getColumnIndex(memorys_GIID))))
                    {
                        db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + cursorTopics.getString(cursorTopics.getColumnIndex(memorys_GIID)) + "' AND "
                                + memorys_PID + "='" + cursorTopics.getString(cursorTopics.getColumnIndex(memorys_PID)) + "'", null);
                    }
                }
                while(cursorTopics.moveToNext());
            }
            cursorTopics.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePostsPassedDeadline() failed: " + ec);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableSavedMemorys); //Gepeicherten memorys posts.
        db.execSQL(createTableSeenPosts); //Seen memorys posts.
        db.execSQL(createTableAktuelleMemoryTopics); //Aktuelle memory topics.
        db.execSQL(createTableMemorysMembers); //Members von memory topics. Werden gelöscht, weil diese einfach aus der datenbank tabelle rausgefilter werden.
        db.execSQL(createTableAktuelleMemorys); //Aktuellen memorys posts, verschwinden nach 24 stunden. Gespeicherten sind rübergezogen worden.
        db.execSQL(createTableSeenOrSavedPostsFromPartners);
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
        db.execSQL(createTableSavedMemorys); //Gepeicherten memorys posts.
        db.execSQL(createTableSeenPosts); //Seen memorys posts.
        db.execSQL(createTableAktuelleMemoryTopics); //Aktuelle memory topics.
        db.execSQL(createTableMemorysMembers); //Members von memory topics. Werden gelöscht, weil diese einfach aus der datenbank tabelle rausgefilter werden.
        db.execSQL(createTableAktuelleMemorys); //Aktuellen memorys posts, verschwinden nach 24 stunden. Gespeicherten sind rübergezogen worden.
        db.execSQL(createTableSeenOrSavedPostsFromPartners);
    }

    public void dropAllCurrentMomentsAndMomentPosts()
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableGruppen, null, null);
        db.delete(tableMemoryPostsAktuelle, null, null);
        db.delete(tableMemoryUsersIn, null, null);
        db.delete(tablePartnerSavedOrSeenYourPostings, null, null);
        db.delete(tableNameSeenPosts, null, null);
    }


    public void dropAllDataMoments()
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            db.delete(tableGruppen, null, null);
            db.delete(tableMemoryPostsAktuelle, null, null);
            db.delete(tableMemoryUsersIn, null, null);
            db.delete(tablePartnerSavedOrSeenYourPostings, null, null);
            db.delete(tableNameSeenPosts, null, null);
            db.delete(tableMemoryPostsSaved, null, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropAllDataMoments() failed: " + ec);
        }

    }


    public void insertPostWasSeenToTransmit(String Username, String PID, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_Username, Username);
        contentValues.put(memorys_PID, PID);
        contentValues.put(memorys_GIID, MIID);
        contentValues.put(memorys_Time, System.currentTimeMillis());

        db.insert(tableNameSeenPosts, null, contentValues);
    }


    public void removePostThatWasSeen(String PID, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableNameSeenPosts, memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);
    }


    public List<GroupConversationMessage> getAllConversationMessagesFromGroup(String MyUsername, String GIID, int startFrom)
    {
        List<GroupConversationMessage> groupConversationMessages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + GIID + "' ORDER BY " + memorys_Time
                    + " DESC LIMIT " + startFrom + ", 20", null);

            if(cursorDownloaded.moveToFirst())
            {
                do
                {
                    groupConversationMessages.add(new GroupConversationMessage(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Username)),
                            GIID,
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                            cursorDownloaded.getShort(cursorDownloaded.getColumnIndex(memorys_Format)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_MessageText)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Beschreibung)),
                            didISavedThePost(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                                    GIID, MyUsername),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(memorys_status)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_MessageHash)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_Time))
                    ));
                }
                while(cursorDownloaded.moveToNext());
            }

            cursorDownloaded.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllConversationMessagesFromGroup() failed: " + ec);
        }
        return groupConversationMessages;
    }


    public GroupConversationMessage getSingleGroupConversationMessage(String MyUsername, String PID, String GIID)
    {
        GroupConversationMessage groupConversationMessages = null;
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + GIID + "' AND " + memorys_PID + "='" + PID + "' LIMIT 1", null);

            if(cursorDownloaded.moveToFirst())
            {
                do
                {
                    groupConversationMessages = new GroupConversationMessage(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Username)),
                            GIID,
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                            cursorDownloaded.getShort(cursorDownloaded.getColumnIndex(memorys_Format)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_MessageText)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Beschreibung)),
                            didISavedThePost(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                                    GIID, MyUsername),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(memorys_status)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_MessageHash)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_Time))
                    );
                }
                while(cursorDownloaded.moveToNext());
            }

            cursorDownloaded.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getSingleGroupConversationMessage() failed: " + ec);
        }
        return groupConversationMessages;
    }


    public GroupConversationMessage getSingleGroupConversationMessageByHash(String GIID, long msgHash)
    {
        GroupConversationMessage groupConversationMessages = null;
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle
                    + " WHERE " + memorys_GIID + "='" + GIID + "' AND " + memorys_MessageHash + "=" + msgHash + " LIMIT 1", null);

            if(cursorDownloaded.moveToFirst())
            {
                do
                {
                    groupConversationMessages = new GroupConversationMessage(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Username)),
                            GIID,
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                            cursorDownloaded.getShort(cursorDownloaded.getColumnIndex(memorys_Format)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_MessageText)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Beschreibung)),
                            false,
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(memorys_status)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_MessageHash)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_Time))
                    );
                }
                while(cursorDownloaded.moveToNext());
            }

            cursorDownloaded.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getSingleGroupConversationMessageByHash() failed: " + ec);
        }
        return groupConversationMessages;
    }



    public int getCountOfAktuellenBeitrag(String MIID) //Wichtig, diese funktion gibt nur lediglich die anzahl aus deren beiträge gespeichert wurde. nicht die oben vorhanden sind.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID + "' AND " + memorys_Format + "!='FTEX'", null);
            int count = 0;
            if(cursorDownloaded.moveToFirst())
            {
                count += cursorDownloaded.getCount();
            }
            cursorDownloaded.close();
            db.close();
            return count;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getCountOfSavedBeitrag() failed: " + ec);
            return 0;
        }
    }


    public AktuelleGruppe getSingleCurrentMoment(String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
            AktuelleGruppe aktuellerMoment = null;
            if(cursorDownloaded.moveToFirst())
            {
                aktuellerMoment = new AktuelleGruppe(
                        cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)),
                        ""+getCountOfAktuellenBeitrag(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                        cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_Title)),
                        getLastPreviewMomentTypeFromAllMemorysSaved(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                        cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(group_topic_LastActionTime)),
                        cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_ADMIN)),
                        cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_creator)),
                        cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(group_topic_leaved)) > 0,
                        getLastPostetThingInAktuellenMoment(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))));
            }
            else
            {
                System.out.println("PASSED MIID MOMENT NOT FOUND.");
            }

            cursorDownloaded.close();
            return aktuellerMoment;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getSingleCurrentMoment() failed: " + ec);
            return null;
        }
    }


    public List<Object> getAllCurrentMoments(int startFrom) //Gibt für die liste alle memorys aus. AKTUELLE MOMENTE.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableGruppen + " WHERE " + group_topic_leaved + "=0" + " ORDER BY " + group_topic_Title
                    + " LIMIT " + startFrom + ",5", null);
            List<Object> list = new ArrayList<>();

            if(cursorDownloaded.moveToFirst())
            {
                for(int counter = 0; counter < cursorDownloaded.getCount(); counter++)
                {
                    list.add(new AktuelleGruppe(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)),
                            ""+getCountOfAktuellenBeitrag(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_Title)),
                            getLastPreviewMomentTypeFromAllMemorysSaved(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(group_topic_LastActionTime)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_ADMIN)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_creator)),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(group_topic_leaved)) > 0,
                            getLastPostetThingInAktuellenMoment(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)))
                    ));
                    cursorDownloaded.moveToNext();
                }
            }

            cursorDownloaded.close();
            db.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllCurrentMoments() failed: " + ec);
            return null;
        }
    }



    public List<Object> getLastContactedGroups() //Gibt für die liste alle memorys aus. AKTUELLE MOMENTE.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableGruppen + " WHERE " + group_topic_leaved + "=0" + " ORDER BY " + group_topic_LastActionTime
                    + " DESC LIMIT 5", null);
            List<Object> list = new ArrayList<>();

            if(cursorDownloaded.moveToFirst())
            {
                for(int counter = 0; counter < cursorDownloaded.getCount(); counter++)
                {
                    list.add(new AktuelleGruppe(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)),
                            ""+getCountOfAktuellenBeitrag(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_Title)),
                            getLastPreviewMomentTypeFromAllMemorysSaved(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(group_topic_LastActionTime)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_ADMIN)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_creator)),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(group_topic_leaved)) > 0,
                            getLastPostetThingInAktuellenMoment(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)))
                    ));
                    cursorDownloaded.moveToNext();
                }
            }

            cursorDownloaded.close();
            db.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastContactedGroups() failed: " + ec);
            return null;
        }
    }



    public List<Object> getAllGroups() //Gibt für die liste alle memorys aus. AKTUELLE MOMENTE.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableGruppen + " ORDER BY " + group_topic_LastActionTime + " DESC ", null);
            List<Object> list = new ArrayList<>();

            if(cursorDownloaded.moveToFirst())
            {
                for(int counter = 0; counter < cursorDownloaded.getCount(); counter++)
                {

                    list.add(new AktuelleGruppe(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)),
                            ""+getCountOfAktuellenBeitrag(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            (cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_Title))),
                            getLastPreviewMomentTypeFromAllMemorysSaved(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID))),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(group_topic_LastActionTime)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_ADMIN)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_creator)),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(group_topic_leaved)) > 0,
                            getLastPostetThingInAktuellenMoment(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(group_topic_MIID)))

                    ));
                    cursorDownloaded.moveToNext();
                }
            }

            cursorDownloaded.close();
            db.close();
            Log.i(getClass().getName(), "getAllGroups() list size: " + list.size());
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllCurrentMoments() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public void updatePrivateTextMessageSentToServer(String GIID, long newTimeFromServer, long oldTimeHash)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_MessageHash, newTimeFromServer);
        contentValues.put(memorys_status, ConversationStatusHelper.STATUS_SENT);

        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            db.update(SQLGroups.tableMemoryPostsAktuelle, contentValues,  memorys_GIID + "='" + GIID + "' AND " + memorys_MessageHash + "=" + oldTimeHash, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updatePrivateTextMessageSentToServer() failed: " + ec);
        }
    }

    public void updatePrivateTextMessageFailtToSentServer(String GIID, long oldTimeHash)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_status, ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);

        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            db.update(SQLGroups.tableMemoryPostsAktuelle, contentValues,  memorys_GIID + "='" + GIID + "' AND " + memorys_MessageHash + "=" + oldTimeHash, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updatePrivateTextMessageFailtToSentServer() failed: " + ec);
        }
    }

    public void updateAllChatMessagesTextAsRead(String GIID, String MyUsername)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_status, ConversationStatusHelper.STATUS_CHAT_OPENED);

        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            db.update(SQLGroups.tableMemoryPostsAktuelle, contentValues,  memorys_GIID + "='" + GIID + "' AND " + memorys_Format + "='FTEX' AND " +
                    memorys_Username + "!='" + MyUsername + "' OR " + memorys_GIID + "='" + GIID + "' AND " + memorys_Format + "='INF' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateAllChatMessagesTextAsRead() failed: " + ec);
        }
    }


    public List<MomentPost> getAllCurrentMomentPostsLimited(int startFrom, String MIID)
    {
        try
        {
            List<MomentPost> list = new ArrayList<>();
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysCurrent = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID +  "' ORDER BY " + memorys_Time + " DESC " + "LIMIT " + startFrom + ",5", null);
            if(cursorMemorysCurrent.moveToFirst())
            {
                do
                {
                    list.add(new MomentPost(
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Username)),
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_PID)),
                            MIID,
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Format)),
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Beschreibung)),
                            cursorMemorysCurrent.getInt(cursorMemorysCurrent.getColumnIndex(memorys_status)), //Means that its saved, and theres no status to handle.
                            cursorMemorysCurrent.getLong(cursorMemorysCurrent.getColumnIndex(memorys_Time)),
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_MessageText))
                    ));
                }
                while(cursorMemorysCurrent.moveToNext());
            }
            cursorMemorysCurrent.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllCurrentMomentPostsLimited() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public List<Object> getAllSavedGroupPostsLimited(int startFrom, String GIID)
    {
        try
        {
            List<Object> list = new ArrayList<>();
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysCurrent = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + GIID +  "' ORDER BY " + memorys_Time + " DESC " + "LIMIT " + startFrom + ",5", null);
            if(cursorMemorysCurrent.moveToFirst())
            {
                do
                {
                    if(doPostExistsInSaved(cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_PID)),
                            cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_GIID))))
                    {
                        list.add(new MomentPost(
                                cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Username)),
                                cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_PID)),
                                GIID,
                                cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Format)),
                                cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_Beschreibung)),
                                cursorMemorysCurrent.getInt(cursorMemorysCurrent.getColumnIndex(memorys_status)), //Means that its saved, and theres no status to handle.
                                cursorMemorysCurrent.getLong(cursorMemorysCurrent.getColumnIndex(memorys_Time)),
                                cursorMemorysCurrent.getString(cursorMemorysCurrent.getColumnIndex(memorys_MessageText))
                        ));
                    }
                }
                while(cursorMemorysCurrent.moveToNext());
            }
            cursorMemorysCurrent.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllSavedGroupPostsLimited() failed: " + ec);
            return new ArrayList<>();
        }
    }


    private String getLastPreviewMomentTypeFromAllMemorysSaved(String MIID) //Ist für den memoryHandler, der gibt das zuletzt gepostet bild in einem memory topic zurück.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysSaved = db.rawQuery("SELECT * FROM " + tableMemoryPostsSaved + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time, null);
            String type = "-1";
            if(cursorMemorysSaved.moveToFirst())
            {
                type = cursorMemorysSaved.getString(cursorMemorysSaved.getColumnIndex(memorys_Format));
            }

            cursorMemorysSaved.close();
            return type;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPreviewImageFromAllMemorys() failed: " + ec);
            return "-1";
        }
    }


    public String getLastPreviewMomentPidSaved(String MIID) //Ist für den memoryHandler, der gibt das zuletzt gepostet bild in einem memory topic zurück.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysSaved = db.rawQuery("SELECT * FROM " + tableMemoryPostsSaved + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            String toReturn = "-1";
            if(cursorMemorysSaved.moveToFirst())
            {
                toReturn = cursorMemorysSaved.getString(cursorMemorysSaved.getColumnIndex(memorys_PID));
            }
            cursorMemorysSaved.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPreviewImageFromAllMemorys() failed: " + ec);
            return "-1";
        }
    }


    public long getLastPostTime(String MIID) //Ist für den memoryHandler, der gibt das zuletzt gepostet bild in einem memory topic zurück.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysSaved = db.rawQuery("SELECT " + memorys_Time + " FROM " + tableMemoryPostsSaved + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            long toReturn = -1;
            if(cursorMemorysSaved.moveToFirst())
            {
                toReturn = cursorMemorysSaved.getLong(cursorMemorysSaved.getColumnIndex(memorys_Time));
            }
            cursorMemorysSaved.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPostTime() failed: " + ec);
            return -1;
        }
    }


    public long getLastPostTimeSavedMomentsAll()
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysSaved = db.rawQuery("SELECT " + memorys_Time + " FROM " + tableMemoryPostsSaved + " WHERE 1 ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            long toReturn = -1;
            if(cursorMemorysSaved.moveToFirst())
            {
                toReturn = cursorMemorysSaved.getLong(cursorMemorysSaved.getColumnIndex(memorys_Time));
            }
            cursorMemorysSaved.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPostTimeSavedMomentsAll() failed: " + ec);
            return -1;
        }
    }


    public String getLastPreviewMomentPidAktuelle(String MIID) //Ist für den memoryHandler, der gibt das zuletzt gepostet bild in einem memory topic zurück.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorMemorysSaved = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            String toReturn = "-1";
            if(cursorMemorysSaved.moveToFirst())
            {
                toReturn = cursorMemorysSaved.getString(cursorMemorysSaved.getColumnIndex(memorys_PID));
                if(toReturn == null)
                {
                    toReturn = "-1";
                }
            }
            cursorMemorysSaved.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPreviewImageFromAllMemorys() failed: " + ec);
            return "-1";
        }
    }

    public int getCountOfChatMessages(String GIID, String MyUsername)
    {
        int returnValue = -1;
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_Format + "='FTEX' AND " + memorys_status + "=" + ConversationStatusHelper.STATUS_NEW_MESSAGE
                    + " AND " + memorys_GIID + "='" + GIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);

            if(cursor.moveToFirst())
            {
                returnValue = cursor.getCount();
            }
            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getCountOfNewChatMessages() failed: " + ec);
        }
        return returnValue;
    }


    public int getGroupNotifyID(String GIID)
    {
        int returnValue = -1;
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + group_topic_CreatedTime + " FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + GIID + "'", null);

            if(cursor.moveToFirst())
            {
                returnValue = (int) cursor.getLong(cursor.getColumnIndex(group_topic_CreatedTime));
            }
            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getGroupNotifyID() failed: " + ec);
        }
        return returnValue;
    }

    public void insertNewMomentPostSaved(String Username, String Format, String MIID, String PID, long Time) //Folgendes mein kind
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, Format);
            contentValues.put(memorys_Time, Time);
            contentValues.put(memorys_GIID, MIID);
            contentValues.put(memorys_Username, Username);
            contentValues.put(memorys_PID, PID);

            if(!new CLPreferences(this.context).getUsername().equals(Username))
            {
                insertYourPostWasSeenOrSavedInMoment(Username, MIID, PID, 1, Time);
            }
            else
            {
                db.insert(tableMemoryPostsSaved, null, contentValues);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentPostSaved() failed: " + ec);
        }
    }

    public void insertNewMomentPostSavedBYSYNCHRONISATION(String Username, String Format, String MIID, String PID, long Time, int myPost,
                                                          JSONArray jsonArrayUsersSeen) //Folgendes mein kind
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, Format);
            contentValues.put(memorys_Time, Time);
            contentValues.put(memorys_GIID, MIID);
            contentValues.put(memorys_Username, Username);
            contentValues.put(memorys_PID, PID);

            if(myPost == 1) //Ich bin absender, die benutzerabfrage wird schon im server erledigt daher unnötig hier nochmal zu überprüfen.
            {
                contentValues.put(memorys_status, ConversationStatusHelper.STATUS_SENT);
                if(jsonArrayUsersSeen != null)
                {
                    for(int counter = 0; counter < jsonArrayUsersSeen.length(); counter++)
                    {
                        JSONObject jsonObject = jsonArrayUsersSeen.getJSONObject(counter);
                        insertYourPostWasSeenOrSavedInMoment(jsonObject.getString("USRN"), MIID, PID, jsonObject.getInt("SV"), jsonObject.getLong("TI"));
                    }
                }
            }
            else //Jemand anderes hat des hochgeladen
            {
                contentValues.put(memorys_status, ConversationStatusHelper.STATUS_CHAT_OPENED);
            }

            if(!doPostExists(PID, MIID))
            {
                db.insert(tableMemoryPostsAktuelle, null, contentValues);
            }

            db.insert(tableMemoryPostsSaved, null, contentValues);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentPostSaved() failed: " + ec);
        }
    }


    public void insertNewMomentTopicBYSYNCHRONISATION(String MIID, String MomentName, long timeCreated)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(group_topic_MIID, MIID);
            contentValues.put(group_topic_Title, (MomentName));
            contentValues.put(group_topic_CreatedTime, timeCreated);

            if(!doMomentTopicExists(MIID))
            {
                db.insert(tableGruppen, null, contentValues);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentTopicSaved() failed: " + ec);
        }
    }


    private boolean doMomentTopicExists(String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
            boolean valid = cursor.moveToFirst();
            cursor.close();
            return valid;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "doPostExists() failed: " + ec);
            return false;
        }
    }


    private boolean doPostExists(String PID, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);
            boolean valid = cursor.moveToFirst();
            cursor.close();
            return valid;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "doPostExists() failed: " + ec);
            return false;
        }
    }


    private boolean doPostExistsInSaved(String PID, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsSaved + " WHERE " + memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);
            boolean valid = cursor.moveToFirst();
            cursor.close();
            return valid;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "doPostExistsInSaved() failed: " + ec);
            return false;
        }
    }


    public boolean didISavedThePost(String PID, String MIID, String Username)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + memorys_GIID + " FROM " + tableMemoryPostsSaved +
                " WHERE " + memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "' LIMIT 1", null);
        boolean iSaved = false;
        if(cursor.moveToFirst())
        {
            iSaved = true;
        }

        cursor.close();
        return iSaved;
    }

    private List<String> getAllMembersOfSavedMomentLIMITED(String MIID) //Getting by posts
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + memorys_Username + " FROM " + tableMemoryPostsSaved + " WHERE " + memorys_GIID + "='" + MIID + "' LIMIT 5", null);
            List<String> toReturn = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                do
                {
                    toReturn.add(cursor.getString(cursor.getColumnIndex(memorys_Username)));
                }
                while(cursor.moveToNext());
            }
            cursor.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllMembersOfSavedMomentLIMITED() failed: " + ec);
            return new ArrayList<>();
        }
    }

    public void insertNewMomentCreated(String Admin, String creator, String MIID, String MomentName, long timeInMillis, JSONArray jsonArrayMembers, boolean leaved)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(group_topic_ADMIN, Admin);
            contentValues.put(group_topic_MIID, MIID);
            contentValues.put(group_topic_Title, MomentName);
            contentValues.put(group_topic_CreatedTime, timeInMillis);
            contentValues.put(group_topic_LastActionTime, timeInMillis);
            contentValues.put(group_topic_creator, creator);
            contentValues.put(group_topic_leaved, leaved);

            if(jsonArrayMembers != null)
            {
                for (int count = 0; count < jsonArrayMembers.length(); count++)
                {
                    JSONObject jsonObject = jsonArrayMembers.getJSONObject(count);
                    insertNewMemberOrUpdateMember(jsonObject.getString("USRN"), MIID);
                }
            }

            if(!doMomentTopicExists(MIID))
            {
                db.insert(tableGruppen, null, contentValues);
            }
            else
            {
                db.update(tableGruppen, contentValues, group_topic_MIID + "='" + MIID + "'", null);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentCreated() failed: " + ec);
        }
    }


    public void moveSingleCurrentMomentPostingsFromPidToSaved(String PID, String MIID, String UsernameWhoSave)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        Cursor cursorCurrent = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "'", null);
        if(cursorCurrent.moveToFirst())
        {
            System.out.println("TTT: moving post to saved");
            insertNewMomentPostSaved(
                    UsernameWhoSave,
                    cursorCurrent.getString(cursorCurrent.getColumnIndex(memorys_Format)),
                    cursorCurrent.getString(cursorCurrent.getColumnIndex(memorys_GIID)),
                    cursorCurrent.getString(cursorCurrent.getColumnIndex(memorys_PID)),
                    cursorCurrent.getLong(cursorCurrent.getColumnIndex(memorys_Time))
            );
        }
        cursorCurrent.close();
    }


    public void userLeftMoment(String MIID, String Username, long time)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryUsersIn, memorys_members_MIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tablePartnerSavedOrSeenYourPostings, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tableNameSeenPosts, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        insertNewShortInfo(new GroupConversationMessage(
                "",
                MIID,
                "",
                (short) -1,
                context.getResources().getString(R.string.txt_group_member_leftGroup, Username),
                "",
                false,
                ConversationStatusHelper.STATUS_NEW_MESSAGE,
                time,
                time));
    }


    public void userWasKickedFromGroup(String MIID, String Username, long time)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryUsersIn, memorys_members_MIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tablePartnerSavedOrSeenYourPostings, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        db.delete(tableNameSeenPosts, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "='" + Username + "'", null);
        insertNewShortInfo(new GroupConversationMessage(
                "",
                MIID,
                "",
                (short) -1,
                context.getResources().getString(R.string.txt_group_member_wasKicked, Username),
                "",
                false,
                ConversationStatusHelper.STATUS_NEW_MESSAGE,
                time,
                time));
    }


    public void removeSavedPostFromMomentsUserDeleted(String PID, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryPostsSaved, memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "'" , null);
        db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "'" , null);
    }


    public void unsaveMomentPost(String PID, String MIID, String Username)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryPostsSaved, memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "'" , null);
    }


    public void deleteMomentCompleteBecauseLeaved(String MIID, String MyUsername, boolean deleteAllMyPicsToo)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        if(deleteAllMyPicsToo)
        {
            db.delete(tableMemoryPostsSaved, memorys_GIID + "='" + MIID + "'", null);
            db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "'", null);
            db.delete(tableNameSeenPosts, memorys_GIID + "='" + MIID + "'", null);
            db.delete(tableMemoryUsersIn, memorys_members_MIID + "='" + MIID + "'", null);
            db.delete(tablePartnerSavedOrSeenYourPostings, memorys_GIID + "='" + MIID + "'", null);
            db.delete(tableGruppen, group_topic_MIID + "='" + MIID + "'", null);
        }
        else
        {
            db.delete(tableMemoryPostsSaved, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
            db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
            db.delete(tableNameSeenPosts, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
            db.delete(tableMemoryUsersIn, memorys_members_MIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
            db.delete(tablePartnerSavedOrSeenYourPostings, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        }
        setMyGroupMemberState(MIID, true);
    }


    public void deleteGroupBecauseYouWasKicked(String MIID, String MyUsername, long time)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryPostsSaved, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        db.delete(tableMemoryPostsAktuelle, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        db.delete(tableNameSeenPosts, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        db.delete(tableMemoryUsersIn, memorys_members_MIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);
        db.delete(tablePartnerSavedOrSeenYourPostings, memorys_GIID + "='" + MIID + "' AND " + memorys_Username + "!='" + MyUsername + "'", null);

        insertNewShortInfo(new GroupConversationMessage(
                "",
                MIID,
                "",
                (short) -1,
                context.getResources().getString(R.string.txt_group_you_removed, getAdminAktuelleMoments(MIID)),
                "",
                false,
                ConversationStatusHelper.STATUS_NEW_MESSAGE,
                time,
                time));

        setMyGroupMemberState(MIID, true);
    }


    public void setMyGroupMemberState(String GIID, boolean leaved)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(group_topic_leaved, leaved);
        db.update(tableGruppen, contentValues, group_topic_MIID + "='" + GIID + "'", null);
    }

    private long lookUpCurrentMomentTimeCreated(String MIID) throws Exception
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
        long time = 0;
        if(cursor.moveToFirst())
        {
            time = cursor.getLong(cursor.getColumnIndex(group_topic_CreatedTime));
        }
        cursor.close();

        if(time > 0)
        {
            return time;
        }
        else
        {
            throw new Exception("lookUpCurrentMomentTimeCreated() failed");
        }
    }


    public void setMomentPostStatus(String MIID, String PID, int status)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_status, status);
        db.update(tableMemoryPostsAktuelle, contentValues, memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "'", null);
    }


    public void insertYourPostWasSeenOrSavedInMoment(String Username, String MIID, String PID, int status, long time)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_Username, Username);
        contentValues.put(memorys_GIID, MIID);
        contentValues.put(memorys_status, status);
        contentValues.put(memorys_PID, PID);
        contentValues.put(memorys_Time, time);

        int updated = db.update(tablePartnerSavedOrSeenYourPostings, contentValues, memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);

        if(updated <= 0)
        {
            db.insert(tablePartnerSavedOrSeenYourPostings, null, contentValues);
        }
    }


    public void insertNewMomentPostForAktuelleForSynchronisation(String Username, String PID, String Format, String MIID, long Time, int myPost,
                                                                 int doISeenIt,
                                                                 int doISavedIt,
                                                                 JSONArray jsonArrayUsersSeen) //Achtung löscht falls nicht vorhanden und nicht upgedated.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, Format);
            contentValues.put(memorys_Time, Time);
            contentValues.put(memorys_GIID, MIID);
            contentValues.put(memorys_Username, Username);
            contentValues.put(memorys_PID, PID);

            if(myPost == 1) //Ich bin absender, die benutzerabfrage wird schon im server erledigt daher unnötig hier nochmal zu überprüfen.
            {
                contentValues.put(memorys_status, ConversationStatusHelper.STATUS_SENT);
                if(jsonArrayUsersSeen != null)
                {
                    for(int counter = 0; counter < jsonArrayUsersSeen.length(); counter++)
                    {
                        JSONObject jsonObject = jsonArrayUsersSeen.getJSONObject(counter);
                        insertYourPostWasSeenOrSavedInMoment(jsonObject.getString("USRN"), MIID, PID, jsonObject.getInt("SV"), jsonObject.getLong("TI"));
                    }
                }
            }
            else //Jemand anderes hat des hochgeladen
            {
                if(doISeenIt == 1)
                {
                    contentValues.put(memorys_status, ConversationStatusHelper.STATUS_CHAT_OPENED);
                }
                else if(doISeenIt == 0)
                {
                    contentValues.put(memorys_status, ConversationStatusHelper.STATUS_NEW_MESSAGE);
                }

                if(doISavedIt == 1)
                {
                    //insertNewMomentTopicSaved WIRD WO ANDERS HERUNTERGELADEN.
                    insertNewMomentPostSaved(Username, Format, MIID, PID, Time);
                }
            }

            if(!doPostExists(PID, MIID))
            {
                db.insert(tableMemoryPostsAktuelle, null, contentValues);
            }
            else //Update the data.
            {
                db.update(tableMemoryPostsAktuelle, contentValues, memorys_PID + "='" + PID + "'" , null);
            }

            ContentValues cvHash = new ContentValues();
            cvHash.put(group_topic_LastActionTime, Time);

            this.setUserHasNewPosts(Username, MIID);

            db.update(tableGruppen, cvHash, group_topic_MIID + "='" + MIID + "'", null); //Wenn kein moment eingetragen, dann pech gehabt. kann man nichts machen, weil dann irgendein schlimmerer fehler passiert ist.
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentPostForAktuelleForSynchronisation() failed: " + ec);
        }
    }


    public void insertNewMomentPostForAktuelle(String Username, String PID, short Format, String MIID, String Beschreibung, long Time) //Achtung löscht falls nicht vorhanden und nicht upgedated.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, Format);
            contentValues.put(memorys_Time, Time);
            contentValues.put(memorys_GIID, MIID);
            contentValues.put(memorys_Username, Username);
            contentValues.put(memorys_PID, PID);
            contentValues.put(memorys_Beschreibung, Beschreibung);
            String MyUsername = new CLPreferences(context).getUsername();

            if(MyUsername.equals(Username)) //Ich bin absender, die benutzerabfrage wird schon im server erledigt daher unnötig hier nochmal zu überprüfen.
            {
                contentValues.put(memorys_status, ConversationStatusHelper.STATUS_SENT);
            }
            else //Jemand anderes hat des hochgeladen
            {
                contentValues.put(memorys_status, ConversationStatusHelper.STATUS_NEW_MESSAGE);
            }

            if(!doPostExists(PID, MIID))
            {
                db.insert(tableMemoryPostsAktuelle, null, contentValues);
            }
            else //Update the data.
            {
                db.update(tableMemoryPostsAktuelle, contentValues, memorys_PID + "='" + PID + "'" , null);
            }

            ContentValues cvHash = new ContentValues();
            cvHash.put(group_topic_LastActionTime, Time);

            this.setUserHasNewPosts(Username, MIID);

            db.update(tableGruppen, cvHash, group_topic_MIID + "='" + MIID + "'", null); //Wenn kein moment eingetragen, dann pech gehabt. kann man nichts machen, weil dann irgendein schlimmerer fehler passiert ist.
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMomentPostForAktuelle() failed: " + ec);
        }
    }


    public void insertNewTextMessage(GroupConversationMessage groupConversationMessage) //Achtung löscht falls nicht vorhanden und nicht upgedated.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, groupConversationMessage.getType());
            contentValues.put(memorys_Time, groupConversationMessage.getUhrzeit());
            contentValues.put(memorys_GIID, groupConversationMessage.getGIID());
            contentValues.put(memorys_Username, groupConversationMessage.getAbsender());
            contentValues.put(memorys_PID, "");
            contentValues.put(memorys_MessageHash, groupConversationMessage.getMessageHash());
            contentValues.put(memorys_MessageText, groupConversationMessage.getMessageText());
            String MyUsername = new CLPreferences(context).getUsername();
            contentValues.put(memorys_status, groupConversationMessage.getMessageStatus());

            db.insert(tableMemoryPostsAktuelle, null, contentValues);

            ContentValues cvHash = new ContentValues();
            cvHash.put(group_topic_LastActionTime, groupConversationMessage.getUhrzeit());

            this.setUserHasNewPosts(groupConversationMessage.getAbsender(), groupConversationMessage.getGIID());

            db.update(tableGruppen, cvHash, group_topic_MIID + "='" + groupConversationMessage.getGIID() + "'", null); //Wenn kein moment eingetragen, dann pech gehabt. kann man nichts machen, weil dann irgendein schlimmerer fehler passiert ist.
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewTextMessage() failed: " + ec);
        }
    }


    public void insertNewShortInfo(GroupConversationMessage groupConversationMessage) //Achtung löscht falls nicht vorhanden und nicht upgedated.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_Format, groupConversationMessage.getType());
            contentValues.put(memorys_Time, groupConversationMessage.getUhrzeit());
            contentValues.put(memorys_GIID, groupConversationMessage.getGIID());
            contentValues.put(memorys_Username, groupConversationMessage.getAbsender());
            contentValues.put(memorys_PID, "");
            contentValues.put(memorys_MessageHash, groupConversationMessage.getMessageHash());
            contentValues.put(memorys_MessageText, groupConversationMessage.getMessageText());
            contentValues.put(memorys_status, groupConversationMessage.getMessageStatus());

            db.insert(tableMemoryPostsAktuelle, null, contentValues);

            ContentValues cvHash = new ContentValues();
            cvHash.put(group_topic_LastActionTime, groupConversationMessage.getUhrzeit());

            this.setUserHasNewPosts(groupConversationMessage.getAbsender(), groupConversationMessage.getGIID());

            db.update(tableGruppen, cvHash, group_topic_MIID + "='" + groupConversationMessage.getGIID() + "'", null); //Wenn kein moment eingetragen, dann pech gehabt. kann man nichts machen, weil dann irgendein schlimmerer fehler passiert ist.
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewShortInfo() failed: " + ec);
        }
    }


    private MomentPost getLastPostetThingInAktuellenMoment(String MIID)
    {
        try
        {
            MomentPost memoryPostSingle = null;
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            if(cursor.moveToFirst())
            {
                memoryPostSingle = new MomentPost(
                        cursor.getString(cursor.getColumnIndex(memorys_Username)),
                        cursor.getString(cursor.getColumnIndex(memorys_PID)),
                        MIID,
                        cursor.getString(cursor.getColumnIndex(memorys_Format)),
                        cursor.getString(cursor.getColumnIndex(memorys_Beschreibung)),
                        cursor.getInt(cursor.getColumnIndex(memorys_status)),
                        cursor.getLong(cursor.getColumnIndex(memorys_Time)),
                        cursor.getString(cursor.getColumnIndex(memorys_MessageText)));
            }
            cursor.close();
            return memoryPostSingle;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPostetThingInAktuellenMoment() failed: " + ec);
            return null;
        }
    }


    private MomentPost getLastMomentPostInSaved(String MIID)
    {
        try
        {
            MomentPost memoryPostSingle = null;
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsSaved + " WHERE " + memorys_GIID + "='" + MIID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            if(cursor.moveToFirst())
            {
                memoryPostSingle = new MomentPost(
                        cursor.getString(cursor.getColumnIndex(memorys_Username)),
                        cursor.getString(cursor.getColumnIndex(memorys_PID)),
                        MIID,
                        cursor.getString(cursor.getColumnIndex(memorys_Format)),
                        cursor.getString(cursor.getColumnIndex(memorys_Beschreibung)),
                        cursor.getInt(cursor.getColumnIndex(memorys_status)),
                        cursor.getLong(cursor.getColumnIndex(memorys_Time)),
                        cursor.getString(cursor.getColumnIndex(memorys_MessageText)));
            }
            cursor.close();
            return memoryPostSingle;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastMomentPostInSaved() failed: " + ec);
            return null;
        }
    }



    public List<GalleryItem> getSavedByCards(long nextDayMillis)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        List<GalleryItem> messages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLGroups.super.getReadableDatabase();

            if(nextDayMillis <= 0)
            {
                Cursor cursor = db.rawQuery("SELECT " +
                        memorys_Time + ", " + memorys_PID + ", " + memorys_Format + " FROM " +
                        tableMemoryPostsSaved + " WHERE " + memorys_Format + "!='FTEX' AND " + memorys_Format + "!='INF'" + " ORDER BY " + memorys_Time + " DESC LIMIT 1", null);

                if(cursor.moveToFirst())
                {
                    messages.add(new GalleryItem(
                            simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(memorys_Time))),
                            cursor.getString(cursor.getColumnIndex(memorys_PID)),
                            cursor.getShort(cursor.getColumnIndex(memorys_Format)),
                            cursor.getLong(cursor.getColumnIndex(memorys_Time)),
                            false));
                }
                cursor.close();
            }
            else
            {
                Cursor cursor = db.rawQuery("SELECT " +
                        memorys_Time + ", " + memorys_PID + ", " + memorys_Format + " FROM " +
                        tableMemoryPostsSaved + " WHERE " + memorys_Format + "!='FTEX' AND " + memorys_Format + "!='INF' AND " + memorys_Time + "<=" + nextDayMillis + " ORDER BY " + memorys_Time + " DESC LIMIT 1", null);

                if(cursor.moveToFirst())
                {
                    messages.add(new GalleryItem(
                            simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(memorys_Time))),
                            cursor.getString(cursor.getColumnIndex(memorys_PID)),
                            cursor.getShort(cursor.getColumnIndex(memorys_Format)),
                            cursor.getLong(cursor.getColumnIndex(memorys_Time)),
                            false));
                }
                cursor.close();
            }
            return messages;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SQLGroups(getSavedByCards()): " + ec);
            return new ArrayList<>();
        }
    }


    public MomentPost getMomentPost(String MIID, String PID)
    {
        try
        {
            MomentPost memoryPostSingle = null;
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_GIID + "='" + MIID + "' AND " + memorys_PID + "='" + PID + "' ORDER BY " + memorys_Time + " DESC LIMIT 1", null);
            if(cursor.moveToFirst())
            {
                memoryPostSingle = new MomentPost(
                        cursor.getString(cursor.getColumnIndex(memorys_Username)),
                        cursor.getString(cursor.getColumnIndex(memorys_PID)),
                        MIID,
                        cursor.getString(cursor.getColumnIndex(memorys_Format)),
                        cursor.getString(cursor.getColumnIndex(memorys_Beschreibung)),
                        cursor.getInt(cursor.getColumnIndex(memorys_status)),
                        cursor.getLong(cursor.getColumnIndex(memorys_Time)),
                        cursor.getString(cursor.getColumnIndex(memorys_MessageText)));
            }
            cursor.close();
            return memoryPostSingle;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getLastPostetThingInAktuellenMoment() failed: " + ec);
            return null;
        }
    }


    public List<Object> getAllMembersOfAktuellenMomentLIMITED(int startFrom, String MIID)
    {
        try
        {
            List<Object> toReturn = new ArrayList<>();
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryUsersIn + " WHERE " + memorys_members_MIID + "='" + MIID + "' LIMIT " + startFrom + ",5", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    toReturn.add(new InternMomentMember(cursor.getString(cursor.getColumnIndex(memorys_members_Username)),
                            getCountOfUserPostingsAktuelle(cursor.getString(cursor.getColumnIndex(memorys_members_Username)), MIID),
                            cursor.getInt(cursor.getColumnIndex(memorys_members_hasNewPostings)),
                            this.checkIfICanSaveSomePostsFromUser(cursor.getString(cursor.getColumnIndex(memorys_members_Username)), MIID)));
                }
                while(cursor.moveToNext());
            }
            cursor.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllMembersOfAktuellenMomentLIMITED() failed: " + ec);
            return null;
        }
    }

    public boolean isUserAdmin(String Username, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + group_topic_ADMIN + " FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
            boolean isAdmin = false;
            if(cursor.moveToFirst())
            {
                if(Username.equals(cursor.getString(cursor.getColumnIndex(group_topic_ADMIN))))
                {
                    isAdmin = true;
                }
            }

            cursor.close();
            return isAdmin;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "isUserAdmin() failed: " + ec);
            return false;
        }
    }


    private List<String> getMomentsUserIsInSavedMomentsPosts(String Username)
    {
        List<String> toReturn = new ArrayList<>();
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsSaved + " WHERE " + memorys_Username + "='" + Username + "'", null);

        if(cursor.moveToFirst())
        {
            do
            {
                toReturn.add(cursor.getString(cursor.getColumnIndex(memorys_members_MIID)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return toReturn;
    }


    public boolean existUserInGroup(String Username, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryUsersIn + " WHERE " + memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_Username + "='" + Username + "'", null);
            boolean toReturn = cursor.moveToFirst();
            cursor.close();
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "existUserInGroup() failed: " + ec);
            return false;
        }
    }


    private boolean checkIfICanSaveSomePostsFromUser(String Username, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getReadableDatabase();
        Cursor cursorAktuelle = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        if(cursorAktuelle.moveToFirst())
        {
            do
            {
                if(!doPostExistsInSaved(cursorAktuelle.getString(cursorAktuelle.getColumnIndex(memorys_PID)), MIID))
                {
                    cursorAktuelle.close();
                    return true;
                }
            }
            while(cursorAktuelle.moveToNext());
        }
        cursorAktuelle.close();
        return false;
    }


    private int getCountOfUserPostingsAktuelle(String Username, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        if(cursor.moveToFirst())
        {
            int anzahl = cursor.getCount();
            cursor.close();
            return anzahl;
        }
        else
        {
            cursor.close();
            return 0;
        }
    }


    public void insertNewMemberOrUpdateMember(String username, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_members_Username, username);
            contentValues.put(memorys_members_MIID, MIID);
            db.insert(tableMemoryUsersIn, null, contentValues);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewMemberOrUpdateMember() failed: " + ec);
        }
    }



    public void setUserHasNoMoreNewPosts(String Username, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_members_hasNewPostings, 0);
        db.update(tableMemoryUsersIn, contentValues, memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_Username + "='" + Username + "'", null);
    }



    public void setUserHasNewPosts(String Username, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(memorys_members_hasNewPostings, 1);
        db.update(tableMemoryUsersIn, contentValues, memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_Username + "='" + Username + "'", null);
    }



    public String getAdminAktuelleMoments(String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + group_topic_ADMIN + " FROM " + tableGruppen + " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
            String Admin = "";
            if(cursor.moveToFirst())
            {
                Admin = cursor.getString(cursor.getColumnIndex(group_topic_ADMIN));
            }

            cursor.close();
            return Admin;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAdminAktuelleMoments() failed: " + ec);
            return null;
        }
    }


    public void removeAllUserDataBlockedMode(String Username)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryPostsSaved, memorys_Username + "='" + Username + "'", null);
        db.delete(tableNameSeenPosts, memorys_Username + "='" + Username + "'", null);
        db.delete(tablePartnerSavedOrSeenYourPostings, memorys_Username + "='" + Username + "'", null);
        db.delete(tableMemoryPostsAktuelle, memorys_Username + "='" + Username + "'", null);
    }

    public void removeAllUserDataFromGroup(String Username, String MIID)
    {
        SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
        db.delete(tableMemoryPostsSaved, memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        db.delete(tableNameSeenPosts, memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        db.delete(tablePartnerSavedOrSeenYourPostings, memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        db.delete(tableMemoryPostsAktuelle, memorys_Username + "='" + Username + "' AND " + memorys_GIID + "='" + MIID + "'", null);
    }

    public List<Object> getAllSavedPostsFromDay(int startFrom, long startMillis, long endMillis)
    {
        String MyUsername = new CLPreferences(this.context).getUsername();
        List<Object> groupConversationMessages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE " + memorys_Time + ">=" + startMillis
                    + " AND " + memorys_Time + "<=" + endMillis + " AND " + memorys_Format + "!='FTEX' AND " + memorys_Format + "!='INF'" + " ORDER BY " + memorys_Time
                    + " LIMIT " + startFrom + ", 10", null);

            if(cursorDownloaded.moveToFirst())
            {
                do
                {
                    groupConversationMessages.add(new GroupConversationMessage(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Username)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_GIID)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                            cursorDownloaded.getShort(cursorDownloaded.getColumnIndex(memorys_Format)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_MessageText)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Beschreibung)),
                            didISavedThePost(cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                                    cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_GIID)), MyUsername),
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(memorys_status)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_MessageHash)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_Time))
                    ));
                }
                while(cursorDownloaded.moveToNext());
            }

            cursorDownloaded.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllConversationMessagesFromGroup() failed: " + ec);
        }

        return groupConversationMessages;
    }

    public void setUserAllowedToSeeYourPicture(String UsernameOfFriend, String PID, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_status, 1); //SAVED IF EXISTS.

            db.update(tablePartnerSavedOrSeenYourPostings, contentValues, memorys_Username + "='" + UsernameOfFriend + "' AND " +
                    memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setUserAllowedToSeeYourPicture() failed:" + ec);
        }
    }


    public void setUserNotAllowedToSeeYourPicture(String UsernameOfFriend, String PID, String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_status, 0); //SEEN IF EXISTS.

            db.update(tablePartnerSavedOrSeenYourPostings, contentValues, memorys_Username + "='" + UsernameOfFriend + "' AND " +
                    memorys_PID + "='" + PID + "' AND " + memorys_GIID + "='" + MIID + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setUserNotAllowedToSeeYourPicture() failed: " + ec);
        }
    }


    public List<GroupConversationMessage> getAllUnsentTextMessages()
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursorDownloaded = db.rawQuery("SELECT * FROM " + tableMemoryPostsAktuelle + " WHERE "
                    + memorys_status + "=" + ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE + " AND " +
                    memorys_Format + "='FTEX'" +
                    " ORDER BY " + memorys_Time, null);

            List<GroupConversationMessage> groupConversationMessages = new ArrayList<>();

            if(cursorDownloaded.moveToFirst())
            {
                do
                {
                    groupConversationMessages.add(new GroupConversationMessage(
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_Username)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_GIID)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_PID)),
                            cursorDownloaded.getShort(cursorDownloaded.getColumnIndex(memorys_Format)),
                            cursorDownloaded.getString(cursorDownloaded.getColumnIndex(memorys_MessageText)),
                            "",
                            false,
                            cursorDownloaded.getInt(cursorDownloaded.getColumnIndex(memorys_status)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_MessageHash)),
                            cursorDownloaded.getLong(cursorDownloaded.getColumnIndex(memorys_Time))
                    ));
                }
                while(cursorDownloaded.moveToNext());
            }
            cursorDownloaded.close();
            return groupConversationMessages;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllUnsentTextMessages() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public void setGroupNeedSynch(String MIID, boolean synch) //If you were added in group.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(group_topic_need_synch, synch);
            db.update(tableGruppen, contentValues, group_topic_MIID + "='" + MIID + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setGroupNeedSynch() failed: " + ec);
        }
    }

    public boolean needGroupSynchBecauseJoined(String MIID)
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + group_topic_need_synch + " FROM " + tableGruppen +
            " WHERE " + group_topic_MIID + "='" + MIID + "'", null);
            boolean needSynch = false;

            if(cursor.moveToFirst())
            {
                needSynch = cursor.getInt(cursor.getColumnIndex(group_topic_need_synch)) > 0;
            }

            cursor.close();
            return needSynch;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "needGroupSynch() failed: " + ec);
            return false;
        }
    }

    public List<String> getNeedGroupSynchAktuellenNutzerDaten(String MIID)
    {
        List<String> list = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + memorys_members_Username + " FROM " + tableMemoryUsersIn +
                    " WHERE " + memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_needSynch + "=1", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    list.add(cursor.getString(cursor.getColumnIndex(memorys_members_Username)));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getNeedGroupSynchAktuellenNutzerDaten() failed: " + ec);
        }
        return list;
    }

    public List<String> needGroupSynchSavedNutzerDaten(String MIID)
    {
        List<String> list = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + memorys_members_Username + " FROM " + tableMemoryUsersIn +
                    " WHERE " + memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_needSynchSavedPostings + "=1", null);

            if(cursor.moveToFirst())
            {
                do
                {
                    list.add(cursor.getString(cursor.getColumnIndex(memorys_members_Username)));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "needGroupSynchSavedNutzerDaten() failed: " + ec);
        }
        return list;
    }

    public void setUserNeedSynchAktuellePostings(String MIID, String Username, boolean needSynch) //User joined, need saved synch aktuelle postings from him.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_members_needSynch, needSynch);
            db.update(tableMemoryUsersIn, contentValues, memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_Username + "='" + Username + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setUserNeedSynchAktuellePostings() failed: " + ec);
        }
    }

    public void setUserNeedSynchSavedPostings(String MIID, String Username, boolean needSynch) //User joined, need saved synch aktuelle postings from him.
    {
        try
        {
            SQLiteDatabase db = SQLGroups.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(memorys_members_needSynchSavedPostings, needSynch);
            db.update(tableMemoryUsersIn, contentValues, memorys_members_MIID + "='" + MIID + "' AND " + memorys_members_Username + "='" + Username + "'", null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setUserNeedSynchAktuellePostings() failed: " + ec);
        }
    }

}
