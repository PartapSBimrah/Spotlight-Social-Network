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

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.download.ImageDownloader;
import esaph.spotlight.StorageManagment.StorageHandler;

public class HandleStickerDisplayingFlow extends LoadingAndDisplayBase
{
    private Context context;
    private StickerRequest stickerRequest;
    private Handler handler;

    public HandleStickerDisplayingFlow(StickerRequest stickerRequest,
                                       ImageLoaderEngine engine,
                                       Handler handler)
    {
        super(stickerRequest, engine);
        this.context = stickerRequest.imageViewAware.getWrappedView().getContext();
        this.stickerRequest = stickerRequest;
        this.handler = handler;
    }

    private Bitmap tryLoadingBitmapFromStorage() throws IOException
    {
        File fileImage = StorageHandler.getFile(context,
                stickerRequest.Folder,
                stickerRequest.OBJECT_ID,
                null,
                stickerRequest.PREFIX);

        Bitmap bitmap = null;

        Log.i(getClass().getName(), "ImageDownloader file checking for existens: " + fileImage.getAbsolutePath());

        if(fileImage.exists())
        {
            Log.i(getClass().getName(), "ImageDownloader file existing");
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(fileImage)));
            if(bitmap != null) return bitmap;
        }

        if(ImageDownloader.downloadSticker(context,
                this,
                stickerRequest,
                fileImage))
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
        stickerRequest.reentrantLock.lock();
        Bitmap bmp = null;

        try
        {
            checkTaskNotActual(stickerRequest.imageViewAware, stickerRequest.OBJECT_ID);
            bmp = tryLoadingBitmapFromStorage();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "HandleImageDisplayingFlow failed: " + ec);
        }
        finally
        {
            stickerRequest.reentrantLock.unlock();
        }

        handler.post(new GlobalDisplay(stickerRequest, getEngine(), bmp));
    }

}