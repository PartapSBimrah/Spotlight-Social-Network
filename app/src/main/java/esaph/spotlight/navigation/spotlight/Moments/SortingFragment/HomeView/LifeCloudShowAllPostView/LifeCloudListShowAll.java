/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphLifeCloudBackUpView.EsaphLifeCloudBackUpView;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentClickListener;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.Background.RunnableLoadMoreLifeCloudIntern;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.Background.RunnableSynchroniseLifeCloudPosts;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.LifeCloudShowAllBigView.LifeCloudShowAllBigView;
import esaph.spotlight.services.UploadService.UploadServiceCallbacksLifeCloud;

public class LifeCloudListShowAll extends EsaphGlobalCommunicationFragment implements ILoader, UploadServiceCallbacksLifeCloud, DataBaseLoadWaiter
{
    private static final int totalCellCount = 4;
    private RecyclerView recyclerViewMain;
    private TextView textViewNoDataDisplay;
    private ImageView imageViewNoDataDisplay;
    private EsaphLifeCloudBackUpView esaphImageFolderBackUpView;
    private LifeCloudListShowAllAdapter lifeCloudListShowAllAdapter;
    private EsaphDragableViewFragment frameLayoutAttachedTo;

    public LifeCloudListShowAll()
    {
        // Required empty public constructor
    }

    public static LifeCloudListShowAll getInstance()
    {
        return new LifeCloudListShowAll();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(getActivity());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        esaphImageFolderBackUpView = null;
        recyclerViewMain = null;
        textViewNoDataDisplay = null;
        imageViewNoDataDisplay = null;
        lifeCloudListShowAllAdapter = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lifeCloudListShowAllAdapter = new LifeCloudListShowAllAdapter(getActivity(),
                new MomentsFragmentClickListener()
                {
                    @Override
                    public void onItemClick(View view, long postTimeOfDay, int position)
                    {
                        getChildFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                .replace(R.id.frameLayoutBigViewLifeCloud, LifeCloudShowAllBigView.getInstance(LifeCloudListShowAll.this,
                                        position,
                                        lifeCloudListShowAllAdapter.getListDataDisplay()))
                                .commit();
                    }
                },
                new WeakReference[]{new WeakReference(textViewNoDataDisplay),
                new WeakReference(imageViewNoDataDisplay)});
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_lifecloud_show_all, container, false);
        esaphImageFolderBackUpView = (EsaphLifeCloudBackUpView) rootView.findViewById(R.id.headerLifeCloudBackUp);
        recyclerViewMain = (RecyclerView) rootView.findViewById(R.id.momentsMainRecylerView);
        textViewNoDataDisplay = (TextView) rootView.findViewById(R.id.textViewMomentsNoData);
        imageViewNoDataDisplay = (ImageView) rootView.findViewById(R.id.imageViewMomentsNoData);
        frameLayoutAttachedTo = (EsaphDragableViewFragment) getActivity().findViewById(R.id.esaphMainFrameLayout);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        esaphImageFolderBackUpView.showStatistics(getResources().getString(R.string.txt_lifecloud_your_postings),
                getResources().getString(R.string.txt_lifecloud),
                getResources().getString(R.string.txt_lifecloud_hereFind),
                getResources().getString(R.string.txt_hintSearchForLifeCloud),
                100,
                100,
                ContextCompat.getColor(getContext(), R.color.colorDarkerGrey));

