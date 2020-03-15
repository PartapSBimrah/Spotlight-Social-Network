/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentClickListener;

public class LifeCloudListShowAllAdapter extends EsaphMomentsRecylerView
{
    private static MomentsFragmentClickListener momentPostClickListener;
    private LayoutInflater inflater;
    private List<Object> listFooter;
    private List<Object> listDataDisplay;
    private List<Object> listDataOriginal;
    private Context context;


    public LifeCloudListShowAllAdapter(Context context,
                                       MomentsFragmentClickListener momentPostClickListener,
                                       WeakReference<View>[] views) {
        super(views);
        this.context = context;
        this.listFooter = new ArrayList<>();
        this.listDataDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        LifeCloudListShowAllAdapter.momentPostClickListener = momentPostClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        imageCount = 0;
        int size = listDataDisplay.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(listDataDisplay.get(counter) instanceof LifeCloudUpload)
            {
                imageCount++;
            }
        }
        super.notifyDataSetChangeBypass();
    }


    public void removeSinglePostByPID(String PID)
    {
        for(int counterIntern = 0; counterIntern < listDataDisplay.size(); counterIntern++)
        {
            Object object = listDataDisplay.get(counterIntern);
            if(object instanceof LifeCloudUpload)
            {
                if(((LifeCloudUpload)object).getCLOUD_PID().equals(PID))
                {
                    if(counterIntern - 1 > -1 && listDataDisplay.get(counterIntern-1) instanceof DatumList)
                    {
                        listDataDisplay.remove(counterIntern-1);
                        notifyItemRemoved(counterIntern-1);
                        listDataDisplay.remove(counterIntern-1);
                        notifyItemRemoved(counterIntern-1);
                    }
                    else
                    {
                        listDataDisplay.remove(counterIntern);
                        notifyItemRemoved(counterIntern);
                    }
                }
            }
        }
    }

    @Override
    public int[] getObjectCountsThreadSafe() {
        return new int[]{imageCount};
    }

    @Override
    public boolean isEmpty() {
        return imageCount <= 0;
    }

    @Override
    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(listDataDisplay.size()+listFooter.size()-1);
        }
    }

    @Override
    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(listDataDisplay.size()+listFooter.size()+1);
    }

    @Override
    public Filter getFilter()
    {
        //#Schrottelung
        return null;
    }

    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        this.listDataDisplay.addAll(data);
        this.listDataOriginal.addAll(data);
    }

    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
        notifyDataSetChangeBypass();
    }

    private int imageCount = 0;

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return listDataDisplay.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.listDataDisplay.get(pos);
    }

    @Override
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    private static class ViewHolderMemoryPost extends RecyclerView.ViewHolder
    {
        private ProgressBar progressBar;
        private RelativeLayout relativeLayout;
        private ImageView PostPicture;
        private ImageView imageViewVideoOrBild;

        private ViewHolderMemoryPost(View view)
        {
            super(view);
            this.PostPicture = (ImageView) view.findViewById(R.id.gridViewPrivateMomentMainBildMain);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
            this.imageViewVideoOrBild = (ImageView) view.findViewById(R.id.gridViewPrivateMomentVideoOrBildImageView);
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.display_image_raw_layout_ID);
        }
    }

    private static class ViewHolderDatum extends RecyclerView.ViewHolder
    {
        private TextView textViewDatum;

        private ViewHolderDatum(View view)
        {
            super(view);
            textViewDatum = (TextView) view.findViewById(R.id.textViewMemoryDatum);
        }
    }

    private static class ViewHolderFooter extends RecyclerView.ViewHolder
    {
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private ViewHolderFooter(View view)
        {
            super(view);
            avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.footerView);
        }
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType)
        {
            case 0:
                View viewItemConversation = inflater.inflate(R.layout.display_image_raw_layout, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewItemConversation);
                break;

            case 1:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;

            case 2:
                View viewFooter = inflater.inflate(R.layout.footer_layout_private, parent, false);
                viewHolder = new ViewHolderFooter(viewFooter);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Object object;
        if(position >= listDataDisplay.size())
        {
            object = null;
        }
        else
        {
            object = this.listDataDisplay.get(position);
        }

        switch (getItemViewTypePerformence(position, object))
        {
            case 0:
                final ViewHolderMemoryPost viewHolderMemoryPost = (ViewHolderMemoryPost) holder;
                final LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                viewHolderMemoryPost.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        momentPostClickListener.onItemClick(v, 0, viewHolderMemoryPost.getAdapterPosition());
                    }
                });

                if(lifeCloudUpload.getCLOUD_POST_TYPE() == (CMTypes.FVID))
                {
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderMemoryPost.imageViewVideoOrBild.setImageDrawable(resource);
                            }
                        }
                    });
                    viewHolderMemoryPost.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                    System.out.println("Video is visible");
                }
                else
                {
                    viewHolderMemoryPost.imageViewVideoOrBild.setVisibility(View.GONE);
                    System.out.println("Video is not visible");
                }

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        lifeCloudUpload.getCLOUD_PID(),
                        viewHolderMemoryPost.PostPicture,
                        viewHolderMemoryPost.progressBar,
                        new EsaphDimension(viewHolderMemoryPost.PostPicture.getWidth(),
                                viewHolderMemoryPost.PostPicture.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_rounded_corners));
                break;

            case 1:
                DatumList datumHolder = (DatumList) object;
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
                break;

            case 2:
                ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;
                viewHolderFooter.avLoadingIndicatorView.smoothToShow();
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 2;
        }
        else
        {
            Object objectType = listDataDisplay.get(position);
            if(objectType instanceof LifeCloudUpload)
            {
                return 0;
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }

        return -1;
    }

    private int getItemViewTypePerformence(int position, Object objectType)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 2;
        }
        else
        {
            if(objectType instanceof LifeCloudUpload)
            {
                return 0;
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }

        return -1;
    }

}
