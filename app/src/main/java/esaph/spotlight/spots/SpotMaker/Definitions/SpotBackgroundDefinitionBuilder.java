package esaph.spotlight.spots.SpotMaker.Definitions;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import androidx.core.content.ContextCompat;
import esaph.spotlight.R;

public class SpotBackgroundDefinitionBuilder
{
    private JSONObject jsonObject;
    public static final String KEY_BACKGROUND_SHADER = "FSH";
    public static final String KEY_BACKGROUND_COLOR = "BGC";
    public static final String KEY_SPOT_FORMAT = "SFP";

    private SpotBackgroundDefinitionBuilder(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public static SpotBackgroundDefinitionBuilder create(JSONObject jsonObject)
    {
        return new SpotBackgroundDefinitionBuilder(jsonObject);
    }

    public SpotBackgroundDefinitionBuilder resetToInitState(Context context)
    {
        try
        {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
        catch (Exception ec)
        {
        }
        return this;
    }

    public SpotBackgroundDefinitionBuilder setBackgroundColor(int backgroundColor) throws JSONException
    {
        jsonObject.put(SpotBackgroundDefinitionBuilder.KEY_BACKGROUND_COLOR, backgroundColor);
        return this;
    }

    public SpotBackgroundDefinitionBuilder setBackgroundShader(int backgroundShader) throws JSONException
    {
        jsonObject.put(SpotBackgroundDefinitionBuilder.KEY_BACKGROUND_SHADER, backgroundShader);
        return this;
    }

    public SpotBackgroundDefinitionBuilder setSpotFormat(int spotFormat) throws JSONException
    {
        jsonObject.put(SpotBackgroundDefinitionBuilder.KEY_SPOT_FORMAT, spotFormat);
        return this;
    }


    public static int getBackgroundColor(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotBackgroundDefinitionBuilder.KEY_BACKGROUND_COLOR);
    }

    public static int getBackgroundShader(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotBackgroundDefinitionBuilder.KEY_BACKGROUND_SHADER);
    }

    public static int getSpotFormat(JSONObject jsonObject) throws JSONException
    {
        return jsonObject.getInt(SpotBackgroundDefinitionBuilder.KEY_SPOT_FORMAT);
    }
}
