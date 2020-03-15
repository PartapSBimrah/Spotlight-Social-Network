/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.download.VideoDownloader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.ProcessingUtils;
import esaph.spotlight.StorageManagment.StorageHandler;


public class HandleVideoDisplayingFlow extends LoadingAndDisplayBase
{
    private VideoRequest videoRequest;
    private ImageLoaderEngine engine;
    private Handler handler;
    private Context context;

    public HandleVideoDisplayingFlow(VideoRequest videoRequest,
                                     ImageLoaderEngine engine,
                                     Handler handler)
    {
        super(videoRequest, engine);
        this.videoRequest = videoRequest;
        this.engine = engine;
        this.handler = handler;
        this.context = videoRequest.videoViewAware.getWrappedView().getContext();
    }

    /*
    private Bitmap handleSavedFilesVideo(File fileSaveTo) throws IOException //Video or image
    {
        List<File> listFilesFound = StorageHandler.getFilesSamePID(videoRequest.Folder, context, videoRequest.OBJECT_ID);
        if(!listFilesFound.isEmpty())
        {
            File bestImageResolutionForCurrentView = ProcessingUtils.determineBestDimension(listFilesFound, videoRequest.RESOLUTION);

            if(bestImageResolutionForCurrentView != null)
            {
                if(ProcessingUtils.isImageHigher(bestImageResolutionForCurrentView, videoRequest.RESOLUTION))
                {
                    Bitmap bitmap = ProcessingUtils.scaleBitmap(BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(bestImageResolutionForCurrentView))));
                    StorageHandler.saveToResolutions(context, bitmap, fileSaveTo);
                    return bitmap;
                }
                else
                {
                    File fileVideo = StorageHandler.getFileVideo(videoRequest.Folder, context, videoRequest.OBJECT_ID);
                    if(fileVideo != null)
                    {
                        Bitmap bitmapBlur = new BlurBuilder().blur(context,
                                BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(fileVideo))));

                        if(bitmapBlur != null)
                        {
                            handler.post(new EsaphGlobalImageLoader.GlobalDisplay(videoRequest, bitmapBlur));
                        }
                    }

                    return null;
                }
            }
            else
            {
                //No thumpnails found, check if video file is avaiable.
                File fileVideo = StorageHandler.getFileVideo(videoRequest.Folder, context, videoRequest.OBJECT_ID);
                if(StorageHandler.fileExists(fileVideo) && ProcessingUtils.isFileVideo(fileVideo))
                {
                    //CREATE THUMPNAIL, BECAUSE NOT EXISTS.
                    //SCALE IT FOR VIEW.

                    Bitmap bitmapThumpnail = ProcessingUtils.createVideoThumpnail(fileVideo);
                    StorageHandler.saveToResolutions(context, bitmapThumpnail, fileSaveTo);
                    return bitmapThumpnail;
                }
            }
        }
        else //No thumpnails found, check if video file is avaiable.
        {
            File fileVideo = StorageHandler.getFileVideo(videoRequest.Folder, context, videoRequest.OBJECT_ID);
            if(StorageHandler.fileExists(fileVideo) && isFileVideo(fileVideo))
            {
                //CREATE THUMPNAIL, BECAUSE NOT EXISTS.
                //SCALE IT FOR VIEW.

                Bitmap bitmapThumpnail = this.createVideoThumpnail(fileVideo);
                StorageHandler.saveToResolutions(context, bitmapThumpnail, fileSaveTo);
                return bitmapThumpnail;
            }
            //Do no downloading operations in this section, this is getting handled by boolean if the user want to see the video.
        }

        return null;
    }*/


    private void tryLoadFile(File fileVideo) throws TaskCancelledException
    {
        if(StorageHandler.fileExists(fileVideo) && ProcessingUtils.isFileVideo(fileVideo))
        {
            return;
        }

        if(fileVideo == null) return;

        checkTaskNotActual();

        VideoDownloader.startDownloadingVideo(context,
                videoRequest,
                this,
                fileVideo);
    }

    @Override
    public void run()
    {
        videoRequest.reentrantLock.lock();
        File file = null;
        try
        {
            file = StorageHandler.getFileVideo(videoRequest.Folder, context, videoRequest.OBJECT_ID);
            checkTaskNotActual();
            tryLoadFile(file);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "HandleVideoDisplayingFlow failed: " + ec);
        }
        finally
        {
            videoRequest.reentrantLock.unlock();
        }

        handler.post(new GlobalDisplayVideoFile(videoRequest, file));
    }


    public void checkTaskNotActual() throws TaskCancelledException
    {
        checkViewCollected();
        checkViewReused();
    }

    /**
     * @return <b>true</b> - if task is not actual (target VideoAware is collected by GC or the image URI of this task
     * doesn't match to image URI which is actual for current VideoAware at this moment)); <b>false</b> - otherwise
     */
    private boolean isTaskNotActual() {
        return isViewCollected() || isViewReused();
    }

    /** @throws TaskCancelledException if target VideoAware is collected */
    private void checkViewCollected() throws TaskCancelledException {
        if (isViewCollected()) {
            throw new TaskCancelledException();
        }
    }

    /** @return <b>true</b> - if target VideoAware is collected by GC; <b>false</b> - otherwise */
    private boolean isViewCollected() {
        if (videoRequest.videoViewAware.isCollected()) {
            return true;
        }
        return false;
    }

    /** @throws TaskCancelledException if target VideoAware is collected by GC */
    private void checkViewReused() throws TaskCancelledException {
        if (isViewReused()) {
            throw new TaskCancelledException();
        }
    }

    /** @return <b>true</b> - if current VideoAware is reused for displaying another image; <b>false</b> - otherwise */
    private boolean isViewReused() {
        String currentCacheKey = engine.getLoadingUriForView(videoRequest.videoViewAware);
        // Check whether memory cache key (image URI) for current VideoAware is actual.
        // If VideoAware is reused for another task then current task should be cancelled.
        boolean imageAwareWasReused = !videoRequest.OBJECT_ID.equals(currentCacheKey);
        if (imageAwareWasReused) {
            return true;
        }
        return false;
    }

    class TaskCancelledException extends Exception {
    }
    
}