package esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects;

import android.content.Context;
import android.util.AttributeSet;

import org.json.JSONObject;

import androidx.appcompat.widget.AppCompatEditText;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotEmojieDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;

public class SpotMakerEdittextEmojie extends AppCompatEditText
{
    private EsaphShader esaphShader;

    public SpotMakerEdittextEmojie(Context context)
    {
        super(context);
    }

    public SpotMakerEdittextEmojie(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SpotMakerEdittextEmojie(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void onValuesChanged(JSONObject jsonObject)
    {
        try
        {
            setTextSize(SpotEmojieDefinitionBuilder.getTextSize(jsonObject));
            setGravity(SpotTextAlignment.getGravityForApiVersion(SpotEmojieDefinitionBuilder.getTextAlignment(jsonObject)));
        }
        catch (Exception ec)
        {
        }
        finally
        {
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if(this.esaphShader != null)
        {
            this.esaphShader.onLayout(getHeight(), getWidth());
            this.esaphShader.onDrawShader(getPaint());
        }
    }

    private void setEsaphShader(EsaphShader esaphShader)
    {
        this.esaphShader = esaphShader;
    }
}
