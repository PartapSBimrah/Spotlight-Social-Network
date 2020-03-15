package esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittextEmojie;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerFormatView;

public class SpotMakerFormatEmojieDefault extends SpotMakerFormatView
{
    private SpotMakerEdittextEmojie spotMakerEdittextEmojie;

    public SpotMakerFormatEmojieDefault(Context context) {
        super(context);
    }

    public SpotMakerFormatEmojieDefault(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpotMakerFormatEmojieDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public SpotMakerFormatEmojieDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SpotMakerEdittextEmojie getSpotMakerEdittextEmojie() {
        return spotMakerEdittextEmojie;
    }

    @Override
    public int inflateLayout(Context context)
    {
        return R.layout.layout_spot_format_emojie_normal;
    }

    @Override
    public void onSetupView(View view)
    {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        spotMakerEdittextEmojie = (SpotMakerEdittextEmojie) view.findViewById(R.id.spotMakerEditText);
    }

    @Override
    public void onValuesChanges(JSONObject jsonObject)
    {
        try
        {
            spotMakerEdittextEmojie.onValuesChanged(jsonObject);
            setBackgroundColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    public ConversationMessage getSpotMessage(JSONObject jsonObject)
    {
        Editable editable = spotMakerEdittextEmojie.getText();

        if(editable != null)
        {
            String message = editable.toString();
            if(!message.isEmpty())
            {
                return new EsaphAndroidSmileyChatObject(
                        -1,
                        -1,
                        -1,
                        System.currentTimeMillis(),
                        ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                        new EsaphEmojie(message),
                        "",
                        jsonObject.toString());
            }
        }

        return null;
    }
}
