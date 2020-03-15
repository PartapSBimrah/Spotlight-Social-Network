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

import java.util.Iterator;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;

public class SpotsStickerDefinitionBuilder
{
    private JSONObject jsonObject;
    private static final String KEY_TEXT_SIZE = "TS";
    private static final String KEY_TEXT_ALIGNMENT = "FTA";
    private static final int DEAULT_STICKER_SIZE = 15;


    private SpotsStickerDefinitionBuilder(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public static SpotsStickerDefinitionBuilder create(JSONObject jsonObject)
    {
        return new SpotsStickerDefinitionBuilder(jsonObject);
    }

    public SpotsStickerDefinitionBuilder setTextSize(int textSize) throws JSONException
    {
        jsonObject.put(SpotsStickerDefinitionBuilder.KEY_TEXT_SIZE, textSize);
        return this;
    }

    public SpotsStickerDefinitionBuilder resetToInitState(Context context)
    {
        try
        {
            setTextSize(SpotsStickerDefinitionBuilder.DEAULT_STICKER_SIZE)
                    .setTextAlignment(SpotTextAlignment.ALIGNMENT_CENTER);
        }
        catch (Exception ec)
        {
        }
        return this;
    }

    public SpotsStickerDefinitionBuilder setTextAlignment(short textAlignment) throws JSONException
    {
        jsonObject.put(SpotsStickerDefinitionBuilder.KEY_TEXT_ALIGNMENT, textAlignment);
        return this;
    }

    public int getTextSize() throws JSONException
    {
        return DisplayUtils.dp2px(jsonObject.getInt(SpotsStickerDefinitionBuilder.KEY_TEXT_SIZE));
    }

    public short getTextAlignment() throws JSONException
    {
        return (short) jsonObject.getInt(SpotsStickerDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }

    public static int getTextSize(JSONObject jsonObject) throws JSONException
    {
        return DisplayUtils.dp2px(jsonObject.getInt(SpotsStickerDefinitionBuilder.KEY_TEXT_SIZE));
    }

    public static short getTextAlignment(JSONObject jsonObject) throws JSONException
    {
        return (short) jsonObject.getInt(SpotsStickerDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }
}
