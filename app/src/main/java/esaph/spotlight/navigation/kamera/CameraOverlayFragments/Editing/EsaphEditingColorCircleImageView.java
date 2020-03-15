/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Editing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class EsaphEditingColorCircleImageView extends AppCompatImageView
{
    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;

    private Paint mPaintBackground;
    private int mBackgroundColor = Color.WHITE;   // Outline color
    private int mViewWidth;
    private int mViewHeight;

    public EsaphEditingColorCircleImageView(Context context)
    {
        super(context);
        init();
    }

    public EsaphEditingColorCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EsaphEditingColorCircleImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mBorderOverlay = DEFAULT_BORDER_OVERLAY;
        init();
    }

    private void init()
    {
        super.setScaleType(SCALE_TYPE);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReady = true;

        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.FILL);

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    public void setDrawableBackgroundColor(int color)
    {
        this.mBackgroundColor = color;
        invalidate();
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType)
    {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds)
    {
        if (adjustViewBounds)
        {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mViewWidth = getWidth() / 2;
        mViewHeight = getHeight() / 2;

        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.FILL);

        canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth, mPaintBackground); //Kreis im hintergrund zeichnen.

        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable)
    {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    private void initializeBitmap()
    {
        setup();
    }

    private void setup()
    {
        if (!mReady)
        {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0)
        {
            return;
        }

        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(mBackgroundColor);
        invalidate();
    }
}
