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

public class SpotAudioDefinitionBuilder
{
    private JSONObject jsonObject;

    private SpotAudioDefinitionBuilder(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public static SpotAudioDefinitionBuilder create(JSONObject jsonObject)
    {
        return new SpotAudioDefinitionBuilder(jsonObject);
    }

    public SpotAudioDefinitionBuilder resetToInitState(Context context)
    {
        return this;
    }
}
