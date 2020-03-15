package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class EsaphCurvedBottomLayout extends RelativeLayout implements EsaphColorTransitionAnimateAble
{
    private Paint mPaint;

    /** the CURVE_CIRCLE_RADIUS represent the radius of the fab button */
    public final int CURVE_CIRCLE_RADIUS = 256 / 3;
    // the coordinates of the first curve

    private Path mOtherPath;

    // the coordinates of the first "inverted" curve
    public Point mFirstOtherCurveStartPoint = new Point();
    public Point mFirstOtherCurveEndPoint = new Point();
    public Point mFirstOtherCurveControlPoint2 = new Point();
    public Point mFirstOtherCurveControlPoint1 = new Point();

    //the coordinates of the second "inverted" curve
    @SuppressWarnings("FieldCanBeLocal")
    public Point mSecondOtherCurveStartPoint = new Point();
    public Point mSecondOtherCurveEndPoint = new Point();
    public Point mSecondOtherCurveControlPoint1 = new Point();
    public Point mSecondOtherCurveControlPoint2 = new Point();

    public int mNavigationBarWidth;
    public int mNavigationBarHeight;

    public EsaphCurvedBottomLayout(Context context)
    {
        super(context);
        init();
    }

    public EsaphCurvedBottomLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EsaphCurvedBottomLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.TRANSPARENT);
        mOtherPath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if(esaphShader != null)
        {
            esaphShader.onLayout(getHeight(), getWidth());
        }

        // get width and height of navigation bar
        // Navigation bar bounds (width & height)
        mNavigationBarWidth = getWidth();
        mNavigationBarHeight = getHeight();
        // the coordinates (x,y) of the start point before curve

// and now once more for the "inverted" curve...
//

// the coordinates (x,y) of the start point before curve
        mFirstOtherCurveStartPoint.set((mNavigationBarWidth / 2) - (CURVE_CIRCLE_RADIUS * 2) - (CURVE_CIRCLE_RADIUS / 3), mNavigationBarHeight);
// the coordinates (x,y) of the end point after curve
        mFirstOtherCurveEndPoint.set(mNavigationBarWidth / 2, mNavigationBarHeight - (CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4) ) );
// same thing for the second curve
        mSecondOtherCurveStartPoint = mFirstOtherCurveEndPoint;
        mSecondOtherCurveEndPoint.set((mNavigationBarWidth / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3), mNavigationBarHeight);

// the coordinates (x,y)  of the 1st control point on a cubic curve
        mFirstOtherCurveControlPoint1.set(mFirstOtherCurveStartPoint.x + CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4), mFirstOtherCurveStartPoint.y);
// the coordinates (x,y)  of the 2nd control point on a cubic curve
        mFirstOtherCurveControlPoint2.set(mFirstOtherCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2) + CURVE_CIRCLE_RADIUS, mFirstOtherCurveEndPoint.y);

        mSecondOtherCurveControlPoint1.set(mSecondOtherCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2) - CURVE_CIRCLE_RADIUS, mSecondOtherCurveStartPoint.y);
        mSecondOtherCurveControlPoint2.set(mSecondOtherCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4)), mSecondOtherCurveEndPoint.y);
    }


    private EsaphSwipeShader esaphShader;

    public void setEsaphShader(EsaphSwipeShader esaphShader)
    {
        this.esaphShader = esaphShader;
        this.esaphShader.setEsaphSwipeShaderCommunication(this);
    }


    public EsaphSwipeShader getEsaphShader() {
        return esaphShader;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawPath(mOtherPath, mPaint);

        mOtherPath.reset();
        mOtherPath.moveTo(0, mNavigationBarHeight);
        mOtherPath.lineTo(mFirstOtherCurveStartPoint.x, mFirstOtherCurveStartPoint.y);
        mOtherPath.cubicTo(mFirstOtherCurveControlPoint1.x, mFirstOtherCurveControlPoint1.y,
                mFirstOtherCurveControlPoint2.x, mFirstOtherCurveControlPoint2.y,
                mFirstOtherCurveEndPoint.x, mFirstOtherCurveEndPoint.y);

        mOtherPath.cubicTo(mSecondOtherCurveControlPoint1.x, mSecondOtherCurveControlPoint1.y,
                mSecondOtherCurveControlPoint2.x, mSecondOtherCurveControlPoint2.y,
                mSecondOtherCurveEndPoint.x, mSecondOtherCurveEndPoint.y);
        mOtherPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
        mOtherPath.lineTo(mNavigationBarWidth, 0);
        mOtherPath.lineTo(0, 0);
        mOtherPath.close();

        if(esaphShader != null)
        {
            esaphShader.onDrawShader(mPaint);
            setBackgroundColor(esaphShader.getAlphaStupidBackground());
            canvas.drawPath(mOtherPath, mPaint);
        }
    }

    private static final float[] positionsGradient = new float[]{0f, 0.25f, 0.50f, 0.75f};

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
                            getWidth(),
                            0,
                            colorArray
                            ,
                            positionsGradient,
                            Shader.TileMode.CLAMP);
        }
        else if(position == 1)
        {
            colorArray = EsaphColorSwipeTransitionHelper.applyColorFilterPositionOne(mPaint, positionOffset);
            mLinearGradient =
                    new LinearGradient(
                            0,
                            0,
                            getWidth(),
                            0,
                            colorArray
                            ,
                            positionsGradient,
                            Shader.TileMode.CLAMP);
        }
        else if(position == 2)
        {
            colorArray = EsaphColorSwipeTransitionHelper.applyColorFilterPositionTwo(mPaint, positionOffset);
            mLinearGradient =
                    new LinearGradient(
                            0,
                            0,
                            getWidth(),
                            0,
                            colorArray
                            ,
                            positionsGradient,
                            Shader.TileMode.CLAMP);
        }
        return mLinearGradient;
    }

    @Override
    public void onInvalidate() {
        invalidate();
    }
}
