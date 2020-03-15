/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.NotificationAndMessageHandling;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.navigation.spotlight.PublicSearch.PublicSearchFragment;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

import static android.content.Context.POWER_SERVICE;

public class MessageHandler implements Runnable
{
    private static final String MessageHandlerWakeLock = "esaph.spotlight:MSGH";
    private MsgServiceConnection msgServiceConnection;
    private Context context;
    private List<JSONObject> chatMessages;

    //For Moments
    private static final long oneDayInMillis = 24 * 60 * 60 * 1000;

    public static final String actionBroadCastUserLoaded = "esaph.appname.asyncgetuserfromserver.action.LOOKUPFRIEND";
    public static final String extraLongUidLoadedForLoaded = "esaph.appname.asycgetuserfromserver.extra.username";

    private abstract class MessageTypeIdentifier
    {
        private static final String CMD_UserSavedYourPostPrivate = "CUSYPP";
        private static final String CMD_UserUnsavedYourPostPrivate = "CUUYPP";
        private static final String CMD_UserSeenYourPostPrivate = "CUSEYPP";
        private static final String CMD_UserRemovedPostFromPrivate = "CURPFP";
        private static final String CMD_FriendStatus = "CFS";
        private static final String CMD_UserDeletedAccount = "CUDA";
        private static final String CMD_NewPrivatePost = "CNPP";
        private static final String CMD_UserDisallowedYouToSeeHimPostInPrivate = "CDYTPIP";
        private static final String CMD_UserAllowedYouToSeeHimPostInPrivate = "CAYTPIP";
        private static final String CMD_UserSendTextMessageInPrivate = "CUSTMIP";
        private static final String CMD_UserTyping = "CUTM";
        private static final String CMD_UserStopedTyping = "CUSTM";
        private static final String CMD_NEW_AUDIO = "CUNA";
        private static final String CMD_NEW_STICKER = "CUNS";
        private static final String CMD_NEW_EMOJIE = "CNE";
        private static final String CMD_NEW_SHARED_POST = "CNSP";
        private static final String CMD_NEW_COMMENT = "CMNPC";
        private static final String CMD_USER_REMOVED_SAVED_PUBLIC = "CUSSIP";
        private static final String CMD_USER_SAVED_PUBLIC = "CURSIP";
        private static final String CMD_NEW_SHARED_PUBLIC_POST = "CNSPP";
        private static final String CMD_YOUR_PUBLIC_POST_WAS_SHARED = "CYPPWS";
        private static final String CMD_PB_UPDATE = "CPUPU";
        private static final String CMD_PB_REMOVE = "CPRPB";
        private static final String CMD_UserOpenenedChat = "CUOC";
    }


    public MessageHandler(List<JSONObject> chatMessages, Context context, MsgServiceConnection msgServiceConnection)
    {
        this.context = context;
        this.msgServiceConnection = msgServiceConnection;
        this.chatMessages = chatMessages;
    }


