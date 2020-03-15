package esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Loader.CanvasLoader;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;

public class CanvasSpotGeneratorStatic
{

    private static float getTextSizeForViewWidht(Paint paint, float desiredWidth, String text)
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



    public static final int PADDING_TOP_BOTTOM = DisplayUtils.dp2px(10); //Double it for 5 dp top and 5 dp bottom.
    private static void canvasDrawText(short TextAlignment, Canvas canvas, Paint paint, String text, EsaphDimension esaphDimension)
    {
        TextPaint textPaint = new TextPaint(paint);
        StaticLayout myStaticLayout = new StaticLayout(text,
                textPaint,
                canvas.getWidth(),
                SpotTextAlignment.getAlignmentForApiVersion(TextAlignment),
                1,
                0,
                false);

        int PADDED_IMAGE_HEIGHT = esaphDimension.getHeight() - CanvasLoader.PADDING_TOP_BOTTOM;

        float height = myStaticLayout.getHeight();
        float translate = (float) (PADDED_IMAGE_HEIGHT - height) / 2;
        canvas.translate(0, translate);

        myStaticLayout.draw(canvas);
    }



    public static Bitmap createText(Context context,
                                    ConversationMessage conversationMessage,
                                    boolean autoTextSize,
                                    EsaphDimension esaphDimension) //Theres where the magic happens. Saving about 1000000% of stupid image space.
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

            if(autoTextSize)
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
                    text,
                    esaphDimension);

            //END TEXT SECTION
            return tempBitmap;
        }
        catch (Exception ec)
        {
            Log.i(CanvasSpotGeneratorStatic.class.getName(), "CanvasSpotGeneratorStatic createText failed() - create: " + ec);
            return null;
        }
    }

    public static Bitmap mark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());



        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);

        Rect bounds = new Rect();
        paint.getTextBounds(watermark, 0, watermark.length(), bounds);
        int height = bounds.height();

        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, location.x, height + location.y, paint);

        return result;
    }
}
