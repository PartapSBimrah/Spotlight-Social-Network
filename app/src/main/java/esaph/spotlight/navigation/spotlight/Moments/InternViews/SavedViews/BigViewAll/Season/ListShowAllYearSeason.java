/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.Season;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphLifeCloudBackUpView.EsaphLifeCloudBackUpView;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.BigGalleryViewUniversal;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowAll.SeasonRecylerViewAdapterAll;

public class ListShowAllYearSeason extends EsaphGlobalCommunicationFragment implements ILoader, DataBaseLoadWaiter
{
    private static final int totalCellCount = 4;
    private RecyclerView recyclerViewMain;
    private TextView textViewNoDataDisplay;
    private ImageView imageViewNoDataDisplay;
    private SeasonRecylerViewAdapterAll seasonRecylerViewAdapterAll;
    private EsaphLifeCloudBackUpView esaphImageFolderBackUpView;
    private EsaphDragableViewFragment frameLayoutAttachedTo;

    public ListShowAllYearSeason()
    {
        // Required empty public constructor
    }

    private static YearTime yearTime;
    public static ListShowAllYearSeason getInstance(YearTime yearTime)
    {
        ListShowAllYearSeason.yearTime = yearTime;
        return new ListShowAllYearSeason();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(executorService != null)
        {
            executorService.shutdownNow();
        }

        executorService = null;
        recyclerViewMain = null;
        seasonRecylerViewAdapterAll = null;
        esaphBigViewFragment = null;
        esaphImageFolderBackUpView = null;
        textViewNoDataDisplay = null;
        imageViewNoDataDisplay = null;
    }

    private EsaphGlobalCommunicationFragment esaphBigViewFragment = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        seasonRecylerViewAdapterAll = new SeasonRecylerViewAdapterAll(getActivity(),
                new SeasonRecylerViewAdapterAll.SeasonRecylerViewOnClickListener()
                {
                    @Override
                    public void onItemClickNormal(View view, long postTimeOfDay, int position)
                    {
                        esaphBigViewFragment = BigGalleryViewUniversal.getInstance(
                                ListShowAllYearSeason.this,
                                seasonRecylerViewAdapterAll,
                                position);

                        FragmentActivity activity = getActivity();
                        if(activity != null)
                        {
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                    .replace(R.id.esaphMainFrameLayout, esaphBigViewFragment)
                                    .commit();
                        }
                    }
                },
                new WeakReference[]{new WeakReference(textViewNoDataDisplay),
                        new WeakReference(imageViewNoDataDisplay)});
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_spotlight_year_season_list, container, false);
        esaphImageFolderBackUpView = (EsaphLifeCloudBackUpView) rootView.findViewById(R.id.lifeCloudBackUpView);
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
        esaphImageFolderBackUpView.showStatistics(getResources().getString(R.string.txt_back),
                yearTime.getYeartime(),
                yearTime.getBestPersons(),
                getResources().getString(R.string.txt_hintSearchForSeason),
                100,
                100,
                ContextCompat.getColor(getContext(), R.color.colorDarkerGrey));

        esaphImageFolderBackUpView.getImageViewBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent();
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), ListShowAllYearSeason.totalCellCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (seasonRecylerViewAdapterAll.getItemViewType(position))
                {
                    default:
                        return 1;

                    case 1:
                        return ListShowAllYearSeason.totalCellCount;
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
                loadMore();
            }
        });
        recyclerViewMain.setAdapter(seasonRecylerViewAdapterAll);

        loadMore();
    }

    private ExecutorService executorService = null;
    private AtomicBoolean obLock = new AtomicBoolean(false);
    private void loadMore()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();


        if(!this.obLock.compareAndSet(false, true))
            return;


        executorService.execute(new RunnableAlgorythmLoadMoreYeartimeSeason(getContext(),
                this,
                seasonRecylerViewAdapterAll,
                obLock,
                ListShowAllYearSeason.yearTime,
                -1));
    }

    private boolean searchingActivated = false;

    public void setSearchingActivated(boolean searchingActivated)
    {
        this.searchingActivated = searchingActivated;
    }

    private EsaphGlobalCommunicationFragment getCurrentFragmentUserSeeing() {
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
            if(activity != null && frameLayoutAttachedTo != null)
            {
                frameLayoutAttachedTo.killFragmentAnimated(activity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(ListShowAllYearSeason.this));
                return true;
            }
        }
        return false;
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


        return new RunnableAlgorythmLoadMoreYeartimeSeason(getContext(),
                this,
                seasonRecylerViewAdapterAll,
                obLock,
                ListShowAllYearSeason.yearTime,
                -1);
    }

    @Override
    public void refreshData()
    {
        super.refreshData();
        if(seasonRecylerViewAdapterAll != null)
        {
            seasonRecylerViewAdapterAll.clearAllWithOutNotify();
            loadMore();
        }
    }

    @Override
    public void onNoDataAvaiable() {
        if(isAdded())
        {

        }
    }
}
