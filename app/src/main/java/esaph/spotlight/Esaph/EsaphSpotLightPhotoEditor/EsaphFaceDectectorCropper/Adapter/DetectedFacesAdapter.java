/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.EsaphFaceDetectorPicker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;

public class DetectedFacesAdapter extends RecyclerView.Adapter<DetectedFacesAdapter.ViewHolderDetectedFace>
{
    private Context context;
    private EsaphFaceDetectorPicker esaphFaceDetectorPicker;
    private LayoutInflater inflater;
    private List<EsaphSpotLightSticker> listStickers;

    public DetectedFacesAdapter(EsaphFaceDetectorPicker esaphFaceDetectorPicker,
                                Context context,
                                List<EsaphSpotLightSticker> spotLightStickers)
    {
        this.context = context;
        this.esaphFaceDetectorPicker = esaphFaceDetectorPicker;
        this.inflater = LayoutInflater.from(context);
        this.listStickers = spotLightStickers;
    }

    public void updateItem(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        int size = listStickers.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(listStickers.get(counter).getSTICKER_ID() == esaphSpotLightSticker.getSTICKER_ID())
            {
                listStickers.set(counter, esaphSpotLightSticker);
                notifyItemChanged(counter);
                break;
            }
        }
    }

    public class ViewHolderDetectedFace extends RecyclerView.ViewHolder
    {
        private ProgressBar progressBar;
        private TextView textViewStickerState;
        private ImageView imageViewSticker;

        public ViewHolderDetectedFace(@NonNull View itemView)
        {
            super(itemView);
            this.progressBar = itemView.findViewById(R.id.progressBarLoading);
            this.textViewStickerState = itemView.findViewById(R.id.textViewStickerSaveState);
            this.imageViewSticker = itemView.findViewById(R.id.imageViewSticker);
        }
    }

    @NonNull
    @Override
    public ViewHolderDetectedFace onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderDetectedFace(inflater.inflate(R.layout.layout_item_detected_face, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderDetectedFace holder, int position)
    {
        final EsaphSpotLightSticker esaphSpotLightSticker = listStickers.get(position);

        if(esaphSpotLightSticker.isSelected())
        {
            Glide.with(context).load(R.drawable.background_add_sticker_added).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        holder.textViewStickerState.setBackground(resource);
                    }
                }
            });
            holder.textViewStickerState.setText(context.getResources().getString(R.string.txt_undo));
        }
        else
        {
            Glide.with(context).load(R.drawable.background_add_sticker_add).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        holder.textViewStickerState.setBackground(resource);
                    }
                }
            });
            holder.textViewStickerState.setText(context.getResources().getString(R.string.txt_saveSticker));
        }

        holder.textViewStickerState.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                esaphFaceDetectorPicker.handleStickerAddClick(esaphSpotLightSticker);
            }
        });

        EsaphGlobalImageLoader.with(context).displayImage(
                StickerRequest.builder(esaphSpotLightSticker.getIMAGE_ID(),
                        holder.imageViewSticker,
                        holder.progressBar,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_sticker_missing).setTemp(true));
    }

    @Override
    public int getItemCount()
    {
        return listStickers.size();
    }
}
