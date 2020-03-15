package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.TodayHorizontalMomentsAdapter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.HomeView;

public class HomeViewAdapter extends EsaphMomentsRecylerView
{
    private HomeView homeView;
    private TodayHorizontalMomentsAdapter todayHorizontalMomentsAdapter;
    private List<Object> listFooter;
    private List<Object> listDisplay;
    private LayoutInflater inflater;
    private Context context;
    private InterClickListener interClickListener;

    public HomeViewAdapter(Context context,
                           InterClickListener interClickListener,
                           TodayHorizontalMomentsAdapter.TodayVerticalMomentsAdapterOnClickListener todayVerticalMomentsAdapterOnClickListener,
                           HomeView homeView,
                           WeakReference<View>[] views) {
        super(views);
        this.homeView = homeView;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.interClickListener = interClickListener;
        this.listDisplay = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.todayHorizontalMomentsAdapter = new TodayHorizontalMomentsAdapter(context, todayVerticalMomentsAdapterOnClickListener, new WeakReference[]{});
        this.listDisplay.add(0, todayHorizontalMomentsAdapter);
        setupHorizontalData();
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        super.notifyDataSetChangeBypass();
    }

    @Override
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    public interface InterClickListener
    {
        void onClick(int pos);
    }

    public TodayHorizontalMomentsAdapter getTodayHorizontalMomentsAdapter()
    {
        return todayHorizontalMomentsAdapter;
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public List<Object> getListDataDisplay()
    {
        return listDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data) {
        listDisplay.addAll(data);
    }

    @Override
    public void clearAllWithNotify()
    {
    }

    public void clearWithOutNotify()
    {
        listDisplay.clear();
        listFooter.clear();
    }

    @Override
    public void removeSinglePostByPID(String PID)
    {
    }

    @Override
    public int[] getObjectCounts()
    {
        return new int[]{};
    }

    @Override
    public int[] getObjectCountsThreadSafe() {
        return new int[0];
    }

    @Override
    public boolean isEmpty() {
        return listDisplay.isEmpty();
    }

    @Override
    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(listDisplay.size()+listFooter.size()-1);
        }
    }

    @Override
    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(listDisplay.size()+listFooter.size()+1);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position >= listDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            Object object = this.listDisplay.get(position);
            if(object instanceof TodayHorizontalMomentsAdapter)
            {
                return 0;
            }
            else if(object instanceof EsaphHashtag)
            {
                return 1;
            }
            else if(object instanceof LifeCloudUpload)
            {
                return 2;
            }
        }

        return -1;
    }

    private static class ViewHolderVerticalRecylerView extends RecyclerView.ViewHolder
    {
        private RecyclerView recylerView;

        public ViewHolderVerticalRecylerView(View view)
        {
            super(view);
            this.recylerView = view.findViewById(R.id.recylerviewMain);
        }
    }

    private static class ViewHolderHashtag extends RecyclerView.ViewHolder
    {
        private TextView textViewTitleLeft;
        private TextView textViewShowAll;
        private ImageView imageView;
        private ProgressBar progressBar;
        private ImageView imageViewVideoOrBild;

        private TextView textViewHashtagName;
        private TextView textViewCount;

        public ViewHolderHashtag(View view)
        {
            super(view);
            this.imageViewVideoOrBild = (ImageView) view.findViewById(R.id.gridViewPrivateMomentVideoOrBildImageView);
            this.imageView = view.findViewById(R.id.imageViewLastPic);
            this.progressBar = view.findViewById(R.id.progressBarSmallViewLoading);
            this.textViewHashtagName = view.findViewById(R.id.textViewHashtagName);
            this.textViewCount = view.findViewById(R.id.textViewHashtagAnzahl);

            this.textViewTitleLeft = view.findViewById(R.id.textViewTitleLeft);
            this.textViewShowAll = view.findViewById(R.id.textViewShowAll);
        }
    }

    private static class ViewHolderLifeCloud extends RecyclerView.ViewHolder
    {
        private TextView textViewTitleLeft;
        private TextView textViewShowAll;

        private ProgressBar progressBar;
        private ImageView imageView;
        private ImageView imageViewVideoOrBild;

        private ViewHolderLifeCloud(View view)
        {
            super(view);
            this.textViewTitleLeft = view.findViewById(R.id.textViewTitleLeft);
            this.textViewShowAll = view.findViewById(R.id.textViewShowAll);
            this.imageView = view.findViewById(R.id.imageViewLastPic);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
            this.imageViewVideoOrBild = (ImageView) view.findViewById(R.id.gridViewPrivateMomentVideoOrBildImageView);
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewItemConversation = inflater.inflate(R.layout.layout_vertical_recylerview, parent, false);
                viewHolder = new ViewHolderVerticalRecylerView(viewItemConversation);
                break;

            case 1:
                View viewItemHashtag = inflater.inflate(R.layout.layout_home_view_main_hashtag, parent, false);
                viewHolder = new ViewHolderHashtag(viewItemHashtag);
                break;

            case 2:
                View viewItemNormalImage = inflater.inflate(R.layout.layout_home_view_main_lifecloud, parent, false);
                viewHolder = new ViewHolderLifeCloud(viewItemNormalImage);
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
        if(position >= listDisplay.size())
        {
            object = null;
        }
        else
        {
            object = this.listDisplay.get(position);
        }


        switch (getItemViewType(position))
        {
            case 0:
                final ViewHolderVerticalRecylerView viewHolderVerticalRecylerView = (ViewHolderVerticalRecylerView) holder;
                TodayHorizontalMomentsAdapter todayVerticalMomentsAdapter = (TodayHorizontalMomentsAdapter) listDisplay.get(position);
                viewHolderVerticalRecylerView.recylerView.setAdapter(todayVerticalMomentsAdapter);
                viewHolderVerticalRecylerView.recylerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                break;

            case 1:
                final ViewHolderHashtag viewHolderHashtag = (ViewHolderHashtag) holder;
                EsaphHashtag esaphHashtag = (EsaphHashtag) object;

                viewHolderHashtag.textViewShowAll.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(homeView != null)
                        {
                            homeView.getViewPagerMomentsFragment().setCurrentItem(3);
                        }
                    }
                });

                viewHolderHashtag.textViewTitleLeft.setText(context.getResources().getString(R.string.txt_hashtag));
                viewHolderHashtag.textViewCount.setText(""+esaphHashtag.getHashtagAnzahl());
                viewHolderHashtag.textViewHashtagName.setText(context.getResources().getString(R.string.txt_hashtTagChar, esaphHashtag.getHashtagName()));

                if(esaphHashtag.getLastConversationMessage() != null)
                {
                    if(esaphHashtag.getLastConversationMessage().getType() == (CMTypes.FVID))
                    {
                        Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderHashtag.imageViewVideoOrBild.setImageDrawable(resource);
                                }
                            }
                        });
                        viewHolderHashtag.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolderHashtag.imageViewVideoOrBild.setVisibility(View.GONE);
                    }

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            esaphHashtag.getLastConversationMessage().getIMAGE_ID(),
                            viewHolderHashtag.imageView,
                            viewHolderHashtag.progressBar,
                            new EsaphDimension(viewHolderHashtag.imageView.getWidth(),
                                    viewHolderHashtag.imageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_no_round));
                }
                break;

            case 2:
                final ViewHolderLifeCloud viewHolderLifeCloud = (ViewHolderLifeCloud) holder;
                LifeCloudUpload lifeCloudUploadNormal = (LifeCloudUpload) object;

                viewHolderLifeCloud.textViewShowAll.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(interClickListener != null)
                        {
                            interClickListener.onClick(0);
                        }
                    }
                });

                viewHolderLifeCloud.textViewTitleLeft.setText(context.getResources().getString(R.string.txt_lifecloud_your_postings));
                viewHolderLifeCloud.imageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        interClickListener.onClick(viewHolderLifeCloud.getAdapterPosition());
                    }
                });

                if(lifeCloudUploadNormal.getCLOUD_POST_TYPE() == (CMTypes.FVID))
                {
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderLifeCloud.imageViewVideoOrBild.setImageDrawable(resource);
                            }
                        }
                    });
                    viewHolderLifeCloud.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                }
                else
                {
                    viewHolderLifeCloud.imageViewVideoOrBild.setVisibility(View.GONE);
                }

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        lifeCloudUploadNormal.getCLOUD_PID(),
                        viewHolderLifeCloud.imageView,
                        viewHolderLifeCloud.progressBar,
                        new EsaphDimension(viewHolderLifeCloud.imageView.getWidth(),
                                viewHolderLifeCloud.imageView.getHeight()),
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
    public int getItemCount()
    {
        return listDisplay.size() + listFooter.size();
    }

    private void setupHorizontalData()
    {
        new LoadTodayHorizontalMomentsData().execute();
    }

    private class LoadTodayHorizontalMomentsData extends AsyncTask<Void, Void, List<Object>>
    {
        @Override
        protected List<Object> doInBackground(Void... params)
        {
            List<Object> returnMe = new ArrayList<>();
            SQLChats sqlChats = null;
            SQLLifeCloud sqlLifeCloud = null;
            try
            {
                sqlChats = new SQLChats(context);
                sqlLifeCloud = new SQLLifeCloud(context);
                LifeCloudUpload lifeCloudUpload = sqlLifeCloud.getLatestLifeCloudUploadFromToday();
                if(lifeCloudUpload != null)
                {
                    returnMe.add(0, lifeCloudUpload);
                }

               // returnMe.addAll(sqlChats.getTodayMyPosts());
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "LoadTodayHorizontalMomentsData doInBackground() failed: " + ec);
            }
            finally
            {
                if(sqlChats != null)
                {
                    sqlChats.close();
                }
            }

            return returnMe;
        }

        @Override
        protected void onPostExecute(List<Object> dataSet)
        {
            if(dataSet != null && !dataSet.isEmpty())
            {
                if(listDisplay != null && !listDisplay.isEmpty() && todayHorizontalMomentsAdapter != null)
                {
                    todayHorizontalMomentsAdapter.pushNewDataInAdapter(dataSet);
                    todayHorizontalMomentsAdapter.notifyDataSetChangeBypass();
                }
            }
        }

        @Override
        protected void onPreExecute()
        {
            //Loading...!
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }
}
