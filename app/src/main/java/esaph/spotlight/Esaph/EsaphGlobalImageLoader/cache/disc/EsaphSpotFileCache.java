package esaph.spotlight.Esaph.EsaphGlobalImageLoader.cache.disc;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;

public class EsaphSpotFileCache
{
    public static File getFile(Context context, long ID, EsaphDimension esaphDimension, String jsonHash)
    {
        return new File(context.getCacheDir(),ID + jsonHash + esaphDimension.getComparedDimensions());
    }

    public static void putFile(Context context, File file, Bitmap bitmap)
    {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try
            {
                if(outputStream != null)
                {
                    outputStream.close();
                }

            }
            catch (Exception ec)
            {
            }
        }
    }
}
