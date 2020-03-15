/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphLifeCloudBackup.DisplayingFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class PictureFragmentLifeCloud extends EsaphGlobalCommunicationFragment
{
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String extra_Pid = "esaph.livetemp.moment.private.viewpager.endless.pid";
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;
    private String postPID;

    public PictureFragmentLifeCloud()
    {
        // Required empty public constructor
    }

    public static PictureFragmentLifeCloud getInstance(String PID)
    {
        Bundle bundle = new Bundle();
        bundle.putString(PictureFragmentLifeCloud.extra_Pid, PID);
        PictureFragmentLifeCloud fragobj = new PictureFragmentLifeCloud();
        fragobj.setArguments(bundle);
        return fragobj;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.imageView = null;
        this.textViewTryAgain = null;
        this.textViewFailInfo = null;
        this.progressBar = null;

        if(executorService != null)
        {
            executorService.shutdown();
        }
        this.executorService = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_picture_fragment_lifecloud, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageViewMainPictureFragment);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBigViewDownloading);

        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                tryAgainDownloadImage();
                removeFailed();
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
            postPID = getArguments().getString(PictureFragmentLifeCloud.extra_Pid);
            tryAgainDownloadImage();
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

    public void tryAgainDownloadImage()
    {
        if (!isAdded())
            return;

        EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                postPID,
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

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }
}
