package esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerFormatView;

public class SpotMakerFormatStickerDefault extends SpotMakerFormatView
{
    private ImageView imageViewStickerHolder;
    private EsaphSpotLightSticker esaphSpotLightSticker;

    public SpotMakerFormatStickerDefault(Context context) {
        super(context);
    }

    public SpotMakerFormatStickerDefault(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpotMakerFormatStickerDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public SpotMakerFormatStickerDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ImageView getImageViewStickerHolder() {
        return imageViewStickerHolder;
    }

    public void setEsaphSpotLightSticker(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        this.esaphSpotLightSticker = esaphSpotLightSticker;
    }

    @Override
    public int inflateLayout(Context context)
    {
        return R.layout.layout_spot_format_sticker_normal;
    }

    @Override
    public void onSetupView(View view)
    {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        imageViewStickerHolder = (ImageView) view.findViewById(R.id.imageViewStickerSpotFormatView);
    }

    @Override
    public void onValuesChanges(JSONObject jsonObject)
    {
        try
        {
            setBackgroundColor(SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject));

            EsaphGlobalImageLoader.with(getContext()).displayImage(
                    StickerRequest.builder(
                            esaphSpotLightSticker.getIMAGE_ID(),
                            imageViewStickerHolder,
                    null,
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_sticker_missing));
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    public ConversationMessage getSpotMessage(JSONObject jsonObject)
    {
        return new EsaphStickerChatObject(
                -1,
                -1,
                -1,
                System.currentTimeMillis(),
                ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                esaphSpotLightSticker,
                "",
                jsonObject.toString());
    }
}
