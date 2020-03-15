package esaph.spotlight.Esaph.EsaphAudioRecorder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphColorTransitionShader;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShadersColorArrays;
import esaph.spotlight.R;

public class EsaphAudioRecorderVisualizerView extends FrameLayout
{
    private static final int DEFAULT_NUM_COLUMNS = 20;
    private static final int RENDAR_RANGE_TOP = 0;
    private static final int RENDAR_RANGE_BOTTOM = 1;
    private static final int RENDAR_RANGE_TOP_BOTTOM = 2;

    private int mNumColumns;
    private int mRenderColor;
    private int mType;
    private int mRenderRange;

    private int mBaseY;

    private Canvas mCanvas;
    private Bitmap mCanvasBitmap;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();
    private Paint mFadePaint = new Paint();

    private float mColumnWidth;
    private float mSpace;

    public EsaphAudioRecorderVisualizerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
        mPaint.setColor(mRenderColor);
        mFadePaint.setColor(Color.argb(138, 255, 255, 255));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray args = context.obtainStyledAttributes(attrs, R.styleable.EsaphAudioRecorderVisualizerView);
        mNumColumns = args.getInteger(R.styleable.EsaphAudioRecorderVisualizerView_numColumns, DEFAULT_NUM_COLUMNS);
        mRenderColor = args.getColor(R.styleable.EsaphAudioRecorderVisualizerView_renderColor, Color.WHITE);
        mType = args.getInt(R.styleable.EsaphAudioRecorderVisualizerView_renderType, EsaphAudioRecorderVisualizerView.Type.BAR.getFlag());
        mRenderRange = args.getInteger(R.styleable.EsaphAudioRecorderVisualizerView_renderRange, RENDAR_RANGE_TOP);
        args.recycle();
        esaphColorTransitionShader.init();
    }

    public void setmRenderColor(int mRenderColor)
    {
        this.mRenderColor = mRenderColor;
        this.mPaint.setColor(mRenderColor);
        invalidate();
    }

    public int getmRenderColor() {
        return mRenderColor;
    }

    /**
     * @param baseY center Y position of visualizer
     */

    public void setBaseY(int baseY) {
        mBaseY = baseY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Create canvas once we're ready to draw
        mRect.set(0, 0, getWidth(), getHeight());

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(
                    canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        }

        if (mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if (mNumColumns > getWidth()) {
            mNumColumns = DEFAULT_NUM_COLUMNS;
        }

        mColumnWidth = (float) getWidth() / (float) mNumColumns;
        mSpace = mColumnWidth / 8f;

        if (mBaseY == 0) {
            mBaseY = getHeight() / 2;
        }

        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
    }

    protected void receive(final int volume)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mCanvas == null) {
                    return;
                }

                if (volume == 0)
                {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }
                else if ((mType & EsaphAudioRecorderVisualizerView.Type.FADE.getFlag()) != 0)
                {
                    // Fade out old contents
                    mFadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                    mCanvas.drawPaint(mFadePaint);
                }
                else {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }

                if ((mType & EsaphAudioRecorderVisualizerView.Type.BAR.getFlag()) != 0)
                {
                    drawBar(volume);
                }

                if ((mType & EsaphAudioRecorderVisualizerView.Type.PIXEL.getFlag()) != 0)
                {
                    drawPixel(volume);
                }
                invalidate();
            }
        });
    }

    private EsaphColorTransitionShader esaphColorTransitionShader = new EsaphColorTransitionShader(EsaphShadersColorArrays.COLORS_BLUE_GREEN,
            3, this);

    private void drawBar(int volume)
    {
        for (int i = 0; i < mNumColumns; i++)
        {
            float height = getRandomHeight(volume);
            float left = i * mColumnWidth + mSpace;
            float right = (i + 1) * mColumnWidth - mSpace;

            RectF rect = createRectF(left, right, height);
            esaphColorTransitionShader.onDrawShader(mPaint);
            mCanvas.drawRect(rect, mPaint);
        }
    }

    private void drawPixel(int volume) {
        for (int i = 0; i < mNumColumns; i++) {
            float height = getRandomHeight(volume);
            float left = i * mColumnWidth + mSpace;
            float right = (i + 1) * mColumnWidth - mSpace;

            int drawCount = (int) (height / (right - left));
            if (drawCount == 0) {
                drawCount = 1;
            }
            float drawHeight = height / drawCount;

            // draw each pixel
            for (int j = 0; j < drawCount; j++) {

                float top, bottom;
                RectF rect;

                switch (mRenderRange) {
                    case RENDAR_RANGE_TOP:
                        bottom = mBaseY - (drawHeight * j);
                        top = bottom - drawHeight + mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    case RENDAR_RANGE_BOTTOM:
                        top = mBaseY + (drawHeight * j);
                        bottom = top + drawHeight - mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    case RENDAR_RANGE_TOP_BOTTOM:
                        bottom = mBaseY - (height / 2) + (drawHeight * j);
                        top = bottom - drawHeight + mSpace;
                        rect = new RectF(left, top, right, bottom);
                        break;

                    default:
                        return;
                }
                mCanvas.drawRect(rect, mPaint);
            }
        }
    }

    private float getRandomHeight(int volume)
    {
        double randomVolume = Math.random() * volume + 1;
        float height = getHeight();
        switch (mRenderRange) {
            case RENDAR_RANGE_TOP:
                height = mBaseY;
                break;
            case RENDAR_RANGE_BOTTOM:
                height = (getHeight() - mBaseY);
                break;
            case RENDAR_RANGE_TOP_BOTTOM:
                height = getHeight();
                break;
        }
        return (height / 95f) * (float) randomVolume;
    }

    private RectF createRectF(float left, float right, float height) {
        switch (mRenderRange) {
            case RENDAR_RANGE_TOP:
                return new RectF(left, mBaseY - height, right, mBaseY);
            case RENDAR_RANGE_BOTTOM:
                return new RectF(left, mBaseY, right, mBaseY + height);
            case RENDAR_RANGE_TOP_BOTTOM:
                return new RectF(left, mBaseY - height, right, mBaseY + height);
            default:
                return new RectF(left, mBaseY - height, right, mBaseY);
        }
    }

    /**
     * visualizer type
     */
    public enum Type {
        BAR(0x1), PIXEL(0x2), FADE(0x4);

        private int mFlag;

        Type(int flag) {
            mFlag = flag;
        }

        public int getFlag() {
            return mFlag;
        }
    }
}

