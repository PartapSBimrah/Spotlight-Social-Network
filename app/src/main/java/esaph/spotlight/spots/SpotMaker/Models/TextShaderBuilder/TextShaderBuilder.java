/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public abstract class TextShaderBuilder
{
    private JSONObject jsonObject;
    public TextShaderBuilder(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public abstract EsaphShader getShader() throws JSONException;
}
