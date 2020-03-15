package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.util.Log;

import java.io.File;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;


public class GlobalDisplayVideoFile implements Runnable
{
    private VideoRequest videoRequest;
    private File file;

    public GlobalDisplayVideoFile(VideoRequest videoRequest,
                                  File file)
    {
        this.videoRequest = videoRequest;
        this.file = file;
    }

    @Override
    public void run()
    {
        try
        {
            EsaphGlobalDownloadListener esaphGlobalVideoDownloadListener = videoRequest.esaphGlobalDownloadListener;
            if(esaphGlobalVideoDownloadListener != null)
            {
                if(file == null || !file.exists())
                {
                    esaphGlobalVideoDownloadListener.onFailed(videoRequest.OBJECT_ID);
                }
                else {
                    esaphGlobalVideoDownloadListener.onAvaiableVideo(file);
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "GlobalDisplayVideoFile failed: " + ec);
        }
    }
}
