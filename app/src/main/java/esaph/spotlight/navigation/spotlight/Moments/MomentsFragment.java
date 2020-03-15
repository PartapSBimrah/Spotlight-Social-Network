/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.ShowUserMomentsPrivate;
import esaph.spotlight.navigation.spotlight.Moments.LoadingTasks.RunnableLoadMomentsUsers;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.MomentsSearchingFragment;
import esaph.spotlight.navigation.spotlight.Moments.MomentsRecylerViewAdapter.MomentsRecylerViewAdapter;

public class MomentsFragment extends EsaphGlobalCommunicationFragment implements DataBaseLoadWaiter
{
    private LinearLayout linearLayoutNoSearchResults;
    private MomentsRecylerViewAdapter momentsRecylerViewAdapter;
    private RecyclerView recyclerView;
    private ImageView imageViewSearch;

    public static MomentsFragment getInstance()
    {
        return new MomentsFragment();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        linearLayoutNoSearchResults = null;
        imageViewSearch = null;
        momentsRecylerViewAdapter = null;
        recyclerView = null;
    }

    public MomentsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentActivity activity = getActivity();
                if(activity != null)
                {
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayoutMainMoments, MomentsSearchingFragment.getInstance())
                            .commit();
                }
            }
        });

        momentsRecylerViewAdapter =
                new MomentsRecylerViewAdapter(getContext(), new MomentsRecylerViewAdapter.MomentsRecyclerViewAdapterClickListener()
                {
                    @Override
                    public void onItemClicked(ChatPartner chatPartner)
                    {
                        FragmentActivity activity = getActivity();
                        if(activity != null)
                        {
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                    .replace(R.id.esaphMainFrameLayout, ShowUserMomentsPrivate.getInstance(chatPartner))
                                    .commit();
                        }
                    }
                }, new WeakReference[]{new WeakReference(linearLayoutNoSearchResults)});
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), MomentsRecylerViewAdapter.total_column_count);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(momentsRecylerViewAdapter);
        // Iterate over all tabs and set the custom view
        loadMore();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moments, container, false);
        linearLayoutNoSearchResults = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoSearchResults);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recylerViewMoments);
        imageViewSearch = (ImageView) rootView.findViewById(R.id.imageViewMomentsFragmentStartSearching);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        EsaphActivity esaphActivity = getEsaphActivity();
        if(esaphActivity != null)
        {
            EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = SwipeNavigation.FragmentsEventDispatcherContainerHolder.getCurrentFragmentVisibleToUser(esaphActivity);
            if(esaphGlobalCommunicationFragment != null)
            {
                return esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed();
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY);
            Activity activity = getActivity();
            if(activity != null)
            {
                activity.registerReceiver(broadcastReceiver, intentFilter);
            }
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        try
        {
            Activity activity = getActivity();
            if(activity != null)
            {
                activity.unregisterReceiver(broadcastReceiver);
                broadcastReceiver = null;
            }
        }
        catch (Exception ec)
        {
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && intent.getAction() != null)
            {
                if(intent.getAction().equals(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY))
                {
                    if(isAdded() && momentsRecylerViewAdapter != null)
                    {
                        momentsRecylerViewAdapter.clearAllWithNotify();
                        loadMore();
                    }
                }
            }
        }
    };

    private ExecutorService executorService = null;
    private AtomicBoolean obLock = new AtomicBoolean(false);

    public void loadMore() //Searching in 2 kinds . Moments and groups!
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;

        executorService.execute(new RunnableLoadMomentsUsers(getContext(),
                this,
                momentsRecylerViewAdapter,
                obLock));
    }

    @Override
    public void onNoDataAvaiable() {

    }
}
