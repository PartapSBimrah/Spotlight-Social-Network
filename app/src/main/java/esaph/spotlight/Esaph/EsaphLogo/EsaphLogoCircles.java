package esaph.spotlight.Esaph.EsaphLogo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphLogoCircles extends View
{
    private final Paint mPaint;
    private int mStrokeWidth;             // Width of outline
    private int mColor = Color.WHITE;   // Outline color

    public EsaphLogoCircles(Context context)
    {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphLogoCircles(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphLogoCircles(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    @RequiresApi(21)
    public EsaphLogoCircles(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeWidth = DisplayUtils.dp2px(2);
    }

    public EsaphShader esaphShader;

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

    public void setEsaphShader(EsaphShader esaphShader)
    {
        this.esaphShader = esaphShader;
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

        if(esaphShader != null && shaderEnabled)
        {
            esaphShader.onDrawShader(mPaint);
        }

        mViewWidth = getWidth() / 2;
        mViewHeight = getHeight() / 2;

        canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth * EsaphLogoCircles.CIRLCE_DISTANCE_ONE, mPaint);
        canvas.drawCircle(mViewWidth, mViewHeight, mViewWidth * EsaphLogoCircles.CIRLCE_DISTANCE_TWO, mPaint);
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
        mPaint.setColor(this.mColor);
        invalidate();
    }

    public void setStrokeWidth(int newWidth)
    {
        mStrokeWidth = newWidth;
        invalidate();
    }
}
