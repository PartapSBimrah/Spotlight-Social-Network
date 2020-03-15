/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.R;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AdapterShowStickerPacksForAdding extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private LayoutInflater inflater;
    private List<EsaphSpotLightStickerPack> listDisplay;
    private List<EsaphSpotLightStickerPack> listOriginal;
    private EsaphSpotLightSticker esaphSpotLightStickerAdding;
    private Context context;
    private DialogAddStickerToPack.ItemSelectListener itemSelectListener;
    private DialogAddStickerToPack dialogAddStickerToPack;
    private LinearLayout linearLayoutShowNoStickerPacks;

    public AdapterShowStickerPacksForAdding(Context context,
                                            LinearLayout linearLayoutShowNoStickerPacks,
                                            DialogAddStickerToPack dialogAddStickerToPack,
                                            EsaphSpotLightSticker esaphSpotLightStickerAdding,
                                            DialogAddStickerToPack.ItemSelectListener itemSelectListener)
    {
        this.context = context;
        this.linearLayoutShowNoStickerPacks = linearLayoutShowNoStickerPacks;
        this.dialogAddStickerToPack = dialogAddStickerToPack;
        this.esaphSpotLightStickerAdding = esaphSpotLightStickerAdding;
        this.listDisplay = new ArrayList<>();
        this.listOriginal = new ArrayList<>();
        this.itemSelectListener = itemSelectListener;
        this.inflater = LayoutInflater.from(context);
    }

    public void clear()
    {
        listDisplay.clear();
        listOriginal.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<EsaphSpotLightStickerPack> esaphSpotLightStickerPacks)
    {
        listDisplay.addAll(esaphSpotLightStickerPacks);
        listOriginal.addAll(esaphSpotLightStickerPacks);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results)
            {
                List<EsaphSpotLightStickerPack> listFiltered = (ArrayList<EsaphSpotLightStickerPack>) results.values; // has the filtered values
                if(listFiltered.isEmpty())
                {
                    if(constraint.toString().isEmpty())
                    {
                        listFiltered.clear();
                    }
                    else
                    {
                        handleStickerPackNameCreating(listFiltered, constraint.toString());
                    }
                }

                listDisplay = listFiltered;

                if(getItemCount() == 0)
                {
                    linearLayoutShowNoStickerPacks.setVisibility(View.VISIBLE);
                }
                else
                {
                    linearLayoutShowNoStickerPacks.setVisibility(View.GONE);
                }

                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<EsaphSpotLightStickerPack> FilteredArrList = new ArrayList<EsaphSpotLightStickerPack>();

                if (listOriginal == null)
                {
                    listOriginal = new ArrayList<EsaphSpotLightStickerPack>(listDisplay); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0)
                {
                    // set the Original result to return
                    results.count = listOriginal.size();
                    results.values = listOriginal;
                }
                else
                {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listOriginal.size(); i++)
                    {
                        EsaphSpotLightStickerPack esaphSpotLightStickerPack = listOriginal.get(i);
                        if(esaphSpotLightStickerPack.getPACK_NAME().toLowerCase().startsWith(constraint.toString()))
                        {
                            EsaphSpotLightStickerPack esaphSpotLightStickerPackIntern =
                                    new EsaphSpotLightStickerPack(esaphSpotLightStickerPack.getPACK_NAME(),
                                            esaphSpotLightStickerPack.getLSPID(),
                                            esaphSpotLightStickerPack.getUIDCreator(),
                                            esaphSpotLightStickerPack.getTimeCreated(),
                                            esaphSpotLightStickerPack.getEsaphSpotLightStickers());

                            FilteredArrList.add(esaphSpotLightStickerPackIntern);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }



    private List<EsaphSpotLightStickerPack> arrayListSelectedStickerPacks = new ArrayList<>();

    public List<EsaphSpotLightStickerPack> getSelectedItems()
    {
        return arrayListSelectedStickerPacks;
    }

    public boolean isItemSelected(EsaphSpotLightStickerPack esaphSpotLightStickerPack)
    {
        for(EsaphSpotLightStickerPack esaphSpotLightStickerPackList : arrayListSelectedStickerPacks)
        {
            if(esaphSpotLightStickerPackList.getLSPID() == esaphSpotLightStickerPack.getLSPID())
            {
                return true;
            }
        }
        return false;
    }

    private void removeSelection(EsaphSpotLightStickerPack esaphSpotLightStickerPack)
    {
        for(EsaphSpotLightStickerPack esaphSpotLightStickerPackList : arrayListSelectedStickerPacks)
        {
            if(esaphSpotLightStickerPackList.getLSPID() == esaphSpotLightStickerPack.getLSPID())
            {
                arrayListSelectedStickerPacks.remove(esaphSpotLightStickerPackList);
                break;
            }
        }
    }


    private static class ViewHolderStickerPack extends RecyclerView.ViewHolder
    {
        private RelativeLayout relativeLayout;
        private EsaphCircleImageView imageViewStickerPackPreview;
        private TextView textViewStickerPackName;

        public ViewHolderStickerPack(View view)
        {
            super(view);
            this.imageViewStickerPackPreview = (EsaphCircleImageView) view.findViewById(R.id.imageViewStickersPreview);
            this.textViewStickerPackName = (TextView) view.findViewById(R.id.textViewStickerPackname);
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.rootViewStickerPackPicker);
        }
    }

    private static class ViewHolderNewStickerPack extends RecyclerView.ViewHolder
    {
        private RelativeLayout relativeLayoutRootView;
        private TextView textViewStickerPackName;

        public ViewHolderNewStickerPack(View view)
        {
            super(view);
            this.relativeLayoutRootView = (RelativeLayout) view.findViewById(R.id.rootViewStickerPackPicker);
            this.textViewStickerPackName = (TextView) view.findViewById(R.id.textViewStickerPackname);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType)
        {
            case 0:
                viewHolder = new ViewHolderStickerPack(inflater.inflate(R.layout.sticker_pack_choose_listview_item, parent, false));
                break;

            case 1:
                viewHolder = new ViewHolderNewStickerPack(inflater.inflate(R.layout.sticker_pack_create_new_listview_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final EsaphSpotLightStickerPack esaphSpotLightStickerPack = listDisplay.get(position);

        switch (getItemViewType(position))
        {
            case 0:
                final ViewHolderStickerPack viewHolderStickerPack = (ViewHolderStickerPack) holder;
                viewHolderStickerPack.textViewStickerPackName.setText(esaphSpotLightStickerPack.getPACK_NAME());

                if(isItemSelected(esaphSpotLightStickerPack))
                {
                    viewHolderStickerPack.textViewStickerPackName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                    viewHolderStickerPack.imageViewStickerPackPreview.setBorderColorBackground(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                }
                else
                {
                    viewHolderStickerPack.textViewStickerPackName.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    viewHolderStickerPack.imageViewStickerPackPreview.setBorderColorBackground(ContextCompat.getColor(context, R.color.colorTransparent));
                }

                viewHolderStickerPack.relativeLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if(isItemSelected(esaphSpotLightStickerPack))
                        {
                            removeSelection(esaphSpotLightStickerPack);
                        }
                        else
                        {
                            arrayListSelectedStickerPacks.add(esaphSpotLightStickerPack);
                        }

                        if(itemSelectListener != null)
                        {
                            itemSelectListener.onSelectionChanged(getSelectedItems().size());
                        }

                        notifyItemChanged(viewHolderStickerPack.getLayoutPosition());
                    }
                });

                if(esaphSpotLightStickerPack.getEsaphSpotLightStickers().size() > 0)
                {
                    EsaphGlobalImageLoader.with(context).displayImage(
                            StickerRequest.builder(
                                    esaphSpotLightStickerPack.getEsaphSpotLightStickers().get(0).getIMAGE_ID(),
                            viewHolderStickerPack.imageViewStickerPackPreview,
                            null,
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_sticker_missing));
                }

                break;

            case 1:
                final ViewHolderNewStickerPack viewHolderNewStickerPack = (ViewHolderNewStickerPack) holder;
                viewHolderNewStickerPack.relativeLayoutRootView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        dialogAddStickerToPack.onStartNewStickerPack();
                    }
                });

                viewHolderNewStickerPack.textViewStickerPackName.setText(esaphSpotLightStickerPack.getPACK_NAME());
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size();
    }

    private void handleStickerPackNameCreating(List<EsaphSpotLightStickerPack> list, String NAME)
    {
        try
        {
            List<EsaphSpotLightSticker> esaphSpotLightStickers = new ArrayList<>();
            esaphSpotLightStickers.add(esaphSpotLightStickerAdding);

            EsaphSpotLightStickerPack esaphSpotLightStickerPackIntern =
                    new EsaphSpotLightStickerPack(NAME,
                            System.currentTimeMillis(),
                            SpotLightLoginSessionHandler.getLoggedUID(),
                            System.currentTimeMillis(),
                            esaphSpotLightStickers);

            esaphSpotLightStickerPackIntern.setViewType(EsaphSpotLightStickerPack.VIEWTYPE_STICKER_NEW);
            list.add(esaphSpotLightStickerPackIntern);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "handleStickerPackNameCreating() failed: " + ec);
        }
    }

    public void insertNew(EsaphSpotLightStickerPack esaphSpotLightStickerPack)
    {
        listOriginal.add(esaphSpotLightStickerPack);
        listDisplay.add(esaphSpotLightStickerPack);
        notifyDataSetChanged();
        if(itemSelectListener != null)
        {
            itemSelectListener.onSelectionChanged(getSelectedItems().size());
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return listDisplay.get(position).getViewType();
    }
}
