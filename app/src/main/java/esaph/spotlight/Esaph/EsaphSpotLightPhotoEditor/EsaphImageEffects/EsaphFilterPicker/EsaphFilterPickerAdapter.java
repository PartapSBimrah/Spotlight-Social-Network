/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.ImageFilterView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoFilter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker.FilterViewPagerViews.EsaphFilterListNormal;
import esaph.spotlight.R;
import esaph.spotlight.navigation.SwipeNavigation;

public class EsaphFilterPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private EsaphFilterListNormal.EsaphFilterPickerListener esaphFilterPickerListener;
    private LayoutInflater inflater;
    private List<PhotoFilter> listFilters;
    private Context context;
    private EsaphFilterPickerViewBASEFragment esaphFilterPickerViewBASEFragment;
    private Bitmap bitmap;

    public EsaphFilterPickerAdapter(EsaphFilterPickerViewBASEFragment esaphFilterPickerViewBASEFragment,
                                    Bitmap bitmap,
                                    List<PhotoFilter> filters,
                                    EsaphFilterListNormal.EsaphFilterPickerListener esaphFilterPickerListener)
    {
        this.context = esaphFilterPickerViewBASEFragment.getContext();
        this.bitmap = bitmap;
        this.esaphFilterPickerListener = esaphFilterPickerListener;
        this.esaphFilterPickerViewBASEFragment = esaphFilterPickerViewBASEFragment;
        this.listFilters = filters;
        this.inflater = LayoutInflater.from(context);
        notifyDataSetChanged();
    }

    public void renderImageWithFilter(PhotoFilter photoFilter, Bitmap bitmap, ImageFilterView filterImageView)
    {
        filterImageView.setSourceBitmap(bitmap);
        filterImageView.setFilterEffect(photoFilter);
    }

    private static class ViewHolderEffekt extends RecyclerView.ViewHolder
    {
        private ImageFilterView imageFilterView;

        public ViewHolderEffekt(View view)
        {
            super(view);
            this.imageFilterView = (ImageFilterView) view.findViewById(R.id.imageFilterMainRender);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View viewPost = inflater.inflate(R.layout.layout_filter_view_item, parent, false);
        viewHolder = new ViewHolderEffekt(viewPost);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final PhotoFilter photoFilter = listFilters.get(position);
        final ViewHolderEffekt viewHolder = (ViewHolderEffekt) holder;

        renderImageWithFilter(photoFilter, bitmap, viewHolder.imageFilterView);

        viewHolder.imageFilterView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EsaphActivity esaphActivity = esaphFilterPickerViewBASEFragment.getEsaphActivity();
                if(esaphActivity instanceof SwipeNavigation)
                {
                    esaphFilterPickerListener.onFilterSelected(listFilters.get(viewHolder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return listFilters.size();
    }
}
