package esaph.spotlight.navigation.spotlight.Chats.PrivateChat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chibde.visualizer.CircleBarVisualizer;
import com.hanks.htextview.fall.FallTextView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphContainsUtils;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphListView.EsaphListViewKeepPosition;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveSinglePostFromPrivateUser;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.ListDataChangedListener;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.AudioMessagePlayer.EsaphAudioMessagePlayer;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.InformationNoChats;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogAlertPostIsInDeleteMode;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.ShowUserMomentsPrivate;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UploadService.UploadPost;
import esaph.spotlight.services.UploadService.UploadServiceCallBacksNormal;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotTextRenderView;

public class ArrayAdapterPrivateChat extends BaseAdapter implements UploadServiceCallBacksNormal
{
    private MediaPlayer mediaPlayer;
    private long LOGGED_UID;
    private static int SCALE_PADDING_DIFFERENT_MESSAGES;
    private static int SCALE_PADDING_ONE_DP;
    private Context context;
    private List<Object> listDataDisplay;
    private LayoutInflater layoutInflater;
    private ChatPartner chatPartner;
    private PrivateChat privateChat;
    private static float ALPHA_SENDING = 0.6f;
    private ListView listView;
    private WeakReference<ListDataChangedListener> listDataChangedListenerWeakReference;

    public ArrayAdapterPrivateChat(PrivateChat privateChat,
                                   ChatPartner chatPartner,
                                   ListView listView)
    {
        this.context = privateChat;
        this.LOGGED_UID = SpotLightLoginSessionHandler.getLoggedUID();
        this.privateChat = privateChat;
        this.listView = listView;

        SCALE_PADDING_DIFFERENT_MESSAGES = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12, privateChat.getResources().getDisplayMetrics());

