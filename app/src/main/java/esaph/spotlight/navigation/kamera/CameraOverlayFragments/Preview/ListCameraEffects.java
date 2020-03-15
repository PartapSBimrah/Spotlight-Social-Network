/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview;

import android.content.Context;
import android.hardware.Camera;

import com.google.android.material.tabs.TabLayout;

import esaph.spotlight.R;

public class ListCameraEffects
{
    private String[] arrayEffects;
    private String[] arrayFilterEffects;
    private String[] arrayEmojieOverLay;

    public ListCameraEffects(Context context)
    {
        arrayEffects = new String[]
                {
                        context.getResources().getString(R.string.txt_camera_effect_normal),
                      //  context.getResources().getString(R.string.txt_camera_effect_pauseable),
                        context.getResources().getString(R.string.txt_camera_effect_sticker_hunt),
                        context.getResources().getString(R.string.txt_filter),
                        context.getResources().getString(R.string.txt_overlay_filter),
                };


        this.arrayFilterEffects = new String[]
                {
                        Camera.Parameters.EFFECT_NONE,
                        Camera.Parameters.EFFECT_AQUA,
                        Camera.Parameters.EFFECT_NEGATIVE,
                        Camera.Parameters.EFFECT_POSTERIZE,
                        Camera.Parameters.EFFECT_SEPIA,
                };

        this.arrayEmojieOverLay = new String[]
                {
                        "❤",
                        "\uD83D\uDE9A",
                        "\uD83C\uDF4C",
                };
    }

    public String[] getArrayCameraModes() {
        return arrayEffects;
    }

    public String[] getArrayFilterEffects() {
        return arrayFilterEffects;
    }

    public String[] getArrayEmojieOverLay() {
        return arrayEmojieOverLay;
    }

    public void initTabs(TabLayout tabLayout, String[] array)
    {
        for(int counter = 0; counter < array.length; counter++)
        {
            tabLayout.addTab(tabLayout.newTab().setText(array[counter]));
        }
    }
}
