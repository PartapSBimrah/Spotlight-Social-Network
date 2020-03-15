/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

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
