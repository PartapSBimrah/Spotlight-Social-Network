/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chibde.visualizer.CircleBarVisualizer;
import com.hanks.htextview.fall.FallTextView;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ArrayAdapterPrivateChat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.AudioMessagePlayer.EsaphAudioMessagePlayer;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.PrivateChatViewPagerAdapter;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.CountDownTimerWithPause;

public class ChatItemFragmentAudio extends EsaphGlobalCommunicationFragment
{
    private static SimpleDateFormat simpleDateFormatAudioMillis = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private AudioMessage audioMessage;

    private CircleBarVisualizer circleBarVisualizer;
    private FallTextView textViewPlayTime;
    private ImageView imageView;
    private ImageView imageViewProfilbild;
    private TextView textViewAbsender;
    private TextView textViewUhrzeit;
    private ProgressBar progressBar;
    private ImageView imageViewClose;

    public ChatItemFragmentAudio() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageViewClose = null;
        imageView = null;
        textViewAbsender = null;
        textViewUhrzeit = null;
        imageViewProfilbild = null;
        progressBar = null;
        textViewPlayTime = null;
        circleBarVisualizer = null;
    }

    public static ChatItemFragmentAudio getInstance(ConversationMessage conversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE, conversationMessage);
        ChatItemFragmentAudio chatItemFragmentAudio = new ChatItemFragmentAudio();
        chatItemFragmentAudio.setArguments(bundle);
        return chatItemFragmentAudio;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            audioMessage = (AudioMessage) bundle.getSerializable(PrivateChatViewPagerAdapter.KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chat_item_fragment_audio, container, false);
        imageViewClose = (ImageView) rootView.findViewById(R.id.imageViewCloseMasterClassBigViewViewpager);
        imageView = rootView.findViewById(R.id.imageViewChatMainPreview);
        textViewAbsender = rootView.findViewById(R.id.textViewAbsenderName);
        textViewUhrzeit = rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        progressBar = rootView.findViewById(R.id.progressBarSmallViewLoading);
        circleBarVisualizer = rootView.findViewById(R.id.imageViewVisualAudio);
        textViewPlayTime = rootView.findViewById(R.id.textViewPlayLength);
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

        circleBarVisualizer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startPlaying();
            }
        });

        EsaphAudioMessagePlayer.with(getContext()).handleAudioMessage(audioMessage, new EsaphAudioMessagePlayer.AudioHandlerCallBack()
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
                    textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millSecond));
                    audioMessage.setFileDescriptor(fis.getFD());
                    audioMessage.setLengthMillis(millSecond);
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

        textViewAbsender.setText(audioMessage.getAbsender());
        textViewUhrzeit.setText(ChatListViewPagerHelperClass.formatTime(audioMessage.getMessageTime()));

        EsaphGlobalImageLoader.with(getContext()).canvasMode(CanvasRequest.builder(imageView, new EsaphDimension(
                imageView.getWidth(),
                imageView.getHeight()
        ), audioMessage));

        try
        {
            EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(imageViewProfilbild,
                    null,
                    audioMessage.getABS_ID(),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_no_round,
                    StorageHandlerProfilbild.FOLDER_PROFILBILD);
        }
        catch (Exception ec)
        {
        }

        audioMessage.setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = new SQLChats(getContext());
                sqlChats.updateStatusByID(audioMessage);
                sqlChats.close();
            }
        }).start();
    }


    private MediaPlayer mediaPlayer;
    private boolean isPlayerPrepared = false;
    private int MEDIAPLAYER_CURRENT_POS;
    private ArrayAdapterPrivateChat.AndroidIsStupidAudioFinishListener androidIsStupidAudioFinishListener;
    private CountDownTimerWithPause countDownTimer;

    private void startPlaying()
    {
        try
        {
            if(mediaPlayer == null)
                mediaPlayer = new MediaPlayer();


            try
            {
                if(isPlayerPrepared)
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

            if(!isPlayerPrepared)
            {
                if(androidIsStupidAudioFinishListener != null)
                {
                    androidIsStupidAudioFinishListener.onFinished();
                }

                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setDataSource(audioMessage.getFileDescriptor());
                circleBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                mediaPlayer.prepare();
                isPlayerPrepared = true;

                if(countDownTimer != null)
                {
                    countDownTimer.cancel();
                }

                androidIsStupidAudioFinishListener = new ArrayAdapterPrivateChat.AndroidIsStupidAudioFinishListener()
                {
                    @Override
                    public void onFinished()
                    {
                        mediaPlayer.stop();
                        circleBarVisualizer.release();
                        textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(audioMessage.getLengthMillis()));
                        isPlayerPrepared = false;
                    }
                };

                countDownTimer = new CountDownTimerWithPause(audioMessage.getLengthMillis(), 1000,
                        true)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        textViewPlayTime.animateText(simpleDateFormatAudioMillis.format(millisUntilFinished));
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
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "PloppAudioBuilder(start pauseAndSeekToZero audio) failed: " + ec);
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed() {
        return false;
    }


}
