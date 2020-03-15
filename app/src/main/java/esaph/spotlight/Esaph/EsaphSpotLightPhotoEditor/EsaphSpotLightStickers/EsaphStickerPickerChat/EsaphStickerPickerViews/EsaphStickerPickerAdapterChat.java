/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class EsaphStickerPickerAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<EsaphSpotLightSticker> listDisplay;
    private Context context;
    private EsaphStickerViewBASEFragmentChat esaphStickerViewBASEFragment;

    public EsaphStickerPickerAdapterChat(EsaphStickerViewBASEFragmentChat esaphGlobalCommunicationFragment,
                                         List<EsaphSpotLightSticker> stickers)
    {
        this.context = esaphGlobalCommunicationFragment.getContext();
        this.esaphStickerViewBASEFragment = esaphGlobalCommunicationFragment;
        this.listDisplay = stickers;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnStickerSelectedListenerChat extends Serializable
    {
        void onStickerSelected(EsaphStickerChatObject esaphStickerChatObject);
        void onStickerLongClick(EsaphSpotLightSticker esaphSpotLightSticker);
    }

    private static class ViewHolderSmiley extends RecyclerView.ViewHolder
    {
        private ImageView imageViewSticker;

        public ViewHolderSmiley(View view)
        {
            super(view);
            this.imageViewSticker = (ImageView) view.findViewById(R.id.imageViewSticker);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View viewPost = inflater.inflate(R.layout.layout_sticker_picker_item, parent, false);
        viewHolder = new ViewHolderSmiley(viewPost);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final EsaphSpotLightSticker esaphSpotLightSticker = listDisplay.get(position);
        final ViewHolderSmiley viewHolderSmiley = (ViewHolderSmiley) holder;

        viewHolderSmiley.imageViewSticker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bitmap bm = null;
                try
                {
                    bm = ((BitmapDrawable) viewHolderSmiley.imageViewSticker.getDrawable()).getBitmap();
                }
                catch (Exception ec)
                {
                }

                if(bm != null)
                {
                    try
                    {
                        OnStickerSelectedListenerChat onStickerSelectedListenerChat = esaphStickerViewBASEFragment.getOnStickerSelectedListenerChat();
                        if(onStickerSelectedListenerChat != null)
                        {
                            onStickerSelectedListenerChat.onStickerSelected(new EsaphStickerChatObject(
                                    -1,
                                    SpotLightLoginSessionHandler.getLoggedUID(),
                                    -1,
                                    System.currentTimeMillis(),
                                    ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                                    esaphSpotLightSticker,
                                    SpotLightLoginSessionHandler.getLoggedUsername(),
                                    ""));
                        }
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "onClick, Sticker Selected(): " + ec);
                    }

                }
            }
        });

        viewHolderSmiley.imageViewSticker.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                OnStickerSelectedListenerChat onStickerSelectedListenerChat = esaphStickerViewBASEFragment.getOnStickerSelectedListenerChat();
                if(onStickerSelectedListenerChat != null)
                {
                    onStickerSelectedListenerChat.onStickerLongClick(esaphSpotLightSticker); //shouldnt be there, function will get deprecated.
                }
                return true;
            }
        });

        EsaphGlobalImageLoader.with(context).displayImage(
                StickerRequest.builder(
                        esaphSpotLightSticker.getIMAGE_ID(),
                        viewHolderSmiley.imageViewSticker,
                null,
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_sticker_missing));
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size();
    }
}
