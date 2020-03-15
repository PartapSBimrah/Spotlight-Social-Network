/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.DisplayingFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphContainsUtils;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveSinglePostFromPrivateUser;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightViewPagerAdapter;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogAlertPostIsInDeleteMode;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class VideoFragmentPrivateUser extends EsaphGlobalCommunicationFragment
{
    private long LOGGED_UID;
    private static final String extra_PID_post = "esaph.livetemp.moment.private.viewpager.endless.key_POST";
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;
    private ProgressBar progressBar;
    private boolean invokedIsVisible = true;
    private ChatVideo chatVideo;
    private EsaphTextureVideoView esaphTextureVideoView;
    private SpotLightViewPagerAdapter spotLightViewPagerAdapter;
    private ImageView imageViewClose;
    private ImageView imageViewProfilbild;

    private CheckBox checkBoxAddToGallery;

    private TextView textViewCurrentPosterUsername;
    private TextView textViewCurrentPostTimeAgo;

    private TextView textViewBeschreibung;
    private TextView textViewHashtags;


    public VideoFragmentPrivateUser()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(this.esaphTextureVideoView != null)
        {
            this.esaphTextureVideoView.pauseAndSeekToZero();
        }
        imageViewClose = null;
        this.esaphTextureVideoView = null;
        this.textViewFailInfo = null;
        this.textViewTryAgain = null;
        this.progressBar = null;
        this.textViewHashtags = null;
        this.textViewBeschreibung = null;
        this.textViewCurrentPosterUsername = null;
        this.textViewCurrentPostTimeAgo = null;
        this.checkBoxAddToGallery = null;
    }


    public static VideoFragmentPrivateUser getInstance(ChatVideo chatVideo)
    {
        VideoFragmentPrivateUser fragment = new VideoFragmentPrivateUser();
        Bundle args = new Bundle();
        args.putSerializable(extra_PID_post, chatVideo);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoFragmentPrivateUser setSpotLightViewPagerAdapter(SpotLightViewPagerAdapter spotLightViewPagerAdapter) {
        this.spotLightViewPagerAdapter = spotLightViewPagerAdapter;
        return this;
    }

    private void virtualCallPauseMediaPlayer()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pause();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView =  inflater.inflate(R.layout.fragment_video_fragment_private_user, container, false);

        imageViewProfilbild = (ImageView) rootView.findViewById(R.id.imageViewProfilbild);
        imageViewClose = (ImageView) rootView.findViewById(R.id.imageViewCloseMasterClassBigViewViewpager);
        esaphTextureVideoView = (EsaphTextureVideoView) rootView.findViewById(R.id.imageViewMainVideoView);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBigViewDownloading);

        textViewCurrentPosterUsername = (TextView) rootView.findViewById(R.id.textViewAbsenderName);
        textViewCurrentPostTimeAgo = (TextView) rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        textViewBeschreibung = (TextView) rootView.findViewById(R.id.textViewBeschreibung);
        textViewHashtags = (TextView) rootView.findViewById(R.id.textViewHashtagsOverImage);
        checkBoxAddToGallery = rootView.findViewById(R.id.imageViewAddToGallery);

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

        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                removeFailed();
                runDisplayVideo();
            }
        });

        checkBoxAddToGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkBoxAddToGallery.setClickable(false);

                final boolean isInDeleteMode = AsyncSaveOrUnsaveSinglePostFromPrivateUser.isPostInDeleteMode(chatVideo, getContext());

                if(!isInDeleteMode)
                {
                    new Thread(new AsyncSaveOrUnsaveSinglePostFromPrivateUser(
                            getContext(),
                            new AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener() {
                                @Override
                                public void onAddedToGallery(ConversationMessage conversationMessage) {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                    }
                                }

                                @Override
                                public void onRemovedFromGallery(ConversationMessage conversationMessage) {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                    }
                                }

                                @Override
                                public void onPostDied(ConversationMessage conversationMessage)
                                {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        if(postStateListenerPrivateChat != null)
                                        {
                                            postStateListenerPrivateChat.onPostDied(conversationMessage);
                                        }

                                        spotLightViewPagerAdapter.removePostByPid(conversationMessage.getMESSAGE_ID());
                                        if(spotLightViewPagerAdapter.getObjectsCount()[0] <= 0)
                                        {
                                            EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                            if(esaphActivity != null)
                                            {
                                                esaphActivity.onActivityDispatchBackPressEvent();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(ConversationMessage conversationMessage)
                                {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                    }
                                }
                            },
                            chatVideo)).start();
                }
                else
                {
                    checkBoxAddToGallery.setChecked(true);
                    checkBoxAddToGallery.jumpDrawablesToCurrentState();
                    Activity activity = getActivity();
                    if(activity != null)
                    {
                        final DialogAlertPostIsInDeleteMode dialogAlertPostIsInDeleteMode = new DialogAlertPostIsInDeleteMode(activity,
                                (ViewGroup) getView().getRootView(),
                                chatVideo);

                        final TextView textViewConfirm = dialogAlertPostIsInDeleteMode.findViewById(R.id.textViewUnsaveSaveDialogStartAction);
                        textViewConfirm.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Context context = getContext();
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

                                new Thread(new AsyncSaveOrUnsaveSinglePostFromPrivateUser(
                                        getContext(),
                                        new AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener() {
                                            @Override
                                            public void onAddedToGallery(ConversationMessage conversationMessage) {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                                }
                                            }

                                            @Override
                                            public void onRemovedFromGallery(ConversationMessage conversationMessage)
                                            {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                                }
                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }

                                            @Override
                                            public void onPostDied(ConversationMessage conversationMessage)
                                            {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    if(postStateListenerPrivateChat != null)
                                                    {
                                                        postStateListenerPrivateChat.onPostDied(conversationMessage);
                                                    }

                                                    spotLightViewPagerAdapter.removePostByPid(conversationMessage.getMESSAGE_ID());
                                                    if(spotLightViewPagerAdapter.getObjectsCount()[0] <= 0)
                                                    {
                                                        EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                                        if(esaphActivity != null)
                                                        {
                                                            esaphActivity.onActivityDispatchBackPressEvent();
                                                        }
                                                    }
                                                }

                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }

                                            @Override
                                            public void onFailed(ConversationMessage conversationMessage) {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                }
                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }
                                        },
                                        chatVideo)).start();
                            }
                        });
                        dialogAlertPostIsInDeleteMode.show();
                    }
                }
            }
        });

        textViewCurrentPosterUsername.setText(chatVideo.getAbsender());

        if(chatVideo.getBeschreibung() != null && !chatVideo.getBeschreibung().isEmpty())
        {
            textViewBeschreibung.setVisibility(View.VISIBLE);
            textViewBeschreibung.setText(chatVideo.getBeschreibung());
        }
        else
        {
            textViewBeschreibung.setVisibility(View.GONE);
        }

        /*
        if(chatVideo.hasHashtag())
        {
            textViewHashtags.setText(chatVideo.getAllHashtagsTogether());
        }
        else
        {
            textViewHashtags.setText("");
        }*/

        textViewCurrentPostTimeAgo.setText(TimeDifferenceHelperClass.getDateDiff(getResources(),
                chatVideo.getMessageTime(),
                System.currentTimeMillis()));

        EsaphContainsUtils.hasSaved(getContext(), new EsaphContainsUtils.SaverCodeExecutionCallback() {
                    @Override
                    public void onExecute(boolean hasSaved)
                    {
                        if(checkBoxAddToGallery == null) return;

                        if(hasSaved)
                        {
                            checkBoxAddToGallery.setChecked(true);
                            checkBoxAddToGallery.jumpDrawablesToCurrentState();
                        }
                        else
                        {
                            checkBoxAddToGallery.setChecked(false);
                            checkBoxAddToGallery.jumpDrawablesToCurrentState();
                        }
                    }
                },
                chatVideo.getMESSAGE_ID(),
                LOGGED_UID);

        runDisplayVideo();

        EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(imageViewProfilbild,
                null,
                chatVideo.getABS_ID(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_no_round,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOGGED_UID = SpotLightLoginSessionHandler.getLoggedUID();
        if(getArguments() != null)
        {
            chatVideo = (ChatVideo) getArguments().getSerializable(VideoFragmentPrivateUser.extra_PID_post);
        }
    }

    private File fileCached = null;
    public void runDisplayVideo()
    {
        EsaphGlobalImageLoader.with(getContext()).displayVideo(VideoRequest.builder(
                chatVideo.getIMAGE_ID(),
                progressBar,
                esaphTextureVideoView).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener()
        {
            @Override
            public void onAvaiableImage(String PID) {
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

    private void prepareVideo(File mainFileOfVideoCache)
    {
        try
        {
            fileCached = mainFileOfVideoCache;
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

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



    private AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener postStateListenerPrivateChat;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener)
        {
            postStateListenerPrivateChat = (AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener) context;
        }
    }
}