    @Override
    public void run()
    {
        try
        {
            PowerManager.WakeLock wakeLock = null;
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            if(powerManager != null)
            {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MessageHandlerWakeLock);
                if(!wakeLock.isHeld())
                {
                    wakeLock.acquire(10*60*1000L /*10 minutes*/);
                }
            }

            ListIterator<JSONObject> listIterator = this.chatMessages.listIterator();
            SQLGroups sqlMemorys = new SQLGroups(this.context);
            SQLChats sqlChats = new SQLChats(this.context);
            SQLFriends friends = new SQLFriends(this.context);

            while(listIterator.hasNext())
            {
                JSONObject messageFromList = listIterator.next();
                String COMMAND = messageFromList.getString("CMD");
                if(COMMAND != null && !COMMAND.isEmpty())
                {
                    switch(COMMAND)
                    {
                        case MessageTypeIdentifier.CMD_UserSavedYourPostPrivate:

                            int notifyId = friends.getFriendNotifyId(messageFromList.getLong("USRN"));

                            if(notifyId > -1)
                            {
                                GlobalNotificationDisplayer.createNotificationSingleMessage(this.context,
                                        friends.lookUpUsername(messageFromList.getLong("USRN")),
                                        this.context.getResources().getString(R.string.txt_noti_user_saved_your_postInPrivate, friends.lookUpUsername(messageFromList.getLong("USRN"))),
                                        friends.lookUpUsername(messageFromList.getLong("USRN")),
                                        R.drawable.ic_app_logo_notification_icon,
                                        PrivateChat.class,
                                        notifyId);
                            }
                            else
                            {
                                GlobalNotificationDisplayer.createNotificationSingleMessage(this.context,
                                        friends.lookUpUsername(messageFromList.getLong("USRN")),
                                        this.context.getResources().getString(R.string.txt_noti_user_saved_your_postInPrivate, friends.lookUpUsername(messageFromList.getLong("USRN"))),
                                        friends.lookUpUsername(messageFromList.getLong("USRN")),
                                        R.drawable.ic_app_logo_notification_icon,
                                        PrivateChat.class);
                            }

                            ChatInfoStateMessage chatInfoStateMessageUserSavedYourPost = new ChatInfoStateMessage(
                                    sqlChats.getPostByInternId(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"))),
                                    -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_SAVED,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")));

                            sqlChats.insertNewInfoStateMessage(chatInfoStateMessageUserSavedYourPost, ConversationReceiverHelper.getReceiverFromMessage(chatInfoStateMessageUserSavedYourPost));

                            sqlChats.insertPostSaved(messageFromList.getLong("USRN"),
                                    messageFromList.getLong("PPID"));

                            ConversationMessage conversationMessage = sqlChats.getPostByServerIdAndChatKey(messageFromList.getLong("PPID"),
                                    messageFromList.getLong("USRN"));

                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(
                                    conversationMessage,
                                    chatInfoStateMessageUserSavedYourPost);

                            break;

                        case MessageTypeIdentifier.CMD_UserUnsavedYourPostPrivate:
                            ChatInfoStateMessage chatInfoStateMessageUnsaved = new ChatInfoStateMessage(
                                    sqlChats.getPostByInternId(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"))),
                                    -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_UNSAVED,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")));

                            sqlChats.insertNewInfoStateMessage(chatInfoStateMessageUnsaved, ConversationReceiverHelper.getReceiverFromMessage(chatInfoStateMessageUnsaved));

                            sqlChats.removePostSaved(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID")));

                            ConversationMessage conversationMessageUnsaved = sqlChats.getPostByServerIdAndChatKey(messageFromList.getLong("PPID"),
                                    messageFromList.getLong("USRN"));

                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(
                                    conversationMessageUnsaved,
                                    chatInfoStateMessageUnsaved);
                            break;

                        case MessageTypeIdentifier.CMD_UserSeenYourPostPrivate:

                            ConversationMessage conversationMessageSeen = sqlChats.getPostByServerIdAndChatKey(messageFromList.getLong("PPID"),
                                    messageFromList.getLong("USRN"));

                            conversationMessageSeen.setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);
                            sqlChats.updateStatusByID(conversationMessageSeen);

                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(
                                    conversationMessageSeen, null);
                            break;

                        case MessageTypeIdentifier.CMD_UserRemovedPostFromPrivate:
                            long INTERN_ID_FROM_POST_PREVENT_DELETING =
                                    sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"));

                            sqlChats.deletePostInChat(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID")),
                                    messageFromList.getLong("USRN"));

                            msgServiceConnection.broadCastToAllCallbacksOnUserRemovedPost(
                                    sqlChats.getSingleChatPartner(
                                            messageFromList.getLong("USRN")),
                                    INTERN_ID_FROM_POST_PREVENT_DELETING);
                            break;

                        case MessageTypeIdentifier.CMD_UserDeletedAccount:
                            long FUID_DELETED_ACCOUNT = messageFromList.getLong("USRN");

                            friends.updateFollowNegotiation(FUID_DELETED_ACCOUNT, ServerPolicy.POLICY_DETAIL_CASE_NOTHING);
                            sqlChats.removeAllUserData(FUID_DELETED_ACCOUNT);
                            friends.removeAllUserData(FUID_DELETED_ACCOUNT);
                            break;

                        case MessageTypeIdentifier.CMD_FriendStatus:
                            short Status = (short) messageFromList.getInt("FST");
                            long FUID = messageFromList.getLong("USRN");
                            String UsernamePartner = messageFromList.optString("USRN_STR", "");
                            String Vorname = messageFromList.optString("VORN", "");
                            String Region = messageFromList.optString("REG", "");

                            if(Status == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS) //FRIENSHIP
                            {
                                int notifyIdFriendStatus = friends.getFriendNotifyId(FUID);
                                if(notifyIdFriendStatus > -1)
                                {
                                    GlobalNotificationDisplayer.createNotificationSingleMessage(this.context,
                                            this.context.getResources().getString(R.string.txt_new_friendAnfrageAccepted),
                                            this.context.getResources().getString(R.string.txt_new_friendShipIsTrueNotification, UsernamePartner),
                                            UsernamePartner,
                                            R.drawable.ic_friend_notification,
                                            PublicSearchFragment.class,
                                            notifyIdFriendStatus);
                                }
                                else
                                {
                                    GlobalNotificationDisplayer.createNotificationSingleMessage(this.context,
                                            this.context.getResources().getString(R.string.txt_new_friendAnfrageAccepted),
                                            this.context.getResources().getString(R.string.txt_new_friendShipIsTrueNotification, UsernamePartner),
                                            UsernamePartner,
                                            R.drawable.ic_friend_notification,
                                            PublicSearchFragment.class);
                                }

                                if(messageFromList.has("PF"))
                                {
                                    JSONObject jsonObjectUser = messageFromList.getJSONObject("PF");

                                    friends.updateFollowNegotiation(FUID, Status);

                                    friends.insertWatcher(new SpotLightUser(
                                            jsonObjectUser.getLong("UID"),
                                            jsonObjectUser.getString("Benutzername"),
                                            jsonObjectUser.getString("Vorname"),
                                            jsonObjectUser.getLong("Geburtstag"),
                                            jsonObjectUser.getString("Region"),
                                            false,
                                            Status,
                                            jsonObjectUser.getJSONObject("DESCPL").toString()));

                                    friends.deleteWatcherStatusBecauseFriendAddedOrFriendshipDied(FUID);
                                    friends.setFriendStatusIstrue(FUID);

                                    Intent intentFriendAddedNew = new Intent();
                                    intentFriendAddedNew.setAction(actionBroadCastUserLoaded);
                                    intentFriendAddedNew.putExtra(extraLongUidLoadedForLoaded, FUID);
                                    context.sendBroadcast(intentFriendAddedNew);
                                }
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE) //ANFRAGE VERSCHICKT.
                            {
                                friends.updateFollowNegotiation(FUID, Status);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT) //Ich anfrage bekommen
                            {
                                friends.insertNewFollowNegotiation(new SocialFriendNegotiation(FUID,
                                        UsernamePartner,
                                        Vorname,
                                        Status,
                                        Region));

                                GlobalNotificationDisplayer.createNotificationSingleMessage(this.context,
                                        this.context.getResources().getString(R.string.txt_new_friendAnfrageNotificationTitel),
                                        this.context.getResources().getString(R.string.txt_new_friendAnfrageNotification, UsernamePartner),
                                        UsernamePartner,
                                        R.drawable.ic_friend_notification, PublicSearchFragment.class);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_NOTHING) //KEINE VERBINDUNG.
                            {
                                friends.removeAllUserData(FUID);
                                friends.updateFollowNegotiation(FUID, Status);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE) //ICH HAB JEMANDEN GEBLOCKT.
                            {
                                friends.updateFollowNegotiation(FUID, Status);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED) //ICH WURDE GEBLOCKT.
                            {
                                sqlChats.removeAllUserData(messageFromList.getLong("USRN"));
                                friends.removeAllUserData(FUID);
                                friends.updateFollowNegotiation(FUID, Status);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_I_FOLLOW)
                            {
                                friends.updateFollowNegotiation(FUID, Status);
                            }
                            else if(Status == ServerPolicy.POLICY_DETAIL_FOLLOWS_ME)
                            {
                                friends.updateFollowNegotiation(FUID, Status);
                            }

                            Intent intentFriendAnfrage = new Intent();
                            intentFriendAnfrage.setAction(MessageHandler.ACTION_FRIEND_UPDATE);
                            intentFriendAnfrage.putExtra(MessageHandler.ID_UID, FUID);
                            intentFriendAnfrage.putExtra(MessageHandler.ID_FOLLOW_STATE, Status);
                            this.context.sendBroadcast(intentFriendAnfrage);

                            msgServiceConnection.broadCastToAllCallbacksFriendStatusUpdate(Status, sqlChats.getSingleChatPartner(FUID));
                            break;

                        case MessageTypeIdentifier.CMD_NewPrivatePost:

                            if (System.currentTimeMillis() < messageFromList.getLong("TIME") + MessageHandler.oneDayInMillis)
                            {
                                ArrayList<EsaphHashtag> esaphHashtag = new ArrayList<>();

                                if(messageFromList.has("ARR_EHT"))
                                {
                                    JSONArray jsonArray = messageFromList.getJSONArray("ARR_EHT");
                                    for (int counterHashtag = 0; counterHashtag < jsonArray.length(); counterHashtag++)
                                    {
                                        JSONObject jsonObjectHashtag = jsonArray.getJSONObject(counterHashtag);
                                        esaphHashtag.add(new EsaphHashtag(jsonObjectHashtag.getString("TAG"),
                                                null,
                                                0));
                                    }
                                }


                                if((short) messageFromList.getInt("FT") == CMTypes.FPIC)
                                {
                                    ChatImage chatImage = new ChatImage(
                                            messageFromList.getLong("PPID"),
                                            -1,
                                            messageFromList.getLong("USRN"),
                                            messageFromList.getLong("USRN"),
                                            messageFromList.getLong("TIME_POST"),
                                            ConversationStatusHelper.STATUS_NEW_MESSAGE,
                                            messageFromList.getString("DES"),
                                            messageFromList.getString("PID"),
                                            friends.lookUpUsername(messageFromList.getLong("USRN")));

                                    sqlChats.insertNewConversationMessage(chatImage,
                                            esaphHashtag,
                                            new ArrayList<SavedInfo>(),
                                            new JSONArray().put(new JSONObject().put("REC_ID",msgServiceConnection.getBoundServiceUID())
                                    .put("ST", ConversationStatusHelper.STATUS_NEW_MESSAGE)));

                                    int notifyIdNewPost = friends.insertNewNotifyPostId(chatImage);
                                    GlobalNotificationDisplayer.createNotificationNewPrivateImage(this.context,
                                            friends.lookUpUsername(messageFromList.getLong("USRN")),
                                            messageFromList.getLong("TIME"),
                                            notifyIdNewPost);
                                }
                                else if((short) messageFromList.getInt("FT") == CMTypes.FVID)
                                {
                                    ChatVideo chatVideo = new ChatVideo(
                                            messageFromList.getLong("PPID"),
                                            -1,
                                            messageFromList.getLong("USRN"),
                                            messageFromList.getLong("USRN"),
                                            messageFromList.getLong("TIME_POST"),
                                            ConversationStatusHelper.STATUS_NEW_MESSAGE,
                                            messageFromList.getString("DES"),
                                            messageFromList.getString("PID"),
                                            friends.lookUpUsername(messageFromList.getLong("USRN")));

                                    sqlChats.insertNewConversationMessage(chatVideo,
                                            esaphHashtag,
                                            new ArrayList<SavedInfo>(),
                                            new JSONArray().put(new JSONObject().put("REC_ID",msgServiceConnection.getBoundServiceUID())
                                            .put("ST", ConversationStatusHelper.STATUS_NEW_MESSAGE)));

                                    int notifyIdNewPost = friends.insertNewNotifyPostId(chatVideo);
                                    GlobalNotificationDisplayer.createNotificationNewPrivateImage(this.context,
                                            friends.lookUpUsername(messageFromList.getLong("USRN")),
                                            messageFromList.getLong("TIME"),
                                            notifyIdNewPost);
                                }

                                msgServiceConnection.broadCastToAllCallbacksNewContentReceived(sqlChats.getSingleChatPartner(messageFromList.getLong("USRN")));
                            }
                            break;

                        case MessageTypeIdentifier.CMD_UserDisallowedYouToSeeHimPostInPrivate:

                            ChatInfoStateMessage chatInfoStateMessage =  new ChatInfoStateMessage(
                                    sqlChats.getPostByInternId(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"))),
                                    -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_DECLINED_PERMISSION,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")));

                            sqlChats.insertNewInfoStateMessage(chatInfoStateMessage, ConversationReceiverHelper.getReceiverFromMessage(chatInfoStateMessage));

                            long INTERN_ID_FROM_POST_PREVENT_DELETING_SAVE_STATUS =
                                    sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"));

                            sqlChats.deletePostInChat(INTERN_ID_FROM_POST_PREVENT_DELETING_SAVE_STATUS,
                                    messageFromList.getLong("USRN"));

                            msgServiceConnection.broadCastToAllCallbacksOnUserDisallowToSeePost(sqlChats.getSingleChatPartner(messageFromList.getLong("USRN")),
                                    chatInfoStateMessage,
                                    INTERN_ID_FROM_POST_PREVENT_DELETING_SAVE_STATUS);

                            break;

                        case MessageTypeIdentifier.CMD_UserAllowedYouToSeeHimPostInPrivate:
                            ArrayList<EsaphHashtag> esaphHashtag = new ArrayList<>();

                            if(messageFromList.has("ARR_EHT"))
                            {
                                JSONArray jsonArray = messageFromList.getJSONArray("ARR_EHT");
                                for (int counterHashtag = 0; counterHashtag < jsonArray.length(); counterHashtag++)
                                {
                                    JSONObject jsonObjectHashtag = jsonArray.getJSONObject(counterHashtag);
                                    esaphHashtag.add(new EsaphHashtag(jsonObjectHashtag.getString("TAG"),
                                            null,
                                            0));
                                }
                            }

                            ChatInfoStateMessage chatInfoStateMessageFreigabe = new ChatInfoStateMessage(
                                    sqlChats.getPostByInternId(sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID"))),
                                    -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_ALLOWED_PERMISSION,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")));

                            sqlChats.insertNewInfoStateMessage(chatInfoStateMessageFreigabe, ConversationReceiverHelper.getReceiverFromMessage(chatInfoStateMessageFreigabe));

                            if((short) messageFromList.getInt("FT") == CMTypes.FPIC)
                            {
                                ChatImage chatImage = new ChatImage(
                                        messageFromList.getLong("PPID"),
                                        -1,
                                        messageFromList.getLong("USRN"),
                                        -1,
                                        messageFromList.getLong("TIME_POST"),
                                        (short) -3,
                                        messageFromList.getString("DES"),
                                        messageFromList.getString("PID"),
                                        friends.lookUpUsername(messageFromList.getLong("USRN")));

                                sqlChats.insertNewConversationMessage(chatImage,
                                        esaphHashtag,
                                        new ArrayList<SavedInfo>(),
                                        new JSONArray().put(new JSONObject().put("REC_ID",msgServiceConnection.getBoundServiceUID())
                                        .put("ST", ConversationStatusHelper.STATUS_CHAT_OPENED)));
                            }
                            else if((short) messageFromList.getInt("FT") == CMTypes.FVID)
                            {
                                ChatVideo chatVideo = new ChatVideo(
                                        messageFromList.getLong("PPID"),
                                        -1,
                                        messageFromList.getLong("USRN"),
                                        -1,
                                        messageFromList.getLong("TIME_POST"),
                                        (short) -3,
                                        messageFromList.getString("DES"),
                                        messageFromList.getString("PID"),
                                        friends.lookUpUsername(messageFromList.getLong("USRN")));

                                sqlChats.insertNewConversationMessage(chatVideo,
                                        esaphHashtag,
                                        new ArrayList<SavedInfo>(),
                                        new JSONArray().put(new JSONObject().put("REC_ID",msgServiceConnection.getBoundServiceUID())
                                        .put("ST", ConversationStatusHelper.STATUS_CHAT_OPENED)));
                            }
                            
                            msgServiceConnection.broadCastToAllCallbacksOnUserAllowToSeePost(
                                            sqlChats.getSingleChatPartner(
                                                    messageFromList.getLong("USRN")),
                                            chatInfoStateMessageFreigabe,
                                            sqlChats.lookUpInternIDFromServerID(messageFromList.getLong("PPID")));
                            break;

                        case MessageTypeIdentifier.CMD_UserSendTextMessageInPrivate:
                            ChatTextMessage chatTextMessage = new ChatTextMessage(
                                    messageFromList.getString("MSG"),
                                    -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ConversationStatusHelper.STATUS_NEW_MESSAGE,
                                    
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    messageFromList.getJSONObject("PLINF").toString());
                            
                            sqlChats.insertNewPrivateChatMessage(chatTextMessage,
                                    ConversationReceiverHelper.getOwnReceiver(chatTextMessage, msgServiceConnection.getBoundServiceUID()));

                            int notfiyId = friends.getFriendNotifyId(messageFromList.getLong("USRN"));

                            int privateMessageCount = sqlChats.getCountOfNewChatMessages(messageFromList.getLong("USRN"));
                            if(privateMessageCount > 0)
                            {
                                if(privateMessageCount < 2)
                                {
                                    GlobalNotificationDisplayer.displayNewPrivateMessage(context, friends.lookUpUsername(messageFromList.getLong("USRN")),
                                            messageFromList.getString("MSG"),
                                            notfiyId,
                                            SpotBackgroundDefinitionBuilder.getBackgroundColor(chatTextMessage.getEsaphPloppInformationsJSON()),
                                            SpotTextDefinitionBuilder.getTextColor(chatTextMessage.getEsaphPloppInformationsJSON()));
                                }
                                else
                                {
                                    GlobalNotificationDisplayer.displayNewPrivateMessage(context, friends.lookUpUsername(messageFromList.getLong("USRN")),
                                            context.getResources().getString(R.string.txt_newMessageCount, ""+privateMessageCount),
                                            notfiyId,
                                            SpotBackgroundDefinitionBuilder.getBackgroundColor(chatTextMessage.getEsaphPloppInformationsJSON()),
                                            SpotTextDefinitionBuilder.getTextColor(chatTextMessage.getEsaphPloppInformationsJSON()));
                                }
                            }

                            msgServiceConnection.broadCastToAllCallbacksNewContentReceived(sqlChats.getSingleChatPartner(messageFromList.getLong("USRN")));
                            //sendBroadCastForNewPrivateTextMessage(messageFromList.getLong("USRN"), messageFromList.getLong("TIME"));
                            break;

                        case MessageTypeIdentifier.CMD_UserTyping:
                            long timeUserStartedTyping = messageFromList.getLong("TIME");
                            long USERID_startedTyping = messageFromList.getLong("USRN");

                            DateTime now = DateTime.now();
                            DateTime dateTime = new DateTime(timeUserStartedTyping);
                            Seconds seconds = Seconds.secondsBetween(now, dateTime);
                            if(seconds.getSeconds() <= 30)
                            {
                                msgServiceConnection.broadCastToAllCallbacks_UserTypingState(USERID_startedTyping, true);
                            }
                            break;

                        case MessageTypeIdentifier.CMD_UserStopedTyping:
                            msgServiceConnection.broadCastToAllCallbacks_UserTypingState(messageFromList.getLong("USRN"), false);
                            break;

                        case MessageTypeIdentifier.CMD_NEW_AUDIO:
                            JSONObject jsonObjectSpotColorsNewAudio = messageFromList.getJSONObject("PLINF");

                            AudioMessage audioMessage = new AudioMessage(-1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ConversationStatusHelper.STATUS_NEW_MESSAGE,
                                    messageFromList.getString("AID"),
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    jsonObjectSpotColorsNewAudio.toString());

                            sqlChats.insertNewAudio(audioMessage, ConversationReceiverHelper.getOwnReceiver(audioMessage, msgServiceConnection.getBoundServiceUID()));
                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(audioMessage);


                            GlobalNotificationDisplayer.displayNewPrivateMessage(context,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    context.getResources().getString(R.string.txt_chat_status_new_message_audio),
                                    friends.getFriendNotifyId(messageFromList.getLong("USRN")),
                                    SpotTextDefinitionBuilder.getTextColor(jsonObjectSpotColorsNewAudio),
                                    SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObjectSpotColorsNewAudio));
                            break;

                        case MessageTypeIdentifier.CMD_NEW_STICKER:
                            JSONObject jsonObjectSpotNewSticker = messageFromList.getJSONObject("PLINF");

                            JSONObject jsonObjectPayload = messageFromList.getJSONObject("PAYLOAD");

                            EsaphStickerChatObject esaphStickerChatObject = new
                                    EsaphStickerChatObject(-1,
                                            messageFromList.getLong("USRN"),
                                            messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ConversationStatusHelper.STATUS_NEW_MESSAGE,

                                    new EsaphSpotLightSticker(messageFromList.getLong("USRN"), //Da man nur sticker verschicken kann, welche man selber erstellt hat. Wäre dies hier eigentlich nicht von Nöten.
                                            jsonObjectPayload.getLong("LSID"),
                                            jsonObjectPayload.getLong("LSPID"),
                                            jsonObjectPayload.getString("STID"),
                                            0),

                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    jsonObjectSpotNewSticker.toString());

                            sqlChats.insertNewSticker(esaphStickerChatObject, ConversationReceiverHelper.getOwnReceiver(esaphStickerChatObject, msgServiceConnection.getBoundServiceUID()));
                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(esaphStickerChatObject);

                            GlobalNotificationDisplayer.displayNewPrivateMessage(context,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    context.getResources().getString(R.string.txt_chat_status_new_message_sticker),
                                    friends.getFriendNotifyId(messageFromList.getLong("USRN")),
                                    SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObjectSpotNewSticker),
                                    -1);
                            break;

                        case MessageTypeIdentifier.CMD_NEW_EMOJIE:
                            JSONObject jsonObjectSpotNewEmojie = messageFromList.getJSONObject("PLINF");

                            EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = new
                                    EsaphAndroidSmileyChatObject(
                                            -1,
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TIME"),
                                    ConversationStatusHelper.STATUS_NEW_MESSAGE,
                                    new EsaphEmojie(messageFromList.getString("MSG")),
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    jsonObjectSpotNewEmojie.toString());

                            sqlChats.insertNewEmojieMessage(esaphAndroidSmileyChatObject, ConversationReceiverHelper.getOwnReceiver(esaphAndroidSmileyChatObject, msgServiceConnection.getBoundServiceUID()));
                            msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(esaphAndroidSmileyChatObject);

                            GlobalNotificationDisplayer.displayNewPrivateMessage(context,
                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    context.getResources().getString(R.string.txt_chat_status_new_message_emojie),
                                    friends.getFriendNotifyId(messageFromList.getLong("USRN")),
                                    SpotTextDefinitionBuilder.getTextColor(jsonObjectSpotNewEmojie),
                                    SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObjectSpotNewEmojie));
                            break;

                        case MessageTypeIdentifier.CMD_PB_UPDATE:
                            StorageHandlerProfilbild.deleteFile(context, messageFromList.getLong("USRN"));
                            break;

                        case MessageTypeIdentifier.CMD_PB_REMOVE:
                            StorageHandlerProfilbild.deleteFile(context, messageFromList.getLong("USNR"));
                            break;


                        case MessageTypeIdentifier.CMD_UserOpenenedChat:
                            sqlChats.setChatWasReadedUntilTime(messageFromList.getLong("USRN"),
                                    messageFromList.getLong("TM"));
                            break;

                        case MessageTypeIdentifier.CMD_NEW_SHARED_POST:

                            /*
                            ChatShared chatShared = new ChatShared(
                                    (short) messageFromList.getInt("TP"),
                                    messageFromList.getLong("MH"),
                                    messageFromList.getString("OU"),
                                    messageFromList.getLong("USRN"),
                                    messageFromList.getString("PID"),
                                    messageFromList.getLong("TIME"),
                                    ConversationStatusHelper.STATUS_NEW_MESSAGE,

                                    friends.lookUpUsername(messageFromList.getLong("USRN")),
                                    msgServiceConnection.getBoundServiceUsername());*/


                            sqlChats.insertNewSharedPost(null);

                            break;
                    }
                }
                sqlChats.close();
                sqlMemorys.close();
                friends.close();
            }

            if(wakeLock != null && wakeLock.isHeld())
            {
                wakeLock.release();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Message handler konnte Nachricht nicht verarbeiten: " + ec);
        }

    }

    public static final String ACTION_FRIEND_UPDATE = "esaph.spotlight.services.s.Messaging.UPDATE_STATUS.FRIEND";
    public static final String ID_UID = "esaph.appname.fcmservice.images.chat.new.extra.UID";
    public static final String ID_FOLLOW_STATE = "esaph.appname.fcmservice.images.chat.new.extra.FOLLOW_STATE";
}
