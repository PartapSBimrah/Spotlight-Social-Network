/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphRoundedCirclesLogoView extends View implements EsaphColorTransitionAnimateAble
{
    private final Paint mPaint;
    private int mStrokeWidth;             // Width of outline
    private int mColor = Color.WHITE;   // Outline color

    public EsaphRoundedCirclesLogoView(Context context)
    {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphRoundedCirclesLogoView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphRoundedCirclesLogoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    @RequiresApi(21)
    public EsaphRoundedCirclesLogoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphSwipeShader esaphSwipeShader;
    private List<EsaphRoundedCirclesLogoLayoutHolder.ColorChangeCallback> listColorChangeCallback = new ArrayList<>();

    private static final float  CIRLCE_DISTANCE_ONE = 0.9f;
    private static final float  CIRLCE_DISTANCE_TWO = 0.5f;

    private int mViewWidth;
    private int mViewHeight;

    private boolean shaderEnabled = false;

    public void enableShader(boolean shaderEnabled)
    {
        this.shaderEnabled = shaderEnabled;
        invalidate();
    }

    public void setEsaphShader(EsaphSwipeShader esaphSwipeShader)
    {
        this.esaphSwipeShader = esaphSwipeShader;
        this.esaphSwipeShader.setEsaphSwipeShaderCommunication(this);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        if(esaphSwipeShader != null && shaderEnabled)
        {
            esaphSwipeShader.onDrawShader(mPaint);
        }

        mViewWidth = getWidth() / 2;
        mViewHeight = getHeight() / 2;

        canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth * EsaphRoundedCirclesLogoView.CIRLCE_DISTANCE_ONE, mPaint);
        canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth * EsaphRoundedCirclesLogoView.CIRLCE_DISTANCE_TWO, mPaint);
    }

    public void setStrokeWidth(int newWidth)
    {
        mStrokeWidth = newWidth;
        invalidate();
    }

    @Override
    public LinearGradient onCalculateGradient(int position, float positionOffset)
    {
        LinearGradient mLinearGradient = null;

        int[] colorArray = null;
        if(position == 0)
        {
            colorArray = EsaphColorSwipeTransitionHelper.applyColorFilterPositionZero(mPaint, positionOffset);
            mLinearGradient =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            getHeight(),
                            colorArray
                            ,
                            null,
                            Shader.TileMode.CLAMP);
        }
        else if(position == 1)
        {
            colorArray = EsaphColorSwipeTransitionHelper.applyColorFilterPositionOneMiddleTabIcon(mPaint, positionOffset);
            mLinearGradient =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            getHeight(),
                            colorArray
                            ,
                            null,
                            Shader.TileMode.CLAMP);
        }
        else if(position == 2)
        {
            colorArray = EsaphColorSwipeTransitionHelper.applyColorFilterPositionTwo(mPaint, positionOffset);
            mLinearGradient =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            getHeight(),
                            colorArray
                            ,
                            null,
                            Shader.TileMode.CLAMP);
        }

        if(listColorChangeCallback != null && colorArray != null)
        {
            for(EsaphRoundedCirclesLogoLayoutHolder.ColorChangeCallback colorChangeCallback : listColorChangeCallback)
            {
                colorChangeCallback.onTintChanged(colorArray[0]);
            }
        }

        return mLinearGradient;
    }

    @Override
    public void onInvalidate() {
        invalidate();
    }

    public void addColorChangedListener(EsaphRoundedCirclesLogoLayoutHolder.ColorChangeCallback colorChangeCallback)
    {
        this.listColorChangeCallback.add(colorChangeCallback);
    }
}
