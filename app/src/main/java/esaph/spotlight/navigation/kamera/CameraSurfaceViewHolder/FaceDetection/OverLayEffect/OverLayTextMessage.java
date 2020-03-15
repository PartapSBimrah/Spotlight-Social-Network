/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.OverLayEffect;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

public class OverLayTextMessage extends OverLayEffect
{
    private static final int DEFAULT_TEXT_SIZE = DisplayUtils.dp2px(36);
    private String Message;
    public OverLayTextMessage(String Message)
    {
        this.Message = Message;
    }

    private int drawingCounter = 0;

    @Override
    public void draw(GraphicOverlay graphicOverlay, Canvas canvas)
    {
        super.draw(graphicOverlay, canvas);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(OverLayTextMessage.DEFAULT_TEXT_SIZE);
        textPaint.setAlpha(alpha);

        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.getTextBounds(Message, 0, Message.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(Message, x, y, textPaint);

        System.out.println("OverLayTextMessage: DRAWING STEP " + drawingCounter + "---" + graphicOverlay.getHeight());
        System.out.println("OverLayTextMessage: Animating " + alpha);
        drawingCounter++;
    }



    private int alpha = 255;

    @Override
    public ValueAnimator getAnimator()
    {
        System.out.println("OverLayTextMessage: Get animator");
        PropertyValuesHolder propertyAlpha = PropertyValuesHolder.ofInt("ALPHA",255, 0);

        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertyAlpha);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                alpha = (int) animation.getAnimatedValue("ALPHA");
                draw(getGraphicOverlay(), getCanvas());
            }
        });

        return animator;
    }
}
