/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder;

import android.widget.ProgressBar;

import java.util.concurrent.locks.ReentrantLock;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.VideoViewAware;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;

public class VideoRequest extends BaseRequest
{
    public final String OBJECT_ID;
    public final VideoViewAware videoViewAware;
    public boolean blurry;

    private VideoRequest(ReentrantLock reentrantLock,
                        String PID,
                         VideoViewAware videoViewAware,
                        EsaphGlobalDownloadListener esaphGlobalDownloadListener,
                        ProgressBar progressBar)
    {
        super(reentrantLock,
                videoViewAware,
                StorageHandler.FOLDER__SPOTLIGHT,
                PID,
                StorageHandler.VIDEO_PREFIX,
                progressBar,
                esaphGlobalDownloadListener);

        this.OBJECT_ID = PID;
        this.videoViewAware = videoViewAware;
        this.blurry = false;
    }


    public static VideoRequestBuilder builder(String PID,
                                              ProgressBar progressBar,
                                              EsaphTextureVideoView esaphTextureVideoView)
    {
        return new VideoRequestBuilder(PID,
                progressBar,
                new VideoViewAware(esaphTextureVideoView));
    }


    public static class VideoRequestBuilder
    {
        private final String OBJECT_ID;
        private final VideoViewAware videoViewAware;
        private boolean blurry;
        private final ProgressBar progressBar;
        private EsaphGlobalDownloadListener esaphGlobalDownloadListener;

        public VideoRequestBuilder(String PID,
                                  ProgressBar progressBar,
                                   VideoViewAware videoViewAware)
        {
            this.OBJECT_ID = PID;
            this.progressBar = progressBar;
            this.videoViewAware = videoViewAware;
            this.blurry = false;
        }

        public VideoRequestBuilder setEsaphGlobalDownloadListener(EsaphGlobalDownloadListener esaphGlobalDownloadListener) {
            this.esaphGlobalDownloadListener = esaphGlobalDownloadListener;
            return this;
        }


        public VideoRequestBuilder setBlurry(boolean blurry) {
            this.blurry = blurry;
            return this;
        }

        public String getOBJECT_ID() {
            return OBJECT_ID;
        }

        public VideoRequest build(ReentrantLock reentrantLock)
        {
            return new VideoRequest(reentrantLock,
                    OBJECT_ID,
                    videoViewAware,
                    esaphGlobalDownloadListener,
                    progressBar);
        }
    }


}