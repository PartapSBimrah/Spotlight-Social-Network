/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

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