        SCALE_PADDING_ONE_DP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, privateChat.getResources().getDisplayMetrics());

        this.chatPartner = chatPartner;
        this.listDataDisplay = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setListDataChangedListenerWeakReference(ListDataChangedListener listDataChangedListener)
    {
        this.listDataChangedListenerWeakReference = new WeakReference<ListDataChangedListener>(listDataChangedListener);
    }

    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    public void clear()
    {
        this.listDataDisplay.clear();
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() //NEVER CALL FROM THIS METHOD NOTIFYDATASETCHAGEND.
    {
        if(listDataChangedListenerWeakReference != null)
        {
            ListDataChangedListener listDataChangedListener = listDataChangedListenerWeakReference.get();
            if(listDataChangedListener != null)
            {
                listDataChangedListener.onListDataChanged(); //Need to be set for the viewpager if its opened to notifyviewpager when same list data is changed.
            }
        }

        privateChat.showNoChats(listDataDisplay.isEmpty());

        //Handle code before superclass getting called.
        super.notifyDataSetChanged();
    }

    public void pushData(List<Object> conversationMessages,
                         EsaphListViewKeepPosition listView)
    {
        // get first visible position of the list view
        int firstVisPos = listView.getFirstVisiblePosition();// get child view at visible 0th position of the listview
        View firstVisView = listView.getChildAt(0);// set top in pixel of the child view
        int top = firstVisView != null ? firstVisView.getTop() : 0;// block from laying child layout
        listView.setBlockLayoutChildren(true);// add new item to the collection
        int savedPosition = listDataDisplay.size();

        listDataDisplay.addAll(0, conversationMessages);

        notifyDataSetChanged();// un block from laying child layout
        listView.setBlockLayoutChildren(false);// finally set item selection
        listView.setSelection(listView.getCount());

        System.out.println("HURENPOSITION: " + (savedPosition + conversationMessages.size()));



        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setSelectionFromTop(firstVisPos +   itemsAddedBeforeFirstVisible, top);
        } else {

        }*/


        if(canNotifyWait)
        {
            synchronized (listWaitNotifyObject)
            {
                listWaitNotifyObject.notifyAll();
            }
        }
    }

    public void pushSingleMessage(Object objectAdding)
    {
        listDataDisplay.add(objectAdding);
        scrollMyListViewToBottom();
        notifyDataSetChanged();
    }

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

    public void scrollMyListViewToBottom()
    {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(getCount() - 1);
            }
        });
    }

    private void scrollSmoothMyListViewToPosition(final ListView listView, final int pos)
    {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.smoothScrollToPosition(pos);
            }
        });
    }

    public void setPostUploaded(UploadPost uploadPost, ConversationMessage conversationMessage)
    {
        for(int counter = 0; counter < listDataDisplay.size(); counter++)
        {
            Object object = listDataDisplay.get(counter);
            if(object instanceof UploadPost)
            {
                UploadPost uploadPostIntern = (UploadPost) object;
                if(uploadPostIntern.getMESSAGE_ID() == uploadPost.getMESSAGE_ID())
                {
                    listDataDisplay.set(counter, conversationMessage);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }


    public void updateConversationMessageByID(ConversationMessage conversationMessage)
    {
        if(conversationMessage == null) return;

        for(int counter = listDataDisplay.size() - 1; counter >= 0 ; counter--)
        {
            Object object = listDataDisplay.get(counter);
            if (object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessageIntern = (ConversationMessage) object;
                if(conversationMessageIntern.getMESSAGE_ID() == conversationMessage.getMESSAGE_ID())
                {
                    listDataDisplay.set(counter, conversationMessage);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeImageAndVideosFromPartner()
    {
        long OWN_UID = SpotLightLoginSessionHandler.getLoggedUID();
        for (Iterator<Object> iterator = listDataDisplay.iterator(); iterator.hasNext();)
        {
            Object object = iterator.next();
            if (object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if((conversationMessage.getType() == CMTypes.FPIC ||
                        conversationMessage.getType() == CMTypes.FVID) && conversationMessage.getABS_ID() != OWN_UID)
                {
                    iterator.remove();
                }
            }
        }
        notifyDataSetChanged();
    }


    public void removeItemById(long MESSAGE_ID)
    {
        for(int counter = 0; counter < listDataDisplay.size(); counter++)
        {
            Object object = listDataDisplay.get(counter);
            if (object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if (conversationMessage.getMESSAGE_ID() == MESSAGE_ID)
                {
                    listDataDisplay.remove(counter);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public int getCount()
    {
        return listDataDisplay.size();
    }

    public long getLastTimeMillis()
    {
        Object object = listDataDisplay.get(0);
        if(object instanceof ConversationMessage)
        {
            ConversationMessage conversationMessage = (ConversationMessage) object;
            return conversationMessage.getMessageTime();
        }
        else if(object instanceof DatumList)
        {
            try
            {
                DatumList datumHolder = (DatumList) object;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
                return simpleDateFormat.parse(datumHolder.getNormalUniqueDatumFormat()).getTime();
            }
            catch (Exception ec)
            {
                return -1;
            }
        }
        return -1;
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

    @Override
    public void onPostFailedUpload(final UploadPost uploadPost)
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
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
                insertNewUploadPostOrUpdate(uploadPost);
            }
        });
    }

    @Override
    public void onProgressUpdate(UploadPost uploadPost, int progress) {

    }

    @Override
    public void onPostUploadSuccess(final UploadPost uploadPost, final long PPID)
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = new SQLChats(context);
                setPostUploaded(uploadPost, sqlChats.getPostByInternIdAndChatKey(PPID, chatPartner.getUID_CHATPARTNER()));
                sqlChats.close();
                notifyDataSetChanged();
            }
        });
    }

    private static class ViewHolderOwnAudio
    {
        private ImageView imageViewProfilbild;
        private ImageView imageViewMainContent;
        private TextView textViewUhrzeitAndUsername;
        private CircleBarVisualizer circleBarVisualizer;
        private FallTextView textViewPlayTime;
        private RelativeLayout relativLayoutRootView;
        private ProgressBar progressBar;
    }

    private static class ViewHolderPartnerAudio
    {
        private ImageView imageViewProfilbild;
        private ImageView imageViewMainContent;
        private TextView textViewUhrzeitAndUsername;
        private CircleBarVisualizer circleBarVisualizer;
        private FallTextView textViewPlayTime;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatMessageOwn
    {
        private ImageView imageViewProfilbild;
        private SpotTextRenderView spotTextRenderView;
        private TextView textViewUhrzeitAndUsername;
        private RelativeLayout relativLayoutRootView;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatMessagePartner
    {
        private ImageView imageViewProfilbild;
        private SpotTextRenderView spotTextRenderView;
        private TextView textViewUhrzeitAndUsername;
        private RelativeLayout relativLayoutRootView;
        private ProgressBar progressBar;
    }

    private static class ViewHolderEmojieOwn
    {
        private ImageView imageViewProfilbild;
        private ImageView imageViewMainContent;
        private TextView textViewUhrzeitAndUsername;
        private RelativeLayout relativLayoutRootView;
        private ProgressBar progressBar;
    }

    private static class ViewHolderEmojiePartner
    {
        private ImageView imageViewProfilbild;
        private ImageView imageViewMainContent;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatImageOwn
    {
        private CheckBox checkBoxAdded;
        private ImageView imageViewProfilbild;
        private RelativeLayout relativLayoutRootView;
        private ImageView imageViewPreview;
        private TextView textViewPostStatus;
        private TextView textViewBeschreibung;
        private TextView textViewHashtags;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatVideoOwn
    {
        private CheckBox checkBoxAdded;
        private ImageView imageViewProfilbild;
        private TextView textViewHashtags;
        private ImageView imageViewPreview;
        private TextView textViewPostStatus;
        private ImageView imageViewVideoIcon;
        private TextView textViewBeschreibung;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }


    private static class ViewHolderChatImagePartnerOpened
    {
        private CheckBox checkBoxAdded;
        private ImageView imageViewProfilbild;
        private TextView textViewHashtags;
        private ImageView imageViewPreview;
        private TextView textViewPostStatus;
        private TextView textViewBeschreibung;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatVideoMessagePartnerOpened
    {
        private CheckBox checkBoxAdded;
        private ImageView imageViewProfilbild;
        private RelativeLayout relativeLayoutRootView;
        private TextView textViewHashtags;
        private ImageView imageViewPreview;
        private TextView textViewPostStatus;
        private TextView textViewBeschreibung;
        private TextView textViewUhrzeitAndUsername;
        private ImageView imageViewVideoIcon;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatVideoMessagePartner
    {
        private EsaphCircleImageView imageViewPreview;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatImagePartner
    {
        private ImageView imageViewMainContent;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderUploadPost
    {
        private AVLoadingIndicatorView avLoadingIndicatorViewUploading;
        private ImageView imageViewVideoIcon;
        private ImageView imageViewDelete;
        private TextView textViewHashtags;
        private ImageView imageViewPreview;
        private TextView textViewPostStatus;
        private TextView textViewBeschreibung;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatStickerOwn
    {
        private ImageView imageViewProfilbild;
        private RelativeLayout relativLayoutRootView;
        private ImageView imageViewSticker;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderChatStickerPartner
    {
        private ImageView imageViewProfilbild;
        private ImageView imageViewSticker;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderPartnerInfoState
    {
        private TextView textViewInfo;
        private ImageView imageView;
        private RelativeLayout relativeLayoutRootView;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }

    private static class ViewHolderMyInfoState
    {
        private TextView textViewInfo;
        private ImageView imageView;
        private RelativeLayout relativeLayoutRootView;
        private TextView textViewUhrzeitAndUsername;
        private ProgressBar progressBar;
    }


    private static class ViewHolderDatum
    {
        private TextView textViewDatum;
    }

    private static class ViewHolderInformation
    {
        private TextView textViewText;
        private LinearLayout linearLayoutInternGallery;
        private LinearLayout linearLayoutOurMoments;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = this.listDataDisplay.get(position);

        System.out.println("CURRENT TYPE: " + object.getClass().getName());

        if(object instanceof AudioMessage)
        {
            AudioMessage audioMessage = (AudioMessage) object;
            if(audioMessage.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                return 13;
            }
            else
            {
                return 14;
            }
        }
        else if(object instanceof ChatTextMessage)
        {
            ChatTextMessage chatTextMessage = (ChatTextMessage) object;
            if(chatTextMessage.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                return 0;
            }
            else
            {
                return 4;
            }
        }
        else if(object instanceof ChatInfoStateMessage)
        {
            ChatInfoStateMessage chatInfoStateMessage = (ChatInfoStateMessage) object;
            if(chatInfoStateMessage.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                return 3;
            }
            else
            {
                return 9;
            }
        }
        else if(object instanceof EsaphStickerChatObject)
        {
            EsaphStickerChatObject esaphStickerChatObject = (EsaphStickerChatObject) object;
            if(esaphStickerChatObject.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                return 15;
            }
            else
            {
                return 16;
            }
        }
        else if(object instanceof EsaphAndroidSmileyChatObject)
        {
            EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = (EsaphAndroidSmileyChatObject) object;
            if(esaphAndroidSmileyChatObject.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                return 17;
            }
            else
            {
                return 18;
            }
        }
        else if(object instanceof ConversationMessage)
        {
            ConversationMessage conversationMessage = (ConversationMessage) object;
            if(conversationMessage.getABS_ID() == LOGGED_UID) //ICH BIN ABSENDER
            {
                if(conversationMessage.getType() == (CMTypes.FPIC))
                {
                    return 1;
                }
                else if(conversationMessage.getType() == (CMTypes.FVID))
                {
                    return 2;
                }
            }
            else
            {
                if(conversationMessage.getType() == (CMTypes.FPIC))
                {
                    if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_CHAT_OPENED)
                    {
                        return 5;
                    }
                    else if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_NEW_MESSAGE)
                    {
                        return 6;
                    }
                }
                else if(conversationMessage.getType() == (CMTypes.FVID))
                {
                    if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_CHAT_OPENED)
                    {
                        return 7;
                    }
                    else if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_NEW_MESSAGE)
                    {
                        return 8;
                    }
                }
            }
        }
        else if(object instanceof UploadPost)
        {
            return 10;
        }
        else if(object instanceof DatumList)
        {
            return 11;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 18;
    }

    private View getView(final int position, View convertView, ViewGroup parent, final ChatTextMessage chatTextMessage)
    {
        switch(getItemViewType(position))
        {
            case 0:
                final ViewHolderChatMessageOwn viewHolderChatMessageOwn;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_own, parent, false);
                    viewHolderChatMessageOwn = new ViewHolderChatMessageOwn();
                    viewHolderChatMessageOwn.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatMessageOwn.spotTextRenderView = (SpotTextRenderView) convertView.findViewById(R.id.spotTextRenderViewAdapter);
                    viewHolderChatMessageOwn.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatMessageOwn.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    convertView.setTag(viewHolderChatMessageOwn);
                }
                else
                {
                    viewHolderChatMessageOwn = (ViewHolderChatMessageOwn) convertView.getTag();
                }

                if(chatTextMessage.getMessageStatus() == ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE
                        || chatTextMessage.getMessageStatus() == ConversationStatusHelper.STATUS_SENDING)
                {
                    viewHolderChatMessageOwn.relativLayoutRootView.setAlpha(ArrayAdapterPrivateChat.ALPHA_SENDING);
                }
                else
                {
                    viewHolderChatMessageOwn.relativLayoutRootView.setAlpha(1f);
                }

                if((position+1) == listDataDisplay.size())
                {
                    viewHolderChatMessageOwn.relativLayoutRootView.setPadding(0,
                            ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                            0,
                            ArrayAdapterPrivateChat.SCALE_PADDING_DIFFERENT_MESSAGES);  // left, top, right, bottom);
                }
                else
                {
                    if(shouldBePaddingDoubled(position, chatTextMessage))
                    {
                        viewHolderChatMessageOwn.relativLayoutRootView.setPadding(0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                                0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_DIFFERENT_MESSAGES);  // left, top, right, bottom);
                    }
                    else
                    {
                        viewHolderChatMessageOwn.relativLayoutRootView.setPadding(0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                                0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP);  // left, top, right, bottom);
                    }
                }

                viewHolderChatMessageOwn.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatTextMessage.getMessageTime(), System.currentTimeMillis()));
                viewHolderChatMessageOwn.spotTextRenderView.onValuesChanged(chatTextMessage.getEsaphPloppInformationsJSON(), chatTextMessage.getTextMessage());

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatMessageOwn.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;


            case 4:
                final ViewHolderChatMessagePartner viewHolderChatMessagePartner;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner, parent, false);
                    viewHolderChatMessagePartner = new ViewHolderChatMessagePartner();
                    viewHolderChatMessagePartner.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatMessagePartner.spotTextRenderView = (SpotTextRenderView) convertView.findViewById(R.id.spotTextRenderViewAdapterPartner);
                    viewHolderChatMessagePartner.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatMessagePartner.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    convertView.setTag(viewHolderChatMessagePartner);
                }
                else
                {
                    viewHolderChatMessagePartner = (ViewHolderChatMessagePartner) convertView.getTag();
                }

                if((position+1) == listDataDisplay.size())
                {
                    viewHolderChatMessagePartner.relativLayoutRootView.setPadding(0,
                            ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                            0,
                            ArrayAdapterPrivateChat.SCALE_PADDING_DIFFERENT_MESSAGES);  // left, top, right, bottom);
                }
                else
                {
                    if(shouldBePaddingDoubled(position, chatTextMessage))
                    {
                        viewHolderChatMessagePartner.relativLayoutRootView.setPadding(0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                                0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_DIFFERENT_MESSAGES);  // left, top, right, bottom);
                    }
                    else
                    {
                        viewHolderChatMessagePartner.relativLayoutRootView.setPadding(0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP,
                                0,
                                ArrayAdapterPrivateChat.SCALE_PADDING_ONE_DP);  // left, top, right, bottom);
                    }
                }

                viewHolderChatMessagePartner.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatTextMessage.getMessageTime(), System.currentTimeMillis()));
                viewHolderChatMessagePartner.spotTextRenderView.onValuesChanged(chatTextMessage.getEsaphPloppInformationsJSON(),
                        chatTextMessage.getTextMessage());

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatMessagePartner.imageViewProfilbild,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;
        }
        return convertView;
    }

    private View getView(final int position, View convertView, ViewGroup parent, final ChatImage chatImage)
    {
        switch(getItemViewType(position))
        {
            case 1:
                final ViewHolderChatImageOwn viewHolderChatImageOwn;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_own_image, parent, false);
                    viewHolderChatImageOwn = new ViewHolderChatImageOwn();
                    viewHolderChatImageOwn.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatImageOwn.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatImageOwn.imageViewPreview = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderChatImageOwn.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderChatImageOwn.textViewPostStatus = (TextView) convertView.findViewById(R.id.textViewImageStatus);
                    viewHolderChatImageOwn.textViewBeschreibung = (TextView) convertView.findViewById(R.id.textViewBeschreibung);
                    viewHolderChatImageOwn.textViewHashtags = (TextView) convertView.findViewById(R.id.textViewHashtags);
                    viewHolderChatImageOwn.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    viewHolderChatImageOwn.checkBoxAdded = (CheckBox) convertView.findViewById(R.id.imageViewAddToGallery);
                    convertView.setTag(viewHolderChatImageOwn);
                }
                else
                {
                    viewHolderChatImageOwn = (ViewHolderChatImageOwn) convertView.getTag();
                }


                EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback() {
                    @Override
                    public void onExecute(boolean hasSaved)
                    {
                        if(viewHolderChatImageOwn.checkBoxAdded == null) return;

                        if(hasSaved)
                        {
                            viewHolderChatImageOwn.checkBoxAdded.setChecked(true);
                            viewHolderChatImageOwn.checkBoxAdded.jumpDrawablesToCurrentState();
                        }
                        else {
                            viewHolderChatImageOwn.checkBoxAdded.setChecked(false);
                            viewHolderChatImageOwn.checkBoxAdded.jumpDrawablesToCurrentState();
                        }
                    }
                },
                        chatImage.getMESSAGE_ID(),
                        LOGGED_UID);


                viewHolderChatImageOwn.checkBoxAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleImageOrVideoSaveState(chatImage, (CheckBox) v);
                    }
                });

                viewHolderChatImageOwn.imageViewPreview.setTag(chatImage.getIMAGE_ID());

                if(chatImage.getBeschreibung() != null && !chatImage.getBeschreibung().isEmpty())
                {
                    viewHolderChatImageOwn.textViewBeschreibung.setVisibility(View.VISIBLE);
                    viewHolderChatImageOwn.textViewBeschreibung.setText(chatImage.getBeschreibung());
                }
                else
                {
                    viewHolderChatImageOwn.textViewBeschreibung.setVisibility(View.GONE);
                }

                viewHolderChatImageOwn.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatImage.getMessageTime(), System.currentTimeMillis()));

                setHandlePostStateStatusAsync(viewHolderChatImageOwn.textViewPostStatus,
                        chatImage);

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatImage.getIMAGE_ID(),
                        viewHolderChatImageOwn.imageViewPreview,
                        viewHolderChatImageOwn.progressBar,
                        new EsaphDimension(viewHolderChatImageOwn.imageViewPreview.getWidth(),
                                viewHolderChatImageOwn.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatImageOwn.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;

            case 5: //Image opened.
                final ViewHolderChatImagePartnerOpened viewHolderChatImagePartnerOpened;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner_image_opened, parent, false);
                    viewHolderChatImagePartnerOpened = new ViewHolderChatImagePartnerOpened();
                    viewHolderChatImagePartnerOpened.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatImagePartnerOpened.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatImagePartnerOpened.imageViewPreview = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderChatImagePartnerOpened.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderChatImagePartnerOpened.textViewPostStatus = (TextView) convertView.findViewById(R.id.textViewImageStatus);
                    viewHolderChatImagePartnerOpened.textViewBeschreibung = (TextView) convertView.findViewById(R.id.textViewBeschreibung);
                    viewHolderChatImagePartnerOpened.textViewHashtags = (TextView) convertView.findViewById(R.id.textViewHashtags);
                    viewHolderChatImagePartnerOpened.checkBoxAdded = (CheckBox) convertView.findViewById(R.id.imageViewAddToGallery);
                    convertView.setTag(viewHolderChatImagePartnerOpened);
                }
                else
                {
                    viewHolderChatImagePartnerOpened = (ViewHolderChatImagePartnerOpened) convertView.getTag();
                }


                EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback() {
                            @Override
                            public void onExecute(boolean hasSaved)
                            {
                                if(viewHolderChatImagePartnerOpened.checkBoxAdded == null) return;

                                if(hasSaved)
                                {
                                    viewHolderChatImagePartnerOpened.checkBoxAdded.setChecked(true);
                                    viewHolderChatImagePartnerOpened.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                                else {
                                    viewHolderChatImagePartnerOpened.checkBoxAdded.setChecked(false);
                                    viewHolderChatImagePartnerOpened.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                            }
                        },
                        chatImage.getMESSAGE_ID(),
                        LOGGED_UID);



                viewHolderChatImagePartnerOpened.checkBoxAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleImageOrVideoSaveState(chatImage, (CheckBox) v);
                    }
                });


                viewHolderChatImagePartnerOpened.imageViewPreview.setTag(chatImage.getIMAGE_ID());


                if(chatImage.getBeschreibung() != null && !chatImage.getBeschreibung().isEmpty())
                {
                    viewHolderChatImagePartnerOpened.textViewBeschreibung.setVisibility(View.VISIBLE);
                    viewHolderChatImagePartnerOpened.textViewBeschreibung.setText(chatImage.getBeschreibung());
                }
                else
                {
                    viewHolderChatImagePartnerOpened.textViewBeschreibung.setVisibility(View.GONE);
                }

                setHandlePostStateStatusAsync(viewHolderChatImagePartnerOpened.textViewPostStatus, chatImage);

                viewHolderChatImagePartnerOpened.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatImage.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatImage.getIMAGE_ID(),
                        viewHolderChatImagePartnerOpened.imageViewPreview,
                        viewHolderChatImagePartnerOpened.progressBar,
                        new EsaphDimension(viewHolderChatImagePartnerOpened.imageViewPreview.getWidth(),
                                viewHolderChatImagePartnerOpened.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatImagePartnerOpened.imageViewProfilbild,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;

            case 6:
                final ViewHolderChatImagePartner viewHolderChatImagePartner;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner_image, parent, false);
                    viewHolderChatImagePartner = new ViewHolderChatImagePartner();
                    viewHolderChatImagePartner.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewMessageTime);
                    viewHolderChatImagePartner.imageViewMainContent = (ImageView) convertView.findViewById(R.id.imageViewMainPreview);
                    viewHolderChatImagePartner.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    convertView.setTag(viewHolderChatImagePartner);
                }
                else
                {
                    viewHolderChatImagePartner = (ViewHolderChatImagePartner) convertView.getTag();
                }

                viewHolderChatImagePartner.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatImage.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(
                        ImageRequest.builder(chatImage.getIMAGE_ID(),
                                viewHolderChatImagePartner.imageViewMainContent,
                        viewHolderChatImagePartner.progressBar,
                        new EsaphDimension(viewHolderChatImagePartner.imageViewMainContent.getWidth(),
                                viewHolderChatImagePartner.imageViewMainContent.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));
                break;
        }

        return convertView;
    }


    private View getView(final int position, View convertView, ViewGroup parent, final ChatVideo chatVideo)
    {
        switch(getItemViewType(position))
        {
            case 2:
                final ViewHolderChatVideoOwn viewHolderChatVideoOwn;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_own_video, parent, false);
                    viewHolderChatVideoOwn = new ViewHolderChatVideoOwn();
                    viewHolderChatVideoOwn.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatVideoOwn.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatVideoOwn.imageViewPreview = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderChatVideoOwn.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderChatVideoOwn.textViewPostStatus = (TextView) convertView.findViewById(R.id.textViewImageStatus);
                    viewHolderChatVideoOwn.imageViewVideoIcon = (ImageView) convertView.findViewById(R.id.imageViewVideoIcon);
                    viewHolderChatVideoOwn.textViewBeschreibung = (TextView) convertView.findViewById(R.id.textViewBeschreibung);
                    viewHolderChatVideoOwn.textViewHashtags = (TextView) convertView.findViewById(R.id.textViewHashtags);
                    viewHolderChatVideoOwn.checkBoxAdded = (CheckBox) convertView.findViewById(R.id.imageViewAddToGallery);
                    convertView.setTag(viewHolderChatVideoOwn);
                }
                else
                {
                    viewHolderChatVideoOwn = (ViewHolderChatVideoOwn) convertView.getTag();
                }

                EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback() {
                            @Override
                            public void onExecute(boolean hasSaved)
                            {
                                if(viewHolderChatVideoOwn.checkBoxAdded == null) return;

                                if(hasSaved)
                                {
                                    viewHolderChatVideoOwn.checkBoxAdded.setChecked(true);
                                    viewHolderChatVideoOwn.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                                else {
                                    viewHolderChatVideoOwn.checkBoxAdded.setChecked(false);
                                    viewHolderChatVideoOwn.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                            }
                        },
                        chatVideo.getMESSAGE_ID(),
                        LOGGED_UID);


                viewHolderChatVideoOwn.checkBoxAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleImageOrVideoSaveState(chatVideo, (CheckBox) v);
                    }
                });

                viewHolderChatVideoOwn.imageViewPreview.setTag(chatVideo.getIMAGE_ID());

                if(chatVideo.getBeschreibung() != null && !chatVideo.getBeschreibung().isEmpty())
                {
                    viewHolderChatVideoOwn.textViewBeschreibung.setVisibility(View.VISIBLE);
                    viewHolderChatVideoOwn.textViewBeschreibung.setText(chatVideo.getBeschreibung());
                }
                else
                {
                    viewHolderChatVideoOwn.textViewBeschreibung.setVisibility(View.GONE);
                }

                setHandlePostStateStatusAsync(viewHolderChatVideoOwn.textViewPostStatus, chatVideo);

                Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            viewHolderChatVideoOwn.imageViewVideoIcon.setImageDrawable(resource);
                        }
                    }
                });

                viewHolderChatVideoOwn.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatVideo.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatVideo.getIMAGE_ID(),
                        viewHolderChatVideoOwn.imageViewPreview,
                        viewHolderChatVideoOwn.progressBar,
                        new EsaphDimension(viewHolderChatVideoOwn.imageViewPreview.getWidth(),
                                viewHolderChatVideoOwn.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatVideoOwn.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;


            case 7:
                final ViewHolderChatVideoMessagePartnerOpened viewHolderChatVideoMessagePartnerOpened;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner_video_opened, parent, false);
                    viewHolderChatVideoMessagePartnerOpened = new ViewHolderChatVideoMessagePartnerOpened();
                    viewHolderChatVideoMessagePartnerOpened.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatVideoMessagePartnerOpened.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatVideoMessagePartnerOpened.imageViewPreview = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderChatVideoMessagePartnerOpened.relativeLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    viewHolderChatVideoMessagePartnerOpened.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderChatVideoMessagePartnerOpened.imageViewVideoIcon = (ImageView) convertView.findViewById(R.id.imageViewVideoIcon);
                    viewHolderChatVideoMessagePartnerOpened.textViewPostStatus = (TextView) convertView.findViewById(R.id.textViewImageStatus);
                    viewHolderChatVideoMessagePartnerOpened.textViewBeschreibung = (TextView) convertView.findViewById(R.id.textViewBeschreibung);
                    viewHolderChatVideoMessagePartnerOpened.textViewHashtags = (TextView) convertView.findViewById(R.id.textViewHashtags);
                    viewHolderChatVideoMessagePartnerOpened.checkBoxAdded = (CheckBox) convertView.findViewById(R.id.imageViewAddToGallery);
                    convertView.setTag(viewHolderChatVideoMessagePartnerOpened);
                }
                else
                {
                    viewHolderChatVideoMessagePartnerOpened = (ViewHolderChatVideoMessagePartnerOpened) convertView.getTag();
                }


                EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback() {
                            @Override
                            public void onExecute(boolean hasSaved)
                            {
                                if(viewHolderChatVideoMessagePartnerOpened.checkBoxAdded == null) return;
                                if(hasSaved)
                                {
                                    viewHolderChatVideoMessagePartnerOpened.checkBoxAdded.setChecked(true);
                                    viewHolderChatVideoMessagePartnerOpened.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                                else {
                                    viewHolderChatVideoMessagePartnerOpened.checkBoxAdded.setChecked(false);
                                    viewHolderChatVideoMessagePartnerOpened.checkBoxAdded.jumpDrawablesToCurrentState();
                                }
                            }
                        },
                        chatVideo.getMESSAGE_ID(),
                        LOGGED_UID);


                viewHolderChatVideoMessagePartnerOpened.checkBoxAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleImageOrVideoSaveState(chatVideo, (CheckBox) v);
                    }
                });

                viewHolderChatVideoMessagePartnerOpened.imageViewPreview.setTag(chatVideo.getIMAGE_ID());

                if(chatVideo.getBeschreibung() != null && !chatVideo.getBeschreibung().isEmpty())
                {
                    viewHolderChatVideoMessagePartnerOpened.textViewBeschreibung.setVisibility(View.VISIBLE);
                    viewHolderChatVideoMessagePartnerOpened.textViewBeschreibung.setText(chatVideo.getBeschreibung());
                }
                else
                {
                    viewHolderChatVideoMessagePartnerOpened.textViewBeschreibung.setVisibility(View.GONE);
                }

                Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            viewHolderChatVideoMessagePartnerOpened.imageViewVideoIcon.setImageDrawable(resource);
                        }
                    }
                });

                setHandlePostStateStatusAsync(viewHolderChatVideoMessagePartnerOpened.textViewPostStatus, chatVideo);
                viewHolderChatVideoMessagePartnerOpened.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatVideo.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatVideo.getIMAGE_ID(),
                        viewHolderChatVideoMessagePartnerOpened.imageViewPreview,
                        viewHolderChatVideoMessagePartnerOpened.progressBar,
                        new EsaphDimension(viewHolderChatVideoMessagePartnerOpened.imageViewPreview.getWidth(),
                                viewHolderChatVideoMessagePartnerOpened.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));


                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatVideoMessagePartnerOpened.imageViewProfilbild,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;

            case 8:
                final ViewHolderChatVideoMessagePartner viewHolderChatVideoMessagePartner;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner_video, parent, false);
                    viewHolderChatVideoMessagePartner = new ViewHolderChatVideoMessagePartner();
                    viewHolderChatVideoMessagePartner.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewMessageTime);
                    viewHolderChatVideoMessagePartner.imageViewPreview = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewMainPreview);
                    viewHolderChatVideoMessagePartner.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    convertView.setTag(viewHolderChatVideoMessagePartner);
                }
                else
                {
                    viewHolderChatVideoMessagePartner = (ViewHolderChatVideoMessagePartner) convertView.getTag();
                }

                viewHolderChatVideoMessagePartner.imageViewPreview.setTag(chatVideo.getIMAGE_ID());

                viewHolderChatVideoMessagePartner.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatVideo.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatVideo.getIMAGE_ID(),
                        viewHolderChatVideoMessagePartner.imageViewPreview,
                        viewHolderChatVideoMessagePartner.progressBar,
                        new EsaphDimension(viewHolderChatVideoMessagePartner.imageViewPreview.getWidth(),
                                viewHolderChatVideoMessagePartner.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));
                break;
        }

        return convertView;
    }

    private View getView(final int position, View convertView, ViewGroup parent, final InformationNoChats informationNoChats)
    {
        final ViewHolderInformation viewHolderInformation;

        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layout_chat_information_no_chat, parent, false);
            viewHolderInformation = new ViewHolderInformation();
            viewHolderInformation.textViewText = (TextView) convertView.findViewById(R.id.textViewInformationText);
            viewHolderInformation.linearLayoutInternGallery = (LinearLayout) convertView.findViewById(R.id.linearLayoutInformationOpenInternGallery);
            viewHolderInformation.linearLayoutOurMoments = (LinearLayout) convertView.findViewById(R.id.linearLayoutInformationOpenOurGallery);
            convertView.setTag(viewHolderInformation);
        }
        else
        {
            viewHolderInformation = (ViewHolderInformation) convertView.getTag();
        }

        viewHolderInformation.linearLayoutInternGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getResources().getString(R.string.app_name));
                dialog.setMessage(context.getResources().getString(R.string.txt_still_working));
                dialog.show();
            }
        });

        viewHolderInformation.linearLayoutOurMoments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                privateChat.setBottomFragmentFullscreen(ShowUserMomentsPrivate.getInstance(chatPartner));
            }
        });

        viewHolderInformation.textViewText.setText(context.getResources().getString(R.string.txt_chat_information_write_user, chatPartner.getPartnerUsername()));
        return convertView;
    }

    private View getView(final int position, View convertView, ViewGroup parent, DatumList datumHolder)
    {
        switch(getItemViewType(position))
        {
            case 11:
                final ViewHolderDatum viewHolderDatum;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_datum, parent, false);
                    viewHolderDatum = new ViewHolderDatum();
                    viewHolderDatum.textViewDatum = (TextView) convertView.findViewById(R.id.textViewChatMessageDatumSet);
                    convertView.setTag(viewHolderDatum);
                }
                else
                {
                    viewHolderDatum = (ViewHolderDatum) convertView.getTag();
                }

                /*
                if(datumHolder.getNormalUniqueDatumFormat().equals(DATUM_HEUTE))
                {
                    viewHolderDatum.textViewDatum.setText(context.getResources().getString(R.string.txt_heute));
                }
                else if(datumHolder.getNormalUniqueDatumFormat().equals(DATUM_GESTERN))
                {
                    viewHolderDatum.textViewDatum.setText(context.getResources().getString(R.string.txt_gestern));
                }
                else
                {
                    viewHolderDatum.textViewDatum.setText(datumHolder.getNormalUniqueDatumFormat());
                }
                */

                break;
        }
        return convertView;
    }


    private View getView(int position, View convertView, ViewGroup parent, EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        switch(getItemViewType(position))
        {
            case 17: //Ich bin Absender
                final ViewHolderEmojieOwn viewHolderEmojieOwn;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_emojie_own, parent, false);
                    viewHolderEmojieOwn = new ViewHolderEmojieOwn();
                    viewHolderEmojieOwn.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderEmojieOwn.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderEmojieOwn.imageViewMainContent = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderEmojieOwn.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    convertView.setTag(viewHolderEmojieOwn);
                }
                else
                {
                    viewHolderEmojieOwn = (ViewHolderEmojieOwn) convertView.getTag();
                }

                viewHolderEmojieOwn.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(), System.currentTimeMillis()));


                if(esaphAndroidSmileyChatObject.getMessageStatus() == ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE
                        || esaphAndroidSmileyChatObject.getMessageStatus() == ConversationStatusHelper.STATUS_SENDING)
                {
                    viewHolderEmojieOwn.relativLayoutRootView.setAlpha(ArrayAdapterPrivateChat.ALPHA_SENDING);
                }
                else
                {
                    viewHolderEmojieOwn.relativLayoutRootView.setAlpha(1f);
                }

                EsaphGlobalImageLoader.with(context).canvasMode(CanvasRequest.builder(viewHolderEmojieOwn.imageViewMainContent, new EsaphDimension(
                        viewHolderEmojieOwn.imageViewMainContent.getWidth(),
                        viewHolderEmojieOwn.imageViewMainContent.getHeight()
                ), esaphAndroidSmileyChatObject).setAutoTextSize(true));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderEmojieOwn.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;


            case 18:
                final ViewHolderEmojiePartner viewHolderEmojiePartner;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_emojie_partner, parent, false);
                    viewHolderEmojiePartner = new ViewHolderEmojiePartner();
                    viewHolderEmojiePartner.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderEmojiePartner.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderEmojiePartner.imageViewMainContent = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    convertView.setTag(viewHolderEmojiePartner);
                }
                else
                {
                    viewHolderEmojiePartner = (ViewHolderEmojiePartner) convertView.getTag();
                }

                viewHolderEmojiePartner.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphAndroidSmileyChatObject.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).canvasMode(CanvasRequest.builder(viewHolderEmojiePartner.imageViewMainContent, new EsaphDimension(
                        viewHolderEmojiePartner.imageViewMainContent.getWidth(),
                        viewHolderEmojiePartner.imageViewMainContent.getHeight()
                ), esaphAndroidSmileyChatObject).setAutoTextSize(true));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderEmojiePartner.imageViewProfilbild,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;
        }
        return convertView;
    }

    private View getView(int position, View convertView, ViewGroup parent, EsaphStickerChatObject esaphStickerChatObject)
    {
        switch(getItemViewType(position))
        {
            case 15: //Ich bin Absender
                final ViewHolderChatStickerOwn viewHolderChatSticker;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_sticker_own, parent, false);
                    viewHolderChatSticker = new ViewHolderChatStickerOwn();
                    viewHolderChatSticker.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatSticker.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatSticker.imageViewSticker = (ImageView) convertView.findViewById(R.id.imageViewSticker);
                    viewHolderChatSticker.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    viewHolderChatSticker.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    convertView.setTag(viewHolderChatSticker);
                }
                else
                {
                    viewHolderChatSticker = (ViewHolderChatStickerOwn) convertView.getTag();
                }

                viewHolderChatSticker.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(), System.currentTimeMillis()));

                if(esaphStickerChatObject.getMessageStatus() == ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE
                        || esaphStickerChatObject.getMessageStatus() == ConversationStatusHelper.STATUS_SENDING)
                {
                    viewHolderChatSticker.relativLayoutRootView.setAlpha(ArrayAdapterPrivateChat.ALPHA_SENDING);
                }
                else
                {
                    viewHolderChatSticker.relativLayoutRootView.setAlpha(1f);
                }

                EsaphGlobalImageLoader.with(context).displayImage(
                        StickerRequest.builder(esaphStickerChatObject.getEsaphSpotLightSticker().getIMAGE_ID(),
                        viewHolderChatSticker.imageViewSticker,
                        viewHolderChatSticker.progressBar,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_sticker_missing));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatSticker.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;


            case 16:
                final ViewHolderChatStickerPartner viewHolderChatStickerPartner;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_sticker_partner, parent, false);
                    viewHolderChatStickerPartner = new ViewHolderChatStickerPartner();
                    viewHolderChatStickerPartner.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderChatStickerPartner.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderChatStickerPartner.imageViewSticker = (ImageView) convertView.findViewById(R.id.imageViewSticker);
                    viewHolderChatStickerPartner.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarSmallViewLoading);
                    convertView.setTag(viewHolderChatStickerPartner);
                }
                else
                {
                    viewHolderChatStickerPartner = (ViewHolderChatStickerPartner) convertView.getTag();
                }

                viewHolderChatStickerPartner.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), esaphStickerChatObject.getMessageTime(), System.currentTimeMillis()));

                EsaphGlobalImageLoader.with(context).displayImage(
                        StickerRequest.builder(esaphStickerChatObject.getEsaphSpotLightSticker().getIMAGE_ID(),
                        viewHolderChatStickerPartner.imageViewSticker,
                        viewHolderChatStickerPartner.progressBar,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_sticker_missing));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderChatStickerPartner.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;
        }
        return convertView;
    }

    private View getView(final int position, View convertView, ViewGroup parent, final UploadPost uploadPost)
    {
        switch(getItemViewType(position))
        {
            case 10:
                final ViewHolderUploadPost viewHolderUploadPost;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_partner_uploading, parent, false);
                    viewHolderUploadPost = new ViewHolderUploadPost();
                    viewHolderUploadPost.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderUploadPost.imageViewPreview = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderUploadPost.textViewPostStatus = (TextView) convertView.findViewById(R.id.textViewImageStatus);
                    viewHolderUploadPost.imageViewVideoIcon = (ImageView) convertView.findViewById(R.id.imageViewVideoIcon);
                    viewHolderUploadPost.imageViewDelete = (ImageView) convertView.findViewById(R.id.imageViewDelete);
                    viewHolderUploadPost.textViewBeschreibung = (TextView) convertView.findViewById(R.id.textViewBeschreibung);
                    viewHolderUploadPost.textViewHashtags = (TextView) convertView.findViewById(R.id.textViewHashtags);
                    viewHolderUploadPost.avLoadingIndicatorViewUploading = (AVLoadingIndicatorView) convertView.findViewById(R.id.progressBarUploadingImage);
                    convertView.setTag(viewHolderUploadPost);
                }
                else
                {
                    viewHolderUploadPost = (ViewHolderUploadPost) convertView.getTag();
                }

                viewHolderUploadPost.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v)
                    {
                        SQLUploads sqlUploads = new SQLUploads(context);
                        sqlUploads.removeSinglePostCompletly(uploadPost.getPID());
                        sqlUploads.close();


                        removeItemById(uploadPost.getMESSAGE_ID());
                    }
                });

                if(uploadPost.getBeschreibung() != null && !uploadPost.getBeschreibung().isEmpty())
                {
                    viewHolderUploadPost.textViewBeschreibung.setVisibility(View.VISIBLE);
                    viewHolderUploadPost.textViewBeschreibung.setText(uploadPost.getBeschreibung());
                }
                else
                {
                    viewHolderUploadPost.textViewBeschreibung.setVisibility(View.GONE);
                }

                if(uploadPost.getHashtagsTogether() != null && !uploadPost.getHashtagsTogether().isEmpty())
                {
                    viewHolderUploadPost.textViewHashtags.setVisibility(View.VISIBLE);
                    viewHolderUploadPost.textViewHashtags.setText(uploadPost.getHashtagsTogether());
                }
                else
                {
                    viewHolderUploadPost.textViewHashtags.setVisibility(View.GONE);
                }

                viewHolderUploadPost.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), uploadPost.getShootTime(), System.currentTimeMillis()));

                viewHolderUploadPost.imageViewPreview.setTag(uploadPost.getPID());

                if(uploadPost.getType() == (CMTypes.FVID))
                {
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUploadPost.imageViewVideoIcon.setImageDrawable(resource);
                            }
                        }
                    });
                }
                else
                {
                    viewHolderUploadPost.imageViewVideoIcon.setImageDrawable(null);
                }

                /*
                if(uploadService != null && uploadService.isUploading(uploadPost.getIMAGE_ID()))
                {
                    viewHolderUploadPost.avLoadingIndicatorViewUploading.smoothToShow();
                    viewHolderUploadPost.textViewPostStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                    Glide.with(context).load(R.drawable.background_rounded_blue_line).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUploadPost.textViewPostStatus.setBackground(resource);
                            }
                        }
                    });
                }
                else
                {
                    viewHolderUploadPost.avLoadingIndicatorViewUploading.smoothToHide();
                    viewHolderUploadPost.textViewPostStatus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    Glide.with(context).load(R.drawable.background_rounded_blue).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUploadPost.textViewPostStatus.setBackground(resource);
                            }
                        }
                    });
                }*/

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        uploadPost.getPID(),
                        viewHolderUploadPost.imageViewPreview,
                        null,
                        new EsaphDimension(viewHolderUploadPost.imageViewPreview.getWidth(),
                                viewHolderUploadPost.imageViewPreview.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));

                break;
        }
        return convertView;
    }

    private View getView(final int position, View convertView, ViewGroup parent, final ChatInfoStateMessage chatInfoStateMessage)
    {
        switch(getItemViewType(position))
        {
            case 3:
                final ViewHolderMyInfoState viewHolderMyInfoState;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_info_my_state, parent, false);
                    viewHolderMyInfoState = new ViewHolderMyInfoState();
                    viewHolderMyInfoState.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderMyInfoState.imageView = (ImageView) convertView.findViewById(R.id.imageViewInfoImage);
                    viewHolderMyInfoState.relativeLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutRootViewState);
                    viewHolderMyInfoState.textViewInfo = (TextView) convertView.findViewById(R.id.textViewTextInfo);
                    convertView.setTag(viewHolderMyInfoState);
                }
                else
                {
                    viewHolderMyInfoState = (ViewHolderMyInfoState) convertView.getTag();
                }

                viewHolderMyInfoState.relativeLayoutRootView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int result = findPositionFromImageOrVideo(position, chatInfoStateMessage.getIMAGE_ID());

                        if(result < 0) return;

                        privateChat.onItemClickTriggered(result);
                    }
                });

                viewHolderMyInfoState.relativeLayoutRootView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int result = findPositionFromImageOrVideo(position, chatInfoStateMessage.getIMAGE_ID());

                        if(result < 0) return true;

                        // TODO: 27.04.2019 insert this function everywhere .

                        return true;
                    }
                });

                viewHolderMyInfoState.textViewInfo.setText("");

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        chatInfoStateMessage.getIMAGE_ID(),
                        viewHolderMyInfoState.imageView,
                        null,
                        new EsaphDimension(viewHolderMyInfoState.imageView.getWidth(),
                                viewHolderMyInfoState.imageView.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));

                break;

            case 9: //Parter hat irgendwas gemacht, gespeichert oder so.
                final ViewHolderPartnerInfoState viewHolderPartnerInfoState;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_info_partner_state, parent, false);
                    viewHolderPartnerInfoState = new ViewHolderPartnerInfoState();
                    viewHolderPartnerInfoState.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderPartnerInfoState.imageView = (ImageView) convertView.findViewById(R.id.imageViewInfoImage);
                    viewHolderPartnerInfoState.relativeLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutRootViewState);
                    viewHolderPartnerInfoState.textViewInfo = (TextView) convertView.findViewById(R.id.textViewTextInfo);
                    convertView.setTag(viewHolderPartnerInfoState);
                }
                else
                {
                    viewHolderPartnerInfoState = (ViewHolderPartnerInfoState) convertView.getTag();
                }

                viewHolderPartnerInfoState.relativeLayoutRootView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: 03.02.2019 make this avaiable

                        //privateChat.onItemClickTriggered(findPositionFromImageOrVideo(position, chatInfoStateMessage.getIMAGE_ID()));
                    }
                });

                viewHolderPartnerInfoState.relativeLayoutRootView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        return true;
                    }
                });

                String arrayChoose = context.getResources().getStringArray(R.array.arrayInfoStatesForChatPartner)[chatInfoStateMessage.getSTATE_CODE()];
                arrayChoose = arrayChoose.replaceFirst("%s", chatPartner.getPartnerUsername());

                ConversationMessage conversationMessageFrom = chatInfoStateMessage.getConversationMessageFrom();
                if(conversationMessageFrom != null)
                {
                    if(conversationMessageFrom.getType() == (CMTypes.FVID))
                    {
                        arrayChoose = arrayChoose.replaceFirst("%s", context.getResources().getString(R.string.txt_VIDEO));
                    }
                    else if(conversationMessageFrom.getType() == (CMTypes.FPIC))
                    {
                        arrayChoose = arrayChoose.replaceFirst("%s", context.getResources().getString(R.string.txt_IMAGE));
                    }

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            conversationMessageFrom.getIMAGE_ID(),
                            viewHolderPartnerInfoState.imageView,
                            null,
                            new EsaphDimension(viewHolderPartnerInfoState.imageView.getWidth(),
                                    viewHolderPartnerInfoState.imageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle));
                }
                else
                {
                    arrayChoose = arrayChoose.replaceFirst("%s", "");

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            "",
                            viewHolderPartnerInfoState.imageView,
                            null,
                            new EsaphDimension(viewHolderPartnerInfoState.imageView.getWidth(),
                                    viewHolderPartnerInfoState.imageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle));
                }

                viewHolderPartnerInfoState.textViewInfo.setText(arrayChoose);
                break;
        }
        return convertView;
    }


    private SimpleDateFormat simpleDateFormatAudioMillis = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private View getView(final int position, View convertView, ViewGroup parent, final AudioMessage chatAudioMessage)
    {
        switch (getItemViewType(position))
        {
            case 13: //ICH ABSENDER JA
                final ViewHolderOwnAudio viewHolderOwnAudio;

                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_memo_own, parent, false);
                    viewHolderOwnAudio = new ViewHolderOwnAudio();
                    viewHolderOwnAudio.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderOwnAudio.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderOwnAudio.circleBarVisualizer = (CircleBarVisualizer) convertView.findViewById(R.id.imageViewVisualAudio);
                    viewHolderOwnAudio.textViewPlayTime = (FallTextView) convertView.findViewById(R.id.textViewPlayLength);
                    viewHolderOwnAudio.imageViewMainContent = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    viewHolderOwnAudio.relativLayoutRootView = (RelativeLayout) convertView.findViewById(R.id.relativLayoutRootView);
                    convertView.setTag(viewHolderOwnAudio);
                }
                else
                {
                    viewHolderOwnAudio = (ViewHolderOwnAudio) convertView.getTag();
                }

                if(chatAudioMessage.getMessageStatus() == ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE
                        || chatAudioMessage.getMessageStatus() == ConversationStatusHelper.STATUS_SENDING)
                {
                    viewHolderOwnAudio.relativLayoutRootView.setAlpha(ArrayAdapterPrivateChat.ALPHA_SENDING);
                }
                else
                {
                    viewHolderOwnAudio.relativLayoutRootView.setAlpha(1f);
                }

                viewHolderOwnAudio.circleBarVisualizer.setColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                viewHolderOwnAudio.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatAudioMessage.getMessageTime(), System.currentTimeMillis()));
                viewHolderOwnAudio.circleBarVisualizer.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        try
                        {
                            if(mediaPlayer == null) mediaPlayer = new MediaPlayer();

                            if(chatAudioMessage.getFileDescriptor() != null)
                            {
                                try
                                {
                                    if(isPlayerPrepared && PREPARED_AID.equals(chatAudioMessage.getAID()))
                                    {
                                        if(mediaPlayer.isPlaying())
                                        {
                                            mediaPlayer.pause();
                                            MEDIAPLAYER_CURRENT_POS = mediaPlayer.getCurrentPosition();
                                            countDownTimer.pause();
                                        }
                                        else
                                        {
                                            mediaPlayer.seekTo(MEDIAPLAYER_CURRENT_POS);
                                            mediaPlayer.start();
                                            countDownTimer.resume();
                                        }
                                    }
                                }
                                catch (Exception ec)
                                {
                                }

                                if(!isPlayerPrepared || !PREPARED_AID.equals(chatAudioMessage.getAID()))
                                {
                                    if(androidIsStupidAudioFinishListener != null)
                                    {
                                        androidIsStupidAudioFinishListener.onFinished();
                                    }

                                    mediaPlayer.stop();
                                    mediaPlayer.reset();

                                    mediaPlayer.setDataSource(chatAudioMessage.getFileDescriptor());
                                    viewHolderOwnAudio.circleBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                                    mediaPlayer.prepare();
                                    isPlayerPrepared = true;
                                    PREPARED_AID = chatAudioMessage.getAID();

                                    if(countDownTimer != null)
                                    {
                                        countDownTimer.cancel();
                                    }

                                    androidIsStupidAudioFinishListener = new AndroidIsStupidAudioFinishListener()
                                    {
                                        @Override
                                        public void onFinished()
                                        {
                                            mediaPlayer.stop();
                                            viewHolderOwnAudio.circleBarVisualizer.release();
                                            viewHolderOwnAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(chatAudioMessage.getLengthMillis()));
                                            isPlayerPrepared = false;
                                        }
                                    };

                                    countDownTimer = new CountDownTimerWithPause(chatAudioMessage.getLengthMillis(), 1000,
                                            true)
                                    {
                                        @Override
                                        public void onTick(long millisUntilFinished)
                                        {
                                            viewHolderOwnAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millisUntilFinished));
                                        }

                                        @Override
                                        public void onFinish()
                                        {
                                        }
                                    };

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            androidIsStupidAudioFinishListener.onFinished();
                                        }
                                    });

                                    mediaPlayer.start();
                                    countDownTimer.resume();
                                }
                            }
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "PrivateChatPlayAudio(OWNAUDIO) failed: " + ec);
                        }
                    }
                });

                EsaphAudioMessagePlayer.with(context).handleAudioMessage(chatAudioMessage, new EsaphAudioMessagePlayer.AudioHandlerCallBack()
                {
                    @Override
                    public void onAudioMessageAvailable(File file)
                    {
                        try
                        {
                            FileInputStream fis = new FileInputStream(file);
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(fis.getFD());
                            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            int millSecond = Integer.parseInt(durationStr);
                            viewHolderOwnAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millSecond));
                            chatAudioMessage.setFileDescriptor(fis.getFD());
                            chatAudioMessage.setLengthMillis(millSecond);
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "onAudioMessageAvailable(OWNAUDIO) failed in adapter callback: " + ec);
                        }
                    }

                    @Override
                    public void onAudioMessageDownloadFailed()
                    {
                    }
                });

                EsaphGlobalImageLoader.with(context).canvasMode(CanvasRequest.builder(viewHolderOwnAudio.imageViewMainContent, new EsaphDimension(
                        viewHolderOwnAudio.imageViewMainContent.getWidth(),
                        viewHolderOwnAudio.imageViewMainContent.getHeight()
                ), chatAudioMessage));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderOwnAudio.imageViewProfilbild,
                        null,
                        LOGGED_UID,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;


            case 14: //MEMO VOM PARTNER
                final ViewHolderPartnerAudio viewHolderPartnerAudio;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_chat_message_memo_partner, parent, false);
                    viewHolderPartnerAudio = new ViewHolderPartnerAudio();
                    viewHolderPartnerAudio.imageViewProfilbild = (EsaphCircleImageView) convertView.findViewById(R.id.imageViewProfilbild);
                    viewHolderPartnerAudio.textViewUhrzeitAndUsername = (TextView) convertView.findViewById(R.id.textViewChatMessageUhrzeit);
                    viewHolderPartnerAudio.circleBarVisualizer = (CircleBarVisualizer) convertView.findViewById(R.id.imageViewVisualAudio);
                    viewHolderPartnerAudio.textViewPlayTime = (FallTextView) convertView.findViewById(R.id.textViewPlayLength);
                    viewHolderPartnerAudio.imageViewMainContent = (ImageView) convertView.findViewById(R.id.imageViewChatMainPreview);
                    convertView.setTag(viewHolderPartnerAudio);
                }
                else
                {
                    viewHolderPartnerAudio = (ViewHolderPartnerAudio) convertView.getTag();
                }

                viewHolderPartnerAudio.textViewUhrzeitAndUsername.setText(TimeDifferenceHelperClass.getDateDiff(context.getResources(), chatAudioMessage.getMessageTime(), System.currentTimeMillis()));

                viewHolderPartnerAudio.circleBarVisualizer.setColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                viewHolderPartnerAudio.circleBarVisualizer.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        try
                        {
                            if(mediaPlayer == null) mediaPlayer = new MediaPlayer();

                            if(chatAudioMessage.getFileDescriptor() != null)
                            {
                                try
                                {
                                    if(isPlayerPrepared && PREPARED_AID.equals(chatAudioMessage.getAID()))
                                    {
                                        if(mediaPlayer.isPlaying())
                                        {
                                            mediaPlayer.pause();
                                            MEDIAPLAYER_CURRENT_POS = mediaPlayer.getCurrentPosition();
                                            countDownTimer.pause();
                                        }
                                        else
                                        {
                                            mediaPlayer.seekTo(MEDIAPLAYER_CURRENT_POS);
                                            mediaPlayer.start();
                                            countDownTimer.resume();
                                        }
                                    }
                                }
                                catch (Exception ec)
                                {
                                }

                                if(!isPlayerPrepared || !PREPARED_AID.equals(chatAudioMessage.getAID()))
                                {
                                    if(androidIsStupidAudioFinishListener != null)
                                    {
                                        androidIsStupidAudioFinishListener.onFinished();
                                    }

                                    mediaPlayer.stop();
                                    mediaPlayer.reset();

                                    mediaPlayer.setDataSource(chatAudioMessage.getFileDescriptor());
                                    viewHolderPartnerAudio.circleBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                                    mediaPlayer.prepare();
                                    isPlayerPrepared = true;
                                    PREPARED_AID = chatAudioMessage.getAID();

                                    if(countDownTimer != null)
                                    {
                                        countDownTimer.cancel();
                                    }

                                    androidIsStupidAudioFinishListener = new AndroidIsStupidAudioFinishListener()
                                    {
                                        @Override
                                        public void onFinished()
                                        {
                                            mediaPlayer.stop();
                                            viewHolderPartnerAudio.circleBarVisualizer.release();
                                            viewHolderPartnerAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(chatAudioMessage.getLengthMillis()));
                                            isPlayerPrepared = false;
                                        }
                                    };

                                    countDownTimer = new CountDownTimerWithPause(chatAudioMessage.getLengthMillis(), 1000,
                                            true)
                                    {
                                        @Override
                                        public void onTick(long millisUntilFinished)
                                        {
                                            viewHolderPartnerAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millisUntilFinished));
                                        }

                                        @Override
                                        public void onFinish()
                                        {
                                        }
                                    };

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            androidIsStupidAudioFinishListener.onFinished();
                                        }
                                    });

                                    mediaPlayer.start();
                                    countDownTimer.resume();
                                }
                            }
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "PrivateChatPlayAudio(OWNAUDIO) failed: " + ec);
                        }
                    }
                });

                EsaphAudioMessagePlayer.with(context).handleAudioMessage(chatAudioMessage, new EsaphAudioMessagePlayer.AudioHandlerCallBack()
                {
                    @Override
                    public void onAudioMessageAvailable(File file)
                    {
                        try
                        {
                            FileInputStream fis = new FileInputStream(file);
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(fis.getFD());
                            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            int millSecond = Integer.parseInt(durationStr);
                            viewHolderPartnerAudio.textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millSecond));
                            chatAudioMessage.setFileDescriptor(fis.getFD());
                            chatAudioMessage.setLengthMillis(millSecond);
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "onAudioMessageAvailable(OWNAUDIO) failed in adapter callback: " + ec);
                        }
                    }

                    @Override
                    public void onAudioMessageDownloadFailed()
                    {
                    }
                });

                EsaphGlobalImageLoader.with(context).canvasMode(CanvasRequest.builder(viewHolderPartnerAudio.imageViewMainContent, new EsaphDimension(
                        viewHolderPartnerAudio.imageViewMainContent.getWidth(),
                        viewHolderPartnerAudio.imageViewMainContent.getHeight()
                ), chatAudioMessage));

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderPartnerAudio.imageViewProfilbild,
                        null,
                        chatPartner.getUID_CHATPARTNER(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;
        }
        return convertView;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Object object = this.listDataDisplay.get(position);

        if(object instanceof AudioMessage)
        {
            return getView(position, convertView, parent, (AudioMessage) object);
        }
        else if(object instanceof ChatTextMessage)
        {
            return getView(position, convertView, parent, (ChatTextMessage) object);
        }
        else if(object instanceof ChatInfoStateMessage)
        {
            return getView(position, convertView, parent, (ChatInfoStateMessage) object);
        }
        else if(object instanceof EsaphStickerChatObject)
        {
            return getView(position, convertView, parent, (EsaphStickerChatObject) object);
        }
        else if(object instanceof EsaphAndroidSmileyChatObject)
        {
            return getView(position, convertView, parent, (EsaphAndroidSmileyChatObject) object);
        }
        else if(object instanceof ChatImage)
        {
            return getView(position, convertView, parent, (ChatImage) object);
        }
        else if(object instanceof ChatVideo)
        {
            return getView(position, convertView, parent, (ChatVideo) object);
        }
        else if(object instanceof DatumList)
        {
            return getView(position, convertView, parent, (DatumList) object);
        }
        else if(object instanceof UploadPost)
        {
            return getView(position, convertView, parent, (UploadPost) object);
        }

        return null;
    }

    private String getLastMessageTextInfo(String user, long time, int status, boolean ownMessage, String pSaved)
    {
        System.out.println("DEBUG: LAST STATUS = " + user + " -- " + status);
        StringBuilder stringBuilder = new StringBuilder();
        switch (status)
        {
            case ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE:
                stringBuilder.append(context.getResources().getString(R.string.txt_chat_status_own_message_failed_sending)); //Download failed.
                return stringBuilder.toString();

            case ConversationStatusHelper.STATUS_CHAT_OPENED:
                stringBuilder.append(context.getResources().getString(R.string.txt_chat_status_partner_opened));
                break;

            case ConversationStatusHelper.STATUS_NEW_MESSAGE:
                stringBuilder.append(context.getResources().getString(R.string.txt_TippeUmOpen));
                break;
        }
        return stringBuilder.toString();
    }

    private boolean shouldBePaddingDoubled(int pos, ConversationMessage conversationMessage)
    {
        if(pos+1 < listDataDisplay.size())
        {
            Object object = listDataDisplay.get(pos + 1);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessageNext = (ConversationMessage) object;
                if(conversationMessage.getABS_ID() != (conversationMessageNext.getABS_ID()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void setHandlePostStateStatusAsync(final TextView textViewPostStatus, final ConversationMessage conversationMessage)
    {
        EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback()
                {
                    @Override
                    public void onExecute(boolean hasSaved)
                    {
                        final boolean OWN_RESULT_SAVED = hasSaved;

                        EsaphContainsUtils.hasSaved(context, new EsaphContainsUtils.SaverCodeExecutionCallback()
                                {
                                    @Override
                                    public void onExecute(boolean hasSaved)
                                    {
                                        if(textViewPostStatus == null) return;

                                        StringBuilder stringBuilder = new StringBuilder();

                                        if(conversationMessage.getABS_ID() != LOGGED_UID) //Ich bin empfänger.
                                        {
                                            if(OWN_RESULT_SAVED)
                                            {
                                                stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_Gespeichert)); //Gespeichert
                                            }
                                            else
                                            {
                                                stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_Gesehen)); //Nicht gespeichert
                                            }
                                        }
                                        else
                                        {
                                            if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_SENT)
                                            {
                                                if(OWN_RESULT_SAVED)
                                                {
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_Gespeichert));
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_POINT));
                                                }

                                                stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_Hochgeladen)); //Partner hat noch nicht gesehen.
                                            }
                                            else if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_CHAT_OPENED)
                                            {
                                                if(OWN_RESULT_SAVED)
                                                {
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_Gespeichert)); //Ich gespeichert
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_POINT));
                                                }

                                                if(OWN_RESULT_SAVED)
                                                {
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_PartnerGespeichert, chatPartner.getPartnerUsername())); //Gespeichert
                                                }
                                                else
                                                {
                                                    stringBuilder.append(context.getResources().getString(R.string.txt_chat_SHORT_PartnerGesehen, chatPartner.getPartnerUsername())); //Geöffnet, der partner
                                                }
                                            }
                                        }

                                        if(textViewPostStatus == null) return;

                                        textViewPostStatus.setText(stringBuilder.toString());
                                    }
                                },
                                conversationMessage.getMESSAGE_ID(),
                                chatPartner.getUID_CHATPARTNER());
                    }
                },
                conversationMessage.getMESSAGE_ID(),
                LOGGED_UID);
    }

    private int findPositionFromImageOrVideo(int posClicked, String PID) //Starting from posClicked because it can only be above.
    {
        int lastSize = getCount();

        for(int counter = posClicked; counter >= 0; counter--)
        {
            Object object = this.listDataDisplay.get(counter);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessageIntern = (ConversationMessage) object;
                if(conversationMessageIntern.getType() != (CMTypes.FTEX)
                        && conversationMessageIntern.getType() != (CMTypes.FAUD)
                        && conversationMessageIntern.getType() != (CMTypes.FEMO)
                        && conversationMessageIntern.getType() != (CMTypes.FSTI)
                        && conversationMessageIntern.getIMAGE_ID().equals(PID))
                {
                    return counter;
                }
            }
        }

        privateChat.loadMoreDataChat(getCount());
        try
        {
            synchronized (listWaitNotifyObject)
            {
                canNotifyWait = true;
                listWaitNotifyObject.wait(); //waiting until list was filled with new data.
                if(lastSize == getCount()) return -1; //IF no new data loaded, return to prevent infinite loop.

                findPositionFromImageOrVideo(posClicked, PID);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean canNotifyWait = false;
    private final Object listWaitNotifyObject = new Object();

    public Object getListWaitNotifyObject()
    {
        return listWaitNotifyObject;
    }

    public interface AndroidIsStupidAudioFinishListener
    {
        void onFinished();
    }


    private String PREPARED_AID;
    private boolean isPlayerPrepared = false;
    private int MEDIAPLAYER_CURRENT_POS;
    private CountDownTimerWithPause countDownTimer;
    private AndroidIsStupidAudioFinishListener androidIsStupidAudioFinishListener;


    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private void handleImageOrVideoSaveState(final ConversationMessage conversationMessage, final CheckBox checkBox)
    {
        if(AsyncSaveOrUnsaveSinglePostFromPrivateUser.isPostInDeleteMode(conversationMessage, context))
        {
            final DialogAlertPostIsInDeleteMode dialogAlertPostIsInDeleteMode = new DialogAlertPostIsInDeleteMode(context,
                    null,
                    conversationMessage);

            final TextView textViewConfirm = dialogAlertPostIsInDeleteMode.findViewById(R.id.textViewUnsaveSaveDialogStartAction);
            textViewConfirm.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(context != null)
                    {
                        Glide.with(context).load(R.drawable.background_rounded_loading_grey).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    textViewConfirm.setBackground(resource);
                                }
                            }
                        });
                    }

                    startUnsaveOrSavePost(checkBox, conversationMessage);
                }
            });
            dialogAlertPostIsInDeleteMode.show();
        }
        else
        {
            startUnsaveOrSavePost(checkBox, conversationMessage);
        }
    }

    protected void startUnsaveOrSavePost(final CheckBox checkBox, ConversationMessage conversationMessage)
    {
        final boolean lastState = !checkBox.isChecked();

        executorService.submit(new AsyncSaveOrUnsaveSinglePostFromPrivateUser(
                context,
                new AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener()
                {
                    @Override
                    public void onAddedToGallery(ConversationMessage conversationMessage)
                    {
                        checkBox.setChecked(true);
                        checkBox.jumpDrawablesToCurrentState();
                    }

                    @Override
                    public void onRemovedFromGallery(ConversationMessage conversationMessage)
                    {
                        checkBox.setChecked(false);
                        checkBox.jumpDrawablesToCurrentState();
                    }

                    @Override
                    public void onPostDied(ConversationMessage conversationMessage)
                    {
                        removeItemById(conversationMessage.getMESSAGE_ID());
                    }

                    @Override
                    public void onFailed(ConversationMessage conversationMessage)
                    {
                        checkBox.setChecked(lastState);
                        checkBox.jumpDrawablesToCurrentState();
                    }
                },
                conversationMessage));
    }


    public void onResume()
    {
        if(mediaPlayer != null)
        {

        }
    }

    public void onPause()
    {
        if(mediaPlayer != null && isPlayerPrepared)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
            }
        }
    }

    public void onStop()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlayerPrepared = false;
        }
    }
}
