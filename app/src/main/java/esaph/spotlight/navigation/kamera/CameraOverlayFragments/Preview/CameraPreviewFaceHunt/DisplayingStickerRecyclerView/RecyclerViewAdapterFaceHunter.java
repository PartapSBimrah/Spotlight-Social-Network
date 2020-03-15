package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt.DisplayingStickerRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.AsyncSaveSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.DialogAddStickerToPack;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.R;

public class RecyclerViewAdapterFaceHunter extends RecyclerView.Adapter<RecyclerViewAdapterFaceHunter.ViewHolderFaceHuntedSticker>
{
    private Context context;
    private LayoutInflater inflater;
    private List<EsaphSpotLightSticker> list;

    public RecyclerViewAdapterFaceHunter(Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();
    }

    public void addFace(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        list.add(esaphSpotLightSticker);
        notifyDataSetChanged();
    }

    public static class ViewHolderFaceHuntedSticker extends RecyclerView.ViewHolder
    {
        private TextView textViewAdd;
        private ImageView imageViewStickerPreview;

        public ViewHolderFaceHuntedSticker(@NonNull View itemView)
        {
            super(itemView);
            this.textViewAdd = itemView.findViewById(R.id.textViewFaceHunterPreviewAdd);
            this.imageViewStickerPreview = itemView.findViewById(R.id.imageViewFaceHunterPreview);
        }
    }

    @NonNull
    @Override
    public ViewHolderFaceHuntedSticker onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderFaceHuntedSticker(inflater.inflate(R.layout.layout_face_hunter_item, parent, false));
    }


    private TextView textViewConfirmAddingDialog;
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderFaceHuntedSticker holder, int position)
    {
        holder.textViewAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EsaphSpotLightSticker esaphSpotLightSticker = list.get(holder.getAdapterPosition());
                final DialogAddStickerToPack dialogAddStickerToPack = new DialogAddStickerToPack(context,
                        esaphSpotLightSticker,
                        new DialogAddStickerToPack.ItemSelectListener()
                        {
                            @Override
                            public void onSelectionChanged(int totalCount)
                            {
                                if(totalCount > 0)
                                {
                                    textViewConfirmAddingDialog.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    textViewConfirmAddingDialog.setVisibility(View.GONE);
                                }
                            }
                        });

                dialogAddStickerToPack.show();


                textViewConfirmAddingDialog = (TextView) dialogAddStickerToPack.findViewById(R.id.textViewAddStickerConfirm);
                textViewConfirmAddingDialog.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        List<EsaphSpotLightStickerPack> listSelected = dialogAddStickerToPack.getAdapterShowStickerPacks().getSelectedItems();
                        new AsyncSaveSticker(context, listSelected, esaphSpotLightSticker,
                                new AsyncSaveSticker.StickerSavingListener()
                                {
                                    @Override
                                    public void onStickerUpdate(EsaphSpotLightSticker esaphSpotLightSticker)
                                    {
                                        if(esaphSpotLightSticker.isSelected())
                                        {
                                            removeSticker(esaphSpotLightSticker);
                                        }
                                    }
                                }).execute();
                        dialogAddStickerToPack.dismiss();
                    }
                });
            }
        });


        EsaphGlobalImageLoader.with(context).displayImage(StickerRequest.builder(
                list.get(position).getIMAGE_ID(),
                holder.imageViewStickerPreview,
                null,
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_sticker_missing
        ));
    }

    public void removeSticker(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        int length = list.size();
        for(int counter = 0; counter < length; counter++)
        {
            EsaphSpotLightSticker esaphSpotLightStickerList = list.get(counter);
            if(esaphSpotLightStickerList.getSTICKER_ID() == esaphSpotLightSticker.getSTICKER_ID())
            {
                list.remove(counter);
                notifyItemRemoved(counter);
                break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
