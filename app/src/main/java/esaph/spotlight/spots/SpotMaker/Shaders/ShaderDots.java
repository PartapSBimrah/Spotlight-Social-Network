package esaph.spotlight.spots.SpotMaker.Shaders;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class ShaderDots extends EsaphShader
{
    private DashPathEffect pathDashPathEffect;
    public ShaderDots() {
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        paint.setPathEffect(pathDashPathEffect);
    }

    @Override
    public void onLayout(int height, int width)
    {
        pathDashPathEffect = new DashPathEffect(new float[]{10,50}, 6);
    }
}
