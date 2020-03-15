/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphDrawingShaderEffects;

import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphDrawingShaderRainbow extends EsaphShader
{
    private int strokeWidth;
    private Matrix mMatrix;
    private float mTranslate;
    private float colorSpeed;
    private float colorSpace;
    private int mColors[];
    private LinearGradient linearGradient;

    public EsaphDrawingShaderRainbow(int strokeWidth)
    {
        this.strokeWidth = strokeWidth;
        this.mMatrix = new Matrix();
        this.colorSpace = DisplayUtils.dp2px(20);
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        mTranslate += colorSpeed;
        mMatrix.setTranslate(mTranslate, 0);
        linearGradient.setLocalMatrix(mMatrix);
        paint.setShader(linearGradient);
    }

    @Override
    public void onLayout(int height, int width) {
        mColors = new int[] { //Black started
                0xFFFF0000,
                0xFFFFAA00,
                0xFFFFFB00,
                0xFF11FF00,
                0xFF00FFFB,
                0xFF000DFF,
                0xFFCC00FF,
                0xFFFF00AE,
        };
        linearGradient = new LinearGradient(0, 0, 0, strokeWidth, mColors, null, TileMode.CLAMP /*or REPEAT*/);
    }
}
