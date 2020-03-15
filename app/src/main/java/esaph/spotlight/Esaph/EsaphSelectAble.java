/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph;

public abstract class EsaphSelectAble
{
    private boolean selected = false;

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelection(boolean selected)
    {
        this.selected = selected;
    }
}
