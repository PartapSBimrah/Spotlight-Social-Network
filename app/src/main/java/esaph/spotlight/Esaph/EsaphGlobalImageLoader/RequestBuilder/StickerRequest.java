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
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageViewAware;
import esaph.spotlight.StorageManagment.StorageHandler;

public class StickerRequest extends BaseRequest
{
    public final ImageViewAware imageViewAware;
    public final int esaphImageLoaderDisplayingAnimation;
    public final boolean blurry;
    public final int placeHolderId;

    private StickerRequest(ReentrantLock reentrantLock,
                           ImageViewAware imageViewAware,
                           String PID,
                           ProgressBar progressBar,
                           boolean blurry,
                           EsaphGlobalDownloadListener esaphGlobalDownloadListener,
                           int esaphImageLoaderDisplayingAnimation,
                           int placeHolderId,
                           String Folder)
    {
        super(reentrantLock,
                imageViewAware,
                Folder,
                PID,
                StorageHandler.STICKER_PREFIX,
                progressBar,
                esaphGlobalDownloadListener);

        this.imageViewAware = imageViewAware;
        this.esaphImageLoaderDisplayingAnimation = esaphImageLoaderDisplayingAnimation;
        this.blurry = blurry;
        this.placeHolderId = placeHolderId;
    }


    public static StickerRequestBuilder builder(String PID,
                                              ImageView imageView,
                                              ProgressBar progressBar,
                                              int esaphImageLoaderDisplayingAnimation,
                                              int placeHolderId)
    {
        return new StickerRequestBuilder(imageView,
                PID,
                progressBar,
                esaphImageLoaderDisplayingAnimation,
                placeHolderId);
    }



    public static class StickerRequestBuilder
    {
        private final ImageViewAware imageViewAware;
        private final int esaphImageLoaderDisplayingAnimation;
        private boolean blurry;
        private final String OBJECT_ID;
        private final ProgressBar progressBar;
        private EsaphGlobalDownloadListener esaphGlobalDownloadListener;
        private final int placeHolderId;
        private boolean isTemp = false;

        public StickerRequestBuilder(ImageView imageView,
                            String PID,
                            ProgressBar progressBar,
                            int esaphImageLoaderDisplayingAnimation,
                                   int placeHolderId)
        {
            this.imageViewAware = new ImageViewAware(imageView);
            this.esaphImageLoaderDisplayingAnimation = esaphImageLoaderDisplayingAnimation;
            this.blurry = false;
            this.OBJECT_ID = PID;
            this.progressBar = progressBar;
            this.placeHolderId = placeHolderId;
        }

        public StickerRequest build(ReentrantLock reentrantLock)
        {
            String FolderName = StorageHandler.FOLDER__SPOTLIGHT_STICKER;
            if(isTemp) FolderName = StorageHandler.FOLDER__TEMP;

            return new StickerRequest(reentrantLock,
                    imageViewAware,
                    OBJECT_ID,
                    progressBar,
                    blurry,
                    esaphGlobalDownloadListener,
                    esaphImageLoaderDisplayingAnimation,
                    placeHolderId,
                    FolderName);
        }

        public StickerRequestBuilder setEsaphGlobalDownloadListener(EsaphGlobalDownloadListener esaphGlobalDownloadListener) {
            this.esaphGlobalDownloadListener = esaphGlobalDownloadListener;
            return this;
        }

        public String getOBJECT_ID() {
            return OBJECT_ID;
        }

        public StickerRequestBuilder setBlurry(boolean blurry) {
            this.blurry = blurry;
            return this;
        }

        public StickerRequestBuilder setTemp(boolean temp) {
            isTemp = temp;
            return this;
        }
    }
}