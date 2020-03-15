package esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback;

import java.io.File;

public interface EsaphGlobalDownloadListener
{
    void onAvaiableImage(String PID);
    void onAvaiableVideo(File file);
    void onFailed(String PID);
}
