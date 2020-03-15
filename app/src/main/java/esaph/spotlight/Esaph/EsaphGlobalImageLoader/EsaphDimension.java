/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

public class EsaphDimension
{
    private int width;
    private int height;

    public EsaphDimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getComparedDimensions()
    {
        return "" + width + height;
    }

    public int getComparedDimensionsInt()
    {
        return width + height;
    }
}
