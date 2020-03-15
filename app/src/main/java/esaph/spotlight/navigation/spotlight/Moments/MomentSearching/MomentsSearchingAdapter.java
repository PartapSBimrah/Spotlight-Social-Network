/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MomentSearching;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemImage;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemMainMoments;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemVideo;

public class MomentsSearchingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private List<SearchItemMainMoments> list;

    public MomentsSearchingAdapter(Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();
    }

    public void setSearchData(List<SearchItemMainMoments> listNewData)
    {
        this.list.clear();
        this.list.addAll(listNewData);
        notifyDataSetChanged();
    }

    private static class ViewHolderImage extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        private TextView textViewUsername;
        private TextView textViewReasonFound;

        public ViewHolderImage(@NonNull View itemView)
        {
            super(itemView);
            this.roundedImageView = itemView.findViewById(R.id.roundedImageViewSearchItem);
            this.textViewReasonFound = itemView.findViewById(R.id.textViewReasonFound);
        }
    }


    private static class ViewHolderVideo extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        private TextView textViewUsername;
        private TextView textViewReasonFound;

        public ViewHolderVideo(@NonNull View itemView) {
            super(itemView);
            this.roundedImageView = itemView.findViewById(R.id.roundedImageViewSearchItem);
            this.textViewReasonFound = itemView.findViewById(R.id.textViewReasonFound);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case SearchItemMainMoments.TYPE_IMAGE:
                View viewImage = inflater.inflate(R.layout.search_item_moments_image, parent, false);
                viewHolder = new ViewHolderImage(viewImage);
                break;

            case SearchItemMainMoments.TYPE_VIDEO:
                View viewVideo = inflater.inflate(R.layout.search_item_moments_video, parent, false);
                viewHolder = new ViewHolderVideo(viewVideo);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        SearchItemMainMoments searchItemMainMoments = list.get(position);
        switch (getItemViewType(position))
        {
            case SearchItemMainMoments.TYPE_IMAGE:
                SearchItemImage searchItemImage = (SearchItemImage) searchItemMainMoments;
                ViewHolderImage viewHolderImage = (ViewHolderImage) holder;

                viewHolderImage.textViewReasonFound.setText(searchItemImage.getReasonFound());

                EsaphGlobalImageLoader.with(context).displayImage(
                        ImageRequest.builder(
                        searchItemImage.getmPID(),
                        viewHolderImage.roundedImageView,
                        null,
                        new EsaphDimension(viewHolderImage.roundedImageView.getWidth(),
                                viewHolderImage.roundedImageView.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_rounded_corners));
                break;


            case SearchItemMainMoments.TYPE_VIDEO:
                SearchItemVideo searchItemVideo = (SearchItemVideo) searchItemMainMoments;
                ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;

                viewHolderVideo.textViewReasonFound.setText(searchItemVideo.getReasonFound());

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        searchItemVideo.getmPID(),
                        viewHolderVideo.roundedImageView,
                        null,
                        new EsaphDimension(viewHolderVideo.roundedImageView.getWidth(),
                                viewHolderVideo.roundedImageView.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_rounded_corners));
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return list.get(position).getTYPE();
    }
}
