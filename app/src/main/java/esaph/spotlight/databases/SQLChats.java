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

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphDays;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphWeeks;
import esaph.spotlight.R;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentAudio;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentEmojie;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentImage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentText;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentVideo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatShared;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemImage;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemMainMoments;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemVideo;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UndeliveredMessageHandling.UndeliveredChatSeenMessage;

public class SQLChats extends SQLSpotlight
{
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";

    private static final String tableNameTextualMessagesSeenPosts = "SeenPosts";
    private static final String tableNameTextualMessagesSeenChats = "SeenChats";


    public static final String chat_PID = "PID";
    private static final String chat_Beschreibung = "Beschreibung"; //OB BILD ODER VIDEO ODER TYPE_TEXT

    private static final String tableNameSavedPosts = "SAVP";
    private static final String ts_SAVED_EINTRAG_UNIQUE_ID = "_savedid";
    private static final String ts_PRIMARY_ID_KEY_FROM_POST = "_id_post";
    private static final String ts_CHAT_POST_SAVER_ID = "_ksid";


    public static final String tableNamePostings = "TTM";
    private static final String chatGlobalAbsender = "ABS";
    private static final String chatGlobalTime = "TIME";
    private static final String rec_Status = "State";
    public static final String chatGlobalType = "Type";
    private static final String chatGlobalAbsenderUsername = "GAbsender";


    private static final String chatInfo_ID_FROMMESSAGEID = "CPID";
    private static final String chatInfo_CurrentState = "CState"; //INformation what happend to a conversationmessage, like state 1 the conversationmessage was unsaved etc from bla.


    private static final String chatAudio_AID = "AID";


    private static final String chatSticker_LSID = "LSID";
    private static final String chatSticker_LSPID = "LSPID";


    private static final String chatSmiley_DATA = "SDAT";


    private static final String chatText_message = "Nachricht";
    private static final String chatGlobalJSONPloppInformaton = "JPINF";


    private static final String tableNameTextualMessagesChatShared = "Shared";
    private static final String chatShared_POSTFROM = "PF";
    private static final String chatShared_Type = "Type";
    private static final String chatShared_empfaenger = "ChatPartner";
    private static final String chatShared_OriginalBeschreibung = "OriDesc";
    private static final String chatShared_absender = "Absender";
    private static final String chatShared_time = "Uhrzeit";
    private static final String chatShared_PID = "PID";
    private static final String chatShared_status = "Status";
    private static final String chatSharedISaved = "ISAVED";

    private static final String tableNameTextualMessagesChats = "CCHATTING";
    private static final String RECEIVERS_KEY_CHAT = "_idf";
    private static final String createTableChats = "create table if not exists " +
            tableNameTextualMessagesChats +
            " ("
            + RECEIVERS_KEY_CHAT + " INTEGER)";


    private static final String createTableSaved = "create table if not exists " + tableNameSavedPosts +
            " (" +
            ts_SAVED_EINTRAG_UNIQUE_ID + " INTEGER PRIMARY KEY autoincrement, " +
            ts_CHAT_POST_SAVER_ID + " INTEGER, " +
            ts_PRIMARY_ID_KEY_FROM_POST + " INTEGER)";


    public static final String MESSAGE_ID = "_mid";
    private static final String SERVER_POST_ID = "_sppid";
    private static final String createTableTextualMessages = "create table if not exists " + tableNamePostings +
            " (" +
            MESSAGE_ID + " INTEGER PRIMARY KEY autoincrement, " +
            SERVER_POST_ID + " INTEGER ," +
            chatGlobalAbsender + " INTEGER, " +
            chatGlobalAbsenderUsername + " TEXT, " +
            chatGlobalType + " SHORT, " +
            chatGlobalTime + " INTEGER, " +
            chat_Beschreibung + " TEXT, " +
            chatGlobalJSONPloppInformaton + " TEXT, " +
            chat_PID + " TEXT, " +
            chatInfo_CurrentState + " SHORT, " +
            chatText_message + " TEXT, " +
            chatInfo_ID_FROMMESSAGEID + " INTEGER, " +
            chatAudio_AID + " TEXT, " +
            chatSticker_LSID + " INTEGER, " +
            chatSticker_LSPID + " INTEGER, " +
            chatSmiley_DATA + " TEXT)";


    private static final String tableReceivers = "receivers";
    private static final String RECEIVERS_MSG_ID_POST = "_ID";


    private static final String createTableReceivers = "create table if not exists " + tableReceivers +
            " (" +
            RECEIVERS_MSG_ID_POST + " INTEGER, " +
            RECEIVERS_KEY_CHAT + " INTEGER, " +
            rec_Status + " SHORT)";



    private static final String SEEN_ID = "_IDSE";
    private static final String createTableSeenPosts = "create table if not exists " + tableNameTextualMessagesSeenPosts +
            " (" +
            SEEN_ID + " INTEGER PRIMARY KEY autoincrement, " +
            chatGlobalAbsender + " INTEGER, " +
            MESSAGE_ID + " INTEGER, " +
            chatGlobalTime + " INTEGER)";


    private static final String createTableSeenChats = "create table if not exists " + tableNameTextualMessagesSeenChats +
            " (" +
            SEEN_ID + " INTEGER PRIMARY KEY autoincrement, " +
            chatGlobalAbsender + " INTEGER, " +
            chatGlobalTime + " INTEGER)";



    private static final String createTableChatShared = "create table if not exists " + tableNameTextualMessagesChatShared +
            " (" +
            chatShared_POSTFROM + " TEXT, " +
            chatShared_empfaenger + " TEXT, " +
            chatShared_absender + " TEXT, " +
            chatShared_OriginalBeschreibung + " TEXT, " +
            chatShared_PID + " TEXT, " +
            chatShared_status + " SHORT, " +
            chatShared_Type + " SHORT, " +
            chatSharedISaved + " SHORT, " +
            chatShared_time + " INTEGER)";


    public SQLChats(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }


    private static final String QUERY_GetPostsPassedDeadline =
            "SELECT * FROM (SELECT * FROM " + tableNamePostings + " WHERE ("
                    + chatGlobalType + "=" + CMTypes.FPIC + " OR " + chatGlobalType + "=" + CMTypes.FVID
                    + ") AND "
                    + chatGlobalTime + "<=? AND NOT EXISTS(SELECT NULL FROM "
            + tableNameSavedPosts + " WHERE " +
                    tableNamePostings + "." + MESSAGE_ID + "=" +
                    tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST
            + "))";

