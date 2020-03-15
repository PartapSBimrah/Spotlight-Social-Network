package esaph.spotlight.spots.SpotMaker.Shaders;

import android.graphics.Paint;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class ShaderGlow extends EsaphShader
{
    private int RADIUS;

    public ShaderGlow(int RADIUS) {
        this.RADIUS = RADIUS;
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        paint.setShadowLayer(RADIUS, 0, 0, paint.getColor());
    }

    @Override
    public void onLayout(int height, int width)
    {

    }
}
