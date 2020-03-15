package esaph.spotlight.Esaph.EsaphGlobalImageLoader.download;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.HandleVideoDisplayingFlow;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class VideoDownloader
{

    public static boolean startDownloadingVideo(Context context,
                                             final VideoRequest videoRequest,
                                             final HandleVideoDisplayingFlow handleVideoDisplayingFlow,
                                             File fileVideoSavingPlace)
    {
        long filesize = -1;
        int totalRead = 0;
        boolean failed = false;

        try
        {
            final ObjectAnimator objectAnimator = progressBarJump(handleVideoDisplayingFlow, videoRequest);

            JSONObject json = new JSONObject();
            json.put("PLSC", "PLUPV");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("PID", videoRequest.OBJECT_ID);
            json.put("QU", 1); //1 meaning here to download the video file, 0 means is for thumpnail of the video.

            handleVideoDisplayingFlow.checkTaskNotActual();

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(json.toString());
            writer.flush();

            if(reader.readLine().equals("1")) //FILE FOUND
            {
                writer.println("1");
                writer.flush();

                filesize = Long.parseLong(reader.readLine());
                DataInputStream dateiInputStream = new DataInputStream(socket.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(fileVideoSavingPlace);

                byte[] buffer = new byte[4096];
                int read = 0;
                totalRead = 0;
                long remaining = filesize;

                cancelJump(handleVideoDisplayingFlow,
                        videoRequest, objectAnimator);

                int progess = 0;
                while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
                {
                    if(progess < 75) handleVideoDisplayingFlow.checkTaskNotActual();

                    totalRead += read;
                    remaining -= read;

                    progess = (int) ((totalRead * 100) / filesize);
                    postProgressUpdate(handleVideoDisplayingFlow,
                            videoRequest, progess);

                    fileOutputStream.write(buffer, 0, read);
                }
                dateiInputStream.close();
                fileOutputStream.close();

                if(totalRead != filesize)
                {
                    failed = true;
                }

                reader.close();
                writer.close();
                socket.close();
            }
        }
        catch (Exception ec)
        {
            Log.i("VideoDownloader", "RunnableDownloadVideoFromPrivateMoments() failed: " + ec);
            failed = true;
        }
        finally
        {
            if(totalRead != filesize)
            {
                fileVideoSavingPlace.delete();
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = videoRequest.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        return !failed;
    }


    private static void cancelJump(final HandleVideoDisplayingFlow handleVideoDisplayingFlow,
                                   final VideoRequest videoRequest,
                                   final ObjectAnimator objectAnimator)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                ProgressBar progressBar = videoRequest.progressBar;
                if(progressBar != null)
                {
                    progressBar.clearAnimation();
                    progressBar.animate().cancel();
                    objectAnimator.cancel();
                }
            }
        });

    }

    private static ObjectAnimator progressBarJump(final HandleVideoDisplayingFlow handleVideoDisplayingFlow,
                                                  final VideoRequest videoRequest)
    {
        final ObjectAnimator animY = ObjectAnimator.ofFloat(videoRequest.progressBar, "translationY", -100f, 0f);
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    handleVideoDisplayingFlow.checkTaskNotActual();

                    ProgressBar progressBar = videoRequest.progressBar;
                    if(progressBar != null)
                    {

                        animY.setDuration(1000);//1sec
                        animY.setInterpolator(new BounceInterpolator());
                        animY.setRepeatCount(ObjectAnimator.INFINITE);
                        animY.setRepeatMode(ObjectAnimator.RESTART);
                        animY.start();

                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception ec)
                {
                }
            }
        });

        return animY;
    }

    private static void postProgressUpdate(final HandleVideoDisplayingFlow handleVideoDisplayingFlow,
                                           final VideoRequest videoRequest,
                                           final int value)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    handleVideoDisplayingFlow.checkTaskNotActual();
                    ProgressBar progressBar = videoRequest.progressBar;
                    if(progressBar != null)
                    {
                        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), value);
                        animation.setDuration(100);
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.start();
                    }
                }
                catch (Exception ec)
                {
                }
            }
        });
    }
}
