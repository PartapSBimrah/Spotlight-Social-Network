/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphColorableEdittext extends AppCompatEditText
{
    private Paint paintBackground;
    private Paint paintStroke;
    private int strokeColor = 0xFFEBEBEB;
    private int RADIUS = DisplayUtils.dp2px(20);
    private int colorPaintBackground;

    public EsaphColorableEdittext(Context context) {
        super(context);
        init(context);
    }

    public EsaphColorableEdittext(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public EsaphColorableEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(Color.WHITE);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(DisplayUtils.dp2px(2f));
        paintStroke.setColor(strokeColor);
    }

    public void setPaintBackgroundColor(int color)
    {
        this.colorPaintBackground = color;
        paintBackground.setColor(this.colorPaintBackground);
        invalidate();
    }

    public int getPaintBackgroundColor()
    {
        return colorPaintBackground;
    }

    private RectF rectF;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawRoundRect(rectF, RADIUS, RADIUS, paintBackground);
        canvas.drawRoundRect(rectF, RADIUS, RADIUS, paintStroke);
        super.onDraw(canvas);
    }
}
