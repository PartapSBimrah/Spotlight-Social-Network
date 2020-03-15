/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.StorageManagment;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;

public class StorageHandler
{
    public static final String VIDEO_PREFIX = ".esv";
    public static final String IMAGE_PREFIX = ".esi";
    public static final String SPOTMESSAGE_IMAGE_PREFIX = ".smesi";
    public static final String AUDIO_PREFIX = ".3gp";
    public static final String LIFECLOUD_PREFIX = ".lcb"; //LifeCloudBackup
    public static final String STICKER_PREFIX = ".lfs";

    public static final String FOLDER__TEMP = "temp";
    public static final String FOLDER__SPOTLIGHT = "Spotlight";
    public static final String FOLDER__SPOTLIGHT_AUDIO = "Audio";
    public static final String FOLDER__LIFECLOUD = "LifeCloud";
    public static final String FOLDER__SPOTLIGHT_MESSAGES = "SpotMessages";
    public static final String FOLDER__SPOTLIGHT_STICKER = "SpotLightSticker";



    public static long removeImageData(String Folder, Context context, String PID)
    {
        List<File> fileList = getFilesSamePID(context, PID, Folder);
        long lengthTotalSum = 0;

        if(fileList != null)
        {
            for(File file : fileList)
            {
                if(file != null && file.exists())
                {
                    lengthTotalSum += file.length();
                    file.delete();
                }
            }
        }

        return lengthTotalSum;
    }

    public static File getFile(Context context, String Folder, String PID, EsaphDimension dimension, String prefix)
    {
        /*
        if(dimension == null && Folder != SpotLightFileTypes.FILE_TYPE_AUDIO) //This is for uploading shit.
        {
            List<File> list = getFilesSamePID(Folder, context, PID);

            BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
            bitMapOption.inJustDecodeBounds = true;

            File currentBestResolution = null;
            int lastBest = 0;

            if(list != null)
            {
                for(File file : list)
                {
                    try
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
                        }
                        else //Video scaling not exists.
                        {
                            currentBestResolution = file;
                            break;
                        }
                    }
                    catch (Exception ec)
                    {
                        Log.i("StorageHandler", "getFile() finding highest resolution failed(): " + ec);
                    }
                }
            }

            return currentBestResolution;
        }

        if(Folder == SpotLightFileTypes.FILE_TYPE_SPOTLIGHT)
        {

        }
        else if(Folder == SpotLightFileTypes.FILE_TYPE_LIFECLOUD)
        {
            return StorageHandlerLifeCloud.getFile(context, PID, dimension, prefix);
        }
        else if(Folder == SpotLightFileTypes.FILE_TYPE_AUDIO)
        {
            return StorageHandlerAudio.getFile(context, PID);
        }
        else if(Folder == SpotLightFileTypes.FILE_TYPE_STICKER)
        {
            StorageHandlerSticker.getStickerFile(context, PID, prefix);
        }
*/

        File rootPath = new File(context.getFilesDir(), Folder);
        rootPath.mkdirs();

        if(dimension == null)
            return new File(rootPath, PID + prefix);

        return new File(rootPath, PID + dimension.getComparedDimensions() + prefix);
    }


    public static File getFileVideo(String Folder, Context context, String PID) //Use only when saving downloaded video.
    {
        File rootPath = new File(context.getFilesDir(), Folder);
        rootPath.mkdirs();

        return new File(rootPath,PID + StorageHandler.VIDEO_PREFIX);
    }

    public static void dropAllFiles(Context context)
    {
        dropFolder(context, StorageHandler.FOLDER__SPOTLIGHT_AUDIO);
        dropFolder(context, StorageHandler.FOLDER__SPOTLIGHT);
        dropFolder(context, StorageHandler.FOLDER__LIFECLOUD);
        dropFolder(context, StorageHandler.FOLDER__SPOTLIGHT_STICKER);
        dropFolder(context, StorageHandler.FOLDER__SPOTLIGHT_MESSAGES);
        dropTempFiles(context);
    }

    public static void dropFolder(Context context, String Folder)
    {
        File[] files = new File(context.getFilesDir(), Folder).listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                file.delete();
            }
        }
    }

    public static void saveToResolutions(Context context, Bitmap bitmap, File file)
    {
        FileOutputStream outputStream = null;

        try
        {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (Exception ec)
                {
                    Log.i(context.getClass().getName(), "saveToResolutions() failed closing stream: " + ec);
                }
            }
        }
    }

    public static void saveToResolutionsWithCompression(Context context, Bitmap bitmap, File file, int factor)
    {
        FileOutputStream outputStream = null;

        try
        {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, factor, outputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (Exception ec)
                {
                    Log.i(context.getClass().getName(), "saveToResolutionsWithCompression() failed closing stream: " + ec);
                }
            }
        }
    }


    public static void updateInternPidWithServerPid(Context context,
                                                    String PID_INTERN,
                                                    String PID_SERVER,
                                                    String FOLDER)
    {

        try
        {
            File fileroot = new File(context.getFilesDir(), FOLDER);
            String[] files = fileroot.list();

            if(files != null)
            {
                for(String fileName : files)
                {
                    System.out.println("Trying to update");
                    if(fileName.contains(PID_INTERN))
                    {
                        String newFileName = fileName.replace(PID_INTERN, PID_SERVER);

                        File originalFile = new File(fileroot, fileName);
                        File newFile = new File(originalFile.getParent(), newFileName);
                        System.out.println("Trying to NEW FILE NAME: " + newFile.getAbsolutePath());
                        System.out.println("Trying to ORIGINAL FILE NAME: " + originalFile.getAbsolutePath());
                        if (fileExists(newFile))
                        {
                            newFile.delete();
                        }

                        if(!originalFile.renameTo(newFile))
                        {
                            System.out.println("Trying to update NOT RENAMED: " + newFile.getAbsolutePath());
                            originalFile.delete();
                        }
                        else
                        {
                            System.out.println("Trying to update RENAMED: " + originalFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void saveVideoFile(Context context, byte[] videoData, File file)
    {
        FileOutputStream outputStream = null;

        try
        {
            outputStream = new FileOutputStream(file);
            outputStream.write(videoData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (Exception ec)
                {
                    Log.i(context.getClass().getName(), "saveVideoFile() failed closing stream: " + ec);
                }
            }
        }
    }

    public static boolean fileExists(File file)
    {
        if(file == null || !file.exists())
        {
            return false;
        }
        return true;
    }

    public static long fileLength(File file)
    {
        if(!fileExists(file))
        {
            return -1;
        }
        return file.length();
    }


    public static long getSizeOfFolder(File dir)
    {
        long size = 0;
        for (File file : dir.listFiles())
        {
            if (file.isFile())
            {
                size += file.length();
            }
            else
            {
                size += getSizeOfFolder(file);
            }

            if(size < 0) //Getting negativ if size were to big.
            {
                return Long.MAX_VALUE;
            }
        }
        return size;
    }


    public static List<File> getFilesSamePID(Context context, String PID, String Folder)
    {
        List<File> listFilesSameResolution = new ArrayList<>();

        File fileroot = new File(context.getFilesDir(), Folder);
        String[] files = fileroot.list();

        if(files != null)
        {
            for(String fileName : files)
            {
                if(fileName.contains(PID))
                {
                    listFilesSameResolution.add(new File(fileroot, fileName));
                }
            }
        }

        return listFilesSameResolution;
    }


    public static void dropTempFiles(Context context)
    {
        File[] files = new File(context.getFilesDir(), StorageHandler.FOLDER__TEMP).listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                file.delete();
            }
        }
    }

    public static void copy(File src, File dst) throws IOException
    {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
