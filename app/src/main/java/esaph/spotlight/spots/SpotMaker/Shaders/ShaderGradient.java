/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.Shaders;

import android.graphics.LinearGradient;
import android.graphics.Paint;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class ShaderGradient extends EsaphShader
{
    private LinearGradient mLinearGradient;
    private int[] colors;

    public ShaderGradient(int[] colors)
    {
        this.colors = colors;
    }

    @Override
    public void onLayout(int height, int width)
    {
        mLinearGradient = new LinearGradient(0,
                0,
                width,
                height,
                colors,
                null,
                TileMode.CLAMP);
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        paint.setShader(mLinearGradient);
    }
}
