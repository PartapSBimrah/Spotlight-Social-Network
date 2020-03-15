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
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.Shaders.ShaderGlow;

public class TextShaderBuilderGlow extends TextShaderBuilder
{
    public static final short SHADER_GLOW = 0;
    private static final String KEY_FONT_SHADER_SHADOW_RADIUS = "FSSR";

    public TextShaderBuilderGlow(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        jsonObject.put(SpotTextShader.KEY_FONT_SHADER_TYPE, TextShaderBuilderGlow.SHADER_GLOW);
    }

    public static TextShaderBuilderGlow from(JSONObject jsonObject) throws JSONException
    {
        return new TextShaderBuilderGlow(jsonObject);
    }

    public TextShaderBuilderGlow setRadius(int radius) throws JSONException
    {
        JSONObject jsonObjectShader = super.getJsonObject();
        jsonObjectShader.put(SpotTextShader.KEY_FONT_SHADER_TYPE, TextShaderBuilderGlow.SHADER_GLOW);
        jsonObjectShader.put(TextShaderBuilderGlow.KEY_FONT_SHADER_SHADOW_RADIUS, radius);
        return this;
    }

    @Override
    public EsaphShader getShader() throws JSONException
    {
        return new ShaderGlow(super.getJsonObject().getInt(TextShaderBuilderGlow.KEY_FONT_SHADER_SHADOW_RADIUS));
    }
}
