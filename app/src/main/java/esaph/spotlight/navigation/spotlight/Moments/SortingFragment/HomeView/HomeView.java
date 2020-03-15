package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.R;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.TodayOurStory.TodayOurStory;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.HomeViewAdapter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.Model.TodayMomentsUser;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.TodayHorizontalMomentsAdapter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Background.AsyncAlgorythmLoadHomeView;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.LifeCloudListShowAll;
import esaph.spotlight.services.UploadService.UploadServiceCallbacksLifeCloud;

public class HomeView extends EsaphGlobalCommunicationFragment implements UploadServiceCallbacksLifeCloud
{
    private static final int totalCellCount = 1;
    private RecyclerView recyclerView;
    private HomeViewAdapter adapter;
    private EsaphActivity esaphActivity;
    private ViewPager viewPager;

    public HomeView()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recyclerView = null;
        esaphActivity = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapter = null;
    }

    public HomeViewAdapter getAdapter()
    {
        return adapter;
    }

    public static HomeView getInstance()
    {
        return new HomeView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof EsaphActivity)
        {
            esaphActivity = (EsaphActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new HomeViewAdapter(getActivity(),
                new HomeViewAdapter.InterClickListener()
        {
            @Override
            public void onClick(int pos)
            {
                Activity activity = getActivity();
                if(activity != null)
                {
                    ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                            .replace(R.id.esaphMainFrameLayout, LifeCloudListShowAll.getInstance())
                            .commit();
                }
            }
        },
                new TodayHorizontalMomentsAdapter.TodayVerticalMomentsAdapterOnClickListener()
                {
                    @Override
                    public void onClick(int pos)
                    {
                        Object object = adapter.getListDataDisplay().get(0);
                        if(object instanceof TodayHorizontalMomentsAdapter)
                        {
                            TodayHorizontalMomentsAdapter todayHorizontalMomentsAdapter = (TodayHorizontalMomentsAdapter) object;
                            Object objectHorizontal = todayHorizontalMomentsAdapter.getListDataDisplay().get(pos);

                            if(objectHorizontal instanceof TodayMomentsUser && esaphActivity != null)
                            {
                                TodayMomentsUser todayMomentsUser = (TodayMomentsUser) objectHorizontal;
                                esaphActivity.getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                        .replace(R.id.esaphMainFrameLayout, TodayOurStory.getInstance(todayMomentsUser.getUID()))
                                        .commit();
                            }
                        }
                    }
                }, HomeView.this,
                new WeakReference[]{});

        new AsyncAlgorythmLoadHomeView(getContext(),
                adapter).execute();
    }

    public ViewPager getViewPagerMomentsFragment()
    {
        return viewPager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home_view, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recylerViewMain);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), HomeView.totalCellCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


    @Override
    public void refreshData()
    {
        super.refreshData();
        if(adapter != null)
        {
            adapter.clearWithOutNotify();
            new AsyncAlgorythmLoadHomeView(getContext(),
                    adapter).execute();
        }
    }

    @Override
    public void onPostFailedUpload(LifeCloudUpload lifeCloudUpload)
    {
        EsaphGlobalCommunicationFragment currentFragmentUserSeeing = SwipeNavigation.FragmentsEventDispatcherContainerHolder.getCurrentFragmentVisibleToUser(getEsaphActivity());
        if(currentFragmentUserSeeing != null)
        {
            if(currentFragmentUserSeeing instanceof UploadServiceCallbacksLifeCloud)
            {
                UploadServiceCallbacksLifeCloud uploadServiceCallbacksLifeCloud = (UploadServiceCallbacksLifeCloud) currentFragmentUserSeeing;
                uploadServiceCallbacksLifeCloud.onPostFailedUpload(lifeCloudUpload);
            }
        }
    }

    @Override
    public void onPostUploading(LifeCloudUpload lifeCloudUpload)
    {
        EsaphGlobalCommunicationFragment currentFragmentUserSeeing = SwipeNavigation.FragmentsEventDispatcherContainerHolder.getCurrentFragmentVisibleToUser(getEsaphActivity());
        if(currentFragmentUserSeeing != null)
        {
            if(currentFragmentUserSeeing instanceof UploadServiceCallbacksLifeCloud)
            {
                UploadServiceCallbacksLifeCloud uploadServiceCallbacksLifeCloud = (UploadServiceCallbacksLifeCloud) currentFragmentUserSeeing;
                uploadServiceCallbacksLifeCloud.onPostUploading(lifeCloudUpload);
            }
        }
    }

    @Override
    public void onPostUploadSuccess(LifeCloudUpload lifeCloudUploadInternPid, LifeCloudUpload lifeCloudUploadPidServer)
    {
        EsaphGlobalCommunicationFragment currentFragmentUserSeeing = SwipeNavigation.FragmentsEventDispatcherContainerHolder.getCurrentFragmentVisibleToUser(getEsaphActivity());
        if(currentFragmentUserSeeing != null)
        {
            if(currentFragmentUserSeeing instanceof UploadServiceCallbacksLifeCloud)
            {
                UploadServiceCallbacksLifeCloud uploadServiceCallbacksLifeCloud = (UploadServiceCallbacksLifeCloud) currentFragmentUserSeeing;
                uploadServiceCallbacksLifeCloud.onPostUploadSuccess(lifeCloudUploadInternPid, lifeCloudUploadPidServer);
            }
        }
    }
}
