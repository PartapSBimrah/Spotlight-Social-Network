/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphCircleImageView;

import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphColorTransitionShader extends EsaphShader
{
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private float mTranslate;
    private float colorSpeed;
    private float colorSpace;

    private int[] colors;

    public EsaphColorTransitionShader(int[] colors, int colorSpeed, final View view)
    {
        this.colors = colors;
        this.colorSpace = DisplayUtils.dp2px(100);
        this.colorSpeed = DisplayUtils.dp2px(colorSpeed);
        init();
    }

    public void init()
    {
        mMatrix = new Matrix();
        mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        mTranslate += colorSpeed;
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
        paint.setShader(mLinearGradient);
    }

    @Override
    public void onLayout(int height, int width) {
        init();
    }
}
