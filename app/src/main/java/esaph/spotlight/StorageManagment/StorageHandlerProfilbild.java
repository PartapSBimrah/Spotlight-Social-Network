package esaph.spotlight.StorageManagment;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;

public class StorageHandlerProfilbild
{
    public static final String FOLDER_PROFILBILD = "PB";
    public static final String FOLDER_CACHE = "PBCA";

    public static long fileLength(File file)
    {
        if(!StorageHandlerProfilbild.fileExists(file))
        {
            return -1;
        }
        return file.length();
    }

    public static File getFile(Context context, String Username, EsaphDimension dimension, String FOLDER) //Cache only, for profiles not from friends. So they get deleted with new start
    {
        File rootPath = new File(context.getFilesDir(), FOLDER);
        rootPath.mkdirs();

        return new File(rootPath, Username + dimension.getComparedDimensions());
    }

    public static List<File> getFilesSamePID(Context context, String PID, String FOLDER)
    {
        List<File> listFilesSameResolution = new ArrayList<>();
        String[] files = new String[]{};
        File fileroot = null;

        fileroot = new File(context.getFilesDir(), FOLDER);
        files = fileroot.list();

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


    public static void deleteFile(Context context, long UID)
    {
        List<File> list = getFilesSamePID(context, Long.toString(UID), StorageHandlerProfilbild.FOLDER_PROFILBILD);
        for (File file : list)
        {
            if(file.exists())
            {
                file.delete();
            }
        }
    }

    public static File getFile(Context context, String Username, boolean storeable) //Cache only, for profiles not from friends. So they get deleted with new start
    {
        if(storeable)
        {
            File rootPath = new File(context.getFilesDir(), StorageHandlerProfilbild.FOLDER_PROFILBILD);
            rootPath.mkdirs();

            return new File(rootPath, Username);
        }
        else
        {
            File rootPath = new File(context.getFilesDir(), StorageHandlerProfilbild.FOLDER_CACHE);
            rootPath.mkdirs();

            return new File(rootPath, Username);
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

    public static void dropCacheFolder(Context context)
    {
        File[] files = new File(context.getFilesDir(), StorageHandlerProfilbild.FOLDER_CACHE).listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                file.delete();
            }
        }
    }


    public static void dropProfilbildFolder(Context context)
    {
        File[] files = new File(context.getFilesDir(), StorageHandlerProfilbild.FOLDER_PROFILBILD).listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                file.delete();
            }
        }
    }


    public static void dropAllFiles(Context context)
    {
        StorageHandlerProfilbild.dropCacheFolder(context);
        StorageHandlerProfilbild.dropProfilbildFolder(context);
    }
}