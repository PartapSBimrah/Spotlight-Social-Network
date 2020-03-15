package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Loader.CanvasLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.cache.memory.EsaphImageLoaderMemoryCache;

public class EsaphGlobalImageLoader //CLASS LOADING FOR VIEWPAGER AND RECYLERVIEW, needs to be implemented by activity
{
    private volatile static EsaphGlobalImageLoader esaphGlobalImageLoader;

    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageLoaderEngine engine;
    private EsaphImageLoaderMemoryCache esaphImageLoaderMemoryCache;

    private EsaphGlobalImageLoader(Context context)
    {
        this.engine = new ImageLoaderEngine(context);
        this.esaphImageLoaderMemoryCache = new EsaphImageLoaderMemoryCache();
        this.context = context;
    }

    public EsaphImageLoaderMemoryCache getEsaphImageLoaderMemoryCache()
    {
        return esaphImageLoaderMemoryCache;
    }

    public static EsaphGlobalImageLoader with(Context context)
    {
        if (esaphGlobalImageLoader == null)
        {
            synchronized (EsaphGlobalImageLoader.class)
            {
                if (esaphGlobalImageLoader == null)
                {
                    esaphGlobalImageLoader = new EsaphGlobalImageLoader(context);
                }
            }
        }
        return esaphGlobalImageLoader;
    }

    public Handler getHandler()
    {
        return handler;
    }

    public interface ResourceReadyListener
    {
        void onResourceReady(Bitmap bitmap);
    }

    public void canvasMode(CanvasRequest.CanvasRequestBuilder canvasReqeustBuilder)
    {
        final CanvasRequest canvasRequest = canvasReqeustBuilder.build(engine.getLockForID(Long.toString(canvasReqeustBuilder.getConversationMessage().getMESSAGE_ID())));
        engine.prepareDisplayTaskFor(canvasRequest.imageViewAware, canvasRequest.OBJECT_ID);

        canvasRequest.imageViewAware.getWrappedView().post(new Runnable() {
            @Override
            public void run()
            {
                Bitmap bitmapPostBild = esaphImageLoaderMemoryCache.getDataFromKey(canvasRequest.OBJECT_ID
                                + canvasRequest.conversationMessage.getEsaphPloppInformationsJSON().toString().hashCode(),
                        canvasRequest.imageViewAware.getResolution());

                if(bitmapPostBild != null) //Hochgeladenen foto
                {
                    canvasRequest.imageViewAware.setImageBitmap(bitmapPostBild);
                }
                else
                {
                    engine.submit(new CanvasLoader(context,
                            engine,
                            EsaphGlobalImageLoader.this,
                            canvasRequest),
                            canvasRequest.imageViewAware.getResolution());
                }
            }
        });
    }

    public void displayVideo(VideoRequest.VideoRequestBuilder videoRequestBuilder) //Use only for big view, when you really need to display a video.
    {
        VideoRequest request = videoRequestBuilder.build(engine.getLockForID(videoRequestBuilder.getOBJECT_ID()));

        engine.prepareDisplayTaskFor(request.videoViewAware, request.OBJECT_ID);
        engine.submit(new HandleVideoDisplayingFlow(request,
                engine,
                handler), new EsaphDimension(0,0));
    }

    public void displayImage(ImageRequest.ImageRequestBuilder imageRequestBuilder)
    {
        final ImageRequest request = imageRequestBuilder.build(engine.getLockForID(imageRequestBuilder.getOBJECT_ID()));
        engine.prepareDisplayTaskFor(request.imageViewAware, request.OBJECT_ID);

        request.imageViewAware.getWrappedView().post(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmapPostBild = esaphImageLoaderMemoryCache.getDataFromKey(request.OBJECT_ID, request.imageViewAware.getResolution());

                if(bitmapPostBild != null) //Hochgeladenen foto
                {
                    request.imageViewAware.setImageBitmap(bitmapPostBild);
                }
                else
                {
                    engine.submit(new HandleImageDisplayingFlow(request, engine, new Handler(Looper.getMainLooper())),
                            request.imageViewAware.getResolution());

                    Drawable icon = null;
                    try
                    {
                        icon = ContextCompat.getDrawable(context, request.placeHolderId);
                    }
                    catch (Exception ec)
                    {
                    }
                    finally
                    {
                        if(icon != null)
                        {
                            request.imageViewAware.setImageDrawable(icon);
                        }
                    }
                }
            }
        });
    }


    public void displayImage(StickerRequest.StickerRequestBuilder stickerRequestBuilder)
    {
        final StickerRequest request = stickerRequestBuilder.build(engine.getLockForID(stickerRequestBuilder.getOBJECT_ID()));
        engine.prepareDisplayTaskFor(request.imageViewAware, request.OBJECT_ID);

        request.imageViewAware.getWrappedView().post(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmapPostBild = esaphImageLoaderMemoryCache.getDataFromKey(request.OBJECT_ID, request.imageViewAware.getResolution());

                if(bitmapPostBild != null) //Hochgeladenen foto
                {
                    request.imageViewAware.setImageBitmap(bitmapPostBild);
                }
                else
                {
                    engine.submit(new HandleStickerDisplayingFlow(request, engine, new Handler(Looper.getMainLooper())),
                            request.imageViewAware.getResolution());

                    Drawable icon = null;
                    try
                    {
                        icon = ContextCompat.getDrawable(context, request.placeHolderId);
                    }
                    catch (Exception ec)
                    {
                    }
                    finally
                    {
                        if(icon != null)
                        {
                            request.imageViewAware.setImageDrawable(icon);
                        }
                    }
                }
            }
        });
    }

}