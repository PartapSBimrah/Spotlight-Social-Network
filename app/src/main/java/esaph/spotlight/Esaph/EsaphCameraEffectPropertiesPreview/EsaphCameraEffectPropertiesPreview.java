package esaph.spotlight.Esaph.EsaphCameraEffectPropertiesPreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class EsaphCameraEffectPropertiesPreview extends View
{
    private EsaphShader esaphShader;
    private Paint paint;
    private float[] dataSet = new float[]{};

    public EsaphCameraEffectPropertiesPreview(Context context)
    {
        super(context);
    }

    public EsaphCameraEffectPropertiesPreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EsaphCameraEffectPropertiesPreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public EsaphCameraEffectPropertiesPreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPaint(Paint paint)
    {
        this.paint = paint;
    }

    public void setEsaphShader(EsaphShader esaphShader) //Shader must have called init!!
    {
        this.esaphShader = esaphShader;
    }

    private Path mPath;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath = new Path();
        mPath.moveTo(0, h / 2);
        mPath.quadTo(w/2, h/2, w, h/2);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(paint == null)
            return;

        if(esaphShader != null)
        {
            esaphShader.onDrawShader(paint);
        }

        canvas.drawPath(mPath, paint);
    }
}
