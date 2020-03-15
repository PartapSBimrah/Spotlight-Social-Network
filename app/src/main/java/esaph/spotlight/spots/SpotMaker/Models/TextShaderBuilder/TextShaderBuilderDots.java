package esaph.spotlight.spots.SpotMaker.Models.TextShaderBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.Shaders.ShaderDots;

public class TextShaderBuilderDots extends TextShaderBuilder
{
    public static final short SHADER_DOT = 1;

    public TextShaderBuilderDots(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        jsonObject.put(SpotTextShader.KEY_FONT_SHADER_TYPE, TextShaderBuilderDots.SHADER_DOT);
    }

    public static TextShaderBuilderDots from(JSONObject jsonObject) throws JSONException
    {
        return new TextShaderBuilderDots(jsonObject);
    }

    @Override
    public EsaphShader getShader() throws JSONException
    {
        return new ShaderDots();
    }
}
