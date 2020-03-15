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

import androidx.core.content.ContextCompat;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;

public class SpotTextDefinitionBuilder
{
    private JSONObject jsonObject;
    private static final String KEY_FONT_SHADER = "FSH";
    private static final String KEY_TEXT_COLOR = "TC";
    private static final String KEY_TEXT_SIZE = "TS";
    private static final String KEY_TEXT_ALIGNMENT = "FTA";
    private static final String KEY_FONT_STYLE = "FS";
    private static final String KEY_FONT_FAMILY = "FFY";
    private static final int DEAULT_TEXT_SIZE = 20; //SP

    private static final String KEY_TEXT_DESCRIPTION_CONTENT_ONLY_DESCRIPTION = "TDC";



    private SpotTextDefinitionBuilder(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public static SpotTextDefinitionBuilder create(JSONObject jsonObject)
    {
        return new SpotTextDefinitionBuilder(jsonObject);
    }

    public SpotTextDefinitionBuilder setTextSize(int textSize) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_TEXT_SIZE, textSize);
        return this;
    }

    public SpotTextDefinitionBuilder resetToInitState(Context context)
    {
        try
        {
            setText("")
                    .setTextSize(SpotTextDefinitionBuilder.DEAULT_TEXT_SIZE)
                    .setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                    .setTextAlignment(SpotTextAlignment.ALIGNMENT_CENTER)
                    .setFontStyle(SpotTextFontStyle.FONT_NORMAL)
                    .setFontFamily(SpotTextFontFamlie.FONT_DEFAULT);
        }
        catch (Exception ec)
        {
        }
        return this;
    }

    public SpotTextDefinitionBuilder setTextColor(int textColor) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_TEXT_COLOR, textColor);
        return this;
    }

    public SpotTextDefinitionBuilder setTextAlignment(short textAlignment) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_TEXT_ALIGNMENT, textAlignment);
        return this;
    }

    public SpotTextDefinitionBuilder setFontStyle(int fontStyle) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_FONT_STYLE, fontStyle);
        return this;
    }

    public SpotTextDefinitionBuilder setFontFamily(String fontFamily) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_FONT_FAMILY, fontFamily);
        return this;
    }

    public SpotTextDefinitionBuilder setTextShader(JSONObject shader) throws JSONException
    {
        jsonObject.put(KEY_FONT_SHADER, shader);
        return this;
    }

    public SpotTextDefinitionBuilder removeTextShader()
    {
        jsonObject.remove(KEY_FONT_SHADER);
        return this;
    }

    public SpotTextDefinitionBuilder setText(String Text) throws JSONException
    {
        jsonObject.put(SpotTextDefinitionBuilder.KEY_TEXT_DESCRIPTION_CONTENT_ONLY_DESCRIPTION, Text);
        return this;
    }

    public int getTextSize() throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_SIZE);
    }

    public int getTextColor() throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_COLOR);
    }

    public short getTextAlignment() throws JSONException
    {
        return (short) jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }

    public int getFontStyle() throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_FONT_STYLE);
    }

    public String getFontFamily() throws JSONException
    {
        return jsonObject.getString(SpotTextDefinitionBuilder.KEY_FONT_FAMILY);
    }

    public JSONObject getTextShader() throws JSONException
    {
        return this.jsonObject.getJSONObject(SpotTextDefinitionBuilder.KEY_FONT_SHADER);
    }

    public String getText() throws JSONException
    {
        return jsonObject.getString(SpotTextDefinitionBuilder.KEY_TEXT_DESCRIPTION_CONTENT_ONLY_DESCRIPTION);
    }

    public static int getTextSize(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_SIZE);
    }

    public static int getTextColor(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_COLOR);
    }

    public static short getTextAlignment(JSONObject jsonObject) throws JSONException
    {
        return (short) jsonObject.getInt(SpotTextDefinitionBuilder.KEY_TEXT_ALIGNMENT);
    }

    public static int getFontStyle(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotTextDefinitionBuilder.KEY_FONT_STYLE);
    }

    public static String getFontFamily(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getString(SpotTextDefinitionBuilder.KEY_FONT_FAMILY);
    }

    public static boolean hasShader(JSONObject jsonObject)
    {
        try
        {
            return jsonObject.has(SpotTextDefinitionBuilder.KEY_FONT_SHADER);
        }
        catch (Exception e)
        {
        }

        return false;
    }

    public static JSONObject getTextShader(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getJSONObject(SpotTextDefinitionBuilder.KEY_FONT_SHADER);
    }

    public static String getText(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getString(SpotTextDefinitionBuilder.KEY_TEXT_DESCRIPTION_CONTENT_ONLY_DESCRIPTION);
    }


}
