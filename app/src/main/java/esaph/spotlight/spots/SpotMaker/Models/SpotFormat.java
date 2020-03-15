package esaph.spotlight.spots.SpotMaker.Models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotFormat
{
    private SpotFormat()
    {
    }

    public static SpotFormat options()
    {
        return new SpotFormat();
    }

    private final short SPOT_FORMAT_SUNSET = 0;
    private final short SPOT_FORMAT_GOLD = 1;
    private final short SPOT_FORMAT_HEART = 2;

    public List<JSONObject> preview(Context context) throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();
        list.add(getObject(context, SPOT_FORMAT_SUNSET));
        list.add(getObject(context, SPOT_FORMAT_GOLD));
        list.add(getObject(context, SPOT_FORMAT_HEART));
        return list;
    }

    private JSONObject getObject(Context context, short SPOT_FORMAT) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(context);
        SpotBackgroundDefinitionBuilder.create(jsonObject).resetToInitState(context).setBackgroundColor(R.color.colorDarkSpotPreviewBackground).setSpotFormat(SPOT_FORMAT);
        return jsonObject;
    }
}
