package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.cache.memory.EsaphImageLoaderMemoryCache;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphImageDecodingHelper;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class EsaphGlobalProfilbildLoader //CLASS LOADING FOR VIEWPAGER AND RECYLERVIEW, needs to be implemented by activity
{
    private static EsaphGlobalProfilbildLoader esaphGlobalProfilbildLoader;

    private Context context;
    private EsaphImageLoaderMemoryCache esaphGlobalMemoryPreviewCache;
    private Map<Object, String> imageViewsPrimary = Collections.synchronizedMap(new WeakHashMap<Object, String>()); //Bild das der Nutzer gepostet hat.
    private ExecutorService executorService;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<String> listPreventMultiThreadingFileWriting = new ArrayList<>();

    private EsaphGlobalProfilbildLoader(Context context)
    {
        this.context = context;
        this.esaphGlobalMemoryPreviewCache = new EsaphImageLoaderMemoryCache();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }


    public static EsaphGlobalProfilbildLoader with(Context context)
    {
        if(EsaphGlobalProfilbildLoader.esaphGlobalProfilbildLoader == null)
            esaphGlobalProfilbildLoader = new EsaphGlobalProfilbildLoader(context);


        return EsaphGlobalProfilbildLoader.esaphGlobalProfilbildLoader;
    }

    public void invalidateCaches()
    {
        esaphGlobalMemoryPreviewCache.clearCache();
    }


    public void displayProfilbild(ImageView imageView,
                                  ProgressBar progressBar,
                                  long UID, //Passing -1, = no pb
                                  int esaphImageLoaderDisplayingAnimation,
                                  int noDataPlaceHolderId,
                                  String FOLDER)
    {
        EsaphDimension esaphDimension = new EsaphDimension(imageView.getWidth(), imageView.getHeight());

        if(UID >= 0)
        {
            imageView.setTag(UID);
            imageViewsPrimary.put(imageView, Long.toString(UID));

            Bitmap bitmapPostBild = esaphGlobalMemoryPreviewCache.getDataFromKey(Long.toString(UID), esaphDimension);

            if(bitmapPostBild != null) //Hochgeladenen foto
            {
                imageView.setImageBitmap(bitmapPostBild);
            }
            else
            {
                final GlobalProfilBildLoading globalProfilBildLoading = new GlobalProfilBildLoading(imageView,
                        Long.toString(UID),
                        progressBar,
                        esaphDimension,
                        esaphImageLoaderDisplayingAnimation,
                        FOLDER);

                imageView.post(new Runnable() //START ONLY WHEN VIEW IS READY.
                {
                    @Override
                    public void run()
                    {
                        executorService.submit(new HandleProfilbildDisplayingFlow(globalProfilBildLoading));
                    }
                });

                Drawable icon = null;
                try
                {
                    icon = ContextCompat.getDrawable(context, noDataPlaceHolderId);
                }
                catch (Exception ec)
                {
                }
                finally
                {
                    if(icon != null)
                    {
                        imageView.setImageDrawable(icon);
                    }
                }
            }
        }
        else
        {
            imageView.setTag(null);
            Drawable icon = null;
            try
            {
                icon = ContextCompat.getDrawable(context, noDataPlaceHolderId);
            }
            catch (Exception ec)
            {
            }
            finally
            {
                if(icon != null)
                {
                    imageView.setImageDrawable(icon);
                }
            }
        }

    }
    

    private class GlobalProfilBildLoading
    {
        public ImageView imageView;
        public String PBPID;
        public String FOLDER;
        public ProgressBar progressBar;
        public EsaphDimension RESOLUTION;
        public int esaphImageLoaderDisplayingAnimation;
        
        public GlobalProfilBildLoading(ImageView imageView, String PBPID, ProgressBar progressBar, EsaphDimension RESOLUTION, int esaphImageLoaderDisplayingAnimation, String FOLDER)
        {
            this.imageView = imageView;
            this.PBPID = PBPID;
            this.progressBar = progressBar;
            this.RESOLUTION = RESOLUTION;
            this.esaphImageLoaderDisplayingAnimation = esaphImageLoaderDisplayingAnimation;
            this.FOLDER = FOLDER;
        }
    }

    public class BlurBuilder
    {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 20.0f;

        public Bitmap blur(Context context, Bitmap image)
        {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);

            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            intrinsicBlur.setRadius(BLUR_RADIUS);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    private boolean wasImageViewReusedPrimary(ImageView imageView, String UID)
    {
        String tag = imageViewsPrimary.get(imageView);

        if(tag == null || !tag.equals(UID))
        {
            return true;
        }
        return false;
    }

    private class GlobalDisplay implements Runnable
    {
        private GlobalProfilBildLoading globalImageLoading;
        private Bitmap bitmap;

        private GlobalDisplay(GlobalProfilBildLoading globalImageLoading, Bitmap bitmap)
        {
            this.globalImageLoading = globalImageLoading;
            this.bitmap = bitmap;
        }

        @Override
        public void run()
        {
            try
            {
                if(wasImageViewReusedPrimary(globalImageLoading.imageView, globalImageLoading.PBPID))
                    return;

                if(this.bitmap == null) //bitmap should not be null.
                    return;

                if(globalImageLoading.imageView != null)
                {
                    if(wasImageViewReusedPrimary(globalImageLoading.imageView, globalImageLoading.PBPID))
                        return;

                    globalImageLoading.imageView.setImageBitmap(this.bitmap);
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "GlobalDisplay failed: " + ec);
            }
        }
    }
    
    private final Object synchObjectImage = new Object();
    private Bitmap startDownloadingImageInResolution(final GlobalProfilBildLoading globalImageLoading, File file, boolean serverCanCrop)
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
            json.put("PLSC", "PLGPB");
            json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            json.put("PID", globalImageLoading.PBPID);
            int viewWidth = globalImageLoading.imageView.getWidth();
            int viewHeight = globalImageLoading.imageView.getHeight();
            if(viewHeight <= 0 || viewWidth <= 0)
                return null;
            json.put("VW", viewWidth);
            json.put("VH", viewHeight);
            json.put("CR", serverCanCrop);

            if(wasImageViewReusedPrimary(globalImageLoading.imageView, globalImageLoading.PBPID))
                return null;

            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context, resources.getServerAddress(), resources.getServerPortPServer());
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
                if(wasImageViewReusedPrimary(globalImageLoading.imageView, globalImageLoading.PBPID))
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
            Log.i(getClass().getName(), "startDownloadingImageInResolution() failed: " + ec);
            return null;
        }
        finally
        {
            if(filesize != totalRead)
            {
                file.setReadable(true);
                file.delete();
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


    private class HandleProfilbildDisplayingFlow implements Runnable
    {
        private GlobalProfilBildLoading globalProfilBildLoading;

        private HandleProfilbildDisplayingFlow(GlobalProfilBildLoading globalProfilBildLoading)
        {
            this.globalProfilBildLoading = globalProfilBildLoading;
        }

        private File determineBestDimension(List<File> files, EsaphDimension dimension) throws FileNotFoundException //Finding the heighest resolution. Because scaling is better than making it blur and download shit again.
        {
            BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
            bitMapOption.inJustDecodeBounds = true;

            File currentBestResolution = null;
            int lastBest = 0;

            for(File file : files)
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
            return currentBestResolution;
        }

        private boolean isImageHigher(File file, EsaphDimension dimension) throws FileNotFoundException
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


        private Bitmap scaleBitmap(Bitmap bitmap)
        {
            if(bitmap != null)
            {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if(width == this.globalProfilBildLoading.RESOLUTION.getWidth() && height == this.globalProfilBildLoading.RESOLUTION.getHeight())
                {
                    return bitmap;
                }

                float bitmapRatio = (float) width / (float) height;
                if (bitmapRatio > 1)
                {
                    width = this.globalProfilBildLoading.RESOLUTION.getWidth();
                    height = (int) (width / bitmapRatio);
                }
                else
                {
                    height = this.globalProfilBildLoading.RESOLUTION.getHeight();
                    width = (int) (height * bitmapRatio);
                }

                return Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            return null;
        }

        private Bitmap handleSavedFilesImage(File fileSaveTo) throws IOException
        {
            List<File> listFilesFound = StorageHandlerProfilbild.getFilesSamePID(context, globalProfilBildLoading.PBPID, globalProfilBildLoading.FOLDER);
            if(!listFilesFound.isEmpty())
            {
                File bestImageResolutionForCurrentView = determineBestDimension(listFilesFound, globalProfilBildLoading.RESOLUTION);
                if(bestImageResolutionForCurrentView != null)
                {
                    if(isImageHigher(bestImageResolutionForCurrentView, globalProfilBildLoading.RESOLUTION))
                    {
                        Bitmap bitmap = scaleBitmap(BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(bestImageResolutionForCurrentView))));
                        StorageHandlerProfilbild.saveToResolutions(context, bitmap, fileSaveTo);
                        return bitmap;
                    }
                    else
                    {
                        Bitmap bitmapBlur = new BlurBuilder().blur(context, BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(bestImageResolutionForCurrentView))));

                        if(bitmapBlur != null)
                        {
                            esaphGlobalMemoryPreviewCache.putPostBild(globalProfilBildLoading.PBPID, globalProfilBildLoading.RESOLUTION, bitmapBlur); //CACHE IS GETTING REPLACED.
                            handler.post(new GlobalDisplay(globalProfilBildLoading, bitmapBlur));
                        }

                        return startDownloadingImageInResolution(globalProfilBildLoading, fileSaveTo, true);
                    }
                }
            }
            else
            {
                return startDownloadingImageInResolution(globalProfilBildLoading, fileSaveTo, true);
            }

            return null;
        }


        @Override
        public void run()
        {
            try
            {
                if(wasImageViewReusedPrimary(globalProfilBildLoading.imageView, globalProfilBildLoading.PBPID))
                    return;

                globalProfilBildLoading.RESOLUTION = new EsaphDimension(globalProfilBildLoading.imageView.getWidth(), globalProfilBildLoading.imageView.getHeight());

                Bitmap bitmap = null;

                File file = StorageHandlerProfilbild.getFile(context, globalProfilBildLoading.PBPID, globalProfilBildLoading.RESOLUTION,
                        globalProfilBildLoading.FOLDER);

                if(StorageHandlerProfilbild.fileExists(file))
                {
                    bitmap = EsaphImageDecodingHelper.decodeFromFile(context,
                            file,
                            globalProfilBildLoading.RESOLUTION.getWidth(),
                            globalProfilBildLoading.RESOLUTION.getHeight());
                }
                else
                {
                    bitmap = handleSavedFilesImage(file);
                    if(bitmap == null)
                    {
                        bitmap = startDownloadingImageInResolution(globalProfilBildLoading, file, true);
                        if(StorageHandlerProfilbild.fileExists(file))
                        {
                            StorageHandlerProfilbild.saveToResolutions(context,
                                    bitmap,
                                    file);
                        }
                    }
                }

                if(bitmap != null)
                {
                    esaphGlobalMemoryPreviewCache.putPostBild(globalProfilBildLoading.PBPID,
                            globalProfilBildLoading.RESOLUTION,
                            bitmap);
                }

                handler.post(new GlobalDisplay(globalProfilBildLoading, bitmap));
            }
            catch (Exception ec)
            {
                Log.i(EsaphGlobalProfilbildLoader.this.getClass().getName(), "HandleProfilbildDisplayingFlow run() failed: " + ec);
            }
        }
    }

}
