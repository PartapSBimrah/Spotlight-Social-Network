/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphBounce;

import android.view.animation.Interpolator;

public class EsaphBounceInterpolator implements Interpolator
{
    private double mAmplitude = 0.1;
    private double mFrequency = 5;

    public EsaphBounceInterpolator(double amplitude, double frequency)
    {

    }

    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
