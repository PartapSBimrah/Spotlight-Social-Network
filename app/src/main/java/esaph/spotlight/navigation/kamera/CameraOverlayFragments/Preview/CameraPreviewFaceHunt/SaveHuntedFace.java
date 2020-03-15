/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerSticker;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class SaveHuntedFace implements Runnable
{
    private boolean success = false;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<OnHuntedFaceSavedListener> onHuntedFaceSavedListenerWeakReference;
    private Bitmap bitmap;
    private EsaphSpotLightSticker esaphSpotLightSticker;

    public SaveHuntedFace(Context context, Bitmap bitmap,
                          OnHuntedFaceSavedListener onHuntedFaceSavedListener)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.onHuntedFaceSavedListenerWeakReference = new WeakReference<>(onHuntedFaceSavedListener);
        this.bitmap = bitmap;
    }

    public interface OnHuntedFaceSavedListener
    {
        void onSaved(EsaphSpotLightSticker esaphSpotLightSticker);
        void onFailed();
    }

    @Override
    public void run()
    {
        BufferedOutputStream bufferedOutputStream = null;
        try
        {
            esaphSpotLightSticker = new EsaphSpotLightSticker(
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    System.currentTimeMillis(),
                    -1,
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis());

            File stickerFile = StorageHandler.getFile(contextWeakReference.get(),
                    StorageHandler.FOLDER__TEMP,
                    esaphSpotLightSticker.getIMAGE_ID(),
                    null,
                    StorageHandler.STICKER_PREFIX);

            StorageHandler.saveToResolutionsWithCompression(contextWeakReference.get(),
                    StorageHandlerSticker.scaleSticker(bitmap),
                    stickerFile,
                    EsaphGlobalValues.COMP_RATE_STICKER);

            success = true;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SaveHuntedFace() failed: " + ec);
            success = false;
        }
        finally
        {
            try
            {
                if(bufferedOutputStream != null)
                {
                    bufferedOutputStream.close();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        OnHuntedFaceSavedListener onHuntedFaceSavedListener = onHuntedFaceSavedListenerWeakReference.get();
                        if(onHuntedFaceSavedListener != null)
                        {
                            if(success)
                            {
                                onHuntedFaceSavedListener.onSaved(esaphSpotLightSticker);
                            }
                            else
                            {
                                onHuntedFaceSavedListener.onFailed();
                            }
                        }
                    }
                });
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "SavedHuntedFace finally, run() failed: " + ec);
            }
        }
    }
}
