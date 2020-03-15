/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.graphics.Bitmap;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.BaseRequest;

public class GlobalDisplay implements Runnable
{
    private BaseRequest baseRequest;
    private ImageLoaderEngine engine;
    private Bitmap bitmap;

    public GlobalDisplay(BaseRequest baseRequest, ImageLoaderEngine engine, Bitmap bitmap)
    {
        this.baseRequest = baseRequest;
        this.engine = engine;
        this.bitmap = bitmap;
    }

    @Override
    public void run()
    {
        if(this.bitmap == null) //bitmap should not be null.
            return;

        if(baseRequest.viewAware.isCollected()) return;
        if(isViewReused()) return;

        if(baseRequest.esaphGlobalDownloadListener != null)
        {
            baseRequest.esaphGlobalDownloadListener.onAvaiableImage(baseRequest.OBJECT_ID);
        }

        baseRequest.viewAware.setImageBitmap(this.bitmap);

        engine.cancelDisplayTaskFor(baseRequest.viewAware);
    }

    private boolean isViewReused() {
        String currentCacheKey = engine.getLoadingUriForView(baseRequest.viewAware);
        // Check whether memory cache key (image URI) for current VideoAware is actual.
        // If VideoAware is reused for another task then current task should be cancelled.
        boolean imageAwareWasReused = !baseRequest.OBJECT_ID.equals(currentCacheKey);
        if (imageAwareWasReused) {
            return true;
        }
        return false;
    }

}
