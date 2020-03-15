package esaph.spotlight.navigation.spotlight.Chats.ListeChat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.ShowUserMomentsPrivate;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.AktuelleGruppe;
import esaph.spotlight.navigation.spotlight.PhoneContact;
import esaph.spotlight.services.SpotLightMessageConnection.MessageServiceCallBacks;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UploadService.UploadPost;
import esaph.spotlight.services.UploadService.UploadService;
import esaph.spotlight.services.UploadService.UploadServiceCallBacksNormal;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class AdapterChats extends BaseAdapter implements Filterable, UploadServiceCallBacksNormal, MessageServiceCallBacks
{
    private Context context;
    private List<Object> listDataDisplay;
    private List<Object> listDataOriginal;
    private ChatsFragment chatsFragment;

    private Typeface typefaceMain;
    private LayoutInflater layoutInflater;
    private int countChatPartners;
    private int countUploadPosts;

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results)
            {
                listDataDisplay = (ArrayList<Object>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Object> FilteredArrList = new ArrayList<Object>();

                if (listDataOriginal == null)
                {
                    listDataOriginal = new ArrayList<Object>(listDataDisplay); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0)
                {
                    // set the Original result to return
                    results.count = listDataOriginal.size();
                    results.values = listDataOriginal;
                }
                else
                {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listDataOriginal.size(); i++)
                    {
                        Object object = listDataOriginal.get(i);
                        if(object instanceof ChatPartner)
                        {
                            ChatPartner chatPartner = (ChatPartner) object;
                            if (chatPartner.getPartnerUsername().toLowerCase().startsWith(constraint.toString()))
                            {
                                if(chatPartner.hasConversations())
                                {
                                    ConversationMessage conversationMessageLast = chatPartner.getLastConversationMessage();
                                    ConversationMessage conNewInstance = null;
                                    if(conversationMessageLast instanceof ChatImage)
                                    {
                                        ChatImage chatImage = (ChatImage) conversationMessageLast;
                                        conNewInstance = new ChatImage(
                                                chatImage.getSERVER_ID(),
                                                chatImage.getMESSAGE_ID(),
                                                chatImage.getABS_ID(),
                                                chatImage.getID_CHAT(),
                                                chatImage.getMessageTime(),
                                                chatImage.getMessageStatus(),
                                                chatImage.getBeschreibung(),
                                                chatImage.getIMAGE_ID(),
                                                chatImage.getAbsender());
                                    }
                                    else if(conversationMessageLast instanceof ChatVideo)
                                    {
                                        ChatVideo chatVideo = (ChatVideo) conversationMessageLast;
                                        conNewInstance = new ChatVideo(
                                                chatVideo.getSERVER_ID(),
                                                chatVideo.getMESSAGE_ID(),
                                                chatVideo.getABS_ID(),
                                                chatVideo.getID_CHAT(),
                                                chatVideo.getMessageTime(),
                                                chatVideo.getMessageStatus(),
                                                chatVideo.getBeschreibung(),
                                                chatVideo.getIMAGE_ID(),
                                                chatVideo.getAbsender());
                                    }
                                    else if(conversationMessageLast instanceof AudioMessage)
                                    {
                                        AudioMessage audioMessage = (AudioMessage) conversationMessageLast;
                                        conNewInstance = new AudioMessage(audioMessage.getMESSAGE_ID(),
                                                audioMessage.getABS_ID(),
                                                audioMessage.getID_CHAT(),
                                                audioMessage.getMessageTime(),
                                                audioMessage.getMessageStatus(),
                                                audioMessage.getAID(),
                                                audioMessage.getAbsender(),
                                                audioMessage.getEsaphPloppInformationsJSONString());
                                    }
                                    else if(conversationMessageLast instanceof ChatTextMessage)
                                    {
                                        ChatTextMessage chatTextMessage = (ChatTextMessage) conversationMessageLast;
                                        conNewInstance = new ChatTextMessage(
                                                chatTextMessage.getTextMessage(),
                                                chatTextMessage.getMESSAGE_ID(),
                                                chatTextMessage.getABS_ID(),
                                                chatTextMessage.getID_CHAT(),
                                                chatTextMessage.getMessageTime(),
                                                chatTextMessage.getMessageStatus(),
                                                chatTextMessage.getAbsender(),
                                                chatTextMessage.getEsaphPloppInformationsJSONString());
                                    }
                                    else if(conversationMessageLast instanceof EsaphStickerChatObject)
                                    {
                                        EsaphStickerChatObject esaphStickerChatObject = (EsaphStickerChatObject) conversationMessageLast;
                                        conNewInstance = new EsaphStickerChatObject(
                                                esaphStickerChatObject.getMESSAGE_ID(),
                                                esaphStickerChatObject.getABS_ID(),
                                                esaphStickerChatObject.getID_CHAT(),
                                                esaphStickerChatObject.getMessageTime(),
                                                esaphStickerChatObject.getMessageStatus(),
                                                esaphStickerChatObject.getEsaphSpotLightSticker(),
                                                esaphStickerChatObject.getAbsender(),
                                                esaphStickerChatObject.getEsaphPloppInformationsJSONString());
                                    }
                                    else if(conversationMessageLast instanceof EsaphAndroidSmileyChatObject)
                                    {
                                        EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = (EsaphAndroidSmileyChatObject) conversationMessageLast;

                                        conNewInstance = new EsaphAndroidSmileyChatObject(
                                                esaphAndroidSmileyChatObject.getMESSAGE_ID(),
                                                esaphAndroidSmileyChatObject.getABS_ID(),
                                                esaphAndroidSmileyChatObject.getID_CHAT(),
                                                esaphAndroidSmileyChatObject.getMessageTime(),
                                                esaphAndroidSmileyChatObject.getMessageStatus(),
                                                esaphAndroidSmileyChatObject.getEsaphEmojie(),
                                                esaphAndroidSmileyChatObject.getAbsender(),
                                                esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSONString());

                                    }
                                    else if(conversationMessageLast instanceof ChatInfoStateMessage)
                                    {
                                        ChatInfoStateMessage chatInfoStateMessage = (ChatInfoStateMessage) conversationMessageLast;

                                        conNewInstance = new ChatInfoStateMessage(
                                                chatInfoStateMessage.getConversationMessageFrom(),
                                                chatInfoStateMessage.getMESSAGE_ID(),
                                                chatInfoStateMessage.getABS_ID(),
                                                chatInfoStateMessage.getID_CHAT(),
                                                chatInfoStateMessage.getMessageTime(),
                                                chatInfoStateMessage.getSTATE_CODE(),
                                                chatInfoStateMessage.getAbsender());
                                    }

                                    FilteredArrList.add(new ChatPartner(chatPartner.getPartnerUsername(),
                                            chatPartner.getVorname(),
                                            chatPartner.getDescriptionPlopp(),
                                            chatPartner.getUID_CHATPARTNER(),
                                            conNewInstance,
                                            chatPartner.isHideChat()));
                                }
                                else
                                {
                                    FilteredArrList.add(new ChatPartner(chatPartner.getPartnerUsername(),
                                            chatPartner.getVorname(),
                                            chatPartner.getDescriptionPlopp(),
                                            chatPartner.getUID_CHATPARTNER(),
                                            null,
                                            chatPartner.isHideChat()));
                                }
                            }
                        }
                        else if(object instanceof AktuelleGruppe)
                        {
                            AktuelleGruppe aktuellerMoment = (AktuelleGruppe) object;
                            if(aktuellerMoment.getTitle().toLowerCase().startsWith(constraint.toString()))
                            {
                                if(aktuellerMoment.hasSomePosts())
                                {
                                    FilteredArrList.add(new AktuelleGruppe(aktuellerMoment.getMIID(),
                                            aktuellerMoment.getBeitragAnzahl(),
                                            aktuellerMoment.getTitle(),
                                            aktuellerMoment.getType(),
                                            aktuellerMoment.getLastPostTime(),
                                            aktuellerMoment.getAdmin(),
                                            aktuellerMoment.getCreator(),
                                            aktuellerMoment.didILeaved(),
                                            aktuellerMoment.getLastMomentPost()));
                                }
                                else
                                {
                                    FilteredArrList.add(new AktuelleGruppe(aktuellerMoment.getMIID(),
                                            aktuellerMoment.getBeitragAnzahl(),
                                            aktuellerMoment.getTitle(),
                                            aktuellerMoment.getType(),
                                            aktuellerMoment.getLastPostTime(),
                                            aktuellerMoment.getAdmin(),
                                            aktuellerMoment.getCreator(),
                                            aktuellerMoment.didILeaved(),
                                            null));
                                }
                            }
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private ChatsFragment.DialogCallBackLifeCyle dialogCallBackLifeCyle;

    public AdapterChats(Context context,
                        ChatsFragment.DialogCallBackLifeCyle dialogCallBackLifeCyle,
                        ChatsFragment navigation_chats)
    {
        GetViewAdapterChatsHelper.initStringValues(context);
        //this.typefaceMain = Typeface.create(ResourcesCompat.getFont(context, R.font.carme), Typeface.NORMAL);

        this.typefaceMain = Typeface.DEFAULT;
        this.chatsFragment = navigation_chats;
        this.dialogCallBackLifeCyle = dialogCallBackLifeCyle;
        this.listDataDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void clearAll()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        notifyDataSetChanged();
    }

    public void pushFriendsInList(List<Object> list)
    {
        this.listDataDisplay.addAll(list);
        this.listDataOriginal.addAll(list);
        notifyDataSetChanged();
    }

    public int getCountChatPartners() {
        return countChatPartners;
    }

    public int getCountUploadPosts() {
        return countUploadPosts;
    }

    @Override
    public void notifyDataSetChanged()
    {
        Collections.sort(listDataDisplay, new SortChatsComparator());
        Collections.sort(listDataOriginal, new SortChatsComparator());

        int s = listDataDisplay.size();
        int counterChats = 0;
        int counterUploads = 0;

        for(int counter = 0; counter < s; counter++)
        {
            Object o = listDataDisplay.get(counter);
            if(o instanceof ChatPartner)
            {
                counterChats++;
            }
            else if(o instanceof UploadPost)
            {
                counterUploads++;
            }
        }
        this.countChatPartners = counterChats;
        this.countUploadPosts = counterUploads;
        super.notifyDataSetChanged();
    }

    public void removeUploadPostBecauseUploaded(UploadPost uploadPost) throws JSONException
    {
        for(int counterExtern = 0; counterExtern < this.listDataDisplay.size(); counterExtern++)
        {
            Object object = this.listDataDisplay.get(counterExtern);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostIntern = (UploadPost) object;

                if(uploadPostIntern.getMESSAGE_ID() == uploadPost.getMESSAGE_ID())
                {
                    this.listDataDisplay.remove(counterExtern);
                    JSONArray jsonArray = uploadPostIntern.getJsonArrayWAMP();

                    JSONArray jsonArrayUsers = jsonArray.getJSONArray(0);

                    if(jsonArrayUsers.length() > 0)
                    {
                        SQLChats sqlChats = new SQLChats(this.context);
                        for(int counter = 0; counter < jsonArrayUsers.length(); counter++) //FÜR JEDEN EINZELNEN NUTZER
                        {
                            JSONObject jsonObject = jsonArrayUsers.getJSONObject(counter);
                            updateChatPartner(sqlChats.getSingleChatPartner(jsonObject.getLong("REC_ID")));
                        }
                        sqlChats.close();
                    }
                    break;
                }
            }
        }

        for(int counterExtern = 0; counterExtern < this.listDataOriginal.size(); counterExtern++)
        {
            Object object = this.listDataOriginal.get(counterExtern);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostIntern = (UploadPost) object;

                if(uploadPostIntern.getMESSAGE_ID() == uploadPost.getMESSAGE_ID())
                {
                    this.listDataOriginal.remove(counterExtern);
                    JSONArray jsonArray = uploadPostIntern.getJsonArrayWAMP();

                    JSONArray jsonArrayUsers = jsonArray.getJSONArray(0);

                    if(jsonArrayUsers.length() > 0)
                    {
                        SQLChats sqlChats = new SQLChats(this.context);
                        for(int counter = 0; counter < jsonArrayUsers.length(); counter++) //FÜR JEDEN EINZELNEN NUTZER
                        {
                            JSONObject jsonObject = jsonArrayUsers.getJSONObject(counter);
                            updateChatPartner(sqlChats.getSingleChatPartner(jsonObject.getLong("REC_ID")));
                        }
                        sqlChats.close();
                    }
                    break;
                }
            }
        }
    }

    public void removeUploadPostItemByPID(long ID)
    {
        for(int counterExtern = 0; counterExtern < this.listDataDisplay.size(); counterExtern++)
        {
            Object object = this.listDataDisplay.get(counterExtern);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostIntern = (UploadPost) object;

                if(uploadPostIntern.getMESSAGE_ID() == ID)
                {
                    this.listDataDisplay.remove(counterExtern);
                    break;
                }
            }
        }

        for(int counterExtern = 0; counterExtern < this.listDataOriginal.size(); counterExtern++)
        {
            Object object = this.listDataOriginal.get(counterExtern);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostIntern = (UploadPost) object;

                if(uploadPostIntern.getMESSAGE_ID() == ID)
                {
                    this.listDataOriginal.remove(counterExtern);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    public class SortChatsComparator implements Comparator<Object>
    {
        @Override
        public int compare(Object o1, Object o2)
        {
            Date millisFirst = new Date(-1);
            Date millisSecond = new Date(-1);

            if(o1 instanceof ChatPartner)
            {
                ChatPartner chatPartner = (ChatPartner) o1;

                if(chatPartner.isHideChat())
                {
                    millisFirst = new Date(-2);
                }

                if(chatPartner.hasConversations() && !chatPartner.isHideChat())
                {
                    millisFirst = new Date(chatPartner.getLastConversationMessage().getMessageTime());
                }
            }
            else if(o1 instanceof UploadPost)
            {
                UploadPost uploadPost = (UploadPost) o1;
                millisFirst = new Date(uploadPost.getShootTime());
            }

            if(o2 instanceof ChatPartner)
            {
                ChatPartner chatPartner = (ChatPartner) o2;

                if(chatPartner.isHideChat())
                {
                    millisSecond = new Date(-2);
                }

                if(chatPartner.hasConversations() && !chatPartner.isHideChat())
                {
                    millisSecond = new Date(chatPartner.getLastConversationMessage().getMessageTime());
                }
            }
            else if(o2 instanceof UploadPost)
            {
                UploadPost uploadPost = (UploadPost) o2;
                millisSecond = new Date(uploadPost.getShootTime());
            }

            return millisSecond.compareTo(millisFirst);
        }
    }

    public void removeChatPartner(long UID_CHAT_PARTNER)
    {
        for(int counterExtern = 0; counterExtern < this.listDataDisplay.size(); counterExtern++)
        {
            Object object = this.listDataDisplay.get(counterExtern);
            if(object instanceof ChatPartner)
            {
                ChatPartner chatPartnerIntern = (ChatPartner) object;

                if(chatPartnerIntern.getUID_CHATPARTNER() == UID_CHAT_PARTNER)
                {
                    this.listDataDisplay.remove(counterExtern);
                    break;
                }
            }
        }


        for(int counterExtern = 0; counterExtern < this.listDataOriginal.size(); counterExtern++)
        {
            Object object = this.listDataOriginal.get(counterExtern);
            if(object instanceof ChatPartner)
            {
                ChatPartner chatPartnerIntern = (ChatPartner) object;

                if(chatPartnerIntern.getUID_CHATPARTNER() == UID_CHAT_PARTNER)
                {
                    this.listDataOriginal.remove(counterExtern);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    public void updateChatPartner(ChatPartner chatPartner)
    {
        boolean found = false;
        if(chatPartner == null) return;


        for(int counterExtern = 0; counterExtern < this.listDataDisplay.size(); counterExtern++)
        {
            Object object = this.listDataDisplay.get(counterExtern);
            if(object instanceof ChatPartner)
            {
                ChatPartner chatPartnerIntern = (ChatPartner) object;

                if(chatPartnerIntern.getUID_CHATPARTNER() == chatPartner.getUID_CHATPARTNER())
                {
                    this.listDataDisplay.set(counterExtern, chatPartner);
                    found = true;
                    break;
                }
            }
        }


        for(int counterExtern = 0; counterExtern < this.listDataOriginal.size(); counterExtern++)
        {
            Object object = this.listDataOriginal.get(counterExtern);
            if(object instanceof ChatPartner)
            {
                ChatPartner chatPartnerIntern = (ChatPartner) object;

                if(chatPartnerIntern.getUID_CHATPARTNER() == chatPartner.getUID_CHATPARTNER())
                {
                    this.listDataOriginal.set(counterExtern, chatPartner);
                    found = true;
                    break;
                }
            }
        }

        if(!found)
        {
            List<Object> list = new ArrayList<>();
            list.add(chatPartner);
            pushFriendsInList(list);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return listDataDisplay.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listDataDisplay.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public static class ViewHolderChatPartner
    {
        public EsaphCircleImageView IMAGEVIEW_MAIN_BITMAP;
        public RelativeLayout rootView;
        public TextView TEXTVIEW_LAST_MESSAGE;
        public TextView TEXTVIEW_CHAT_PARTNER_NAME;
    }

    private static class ViewHolderUploadsFailed
    {
        private RoundedImageView roundedImageViewPreview;
        private ImageView imageViewFailedIcon;
        private RoundCornerProgressBar progressBar;
        private TextView textViewDetails;
        private TextView textViewReceivers;
    }

    private static class ViewHolderPhoneContact
    {
        private ImageView IMAGEVIEW_MAIN_BITMAP;
        private TextView TEXTVIEW_CHAT_PARTNER_NAME;
        private TextView TEXTVIEW_ADD_BUTTON;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object objectType = this.listDataDisplay.get(position);

        if(objectType instanceof ChatPartner)
        {
            return 0;
        }
        else if(objectType instanceof UploadPost)
        {
            return 1;
        }
        else if(objectType instanceof PhoneContact)
        {
            return 2;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        switch(getItemViewType(position))
        {
            case 0:
                final ViewHolderChatPartner holder;
                final ChatPartner chatPartner = (ChatPartner) listDataDisplay.get(position);

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.adapter_chats_chat_partner, parent, false);
                    holder = new ViewHolderChatPartner();
                    holder.IMAGEVIEW_MAIN_BITMAP = (EsaphCircleImageView) convertView.findViewById(R.id.chat_partner_listview_pb);
                    holder.rootView = (RelativeLayout) convertView.findViewById(R.id.chatPartnerListViewItemRootView);
                    holder.TEXTVIEW_LAST_MESSAGE = (TextView) convertView.findViewById(R.id.chat_partner_letzte_nachricht);
                    holder.TEXTVIEW_CHAT_PARTNER_NAME = (TextView) convertView.findViewById(R.id.chat_partner_listview_benutzername);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (ViewHolderChatPartner) convertView.getTag();
                }

                if(chatPartner.isHideChat())
                {
                    holder.rootView.setAlpha(0.20f);
                }
                else
                {
                    holder.rootView.setAlpha(1f);
                }

                holder.IMAGEVIEW_MAIN_BITMAP.setAlpha(ChatsAdapterDimensions.ALPHA_NORMAL);
                holder.IMAGEVIEW_MAIN_BITMAP.setDisableCircularTransformation(false);
                holder.IMAGEVIEW_MAIN_BITMAP.setPaintStyleAll(Paint.Style.STROKE);
                holder.IMAGEVIEW_MAIN_BITMAP.setBorderColorBackground(ContextCompat.getColor(context, R.color.colorChatDumpedRounded));
                holder.IMAGEVIEW_MAIN_BITMAP.setTag(null);
                holder.IMAGEVIEW_MAIN_BITMAP.setEsaphShaderBackground(null);
                holder.IMAGEVIEW_MAIN_BITMAP.setEsaphShaderProgress(null);
                holder.IMAGEVIEW_MAIN_BITMAP.setPadding(0,0,0,0);
                holder.IMAGEVIEW_MAIN_BITMAP.setAlpha(1f);
                holder.IMAGEVIEW_MAIN_BITMAP.setBorderWidth(0);
                holder.TEXTVIEW_CHAT_PARTNER_NAME.setText(chatPartner.getPartnerUsername());

                holder.IMAGEVIEW_MAIN_BITMAP.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        chatsFragment.setCurrentFragment(ShowUserMomentsPrivate.getInstance(chatPartner));
                    }
                });

                if(chatPartner.hasConversations())
                {
                    Object object = chatPartner.getLastConversationMessage();

                    if(object != null)
                    {
                        if(object instanceof ChatInfoStateMessage)
                        {
                            ChatInfoStateMessage chatInfoStateMessage = (ChatInfoStateMessage) object;

                            switch (chatInfoStateMessage.getSTATE_CODE())
                            {
                                case ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_DECLINED_PERMISSION:
                                    GetViewAdapterChatsHelper.getViewInfoStatePartnerDeclinedPermission(context, typefaceMain, holder, chatInfoStateMessage);
                                    break;

                                case ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_ALLOWED_PERMISSION:
                                    GetViewAdapterChatsHelper.getViewInfoStatePartnerAllowedPermission(context, typefaceMain, holder, chatInfoStateMessage);
                                    break;

                                case ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_SAVED:
                                    GetViewAdapterChatsHelper.getViewInfoStateSaved(context, typefaceMain, holder, chatInfoStateMessage);
                                    break;

                                case ChatInfoStateMessage.ChatInfoStates.STATE_PARTNER_UNSAVED:
                                    GetViewAdapterChatsHelper.getViewInfoStateUnsaved(context, typefaceMain, holder, chatInfoStateMessage);
                                    break;
                            }
                        }
                        else if(object instanceof ConversationMessage)
                        {
                            ConversationMessage conversationMessage = (ConversationMessage) object;

                            if(chatPartner.getLastConversationMessage().getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID()) //Last message ist own, because i was the absender
                            {
                                switch (conversationMessage.getType())
                                {
                                    case CMTypes.FTEX:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewTextMessagePartnerOpened(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENDING:
                                                GetViewAdapterChatsHelper.getViewTextMessageSending(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewTextMessageSent(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE:
                                                GetViewAdapterChatsHelper.getViewTextMessageSending(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FAUD:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewAudioPartnerOpened(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENDING:
                                                GetViewAdapterChatsHelper.getViewAudioSending(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewAudioSent(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE:
                                                GetViewAdapterChatsHelper.getViewAudioSending(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FPIC:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewImageOwnPartnerOpened(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewImageSent(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FVID:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewVideoOwnPartnerOpened(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewVideoSent(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FEMO:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewEmojieMessagePartnerOpened(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_SENDING:
                                                GetViewAdapterChatsHelper.getViewEmojieMessageSending(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewEmojieMessageSent(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE:
                                                GetViewAdapterChatsHelper.getViewEmojieMessageSending(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;
                                        }
                                        break;


                                    case CMTypes.FSTI:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewStickerPartnerOpened(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_SENDING:
                                                GetViewAdapterChatsHelper.getViewStickerMessageSending(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_SENT:
                                                GetViewAdapterChatsHelper.getViewStickerMessageSent(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE:
                                                GetViewAdapterChatsHelper.getViewStickerMessageSending(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;
                                        }

                                        break;
                                }
                            }
                            else
                            {
                                switch (conversationMessage.getType())
                                {
                                    case CMTypes.FTEX:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewTextMessage(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewTextMessageNewTextMessage(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FAUD:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewAudio(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewAudioNew(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }
                                        break;

                                    case CMTypes.FPIC:

                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewImage(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewImageNew(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FVID:
                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewVideo(context, typefaceMain, holder, chatPartner);
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewVideoNew(context, typefaceMain, holder, chatPartner);
                                                break;
                                        }

                                        break;

                                    case CMTypes.FEMO:
                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewEmojieMessage(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewEmojieMessageNewTextEmojie(context, typefaceMain, holder, (EsaphAndroidSmileyChatObject) chatPartner.getLastConversationMessage());
                                                break;
                                        }
                                        break;

                                    case CMTypes.FSTI:
                                        switch (conversationMessage.getMessageStatus())
                                        {
                                            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                                                GetViewAdapterChatsHelper.getViewStickerMessage(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;

                                            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                                                GetViewAdapterChatsHelper.getViewStickerMessageNewStickerMessage(context, typefaceMain, holder, (EsaphStickerChatObject) chatPartner.getLastConversationMessage());
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
                else
                {
                    holder.TEXTVIEW_LAST_MESSAGE.setTextColor(ChatsAdapterColors.C_MY_NORMAL_TEXT);
                    holder.TEXTVIEW_LAST_MESSAGE.setTypeface(typefaceMain, Typeface.NORMAL);

                    try
                    {
                        JSONObject jsonObject = new JSONObject(chatPartner.getDescriptionPlopp());
                        String Beschreibung = SpotTextDefinitionBuilder.getText(jsonObject);
                        holder.TEXTVIEW_LAST_MESSAGE.setText(Beschreibung);
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "SCHROTTELUNG FAILED: " + ec);
                    }
                }

                if(chatPartner.isTypingAMessage())
                {
                    //holder.TEXTVIEW_LAST_MESSAGE.startTextAnimating();
                    holder.TEXTVIEW_LAST_MESSAGE.setText(context.getResources().getString(R.string.txtUserTypingTrippleDot));
                    holder.TEXTVIEW_LAST_MESSAGE.setTypeface(typefaceMain, Typeface.BOLD);
                    holder.TEXTVIEW_LAST_MESSAGE.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                }
                else
                {
                    // holder.TEXTVIEW_LAST_MESSAGE.stopTextAnimating();
                    //Do not set typface to normal, its beeing reset at the top, than handled from top down
                }

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        holder.IMAGEVIEW_MAIN_BITMAP,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;

            case 1:
                final ViewHolderUploadsFailed viewHolderUploadsFailed;
                final UploadPost uploadPost = (UploadPost) listDataDisplay.get(position);

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.upload_post_listview_item, parent, false);
                    viewHolderUploadsFailed = new ViewHolderUploadsFailed();
                    viewHolderUploadsFailed.textViewReceivers = (TextView) convertView.findViewById(R.id.uploadPostTextViewReceivers);
                    viewHolderUploadsFailed.progressBar = (RoundCornerProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderUploadsFailed.roundedImageViewPreview = (RoundedImageView) convertView.findViewById(R.id.roundedImageViewMessagePreview);
                    viewHolderUploadsFailed.textViewDetails = (TextView) convertView.findViewById(R.id.uploadPostTextViewDetails);
                    viewHolderUploadsFailed.imageViewFailedIcon = (ImageView) convertView.findViewById(R.id.uploadPostImageViewLeftIcon);
                    convertView.setTag(viewHolderUploadsFailed);
                }
                else
                {
                    viewHolderUploadsFailed = (ViewHolderUploadsFailed) convertView.getTag();
                }

                ProgressBarAnimation anim = new ProgressBarAnimation(viewHolderUploadsFailed.progressBar,
                        viewHolderUploadsFailed.progressBar.getProgress(),
                        uploadPost.getProgressUploading());
                anim.setDuration(500);

                viewHolderUploadsFailed.progressBar.startAnimation(anim);
                viewHolderUploadsFailed.imageViewFailedIcon.setTag(uploadPost.getPID());

                viewHolderUploadsFailed.imageViewFailedIcon.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialogCallBackLifeCyle.onStartDialog(uploadPost);
                    }
                });

                viewHolderUploadsFailed.textViewDetails.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), uploadPost.getShootTime(), System.currentTimeMillis()));
                viewHolderUploadsFailed.textViewDetails.setVisibility(View.VISIBLE);

                if(uploadService != null && uploadService.isUploading(uploadPost.getMESSAGE_ID()))
                {
                    viewHolderUploadsFailed.imageViewFailedIcon.setAlpha(0.40f);
                    viewHolderUploadsFailed.progressBar.setVisibility(View.VISIBLE);
                    viewHolderUploadsFailed.textViewDetails.setVisibility(View.GONE);
                }
                else
                {
                    viewHolderUploadsFailed.progressBar.setVisibility(View.GONE);
                    viewHolderUploadsFailed.textViewDetails.setVisibility(View.VISIBLE);
                    viewHolderUploadsFailed.textViewDetails.setText(context.getResources().getString(R.string.txt_chat_status_own_message_failed_sending));
                    viewHolderUploadsFailed.imageViewFailedIcon.setAlpha(1f);
                    Glide.with(context).load(R.drawable.ic_failed_sent).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUploadsFailed.imageViewFailedIcon.setImageDrawable(resource);
                            }
                        }
                    });
                }

                viewHolderUploadsFailed.textViewReceivers.setText(uploadPost.getPreviewForList());


                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        uploadPost.getPID(),
                        viewHolderUploadsFailed.roundedImageViewPreview,
                        null,
                        new EsaphDimension(viewHolderUploadsFailed.roundedImageViewPreview.getWidth(),
                                viewHolderUploadsFailed.roundedImageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));
                break;


            case 2: //Phone contact
                final ViewHolderPhoneContact viewHolderPhoneContact;
                final PhoneContact phoneContact = (PhoneContact) listDataDisplay.get(position);

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_contact_invite, parent, false);
                    viewHolderPhoneContact = new ViewHolderPhoneContact();
                    viewHolderPhoneContact.IMAGEVIEW_MAIN_BITMAP = (ImageView) convertView.findViewById(R.id.chat_partner_listview_pb);
                    viewHolderPhoneContact.TEXTVIEW_CHAT_PARTNER_NAME = (TextView) convertView.findViewById(R.id.chat_partner_listview_benutzername);
                    viewHolderPhoneContact.TEXTVIEW_ADD_BUTTON = (TextView) convertView.findViewById(R.id.textViewAddButton);
                    convertView.setTag(viewHolderPhoneContact);
                }
                else
                {
                    viewHolderPhoneContact = (ViewHolderPhoneContact) convertView.getTag();
                }


                if(phoneContact.isCanAdd())
                {
                    // viewHolderPhoneContact.TEXTVIEW_LAST_MESSAGE.setText(context.getResources().getString(R.string.txt_fromYourContacts));
                    viewHolderPhoneContact.TEXTVIEW_ADD_BUTTON.setText(context.getResources().getString(R.string.txt_hinzufuegen));
                }
                else
                {
                    viewHolderPhoneContact.TEXTVIEW_ADD_BUTTON.setText(context.getResources().getString(R.string.txt_invite));
                }

                viewHolderPhoneContact.TEXTVIEW_CHAT_PARTNER_NAME.setText(phoneContact.getName());

                Glide.with(context).load(R.drawable.ic_add_user).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            viewHolderPhoneContact.IMAGEVIEW_MAIN_BITMAP.setImageDrawable(resource);
                        }
                    }
                });

                break;
        }
        return convertView;
    }

    /*
    ownMessage == TRUE
    -1 failed to send
    0 means, sending
    1 means, currently sent
    2 means, user seen it.

    ownMessage == FALSE
    3 means, new message
    2 means, message has been seen.
     */

    private boolean isUploadServiceBound = false;
    private UploadService uploadService;

    private ServiceConnection myConnectionUploadService = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            UploadService.MyLocalBinder binder = (UploadService.MyLocalBinder) service;
            uploadService = binder.getService(AdapterChats.this);
            isUploadServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            isUploadServiceBound = false;
        }
    };

    public void insertNewUploadPostOrUpdate(UploadPost uploadPost)
    {
        int size = listDataDisplay.size();
        boolean postExists = false;
        for(int counter = 0; counter < size; counter++)
        {
            Object object = listDataDisplay.get(counter);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostList = (UploadPost) object;
                System.out.println("CHECKING POST EXISTS: " + uploadPost.getMESSAGE_ID() + " == " + uploadPostList.getMESSAGE_ID());
                if(uploadPostList.getMESSAGE_ID() == uploadPost.getMESSAGE_ID())
                {
                    postExists = true;
                    listDataDisplay.set(counter, uploadPost);
                    break;
                }
            }
        }

        if(!postExists)
        {
            listDataDisplay.add(0, uploadPost);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onPostFailedUpload(final UploadPost uploadPost)
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dialogCallBackLifeCyle.onPostFailedUpload(uploadPost);
                uploadPost.setProgressUploading(0);
                insertNewUploadPostOrUpdate(uploadPost);
            }
        });
    }

    @Override
    public void onPostUploading(final UploadPost uploadPost)
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dialogCallBackLifeCyle.onPostUploading(uploadPost.getPID());
                insertNewUploadPostOrUpdate(uploadPost);
            }
        });
    }

    @Override
    public void onProgressUpdate(UploadPost uploadPost, int progress)
    {
        uploadPost.setProgressUploading(progress);
        notifyDataSetChanged();
    }

    @Override
    public void onPostUploadSuccess(final UploadPost uploadPost, final long PPID)
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    dialogCallBackLifeCyle.onPostUploadSuccess(uploadPost, PPID);
                    removeUploadPostBecauseUploaded(uploadPost);
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "onPostUploadAdapterChats() failed: " + ec);
                }
            }
        });
    }

    public ServiceConnection getConnectionUploadService()
    {
        return myConnectionUploadService;
    }

    public boolean isUploadServiceBound()
    {
        return isUploadServiceBound;
    }

    public UploadService getUploadService()
    {
        return uploadService;
    }

    public UploadServiceCallBacksNormal getUploadServiceCallBack()
    {
        return AdapterChats.this;
    }

    private MsgServiceConnection msgServiceConnection;
    private boolean isBoundSendingConnection = false;

    public boolean isBoundSendingConnection() {
        return isBoundSendingConnection;
    }

    public ServiceConnection getServiceConnectionSendingText() {
        return myConnectionSendingText;
    }

    public MsgServiceConnection getMessageService()
    {
        return msgServiceConnection;
    }

    private ServiceConnection myConnectionSendingText = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            MsgServiceConnection.MyLocalBinder binder = (MsgServiceConnection.MyLocalBinder) service;
            msgServiceConnection = binder.getService(AdapterChats.this);
            isBoundSendingConnection = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            isBoundSendingConnection = false;
        }
    };

    @Override
    public void onFriendUpdate(short FRIEND_STATUS, ChatPartner chatPartner)
    {
        if(chatPartner != null)
        {
            updateChatPartner(chatPartner);
        }

        chatsFragment.onFriendUpdate(FRIEND_STATUS, chatPartner);
    }

    @Override
    public void onUserRemovedPost(ChatPartner chatPartner, long MESSAGE_ID)
    {
        if(chatPartner != null)
        {
            updateChatPartner(chatPartner);
        }
    }

    @Override
    public void onUserAllowedToSeePostAgain(ChatPartner chatPartner, ChatInfoStateMessage chatInfoStateMessage, long MESSAGE_ID)
    {
        if(chatPartner != null)
        {
            updateChatPartner(chatPartner);
        }
    }

    @Override
    public void onUserDisallowedToSeePost(ChatPartner chatPartner, ChatInfoStateMessage chatInfoStateMessage, long MESSAGE_ID)
    {
        if(chatPartner != null)
        {
            updateChatPartner(chatPartner);
        }
    }

    @Override
    public void onUserUpdateInsertNewContent(ChatPartner chatPartner, ConversationMessage conversationMessage)
    {
        if(chatPartner != null)
        {
            updateChatPartner(chatPartner);
        }
    }

    @Override
    public void onMessageUpdate(ConversationMessage conversationMessage, ChatInfoStateMessage chatInfoStateMessage)
    {
    }

    @Override
    public void onMessageUpdate(ChatTextMessage chatTextMessage)
    {
        ChatPartner chatPartner = null;
        if(chatTextMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID())
        {
            chatPartner = getChatPartnerByID(chatTextMessage.getID_CHAT());
        }
        else
        {
            chatPartner = getChatPartnerByID(chatTextMessage.getABS_ID());
        }

        if(chatPartner != null)
        {
            ConversationMessage conversationMessageChatPartner = (ConversationMessage) chatPartner.getLastConversationMessage();
            if(conversationMessageChatPartner != null)
            {
                chatPartner.setLastConversationMessage(chatTextMessage);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onMessageUpdate(AudioMessage audioMessage)
    {
        ChatPartner chatPartner = null;
        if(audioMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID())
        {
            chatPartner = getChatPartnerByID(audioMessage.getID_CHAT());
        }
        else
        {
            chatPartner = getChatPartnerByID(audioMessage.getABS_ID());
        }


        if(chatPartner != null)
        {
            ConversationMessage conversationMessage = (ConversationMessage) chatPartner.getLastConversationMessage();
            if(conversationMessage != null && conversationMessage.getMessageTime() == audioMessage.getMessageTime())
            {
                chatPartner.setLastConversationMessage(audioMessage);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onMessageUpdate(EsaphStickerChatObject esaphStickerChatObject)
    {
        ChatPartner chatPartner = null;
        if(esaphStickerChatObject.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID())
        {
            chatPartner = getChatPartnerByID(esaphStickerChatObject.getID_CHAT());
        }
        else
        {
            chatPartner = getChatPartnerByID(esaphStickerChatObject.getABS_ID());
        }


        if(chatPartner != null)
        {
            ConversationMessage conversationMessage = (ConversationMessage) chatPartner.getLastConversationMessage();
            if(conversationMessage != null && conversationMessage.getMessageTime() == esaphStickerChatObject.getMessageTime())
            {
                chatPartner.setLastConversationMessage(esaphStickerChatObject);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onMessageUpdate(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        ChatPartner chatPartner = null;
        if(esaphAndroidSmileyChatObject.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID())
        {
            chatPartner = getChatPartnerByID(esaphAndroidSmileyChatObject.getID_CHAT());
        }
        else
        {
            chatPartner = getChatPartnerByID(esaphAndroidSmileyChatObject.getABS_ID());
        }


        if(chatPartner != null)
        {
            ConversationMessage conversationMessage = (ConversationMessage) chatPartner.getLastConversationMessage();
            if(conversationMessage != null && conversationMessage.getMessageTime() == esaphAndroidSmileyChatObject.getMessageTime())
            {
                chatPartner.setLastConversationMessage(esaphAndroidSmileyChatObject);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onUpdateUserTyping(final long USER_ID, final boolean typing)
    {
        ChatPartner chatPartner = getChatPartnerByID(USER_ID);
        if(chatPartner != null)
        {
            chatPartner.setTypingAMessage(typing);
            updateChatPartner(chatPartner);
        }
    }

    private ChatPartner getChatPartnerByID(long USER_ID)
    {
        for(int counter = 0; counter < listDataDisplay.size(); counter++)
        {
            Object object = listDataDisplay.get(counter);
            if(object instanceof ChatPartner)
            {
                ChatPartner chatPartner = (ChatPartner) object;
                if(chatPartner.getUID_CHATPARTNER() == USER_ID)
                {
                    return chatPartner;
                }
            }
        }
        return null;
    }

}