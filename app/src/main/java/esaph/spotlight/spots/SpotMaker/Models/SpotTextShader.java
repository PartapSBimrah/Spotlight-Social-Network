/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.Models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder.TextShaderBuilder;
import esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder.TextShaderBuilderDots;
import esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder.TextShaderBuilderGlow;
import esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder.TextShaderBuilderGradient;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

import static esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder.TextShaderBuilderGradient.SHADER_GRADIENT;

public class SpotTextShader
{
    public static final String KEY_FONT_SHADER_TYPE = "FSTT";
    private TextShaderBuilder textShaderBuilder;

    private SpotTextShader()
    {
    }

    public static SpotTextShader options()
    {
        return new SpotTextShader();
    }

    public List<JSONObject> preview(Context context) throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();


        JSONObject jsonObjectRemovingShader = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObjectRemovingShader).resetToInitState(context)
                .setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                .setText(context.getResources().getString(R.string.txt_no_effects));

        SpotBackgroundDefinitionBuilder.create(jsonObjectRemovingShader).resetToInitState(context).setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkSpotPreviewBackground));
        list.add(jsonObjectRemovingShader);


        list.add(getObject(context, TextShaderBuilderGlow.from(new JSONObject()).setRadius(10), context.getResources().getString(R.string.txt_shaderGlow)));
        list.add(getObject(context, TextShaderBuilderDots.from(new JSONObject()), context.getResources().getString(R.string.txt_shaderPoints)));
        list.add(getObject(context, TextShaderBuilderGradient.from(new JSONObject()).setGradientColors(new int[]{0xFF8A2387, 0xFFE94057}), "Wiretap"));
        list.add(getObject(context, TextShaderBuilderGradient.from(new JSONObject()).setGradientColors(new int[]{0xFF00F260, 0xFF0575E6}), "Rainbow Blue"));
        list.add(getObject(context, TextShaderBuilderGradient.from(new JSONObject()).setGradientColors(new int[] { //Black started
            0xFFFF0000,
            0xFFFFAA00,
            0xFFFFFB00,
            0xFF11FF00,
            0xFF00FFFB,
            0xFF000DFF,
            0xFFCC00FF,
            0xFFFF00AE,}), "Rainbow"));
        list.add(getObject(context, TextShaderBuilderGradient.from(new JSONObject()).setGradientColors(new int[]{0xFFFC4A1A, 0xFFF7B733}), "Orange Fun"));
        list.add(getObject(context, TextShaderBuilderGradient.from(new JSONObject()).setGradientColors(new int[]{0xFF22C1C3, 0xFFFDBB2D}), "Summer"));
        return list;
    }

    public SpotTextShader setShader(TextShaderBuilder textShaderBuilder) throws JSONException
    {
        this.textShaderBuilder = textShaderBuilder;
        return this;
    }

    private JSONObject getObject(Context context, TextShaderBuilder textShaderBuilder, String Shadername) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(context).setTextShader(textShaderBuilder.getJsonObject()).setText(Shadername);
        SpotBackgroundDefinitionBuilder.create(jsonObject).resetToInitState(context).setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkSpotPreviewBackground));
        return jsonObject;
    }

    public static EsaphShader getShaderForApiVersion(JSONObject jsonObject) throws Exception
    {
        short type = (short) jsonObject.getInt(KEY_FONT_SHADER_TYPE);
        switch (type)
        {
            case TextShaderBuilderGlow.SHADER_GLOW:
                return TextShaderBuilderGlow.from(jsonObject).getShader();

            case TextShaderBuilderDots.SHADER_DOT:
                return TextShaderBuilderDots.from(jsonObject).getShader();

            case SHADER_GRADIENT:
                return TextShaderBuilderGradient.from(jsonObject).getShader();
        }

        throw new Exception("No shader in message.");
    }

    public static int getType(JSONObject jsonObject) throws JSONException
    {
        return (short) jsonObject.getInt(KEY_FONT_SHADER_TYPE);
    }

}
