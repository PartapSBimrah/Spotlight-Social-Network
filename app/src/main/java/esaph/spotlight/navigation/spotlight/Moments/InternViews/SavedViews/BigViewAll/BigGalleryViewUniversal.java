/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncDeletePrivateMomentPost;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightBigViewMasterClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogDeletePictureOrVideo;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class BigGalleryViewUniversal extends SpotLightBigViewMasterClass
{
    @Override
    public Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, ViewPagerAdapterGetList viewPagerAdapterGetList)
    {
        if(root instanceof ILoader)
        {
            return ((ILoader)root).getLoadingTask();
        }

        return new Runnable()
        {
            @Override
            public void run()
            {
            }
        };
    }

    @Override
    public List<Object> extendedGetList()
    {
        return BigGalleryViewUniversal.esaphMomentsRecylerView.getListDataDisplay();
    }

    @Override
    public int extendedGetPositionClicked()
    {
        return BigGalleryViewUniversal.positionClicked;
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

        TextView textViewConfirm = (TextView) dialogDeletePictureOrVideo.findViewById(R.id.textViewDeletePicDialogReally);
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
                                if(isAdded())
                                {
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

    @Override
    public ViewPagerDataSetChangedListener extendedGetListener()
    {
        return esaphMomentsRecylerView;
    }

    private static int positionClicked = 0;
    private static EsaphGlobalCommunicationFragment root;
    private static EsaphMomentsRecylerView esaphMomentsRecylerView;



    public static BigGalleryViewUniversal getInstance(EsaphGlobalCommunicationFragment root,
                                                      EsaphMomentsRecylerView esaphMomentsRecylerView,
                                                      int currentPosition)
    {
        BigGalleryViewUniversal.root = root;
        BigGalleryViewUniversal.esaphMomentsRecylerView = esaphMomentsRecylerView;
        BigGalleryViewUniversal.positionClicked = currentPosition;

        Bundle bundle = new Bundle();
        bundle.putString(BigGalleryViewUniversal.TRANS_NAME_BIG_GALLERY_UNIVERSAL, "");
        BigGalleryViewUniversal bigGalleryViewUniversal = new BigGalleryViewUniversal();
        bigGalleryViewUniversal.setArguments(bundle);
        return bigGalleryViewUniversal;
    }
}
