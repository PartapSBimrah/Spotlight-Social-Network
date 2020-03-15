package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLSticker;


public class AsyncDeleteSticker extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<StickerDeletingListener> stickerDeletingListenerWeakReference;
    private EsaphSpotLightSticker spotLightSticker;

    public AsyncDeleteSticker(Context context,
                              EsaphSpotLightSticker esaphSpotLightSticker,
                              StickerDeletingListener stickerDeletingListener)
    {
        this.spotLightSticker = esaphSpotLightSticker;
        this.stickerDeletingListenerWeakReference = new WeakReference<>(stickerDeletingListener);
        this.contextWeakReference = new WeakReference<>(context);
    }

    public interface StickerDeletingListener
    {
        void onStickerUpdate(EsaphSpotLightSticker esaphSpotLightSticker);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            SQLSticker sqlSticker = new SQLSticker(contextWeakReference.get());
            sqlSticker.deleteSticker(spotLightSticker);
            sqlSticker.close();

            StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT_STICKER,
                    contextWeakReference.get(),
                    spotLightSticker.getIMAGE_ID());

            return Boolean.TRUE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncDeleteSticker doInBackground() failed: " + ec);
        }
        return Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        StickerDeletingListener stickerDeletingListener = stickerDeletingListenerWeakReference.get();
        if(stickerDeletingListener != null)
        {
            if(aBoolean != null && aBoolean)
            {
                spotLightSticker.setSelection(false);
            }
            stickerDeletingListener.onStickerUpdate(spotLightSticker);
        }
    }
}
