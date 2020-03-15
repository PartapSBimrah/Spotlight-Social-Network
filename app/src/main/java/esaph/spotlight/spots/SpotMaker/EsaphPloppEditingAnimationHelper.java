/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker;

import android.widget.RelativeLayout;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphPloppEditingAnimationHelper
{
    public static void showColorPicker(RelativeLayout relativeLayoutBottomEditingTools)
    {
        relativeLayoutBottomEditingTools.animate().translationY(RELATIV_BOTTOM_OPTIONS_TRANLATE_HEIGHT_COLOR_PICKER).setDuration(100).start();
    }

    public static void showColorPickerWithFontSizePicker(RelativeLayout relativeLayoutBottomEditingTools)
    {
        relativeLayoutBottomEditingTools.animate().translationY(0f).setDuration(100).start();
    }

    private static float RELATIV_BOTTOM_OPTIONS_TRANLATE_HEIGHT_NORMAL = DisplayUtils.dp2px(80);
    private static float RELATIV_BOTTOM_OPTIONS_TRANLATE_HEIGHT_COLOR_PICKER = DisplayUtils.dp2px(30); //-20 for margin
    public static void removeColorPicker(RelativeLayout relativeLayoutBottomEditingTools)
    {
        relativeLayoutBottomEditingTools.animate().translationY(RELATIV_BOTTOM_OPTIONS_TRANLATE_HEIGHT_NORMAL).setDuration(100).start();
    }
}
