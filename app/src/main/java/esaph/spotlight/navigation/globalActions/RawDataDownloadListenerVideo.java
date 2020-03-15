package esaph.spotlight.navigation.globalActions;

import java.io.File;

public interface RawDataDownloadListenerVideo
{
    void onVideoDownloaded(File file);
    void onVideoDownloadFailed(String PID);
}
