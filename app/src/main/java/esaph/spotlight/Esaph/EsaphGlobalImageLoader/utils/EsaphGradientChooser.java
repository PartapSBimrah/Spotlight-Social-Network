/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils;

import esaph.spotlight.R;

public class EsaphGradientChooser
{
    public static int obtainGradient(int pos)
    {
        if(pos % 2 == 0)
        {
            return R.drawable.gradient_orange_pink;
        }
        else if(pos % 3 == 0)
        {
            return R.drawable.gradient_blue_green;
        }
        else if(pos % 4 == 0)
        {
            return R.drawable.gradient_blue_purple;
        }

        return R.drawable.gradient_yellow_green;
    }
}
