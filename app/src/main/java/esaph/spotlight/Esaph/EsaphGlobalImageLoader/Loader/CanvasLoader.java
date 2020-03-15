/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.Loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.ImageLoaderEngine;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.LoadingAndDisplayBase;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.cache.disc.EsaphSpotFileCache;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageViewAware;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotEmojieDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;

public class CanvasLoader extends LoadingAndDisplayBase
{
    private Context context;
    private EsaphGlobalImageLoader esaphGlobalImageLoader;
    private ConversationMessage conversationMessage;
    private CanvasRequest canvasRequest;
    private ImageLoaderEngine engine;


    public CanvasLoader(Context context,
                        ImageLoaderEngine engine,
                        EsaphGlobalImageLoader esaphGlobalImageLoader,
                        CanvasRequest canvasRequest)
    {
        super(canvasRequest, engine);
        this.engine = engine;
        this.context = context;
        this.canvasRequest = canvasRequest;
        this.esaphGlobalImageLoader = esaphGlobalImageLoader;
        this.conversationMessage = canvasRequest.conversationMessage;
    }

    public boolean fileExists(String filename)
    {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists())
        {
            return false;
        }
        return true;
    }

    @Override
    public void run()
    {
        Bitmap bitmap = null;
        try
        {
            checkTaskNotActual(canvasRequest.imageViewAware, canvasRequest.OBJECT_ID);

            EsaphDimension esaphDimension = new EsaphDimension(canvasRequest.imageViewAware.getWidth(), canvasRequest.imageViewAware.getHeight());

            File fileCached = EsaphSpotFileCache.getFile(context,
                    conversationMessage.getMESSAGE_ID(),
                    esaphDimension,
                    Integer.toString(conversationMessage.getEsaphPloppInformationsJSONString().hashCode()));

            if(fileExists(fileCached.getName())) //ALREADY CACHED.
            {
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(context.openFileInput(fileCached.getName())));
                if(bitmap != null)
                {
                    esaphGlobalImageLoader.getHandler().post(new DisplayResults(bitmap, canvasRequest.imageViewAware));
                }
            }
            else //Check if other resolutions exists;
            {
                switch (conversationMessage.getType())
                {
                    case CMTypes.FTEX:
                        bitmap = createText(esaphDimension);
                        break;

                    case CMTypes.FEMO:
                        bitmap = createEmojie(esaphDimension);
                        break;

                    case CMTypes.FAUD:
                        bitmap = createAudio(esaphDimension);
                        break;
                }

                if(bitmap != null)
                {
                    esaphGlobalImageLoader.getHandler().post(new DisplayResults(bitmap, canvasRequest.imageViewAware)); //Before putting it to cache, it should start the displaying Faster.
                    esaphGlobalImageLoader.getEsaphImageLoaderMemoryCache().putPostBild(Long.toString(conversationMessage.getMESSAGE_ID()), esaphDimension, bitmap);
                    EsaphSpotFileCache.putFile(context, fileCached, bitmap);
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "CanvasLoader() failed: " + ec);
        }
        finally
        {
            EsaphGlobalImageLoader.ResourceReadyListener resourceReadyListener = canvasRequest.resourceReadyListenerWeakReference.get();
            if(resourceReadyListener != null && bitmap != null)
            {
                resourceReadyListener.onResourceReady(bitmap);
            }
        }
    }

    public Bitmap createText(EsaphDimension esaphDimension) //Theres where the magic happens. Saving about 1000000% of stupid image space.
    {
        try
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            ChatTextMessage chatTextMessage = (ChatTextMessage) conversationMessage;
            JSONObject jsonObject = conversationMessage.getEsaphPloppInformationsJSON();

            //Use esaphdimension or use evertime just screen dimensions. I dont know what should be better yet.
            //Create a new image bitmap and attach a brand new canvas to it
            Bitmap tempBitmap = Bitmap.createBitmap(esaphDimension.getWidth(), esaphDimension.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));


            //TEXT SECTION
            String text = chatTextMessage.getTextMessage();

            if(canvasRequest.autoTextSize)
            {
                paint.setTextSize(getTextSizeForViewWidht(paint, esaphDimension.getWidth(), text));
            }
            else
            {
                paint.setTextSize(SpotTextDefinitionBuilder.getTextSize(jsonObject));
            }

            if(SpotTextDefinitionBuilder.hasShader(jsonObject))
            {
                paint.setColor(0xFFFFFFFF);
                Rect r = new Rect();
                canvas.getClipBounds(r);
                int cHeight = r.height();
                int cWidth = r.width();

                EsaphShader esaphShader = SpotTextShader.getShaderForApiVersion(SpotTextDefinitionBuilder.getTextShader(jsonObject));
                esaphShader.onLayout(cHeight, cWidth);
                esaphShader.onDrawShader(paint);
            }
            else
            {
                paint.setColor(SpotTextDefinitionBuilder.getTextColor(jsonObject));
            }

            Typeface typefaceTextStyle = SpotTextFontStyle.getFontStyleForApiVersion(SpotTextDefinitionBuilder.getFontStyle(jsonObject));
            Typeface typefaceFontFamilie = SpotTextFontFamlie.getFontFamlieForApiVersion(context, SpotTextDefinitionBuilder.getFontFamily(jsonObject));

            paint.setTypeface(Typeface.create(typefaceFontFamilie, typefaceTextStyle.getStyle()));


            canvasDrawText(SpotTextDefinitionBuilder.getTextAlignment(jsonObject),
                    canvas,
                    paint,
                    text);

            //END TEXT SECTION
            return tempBitmap;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "CanvasLoader failed() - create: " + ec);
            return null;
        }
    }


    public static final int PADDING_TOP_BOTTOM = DisplayUtils.dp2px(10); //Double it for 5 dp top and 5 dp bottom.
    private void canvasDrawText(short TextAlignment, Canvas canvas, Paint paint, String text)
    {
        TextPaint textPaint = new TextPaint(paint);
        StaticLayout myStaticLayout = new StaticLayout(text,
                textPaint,
                canvas.getWidth(),
                SpotTextAlignment.getAlignmentForApiVersion(TextAlignment),
                1,
                0,
                false);

        int PADDED_IMAGE_HEIGHT = canvasRequest.imageViewAware.getHeight() - CanvasLoader.PADDING_TOP_BOTTOM;

        float height = myStaticLayout.getHeight();
        float translate = (float) (PADDED_IMAGE_HEIGHT - height) / 2;
        canvas.translate(0, translate);

        myStaticLayout.draw(canvas);
    }


    private float getTextSizeForViewWidht(Paint paint, float desiredWidth, String text)
    {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        return desiredTextSize;
    }

    public Bitmap createEmojie(EsaphDimension esaphDimension) //Theres where the magic happens. Saving about 1000000% of stupid image space.
    {
        try
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = (EsaphAndroidSmileyChatObject) conversationMessage;
            JSONObject jsonObject = conversationMessage.getEsaphPloppInformationsJSON();

            //Use esaphdimension or use evertime just screen dimensions. I dont know what should be better yet.
            //Create a new image bitmap and attach a brand new canvas to it
            Bitmap tempBitmap = Bitmap.createBitmap(esaphDimension.getWidth(), esaphDimension.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));


            //TEXT SECTION
            String text = esaphAndroidSmileyChatObject.getEsaphEmojie().getEMOJIE();

            if(canvasRequest.autoTextSize)
            {
                paint.setTextSize(getTextSizeForViewWidht(paint, esaphDimension.getWidth(), text));
            }
            else
            {
                paint.setTextSize(SpotEmojieDefinitionBuilder.getTextSize(jsonObject));
            }

            paint.setColor(Color.BLACK);

            canvasDrawText(SpotEmojieDefinitionBuilder.getTextAlignment(jsonObject),
                    canvas,
                    paint,
                    text);

            //END TEXT SECTION
            return tempBitmap;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "CanvasLoader failed() - create: " + ec);
            return null;
        }
    }

    public Bitmap createAudio(EsaphDimension esaphDimension) //Theres where the magic happens. Saving about 1000000% of stupid image space.
    {
        try
        {
            JSONObject jsonObject = conversationMessage.getEsaphPloppInformationsJSON();

            //Use esaphdimension or use evertime just screen dimensions. I dont know what should be better yet.
            //Create a new image bitmap and attach a brand new canvas to it
            Bitmap tempBitmap = Bitmap.createBitmap(esaphDimension.getWidth(), esaphDimension.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);

            canvas.drawColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));

            return tempBitmap;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "CanvasLoader failed() - create: " + ec);
            return null;
        }
    }

    private class DisplayResults implements Runnable
    {
        private Bitmap bitmap;
        private ImageViewAware imageViewAware;

        public DisplayResults(Bitmap bitmap, ImageViewAware imageViewAware)
        {
            this.bitmap = bitmap;
            this.imageViewAware = imageViewAware;
        }

        @Override
        public void run()
        {
            try
            {
                canvasRequest.imageViewAware.setImageBitmap(bitmap);
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "DisplayResults() failed: " + ec);
            }
        }
    }
}
