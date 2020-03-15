/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphColorCircle;

import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class EsaphSweepGradientShader extends EsaphShader
{
    private int mColors[];
    private Shader shader;

    public EsaphSweepGradientShader(int[] mColors)
    {
        this.mColors = mColors;
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        paint.setShader(shader);
    }

    @Override
    public void onLayout(int height, int width)
    {
        shader = new SweepGradient((float) width / 2, (float) height / 2, mColors, null);
    }
}
