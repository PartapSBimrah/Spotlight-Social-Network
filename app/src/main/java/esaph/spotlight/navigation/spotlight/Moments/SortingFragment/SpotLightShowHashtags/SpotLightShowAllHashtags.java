package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowHashtags;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.MomentsRecylerViewAdapterHashtag;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.ViewHashtags.HashtagViewSingleAll;
import esaph.spotlight.navigation.spotlight.Moments.LoadingTasks.AsyncLoadMoreAllByHashtag;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.Synchronisation.SynchSpotLightDataSetComplete;

public class SpotLightShowAllHashtags extends EsaphGlobalCommunicationFragment implements DataBaseLoadWaiter
{
    private static final int totalCellCount = 2;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private RecyclerView recyclerViewMain;
    private TextView textViewNoDataDisplay;
    private ImageView imageViewNoDataDisplay;
    private MomentsRecylerViewAdapterHashtag momentsRecylerViewAdapterHashtag;

    public SpotLightShowAllHashtags()
    {
        // Required empty public constructor
    }

    public static SpotLightShowAllHashtags getInstance()
    {
        return new SpotLightShowAllHashtags();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recyclerViewMain = null;
        textViewNoDataDisplay = null;
        imageViewNoDataDisplay = null;
        momentsRecylerViewAdapterHashtag = null;
        esaphLockAbleViewPager = null;
    }

    private EsaphGlobalCommunicationFragment esaphBigViewFragment = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        momentsRecylerViewAdapterHashtag = new MomentsRecylerViewAdapterHashtag(getActivity(),
                new MomentsRecylerViewAdapterHashtag.ItemClickListener()
                {
                    @Override
                    public void onItemClick(int pos)
                    {
                        esaphBigViewFragment = HashtagViewSingleAll.getInstance((EsaphHashtag) momentsRecylerViewAdapterHashtag.getListDataDisplay().get(pos));

                        Activity activity = getActivity();
                        if(activity != null)
                        {
                            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_spotlight_show_hashtags, container, false);

        recyclerViewMain = (RecyclerView) rootView.findViewById(R.id.momentsMainRecylerView);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) getActivity().findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        textViewNoDataDisplay = (TextView) rootView.findViewById(R.id.textViewMomentsNoData);
        imageViewNoDataDisplay = (ImageView) rootView.findViewById(R.id.imageViewMomentsNoData);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SpotLightShowAllHashtags.totalCellCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (momentsRecylerViewAdapterHashtag.getItemViewType(position))
                {
                    default:
                        return 1;

                    case 1:
                        return SpotLightShowAllHashtags.totalCellCount;

                    case 2:
                        return SpotLightShowAllHashtags.totalCellCount;
                }
            }
        });

        recyclerViewMain.setLayoutManager(gridLayoutManager);

        recyclerViewMain.clearOnScrollListeners();
        recyclerViewMain.addOnScrollListener(new GridViewEndlessScroll((LinearLayoutManager) recyclerViewMain.getLayoutManager())
        {
            @Override
            public void onScrolledVertical(int offset) {

            }

            @Override
            public void onLoadMore(int current_page)
            {
                loadMoreByHashtag();
            }
        });

        recyclerViewMain.setAdapter(momentsRecylerViewAdapterHashtag);

        loadMoreByHashtag();
        return rootView;
    }

    private ExecutorService executorService;
    private AtomicBoolean obLock = new AtomicBoolean(false);

    private void loadMoreByHashtag()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;

        if(!searchingActivated)
        {
            executorService.execute(
            new AsyncLoadMoreAllByHashtag(getContext(),
                    this,
                    momentsRecylerViewAdapterHashtag,
                    obLock));
        }
    }

    private boolean searchingActivated = false;

    public void setSearchingActivated(boolean searchingActivated) {
        this.searchingActivated = searchingActivated;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(esaphBigViewFragment != null && esaphBigViewFragment.isVisible())
        {
            return esaphBigViewFragment.onActivityDispatchedBackPressed();
        }
        else
        {
            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                        .remove(esaphBigViewFragment);
                return true;
            }
        }

        return false;
    }



    @Override
    public void refreshData()
    {
        if(momentsRecylerViewAdapterHashtag != null)
        {
            momentsRecylerViewAdapterHashtag.clearAllWithoutNotify();
            loadMoreByHashtag();
        }
    }

    @Override
    public void onNoDataAvaiable()
    {
        if(isAdded())
        {
            if(executorService == null)
                executorService = Executors.newSingleThreadExecutor();

            executorService.submit(new SynchSpotLightDataSetComplete(getContext(), new SynchSpotLightDataSetComplete.SynchSpotLightListener()
            {
                @Override
                public void onNewData()
                {
                    loadMoreByHashtag();
                }

                @Override
                public void onNoDataAvaiable()
                {
                    //schluss, wird eh wieder aufgerufen, sobald die liste erneut ganz nach unten gescrollt wurde.
                }
            }, momentsRecylerViewAdapterHashtag));
        }
    }
}
