/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

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
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerFormatView;

public class SpotMakerFormatAudioDefault extends SpotMakerFormatView
{
    public SpotMakerFormatAudioDefault(Context context) {
        super(context);
    }

    public SpotMakerFormatAudioDefault(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpotMakerFormatAudioDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public SpotMakerFormatAudioDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int inflateLayout(Context context)
    {
        return R.layout.layout_spot_format_audio;
    }

    @Override
    public void onSetupView(View view)
    {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
    }

    @Override
    public void onValuesChanges(JSONObject jsonObject)
    {
        try
        {
            setBackgroundColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    public ConversationMessage getSpotMessage(JSONObject jsonObject)
    {
        return new AudioMessage(
                -1,
                -1,
                -1,
                System.currentTimeMillis(),
                ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                "",
                "",
                jsonObject.toString());
    }
}
