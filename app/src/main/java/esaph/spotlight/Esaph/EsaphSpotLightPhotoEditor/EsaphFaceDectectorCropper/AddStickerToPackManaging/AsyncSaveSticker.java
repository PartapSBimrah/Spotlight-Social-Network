/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLSticker;

public class AsyncSaveSticker extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> contextWeakReference;
    private EsaphSpotLightSticker esaphSpotLightSticker;
    private List<EsaphSpotLightStickerPack> esaphSpotLightStickerPacks;
    private WeakReference<StickerSavingListener> stickerSavingListenerWeakReference;

    public AsyncSaveSticker(Context context,
                            List<EsaphSpotLightStickerPack> esaphSpotLightStickerPacks,
                            EsaphSpotLightSticker esaphSpotLightSticker,
                            StickerSavingListener stickerSavingListener)
    {
        this.stickerSavingListenerWeakReference = new WeakReference<>(stickerSavingListener);
        this.contextWeakReference = new WeakReference<>(context);
        this.esaphSpotLightStickerPacks = esaphSpotLightStickerPacks;
        this.esaphSpotLightSticker = esaphSpotLightSticker;
    }

    public interface StickerSavingListener
    {
        void onStickerUpdate(EsaphSpotLightSticker esaphSpotLightSticker);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            SQLSticker sqlSticker = new SQLSticker(contextWeakReference.get());

            for(EsaphSpotLightStickerPack esaphSpotLightStickerPack : esaphSpotLightStickerPacks)
            {
                esaphSpotLightSticker.setSTICKER_PACK_ID(esaphSpotLightStickerPack.getLSPID()); //Matching ids together with existing packets.
                sqlSticker.addSticker(esaphSpotLightStickerPack, esaphSpotLightSticker);
            }
            sqlSticker.close();

            File fileTemp = StorageHandler.getFile(contextWeakReference.get(),
                    StorageHandler.FOLDER__TEMP,
                    esaphSpotLightSticker.getIMAGE_ID(),
                    null,
                    StorageHandler.STICKER_PREFIX);

            File fileDst = StorageHandler.getFile(contextWeakReference.get(),
                    StorageHandler.FOLDER__SPOTLIGHT_STICKER,
                    esaphSpotLightSticker.getIMAGE_ID(),
                    null,
                    StorageHandler.STICKER_PREFIX);

            StorageHandler.copy(fileTemp, fileDst);

            return Boolean.TRUE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSaveSticker doInBackground() failed: " + ec);
        }
        return Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);

        StickerSavingListener stickerSavingListener = stickerSavingListenerWeakReference.get();
        if(stickerSavingListener != null)
        {
            if(aBoolean != null && aBoolean)
            {
                esaphSpotLightSticker.setSelection(true);
            }
            else
            {
                esaphSpotLightSticker.setSelection(false);
            }

            stickerSavingListener.onStickerUpdate(esaphSpotLightSticker);
        }
    }
}
