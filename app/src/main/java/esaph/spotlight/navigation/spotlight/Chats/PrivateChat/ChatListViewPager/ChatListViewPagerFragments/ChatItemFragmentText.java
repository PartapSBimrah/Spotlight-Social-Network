package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.PrivateChatViewPagerAdapter;

public class ChatItemFragmentText extends EsaphGlobalCommunicationFragment {
    private ConversationMessage conversationMessage;

    private ImageView imageView;
    private TextView textViewAbsender;
    private TextView textViewUhrzeit;
    private ImageView imageViewProfilbild;
    private ProgressBar progressBar;
    private ImageView imageViewClose;

    public ChatItemFragmentText() {
        // Required empty public constructor
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageViewClose = null;
        imageView = null;
        textViewAbsender = null;
        textViewUhrzeit = null;
        progressBar = null;
        imageViewProfilbild = null;
    }

    public static ChatItemFragmentText getInstance(ConversationMessage conversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE, conversationMessage);
        ChatItemFragmentText chatItemFragmentText = new ChatItemFragmentText();
        chatItemFragmentText.setArguments(bundle);
        return chatItemFragmentText;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            conversationMessage = (ConversationMessage) bundle.getSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chat_item_fragment_text, container, false);
        imageViewClose = (ImageView) rootView.findViewById(R.id.imageViewCloseMasterClassBigViewViewpager);
        imageView = rootView.findViewById(R.id.imageViewChatMainPreview);
        textViewAbsender = rootView.findViewById(R.id.textViewAbsenderName);
        textViewUhrzeit = rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        progressBar = rootView.findViewById(R.id.progressBarSmallViewLoading);
        imageViewProfilbild = rootView.findViewById(R.id.imageViewProfilbild);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent();
                }
            }
        });

        textViewAbsender.setText(conversationMessage.getAbsender());
        textViewUhrzeit.setText(ChatListViewPagerHelperClass.formatTime(conversationMessage.getMessageTime()));

        EsaphGlobalImageLoader.with(getContext()).canvasMode(CanvasRequest.builder(imageView, new EsaphDimension(
                imageView.getWidth(),
                imageView.getHeight()
        ), conversationMessage));

        try
        {
            EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(imageViewProfilbild,
                    null,
                    conversationMessage.getABS_ID(),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_no_round,
                    StorageHandlerProfilbild.FOLDER_PROFILBILD);
        }
        catch (Exception ec)
        {
        }

        conversationMessage.setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = new SQLChats(getContext());
                sqlChats.updateStatusByID(conversationMessage);
                sqlChats.close();
            }
        }).start();
    }

    @Override
    public boolean onActivityDispatchedBackPressed() {
        return false;
    }


}
