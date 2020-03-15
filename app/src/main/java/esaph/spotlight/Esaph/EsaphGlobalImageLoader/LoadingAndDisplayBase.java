/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.BaseRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageViewAware;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ViewAware;

public abstract class LoadingAndDisplayBase implements Runnable
{
    private BaseRequest baseRequestBuilder;
    private ImageLoaderEngine engine;

    public LoadingAndDisplayBase(BaseRequest baseRequestBuilder, ImageLoaderEngine engine)
    {
        this.baseRequestBuilder = baseRequestBuilder;
        this.engine = engine;
    }

    public BaseRequest getBaseRequestBuilder() {
        return baseRequestBuilder;
    }

    public ImageLoaderEngine getEngine() {
        return engine;
    }

    public void checkTaskNotActual(ViewAware viewAware, String OBJECT_ID) throws TaskCancelledException
    {
        checkViewCollected(viewAware);
        checkViewReused(viewAware, OBJECT_ID);
    }

    /**
     * @return <b>true</b> - if task is not actual (target VideoAware is collected by GC or the image URI of this task
     * doesn't match to image URI which is actual for current VideoAware at this moment)); <b>false</b> - otherwise
     */
    private boolean isTaskNotActual(ImageViewAware imageViewAware, String OBJECT_ID)
    {
        return isViewCollected(imageViewAware) || isViewReused(imageViewAware, OBJECT_ID);
    }

    /** @throws TaskCancelledException if target VideoAware is collected */
    private void checkViewCollected(ViewAware viewAware) throws TaskCancelledException
    {
        if (isViewCollected(viewAware))
        {
            throw new TaskCancelledException();
        }
    }

    /** @return <b>true</b> - if target VideoAware is collected by GC; <b>false</b> - otherwise */
    private boolean isViewCollected(ViewAware viewAware)
    {
        if (viewAware.isCollected()) {
            return true;
        }
        return false;
    }

    /** @throws TaskCancelledException if target VideoAware is collected by GC */
    private void checkViewReused(ViewAware viewAware,
                                 String OBJECT_ID) throws TaskCancelledException
    {
        if (isViewReused(viewAware, OBJECT_ID))
        {
            throw new TaskCancelledException();
        }
    }

    /** @return <b>true</b> - if current VideoAware is reused for displaying another image; <b>false</b> - otherwise */
    private boolean isViewReused(ViewAware viewAware,
                                 String OBJECT_ID)
    {
        String currentCacheKey = engine.getLoadingUriForView(viewAware);
        // Check whether memory cache key (image URI) for current VideoAware is actual.
        // If VideoAware is reused for another task then current task should be cancelled.
        boolean imageAwareWasReused = !OBJECT_ID.equals(currentCacheKey);
        if (imageAwareWasReused) {
            return true;
        }
        return false;
    }

    class TaskCancelledException extends Exception {
    }

}
