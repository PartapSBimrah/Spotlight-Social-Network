/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

import esaph.spotlight.R;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraEffectsEnum;

public class MediaRecorderFileHandler
{
    private static File getRecordingFileNormal(Context context) throws IOException
    {
        File fileDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + "."
                + context.getResources().getString(R.string.app_name)
                + File.separator + "UC"
                + File.separator
                + "Current"
                + File.separator);

        if (!fileDir.exists())
        {
            fileDir.mkdirs();
        }

        File fileNormal = new File(fileDir,
                System.currentTimeMillis() + ".mp4");
        fileNormal.createNewFile();

        return fileNormal;
    }


    private static File getRecordingFileCutable(Context context) throws IOException
    {
        File fileDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + "."
                + context.getResources().getString(R.string.app_name)
                + File.separator + "UC"
                + File.separator
                + "Current"
                + File.separator
                + "CASession"
                + File.separator);

        if (!fileDir.exists())
        {
            fileDir.mkdirs();
        }

        File fileCutable = new File(fileDir,
                System.currentTimeMillis() + ".mpca");

        fileCutable.createNewFile();
        return fileCutable;
    }

    public static File getFile(Context context, CameraEffectsEnum cameraEffectsEnum) throws IOException
    {
        switch (cameraEffectsEnum)
        {
            case NONE:
                return MediaRecorderFileHandler.getRecordingFileNormal(context);

                /*
            case CUTABLE:
                return MediaRecorderFileHandler.getRecordingFileCutable(context);*/
        }
        return null;
    }
}
