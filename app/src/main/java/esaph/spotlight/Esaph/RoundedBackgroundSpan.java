package esaph.spotlight.Esaph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class RoundedBackgroundSpan extends ReplacementSpan implements LineHeightSpan
{
    private static final int CORNER_RADIUS = DisplayUtils.dp2px(25);
    private static final int PADDING_X = 10;

    private int   mBackgroundColor;
    private int   mTextColor;

    public RoundedBackgroundSpan() {
    }


    public RoundedBackgroundSpan setmBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        return this;
    }

    public RoundedBackgroundSpan setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        return this;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (PADDING_X + paint.measureText(text,start, end) + PADDING_X);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        float width = paint.measureText(text,start, end);
        RectF rect = new RectF(x, top, x + width + 2 * PADDING_X, bottom);
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x + PADDING_X, y, paint);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fontMetricsInt) {
    }
}

