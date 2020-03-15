package esaph.spotlight.spots.SpotMaker.Models;

import android.content.Context;
import android.graphics.Typeface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotTextFontStyle
{
    public static final int FONT_NORMAL = 0;
    public static final int FONT_BOLD = 1;
    public static final int FONT_CURSIV = 2;

    private SpotTextFontStyle()
    {
    }

    public static SpotTextFontStyle options()
    {
        return new SpotTextFontStyle();
    }

    public List<JSONObject> preview(Context context) throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();
        list.add(getObject(context, FONT_NORMAL, context.getResources().getString(R.string.txt_text_normal)));
        list.add(getObject(context, FONT_BOLD, context.getResources().getString(R.string.txt_text_bold)));
        list.add(getObject(context, FONT_CURSIV, context.getResources().getString(R.string.txt_text_cursiv)));
        return list;
    }

    private JSONObject getObject(Context context, int FONT, String Name) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(context).setTextSize(30).setTextColor(ContextCompat.getColor(context, R.color.colorWhite)).setFontStyle(FONT).setText(Name);
        SpotBackgroundDefinitionBuilder.create(jsonObject).setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkSpotPreviewBackground));
        return jsonObject;
    }

    public static Typeface getFontStyleForApiVersion(int FONT_STYLE)
    {
        switch (FONT_STYLE)
        {
            case FONT_NORMAL:
                return Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);

            case FONT_BOLD:
                return Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

            case FONT_CURSIV:
                return Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
        }
        return Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
    }
}
