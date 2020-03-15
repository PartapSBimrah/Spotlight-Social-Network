package esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerFormatView;

public class SpotMakerFormatTextDefault extends SpotMakerFormatView
{
    private SpotMakerEdittext spotMakerEdittext;

    public SpotMakerFormatTextDefault(Context context) {
        super(context);
    }

    public SpotMakerFormatTextDefault(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpotMakerFormatTextDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public SpotMakerFormatTextDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SpotMakerEdittext getSpotMakerEdittext() {
        return spotMakerEdittext;
    }

    @Override
    public int inflateLayout(Context context)
    {
        return R.layout.layout_spot_format_text_normal;
    }

    @Override
    public void onSetupView(View view)
    {
        spotMakerEdittext = (SpotMakerEdittext) view.findViewById(R.id.spotMakerEditText);
    }

    @Override
    public void onValuesChanges(JSONObject jsonObject)
    {
        try
        {
            spotMakerEdittext.onValuesChanged(jsonObject);
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    public ConversationMessage getSpotMessage(JSONObject jsonObject)
    {
        Editable editable = spotMakerEdittext.getText();

        if(editable != null)
        {
            String message = editable.toString();

            if(!message.isEmpty())
            {
                int startPoint = message.lastIndexOf("\n");
                int realStartPointOfFirst = message.indexOf("\n");

                if (message.matches("[\\n\\r]+")) {
                    return null;
                }

                if (startPoint > 0) {
                    if (startPoint + 1 == message.length()) {
                        message = message.substring(0, realStartPointOfFirst) + message.substring(startPoint, message.length()).replace("\n", "").replace("\r", "");
                    }
                }

                return new ChatTextMessage(
                        message,
                        -1,
                        -1,
                        -1,
                        System.currentTimeMillis(),
                        ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                        "",
                        jsonObject.toString());
            }
        }

        return null;
    }
}
