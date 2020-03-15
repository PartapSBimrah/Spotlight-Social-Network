/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncSetPostSeen;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.PrivateChatViewPagerAdapter;

public class ChatItemFragmentVideo extends Fragment
{
    private ConversationMessage conversationMessage;

    private boolean invokedIsVisible = true;
    private EsaphTextureVideoView esaphTextureVideoView;
    private TextView textViewAbsender;
    private TextView textViewUhrzeit;
    private ProgressBar progressBar;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;

    public ChatItemFragmentVideo() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        this.invokedIsVisible = isVisibleToUser;
        if(isVisibleToUser)
        {
            virtualCallResumeMediaPlayer();
        }
        else
        {
            virtualCallPauseMediaPlayer();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        virtualCallPauseMediaPlayer();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(invokedIsVisible)
        {
            virtualCallResumeMediaPlayer();
        }
    }

    private void virtualCallPauseMediaPlayer()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pause();
            esaphTextureVideoView.setTag(null);
        }
    }


    private void virtualCallResumeMediaPlayer()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.play();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pauseAndSeekToZero();
        }
        esaphTextureVideoView = null;
        textViewAbsender = null;
        textViewUhrzeit = null;
        progressBar = null;
        textViewTryAgain = null;
        textViewFailInfo = null;
    }

    public static ChatItemFragmentVideo getInstance(ConversationMessage conversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE, conversationMessage);
        ChatItemFragmentVideo chatItemFragmentVideo = new ChatItemFragmentVideo();
        chatItemFragmentVideo.setArguments(bundle);
        return chatItemFragmentVideo;
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
        View rootView = inflater.inflate(R.layout.fragment_chat_item_fragment_video, container, false);
        esaphTextureVideoView = (EsaphTextureVideoView) rootView.findViewById(R.id.imageViewMainVideoView);
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
        EsaphGlobalImageLoader.with(getContext()).displayVideo(VideoRequest.builder(
                conversationMessage.getIMAGE_ID(),
                progressBar,
                esaphTextureVideoView).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener() {
            @Override
            public void onAvaiableImage(String PID)
            {
            }

            @Override
            public void onAvaiableVideo(File file)
            {
                removeFailed();
                prepareVideo(file);
            }

            @Override
            public void onFailed(String PID)
            {
                try
                {
                    setFailed();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "maybe referce lost videofragment private user: " + ec);
                }
            }
        }));
    }

    private void prepareVideo(File mainFileOfVideoCache)
    {
        try
        {
            esaphTextureVideoView.setScaleType(EsaphTextureVideoView.ScaleType.CENTER_CROP);
            FileInputStream fis = new FileInputStream(mainFileOfVideoCache);
            esaphTextureVideoView.setDataSource(fis.getFD());
            esaphTextureVideoView.setLooping(true);

            if(invokedIsVisible)
            {
                System.out.println("MEDIADE prepareVideo: PLAY CALLED FROM: " + this);
                esaphTextureVideoView.play();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "prepareVideo() failed: " + ec);
        }
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
