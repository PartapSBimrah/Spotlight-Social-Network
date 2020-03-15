/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder;

import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.concurrent.locks.ReentrantLock;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageViewAware;
import esaph.spotlight.StorageManagment.StorageHandler;

public class ImageRequest extends BaseRequest
{
    public final ImageViewAware imageViewAware;
    public final int esaphImageLoaderDisplayingAnimation;
    public final boolean blurry;
    public final int placeHolderId;
    public boolean temp;

    private ImageRequest(ReentrantLock reentrantLock,
                        boolean temp,
                        ImageViewAware imageViewAware,
                        String PID,
                        ProgressBar progressBar,
                        EsaphDimension MAX_SIZE,
                        boolean blurry,
                        EsaphGlobalDownloadListener esaphGlobalDownloadListener,
                        int esaphImageLoaderDisplayingAnimation,
                         int placeHolderId)
    {
        super(reentrantLock,
                imageViewAware,
                StorageHandler.FOLDER__SPOTLIGHT,
                PID,
                StorageHandler.IMAGE_PREFIX,
                progressBar,
                esaphGlobalDownloadListener);

        this.temp = temp;
        this.imageViewAware = imageViewAware;
        this.esaphImageLoaderDisplayingAnimation = esaphImageLoaderDisplayingAnimation;
        this.blurry = blurry;
        this.placeHolderId = placeHolderId;
    }


    public static ImageRequestBuilder builder(String PID,
                                              ImageView imageView,
                                              ProgressBar progressBar,
                                              EsaphDimension MAX_SIZE,
                                              int esaphImageLoaderDisplayingAnimation,
                                              int placeHolderId)
    {
        return new ImageRequestBuilder(imageView,
                PID,
                progressBar,
                MAX_SIZE,
                esaphImageLoaderDisplayingAnimation,
                placeHolderId);
    }



    public static class ImageRequestBuilder
    {
        private final ImageViewAware imageViewAware;
        private final int esaphImageLoaderDisplayingAnimation;
        private boolean blurry;
        private final String OBJECT_ID;
        private final ProgressBar progressBar;
        private final EsaphDimension RESOLUTION;
        private EsaphGlobalDownloadListener esaphGlobalDownloadListener;
        private final int placeHolderId;
        private boolean temp = false;

        public ImageRequestBuilder(ImageView imageView,
                            String PID,
                            ProgressBar progressBar,
                            EsaphDimension MAX_SIZE,
                            int esaphImageLoaderDisplayingAnimation,
                                   int placeHolderId)
        {
            this.imageViewAware = new ImageViewAware(imageView);
            this.esaphImageLoaderDisplayingAnimation = esaphImageLoaderDisplayingAnimation;
            this.blurry = false;
            this.OBJECT_ID = PID;
            this.progressBar = progressBar;
            this.RESOLUTION = MAX_SIZE;
            this.placeHolderId = placeHolderId;
        }

        public ImageRequest build(ReentrantLock reentrantLock)
        {
            return new ImageRequest(reentrantLock,
                    temp,
                    imageViewAware,
                    OBJECT_ID,
                    progressBar,
                    RESOLUTION,
                    blurry,
                    esaphGlobalDownloadListener,
                    esaphImageLoaderDisplayingAnimation,
                    placeHolderId);
        }

        public ImageRequestBuilder setEsaphGlobalDownloadListener(EsaphGlobalDownloadListener esaphGlobalDownloadListener) {
            this.esaphGlobalDownloadListener = esaphGlobalDownloadListener;
            return this;
        }

        public String getOBJECT_ID() {
            return OBJECT_ID;
        }

        public ImageRequestBuilder setBlurry(boolean blurry) {
            this.blurry = blurry;
            return this;
        }

        public ImageRequestBuilder setTemp(boolean temp) {
            this.temp = temp;
            return this;
        }
    }
}