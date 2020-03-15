/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphColorPicker extends View
{
    private Paint paint;
    private Paint strokePaint;
    private Path path;
    private Bitmap bitmap;
    private int viewWidth;
    private int viewHeight;
    private int centerX;
    private float colorPickerRadius;
    private OnColorChangeListener onColorChangeListener;
    private RectF colorPickerBody;
    private float selectorYPos;
    private int borderColor;
    private float borderWidth = 5f;
    private int[] mColors;
    private boolean cacheBitmap = true;

    public EsaphColorPicker(Context context)
    {
        super(context);
        init();
    }

    public EsaphColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EsaphColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(21)
    public EsaphColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setWillNotDraw(false);

        mColors = new int[] { //Black started
                0xFF000000,
                0xFFFF0000,
                0xFFFFAA00,
                0xFFFFFB00,
                0xFF11FF00,
                0xFF00FFFB,
                0xFF000DFF,
                0xFFCC00FF,
                0xFFFF00AE,
                0xFFFFFFFF //White
        };

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        path = new Path();

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(borderColor);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(borderWidth);

        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.addCircle(centerX, borderWidth + colorPickerRadius, colorPickerRadius, Path.Direction.CW);
        path.addRect(colorPickerBody, Path.Direction.CW);
        path.addCircle(centerX, viewHeight - (borderWidth + colorPickerRadius), colorPickerRadius, Path.Direction.CW);

        canvas.drawPath(path, strokePaint);
        canvas.drawPath(path, paint);

        if (cacheBitmap) {
            bitmap = getDrawingCache();
            cacheBitmap = false;
            invalidate();
        } else {
            canvas.drawLine(colorPickerBody.left, selectorYPos, colorPickerBody.right, selectorYPos, strokePaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float yPos = Math.min(event.getY(), colorPickerBody.bottom);
        yPos = Math.max(colorPickerBody.top, yPos);

        selectorYPos = yPos;
        try
        {
            if(bitmap != null)
            {
                int selectedColor = bitmap.getPixel(viewWidth/2, (int) selectorYPos);

                if (onColorChangeListener != null) {
                    onColorChangeListener.onColorChange(selectedColor);
                }

                invalidate();
            }
        }
        catch (Exception ec)
        {
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w - (DisplayUtils.dp2px(30));
        viewHeight = h;

        centerX = viewWidth/2;
        colorPickerRadius = (viewWidth/2) - borderWidth;

        colorPickerBody = new RectF(
                centerX - colorPickerRadius,
                borderWidth + colorPickerRadius,
                centerX + colorPickerRadius,
                viewHeight - (borderWidth + colorPickerRadius));

        LinearGradient gradient = new LinearGradient(0, colorPickerBody.top, 0, colorPickerBody.bottom, mColors, null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);

        resetToDefault();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public void setmColors(int[] mColors) {
        this.mColors = mColors;
        cacheBitmap = true;
        invalidate();
    }

    public void resetToDefault()
    {
        selectorYPos = borderWidth + colorPickerRadius;

        if (onColorChangeListener != null)
        {
            onColorChangeListener.onColorChange(Color.RED);
        }

        invalidate();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener)
    {
        this.onColorChangeListener = onColorChangeListener;
        resetToDefault();
    }

    public interface OnColorChangeListener {

        void onColorChange(int selectedColor);
    }


}


