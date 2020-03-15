/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudBigView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphDialogBubbly.EsaphDialog;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncDeletePostFromLifeCloud;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.LifeCloudBigViewMaster.LifeCloudBigViewMasterClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogDeletePictureOrVideo;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudBigView.Background.RunnableLoadMoreLifeCloudTodayPosts;

public class LifeCloudTodayBigView extends LifeCloudBigViewMasterClass
{
    public LifeCloudTodayBigView()
    {
        // Required empty public constructor
    }

    private Vibrator v = null;
    private void vibrateDeviceTouch()
    {
        Activity activity = getActivity();
        if(v == null && activity != null)
            v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if(v != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                v.vibrate(VibrationEffect.createOneShot(25, 1));
            }
            else {
                //deprecated in API 26
                v.vibrate(25);
            }
        }
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


            textViewDeletePost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    Object object = getLifeCloudBigViewAdapter().getItemFromList(getViewPager().getCurrentItem());
                    if(object instanceof LifeCloudUpload)
                    {
                        Activity activity = getActivity();
                        if(activity != null && getView() != null)
                        {
                            final LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                            final DialogDeletePictureOrVideo dialogDeletePictureOrVideo = new DialogDeletePictureOrVideo(getActivity(), (ViewGroup) getView().getRootView(), lifeCloudUpload);
                            TextView textViewConfirm = (TextView) dialogDeletePictureOrVideo.findViewById(R.id.textViewDeletePicDialogReally);
                            textViewConfirm.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(final View v)
                                {
                                    new AsyncDeletePostFromLifeCloud(getContext(), new AsyncDeletePostFromLifeCloud.LifeCloudDeleteListener()
                                    {
                                        @Override
                                        public void onDeletedPost(LifeCloudUpload lifeCloudUpload)
                                        {
                                            if(isAdded())
                                            {
                                                getLifeCloudBigViewAdapter().removePostByPid(lifeCloudUpload.getCLOUD_PID());
                                            }
                                        }

                                        @Override
                                        public void onFailedDeletingPost(LifeCloudUpload lifeCloudUpload)
                                        {
                                            EsaphDialog esaphDialog = new EsaphDialog(getContext(), getResources().getString(R.string.txt_ups),
                                                    getResources().getString(R.string.txt_alertMomentFailedToDeleteTitleDetails));
                                            esaphDialog.show();
                                        }
                                    }, lifeCloudUpload).execute();

                                    dialogDeletePictureOrVideo.dismiss();
                                }
                            });
                            dialogDeletePictureOrVideo.show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public List<Object> extendedGetList()
    {
        return new ArrayList<>();
    }

    @Override
    public int extendedGetPositionClicked()
    {
        return 0;
    }

    public static LifeCloudTodayBigView getInstance()
    {
        return new LifeCloudTodayBigView();
    }

    @Override
    public Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, ViewPagerAdapterGetList viewPagerAdapterGetList, ViewPager.OnPageChangeListener onPageChangeListener)
    {
        return new RunnableLoadMoreLifeCloudTodayPosts(getActivity(),
                viewPagerAdapterGetList,
                viewPager,
                onPageChangeListener);
    }
}
