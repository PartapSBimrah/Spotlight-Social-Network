/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGradientTextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import esaph.spotlight.R;

public class EsaphGradientTextView extends AppCompatTextView
{
    private LinearGradient mLinearGradient;
    private int[] colors;

    public EsaphGradientTextView(Context context)
    {
        super(context);
    }

    public EsaphGradientTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public EsaphGradientTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet)
    {
        final TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.EsaphGradientTextView);

        if(array.length() > 0)
        {
            final int id = array.getResourceId(R.styleable.EsaphGradientTextView_esColorArray, 0);

            if (id != 0) {
                colors = getResources().getIntArray(id);
            }
        }


        array.recycle();
    }

    private float[] getSpacings(int[] colors)
    {
        float[] spacings = new float[colors.length];
        float SPACE = 1f / colors.length;
        for (int counter = 0; counter < spacings.length; counter++)
        {
            spacings[counter] = SPACE * counter;
        }

        return spacings;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mLinearGradient = new LinearGradient(0,
            0,
            getWidth(),
            getHeight(),
            colors,
                getSpacings(colors),
            Shader.TileMode.CLAMP);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        //Setting the gradient if layout is changed
        if (changed)
        {
            getPaint().setShader(mLinearGradient);
        }
    }
}
