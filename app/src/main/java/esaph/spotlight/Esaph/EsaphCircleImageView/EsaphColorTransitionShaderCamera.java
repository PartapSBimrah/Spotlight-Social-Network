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

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphColorTransitionShaderCamera extends EsaphShader
{
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private float mTranslate;
    private float colorSpeed;
    private float colorSpace;

    private int[] colors = {
            0xFFFBD4D7,
            0xFFF6B3D4,
            0xFFF298D5,
            0xFFE48CD8,
            0xFFB877DF,
            0xFF7956EE};

    public EsaphColorTransitionShaderCamera()
    {
        init();
    }

    public void init()
    {
        colorSpace = DisplayUtils.dp2px(100);
        colorSpeed = DisplayUtils.dp2px(1);
        mMatrix = new Matrix();
        mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
    }

    public void setNewShaderColors(int[] colors)
    {
        this.colors = colors;
        mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
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
