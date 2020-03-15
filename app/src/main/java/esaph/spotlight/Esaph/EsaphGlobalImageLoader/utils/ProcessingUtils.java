package esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.ImageLoaderEngineConfiguration;
import esaph.spotlight.StorageManagment.StorageHandler;

public class ProcessingUtils
{
    public static File determineBestDimension(List<File> files, EsaphDimension dimension) throws FileNotFoundException //Finding the heighest resolution. Because scaling is better than making it blur and download shit again.
    {
        BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
        bitMapOption.inJustDecodeBounds = true;

        File currentBestResolution = null;
        int lastBest = 0;

        for(File file : files)
        {
            if(!file.getName().endsWith(StorageHandler.VIDEO_PREFIX))
            {
                BitmapFactory.decodeStream(new FileInputStream(file), null, bitMapOption);
                int imageWidth = bitMapOption.outWidth;
                int imageHeight = bitMapOption.outHeight;

                int size = imageWidth + imageHeight;
                if(size > lastBest)
                {
                    lastBest = size;
                    currentBestResolution = file;
                }

                if(size >= dimension.getComparedDimensionsInt())
                {
                    break;
                }
            }
        }
        return currentBestResolution;
    }

    public static boolean isImageHigher(File file, EsaphDimension dimension) throws FileNotFoundException
    {
        BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
        bitMapOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(file)), null, bitMapOption);
        int imageWidth = bitMapOption.outWidth;
        int imageHeight = bitMapOption.outHeight;

        if((imageWidth + imageHeight) > (dimension.getWidth() + dimension.getHeight())) //If same resultion, this code is never called. because it found the required file.
        {
            return true;
        }

        return false;
    }

    public static boolean isResolutionHigher(File file, EsaphDimension dimension) throws FileNotFoundException
    {
        BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
        bitMapOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(file)), null, bitMapOption);
        int imageWidth = bitMapOption.outWidth;
        int imageHeight = bitMapOption.outHeight;

        if((imageWidth + imageHeight) < (dimension.getWidth() + dimension.getHeight())) //If same resultion, this code is never called. because it found the required file.
        {
            return true;
        }

        return false;
    }

    public static boolean isFileVideo(File file) //Mimetype not working.
    {
        return file.getName().endsWith(StorageHandler.VIDEO_PREFIX);
    }

    public static Bitmap createVideoThumpnail(File file) throws FileNotFoundException, IOException
    {
        Bitmap bitmapThumpnail = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            retriever.setDataSource(new FileInputStream(file).getFD());
            bitmapThumpnail = retriever.getFrameAtTime();
        }
        catch (IllegalArgumentException ex)
        {
            Log.i("ProcessingUtils", "failed to get frame from video" + ex);
        }
        catch (RuntimeException ex)
        {
            Log.i("ProcessingUtils", "failed to get frame from video" + ex);
        }
        finally
        {
            try
            {
                retriever.release();
            }
            catch (RuntimeException ex)
            {
                Log.i("ProcessingUtils", "failed to finally get frame from video" + ex);
            }
        }

        return bitmapThumpnail;
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, EsaphDimension SCALE_TO)
    {
        if(bitmap != null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(width == SCALE_TO.getWidth() && height == SCALE_TO.getHeight())
            {
                return bitmap;
            }

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1)
            {
                width = SCALE_TO.getWidth();
                height = (int) (width / bitmapRatio);
            }
            else
            {
                height = SCALE_TO.getHeight();
                width = (int) (height * bitmapRatio);
            }

            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return null;
    }


    public static boolean isViewResolutionBiggerThanLimit(EsaphDimension esaphDimension)
    {
        int valCaller = esaphDimension.getHeight() + esaphDimension.getHeight();
        int valMaxDimension = ImageLoaderEngineConfiguration.esaphDimensionMaxResolutionDisplay.getHeight() + ImageLoaderEngineConfiguration.esaphDimensionMaxResolutionDisplay.getHeight();
        if(valMaxDimension < valCaller)
        {
            return true;
        }

        return false;
    }

}
