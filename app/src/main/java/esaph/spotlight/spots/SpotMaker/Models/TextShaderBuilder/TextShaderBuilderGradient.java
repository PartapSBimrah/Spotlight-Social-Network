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
import esaph.spotlight.spots.SpotMaker.Shaders.ShaderGradient;

public class TextShaderBuilderGradient extends TextShaderBuilder
{
    private static final String KEY_FONT_SHADER_BEGIN_COLOR = "FSBC";
    private static final String KEY_FONT_SHADER_END_COLOR = "FSEC";
    public static final short SHADER_GRADIENT = 2;

    public TextShaderBuilderGradient(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        jsonObject.put(SpotTextShader.KEY_FONT_SHADER_TYPE, TextShaderBuilderGradient.SHADER_GRADIENT);
    }

    public static TextShaderBuilderGradient from(JSONObject jsonObject) throws JSONException
    {
        return new TextShaderBuilderGradient(jsonObject);
    }

    public TextShaderBuilderGradient setGradientColors(int colors[]) throws JSONException
    {
        JSONObject jsonObjectShader = super.getJsonObject();
        jsonObjectShader.put(SpotTextShader.KEY_FONT_SHADER_TYPE, TextShaderBuilderGradient.SHADER_GRADIENT);
        jsonObjectShader.put(TextShaderBuilderGradient.KEY_FONT_SHADER_BEGIN_COLOR, colors[0]);
        jsonObjectShader.put(TextShaderBuilderGradient.KEY_FONT_SHADER_END_COLOR, colors[1]);
        return this;
    }

    @Override
    public EsaphShader getShader() throws JSONException
    {
        return new ShaderGradient(new int[]{super.getJsonObject().getInt(TextShaderBuilderGradient.KEY_FONT_SHADER_BEGIN_COLOR),
                super.getJsonObject().getInt(TextShaderBuilderGradient.KEY_FONT_SHADER_END_COLOR)});
    }
}
