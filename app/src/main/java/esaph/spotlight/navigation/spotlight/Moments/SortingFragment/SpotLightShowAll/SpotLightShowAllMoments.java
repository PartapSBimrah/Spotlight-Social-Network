/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowAll;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.MomentsRecylerViewAdapterAll;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.BigGalleryViewUniversal;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.Season.ListShowAllYearSeason;
import esaph.spotlight.navigation.spotlight.Moments.LoadingTasks.RunnableAlgorythmLoadAll;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.Synchronisation.SynchSpotLightDataSetComplete;

public class SpotLightShowAllMoments extends EsaphGlobalCommunicationFragment implements ILoader, DataBaseLoadWaiter
{
    private static final int totalCellCount = 3;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private RecyclerView recyclerViewMain;
    private TextView textViewTakePicText;
    private ImageView imageViewTakePicText;
    private TextView textViewNoDataDisplay;
    private ImageView imageViewNoDataDisplay;
    private MomentsRecylerViewAdapterAll momentsRecylerViewAdapterAll;

    public SpotLightShowAllMoments()
    {
        // Required empty public constructor
    }

    public static SpotLightShowAllMoments getInstance()
    {
        return new SpotLightShowAllMoments();
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
        textViewTakePicText = null;
        imageViewTakePicText = null;
        textViewNoDataDisplay = null;
        imageViewNoDataDisplay = null;
        esaphLockAbleViewPager = null;
        recyclerViewMain = null;
        momentsRecylerViewAdapterAll = null;
        esaphBigViewFragment = null;
    }

    private EsaphGlobalCommunicationFragment esaphBigViewFragment = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        momentsRecylerViewAdapterAll = new MomentsRecylerViewAdapterAll(getActivity(),
                new MomentsRecylerViewAdapterAll.MomentsRecylerViewAdapterAllClickListener()
                {
                    @Override
                    public void onItemClickNormal(View view, long postTimeOfDay, int position)
                    {
                        esaphBigViewFragment = BigGalleryViewUniversal.getInstance(
                                SpotLightShowAllMoments.this,
                                momentsRecylerViewAdapterAll,
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

                    @Override
                    public void onItemClickSeason(View view, long postTimeOfDay, int position)
                    {
                        esaphBigViewFragment = ListShowAllYearSeason.getInstance((YearTime) momentsRecylerViewAdapterAll.getItem(position));

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
                        new WeakReference(textViewTakePicText), new WeakReference(imageViewNoDataDisplay),
                new WeakReference(imageViewTakePicText)});
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_spotlight_show_all_moments, container, false);

        recyclerViewMain = (RecyclerView) rootView.findViewById(R.id.momentsMainRecylerView);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) getActivity().findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        textViewTakePicText = (TextView) rootView.findViewById(R.id.nagivationGroupTextViewNoChats);
        imageViewTakePicText = (ImageView) rootView.findViewById(R.id.imageViewCameraIconNoData);
        textViewNoDataDisplay = (TextView) rootView.findViewById(R.id.textViewMomentsNoData);
        imageViewNoDataDisplay = (ImageView) rootView.findViewById(R.id.imageViewMomentsNoData);

        textViewTakePicText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                esaphLockAbleViewPager.setCurrentItem(1);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SpotLightShowAllMoments.totalCellCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (momentsRecylerViewAdapterAll.getItemViewType(position))
                {
                    case 0:
                        return 1;

                    case 2:
                        return 1;

                    case 3:
                        return 1;

                    default:
                        return SpotLightShowAllMoments.totalCellCount;
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
                loadMoreByAll();
            }
        });

        recyclerViewMain.setAdapter(momentsRecylerViewAdapterAll);
        loadMoreByAll();
    }

    private ExecutorService executorService = null;
    private AtomicBoolean obLock = new AtomicBoolean(false);
    private void loadMoreByAll()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;

        if(!searchingActivated)
        {
            int startFrom = momentsRecylerViewAdapterAll.getObjectCounts()[0];

            if(startFrom > 0)
            {
                Object object = momentsRecylerViewAdapterAll.getListDataDisplay().get(momentsRecylerViewAdapterAll.getListDataDisplay().size() - 1); //Was hat sich der Julian hierbei gedacht, warum minus 1?
                if(object instanceof ConversationMessage)
                {
                    ConversationMessage conversationMessage = (ConversationMessage) object;

                    executorService.submit(new RunnableAlgorythmLoadAll(getContext(),
                            this,
                            momentsRecylerViewAdapterAll,
                            obLock,
                            getLastMillisOfDay(conversationMessage.getMessageTime())));
                }
            }
            else
            {
                executorService.submit(new RunnableAlgorythmLoadAll(getContext(),
                        this,
                        momentsRecylerViewAdapterAll,
                        obLock,
                        -1));
            }
        }
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

    public void setSearchingActivated(boolean searchingActivated) {
        this.searchingActivated = searchingActivated;
    }

    private EsaphGlobalCommunicationFragment getCurrentFragmentUserSeeing()
    {
        return (EsaphGlobalCommunicationFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frameLayoutBigView);
    }

    @Override
    public boolean onActivityDispatchedBackPressed() {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment != null && esaphGlobalCommunicationFragment.isVisible())
        {
            return esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed();
        }
        return false;
    }



    @Override
    public Runnable getLoadingTask()
    {
        if(!searchingActivated)
        {
            if(!this.obLock.compareAndSet(false, true))
                return new Runnable() {
                    @Override
                    public void run() {

                    }
                };


            int startFrom = momentsRecylerViewAdapterAll.getObjectCounts()[0];

            if(startFrom > 0)
            {
                Object object = momentsRecylerViewAdapterAll.getItem(momentsRecylerViewAdapterAll.getItemCount() - 1); //Was hat sich der Julian hierbei gedacht, warum minus 1?
                if(object instanceof ConversationMessage)
                {
                    ConversationMessage conversationMessage = (ConversationMessage) object;

                    return new RunnableAlgorythmLoadAll(getContext(),
                            this,
                            momentsRecylerViewAdapterAll,
                            obLock,
                            getLastMillisOfDay(conversationMessage.getMessageTime()));
                }
            }
            else
            {
                return new RunnableAlgorythmLoadAll(getContext(),
                        this,
                        momentsRecylerViewAdapterAll,
                        obLock,
                        -1);
            }
        }

        return new Runnable()
        {
            @Override
            public void run()
            {
            }
        };
    }

    @Override
    public void refreshData()
    {
        super.refreshData();
        if(momentsRecylerViewAdapterAll != null)
        {
            momentsRecylerViewAdapterAll.clearAllWithOutNotify();
            loadMoreByAll();
        }
    }

    @Override
    public void onNoDataAvaiable()
    {
        if(isAdded())
        {
            if(executorService == null)
                executorService = Executors.newSingleThreadExecutor();

            executorService.submit(new SynchSpotLightDataSetComplete(getContext(),
                    new SynchSpotLightDataSetComplete.SynchSpotLightListener()
                    {
                        @Override
                        public void onNewData()
                        {
                            loadMoreByAll();
                        }

                        @Override
                        public void onNoDataAvaiable()
                        {
                            //schluss, wird eh wieder aufgerufen, sobald die liste erneut ganz nach unten gescrollt wurde.
                        }
                    }, momentsRecylerViewAdapterAll));
        }
    }
}
