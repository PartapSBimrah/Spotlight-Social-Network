package esaph.spotlight.navigation.globalActions;

import android.graphics.Bitmap;

public interface RawDataDownloadListener
{
    void onDataDownloadOk(Bitmap bitmap);
    void onDataDownloadFailed();
}
