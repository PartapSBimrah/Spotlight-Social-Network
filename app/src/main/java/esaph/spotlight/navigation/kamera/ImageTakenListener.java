package esaph.spotlight.navigation.kamera;

import android.graphics.Bitmap;

public interface ImageTakenListener
{
    void onImageTaken();
    void onImageReady(Bitmap bitmap);
    void onImageTakenFailed();
}
