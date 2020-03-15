/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphPositionTabView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphColorTransitionShaderCamera;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;

public class EsaphCameraTabJana extends View
{
    private final Paint mPaintRecording;
    private final Paint mPaintBackground;
    private final float mStartAngle = -90;      // Always start from top (default is: "3 o'clock on a watch.")
    private float mSweepAngle = 0;              // How long to sweep from mStartAngle
    private float mMaxSweepAngle = 360;         // Max degrees to sweep = full circle
    private int mStrokeWidth;             // Width of outline
    private int mAnimationDuration = 400;       // Animation duration for progress change
    private int mMaxProgress = 100;             // Max progress to use
    private boolean mRoundedCorners = true;     // Set to true if rounded corners should be applied to outline ends
    private int mProgressColor;   // Outline color
    private int DEAULT_STROKE_WIDTH = DisplayUtils.dp2px(6);

    public EsaphCameraTabJana(Context context)
    {
        super(context);
        mPaintRecording = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        init(context);
    }

    public EsaphCameraTabJana(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mPaintRecording = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        init(context);
    }

    public EsaphCameraTabJana(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mPaintRecording = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        init(context);
    }

    @RequiresApi(21)
    public EsaphCameraTabJana(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaintRecording = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        init(context);
    }

    private void init(Context context)
    {
        mStrokeWidth = DEAULT_STROKE_WIDTH;
        mProgressColor = ContextCompat.getColor(context, R.color.colorRecording);

        setAlpha(0f);
        mPaintBackground.setColor(Color.WHITE);
        mPaintBackground.setStrokeWidth(mStrokeWidth);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);

        mPaintRecording.setColor(mProgressColor);
        mPaintRecording.setStrokeWidth(mStrokeWidth);
        mPaintRecording.setAntiAlias(true);
        mPaintRecording.setStyle(Paint.Style.STROKE);
        mPaintRecording.setStrokeCap(Paint.Cap.ROUND);
    }

    public EsaphColorTransitionShaderCamera currentShaderColorTransition = new EsaphColorTransitionShaderCamera();

    private static final float  CIRLCE_DISTANCE_ONE = 0.6f;

    private int mViewWidth;
    private int mViewHeight;

    private boolean shaderEnabled = false;

    public void enableShader(boolean shaderEnabled)
    {
        this.shaderEnabled = shaderEnabled;
        invalidate();
    }

    public void setNewShaderColors(int[] colors)
    {
        this.currentShaderColorTransition.setNewShaderColors(colors);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if(currentShaderColorTransition != null && shaderEnabled)
        {
            currentShaderColorTransition.onDrawShader(mPaintRecording);
        }

        mViewWidth = getWidth() / 2;
        mViewHeight = getHeight() / 2;

        if(isRecording)
        {
            initMeasurments();
            drawOutlineArc(canvas);
        }
        else
        {
            canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth * EsaphCameraTabJana.CIRLCE_DISTANCE_ONE, mPaintBackground);
        }

        if(currentShaderColorTransition != null && shaderEnabled)
        {
            postInvalidateDelayed(100);
        }
    }

    public void setStrokeWidth(int newWidth)
    {
        mStrokeWidth = newWidth;
        invalidate();
    }

    private boolean isRecording = false;
    public void setIsRecording(boolean isRecording)
    {
        this.isRecording = isRecording;
        invalidate();
    }

    public boolean getIsRecording()
    {
        return isRecording;
    }

    private void initMeasurments()
    {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void drawOutlineArc(Canvas canvas)
    {
        final int diameter = Math.min(mViewWidth, mViewHeight) - (mStrokeWidth * 2);
        final RectF outerOval = new RectF(mStrokeWidth, mStrokeWidth, diameter, diameter);

        mPaintRecording.setColor(mProgressColor);
        mPaintRecording.setStrokeWidth(mStrokeWidth);
        mPaintRecording.setAntiAlias(true);
        mPaintRecording.setStrokeCap(mRoundedCorners ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        mPaintRecording.setStyle(Paint.Style.STROKE);

        canvas.drawArc(outerOval, mStartAngle, 360, false, mPaintBackground);
        canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, mPaintRecording);
    }

    private float calcSweepAngleFromProgress(int progress) {
        return (mMaxSweepAngle / mMaxProgress) * progress;
    }

    private int calcProgressFromSweepAngle(float sweepAngle) {
        return (int) ((sweepAngle * mMaxProgress) / mMaxSweepAngle);
    }

    /*
     * Set progress of the circular progress bar.
     * @param progress progress between 0 and 100.
     */
    public void setProgress(int progress) {
        ValueAnimator animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(mAnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mSweepAngle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }


    public static final float STATE_SHOW = 1f;
    public static final float STATE_HIDE = 0f;

    public void setState(float STATE)
    {
        animate().setDuration(50).alpha(STATE).start();

        /*
        if(STATE == EsaphCameraTabJana.STATE_SHOW)
        {
            animate().setDuration(100).scaleX(1.3f).scaleY(1.3f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    animate().setDuration(100).scaleX(1.0f).scaleY(1.0f).start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animate().setDuration(100).scaleX(1.0f).scaleY(1.0f).start();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
        */
    }
}
