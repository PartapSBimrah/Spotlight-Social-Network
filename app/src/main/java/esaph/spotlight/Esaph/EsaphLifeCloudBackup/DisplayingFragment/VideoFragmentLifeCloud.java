/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphLifeCloudBackup.DisplayingFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;

public class VideoFragmentLifeCloud extends EsaphGlobalCommunicationFragment
{
    private static final String extra_Pid = "esaph.livetemp.moment.private.viewpager.endless.pid";
    private EsaphTextureVideoView esaphTextureVideoView;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;
    private ProgressBar progressBar;
    private boolean surfaceUseable = false;
    private boolean invokedIsVisible = true;
    private String postPID;

    public VideoFragmentLifeCloud()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pauseAndSeekToZero();
        }

        this.esaphTextureVideoView = null;
        this.textViewFailInfo = null;
        this.textViewTryAgain = null;
        this.progressBar = null;
    }


    public static VideoFragmentLifeCloud getInstance(String PID)
    {
        VideoFragmentLifeCloud fragment = new VideoFragmentLifeCloud();
        Bundle args = new Bundle();
        args.putString(extra_Pid, PID);
        fragment.setArguments(args);
        return fragment;
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
        View rootView =  inflater.inflate(R.layout.fragment_video_fragment_lifecloud, container, false);

        esaphTextureVideoView = (EsaphTextureVideoView) rootView.findViewById(R.id.imageViewMainVideoView);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBigViewDownloading);

        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                removeFailed();
                tryAgainDownloadImage();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null)
        {
            postPID = getArguments().getString(VideoFragmentLifeCloud.extra_Pid);
            tryAgainDownloadImage();
        }
    }

    public void tryAgainDownloadImage()
    {
        EsaphGlobalImageLoader.with(getContext()).displayVideo(VideoRequest.builder(
                postPID,
                progressBar,
                esaphTextureVideoView).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener() {
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


}
