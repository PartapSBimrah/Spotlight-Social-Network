/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.Definitions;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;

public class SpotEmojieDefinitionBuilder
{
    private JSONObject jsonObject;
    private static final String KEY_TEXT_SIZE = "TS";
    private static final String KEY_TEXT_ALIGNMENT = "FTA";
    private static final int DEAULT_EMOJIE_SIZE = 15;


    private SpotEmojieDefinitionBuilder(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public static SpotEmojieDefinitionBuilder create(JSONObject jsonObject)
    {
        return new SpotEmojieDefinitionBuilder(jsonObject);
    }

    public SpotEmojieDefinitionBuilder setTextSize(int textSize) throws JSONException
    {
        jsonObject.put(SpotEmojieDefinitionBuilder.KEY_TEXT_SIZE, textSize);
        return this;
    }

    public SpotEmojieDefinitionBuilder resetToInitState(Context context)
    {
        try
        {
            setTextSize(SpotEmojieDefinitionBuilder.DEAULT_EMOJIE_SIZE)
                    .setTextAlignment(SpotTextAlignment.ALIGNMENT_CENTER);
        }
        catch (Exception ec)
        {
        }
        return this;
    }

    public SpotEmojieDefinitionBuilder setTextAlignment(short textAlignment) throws JSONException
    {
        jsonObject.put(SpotEmojieDefinitionBuilder.KEY_TEXT_ALIGNMENT, textAlignment);
        return this;
    }

    public int getTextSize() throws JSONException
    {
        return DisplayUtils.dp2px(jsonObject.getInt(SpotEmojieDefinitionBuilder.KEY_TEXT_SIZE));
    }

    public short getTextAlignment() throws JSONException
    {
        return (short) jsonObject.getInt(SpotEmojieDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }

    public static int getTextSize(JSONObject jsonObject) throws JSONException
    {
        return DisplayUtils.dp2px(jsonObject.getInt(SpotEmojieDefinitionBuilder.KEY_TEXT_SIZE));
    }

    public static short getTextAlignment(JSONObject jsonObject) throws JSONException
    {
        return (short) jsonObject.getInt(SpotEmojieDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }
}
