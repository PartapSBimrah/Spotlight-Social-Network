package esaph.spotlight.Esaph.EsaphCircleImageView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;

public class EsaphCircleImageView extends AppCompatImageView
{
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mFillPaint = new Paint();

    private int mFillColor = DEFAULT_FILL_COLOR;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;
    private boolean mDisableCircularTransformation;

    private Paint mPaintProgress;
    private Paint mPaintBackground;
    private final float mStartAngle = -90;      // Always start from top (default is: "3 o'clock on a watch.")
    private float mSweepAngle = 0;              // How long to sweep from mStartAngle
    private float mMaxSweepAngle = 360;         // Max degrees to sweep = full circle
    private int mStrokeWidth = DisplayUtils.dp2px(1);             // Width of outline
    private int mAnimationDuration = 400;       // Animation duration for progress change
    private int mMaxProgress = 100;             // Max progress to use
    private boolean mRoundedCorners = true;     // Set to true if rounded corners should be applied to outline ends

    private int mProgressColor = Color.WHITE;  // Outline color
    private int mBackgroundColor = Color.WHITE;   // Outline color
    private int mViewWidth;
    private int mViewHeight;

    public EsaphCircleImageView(Context context)
    {
        super(context);
        init();
    }

    public EsaphCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EsaphCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EsaphCircleImageView, defStyle, 0);

        mBorderOverlay = a.getBoolean(R.styleable.EsaphCircleImageView_esc_border_overlay, DEFAULT_BORDER_OVERLAY);
        mFillColor = a.getColor(R.styleable.EsaphCircleImageView_esc_fill_color, DEFAULT_FILL_COLOR);
        mBackgroundColor = a.getColor(R.styleable.EsaphCircleImageView_esc_border_color_background, mBackgroundColor);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.EsaphCircleImageView_esc_border_width, mStrokeWidth);

        a.recycle();

        init();
    }

    private void init()
    {
        super.setScaleType(SCALE_TYPE);
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReady = true;

        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setStrokeWidth(mStrokeWidth);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(styleBackground);
        mPaintProgress.setStyle(styleProgress);
        mPaintProgress.setStrokeWidth(mStrokeWidth);

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
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


    private boolean drawCircleWhenCirclularIsDisabled = false;

    public void setDrawCircleWhenCirclularIsDisabled(boolean drawCircleWhenCirclularIsDisabled) {
        this.drawCircleWhenCirclularIsDisabled = drawCircleWhenCirclularIsDisabled;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mDisableCircularTransformation)
        {
            if(currentShaderColorTransitionBackground != null)
            {
                currentShaderColorTransitionBackground.onDrawShader(mPaintBackground);
            }

            if(currentShaderColorTransitionProgress != null)
            {
                currentShaderColorTransitionProgress.onDrawShader(mPaintProgress);
            }


            super.onDraw(canvas);

            if(drawCircleWhenCirclularIsDisabled)
            {
                mViewWidth = getWidth() / 2;
                mViewHeight = getHeight() / 2;

                initMeasurments();
                drawCirlces(canvas);
            }


            if(currentShaderColorTransitionBackground != null || currentShaderColorTransitionProgress != null)
            {
                postInvalidateDelayed(100);
            }

            return;
        }

        if (mBitmap == null)
        {
            super.onDraw(canvas);
            return;
        }

        if(currentShaderColorTransitionBackground != null)
        {
            currentShaderColorTransitionBackground.onDrawShader(mPaintBackground);
        }

        if(currentShaderColorTransitionProgress != null)
        {
            currentShaderColorTransitionProgress.onDrawShader(mPaintProgress);
        }

        mViewWidth = getWidth() / 2;
        mViewHeight = getHeight() / 2;

        initMeasurments();
        drawCirlces(canvas);

        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mFillPaint);
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);

        if(currentShaderColorTransitionBackground != null || currentShaderColorTransitionProgress != null)
        {
            postInvalidateDelayed(100);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if(currentShaderColorTransitionProgress != null)
        {
            currentShaderColorTransitionProgress.onLayout(getHeight(), getWidth());
        }

        if(currentShaderColorTransitionBackground != null)
        {
            currentShaderColorTransitionBackground.onLayout(getHeight(), getWidth());
        }

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

    public int getFillColor() {
        return mFillColor;
    }


    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }

        mFillColor = fillColor;
        mFillPaint.setColor(fillColor);
        invalidate();
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColorRes The color resource to be resolved to a color and
     *                     drawn behind the drawable
     *
     * @deprecated Fill color support is going to be removed in the future
     */
    @Deprecated
    public void setFillColorResource(@ColorRes int fillColorRes) {
        setFillColor(getContext().getResources().getColor(fillColorRes));
    }

    public int getBorderWidth() {
        return this.mStrokeWidth;
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    public boolean isDisableCircularTransformation()
    {
        return mDisableCircularTransformation;
    }

    public void setDisableCircularTransformation(boolean disableCircularTransformation) {
        if (mDisableCircularTransformation == disableCircularTransformation) {
            return;
        }

        mDisableCircularTransformation = disableCircularTransformation;
        initializeBitmap();
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

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void applyColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.setColorFilter(mColorFilter);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        if (mDisableCircularTransformation) {
            mBitmap = null;
        } else {
            mBitmap = getBitmapFromDrawable(getDrawable());
        }
        setup();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if(ignorePadding)
        {
            mBorderRect.set(calculateBoundsBorder());
            mDrawableRect.set(calculateBoundsForDrawable());
        }
        else
        {
            mBorderRect.set(calculateBoundsForDrawable());
            mDrawableRect.set(mBorderRect);
        }

        mBorderRadius = Math.min((mBorderRect.height() - (mStrokeWidth)) / 2.0f, (mBorderRect.width() - (mStrokeWidth)) / 2.0f);

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mPaintBackground.setStyle(styleBackground);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setStrokeWidth(mStrokeWidth);

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        if (!mBorderOverlay && mStrokeWidth > 0) {
            mDrawableRect.inset((mStrokeWidth) - 1.0f, (mStrokeWidth) - 1.0f);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        applyColorFilter();
        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBoundsForDrawable()
    {
        int availableWidth  = getWidth() - getPaddingLeft() - getPaddingRight() - (mStrokeWidth * 2);
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom() - (mStrokeWidth * 2);

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = (mStrokeWidth) + getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = (mStrokeWidth) + getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private RectF calculateBoundsBorder() //Without removing padding shit.
    {
        int availableWidth;
        int availableHeight;
        availableWidth  = getWidth() - (mStrokeWidth * 2);
        availableHeight = getHeight() - (mStrokeWidth * 2);

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = (mStrokeWidth) + (availableWidth - sideLength) / 2f;
        float top = (mStrokeWidth) + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private boolean ignorePadding = false;

    public void setCircleShouldIgnorePadding(boolean ignorePadding)
    {
        this.ignorePadding = ignorePadding;
    }


    public EsaphShader currentShaderColorTransitionBackground;
    public EsaphShader currentShaderColorTransitionProgress;

    public void setEsaphShaderBackground(EsaphShader esaphShader)
    {
        this.currentShaderColorTransitionBackground = esaphShader;
        setup();
    }

    public void setEsaphShaderProgress(EsaphShader esaphShader)
    {
        this.currentShaderColorTransitionProgress = esaphShader;
        setup();
    }

    public void setBorderColorBackground(int color)
    {
        this.setEsaphShaderBackground(null);
        this.mBackgroundColor = color;
        this.mPaintBackground.setColor(this.mBackgroundColor);
        setup();
    }

    private Paint.Style styleBackground = Paint.Style.STROKE;
    private Paint.Style styleProgress = Paint.Style.STROKE;
    public void setPaintStyleAll(Paint.Style style)
    {
        this.styleBackground = style;
        this.styleProgress = style;
        this.mPaintBackground.setStyle(style);
        this.mPaintProgress.setStyle(style);
        setup();
    }

    public void setPaintStyleProgress(Paint.Style style)
    {
        this.styleProgress = style;
        this.mPaintProgress.setStyle(style);
        setup();
    }

    public void setPaintStyleBackground(Paint.Style style)
    {
        this.styleBackground = style;
        this.mPaintBackground.setStyle(style);
        setup();
    }

    public void setBorderColorProgress(int color)
    {
        this.setEsaphShaderProgress(null);
        this.mProgressColor = color;
        this.mPaintProgress.setColor(this.mProgressColor);
        setup();
    }

    public void setBorderWidth(int newWidth)
    {
        this.mStrokeWidth = newWidth;
        this.mPaintBackground.setStrokeWidth(mStrokeWidth);
        this.mPaintProgress.setStrokeWidth(mStrokeWidth);
        setup();
    }

    private void initMeasurments()
    {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void drawCirlces(Canvas canvas)
    {
        mPaintProgress.setColor(mProgressColor);
        mPaintProgress.setStrokeWidth(mStrokeWidth);
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStrokeCap(mRoundedCorners ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        mPaintProgress.setStyle(styleProgress);

        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setStrokeWidth(mStrokeWidth);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(styleBackground);

       // canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mFillPaint); maybe you can need this. But this drawed a black shit circle
        canvas.drawArc(mBorderRect, mStartAngle, 360, false, mPaintBackground); //Kreis im hintergrund zeichnen.
        canvas.drawArc(mBorderRect, mStartAngle, mSweepAngle, false, mPaintProgress); //Kreis im vordergrund zeichnen.
    }

    private float calcSweepAngleFromProgress(int progress)
    {
        return (mMaxSweepAngle / mMaxProgress) * progress;
    }

    private int calcProgressFromSweepAngle(float sweepAngle)
    {
        return (int) ((sweepAngle * mMaxProgress) / mMaxSweepAngle);
    }

    public void setProgress(int progress)
    {
        System.out.println("CURRENT PROGRESS SET: " + progress);
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



}

