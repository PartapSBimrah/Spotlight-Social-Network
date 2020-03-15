/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MomentsRecylerViewAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGradientChooser;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;

public class MomentsRecylerViewAdapter extends EsaphMomentsRecylerView
{
    public static final int total_column_count = 2;
    private MomentsRecyclerViewAdapterClickListener momentsRecyclerViewAdapterClickListener;
    private Context context;
    private LayoutInflater inflater;
    private List<Object> list;

    public MomentsRecylerViewAdapter(Context context,
                                     MomentsRecyclerViewAdapterClickListener momentsRecyclerViewAdapterClickListener,
                                     WeakReference<View>[] views)
    {
        super(views);
        this.momentsRecyclerViewAdapterClickListener = momentsRecyclerViewAdapterClickListener;
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>();
        this.context = context;
    }

    public interface MomentsRecyclerViewAdapterClickListener
    {
        void onItemClicked(ChatPartner chatPartner);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        super.notifyDataSetChangeBypass();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public List<Object> getListDataDisplay() {
        return list;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        list.addAll(data);
    }

    @Override
    public void clearAllWithNotify() {
        list.clear();
        notifyDataSetChangeBypass();
    }

    @Override
    public void removeSinglePostByPID(String PID) {

    }

    @Override
    public int[] getObjectCountsThreadSafe()
    {
        return new int[]{list.size()};
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public void addFooter()
    {

    }

    @Override
    public void removeFooter()
    {
    }

    private static class ViewHolderUserItem extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        private TextView textViewUsername;
        private TextView textViewImageCount;

        public ViewHolderUserItem(@NonNull View itemView)
        {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.roundedImageViewMomentsRecylerView);
            textViewUsername = itemView.findViewById(R.id.textViewUsernameMomentsRecyclerView);
            textViewImageCount = itemView.findViewById(R.id.textViewMomentsCountRecyclerView);
        }
    }

    private static class ViewHolderDeadUser extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        private TextView textViewUsername;
        private TextView textViewImageCount;

        public ViewHolderDeadUser(@NonNull View itemView)
        {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.roundedImageViewMomentsRecylerView);
            textViewUsername = itemView.findViewById(R.id.textViewUsernameMomentsRecyclerView);
            textViewImageCount = itemView.findViewById(R.id.textViewMomentsCountRecyclerView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderUserItem(inflater.inflate(R.layout.layout_moments_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position)
    {
        final ChatPartner chatPartner = (ChatPartner) list.get(position);
        final ViewHolderUserItem viewHolderUserItem = (ViewHolderUserItem) holder;

        viewHolderUserItem.roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                momentsRecyclerViewAdapterClickListener.onItemClicked(chatPartner);
            }
        });

        viewHolderUserItem.textViewImageCount.setText("");
        viewHolderUserItem.textViewUsername.setText(chatPartner.getPartnerUsername());

        if(chatPartner.getLastConversationMessage() != null
                && (chatPartner.getLastConversationMessage().getType() == CMTypes.FPIC || chatPartner.getLastConversationMessage().getType() == CMTypes.FVID))
        {
            EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                    chatPartner.getLastConversationMessage().getIMAGE_ID(),
                    viewHolderUserItem.roundedImageView,
                    null,
                    new EsaphDimension(viewHolderUserItem.roundedImageView.getWidth(),
                            viewHolderUserItem.roundedImageView.getHeight()),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_rounded_corners));
        }
        else
        {
            Glide.with(context).load(EsaphGradientChooser.obtainGradient(position)).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        viewHolderUserItem.roundedImageView.setImageDrawable(resource);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void notifyOnDataChangeInViewpager()
    {

    }
}
