package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceGraphic;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

public class FilterSmileys extends FaceGraphic
{
    private TextPaint textPaint;
    private String mSmiley;

    public FilterSmileys(String mSmiley)
    {
        super();
        this.mSmiley = mSmiley;
        this.textPaint = new TextPaint();

        this.textPaint.setColor(Color.BLACK);
    }

    @Override
    public void draw(GraphicOverlay graphicOverlay, Canvas canvas)
    {
        RectF rectF = getFaceRect();

        /*
        this.textPaint.setTextSize(getTextSizeForViewWidht(textPaint, rectF.right - rectF.right / 2, mSmiley));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
*/
        int xPos = (int) (rectF.right / 2);
        int yPos = (int) ((rectF.bottom / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));


        float width = rectF.right - rectF.left; // define a width which should be achieved
        textPaint.setTextSize(100); // set a text size surely big enough
        Rect r = new Rect();
        textPaint.getTextBounds(mSmiley, 0, mSmiley.length(), r ); // measure the text with a random size
        float fac = width / r.width(); // compute the factor, which will scale the text to our target width
        textPaint.setTextSize( textPaint.getTextSize() * fac );
        textPaint.getTextBounds( mSmiley, 0, mSmiley.length(), r ); // now final measurement: whats the real width?


        canvas.drawText(mSmiley, rectF.left, rectF.bottom, textPaint);
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
        // Set the paint for that size.
        return testTextSize * desiredWidth / bounds.width();
    }


}