    public void removePostsPassedDeadline(long timeMinusDay)
    {
        SQLHashtags sqlHashtags = null;
        try
        {
            sqlHashtags = new SQLHashtags(context);
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetPostsPassedDeadline, new String[]{
                    Long.toString(timeMinusDay)
            });

            if(cursor.moveToFirst())
            {
                do
                {
                    sqlHashtags.removeHashtagWithid(cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)));


                    db.delete(tableNamePostings, chatInfo_ID_FROMMESSAGEID + "=" + cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)), null);

                    db.delete(tableReceivers, RECEIVERS_MSG_ID_POST + "=" + cursor.getLong(cursor.getColumnIndex(MESSAGE_ID))
                            , null);

                    StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT, context, cursor.getString(cursor.getColumnIndex(chat_PID)));
                }
                while(cursor.moveToNext());
            }

            cursor.close();

            db.delete(tableNamePostings,chatGlobalTime + "<=? AND NOT EXISTS(SELECT NULL FROM "
                    + tableNameSavedPosts + " WHERE " +
                    tableNamePostings + "." + MESSAGE_ID + "=" +
                    tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST
                    + ")", new String[]{
                            Long.toString(timeMinusDay)
            });

            db.delete(tableNameTextualMessagesSeenPosts, chatGlobalTime + "<=" + timeMinusDay, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePostsPassedDeadline() failed: " + ec);
        }
        finally
        {
            if(sqlHashtags != null)
            {
                sqlHashtags.close();
            }
        }
    }


    public void dropTableChats()
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            Log.i(getClass().getName(), "DROPPING DOWN");
            db.delete(tableNamePostings, null, null);
            db.delete(tableReceivers, null, null);
            //Do not drop table started chats.
            db.delete(tableNameSavedPosts, null, null);
            db.delete(tableNameTextualMessagesSeenPosts, null, null);
            db.delete("sqlite_sequence", null, null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTableChats() failed: " + ec);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableReceivers);
        db.execSQL(createTableSaved);
        db.execSQL(createTableChats);
        db.execSQL(createTableTextualMessages);
        db.execSQL(createTableSeenPosts);
        db.execSQL(createTableSeenChats);
        db.execSQL(createTableChatShared);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableSeenChats);
    }


    public PostSeenUntransmitted insertISeenNewPost(ConversationMessage conversationMessage)
    {
        SQLiteDatabase db = SQLChats.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(chatGlobalAbsender, conversationMessage.getABS_ID());
        contentValues.put(MESSAGE_ID, conversationMessage.getMESSAGE_ID());
        contentValues.put(chatGlobalTime, System.currentTimeMillis());
        long ID = db.insert(tableNameTextualMessagesSeenPosts, null, contentValues);

        return new PostSeenUntransmitted(ID, conversationMessage);
    }


    public List<PostSeenUntransmitted> getPostThatNeedToBeTransmitedToServer(long ablaufTime) //DIESE EINTRÄGE SIND DIE WELCHE ANGEKLICKT WURDEN.
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableNameTextualMessagesSeenPosts + " WHERE " + chatGlobalTime + " >= " + ablaufTime, null);
            List<PostSeenUntransmitted> list = new ArrayList<>();

            if(cursor.moveToFirst())
            {
                do
                {
                    ConversationMessage conversationMessage = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                            cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)));

                    if(conversationMessage != null)
                    {
                        list.add(new PostSeenUntransmitted((cursor.getLong(cursor.getColumnIndex(SEEN_ID))),
                                conversationMessage));
                    }
                }
                while(cursor.moveToNext());
            }
            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getPostThatNeedToBeTransmitedToServer() failed: " + ec);
            return new ArrayList<>();
        }
    }

    public void removePostThatWasSeen(PostSeenUntransmitted postSeenUntransmitted) //INTERN, FROM ME
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            db.delete(tableNameTextualMessagesSeenPosts, SEEN_ID + "=" + postSeenUntransmitted.get_ID(), null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removePostThatWasSeen() failed: " + ec);
        }
    }

    private static final String queryGetAllUnsentMessages =
            "SELECT * FROM " + tableNamePostings + " JOIN "
                    + tableReceivers + " ON "
                    + tableNamePostings + "." + MESSAGE_ID + "=" + tableReceivers + "." + RECEIVERS_MSG_ID_POST
                    + " AND "
                    + tableReceivers + "." + rec_Status +
            "=" + ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE;

    public List<ConversationMessage> getAllUnsentMessages()
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        List<ConversationMessage> list = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(SQLChats.queryGetAllUnsentMessages, null);

            if(cursor.moveToFirst())
            {
                do {
                    switch (cursor.getShort(cursor.getColumnIndex(rec_Status)))
                    {
                        case CMTypes.FTEX:
                            list.add(new ChatTextMessage(
                                    cursor.getString(cursor.getColumnIndex(chatText_message)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FAUD:
                            list.add(new AudioMessage(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;


                            case CMTypes.FPIC:
                                list.add(new ChatImage(
                                        cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                        cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                        cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                        cursor.getShort(cursor.getColumnIndex(rec_Status)),


                                        
                                        cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                        cursor.getString(cursor.getColumnIndex(chat_PID)),
                                        
                                        cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                        ));
                                break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;

                        case CMTypes.FSTI:
                            EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                                if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                            //I will implement a function to save this in own db.
                            {
                                esaphSpotLightSticker = new EsaphSpotLightSticker(
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                        "",
                                        -1);
                            }

                            list.add(new EsaphStickerChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    esaphSpotLightSticker,
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FEMO:
                            list.add(new EsaphAndroidSmileyChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                    }
                }
                while(cursor.moveToNext());
            }

            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllUnsentMessages() failed: " + ec);
        }
        finally {
            if(sqlSticker != null)
            {
                sqlSticker.close();
            }
        }

        return list;
    }

    private static final String getCountOfNewChatMessages = "SELECT COUNT(*) FROM " + tableReceivers +
            " WHERE " + RECEIVERS_KEY_CHAT + "=? AND " + rec_Status + "="
            + ConversationStatusHelper.STATUS_NEW_MESSAGE;

    public synchronized int getCountOfNewChatMessages(long ABSENDER_ID)
    {
        int returnValue = -1;
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            Cursor cursor = db.rawQuery(getCountOfNewChatMessages, new String[]{
                    Long.toString(ABSENDER_ID)
            });

            if(cursor.moveToFirst())
            {
                returnValue = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
            }
            cursor.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getCountOfNewChatMessages() failed: " + ec);
        }
        return returnValue;
    }

    public void unSavePrivateMomentPostDeleteWhenPassedTime(long ID_SAVED_EINTRAG,
                                                            long MESSAGE_ID,
                                                            boolean shouldDeleted,
                                                            long CHAT_ID)
    {
        if(shouldDeleted)
        {
            deletePostInChat(MESSAGE_ID, CHAT_ID);
            removePostSaved(ID_SAVED_EINTRAG);
        }
        else
        {
            removePostSaved(ID_SAVED_EINTRAG);
        }
    }

    public void deletePostInChat(long KEY_MESSAGE_ID, long CHAT_ID)
    {
        SQLiteDatabase db = SQLChats.super.getWritableDatabase();

        db.delete(tableReceivers, RECEIVERS_MSG_ID_POST + "=" + KEY_MESSAGE_ID
                + " AND " + RECEIVERS_KEY_CHAT + "=" + CHAT_ID
                , null);

        db.delete(tableNameSavedPosts, ts_PRIMARY_ID_KEY_FROM_POST + "=" + KEY_MESSAGE_ID
        + " AND (" + ts_CHAT_POST_SAVER_ID + "=" + CHAT_ID + " OR " + ts_CHAT_POST_SAVER_ID + "=" + SpotLightLoginSessionHandler.getLoggedUID() + ")", null); //Deleting all savings from chat.

        db.delete(tableNamePostings, chatInfo_ID_FROMMESSAGEID + "=" + KEY_MESSAGE_ID + " AND " +
                chatGlobalAbsender + "=" + CHAT_ID
                , null);

        db.delete(tableNameTextualMessagesSeenPosts, MESSAGE_ID + "=" + KEY_MESSAGE_ID, null);

        if(!postHasStillReceivers(KEY_MESSAGE_ID))
        {
            Cursor cursor = db.rawQuery("SELECT " + chat_PID + " FROM " + tableNamePostings + " WHERE " + MESSAGE_ID + "=? LIMIT 1", new String[]{Long.toString(KEY_MESSAGE_ID)});
            if(cursor.moveToFirst())
            {
                StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT,
                        context,
                        cursor.getString(cursor.getColumnIndex(chat_PID)));
            }

            cursor.close();

            db.delete(tableNamePostings, MESSAGE_ID + "=" + KEY_MESSAGE_ID, null);
            db.delete(tableNameSavedPosts, ts_PRIMARY_ID_KEY_FROM_POST + "=" + KEY_MESSAGE_ID, null); //Deleting all savings to be sure.

            SQLHashtags sqlHashtags = new SQLHashtags(context);
            sqlHashtags.removeHashtagWithid(KEY_MESSAGE_ID); //No need to check if hashtag died, because genius has coded it.
            sqlHashtags.close();
        }
    }

    private boolean postHasStillReceivers(long KEY_MESSAGE_ID)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NULL FROM " + tableReceivers + " WHERE " + RECEIVERS_MSG_ID_POST + "=" + KEY_MESSAGE_ID, null);
        boolean has = false;
        if(cursor.moveToFirst())
        {
            has = true;
        }
        cursor.close();
        return has;
    }

    private static final String QUERY_UpdateAllChatMessagesAsReaded = "UPDATE " +
            tableReceivers + " SET "
            + rec_Status + "=? WHERE EXISTS(SELECT * FROM "
            + tableNamePostings + " AS P WHERE P."
            + MESSAGE_ID
            + "="
            + RECEIVERS_MSG_ID_POST + " AND " + chatGlobalAbsender + "=? AND " +
            chatGlobalType + "!=" + CMTypes.FVID + " AND " +
            chatGlobalType + "!=" + CMTypes.FPIC +
            ")";

    public void updateAllChatMessagesTextAsRead(long UID_PARTNER)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            db.execSQL(QUERY_UpdateAllChatMessagesAsReaded, new String[]{
                    Short.toString(ConversationStatusHelper.STATUS_CHAT_OPENED),
                    Long.toString(UID_PARTNER)});
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateAllChatMessagesTextAsRead() failed: " + ec);
        }
    }


    public synchronized void insertNewPrivateChatMessage(ChatTextMessage chatTextMessage, JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, chatTextMessage.getABS_ID());
            contentValues.put(chatText_message, chatTextMessage.getTextMessage());
            contentValues.put(SERVER_POST_ID, -1);
            contentValues.put(chatGlobalTime, chatTextMessage.getMessageTime());
            contentValues.put(chatGlobalType, chatTextMessage.getType());
            contentValues.put(chatGlobalAbsenderUsername, chatTextMessage.getAbsender());
            contentValues.put(chatGlobalJSONPloppInformaton, chatTextMessage.getEsaphPloppInformationsJSON().toString());


            long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
            chatTextMessage.setMESSAGE_ID(ID_POST);
            insertReceivers(db, chatTextMessage, jsonArrayReceivers);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewPrivateChatMessage() failed: " + ec);
        }
    }

    public void insertNewAudio(AudioMessage audioMessage, JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();


            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, audioMessage.getABS_ID());
            contentValues.put(chatAudio_AID, audioMessage.getAID());
            contentValues.put(chatGlobalTime, audioMessage.getMessageTime());
            contentValues.put(chatGlobalType, audioMessage.getType());
            contentValues.put(chatGlobalAbsenderUsername, audioMessage.getAbsender());
            contentValues.put(chatGlobalJSONPloppInformaton, audioMessage.getEsaphPloppInformationsJSON().toString());


            long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
            audioMessage.setMESSAGE_ID(ID_POST);
            insertReceivers(db, audioMessage, jsonArrayReceivers);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewAudio() failed: " + ec);
        }
    }

    public void insertNewInfoStateMessage(ChatInfoStateMessage chatInfoStateMessage, JSONArray jsonArrayReceivers)
    {
        if(chatInfoStateMessage == null)
            return;

        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, chatInfoStateMessage.getABS_ID());
            contentValues.put(chatInfo_ID_FROMMESSAGEID, chatInfoStateMessage.getConversationMessageFrom().getMESSAGE_ID());
            contentValues.put(chatGlobalTime, chatInfoStateMessage.getMessageTime());
            contentValues.put(chatInfo_CurrentState, chatInfoStateMessage.getSTATE_CODE());
            contentValues.put(chatGlobalType, CMTypes.FINF);
            contentValues.put(chatGlobalAbsenderUsername, chatInfoStateMessage.getAbsender());

            long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
            chatInfoStateMessage.setMESSAGE_ID(ID_POST);
            insertReceivers(db, chatInfoStateMessage, jsonArrayReceivers);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewInfoStateMessage() failed: " + ec);
        }
    }

    public void insertNewSticker(EsaphStickerChatObject esaphStickerChatObject, JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, esaphStickerChatObject.getABS_ID());
            contentValues.put(chatSticker_LSID, esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_ID());
            contentValues.put(chatSticker_LSPID, esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_PACK_ID());
            contentValues.put(chatGlobalTime, esaphStickerChatObject.getMessageTime());
            contentValues.put(chatGlobalType, esaphStickerChatObject.getType());
            contentValues.put(chatGlobalAbsenderUsername, esaphStickerChatObject.getAbsender());
            contentValues.put(chatGlobalJSONPloppInformaton, esaphStickerChatObject.getEsaphPloppInformationsJSON().toString());

            long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
            esaphStickerChatObject.setMESSAGE_ID(ID_POST);
            insertReceivers(db, esaphStickerChatObject, jsonArrayReceivers);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewSticker() failed: " + ec);
        }
    }


    public void insertNewEmojieMessage(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject, JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, esaphAndroidSmileyChatObject.getABS_ID());
            contentValues.put(chatSmiley_DATA, esaphAndroidSmileyChatObject.getEsaphEmojie().getEMOJIE());
            contentValues.put(chatGlobalTime, esaphAndroidSmileyChatObject.getMessageTime());
            contentValues.put(chatGlobalType, esaphAndroidSmileyChatObject.getType());
            contentValues.put(chatGlobalAbsenderUsername, esaphAndroidSmileyChatObject.getAbsender());
            contentValues.put(chatGlobalJSONPloppInformaton, esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON().toString());

            long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
            esaphAndroidSmileyChatObject.setMESSAGE_ID(ID_POST);
            insertReceivers(db, esaphAndroidSmileyChatObject, jsonArrayReceivers);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewEmojieMessage() failed: " + ec);
        }
    }


    private void insertReceivers(SQLiteDatabase db, ConversationMessage conversationMessage, JSONArray jsonArrayReceivers)
    {
        long OWN_UID = SpotLightLoginSessionHandler.getLoggedUID();
        try
        {
            ContentValues contentValues = new ContentValues();

            int sizeJSON = jsonArrayReceivers.length();
            for(int countJSON = 0; countJSON < sizeJSON; countJSON++)
            {
                contentValues.clear();

                JSONObject jsonObjectReceiver = jsonArrayReceivers.getJSONObject(countJSON);
                System.out.println("DEBUG ARRAY REC: " + jsonObjectReceiver.getLong("REC_ID"));
                short status = (short) jsonObjectReceiver.getInt("ST");
                if(status == 1)
                {
                    if(conversationMessage.getABS_ID() != SpotLightLoginSessionHandler.getLoggedUID())
                    {
                        status = ConversationStatusHelper.STATUS_NEW_MESSAGE;
                    }
                }

                long KEY_FRIENDS = jsonObjectReceiver.getLong("REC_ID");
                if(OWN_UID == KEY_FRIENDS) //Muss immer partner uid id sein, welche den chat identifiziert.
                {
                    KEY_FRIENDS = conversationMessage.getABS_ID();
                }

                contentValues.put(rec_Status, status);
                contentValues.put(RECEIVERS_MSG_ID_POST, conversationMessage.getMESSAGE_ID());
                contentValues.put(RECEIVERS_KEY_CHAT, KEY_FRIENDS);

                db.insert(tableReceivers, null, contentValues);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertReceivers() failed: " + ec);
        }
    }


    public void insertNewConversationMessage(ChatImage chatImage,
                                             List<EsaphHashtag> esaphHashtags,
                                             List<SavedInfo> savedInfoList,
                                             JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(SERVER_POST_ID, chatImage.getSERVER_ID());
            contentValues.put(chat_Beschreibung, chatImage.getBeschreibung());
            contentValues.put(chat_PID, chatImage.getIMAGE_ID());
            contentValues.put(chatGlobalType, chatImage.getType());
            contentValues.put(chatGlobalTime, chatImage.getMessageTime());
            contentValues.put(chatGlobalAbsender, chatImage.getABS_ID());
            contentValues.put(chatGlobalAbsenderUsername, chatImage.getAbsender());

            long id_lookUp = lookUpInternIDFromServerID(chatImage.getSERVER_ID());
            if(id_lookUp > -1) //When post exists, do not add.
            {
                chatImage.setMESSAGE_ID(id_lookUp);
            }
            else
            {
                long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
                chatImage.setMESSAGE_ID(ID_POST);
            }

            insertReceivers(db, chatImage, jsonArrayReceivers);


            int listSize = savedInfoList.size();
            for(int counter = 0; counter < listSize; counter++)
            {
                SavedInfo savedInfo = savedInfoList.get(counter);
                insertPostSaved(
                        savedInfo.getUSER_ID_SAVED(),
                        chatImage.getMESSAGE_ID());
            }

            SQLHashtags sqlHashtags = new SQLHashtags(context);
            try
            {
                sqlHashtags.addNewHashtag(chatImage.getABS_ID(),
                        chatImage.getMESSAGE_ID(),
                        esaphHashtags);
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "insertNewConversationMessage_Hashtag(): " + ec);
            }
            finally
            {
                sqlHashtags.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewConversationMessage() failed: " + ec);
        }
    }


    public void insertNewConversationMessage(ChatVideo chatVideo,
                                             List<EsaphHashtag> esaphHashtags,
                                             List<SavedInfo> savedInfoList,
                                             JSONArray jsonArrayReceivers)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(SERVER_POST_ID, chatVideo.getSERVER_ID());
            contentValues.put(chat_Beschreibung, chatVideo.getBeschreibung());
            contentValues.put(chat_PID, chatVideo.getIMAGE_ID());
            contentValues.put(chatGlobalType, chatVideo.getType());
            contentValues.put(chatGlobalTime, chatVideo.getMessageTime());
            contentValues.put(chatGlobalAbsenderUsername, chatVideo.getAbsender());
            contentValues.put(chatGlobalAbsender, chatVideo.getABS_ID());

            System.out.println("DEBUG ARRAY PPID: " + chatVideo.getSERVER_ID());

            long id_lookUp = lookUpInternIDFromServerID(chatVideo.getSERVER_ID());
            if(id_lookUp > -1)
            {
                chatVideo.setMESSAGE_ID(id_lookUp);
            }
            else
            {
                long ID_POST = db.insert(SQLChats.tableNamePostings, null, contentValues);
                chatVideo.setMESSAGE_ID(ID_POST);
            }

            insertReceivers(db, chatVideo, jsonArrayReceivers);

            int listSize = savedInfoList.size();
            for(int counter = 0; counter < listSize; counter++)
            {
                SavedInfo savedInfo = savedInfoList.get(counter);
                insertPostSaved(
                        savedInfo.getUSER_ID_SAVED(),
                        chatVideo.getMESSAGE_ID());
            }

            SQLHashtags sqlHashtags = new SQLHashtags(context);
            try
            {
                sqlHashtags.addNewHashtag(chatVideo.getABS_ID(),
                        chatVideo.getMESSAGE_ID(),
                        esaphHashtags);
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "insertNewConversationMessage_Hashtag(): " + ec);
            }
            finally
            {
                sqlHashtags.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewConversationMessage() failed: " + ec);
        }
    }

    private static final String QUERY_GetPostOnlyByIDAndChatKey =
            "SELECT * FROM (SELECT * FROM " + tableNamePostings + " WHERE " + MESSAGE_ID + "=? LIMIT 1) AS Post JOIN "
            + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_MSG_ID_POST + "= Post."
            + MESSAGE_ID + " AND " + tableReceivers + "." + RECEIVERS_KEY_CHAT + "=? LIMIT 1";

    public ConversationMessage getPostByInternIdAndChatKey(long INTER_ID, long KEY_CHAT) //Need one receiver
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetPostOnlyByIDAndChatKey, new String[]{
                    Long.toString(INTER_ID),
                    Long.toString(KEY_CHAT),
            });

            ConversationMessage conversationMessage = null;

            if(cursor.moveToFirst())
            {
                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FTEX:
                        conversationMessage = (new ChatTextMessage(
                                cursor.getString(cursor.getColumnIndex(chatText_message)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FPIC:
                        conversationMessage = new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),

                                
                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;


                    case CMTypes.FVID:
                        conversationMessage = new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                
                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;

                    case CMTypes.FAUD:
                        conversationMessage = (new AudioMessage(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FSTI:
                        EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                        if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                            //I will implement a function to save this in own db.
                        {
                            esaphSpotLightSticker = new EsaphSpotLightSticker(
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                    "",
                                    -1);
                        }


                        conversationMessage = new EsaphStickerChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                esaphSpotLightSticker,
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));

                        break;

                    case CMTypes.FEMO:
                        conversationMessage = new EsaphAndroidSmileyChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));
                        break;

                    case CMTypes.FINF:
                        ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                        conversationMessage = new ChatInfoStateMessage(
                                conversationMessageState,
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)));
                        break;
                }
            }

            cursor.close();
            return conversationMessage;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getPostByInternIdAndChatKey() failed: " + ec);
            return null;
        }
        finally
        {
            sqlSticker.close();
        }
    }


    private static final String QUERY_GetPostOnlyByID =
            "SELECT * FROM (SELECT * FROM " + tableNamePostings + " WHERE " + MESSAGE_ID + "=? LIMIT 1) AS Post JOIN "
                    + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_MSG_ID_POST + "= Post."
                    + MESSAGE_ID  + " LIMIT 1";

    public ConversationMessage getPostByInternId(long INTER_ID)
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetPostOnlyByID, new String[]{
                    Long.toString(INTER_ID),
            });

            ConversationMessage conversationMessage = null;

            if(cursor.moveToFirst())
            {
                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FTEX:
                        conversationMessage = (new ChatTextMessage(
                                cursor.getString(cursor.getColumnIndex(chatText_message)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FPIC:
                        conversationMessage = new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),


                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;


                    case CMTypes.FVID:
                        conversationMessage = new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),

                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;

                    case CMTypes.FAUD:
                        conversationMessage = (new AudioMessage(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FSTI:
                        EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                        if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                        //I will implement a function to save this in own db.
                        {
                            esaphSpotLightSticker = new EsaphSpotLightSticker(
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                    "",
                                    -1);
                        }

                        conversationMessage = new EsaphStickerChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                esaphSpotLightSticker,
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));

                        break;

                    case CMTypes.FEMO:
                        conversationMessage = new EsaphAndroidSmileyChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));
                        break;

                    case CMTypes.FINF:
                        ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                        conversationMessage = new ChatInfoStateMessage(
                                conversationMessageState,
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;
                }
            }

            cursor.close();
            return conversationMessage;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getPostByInternId() failed: " + ec);
            return null;
        }
        finally
        {
            sqlSticker.close();
        }
    }


    private static final String QUERY_GetPostByServerIDAndChatKey =
            "SELECT * FROM (SELECT * FROM " + tableNamePostings + " WHERE " + SERVER_POST_ID + "=? LIMIT 1) AS Post JOIN "
                    + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_MSG_ID_POST + "= Post."
                    + MESSAGE_ID + " AND " + tableReceivers + "." + RECEIVERS_KEY_CHAT + "=? LIMIT 1";

    public ConversationMessage getPostByServerIdAndChatKey(long ServerID, long UID_PARTNER)
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        //IT RETURN THE FIRST POST HE FIND. WARNING IT DOES NOT FILTER IT FOR A CHAT CONVERSATION.
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetPostByServerIDAndChatKey, new String[]{
                    Long.toString(ServerID),
                    Long.toString(UID_PARTNER)
            });

            ConversationMessage conversationMessage = null;

            if(cursor.moveToFirst())
            {
                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FTEX:
                        conversationMessage = (new ChatTextMessage(
                                cursor.getString(cursor.getColumnIndex(chatText_message)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FPIC:
                        conversationMessage = new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;


                    case CMTypes.FVID:
                        conversationMessage = new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;

                    case CMTypes.FAUD:
                        conversationMessage = (new AudioMessage(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FSTI:
                        EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                        if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                        //I will implement a function to save this in own db.
                        {
                            esaphSpotLightSticker = new EsaphSpotLightSticker(
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                    "",
                                    -1);
                        }

                        conversationMessage = new EsaphStickerChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                esaphSpotLightSticker,
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));

                        break;

                    case CMTypes.FEMO:
                        conversationMessage = new EsaphAndroidSmileyChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));
                        break;

                    case CMTypes.FINF:
                        ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                        conversationMessage = new ChatInfoStateMessage(
                                conversationMessageState,
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;
                }
            }

            cursor.close();
            return conversationMessage;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getPostByInternIdAndChatKey() failed: " + ec);
            return null;
        }
        finally {
            sqlSticker.close();
        }
    }

    private static final String QUERY_GetImagesAndVidsOldestLimited =
            "SELECT * FROM " + tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ")"
                    + " AND " + chatGlobalType + "=" + CMTypes.FPIC +
                    " ORDER BY " + chatGlobalTime + " LIMIT ?, 30";

    public List<ConversationMessage> getImagesAndVidsBackOldest(int startFrom)
    {
        List<ConversationMessage> list = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetImagesAndVidsOldestLimited, new String[]{
                    Integer.toString(startFrom)
            });

            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                             list.add(new ChatImage(
                                     cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                     cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))));
                            break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }
                }
                while(cursor.moveToNext());

                cursor.close();
            }
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getImagesAndVidsBackOldest() failed: " + ec);
        }

        return list;
    }

    private static final String QUERY_GetLastConversationMessageOnlyImageAndVideo =
            "SELECT * FROM " + tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ")" + " ORDER BY " + chatGlobalTime + " DESC LIMIT 1";
    public ConversationMessage getLastConversationMessageOnlyImageAndVideo()
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetLastConversationMessageOnlyImageAndVideo, null);

            ConversationMessage conversationMessage = null;
            if(cursor.moveToFirst())
            {
                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FPIC:
                        conversationMessage = (new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                ));
                        break;


                    case CMTypes.FVID:
                        conversationMessage = (new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                ));
                        break;
                }
            }
            cursor.close();

            return conversationMessage;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getLastConversationMessageOnlyImageAndVideo() failed: " + ec);
            return null;
        }
    }

    private static final String QUERY_GetLastConversationMessageByYearSeason =
            "SELECT * FROM " + tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ") AND "
                    + chatGlobalTime +
                    ">=? AND " + chatGlobalTime
            + "<? ORDER BY " + chatGlobalTime + " DESC LIMIT 1";

    public ConversationMessage getLastConversationMessageFromSeason(long seasonStart, long seasonEnd)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetLastConversationMessageByYearSeason, new String[]{
                    Long.toString(seasonStart),
                    Long.toString(seasonEnd)
            });

            ConversationMessage conversationMessage = null;
            if(cursor.moveToFirst())
            {
                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FPIC:
                        conversationMessage = (new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                ));
                        break;


                    case CMTypes.FVID:
                        conversationMessage = (new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),


                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                ));
                        break;
                }
            }
            cursor.close();

            return conversationMessage;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getLastConversationMessageFromSeason() failed: " + ec);
            return null;
        }
    }


    private static final String QUERY_GetConversationMessageByYearSeasonLimited =
            "SELECT * FROM " + tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ") AND "
                    + chatGlobalTime +
                    ">=? AND " + chatGlobalTime
                    + "<? ORDER BY " + chatGlobalTime + " DESC LIMIT ?, 30";
    public List<ConversationMessage> getConversationMessagesFromSeasonLimited(int startFrom, long seasonStart, long seasonEnd)
    {
        try
        {
            List<ConversationMessage> list = new ArrayList<>();
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetConversationMessageByYearSeasonLimited, new String[]{
                    Long.toString(seasonStart),
                    Long.toString(seasonEnd),
                    Integer.toString(startFrom)
            });

            if(cursor.moveToFirst())
            {
                do {

                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            list.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }
            cursor.close();

            return list;
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "getConversationMessagesFromSeasonLimited() failed: " + ec);
            return new ArrayList<>();
        }
    }

    private static final String QUERY_GetAllPosts =
            "SELECT *" +
                    " FROM " + tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ") "
            + " GROUP BY " +
                    chat_PID
                    + " ORDER BY " + chatGlobalTime + " DESC LIMIT ?, 50";

    public List<Object> getByAllWithDatum(int limit, long endOfLastday)
    {
        long cachedTime = System.currentTimeMillis();
        List<Object> messages = new ArrayList<>();
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursor = db.rawQuery(QUERY_GetAllPosts, new String[]{
                    Integer.toString(limit)
            });

            if(cursor.moveToFirst())
            {
                do
                {
                    if(endOfLastday >= cursor.getLong(cursor.getColumnIndex(chatGlobalTime)) || endOfLastday <= -1)
                    {
                        Date dateTime1 = new Date(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
                                stringBuilder.append(simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                            }
                        }

                        messages.add(new DatumList(stringBuilder.toString(), simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))), cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                    }


                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            messages.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            messages.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }


                    final Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
            Log.i(getClass().getName(), "SQLChats(getByAllWithDatum()): " + ec);
            return new ArrayList<>();
        }
    }

