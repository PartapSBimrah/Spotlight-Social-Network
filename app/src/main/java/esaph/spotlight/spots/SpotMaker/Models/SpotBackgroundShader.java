package esaph.spotlight.spots.SpotMaker.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotBackgroundShader
{
    private SpotBackgroundShader()
    {
    }

    public static SpotBackgroundShader options()
    {
        return new SpotBackgroundShader();
    }

    private final short SHADER_GLOW = 0;
    private final short SHADER_DOT = 1;
    private final short SHADER_ = 2;


    public List<JSONObject> preview() throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();
        list.add(getObject(SHADER_GLOW));
        list.add(getObject(SHADER_DOT));
        return list;
    }

    private JSONObject getObject(short SHADER) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObject).setTextColor(R.color.colorWhite).setTextSize(15);
        SpotBackgroundDefinitionBuilder.create(jsonObject).setBackgroundShader(SHADER);
        return jsonObject;
    }

}
