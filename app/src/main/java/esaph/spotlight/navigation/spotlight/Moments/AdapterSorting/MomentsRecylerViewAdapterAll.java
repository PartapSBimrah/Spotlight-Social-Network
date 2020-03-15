/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.AdapterSorting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;

public class MomentsRecylerViewAdapterAll extends EsaphMomentsRecylerView implements Filterable
{
    private static MomentsRecylerViewAdapterAllClickListener momentsRecylerViewAdapterAllClickListener;
    private LayoutInflater inflater;
    private List<Object> listFooter;
    private List<Object> listDataDisplay;
    private List<Object> listDataOriginal;
    private Context context;

    public MomentsRecylerViewAdapterAll(Context context,
                                        MomentsRecylerViewAdapterAllClickListener momentsRecylerViewAdapterAllClickListener,
                                        WeakReference<View>[] views) {
        super(views);
        this.context = context;
        this.listDataDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        MomentsRecylerViewAdapterAll.momentsRecylerViewAdapterAllClickListener = momentsRecylerViewAdapterAllClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        imageCount = 0;
        int size = listDataDisplay.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(listDataDisplay.get(counter) instanceof ConversationMessage)
            {
                imageCount++;
            }
        }
        super.notifyDataSetChangeBypass();
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
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    public interface MomentsRecylerViewAdapterAllClickListener
    {
        void onItemClickNormal(View view, long postTimeOfDay, int position);
        void onItemClickSeason(View view, long postTimeOfDay, int position);
    }

    @Override
    public int[] getObjectCountsThreadSafe()
    {
        return new int[]{imageCount};
    }

    @Override
    public boolean isEmpty()
    {
        return imageCount <= 0;
    }

    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        if(getItemCount() == 0)
        {
            this.listDataDisplay.add(new TitleList(context.getResources().getString(R.string.txt_allMoments)));
            this.listDataOriginal.add(new TitleList(context.getResources().getString(R.string.txt_allMoments)));
        }

        this.listDataDisplay.addAll(data);
        this.listDataOriginal.addAll(data);
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
        imageCount = 0;
        notifyDataSetChangeBypass();
    }

    @Override
    public void removeSinglePostByPID(String PID) {

    }

    public void clearAllWithOutNotify()
    {
        this.listFooter.clear();
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        imageCount = 0;
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
        return this.listDataDisplay.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.listDataDisplay.get(pos);
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

    private static class ViewHolderJahreszeit extends RecyclerView.ViewHolder
    {
        private EsaphCircleImageView imageView;
        private ProgressBar progressBar;
        private TextView textViewTopJahresZeit;
        private TextView textViewBestePersonen;
        private TextView textViewShowMomentsJahresZeit;

        private ViewHolderJahreszeit(View view)
        {
            super(view);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
            this.imageView = (EsaphCircleImageView) view.findViewById(R.id.imageViewTop);
            this.textViewTopJahresZeit = (TextView) view.findViewById(R.id.textViewBelowImage);
            this.textViewBestePersonen = (TextView) view.findViewById(R.id.textViewMostCommonFriends);
            this.textViewShowMomentsJahresZeit = (TextView) view.findViewById(R.id.textViewShowMomentsJahrezeit);
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

            case 1: //Memory title placeholder.
                View viewPlaceholder = inflater.inflate(R.layout.layout_memorys_item_memory_placeholder, parent, false);
                viewHolder = new ViewHolderDividerTitle(viewPlaceholder);
                break;

            case 2:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;

            case 3:
                View viewJahreszeit = inflater.inflate(R.layout.layout_moments_jahreszeit, parent, false);
                viewHolder = new ViewHolderJahreszeit(viewJahreszeit);
                break;

            case 4:
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
        if(position >= this.listDataDisplay.size())
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
                final ConversationMessage conversationMessage = (ConversationMessage) object;

                viewHolderMemoryPost.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        momentsRecylerViewAdapterAllClickListener.onItemClickNormal(v,
                                0,
                                viewHolderMemoryPost.getAdapterPosition());
                    }
                });

                if(conversationMessage.getType() == (CMTypes.FVID))
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
                        conversationMessage.getIMAGE_ID(),
                        viewHolderMemoryPost.PostPicture,
                        viewHolderMemoryPost.progressBar,
                        new EsaphDimension(viewHolderMemoryPost.PostPicture.getWidth(),
                                viewHolderMemoryPost.PostPicture.getHeight()),
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
                YearTime yearTime = (YearTime) object;
                final ViewHolderJahreszeit viewHolderJahreszeit = (ViewHolderJahreszeit) holder;
                viewHolderJahreszeit.textViewBestePersonen.setText(yearTime.getBestPersons());
                viewHolderJahreszeit.textViewTopJahresZeit.setText(yearTime.getYeartime());

                viewHolderJahreszeit.imageView.getRootView().setVisibility(View.VISIBLE);
                if(yearTime.getConversationMessageLast() != null)
                {
                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            yearTime.getConversationMessageLast().getIMAGE_ID(),
                            viewHolderJahreszeit.imageView,
                            viewHolderJahreszeit.progressBar,
                            new EsaphDimension(viewHolderJahreszeit.imageView.getWidth(),
                                    viewHolderJahreszeit.imageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_rounded_corners));
                }
                break;

            case 4:
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
            return 4;
        }
        else
        {
            Object objectType = listDataDisplay.get(position);
            if(objectType instanceof ConversationMessage)
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
            else if(objectType instanceof YearTime)
            {
                return 3;
            }
        }
        return -1;
    }

    private int getItemViewTypePerformence(int position, Object objectType)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 4;
        }
        else
        {
            if(objectType instanceof ConversationMessage)
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
            else if(objectType instanceof YearTime)
            {
                return 3;
            }
        }
        return -1;
    }

}
