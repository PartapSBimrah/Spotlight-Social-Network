/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.Models;

import android.content.Context;
import android.graphics.Typeface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotTextFontFamlie
{
    public static final String FONT_DEFAULT = "DEF";

    public static final int[] fontFamilieResourceIDs
            = new int[]
            {
                    R.font.actionj,
                    R.font.centreclaws,
                    R.font.centreclawsbeam,
                    R.font.centreclawsslant,
                    R.font.condition3d_regular,
                    R.font.condition3dfilled_regular,
                    R.font.disco3,
                    R.font.edbwt,
                    R.font.good_dog_plain,
                    R.font.gwibble_,
                    R.font.jandles,
                    R.font.monodb_,
                    R.font.musicals,
                    R.font.outersid,
                    R.font.penguinattack,
                    R.font.philliboo,
                    R.font.plg,
                    R.font.rm_playtime_3d,
                    R.font.rm_tubeway_chrome,
                    R.font.undergroundnf,
                    R.font.waker,
                    R.font.youre_gone,
            };



    private SpotTextFontFamlie()
    {
    }

    public static SpotTextFontFamlie options()
    {
        return new SpotTextFontFamlie();
    }

    public List<JSONObject> preview(Context context) throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();
        String textPlaceholder = context.getResources().getString(R.string.txt_text_placeholder);

        int length = SpotTextFontFamlie.fontFamilieResourceIDs.length;

        for(int counter = 0; counter < length; counter++)
        {
            list.add(getObject(context,
                    context.getResources().getResourceEntryName(SpotTextFontFamlie.fontFamilieResourceIDs[counter]),
                    textPlaceholder));
        }
        return list;
    }

    private JSONObject getObject(Context context, String FONT_FAMILIE_NAME, String Name) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(context).setTextSize(30).setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                .setFontStyle(SpotTextFontStyle.FONT_NORMAL)
                .setFontFamily(FONT_FAMILIE_NAME)
                .setText(Name);
        SpotBackgroundDefinitionBuilder.create(jsonObject).setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkSpotPreviewBackground));
        return jsonObject;
    }

    public static Typeface getFontFamlieForApiVersion(Context context, String FONT_NAME)
    {
        if(FONT_NAME.equals(SpotTextFontFamlie.FONT_DEFAULT))
        {
            return Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        }


        return Typeface.create(
                ResourcesCompat.getFont(
                        context, context.getResources().getIdentifier(
                                FONT_NAME, "font", context.getApplicationContext().getPackageName())), Typeface.NORMAL);
    }
}
