/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncDeletePrivateMomentPost;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveSinglePostFromPrivateUser;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightBigViewMasterClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogDeletePictureOrVideo;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class ChatSingleImageorVideoView extends SpotLightBigViewMasterClass
{
    public static final String extraInterfaceDeleteListener = "esaph.spotlight.interface.delete.listener";

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

    public ChatSingleImageorVideoView()
    {
    }

    private static List<Object> list;
    private static int posClicked = 0;
    public static ChatSingleImageorVideoView getInstance(List<Object> list,
                                                         AsyncDeletePrivateMomentPost.PostDeleteListener postDeleteListener,
                                                         long MESSAGE_ID_CLICKED)
    {
        ChatSingleImageorVideoView.list = list;
        ChatSingleImageorVideoView.updateCurrentList(list);
        ChatSingleImageorVideoView.posClicked = ChatSingleImageorVideoView.findIndexOfID(MESSAGE_ID_CLICKED);

        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatSingleImageorVideoView.extraInterfaceDeleteListener, postDeleteListener);

        ChatSingleImageorVideoView chatSingleImageorVideoView = new ChatSingleImageorVideoView();
        chatSingleImageorVideoView.setArguments(bundle);
        return chatSingleImageorVideoView;
    }

    public static void updateCurrentList(List<Object> list)
    {
        List<Object> newList = new ArrayList<>();
        for (int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if(conversationMessage.getType() != CMTypes.FSTI && conversationMessage.getType() != CMTypes.FINF)
                {
                    newList.add(object);
                }
            }
        }
        ChatSingleImageorVideoView.list = newList;
    }

    private static int findIndexOfID(long MESSAGE_ID_CLICKED)
    {
        for (int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if(conversationMessage.getMESSAGE_ID() == MESSAGE_ID_CLICKED)
                {
                    return counter;
                }
            }
        }

        return 0;
    }

    @Override
    public Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, ViewPagerAdapterGetList viewPagerAdapterGetList)
    {
        Activity activity = getActivity();
        if(activity instanceof ILoader)
        {
            return ((ILoader)activity).getLoadingTask();
        }

        return new Runnable()
        {
            @Override
            public void run()
            {
            }
        };
    }


    private AsyncDeletePrivateMomentPost.PostDeleteListener postDeleteListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            postDeleteListener = (AsyncDeletePrivateMomentPost.PostDeleteListener) bundle.getSerializable(ChatSingleImageorVideoView.extraInterfaceDeleteListener);
        }
    }

    @Override
    public List<Object> extendedGetList()
    {
        return ChatSingleImageorVideoView.list;
    }

    @Override
    public int extendedGetPositionClicked()
    {
        return ChatSingleImageorVideoView.posClicked;
    }

    @Override
    public ViewPagerDataSetChangedListener extendedGetListener() {
        return null;
    }

    @Override
    public void initShowView(View rootView)
    {
        if(rootView != null)
        {
            TextView textViewEditPost = (TextView) rootView.findViewById(R.id.textViewEditImage);
            TextView textViewSharePost = (TextView) rootView.findViewById(R.id.textViewSharePost);
            TextView textViewDeletePost = (TextView) rootView.findViewById(R.id.textViewDelete);

            textViewEditPost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });


            textViewSharePost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                }
            });


            textViewDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v)
                {
                    Object object = getSpotLightViewPagerAdapter().getItemFromList(getViewPager().getCurrentItem());

                    if(object instanceof ConversationMessage)
                    {
                        ConversationMessage conversationMessage = (ConversationMessage) object;
                        if(conversationMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID()) //MEIN BEITRAG, CURRENT USERNAME IST CHAT PARTNER.
                        {
                            deleteMyPost(conversationMessage);
                        }
                    }
                }
            });
        }
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private void deleteMyPost(final ConversationMessage conversationMessage)
    {
        final DialogDeletePictureOrVideo dialogDeletePictureOrVideo = new DialogDeletePictureOrVideo(
                getActivity(),
                (ViewGroup) getView().getRootView(),
                conversationMessage);

        final TextView textViewConfirm = (TextView) dialogDeletePictureOrVideo.findViewById(R.id.textViewDeletePicDialogReally);
        textViewConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                executorService.execute(new AsyncDeletePrivateMomentPost(getContext(),
                        new AsyncDeletePrivateMomentPost.PostDeleteListener() {
                            @Override
                            public void onDeletedSuccess(ConversationMessage conversationMessage)
                            {
                                if(postDeleteListener != null)
                                {
                                    postDeleteListener.onDeletedSuccess(conversationMessage);
                                }

                                if(isAdded())
                                {
                                    if(postStateListenerPrivateChat != null)
                                    {
                                        postStateListenerPrivateChat.onPostDied(conversationMessage);
                                    }
                                    getSpotLightViewPagerAdapter().removePostByPid(conversationMessage.getMESSAGE_ID());
                                }

                                if(getSpotLightViewPagerAdapter().getObjectsCount()[0] <= 0)
                                {
                                    EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                    if(esaphActivity != null)
                                    {
                                        esaphActivity.onActivityDispatchBackPressEvent();
                                    }
                                }
                            }

                            @Override
                            public void onFailedDelete(ConversationMessage conversationMessage)
                            {
                                if(postDeleteListener != null)
                                {
                                    postDeleteListener.onFailedDelete(conversationMessage);
                                }

                                Context context = getContext();
                                if(context != null)
                                {
                                    AlertDialog.Builder alter = new AlertDialog.Builder(getContext());
                                    alter.setTitle(getContext().getResources().getString(R.string.txt_delete));
                                    alter.setMessage(getContext().getResources().getString(R.string.txt_alertMomentFailedToDeleteTitleDetails));
                                    alter.show();
                                }
                            }
                        },
                        conversationMessage));

                dialogDeletePictureOrVideo.dismiss();
            }
        });
        dialogDeletePictureOrVideo.show();
    }
}
