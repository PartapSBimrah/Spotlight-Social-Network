package esaph.spotlight.Esaph.EsaphVideoCutHelper;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class EsaphVideoCutHelper
{
    public static void cutOutFromVideo(long startMs, long endMs, File inputFileAbsolutePath, File outputFileAbsolutePath)
    {
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i",
                inputFileAbsolutePath.getAbsolutePath(),
                "-t",
                "" + (endMs - startMs) / 1000,
                "-s", "320x240", "-r", "15",
                "-vcodec", "mpeg4", "-b:v",
                "2097152", "-b:a", "48000",
                "-ac", "2", "-ar", "22050",
                outputFileAbsolutePath.getAbsolutePath()};
    }

    public static FFmpeg setUpFFmpeg(Context context)
    {
        FFmpeg fFmpeg = FFmpeg.getInstance(context);

        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("Event ", "onStart");
                }

                @Override
                public void onFailure() {
                    Log.d("Event ", "onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d("Event ", "onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d("Event ", "onFinish");

                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }

        return fFmpeg;
    }

    public static void execFFmpegBinary(FFmpeg fFmpeg, String[] commands)
    {
        try {

            fFmpeg.execute(commands, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("Event ", "onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.e("Event ", "onProgress - " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e("Event ", "onFailure - " + message);

                }

                @Override
                public void onSuccess(String message) {
                    Log.e("Event ", "onSuccess - " + message);

                }

                @Override
                public void onFinish() {
                    Log.e("Event ", "onFinish");

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

}
