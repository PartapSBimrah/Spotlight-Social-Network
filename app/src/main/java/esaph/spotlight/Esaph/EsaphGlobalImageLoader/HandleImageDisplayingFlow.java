/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.download.ImageDownloader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.ProcessingUtils;
import esaph.spotlight.StorageManagment.StorageHandler;

public class HandleImageDisplayingFlow extends LoadingAndDisplayBase
{
    private Context context;
    private ImageRequest imageRequest;
    private Handler handler;

    public HandleImageDisplayingFlow(ImageRequest imageRequest,
                                     ImageLoaderEngine engine,
                                     Handler handler)
    {
        super(imageRequest, engine);
        this.context = imageRequest.imageViewAware.getWrappedView().getContext();
        this.imageRequest = imageRequest;
        this.handler = handler;
    }

    private Bitmap handleSavedFilesImage()
    {
        try
        {
            Log.i(getClass().getName(), "ImageDownloader handling saved files images");
            List<File> listFilesFound = StorageHandler.getFilesSamePID(context, imageRequest.OBJECT_ID, imageRequest.Folder);
            if(listFilesFound.isEmpty()) return null;

            Log.i(getClass().getName(), "ImageDownloader determining best dimension.");
            File fileBestImageResolutionForCurrentView = ProcessingUtils.determineBestDimension(listFilesFound, imageRequest.imageViewAware.getResolution());


            if(fileBestImageResolutionForCurrentView != null)
            {
                Log.i(getClass().getName(), "ImageDownloader isnt null");
                if(ProcessingUtils.isImageHigher(fileBestImageResolutionForCurrentView, imageRequest.imageViewAware.getResolution()))
                {
                    Log.i(getClass().getName(), "ImageDownloader Image is HIIIGH");
                    Bitmap bitmap = ProcessingUtils.scaleBitmap(BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(fileBestImageResolutionForCurrentView))),
                            imageRequest.imageViewAware.getResolution());
                    StorageHandler.saveToResolutions(context, bitmap, fileBestImageResolutionForCurrentView);
                    return bitmap;
                }
            }
        }
        catch (IOException io)
        {
            Log.i(getClass().getName(), "ImageDownloader handleSavedFilesImage() failed: " + io);
        }
        return null;
    }


    private Bitmap tryLoadingBitmapFromStorage() throws IOException
    {
        File fileImage = StorageHandler.getFile(context,
                imageRequest.Folder,
                imageRequest.OBJECT_ID,
                imageRequest.imageViewAware.getResolution(),
                imageRequest.PREFIX);

        Bitmap bitmap;

        Log.i(getClass().getName(), "ImageDownloader file checking for existens: " + fileImage.getAbsolutePath());

        if(fileImage.exists())
        {
            Log.i(getClass().getName(), "ImageDownloader file existing");
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(fileImage)));
            if(bitmap != null) return bitmap;
        }

        bitmap = handleSavedFilesImage();

        if(bitmap != null) return bitmap;

        if(ImageDownloader.startDownloadingImageInResolution(context,
                this,
                imageRequest,
                fileImage,
                true))
        {
            Log.i(getClass().getName(), "ImageDownloader downloaded successfull");
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(fileImage)));
            Log.i(getClass().getName(), "ImageDownloader download size: " + bitmap.getWidth() + "/" + bitmap.getHeight());
        }

        return bitmap;
    }

    @Override
    public void run()
    {
        imageRequest.reentrantLock.lock();
        Bitmap bmp = null;

        try
        {
            /*
            try
            {
                imageRequest.imageViewAware.getWrappedView().setAnimation(AnimationUtils.loadAnimation(context, imageRequest.esaphImageLoaderDisplayingAnimation));
            }
            catch (Exception ec)
            {
            }*/

            checkTaskNotActual(imageRequest.viewAware, imageRequest.OBJECT_ID);
            bmp = tryLoadingBitmapFromStorage();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "HandleImageDisplayingFlow failed: " + ec);
        }
        finally
        {
            imageRequest.reentrantLock.unlock();
        }

        handler.post(new GlobalDisplay(imageRequest, getEngine(), bmp));
    }
}