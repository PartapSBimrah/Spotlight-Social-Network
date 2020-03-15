package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class EsaphShaderLayout extends RelativeLayout
{
    private Paint paintBackground;

    public EsaphShaderLayout(Context context)
    {
        super(context);
        init();
    }

    public EsaphShaderLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EsaphShaderLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(21)
    public EsaphShaderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setWillNotDraw(false);
        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(esaphShader != null)
        {
            esaphShader.onDrawShader(paintBackground);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paintBackground); // that's painting the whole canvas in the chosen color.
            postInvalidateDelayed(25); //40 fps
        }
    }

    private EsaphShader esaphShader;

    public void setEsaphShader(EsaphShader esaphShader)
    {
        this.esaphShader = esaphShader;
    }
}