        esaphImageFolderBackUpView.getImageViewBack().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentActivity activity = getActivity();
                if(frameLayoutAttachedTo != null && activity != null)
                {
                    frameLayoutAttachedTo.killFragmentAnimated(activity.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(LifeCloudListShowAll.this));
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), LifeCloudListShowAll.totalCellCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (lifeCloudListShowAllAdapter.getItemViewType(position))
                {
                    default:
                        return 1;

                    case 1:
                        return LifeCloudListShowAll.totalCellCount;
                }
            }
        });

        recyclerViewMain.setLayoutManager(gridLayoutManager);
        recyclerViewMain.clearOnScrollListeners();
        recyclerViewMain.addOnScrollListener(new GridViewEndlessScroll((LinearLayoutManager) recyclerViewMain.getLayoutManager())
        {
            @Override
            public void onScrolledVertical(int offset)
            {
            }

            @Override
            public void onLoadMore(int current_page)
            {
                loadMoreLifeCloud();
            }
        });

        recyclerViewMain.setAdapter(lifeCloudListShowAllAdapter);
        loadMoreLifeCloud();
    }


    private void setupTopPic()
    {
        Context context = getContext();
        if(context != null)
        {
            Object object = null;
            List list = lifeCloudListShowAllAdapter.getListDataDisplay();
            for(int i = 0; i < list.size(); i++)
            {
                if(list.get(i) instanceof LifeCloudUpload)
                {
                    object = list.get(i);
                }
            }

            if(object instanceof LifeCloudUpload)
            {
                LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;
                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        lifeCloudUpload.getCLOUD_PID(),
                        esaphImageFolderBackUpView.getEsaphCircleImageView(),
                        null,
                        new EsaphDimension(esaphImageFolderBackUpView.getEsaphCircleImageView().getWidth(),
                                esaphImageFolderBackUpView.getEsaphCircleImageView().getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));
            }
        }
    }

    private ExecutorService executorService = null;
    private AtomicBoolean obLock = new AtomicBoolean(false);
    private void loadMoreLifeCloud()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;

        int startFrom = lifeCloudListShowAllAdapter.getObjectCounts()[0];

        if(startFrom > 0)
        {
            Object object = lifeCloudListShowAllAdapter.getListDataDisplay().get((lifeCloudListShowAllAdapter.getListDataDisplay().size() - 1)); //Was hat sich der Julian hierbei gedacht, warum minus 1?
            if(object instanceof LifeCloudUpload)
            {
                LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                executorService.execute(new RunnableLoadMoreLifeCloudIntern(getContext(),
                        this,
                        lifeCloudListShowAllAdapter,
                        obLock,
                        getLastMillisOfDay(lifeCloudUpload.getCLOUD_TIME_UPLOADED())));
            }
        }
        else
        {
            executorService.execute(new RunnableLoadMoreLifeCloudIntern(getContext(),
                    this,
                    lifeCloudListShowAllAdapter,
                    obLock,
                    -1));
        }
    }

    @Override
    public Runnable getLoadingTask()
    {
        if(!this.obLock.compareAndSet(false, true))
            return new Runnable() {
                @Override
                public void run() {

                }
            };


        if(!searchingActivated)
        {
            int startFrom = lifeCloudListShowAllAdapter.getObjectCounts()[0];
            if(startFrom > 0)
            {
                Object object = lifeCloudListShowAllAdapter.getItem(lifeCloudListShowAllAdapter.getItemCount() - 1); //Was hat sich der Julian hierbei gedacht, warum minus 1?
                if(object instanceof LifeCloudUpload)
                {
                    LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                    return new RunnableLoadMoreLifeCloudIntern(getContext(),
                            this,
                            lifeCloudListShowAllAdapter,
                            obLock,
                            getLastMillisOfDay(lifeCloudUpload.getCLOUD_TIME_UPLOADED()));
                }
            }
            else
            {
                return new RunnableLoadMoreLifeCloudIntern(getContext(),
                    this,
                    lifeCloudListShowAllAdapter,
                    obLock,
                    -1);
            }
        }

        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }

    private long getLastMillisOfDay(long millis)
    {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private boolean searchingActivated = false;

    public void setSearchingActivated(boolean searchingActivated)
    {
        this.searchingActivated = searchingActivated;
    }

    private EsaphGlobalCommunicationFragment getCurrentFragmentUserSeeing()
    {
        return (EsaphGlobalCommunicationFragment) getChildFragmentManager().findFragmentById(R.id.frameLayoutBigViewLifeCloud);
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment != null && esaphGlobalCommunicationFragment.isVisible())
        {
            return esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed();
        }
        else
        {
            FragmentActivity activity = getActivity();
            if(frameLayoutAttachedTo != null && activity != null)
            {
                frameLayoutAttachedTo.killFragmentAnimated(activity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(LifeCloudListShowAll.this));
                return true;
            }
        }

        return false;
    }



    @Override
    public void refreshData()
    {
        if(lifeCloudListShowAllAdapter != null)
        {
            lifeCloudListShowAllAdapter.clearAllWithNotify();
            loadMoreLifeCloud();
        }
    }

    @Override
    public void onPostFailedUpload(LifeCloudUpload lifeCloudUpload)
    {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment instanceof UploadServiceCallbacksLifeCloud)
        {
            ((UploadServiceCallbacksLifeCloud)esaphGlobalCommunicationFragment).onPostFailedUpload(lifeCloudUpload);
        }
    }

    @Override
    public void onPostUploading(LifeCloudUpload lifeCloudUpload) {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment instanceof UploadServiceCallbacksLifeCloud)
        {
            ((UploadServiceCallbacksLifeCloud)esaphGlobalCommunicationFragment).onPostUploading(lifeCloudUpload);
        }
    }

    @Override
    public void onPostUploadSuccess(LifeCloudUpload lifeCloudUploadInternPid, LifeCloudUpload lifeCloudUploadPidServer) {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment instanceof UploadServiceCallbacksLifeCloud)
        {
            ((UploadServiceCallbacksLifeCloud)esaphGlobalCommunicationFragment).onPostUploadSuccess(lifeCloudUploadInternPid, lifeCloudUploadPidServer);
        }
    }

    @Override
    public void onNoDataAvaiable()
    {
        if(isAdded())
        {
            if(executorService == null)
                executorService = Executors.newSingleThreadExecutor();

            executorService.execute(new RunnableSynchroniseLifeCloudPosts(getContext(), new RunnableSynchroniseLifeCloudPosts.LifeCloudSynchListener() {
                @Override
                public void onNewData() {
                    loadMoreLifeCloud();
                }

                @Override
                public void onFailed() {

                }
            }));
        }
    }
}
