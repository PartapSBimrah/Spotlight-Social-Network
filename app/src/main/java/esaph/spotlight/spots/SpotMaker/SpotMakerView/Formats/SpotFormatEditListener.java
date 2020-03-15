/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats;

import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;

public interface SpotFormatEditListener
{
    void onStartEditing(SpotMakerEdittext spotMakerEdittext);
    void onViewTouchedOutsideBounds();
}
