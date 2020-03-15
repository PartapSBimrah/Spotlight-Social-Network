package esaph.spotlight.spots.SpotMaker.SpotMakerView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotAudioDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotEmojieDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotsStickerDefinitionBuilder;

public class SpotMakerView extends FrameLayout
{
    private Context context;
    private JSONObject jsonObject;
    private SpotMakerFormatView spotMakerFormatView;
    private SpotTextDefinitionBuilder spotTextDefinitionBuilder;
    private SpotBackgroundDefinitionBuilder spotBackgroundDefinitionBuilder;
    private SpotEmojieDefinitionBuilder spotEmojieDefinitionBuilder;
    private SpotAudioDefinitionBuilder spotAudioDefinitionBuilder;
    private SpotsStickerDefinitionBuilder spotsStickerDefinitionBuilder;

    public SpotMakerView(Context context)
    {
        super(context);
        init(context);
    }

    public SpotMakerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SpotMakerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        jsonObject = new JSONObject();
    }

    @RequiresApi(21)
    public SpotMakerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SpotMakerView from(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
        return this;
    }

    public ConversationMessage getSpotMessage()
    {
        return spotMakerFormatView.getSpotMessage(jsonObject);
    }

    public void setFormat(SpotMakerFormatView spotMakerFormatView)
    {
        this.spotMakerFormatView = spotMakerFormatView;
        addView(spotMakerFormatView);
    }

    public SpotTextDefinitionBuilder obtainText()
    {
        if(spotTextDefinitionBuilder == null) spotTextDefinitionBuilder = SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(context);
        return spotTextDefinitionBuilder;
    }

    public SpotEmojieDefinitionBuilder obtainEmojie()
    {
        if(spotEmojieDefinitionBuilder == null) spotEmojieDefinitionBuilder = SpotEmojieDefinitionBuilder.create(jsonObject).resetToInitState(context);
        return spotEmojieDefinitionBuilder;
    }

    public SpotAudioDefinitionBuilder obtainAudio()
    {
        if(spotAudioDefinitionBuilder == null) spotAudioDefinitionBuilder = SpotAudioDefinitionBuilder.create(jsonObject).resetToInitState(context);
        return spotAudioDefinitionBuilder;
    }

    public SpotsStickerDefinitionBuilder obtainSticker()
    {
        if(spotsStickerDefinitionBuilder == null) spotsStickerDefinitionBuilder = SpotsStickerDefinitionBuilder.create(jsonObject).resetToInitState(context);
        return spotsStickerDefinitionBuilder;
    }

    public SpotBackgroundDefinitionBuilder obtainBackground()
    {
        if(spotBackgroundDefinitionBuilder == null) spotBackgroundDefinitionBuilder = SpotBackgroundDefinitionBuilder.create(jsonObject).resetToInitState(context);
        return spotBackgroundDefinitionBuilder;
    }

    public void commit()
    {
        spotMakerFormatView.onValuesChanges(jsonObject);
    }
}
