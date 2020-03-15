package esaph.spotlight.Esaph.EsaphCircleImageView;

import android.graphics.Paint;
import android.graphics.Shader;

public abstract class EsaphShader extends Shader
{
    public abstract void onDrawShader(Paint paint);
    public abstract void onLayout(int height, int width);
}
