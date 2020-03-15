package esaph.spotlight.Esaph.EsaphColorCircle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class EsaphColorCircle extends View
{
    private final Paint paint;
    private int mColors[];
    private Shader shader;
    public EsaphColorCircle(Context context)
    {
        super(context);
        paint = new Paint();
    }

    public EsaphColorCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        initDefaultColors();
    }

    public EsaphColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        initDefaultColors();
    }

    @RequiresApi(21)
    public EsaphColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint = new Paint();
        initDefaultColors();
    }

    private int circleWidth;
    private int circleHeight;
    private int radius;


    private void initDefaultColors() {
        this.mColors = new int[]{
                Color.parseColor("#F44336"),
                Color.parseColor("#E91E63"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#673AB7"),
                Color.parseColor("#3F51B5"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#00BCD4"),
                Color.parseColor("#009688"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#8BC34A"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#FFEB3B"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#FF5722"),
        };
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        circleWidth = getWidth();
        circleHeight = getHeight();
        radius = circleWidth / 2;

        shader = new SweepGradient((float) circleWidth / 2, (float) circleHeight / 2, mColors, null);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setShader(shader);
        canvas.drawCircle((float) circleWidth / 2, (float) circleHeight / 2, radius, paint);
    }
}