/*
    private static final String query_GetSingleChatPartner =
            "SELECT * FROM (SELECT * FROM " + tableReceivers + " WHERE " + RECEIVERS_KEY_CHAT + "=?) AS REC_TABLE JOIN "
            + tableNamePostings + " ON " + tableNamePostings + "." + MESSAGE_ID + "= REC_TABLE." + RECEIVERS_MSG_ID_POST
            + " ORDER BY " + tableNamePostings + "." + chatGlobalTime + " DESC LIMIT 1";*/


    private static final String query_GetSingleChatPartner =
            "SELECT * FROM (SELECT * FROM (SELECT *," +
                    SQLFriends.tableFriends + "." + SQLFriends.FR_UID + " AS SPECIALKEY" +
                    " FROM " +
                    SQLFriends.tableFriends + " WHERE " +
                    SQLFriends.FR_UID + "=?) AS ChatPartnerTable LEFT JOIN "
                    + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_KEY_CHAT
                    + "=SPECIALKEY GROUP BY SPECIALKEY) AS REC_TABLE LEFT JOIN "
                    + tableNamePostings + " ON " + tableNamePostings + "." + MESSAGE_ID + "= REC_TABLE." + RECEIVERS_MSG_ID_POST + " ORDER BY "
                    + tableNamePostings + "." + chatGlobalTime + " DESC LIMIT 1";


    public ChatPartner getSingleChatPartner(long CHAT_ID)
    {
        try
        {
            ChatPartner chatPartner = null;
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
            SQLSticker sqlSticker = new SQLSticker(context);

            Cursor cursor = sqLiteDatabase.rawQuery(query_GetSingleChatPartner, new String[]
                    {
                            Long.toString(CHAT_ID)
                    });


            ConversationMessage conversationMessageLast = null;
            if(cursor.moveToFirst())
            {
                int index = cursor.getColumnIndex(chatGlobalType);
                if (cursor.isNull(index))
                {
                    chatPartner = new ChatPartner(
                            cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                            cursor.getString(cursor.getColumnIndex(SQLFriends.FR_VORNAME)),
                            cursor.getString(cursor.getColumnIndex(SQLFriends.FR_DESCRIPTION_PLOPP)),
                            cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                            null,
                            cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0);
                    return chatPartner;
                }


                switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                {
                    case CMTypes.FTEX:
                        conversationMessageLast = (new ChatTextMessage(
                                cursor.getString(cursor.getColumnIndex(chatText_message)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FPIC:
                        conversationMessageLast = new ChatImage(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;


                    case CMTypes.FVID:
                        conversationMessageLast = new ChatVideo(
                                cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),

                                cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                cursor.getString(cursor.getColumnIndex(chat_PID)),

                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;

                    case CMTypes.FAUD:
                        conversationMessageLast = (new AudioMessage(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                        break;

                    case CMTypes.FSTI:
                        EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                        if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                        //I will implement a function to save this in own db.
                        {
                            esaphSpotLightSticker = new EsaphSpotLightSticker(
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                    "",
                                    -1);
                        }

                        conversationMessageLast = new EsaphStickerChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                esaphSpotLightSticker,
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));

                        break;

                    case CMTypes.FEMO:
                        conversationMessageLast = new EsaphAndroidSmileyChatObject(
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                
                                cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));
                        break;

                    case CMTypes.FINF:
                        ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                        conversationMessageLast = new ChatInfoStateMessage(
                                conversationMessageState,
                                cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                );
                        break;
                }

                chatPartner = new ChatPartner(
                        cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                        cursor.getString(cursor.getColumnIndex(SQLFriends.FR_VORNAME)),

                        cursor.getString(cursor.getColumnIndex(SQLFriends.FR_DESCRIPTION_PLOPP)),
                        cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                        conversationMessageLast,
                        cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0);
            }

            cursor.close();
            sqlSticker.close();
            return chatPartner;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getSingleChatPartner() failed: " + ec);
            return null;
        }
    }


    private static final String QUERY_GetMostPersonsFromSeason =
            "SELECT COUNT(*), " +
                    chatGlobalAbsender +
                    ", " +
                    RECEIVERS_KEY_CHAT +
                    " FROM (SELECT DISTINCT " +
                    chatGlobalAbsender + ", " + RECEIVERS_KEY_CHAT + " FROM " +
                    tableNamePostings +
                    " WHERE (" +
                    chatGlobalType + "=" + CMTypes.FVID
                    + " OR " + chatGlobalType + "=" + CMTypes.FPIC
                    + ") AND "

                    + chatGlobalTime + ">=?" +
                    " AND " + chatGlobalTime +
                    "<?) AS distinctified ORDER BY COUNT(*)";

    private static final String query_GetAllChatsLimited = //Get all users from cchatting, than join on table receivers with chat key, than from this table join on posts.
            "SELECT * FROM (SELECT * FROM (SELECT * FROM (SELECT *," +
                    SQLFriends.tableFriends + "." + SQLFriends.FR_UID + " AS SPECIALKEY" +
                    " FROM " +
                    SQLFriends.tableFriends + " WHERE " +
                    SQLFriends.FR_FRIENDSHIP_DIED + "=0) AS ChatPartnerTable LEFT JOIN "
            + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_KEY_CHAT
            + "=SPECIALKEY GROUP BY SPECIALKEY) AS REC_TABLE LEFT JOIN "
            + tableNamePostings + " ON " + tableNamePostings + "." + MESSAGE_ID + "= REC_TABLE." + RECEIVERS_MSG_ID_POST + " ORDER BY "
            + tableNamePostings + "." + chatGlobalTime + ") AS MASTER_TABLE WHERE 1 ORDER BY MASTER_TABLE." + chatGlobalTime + " DESC LIMIT ?, 20";


    public List<ChatPartner> getAktuelleChats(int startFrom)
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        List<ChatPartner> chatPartnersMainList = new ArrayList<>();

        try
        {
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(query_GetAllChatsLimited, new String[]
                    {Integer.toString(startFrom)});

            ConversationMessage conversationMessageLast = null;
            if(cursor.moveToFirst())
            {
                do
                {
                    int index = cursor.getColumnIndex(chatGlobalType);
                    if (cursor.isNull(index))
                    {
                        chatPartnersMainList.add(new ChatPartner(
                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_VORNAME)),

                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_DESCRIPTION_PLOPP)),
                                        cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                                null,
                                cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0));
                        continue;
                    }

                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FTEX:
                            conversationMessageLast = (new ChatTextMessage(
                                    cursor.getString(cursor.getColumnIndex(chatText_message)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FPIC:
                            conversationMessageLast = new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    );
                            break;


                        case CMTypes.FVID:
                            conversationMessageLast = new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    );
                            break;

                        case CMTypes.FAUD:
                            conversationMessageLast = (new AudioMessage(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FSTI:
                            EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                            if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                            //I will implement a function to save this in own db.
                            {
                                esaphSpotLightSticker = new EsaphSpotLightSticker(
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                        "",
                                        -1);
                            }

                            conversationMessageLast = new EsaphStickerChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    esaphSpotLightSticker,
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));

                            break;

                        case CMTypes.FEMO:
                            conversationMessageLast = new EsaphAndroidSmileyChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)));
                            break;

                        case CMTypes.FINF:
                            ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                            conversationMessageLast = new ChatInfoStateMessage(
                                    conversationMessageState,
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    );
                            break;
                    }

                    if(conversationMessageLast != null)
                    {
                        chatPartnersMainList.add(new ChatPartner(
                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_VORNAME)),

                                cursor.getString(cursor.getColumnIndex(SQLFriends.FR_DESCRIPTION_PLOPP)),
                                cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                                conversationMessageLast,
                                cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0));
                    }
                }
                while(cursor.moveToNext());
            }

            cursor.close();


            return chatPartnersMainList;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SQLChats(getAktuelleChats()) failed: " + ec);
            return chatPartnersMainList;
        }
        finally
        {
            if(sqlSticker != null)
            {
                sqlSticker.close();
            }
        }
    }


    public List<Object> getPersonalMomentsSavedWithDatum(long KEY_CHAT, int limit, long endOfLastday) //<=
    {
        try
        {
            long cachedTime = System.currentTimeMillis();
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetSavedPostsLimited, new String[]{
                    Long.toString(KEY_CHAT),
                    Long.toString(KEY_CHAT),
                    Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                    Integer.toString(limit)
            });

            List<Object> messages = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                do
                {
                    if(endOfLastday >= cursor.getLong(cursor.getColumnIndex(chatGlobalTime)) || endOfLastday <= -1)
                    {
                        Date dateTime1 = new Date(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
                                stringBuilder.append(simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                            }
                        }

                        messages.add(new DatumList(stringBuilder.toString(), simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))), cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                        System.out.println("ADDIN DATUM HOLDER: " + simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                    }


                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            messages.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            messages.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }

                    final Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
            Log.i(getClass().getName(), "SQLChats(getPersonalMomentsSavedWithDatum()): " + ec);
            return new ArrayList<>();
        }
    }



    private static final String QUERY_GetSavedPostsOnlyOneAbsender = "SELECT * FROM (SELECT * FROM " + tableReceivers
            + " WHERE " + RECEIVERS_KEY_CHAT + "=? AND EXISTS (SELECT NULL FROM " +
        tableNameSavedPosts +
        " WHERE " +
        tableReceivers + "." + RECEIVERS_MSG_ID_POST + "=" + tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST
            + " AND (" + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?"
            + " OR " + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?))"

            + ") AS REC JOIN " + tableNamePostings + " ON " + tableNamePostings + "." + MESSAGE_ID + "=REC." + RECEIVERS_MSG_ID_POST + " AND "
            + tableNamePostings + "." + chatGlobalAbsender + "=? ORDER BY " + tableNamePostings + "." + chatGlobalTime + " DESC LIMIT ?, 100";

    public List<Object> getPersonalMomentsSavedWithDatumOnlyFromOneAbsender(long ABSENDER, long KEY_CHAT, int limit, long endOfLastday) //<=
    {
        try
        {
            long cachedTime = System.currentTimeMillis();
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetSavedPostsOnlyOneAbsender, new String[]{
                    Long.toString(KEY_CHAT),
                    Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                    Long.toString(KEY_CHAT),
                    Long.toString(ABSENDER),
                    Integer.toString(limit)
            });

            List<Object> messages = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                do
                {
                    if(endOfLastday >= cursor.getLong(cursor.getColumnIndex(chatGlobalTime)) || endOfLastday <= -1)
                    {
                        Date dateTime1 = new Date(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
                                stringBuilder.append(simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                            }
                        }

                        messages.add(new DatumList(stringBuilder.toString(), simpleDateFormat.format(cursor.getLong(cursor.getColumnIndex(chatGlobalTime))), cursor.getLong(cursor.getColumnIndex(chatGlobalTime))));
                    }

                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            messages.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            messages.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }

                    final Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(chatGlobalTime)));
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
            Log.i(getClass().getName(), "SQLChats(getPersonalMomentsSavedWithDatumOnlyFromOneAbsender()): " + ec);
            return new ArrayList<>();
        }
    }


    private static final String QUERY_GetSavedPostsLimited =
                    "SELECT * FROM " +
                    " (SELECT * FROM " +
                    tableReceivers +
                    " WHERE "
                    + RECEIVERS_KEY_CHAT + "=?" +
                    " AND EXISTS (SELECT NULL FROM " +
                    tableNameSavedPosts +
                    " WHERE " +
                    tableReceivers + "." + RECEIVERS_MSG_ID_POST + "=" + tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST +

                            " AND (" + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?"
        + " OR " + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?)" +

                    ")) AS RECS JOIN " + tableNamePostings + " ON " +
                            tableNamePostings + "." + MESSAGE_ID + "= RECS." + RECEIVERS_MSG_ID_POST + " ORDER BY "
            + tableNamePostings + "." + chatGlobalTime + " DESC LIMIT ?, 100";


    private static final String QUERY_GetSavedPostsLimitedCount = "SELECT COUNT(*) As AllCount FROM " + tableReceivers + " WHERE " +
            RECEIVERS_KEY_CHAT + "=? AND " +
            " EXISTS(SELECT NULL FROM "+ tableNameSavedPosts + " WHERE "
            + tableReceivers + "." + RECEIVERS_MSG_ID_POST + "=" + tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST
            + " AND (" + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?"
            + " OR " + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?))";

    public int getPersonalMomentsSavedCount(long CHAT_ID)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_GetSavedPostsLimitedCount, new String[]{
                Long.toString(CHAT_ID),
                Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                Long.toString(CHAT_ID)
        });

        int count = 0;
        if(cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("AllCount"));
        }
        cursor.close();
        return count;
    }


    public List<Object> getPersonalMomentsSaved(long CHAT_ID, int limit) //<=
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetSavedPostsLimited, new String[]{
                    Long.toString(CHAT_ID),
                    Long.toString(CHAT_ID),
                    Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                    Integer.toString(limit)
            });

            List<Object> messages = new ArrayList<>();
            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            messages.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            messages.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return messages;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SQLChats(getPersonalMomentsSaved()): " + ec);
            return new ArrayList<>();
        }
    }

    public void removeAllUserData(long CHAT_ID)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();

        db.delete(tableNamePostings,chatGlobalAbsender + "=" + CHAT_ID, null);
        db.delete(tableNameTextualMessagesSeenPosts, chatGlobalAbsender + "=" + CHAT_ID, null);

        Cursor cursorGetPartnerPostings = db.rawQuery("SELECT " + MESSAGE_ID + ","
                + chat_PID
                + " FROM " + tableNamePostings + " WHERE " + chatGlobalAbsender + "=" + CHAT_ID, null);
        if(cursorGetPartnerPostings.moveToFirst())
        {
            do {
                long POST_ID = cursorGetPartnerPostings.getLong(cursorGetPartnerPostings.getColumnIndex(MESSAGE_ID));

                db.delete(tableReceivers,
                        RECEIVERS_MSG_ID_POST + "="
                                + POST_ID, null);

                db.delete(tableNameSavedPosts, ts_PRIMARY_ID_KEY_FROM_POST + "=" + POST_ID
                        + " AND (" + ts_CHAT_POST_SAVER_ID + "=" + CHAT_ID + " OR " + ts_CHAT_POST_SAVER_ID + "=" + SpotLightLoginSessionHandler.getLoggedUID() + ")", null); //Deleting all savings from chat.


                db.delete(tableNameTextualMessagesSeenPosts, MESSAGE_ID + "=" + POST_ID, null);

                StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT,
                        context,
                        cursorGetPartnerPostings.getString(cursorGetPartnerPostings.getColumnIndex(chat_PID)));
            }
            while(cursorGetPartnerPostings.moveToNext());
        }
        cursorGetPartnerPostings.close();
    }

    public void removeAllChatMessagesBetweenPartner(long CHAT_ID)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        db.delete(tableNamePostings, RECEIVERS_KEY_CHAT + "=" + CHAT_ID + " AND " +
                chatGlobalType + "=" + CMTypes.FTEX,null);
    }

    private static final String query_loadMoreFromChat =
            "SELECT * FROM (SELECT * FROM " + tableReceivers + " WHERE " +
                    RECEIVERS_KEY_CHAT + "=?) As Partner JOIN " + tableNamePostings
            + " ON " + tableNamePostings + "." + MESSAGE_ID + "= Partner." + RECEIVERS_MSG_ID_POST
                    + " ORDER BY " + chatGlobalTime + " DESC LIMIT ?, 30";

    public List<Object> getAllCurrentTextualMessages(long UID_PARTNER, int startFrom)
    {
        SQLSticker sqlSticker = new SQLSticker(context);
        try
        {
            List<Object> list = new ArrayList<>();
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(query_loadMoreFromChat,
                    new String[]{Long.toString(UID_PARTNER),
                            Integer.toString(startFrom)});

            if(cursor.moveToFirst())
            {
                do {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FTEX:
                            list.add(new ChatTextMessage(
                                    cursor.getString(cursor.getColumnIndex(chatText_message)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                            cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FAUD:
                            list.add(new AudioMessage(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FPIC:
                            list.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;

                        case CMTypes.FSTI:
                            EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                            if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                            //I will implement a function to save this in own db.
                            {
                                esaphSpotLightSticker = new EsaphSpotLightSticker(
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                        "",
                                        -1);
                            }

                            list.add(new EsaphStickerChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    esaphSpotLightSticker,
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;

                        case CMTypes.FINF:
                            ConversationMessage conversationMessageState = getPostByInternIdAndChatKey(cursor.getLong(cursor.getColumnIndex(chatInfo_ID_FROMMESSAGEID)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)));

                            list.add(new ChatInfoStateMessage(
                                    conversationMessageState,
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(chatInfo_CurrentState)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;

                        case CMTypes.FEMO:
                            list.add(new EsaphAndroidSmileyChatObject(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton))));
                            break;
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllCurrentTextualMessages() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            if(sqlSticker != null)
            {
                sqlSticker.close();
            }
        }
    }

    private static final String Query_GetAllMyUploadedPostsToday =
            "SELECT * FROM " + tableNamePostings + " WHERE " +
                    chatGlobalTime + " > ('NOW' - 'INTERVAL 24 HOUR') AND "
        + " ((" + chatGlobalType + "=" + CMTypes.FPIC
                            + ") OR (" + chatGlobalType + "=" + CMTypes.FVID
                    +  ")) " +
                    " AND " +
                    chatGlobalAbsender + "=?" +
                    " ORDER BY " + chatGlobalTime + " DESC LIMIT ?, 10";

    public List<Object> getTodayMyPosts(int startFrom)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = null;
        List<Object> list = new ArrayList<>();

        try
        {
            cursor = db.rawQuery(Query_GetAllMyUploadedPostsToday, new String[]{Integer.toString(startFrom),
            Long.toString(SpotLightLoginSessionHandler.getLoggedUID())});

            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            list.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getTodayMyPosts() failed: " + ec);
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


    private static final String QUERY_GetTodayConversationMessagesBetweenPartners =
            "SELECT * FROM " +
                    tableNamePostings
                    + " WHERE " +
                    chatGlobalTime + " > ('NOW' - 'INTERVAL 24 HOUR') AND " +
                    " ("
                    + RECEIVERS_KEY_CHAT + "=?) AND "
                    + " ((" + chatGlobalType + "=" + CMTypes.FPIC
                    + ") OR (" + chatGlobalType + "=" + CMTypes.FVID
                    +  ")) AND "
                    + "(" + chatGlobalTime + " >=?) GROUP BY " + chat_PID + " ORDER BY " + chatGlobalTime + " DESC LIMIT ?, 20";

    public List<Object> getTodayConversationMessagesBetweenPartners(long CHAT_ID, int startFrom)
    {

        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = null;
        List<Object> list = new ArrayList<>();

        try
        {
            cursor = db.rawQuery(QUERY_GetTodayConversationMessagesBetweenPartners, new String[]{
                    Long.toString(CHAT_ID),
            Integer.toString(startFrom)});

            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            list.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            list.add(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),




                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getTodayConversationMessagesBetweenPartners() failed: " + ec);
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



    private static final String QUERY_GetAllUnsavedFromMeConversationMessagesTodayUnsorted =
            "SELECT * FROM " +
                    tableNamePostings
                    + " WHERE ("
                    + RECEIVERS_KEY_CHAT + "=?) AND (" +
                    chatGlobalTime + " > ('NOW' - 'INTERVAL 24 HOUR')) " +
                    " AND NOT EXISTS(SELECT NULL FROM " +
                    tableNameSavedPosts + " WHERE " + tableNamePostings + "." + MESSAGE_ID + "="
            + tableNameSavedPosts + "." + ts_PRIMARY_ID_KEY_FROM_POST
                    + " AND (" + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?"
                    + " OR " + tableNameSavedPosts + "." + ts_CHAT_POST_SAVER_ID + "=?))";

    public List<ConversationMessage> getALLUnsavedFromMeConversationMessagesTodayUNSORTED(int CHAT_ID)
    {

        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = null;
        List<ConversationMessage> list = new ArrayList<>();

        try
        {
            cursor = db.rawQuery(QUERY_GetAllUnsavedFromMeConversationMessagesTodayUnsorted, new String[]{Long.toString(CHAT_ID)});

            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            list.add(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))));
                            break;


                        case CMTypes.FVID:
                                    list.add(new ChatVideo(
                                            cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                            cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getALLUnsavedFromMeConversationMessagesTodayUNSORTED() failed: " + ec);
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




    private static final String QUERY_todayLastConversationMessageChatPartner =
            "SELECT * FROM " +
                    tableNamePostings + " WHERE ("
                    + RECEIVERS_KEY_CHAT + "=?) AND " +
                    chatGlobalTime + " > ('NOW' - 'INTERVAL 24 HOUR') AND "
                    + "(" + chatGlobalType + "=" + CMTypes.FPIC + " OR " +
                    chatGlobalType + "=" + CMTypes.FVID

                    + ") ORDER BY " + chatGlobalTime + " DESC LIMIT 1";

    public ConversationMessage getTodayLastConversationMessageChatPartner(long CHAT_ID)
    {
        SQLiteDatabase db = SQLChats.super.getReadableDatabase();
        Cursor cursor = null;
        ConversationMessage conversationMessage = null;

        try
        {
            cursor = db.rawQuery(QUERY_todayLastConversationMessageChatPartner, new String[]{Long.toString(CHAT_ID)});

            if(cursor.moveToFirst())
            {
                do
                {
                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FPIC:
                            conversationMessage = (new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),



                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),

                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))
                                    ));
                            break;


                        case CMTypes.FVID:
                            conversationMessage = (new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername))));
                            break;
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getTodayLastConversationMessageChatPartner() failed: " + ec);
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return conversationMessage;
    }

    private static final String QUERY_getUsersWithLastConversationMessage = //Get all users from cchatting, than join on table receivers with chat key, than from this table join on posts.
            "SELECT * FROM (SELECT * FROM (SELECT *," +
                    SQLFriends.tableFriends + "." + SQLFriends.FR_UID + " AS SPECIALKEY" +
                    " FROM " +
                    SQLFriends.tableFriends + " WHERE 1) AS ChatPartnerTable LEFT JOIN "
                    + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_KEY_CHAT
                    + "=SPECIALKEY GROUP BY SPECIALKEY) AS REC_TABLE LEFT JOIN "
                    + tableNamePostings + " ON " + tableNamePostings + "." + MESSAGE_ID + "= REC_TABLE."+ RECEIVERS_MSG_ID_POST
                    + " AND (" + tableNamePostings + "." + chatGlobalType + "=" + CMTypes.FVID +
                    " OR " + tableNamePostings + "." + chatGlobalType + "=" + CMTypes.FPIC + ") "
                    + " ORDER BY "
                    + tableNamePostings + "." + chatGlobalTime;


    public List<Object> getMomentsListWithUser()
    {
        List<Object> list = new ArrayList<>();
        Cursor cursor = null;
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            cursor = db.rawQuery(SQLChats.QUERY_getUsersWithLastConversationMessage, new String[]{});
            if(cursor.moveToFirst())
            {
                do {

                    if(cursor.isNull(cursor.getColumnIndex(chatGlobalType))) //Wenn keine bilder synchronisiert wurden.
                    {
                        list.add(new ChatPartner(cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                                "",
                                "",
                                cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                                null,
                                cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0));
                    }
                    else
                    {
                        short typeCurrent = cursor.getShort(cursor.getColumnIndex(chatGlobalType));
                        if(typeCurrent == CMTypes.FPIC)
                        {
                            list.add(new ChatPartner(cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                                    "",
                                    "",
                                    cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                                    new ChatImage(
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            (short) -1,
                                            "",
                                            cursor.getString(cursor.getColumnIndex(chat_PID)),
                                            null),
                                    cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0));
                        }
                        else if(typeCurrent == CMTypes.FVID)
                        {
                            list.add(new ChatPartner(cursor.getString(cursor.getColumnIndex(SQLFriends.FR_BENUTZERNAME)),
                                    "",
                                    "",
                                    cursor.getLong(cursor.getColumnIndex("SPECIALKEY")),
                                    new ChatVideo(
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            (short) -1,
                                            "",
                                            cursor.getString(cursor.getColumnIndex(chat_PID)),
                                            null),
                                    cursor.getInt(cursor.getColumnIndex(SQLFriends.FR_FRIENDSHIP_DIED)) > 0));
                        }
                    }
                }
                while(cursor.moveToNext());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getMomentsListWithUser() failed: " + ec);
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


    private ArrayList<EsaphHashtag> getHashTagForCreatingConversationMessage(long ID)
    {
        SQLHashtags sqlHashtags = new SQLHashtags(context);
        ArrayList<EsaphHashtag> esaphHashtags = new ArrayList<>();
        try
        {
            esaphHashtags = sqlHashtags.getHashtagsForPost(ID);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getHashTagForCreatingConversationMessage() failed: " + ec);
        }
        finally
        {
            sqlHashtags.close();
        }

        return esaphHashtags;
    }


    public void insertNewSharedPost(ChatShared chatShared)
    {
        try
        {
            // TODO: 05.03.2019 fix this
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(chatShared_absender, chatShared.getABS_ID());
            contentValues.put(chatShared_empfaenger, chatShared.getID_CHAT());
            contentValues.put(chatShared_Type, chatShared.getType());
            contentValues.put(chatShared_POSTFROM, chatShared.getConversationMessageShared().getABS_ID());
            contentValues.put(chatShared_status, chatShared.getMessageStatus());
            contentValues.put(chatShared_time, chatShared.getMessageTime());
            contentValues.put(chatGlobalType, chatShared.getType());
            contentValues.put(chatGlobalAbsenderUsername, chatShared.getAbsender());

            long UNIQUE_ID = db.insert(tableNameTextualMessagesChatShared, null, contentValues);

            chatShared.setMESSAGE_ID(UNIQUE_ID);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertNewSharedPost() failed: " + ec);
        }
    }

    public void updateStatusByID(ConversationMessage conversationMessage)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(rec_Status, conversationMessage.getMessageStatus());

            db.update(tableReceivers,
                    contentValues,
                    RECEIVERS_MSG_ID_POST + "=" + conversationMessage.getMESSAGE_ID(),
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateStatusByID() failed: " + ec);
        }
    }


    public void updateStickerMessageLSIDAndPacket(EsaphStickerChatObject esaphStickerChatObject,
                                                  long NEW_LSID,
                                                  long NEW_LSPID)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(chatSticker_LSID, NEW_LSID);
            contentValues.put(chatSticker_LSPID, NEW_LSPID);

            db.update(tableNamePostings,
                    contentValues,
                    chatSticker_LSID + "=" + esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_ID() + " AND " +
                            chatSticker_LSPID + "=" + esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_PACK_ID(),
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateStickerMessageLSIDAndPacket() failed: " + ec);
        }
    }


    public boolean isPostSavedBy(long USER_ID, long MESSAGE_ID)
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT 1 FROM " + tableNameSavedPosts +
                " WHERE " + ts_CHAT_POST_SAVER_ID + "=" + USER_ID
                + " AND " + ts_PRIMARY_ID_KEY_FROM_POST + "=" + MESSAGE_ID + " LIMIT 1", null);
        boolean isSaved = false;
        if(cursor.moveToFirst())
        {
            isSaved = true;
        }
        cursor.close();
        return isSaved;
    }

    public long getPostSavedId(long USER_ID, long MESSAGE_ID)
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + ts_SAVED_EINTRAG_UNIQUE_ID + " FROM " + tableNameSavedPosts +
                " WHERE " + ts_CHAT_POST_SAVER_ID + "=" + USER_ID
                + " AND " + ts_PRIMARY_ID_KEY_FROM_POST + "=" + MESSAGE_ID + " LIMIT 1", null);
        long ID = -1;
        if(cursor.moveToFirst())
        {
            ID = cursor.getInt(cursor.getColumnIndex(ts_SAVED_EINTRAG_UNIQUE_ID));
        }
        cursor.close();
        return ID;
    }


    private static final String QUERY_getSaversFromPost =
            "SELECT * FROM " + tableNameSavedPosts +
                    " WHERE " + ts_PRIMARY_ID_KEY_FROM_POST + "=?";

    public List<SavedInfo> lookUpSaversFromPost(long MESSAGE_ID)
    {
        SQLFriends sqlWatcher = new SQLFriends(context);
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(QUERY_getSaversFromPost, new String[]{Long.toString(MESSAGE_ID)});
        List<SavedInfo> list = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            do {
                list.add(
                        new SavedInfo(
                                cursor.getLong(cursor.getColumnIndex(ts_SAVED_EINTRAG_UNIQUE_ID)),
                                cursor.getLong(cursor.getColumnIndex(ts_CHAT_POST_SAVER_ID)),
                        sqlWatcher.lookUpUsername(cursor.getLong(cursor.getColumnIndex(ts_CHAT_POST_SAVER_ID)))));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        sqlWatcher.close();
        return list;
    }

    private static final String QUERY_getSingleSaver =
            "SELECT * FROM " + tableNameSavedPosts +
                    " WHERE " + ts_PRIMARY_ID_KEY_FROM_POST + "=? AND " + ts_CHAT_POST_SAVER_ID + "=? LIMIT 1";

    public boolean hasSaved(long MESSAGE_ID, long UID_SAVER)
    {
        boolean hasSaved = false;
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(QUERY_getSingleSaver, new String[]{Long.toString(MESSAGE_ID),
        Long.toString(UID_SAVER)});

        List<SavedInfo> list = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            hasSaved = true;
        }
        cursor.close();

        return hasSaved;
    }

    private static final String CHECK_IF_SAVED_INSERTED = "SELECT NULL FROM " +
            tableNameSavedPosts + " WHERE "
            + ts_PRIMARY_ID_KEY_FROM_POST + "=? AND " + ts_CHAT_POST_SAVER_ID + "=? LIMIT 1";


    public long insertPostSaved(long ID_SAVER, long ID_POST)
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ts_CHAT_POST_SAVER_ID, ID_SAVER);
        contentValues.put(ts_PRIMARY_ID_KEY_FROM_POST, ID_POST);

        long id = -1;

        Cursor cursor = sqLiteDatabase.rawQuery(CHECK_IF_SAVED_INSERTED,
                new String[]{Long.toString(ID_POST), Long.toString(ID_SAVER)});
        if(!cursor.moveToFirst())
            id = sqLiteDatabase.insert(tableNameSavedPosts, null, contentValues);

        cursor.close();
        return id;
    }


    public void removePostSaved(long ID) //Id von der tabelle fespeicherte einträge.
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
        sqLiteDatabase.delete(tableNameSavedPosts, ts_SAVED_EINTRAG_UNIQUE_ID
                + "=" + ID, null);
    }

    private static final String QUERY_lookUpInternIDFromServer =
            "SELECT " + MESSAGE_ID + " FROM " + tableNamePostings +
                     " WHERE " + SERVER_POST_ID + "=? LIMIT 1";
    public long lookUpInternIDFromServerID(long serverId)
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(QUERY_lookUpInternIDFromServer, new String[]{Long.toString(serverId)});

        long internPostId = -1;

        if(cursor.moveToFirst())
        {
            internPostId = cursor.getLong(cursor.getColumnIndex(MESSAGE_ID));
        }
        cursor.close();
        return internPostId;
    }

    private static final String getAllNewMessages = "SELECT * FROM (SELECT * FROM " +
            tableNamePostings + " WHERE " + chatGlobalAbsender + "=?) AS t1 JOIN "
            + tableReceivers + " ON t1." + MESSAGE_ID + "="
            + tableReceivers + "." + RECEIVERS_MSG_ID_POST
            + " AND " + tableReceivers + "." + rec_Status + "=" + ConversationStatusHelper.STATUS_NEW_MESSAGE
            + " ORDER BY " + "t1." + chatGlobalTime + " DESC ";

    public List<Fragment> getAllNewMessagesPlopps(long UID_PARTNER)
    {
        SQLSticker sqlSticker = null;
        try
        {
            sqlSticker = new SQLSticker(context);
            List<Fragment> listFragments = new ArrayList<>();
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery(getAllNewMessages, new String[]{Long.toString(UID_PARTNER)});
            if(cursor.moveToFirst())
            {
                do {

                    switch (cursor.getShort(cursor.getColumnIndex(chatGlobalType)))
                    {
                        case CMTypes.FTEX:
                            listFragments.add(ChatItemFragmentText.getInstance(new ChatTextMessage(
                                    cursor.getString(cursor.getColumnIndex(chatText_message)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)))));
                            break;

                        case CMTypes.FPIC:
                            listFragments.add(ChatItemFragmentImage.getInstance(new ChatImage(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),

                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)))));
                            break;


                        case CMTypes.FVID:
                            listFragments.add(ChatItemFragmentVideo.getInstance(new ChatVideo(
                                    cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chat_Beschreibung)),
                                    cursor.getString(cursor.getColumnIndex(chat_PID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)))));
                            break;

                        case CMTypes.FAUD:
                            listFragments.add(ChatItemFragmentAudio.getInstance(new AudioMessage(
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                    cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                    cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                    cursor.getString(cursor.getColumnIndex(chatAudio_AID)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                    cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)))));
                            break;

                            /*
                        case CMTypes.FSTI:
                            EsaphSpotLightSticker esaphSpotLightSticker = sqlSticker.getSticker(cursor.getString(cursor.getColumnIndex(chatSticker_LSID)),
                                    cursor.getString(cursor.getColumnIndex(chatSticker_LSPID)));

                            if(esaphSpotLightSticker == null) //If we have got an sticker, its not saved into the database of our stickers. Because we cannot send them.
                            //I will implement a function to save this in own db.
                            {
                                esaphSpotLightSticker = new EsaphSpotLightSticker(
                                        cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSID)),
                                        cursor.getLong(cursor.getColumnIndex(chatSticker_LSPID)),
                                        "",
                                        -1);
                            }

                            listFragments.add(ChatItemFragmentSticker.getInstance(
                                    new EsaphStickerChatObject(
                                            cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                            cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                            cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                            cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                            cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                            esaphSpotLightSticker,
                                            cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                            
                                            cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)))));
                            break;
*/

                        case CMTypes.FEMO:
                            listFragments.add(ChatItemFragmentEmojie.getInstance(
                                    new EsaphAndroidSmileyChatObject(
                                            cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)),
                                            cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                                            cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                            cursor.getLong(cursor.getColumnIndex(chatGlobalTime)),
                                            cursor.getShort(cursor.getColumnIndex(rec_Status)),
                                            new EsaphEmojie(cursor.getString(cursor.getColumnIndex(chatSmiley_DATA))),
                                            cursor.getString(cursor.getColumnIndex(chatGlobalAbsenderUsername)),
                                            cursor.getString(cursor.getColumnIndex(chatGlobalJSONPloppInformaton)))));
                            break;
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();

            return listFragments;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllNewMessagesPlopps() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
            if(sqlSticker != null)
            {
                sqlSticker.close();
            }
        }
    }

    public void insertNewChatConversation(SpotLightUser chatPartner)
    {
        SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECEIVERS_KEY_CHAT, chatPartner.getUID());
        sqLiteDatabase.insert(tableNameTextualMessagesChats, null, contentValues);
    }

    private static final String QUERY_GetPostOnlyByPID =
            "SELECT * FROM (SELECT * FROM " + tableNamePostings + " WHERE " + chat_PID + "=? LIMIT 1) AS Post JOIN "
                    + tableReceivers + " ON " + tableReceivers + "." + RECEIVERS_MSG_ID_POST + "= Post."
                    + MESSAGE_ID;

    public List<UserSeenOrSavedMoment> getUsersSeenOrSavedMemorys(String PID)
    {
        SQLFriends sqlWatcher = new SQLFriends(context);
        try
        {
            List<UserSeenOrSavedMoment> list = new ArrayList<>();
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();

            Cursor cursor = db.rawQuery(QUERY_GetPostOnlyByPID, new String[]{PID});

            if(cursor.moveToFirst())
            {
                do
                {
                    list.add(new UserSeenOrSavedMoment(
                            cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                            sqlWatcher.lookUpUsername(cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT))),
                            cursor.getShort(cursor.getColumnIndex(rec_Status)),
                            isPostSavedBy(cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)), cursor.getLong(cursor.getColumnIndex(MESSAGE_ID))),
                            cursor.getLong(cursor.getColumnIndex(SERVER_POST_ID)),
                            cursor.getString(cursor.getColumnIndex(chat_PID)),
                            cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                            getPostSavedId(cursor.getLong(cursor.getColumnIndex(RECEIVERS_KEY_CHAT)),
                                    cursor.getLong(cursor.getColumnIndex(MESSAGE_ID)))));
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getUsersSeenOrSavedMemorys() failed: " + ec);
            return new ArrayList<>();
        }
        finally {
            sqlWatcher.close();
        }
    }



    private static final String queryFilterChatsUsername = "SELECT " +
            chatGlobalAbsenderUsername + ", " + chat_PID + ", " + chatGlobalType +
            " FROM " +
            tableNamePostings + " WHERE " + chatGlobalAbsenderUsername + " LIKE ?";

    private static final String queryFilterChatsByDescription = "SELECT " +
            chat_Beschreibung + ", " + chat_PID + ", " + chatGlobalType +
            " FROM " +
            tableNamePostings + " WHERE " + chat_Beschreibung + " LIKE ?";

    public List<SearchItemMainMoments> filterChat(String toSearch)
    {
        try
        {
            List<SearchItemMainMoments> list = new ArrayList<>();
            SQLiteDatabase db = SQLChats.super.getReadableDatabase();
            Cursor cursorName = db.rawQuery(SQLChats.queryFilterChatsUsername, new String[]{"%" + toSearch + "%"});
            Cursor cursorDescription = db.rawQuery(SQLChats.queryFilterChatsByDescription, new String[]{"%" + toSearch + "%"});

            if(cursorName.moveToFirst())
            {
                do {
                    short DATA_TYPE = cursorName.getShort(cursorName.getColumnIndex(SQLChats.chatGlobalType));

                    if(DATA_TYPE == CMTypes.FPIC)
                    {
                        list.add(new SearchItemImage(cursorName.getString(cursorName.getColumnIndex(SQLChats.chat_PID)),
                                cursorName.getString(cursorName.getColumnIndex(chatGlobalAbsenderUsername)),
                                ""));
                    }
                    else if(DATA_TYPE == CMTypes.FVID)
                    {
                        list.add(new SearchItemVideo(cursorName.getString(cursorName.getColumnIndex(SQLChats.chat_PID)),
                                cursorName.getString(cursorName.getColumnIndex(chatGlobalAbsenderUsername)),
                                ""));
                    }
                }
                while(cursorName.moveToNext());
            }

            cursorName.close();


            if(cursorDescription.moveToFirst())
            {
                do {
                    short DATA_TYPE = cursorDescription.getShort(cursorDescription.getColumnIndex(SQLChats.chatGlobalType));

                    if(DATA_TYPE == CMTypes.FPIC)
                    {
                        list.add(new SearchItemImage(cursorDescription.getString(cursorDescription.getColumnIndex(SQLChats.chat_PID)),
                                cursorDescription.getString(cursorDescription.getColumnIndex(chat_Beschreibung)),
                                ""));
                    }
                    else if(DATA_TYPE == CMTypes.FVID)
                    {
                        list.add(new SearchItemVideo(cursorDescription.getString(cursorDescription.getColumnIndex(SQLChats.chat_PID)),
                                cursorDescription.getString(cursorDescription.getColumnIndex(chat_Beschreibung)),
                                ""));
                    }
                }
                while(cursorDescription.moveToNext());
            }
            cursorDescription.close();

            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "filterChat() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public UndeliveredChatSeenMessage insertChatWasSeen(long UID_CHATPARTNER, long timeOpened)
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(chatGlobalAbsender, UID_CHATPARTNER);
            contentValues.put(chatGlobalTime, timeOpened);
            long _ID = sqLiteDatabase.insert(tableNameTextualMessagesSeenChats, null, contentValues);

            return new UndeliveredChatSeenMessage(_ID, UID_CHATPARTNER, timeOpened);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "insertChatWasSeen() failed: " + ec);
            return new UndeliveredChatSeenMessage(-1,-1,-1);
        }
    }


    public List<UndeliveredChatSeenMessage> getUndeliveredChatSeenMessages()
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableNameTextualMessagesSeenChats
                    + " WHERE 1", null);
            List<UndeliveredChatSeenMessage> list = new ArrayList<>();

            if(cursor.moveToFirst())
            {
                do {
                    list.add(new UndeliveredChatSeenMessage(
                            cursor.getLong(cursor.getColumnIndex(SEEN_ID)),
                            cursor.getLong(cursor.getColumnIndex(chatGlobalAbsender)),
                            cursor.getLong(cursor.getColumnIndex(chatGlobalTime))
                    ));
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getUndeliveredChatSeenMessages() failed: " + ec);
            return new ArrayList<>();
        }
    }


    public void removeChatWasSeen(long ID)
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLChats.super.getWritableDatabase();
            sqLiteDatabase.delete(tableNameTextualMessagesSeenChats, SEEN_ID + "=" + ID,null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "removeChatWasSeen() failed: " + ec);
        }
    }

    private static final String QUERY_UpdateAllChatMessagesAsReadUntilTime = "UPDATE " +
            tableReceivers + " SET "
            + rec_Status + "=? WHERE EXISTS(SELECT * FROM "
            + tableNamePostings + " AS P WHERE P."
            + MESSAGE_ID
            + "="
            + RECEIVERS_MSG_ID_POST + " AND " + chatGlobalAbsender + "=? AND " +
            RECEIVERS_KEY_CHAT + "=? AND " +
            chatGlobalType + "!=" + CMTypes.FVID + " AND " +
            chatGlobalType + "!=" + CMTypes.FPIC +
            " AND " + chatGlobalTime + " <= ?)";

    public void setChatWasReadedUntilTime(long UID_CHAT_PARTNER, long Until)
    {
        try
        {
            SQLiteDatabase db = SQLChats.super.getWritableDatabase();

            db.execSQL(QUERY_UpdateAllChatMessagesAsReadUntilTime, new String[]
                    {
                            Short.toString(ConversationStatusHelper.STATUS_CHAT_OPENED),
                            Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                            Long.toString(UID_CHAT_PARTNER),
                            Long.toString(Until)
                    });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setChatWasReadedUntilTime() failed: " + ec);
        }
    }
}
