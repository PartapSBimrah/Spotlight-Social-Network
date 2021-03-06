/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.FragmentUserSite.Adapter;

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
import androidx.collection.SparseArrayCompat;
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
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MomentPostClickListener;

public class ArrayAdapterUserSite extends EsaphMomentsRecylerView
{
    private MomentPostClickListener momentPostClickListener;
    private LayoutInflater inflater;
    private List<Object> list;
    private List<Object> listFooter;
    private Context context;
    private SparseArrayCompat<String> sparseArrayCompatSelected = new SparseArrayCompat<String>();
    private int imageCount = 0;

    public ArrayAdapterUserSite(Context context,
                                MomentPostClickListener momentPostClickListener,
                                WeakReference<View>[] views) {
        super(views);
        this.context = context;
        this.list = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.momentPostClickListener = momentPostClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        imageCount = 0;
        int size = list.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(list.get(counter) instanceof ConversationMessage)
            {
                imageCount++;
            }
        }
        super.notifyDataSetChangeBypass();
    }


    public void clearAll()
    {
        this.list.clear();
        this.listFooter.clear();
        this.imageCount = 0;
        notifyDataSetChangeBypass();
    }

    public void removeSinglePostByPID(String PID)
    {
        for(int counterIntern = 0; counterIntern < list.size(); counterIntern++)
        {
            Object object = list.get(counterIntern);
            if(object instanceof ConversationMessage)
            {
                if(((ConversationMessage)object).getIMAGE_ID().equals(PID))
                {
                    if(counterIntern - 1 > -1 && list.get(counterIntern-1) instanceof DatumList)
                    {
                        list.remove(counterIntern-1);
                        notifyItemRemoved(counterIntern-1);
                        list.remove(counterIntern-1);
                        this.imageCount--;
                        notifyItemRemoved(counterIntern-1);
                    }
                    else
                    {
                        list.remove(counterIntern);
                        this.imageCount--;
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


    public void clearAllWithNotify()
    {
        this.list.clear();
        this.listFooter.clear();
        this.imageCount = 0;
        notifyDataSetChangeBypass();
    }

    @Override
    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(list.size()+listFooter.size()-1);
        }
    }

    @Override
    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(list.size()+listFooter.size()+1);
    }

    public List<Object> getListDataDisplay()
    {
        return list;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        list.addAll(data);
    }

    private int getPositionOfItem(String PID)
    {
        for(int counter = 0; counter < sparseArrayCompatSelected.size(); counter++)
        {
            int key = sparseArrayCompatSelected.keyAt(counter);
            if(PID.equals(sparseArrayCompatSelected.get(key)))
            {
                return key;
            }
        }
        return -1;
    }

    public int getCount()
    {
        return this.list.size();
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public void notifyOnDataChangeInViewpager()
    {
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

    private static class ViewHolderFooter extends RecyclerView.ViewHolder
    {
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private ViewHolderFooter(View view)
        {
            super(view);
            avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.footerView);
        }
    }

    private static class ViewHolderDatum extends RecyclerView.ViewHolder
    {
        private TextView textViewDatum;

        private ViewHolderDatum(View view)
        {
            super(view);
            this.textViewDatum = (TextView) view.findViewById(R.id.textViewMemoryDatum);
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return list.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.list.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewPost = inflater.inflate(R.layout.display_image_raw_layout, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewPost);
                break;

            case 1:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;

            case 2:
                View viewBigImage = inflater.inflate(R.layout.layout_user_memorys_first_pic_big, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewBigImage);
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
        if(position >= list.size())
        {
            object = null;
        }
        else
        {
            object = this.list.get(position);
        }

        switch (getItemViewTypePerformence(object, position))
        {
            case 0:
                final ViewHolderMemoryPost viewHolderMemoryPost = (ViewHolderMemoryPost) holder;
                final ConversationMessage conversationMessage = (ConversationMessage) object;
                viewHolderMemoryPost.PostPicture.setTag(conversationMessage.getIMAGE_ID());

                viewHolderMemoryPost.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                        public void onClick(final View v)
                    {
                        momentPostClickListener.onItemClickGridView(v, viewHolderMemoryPost.getLayoutPosition());
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
                        R.drawable.ic_no_image_no_round));
                break;


            case 1:
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                final DatumList datumHolder = (DatumList) object;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
                break;


            case 2:
                final ViewHolderMemoryPost viewHolderMemoryPostBig = (ViewHolderMemoryPost) holder;
                final ConversationMessage memoryPostBig = (ConversationMessage) object;
                viewHolderMemoryPostBig.PostPicture.setTag(memoryPostBig.getIMAGE_ID());

                viewHolderMemoryPostBig.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        
                        momentPostClickListener.onItemClickGridView(v, viewHolderMemoryPostBig.getLayoutPosition());
                    }
                });

                if(memoryPostBig.getType() == (CMTypes.FVID))
                {
                    viewHolderMemoryPostBig.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderMemoryPostBig.imageViewVideoOrBild.setImageDrawable(resource);
                            }
                        }
                    });
                    System.out.println("Video is visible");
                }
                else
                {
                    viewHolderMemoryPostBig.imageViewVideoOrBild.setVisibility(View.GONE);
                    System.out.println("Video is not visible");
                }

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        memoryPostBig.getIMAGE_ID(),
                        viewHolderMemoryPostBig.PostPicture,
                        viewHolderMemoryPostBig.progressBar,
                        new EsaphDimension(viewHolderMemoryPostBig.PostPicture.getWidth(),
                                viewHolderMemoryPostBig.PostPicture.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));
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
        if(position >= list.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            Object objectType = list.get(position);
            if(objectType instanceof ConversationMessage) //Unten die memorys
            {
                if(position == 0)
                {
                    return 2;
                }
                else
                {
                    return 0;
                }
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }
        return -1;
    }

    private int getItemViewTypePerformence(Object objectType, int position)
    {
        if(position >= list.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            if(objectType instanceof ConversationMessage) //Unten die memorys
            {
                if(position == 0)
                {
                    return 2;
                }
                else
                {
                    return 0;
                }
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }

        return -1;
    }

}
