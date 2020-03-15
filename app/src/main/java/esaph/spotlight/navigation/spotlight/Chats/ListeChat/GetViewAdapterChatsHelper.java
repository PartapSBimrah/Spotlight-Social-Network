package esaph.spotlight.navigation.spotlight.Chats.ListeChat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;

public class GetViewAdapterChatsHelper
{
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static StringBuilder stringBuilder = new StringBuilder();



    private static String txt_chat_status_new_message_text = "";
    private static String txt_chat_status_new_message_audio = "";
    private static String txt_chat_status_new_message_sticker = "";
    private static String txt_chat_status_new_message_emojie = "";
    private static String txt_chat_status_new_message_bild = "";
    private static String txt_chat_status_new_message_video = "";

    private static String txt_chat_status_message_text = "";
    private static String txt_chat_status_message_audio = "";
    private static String txt_chat_status_message_sticker = "";
    private static String txt_chat_status_message_emojie = "";
    private static String txt_chat_status_message_bild = "";
    private static String txt_chat_status_message_video = "";

    public static void initStringValues(Context context)
    {
        Resources resources = context.getResources();
        txt_chat_status_new_message_text = resources.getString(R.string.txt_chat_status_new_message_text);
        txt_chat_status_new_message_audio = resources.getString(R.string.txt_chat_status_new_message_audio);
        txt_chat_status_new_message_sticker = resources.getString(R.string.txt_chat_status_new_message_sticker);
        txt_chat_status_new_message_emojie = resources.getString(R.string.txt_chat_status_new_message_emojie);
        txt_chat_status_new_message_bild = resources.getString(R.string.txt_chat_status_new_message_bild);
        txt_chat_status_new_message_video = resources.getString(R.string.txt_chat_status_new_message_video);

        txt_chat_status_message_text = resources.getString(R.string.txt_chat_status_message_text);
        txt_chat_status_message_audio = resources.getString(R.string.txt_chat_status_message_audio);
        txt_chat_status_message_sticker = resources.getString(R.string.txt_chat_status_message_sticker);
        txt_chat_status_message_emojie = resources.getString(R.string.txt_chat_status_message_emojie);
        txt_chat_status_message_bild = resources.getString(R.string.txt_chat_status_message_bild);
        txt_chat_status_message_video = resources.getString(R.string.txt_chat_status_message_video);
    }


