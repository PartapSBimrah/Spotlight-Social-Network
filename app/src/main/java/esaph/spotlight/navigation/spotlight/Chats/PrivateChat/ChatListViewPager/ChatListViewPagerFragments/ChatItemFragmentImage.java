/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

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
import androidx.fragment.app.Fragment;

import java.io.File;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncSetPostSeen;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.PrivateChatViewPagerAdapter;

public class ChatItemFragmentImage extends Fragment {
    private ConversationMessage conversationMessage;

    private ImageView imageView;
    private TextView textViewAbsender;
    private TextView textViewUhrzeit;
    private ProgressBar progressBar;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;

    public ChatItemFragmentImage() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageView = null;
        textViewAbsender = null;
        textViewUhrzeit = null;
        progressBar = null;
        textViewFailInfo = null;
        textViewTryAgain = null;
    }

    public static ChatItemFragmentImage getInstance(ConversationMessage conversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE, conversationMessage);
        ChatItemFragmentImage chatItemFragmentImage = new ChatItemFragmentImage();
        chatItemFragmentImage.setArguments(bundle);
        return chatItemFragmentImage;
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
        View rootView = inflater.inflate(R.layout.fragment_chat_item_fragment_image, container, false);
        imageView = rootView.findViewById(R.id.imageViewMainPictureFragment);
        textViewAbsender = rootView.findViewById(R.id.textViewAbsenderName);
        textViewUhrzeit = rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        progressBar = rootView.findViewById(R.id.progressBarSmallViewLoading);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        textViewAbsender.setText(conversationMessage.getAbsender());
        textViewUhrzeit.setText(ChatListViewPagerHelperClass.formatTime(conversationMessage.getMessageTime()));
        tryAgainDownloadImage();

        if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_NEW_MESSAGE)
        {
            SQLChats sqlChats = new SQLChats(getContext());
            PostSeenUntransmitted postSeenUntransmitted = sqlChats.insertISeenNewPost(conversationMessage);
            conversationMessage.setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);
            sqlChats.updateStatusByID(conversationMessage);
            sqlChats.close();
            new AsyncSetPostSeen(getContext(),
                    postSeenUntransmitted).execute();
        }

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

    public void tryAgainDownloadImage()
    {
        EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                conversationMessage.getIMAGE_ID(),
                imageView,
                progressBar,
                new EsaphDimension(imageView.getWidth(), imageView.getHeight()),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                -1).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener() {
            @Override
            public void onAvaiableImage(String PID) {
                removeFailed();
            }

            @Override
            public void onAvaiableVideo(File file) {
                removeFailed();
            }

            @Override
            public void onFailed(String PID) {
                setFailed();
            }
        }));
    }

    private void setFailed()
    {
        if(textViewTryAgain != null && textViewFailInfo != null)
        {
            textViewTryAgain.setVisibility(View.VISIBLE);
            textViewTryAgain.setClickable(true);
            textViewFailInfo.setVisibility(View.VISIBLE);
        }
    }

    private void removeFailed()
    {
        if(textViewTryAgain != null && textViewFailInfo != null)
        {
            textViewTryAgain.setVisibility(View.INVISIBLE);
            textViewTryAgain.setClickable(false);
            textViewFailInfo.setVisibility(View.INVISIBLE);
        }
    }
}
