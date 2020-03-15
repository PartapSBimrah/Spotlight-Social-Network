/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphCircleImageView;

import android.graphics.Paint;
import android.graphics.Shader;

public abstract class EsaphShader extends Shader
{
    public abstract void onDrawShader(Paint paint);
    public abstract void onLayout(int height, int width);
}