    public static void getViewTextMessage(Context context,
                                          Typeface typeface,
                                          final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                          ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                "",
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                        System.currentTimeMillis()),
                chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewTextMessageSending(Context context, Typeface typeface, final AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_text,
                        context.getResources().getString(R.string.txt_chat_status_own_Senden),
                        GetViewAdapterChatsHelper.txt_chat_status_message_text,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewTextMessageSent(Context context,
                                              Typeface typeface,
                                              final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                              ChatPartner chatPartner)
    {
        ChatTextMessage chatTextMessage = (ChatTextMessage) chatPartner.getLastConversationMessage();

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(context,
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                context.getResources().getString(R.string.txt_chat_status_own_sent),
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                        System.currentTimeMillis()),
                chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);


        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);



    }

    public static void getViewTextMessageNewTextMessage(Context context,
                                                        Typeface typeface,
                                                        final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                        ChatPartner chatPartner)
    {
        ChatTextMessage chatTextMessage = (ChatTextMessage) chatPartner.getLastConversationMessage();
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_new_message_text,
                "",
                GetViewAdapterChatsHelper.txt_chat_status_new_message_text,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                        System.currentTimeMillis()),
                chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_CHAT_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_CHAT_TEXT);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
    }

    public static void getViewTextMessagePartnerOpened(Context context,
                                                       Typeface typeface,
                                                       final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                       ChatPartner chatPartner)
    {
        ChatTextMessage chatTextMessage = (ChatTextMessage) chatPartner.getLastConversationMessage();

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                context.getResources().getString(R.string.txt_chat_status_own_chat_opened),
                GetViewAdapterChatsHelper.txt_chat_status_message_text,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                        System.currentTimeMillis()),
                chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewAudio(Context context,
                                    Typeface typeface,
                                    final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                    ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                context.getResources().getString(R.string.txt_chat_status_partner_opened),
                GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                        System.currentTimeMillis()),
                chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewAudioNew(Context context,
                                       Typeface typeface,
                                       final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                       ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_audio,
                        "",
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_audio,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_AUDIO);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_AUDIO);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));

    }

    public static void getViewAudioSent(Context context,
                                        Typeface typeface,
                                        final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                        ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        context.getResources().getString(R.string.txt_chat_status_own_sent),
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewAudioSending(Context context,
                                           Typeface typeface,
                                           final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                           ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        context.getResources().getString(R.string.txt_chat_status_own_Senden),
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);


    }

    public static void getViewAudioPartnerOpened(Context context,
                                                 Typeface typeface,
                                                 final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                 ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        GetViewAdapterChatsHelper.txt_chat_status_message_audio,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewImage(Context context,
                                    Typeface typeface,
                                    AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                    ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        context.getResources().getString(R.string.txt_chat_status_partner_opened),
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }

    public static void getViewImageOwnPartnerOpened(Context context,
                                                    Typeface typeface,
                                                    AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                    ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }

    public static void getViewImageSent(Context context,
                                        Typeface typeface,
                                        AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                        ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        context.getResources().getString(R.string.txt_chat_status_own_Uploaded),
                        GetViewAdapterChatsHelper.txt_chat_status_message_bild,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }


    public static void getViewImageNew(Context context,
                                       Typeface typeface,
                                       AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                       ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText( //Unglaublich was ich hier fürn schrott veranstaltet hab. Dieser fehler hat mich einen ganzen scheiß tag und wieder 15 euro bußgeld gekostet.
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_bild,
                        "",
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_bild,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_IMAGE);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_IMAGE);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);
    }

    public static void getViewVideo(Context context, Typeface typeface, AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        context.getResources().getString(R.string.txt_chat_status_partner_opened),
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }

    public static void getViewVideoOwnPartnerOpened(Context context, Typeface typeface, AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }


    public static void getViewVideoSent(Context context, Typeface typeface, AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner) //I opened, nothing done with it.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        context.getResources().getString(R.string.txt_chat_status_own_Uploaded),
                        GetViewAdapterChatsHelper.txt_chat_status_message_video,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }

    public static void getViewVideoNew(Context context, Typeface typeface, AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_video,
                        "",
                        GetViewAdapterChatsHelper.txt_chat_status_new_message_video,
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_VIDEO);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_VIDEO);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);
    }

    public static void getViewInfoStateOpened(Context context, Typeface typeface, AdapterChats.ViewHolderChatPartner viewHolderChatPartner, ChatPartner chatPartner)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        "",
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatPartner.getLastConversationMessage().getMessageTime(),
                                System.currentTimeMillis()),
                        chatPartner.getLastConversationMessage().getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }

    public static void getViewInfoStateSaved(Context context,
                                             Typeface typeface,
                                             AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                             ChatInfoStateMessage chatInfoStateMessage)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        context.getResources().getString(R.string.txt_chat_status_own_saved),
                        "",
                        context.getResources().getString(R.string.txt_chat_status_own_saved),
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatInfoStateMessage.getMessageTime(),
                                System.currentTimeMillis()),
                        chatInfoStateMessage.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_WAS_SAVED);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_MY_WAS_SAVED);
    }

    public static void getViewInfoStateUnsaved(Context context,
                                               Typeface typeface,
                                               AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                               ChatInfoStateMessage chatInfoStateMessage)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        "",
                        context.getResources().getString(R.string.txt_chat_status_own_Opened),
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatInfoStateMessage.getMessageTime(),
                                System.currentTimeMillis()),
                        chatInfoStateMessage.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }


    public static void getViewInfoStatePartnerAllowedPermission(Context context,
                                                                Typeface typeface,
                                                                AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                                ChatInfoStateMessage chatInfoStateMessage)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        context.getResources().getString(R.string.txt_chat_status_partner_allowed),
                        "",
                        context.getResources().getString(R.string.txt_chat_status_partner_allowed),
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatInfoStateMessage.getMessageTime(),
                                System.currentTimeMillis()),
                        chatInfoStateMessage.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_WAS_SAVED);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }


    public static void getViewInfoStatePartnerDeclinedPermission(Context context,
                                                                 Typeface typeface,
                                                                 AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                                 ChatInfoStateMessage chatInfoStateMessage)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(
                createText(
                        context,
                        context.getResources().getString(R.string.txt_chat_status_partner_disallowed),
                        "",
                        context.getResources().getString(R.string.txt_chat_status_partner_disallowed),
                        TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatInfoStateMessage.getMessageTime(),
                                System.currentTimeMillis()),
                        chatInfoStateMessage.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_WAS_SAVED);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);
    }


    public static void getViewEmojieMessagePartnerOpened(Context context,
                                                         Typeface typeface,
                                                         final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                         EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                context.getResources().getString(R.string.txt_chat_status_own_chat_opened),
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON()
        ));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewEmojieMessageSending(Context context,
                                                   Typeface typeface,
                                                   final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                   EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(context,
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                context.getResources().getString(R.string.txt_chat_status_own_Senden),
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON()));


        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewEmojieMessageSent(Context context,
                                                Typeface typeface,
                                                final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(context,
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                context.getResources().getString(R.string.txt_chat_status_own_sent),
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);


    }

    public static void getViewEmojieMessageNewTextEmojie(Context context,
                                                         Typeface typeface,
                                                         final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                         EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_new_message_emojie,
                "",
                GetViewAdapterChatsHelper.txt_chat_status_new_message_emojie,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_SMILEY);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_SMILEY);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
    }

    public static void getViewEmojieMessage(Context context,
                                            Typeface typeface,
                                            final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                            EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject) //Smiley opened i.
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                "",
                GetViewAdapterChatsHelper.txt_chat_status_message_emojie,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewStickerMessageNewStickerMessage(Context context,
                                                              Typeface typeface,
                                                              final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                              EsaphStickerChatObject esaphStickerChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_new_message_sticker,
                "",
                GetViewAdapterChatsHelper.txt_chat_status_new_message_sticker,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphStickerChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_NEW_STICKER);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.BOLD);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ChatsAdapterColors.C_NEW_STICKER);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(ChatsAdapterDimensions.DIM_NEW);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
    }

    public static void getViewStickerMessage(Context context,
                                             Typeface typeface,
                                             final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                             EsaphStickerChatObject esaphStickerChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                context.getResources().getString(R.string.txt_chat_status_partner_opened),
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphStickerChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
    }

    public static void getViewStickerPartnerOpened(Context context,
                                                   Typeface typeface,
                                                   final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                   EsaphStickerChatObject esaphStickerChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                context.getResources().getString(R.string.txt_chat_status_partner_opened),
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphStickerChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_PARTNER_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);


        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
    }

    public static void getViewStickerMessageSending(Context context,
                                                    Typeface typeface,
                                                    final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                    EsaphStickerChatObject esaphStickerChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                context.getResources().getString(R.string.txt_chat_status_own_Senden),
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphStickerChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setFillColor(ContextCompat.getColor(context, R.color.colorGreyChatVeryLight));
        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static void getViewStickerMessageSent(Context context,
                                                 Typeface typeface,
                                                 final AdapterChats.ViewHolderChatPartner viewHolderChatPartner,
                                                 EsaphStickerChatObject esaphStickerChatObject)
    {
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setText(createText(
                context,
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                context.getResources().getString(R.string.txt_chat_status_own_sent),
                GetViewAdapterChatsHelper.txt_chat_status_message_sticker,
                TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(),
                        System.currentTimeMillis()),
                esaphStickerChatObject.getEsaphPloppInformationsJSON()));

        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
        viewHolderChatPartner.TEXTVIEW_LAST_MESSAGE.setTypeface(typeface, Typeface.NORMAL);

        viewHolderChatPartner.IMAGEVIEW_MAIN_BITMAP.setCircleShouldIgnorePadding(true);
    }

    public static String createText(Context context, String inhaltOderType, String State, String textShouldBeSpanned, String Uhrzeit, JSONObject jsonObjectPloppInformation)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(inhaltOderType);
        if(!State.isEmpty() && !inhaltOderType.isEmpty())
        {
            stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_POINT)); //Tabulator in string resource set.
        }
        stringBuilder.append(State);
        if(!Uhrzeit.isEmpty() && (!State.isEmpty() || !inhaltOderType.isEmpty()))
        {
            stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_POINT)); //Tabulator in string resource set.
        }
        stringBuilder.append(Uhrzeit);

        return stringBuilder.toString();
    }


    /*
    public static SpannableStringBuilder fontcolor(Context context, String completeText, String textToSpan, int textColor, int backgroundColor)
    {
        if(completeText.isEmpty() || textToSpan.isEmpty())
            return new SpannableStringBuilder(completeText);

        int index = completeText.indexOf(textToSpan);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(completeText);
        RoundedBackgroundSpan roundedBackgroundSpan = new RoundedBackgroundSpan().setmBackgroundColor(backgroundColor)
                .setmTextColor(textColor);
        spannableStringBuilder.setSpan(roundedBackgroundSpan, index, textToSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        return spannableStringBuilder;
    }
*/
    /*
    private Spannable getColoredTextInfo(String text)
    {
        if(completeText.isEmpty() || text.isEmpty())
            return new SpannableString(completeText);


        Spannable raw = new SpannableString(completeText);
        int index = TextUtils.indexOf(raw, text);

        while (index >= 0)
        {
            raw.setSpan(new ForegroundColorSpan(color), index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if(bold)
            {
                raw.setSpan(new StyleSpan(Typeface.BOLD), index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            index = TextUtils.indexOf(raw, text, index + text.length());
        }
    }*/
}
