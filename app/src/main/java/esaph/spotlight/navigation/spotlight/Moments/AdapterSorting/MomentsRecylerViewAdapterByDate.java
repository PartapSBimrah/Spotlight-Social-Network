/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.AdapterSorting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.GalleryItem;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentClickListener;

public class MomentsRecylerViewAdapterByDate extends EsaphMomentsRecylerView implements Filterable
{
    private static MomentsFragmentClickListener momentPostClickListener;
    private LayoutInflater inflater;
    private List<Object> listFooter;
    private List<Object> listDataDisplay;
    private List<Object> listDataOriginal;
    private Context context;


    public MomentsRecylerViewAdapterByDate(Context context,
                                           MomentsFragmentClickListener momentPostClickListener,
                                           WeakReference<View>[] views)
    {
        super(views);
        this.context = context;
        this.listDataDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        MomentsRecylerViewAdapterByDate.momentPostClickListener = momentPostClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        super.notifyDataSetChangeBypass();
    }

    public void removeSinglePostByPID(String PID)
    {
        for(int counterIntern = 0; counterIntern < listDataDisplay.size(); counterIntern++)
        {
            Object object = listDataDisplay.get(counterIntern);
            if(object instanceof ConversationMessage)
            {
                if(((ConversationMessage)object).getIMAGE_ID().equals(PID))
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
        return new int[]{};
    }

    @Override
    public boolean isEmpty() {
        return false;
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


    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        if(getCount() == 0)
        {
            this.listDataDisplay.add(new TitleList(context.getResources().getString(R.string.txt_sorted_by_day)));
            this.listDataOriginal.add(new TitleList(context.getResources().getString(R.string.txt_sorted_by_day)));
        }

        this.listDataDisplay.addAll(data);
        this.listDataOriginal.addAll(data);
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    public int getCount()
    {
        return this.listDataDisplay.size();
    }

    @Override
    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
        notifyDataSetChangeBypass();
    }

    public void clearAllWithOutNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
    }

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


    public List<Long> getMillisChatsAndGroups()
    {
        long millisChats = -1;
        long millisGroups = -1;

        final ListIterator<Object> iterator = listDataDisplay.listIterator(listDataDisplay.size());

        while(iterator.hasPrevious())
        {
            Object objectMain = iterator.previous();
            if(objectMain instanceof GalleryItem)
            {
                GalleryItem galleryItem = (GalleryItem) objectMain;
                if(galleryItem.isObjectChats())
                {
                    if(millisChats <= -1)
                    {
                        millisChats = galleryItem.getDatumInMillis();
                        break;
                    }
                }
                else
                {
                    if(millisGroups <= -1)
                    {
                        millisGroups = galleryItem.getDatumInMillis();
                    }
                }

                if(millisChats > -1 && millisGroups > -1)
                {
                    break;
                }
            }
        }

        List<Long> longs = new ArrayList<>();
        longs.add(millisChats);
        longs.add(millisGroups);
        return longs;
    }

    @Override
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    private static class ViewHolderDividerTitle extends RecyclerView.ViewHolder
    {
        private TextView textViewDividerTitle;

        private ViewHolderDividerTitle(View view)
        {
            super(view);
            textViewDividerTitle = (TextView) view.findViewById(R.id.textViewMemoryItemTitlePlaceholder);
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

    private static class ViewHolderSavedPartnerView extends RecyclerView.ViewHolder
    {
        private ImageView Thumpnail;
        private ProgressBar progressBar;
        private TextView textViewTitleMemory;
        private TextView textViewPostCounter;

        private ViewHolderSavedPartnerView(View view)
        {
            super(view);
            this.Thumpnail = (ImageView) view.findViewById(R.id.imageViewMemoryItemCollectionImage);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
            this.textViewTitleMemory = (TextView) view.findViewById(R.id.textViewMemoryItemCollectionTitle);
            this.textViewPostCounter = (TextView) view.findViewById(R.id.textViewMemoryItemCollectionPostCount);
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
            case 0: //First item listDataDisplay
                View viewFirstItem = inflater.inflate(R.layout.layout_gallery_main_card, parent, false);
                viewHolder = new ViewHolderSavedPartnerView(viewFirstItem);
                break;

            case 1: //Memory title placeholder.
                View viewPlaceholder = inflater.inflate(R.layout.layout_memorys_item_memory_placeholder, parent, false);
                viewHolder = new ViewHolderDividerTitle(viewPlaceholder);
                break;

            case 2:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;

            case 3:
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
                final GalleryItem galleryItem = (GalleryItem) object;
                final ViewHolderSavedPartnerView viewHolderAktuelleMoments = (ViewHolderSavedPartnerView) holder;

                viewHolderAktuelleMoments.Thumpnail.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        momentPostClickListener.onItemClick(v, galleryItem.getDatumInMillis(), viewHolderAktuelleMoments.getAdapterPosition());
                    }
                });

                viewHolderAktuelleMoments.Thumpnail.setTag(galleryItem.getPID());
                viewHolderAktuelleMoments.textViewTitleMemory.setText(galleryItem.getDatumSpotLightStyle());
                viewHolderAktuelleMoments.textViewPostCounter.setVisibility(View.VISIBLE);

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        galleryItem.getPID(),
                        viewHolderAktuelleMoments.Thumpnail,
                        viewHolderAktuelleMoments.progressBar,
                        new EsaphDimension(viewHolderAktuelleMoments.Thumpnail.getWidth(),
                                viewHolderAktuelleMoments.Thumpnail.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_rounded_corners));
                break;

            case 1: //Memory placeholder.
                TitleList memoryDividerPlaceholder = (TitleList) object;
                ViewHolderDividerTitle viewHolderDividerTitle = (ViewHolderDividerTitle) holder;
                viewHolderDividerTitle.textViewDividerTitle.setText(memoryDividerPlaceholder.getTextTitle());
                break;

            case 2:
                DatumList datumHolder = (DatumList) object;
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
                break;

            case 3:
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
            return 3;
        }
        else
        {
            Object objectType = listDataDisplay.get(position);
            if(objectType instanceof GalleryItem) //Unten die memorys
            {
                return 0;
            }
            else if(objectType instanceof TitleList) //Placeholder
            {
                return 1;
            }
            else if(objectType instanceof DatumList)
            {
                return 2;
            }
        }

        return -1;
    }

    private int getItemViewTypePerformence(int position, Object objectType)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            if(objectType instanceof GalleryItem) //Unten die memorys
            {
                return 0;
            }
            else if(objectType instanceof TitleList) //Placeholder
            {
                return 1;
            }
            else if(objectType instanceof DatumList)
            {
                return 2;
            }
        }

        return -1;
    }
}
