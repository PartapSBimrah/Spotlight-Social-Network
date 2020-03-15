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

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.HandleImageDisplayingFlow;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.HandleStickerDisplayingFlow;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.LoadingAndDisplayBase;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.BaseRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class ImageDownloader
{
    public static boolean startDownloadingImageInResolution(Context context,
                                                     HandleImageDisplayingFlow handleImageDisplayingFlow,
                                                     final ImageRequest imageRequestBuilder,
                                                     File file,
                                                     boolean serverCanCrop)
    {
        boolean isFailed = false;
        long filesize = -1;
        int totalRead = 0;

        try
        {
            final ObjectAnimator objectAnimatorJump = progressBarJump(handleImageDisplayingFlow, imageRequestBuilder);

            JSONObject json = new JSONObject();
            json.put("PLSC", "PLUPP");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("PID", imageRequestBuilder.OBJECT_ID);

            int viewWidth = imageRequestBuilder.imageViewAware.getWidth();
            int viewHeight = imageRequestBuilder.imageViewAware.getHeight();

            if(viewHeight <= 0 || viewWidth <= 0)
                return false;

            json.put("VW", viewWidth);
            json.put("VH", viewHeight);
            json.put("CR", serverCanCrop);

            handleImageDisplayingFlow.checkTaskNotActual(imageRequestBuilder.viewAware, imageRequestBuilder.OBJECT_ID);

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(json.toString());
            writer.flush();

            filesize = Long.parseLong(reader.readLine());

            handleImageDisplayingFlow.checkTaskNotActual(imageRequestBuilder.viewAware, imageRequestBuilder.OBJECT_ID);

            DataInputStream dateiInputStream = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read = 0;
            totalRead = 0;
            long remaining = filesize;

            cancelJump(imageRequestBuilder,
                    objectAnimatorJump);

            int progess = 0;
            while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
            {
                if(progess < 75) handleImageDisplayingFlow.checkTaskNotActual(imageRequestBuilder.viewAware, imageRequestBuilder.OBJECT_ID);

                totalRead += read;
                remaining -= read;

                progess = (int) ((totalRead * 100) / filesize);

                postProgressUpdate(handleImageDisplayingFlow, imageRequestBuilder, progess);

                fileOutputStream.write(buffer, 0, read);
            }

            dateiInputStream.close();
            fileOutputStream.close();
            reader.close();
            writer.close();

            if(filesize != totalRead)
            {
                isFailed = true;
            }
        }
        catch (Exception ec)
        {
            isFailed = true;
            Log.i("ImageDownloader", "startDownloadingImageInResolution() failed: " + ec);
        }
        finally
        {
            if((filesize != totalRead) || isFailed)
            {
                Log.i("ImageDownloader", "startDownloadingImageInResolution() deleting");
                if(!file.delete())
                {
                    Log.i("ImageDownloader", "startDownloadingImageInResolution() WARNING NOT DELETED FILE: " + filesize + "!=" + totalRead + "  boolean " + isFailed);
                }
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = imageRequestBuilder.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        return !isFailed;
    }

    public static boolean downloadSticker(Context context,
                                          HandleStickerDisplayingFlow handleStickerDisplayingFlow,
                                          final StickerRequest stickerRequest,
                                          File file)
    {
        boolean isFailed = false;
        long filesize = -1;
        int totalRead = 0;

        try
        {
            final ObjectAnimator objectAnimatorJump = progressBarJump(handleStickerDisplayingFlow, stickerRequest);

            SocketResources resources = new SocketResources();

            JSONObject json = new JSONObject();
            json.put("PLSC", "PLDS");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("STID", stickerRequest.OBJECT_ID);

            handleStickerDisplayingFlow.checkTaskNotActual(stickerRequest.viewAware, stickerRequest.OBJECT_ID);

            SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(json.toString());
            writer.flush();

            filesize = Long.parseLong(reader.readLine());

            DataInputStream dateiInputStream = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read = 0;
            totalRead = 0;
            long remaining = filesize;


            cancelJump(stickerRequest,
                    objectAnimatorJump);


            int progess = 0;
            while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
            {
                if(progess < 75) handleStickerDisplayingFlow.checkTaskNotActual(stickerRequest.viewAware, stickerRequest.OBJECT_ID);

                totalRead += read;
                remaining -= read;

                progess = (int) ((totalRead * 100) / filesize);

                postProgressUpdate(handleStickerDisplayingFlow, stickerRequest, progess);

                fileOutputStream.write(buffer, 0, read);
            }


            dateiInputStream.close();
            fileOutputStream.close();
            reader.close();
            writer.close();

            if(filesize != totalRead)
            {
                isFailed = true;
            }
        }
        catch (Exception ec)
        {
            Log.i("ImageDownloader", "downloadSticker() failed: " + ec);
            isFailed = true;
        }
        finally
        {
            if((filesize != totalRead) || isFailed)
            {
                Log.i("ImageDownloader", "startDownloadingSticker() deleting");
                if(!file.delete())
                {
                    Log.i("ImageDownloader", "startDownloadingSticker() WARNING NOT DELETED FILE: " + filesize + "!=" + totalRead + "  boolean " + isFailed);
                }
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = stickerRequest.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        return !isFailed;
    }

/*
    private Bitmap startDownloadingImageInResolutionLifeCloud(final GlobalImageLoading globalImageLoading, File file, boolean serverCanCrop)
    {
        long filesize = -1;
        int totalRead = 0;

        String ACCESS_KEY = file.getName();
        try
        {
            synchronized (synchObjectImage)
            {
                if(listPreventMultiThreadingFileWriting.contains(ACCESS_KEY))
                {
                    return null;
                }

                file.setReadable(false);
                listPreventMultiThreadingFileWriting.add(ACCESS_KEY);
            }

            SocketResources resources = new SocketResources();
            final ObjectAnimator animY = ObjectAnimator.ofFloat(globalImageLoading.progressBar, "translationY", -100f, 0f);
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = globalImageLoading.progressBar;
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
            });

            Bitmap bitmap = null;

            JSONObject json = new JSONObject();
            json.put("LCS", "LCGPD");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("PID", globalImageLoading.OBJECT_ID);
            int viewWidth = globalImageLoading.imageViewAware.getWidth();
            int viewHeight = globalImageLoading.imageViewAware.getHeight();
            if(viewHeight <= 0 || viewWidth <= 0)
                return null;
            json.put("VW", viewWidth);
            json.put("VH", viewHeight);
            json.put("CR", serverCanCrop);

            if(wasImageViewReusedPrimary(globalImageLoading.imageViewAware, globalImageLoading.OBJECT_ID))
                return null;

            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context, resources.getServerAddress(), resources.getServerPortLCServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(json.toString());
            writer.flush();


            filesize = Long.parseLong(reader.readLine());
            DataInputStream dateiInputStream = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read = 0;
            totalRead = 0;
            long remaining = filesize;

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = globalImageLoading.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.clearAnimation();
                        progressBar.animate().cancel();
                        animY.cancel();
                    }
                }
            });

            while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
            {
                if(wasImageViewReusedPrimary(globalImageLoading.imageViewAware, globalImageLoading.OBJECT_ID))
                    break;

                totalRead += read;
                remaining -= read;

                final int newProgress = (int) ((totalRead * 100) / filesize);
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ProgressBar progressBar = globalImageLoading.progressBar;
                        if(progressBar != null)
                        {
                            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), newProgress);
                            animation.setDuration(100);
                            animation.setInterpolator(new DecelerateInterpolator());
                            animation.start();
                        }
                    }
                });

                fileOutputStream.write(buffer, 0, read);
            }

            dateiInputStream.close();
            fileOutputStream.close();

            if(filesize == totalRead)
            {
                file.setReadable(true);
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(file)));
                if(bitmap == null)
                {
                    file.delete();
                }
            }

            reader.close();
            writer.close();
            return bitmap;
        }
        catch (Exception ec)
        {
            Log.i("ImageDownloader", "Runable-startDownloadingImageInResolutionLifeCloud() failed: " + ec);

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(globalImageLoading.esaphGlobalDownloadListener != null)
                    {
                        globalImageLoading.esaphGlobalDownloadListener.onFailed(globalImageLoading.OBJECT_ID);
                    }
                }
            });

            return null;
        }
        finally
        {
            if(filesize != totalRead)
            {
                file.delete();
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(globalImageLoading.esaphGlobalDownloadListener != null)
                        {
                            globalImageLoading.esaphGlobalDownloadListener.onFailed(globalImageLoading.OBJECT_ID);
                        }
                    }
                });
            }


            synchronized (synchObjectImage)
            {
                file.setReadable(true);
                listPreventMultiThreadingFileWriting.remove(ACCESS_KEY); //NO synchronisation needed i think, because there is only one thread that accessing this? But another can access top?
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = globalImageLoading.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    private File startDownloadingVideoLifeCloud(final GlobalImageLoading globalImageLoading, File fileVideoSavingPlace)
    {
        long filesize = -1;
        int totalRead = 0;

        String ACCESS_KEY = fileVideoSavingPlace.getName();
        synchronized (synchObjectVideo)
        {
            if(listPreventMultiThreadingFileWriting.contains(ACCESS_KEY))
            {
                return null;
            }

            fileVideoSavingPlace.setReadable(false);
            listPreventMultiThreadingFileWriting.add(ACCESS_KEY);
        }


        try
        {
            final ObjectAnimator animY = ObjectAnimator.ofFloat(globalImageLoading.progressBar, "translationY", -100f, 0f);
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = globalImageLoading.progressBar;
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
            });

            JSONObject json = new JSONObject();
            json.put("LCS", "LCGPD");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("PID", globalImageLoading.OBJECT_ID);
            json.put("QU", 1); //1 meaning here to download the video file, 0 means is for thumpnail of the video.

            if(wasImageViewReusedPrimary(globalImageLoading.videoViewAware, globalImageLoading.OBJECT_ID))
                return null;

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortLCServer());
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

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ProgressBar progressBar = globalImageLoading.progressBar;
                        if(progressBar != null)
                        {
                            progressBar.clearAnimation();
                            progressBar.animate().cancel();
                            animY.cancel();
                        }
                    }
                });

                while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
                {
                    if(wasImageViewReusedPrimary(globalImageLoading.videoViewAware, globalImageLoading.OBJECT_ID))
                        break;

                    totalRead += read;
                    remaining -= read;

                    final int newProgress = (int) ((totalRead * 100) / filesize);
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ProgressBar progressBar = globalImageLoading.progressBar;
                            if(progressBar != null)
                            {
                                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), newProgress);
                                animation.setDuration(100);
                                animation.setInterpolator(new DecelerateInterpolator());
                                animation.start();
                            }
                        }
                    });

                    fileOutputStream.write(buffer, 0, read);
                }
                dateiInputStream.close();
                fileOutputStream.close();

                if(totalRead != filesize)
                {
                    return null;
                }

                reader.close();
                writer.close();
                socket.close();
            }
        }
        catch (Exception ec)
        {
            Log.i("ImageDownloader", "Runable-startDownloadingVideoLifeCloud() failed: " + ec);
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(globalImageLoading.esaphGlobalDownloadListener != null)
                    {
                        globalImageLoading.esaphGlobalDownloadListener.onFailed(globalImageLoading.OBJECT_ID);
                    }
                }
            });

            fileVideoSavingPlace.delete();
            return null;
        }
        finally
        {
            if(totalRead != filesize)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(globalImageLoading.esaphGlobalDownloadListener != null)
                        {
                            globalImageLoading.esaphGlobalDownloadListener.onFailed(globalImageLoading.OBJECT_ID);
                        }
                    }
                });

                fileVideoSavingPlace.delete();
            }

            synchronized (synchObjectVideo)
            {
                fileVideoSavingPlace.setReadable(true);
                listPreventMultiThreadingFileWriting.remove(ACCESS_KEY);
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    ProgressBar progressBar = globalImageLoading.progressBar;
                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        return fileVideoSavingPlace;
    }
    */


    private static void cancelJump(final BaseRequest baseRequest,
                                   final ObjectAnimator objectAnimator)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                ProgressBar progressBar = baseRequest.progressBar;
                if(progressBar != null)
                {
                    progressBar.clearAnimation();
                    progressBar.animate().cancel();
                    objectAnimator.cancel();
                }
            }
        });
    }


    private static ObjectAnimator progressBarJump(final LoadingAndDisplayBase loadingAndDisplayBase,
                                        final BaseRequest baseRequest)
    {
        final ObjectAnimator animY = ObjectAnimator.ofFloat(baseRequest.progressBar, "translationY", -100f, 0f);
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    loadingAndDisplayBase.checkTaskNotActual(baseRequest.viewAware, baseRequest.OBJECT_ID);

                    ProgressBar progressBar = baseRequest.progressBar;
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

    private static void postProgressUpdate(final LoadingAndDisplayBase loadingAndDisplayBase,
                                    final BaseRequest baseRequest,
                                    final int value)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    loadingAndDisplayBase.checkTaskNotActual(baseRequest.viewAware, baseRequest.OBJECT_ID);
                    ProgressBar progressBar = baseRequest.progressBar;
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
