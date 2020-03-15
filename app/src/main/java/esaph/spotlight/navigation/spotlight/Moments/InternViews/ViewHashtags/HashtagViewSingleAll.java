package esaph.spotlight.navigation.spotlight.Moments.InternViews.ViewHashtags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphColorTransitionShader;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShadersColorArrays;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphHashtagInfoStateViewTop.EsaphHashtagStateInfoViewTop;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.BigGalleryViewUniversal;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.ViewHashtags.Adapters.HashtagAllRecylerViewAdapter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.ViewHashtags.Background.RunnableLoadMoreSavedConversationsByHashtag;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentClickListener;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.Synchronisation.SynchSpotLightDataSetComplete;

public class HashtagViewSingleAll extends EsaphGlobalCommunicationFragment implements ILoader, DataBaseLoadWaiter
{
    private static final String EXTRA_HASHTAG_PARCEL = "esaph.spotlight.hashtags.parcel.esaphhashtag";
    private EsaphHashtag esaphHashtag;
    private static final int totalCellCount = 3;
    private RecyclerView recyclerView;
    private HashtagAllRecylerViewAdapter hashtagAllRecylerViewAdapter;
    private EsaphHashtagStateInfoViewTop esaphHashtagStateInfoViewTop;
    private EsaphDragableViewFragment frameLayoutAttachedTo;

    public HashtagViewSingleAll()
    {
    }

    public static HashtagViewSingleAll getInstance(EsaphHashtag esaphHashtag)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HashtagViewSingleAll.EXTRA_HASHTAG_PARCEL, esaphHashtag);
        HashtagViewSingleAll hashtagViewAll = new HashtagViewSingleAll();
        hashtagViewAll.setArguments(bundle);
        return hashtagViewAll;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        frameLayoutAttachedTo = null;
        esaphHashtag = null;
        recyclerView = null;
        hashtagAllRecylerViewAdapter = null;
        esaphHashtagStateInfoViewTop = null;
        if(executorService != null)
        {
            executorService.shutdownNow();
        }
        executorService = null;
        bigGalleryViewUniversal = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            Object object = bundle.getSerializable(HashtagViewSingleAll.EXTRA_HASHTAG_PARCEL);
            if(object instanceof EsaphHashtag)
            {
                esaphHashtag = (EsaphHashtag) object;
            }
        }
    }

    private int viewHeight = 0;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        esaphHashtagStateInfoViewTop.getImageViewBack().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getChildFragmentManager().beginTransaction().remove(HashtagViewSingleAll.this).commit();
            }
        });

        esaphHashtagStateInfoViewTop.post(new Runnable()
        {
            @Override
            public void run()
            {
                viewHeight = esaphHashtagStateInfoViewTop.getHeight();
            }
        });

        esaphHashtagStateInfoViewTop.getEsaphCircleImageView()
                .setEsaphShaderBackground(new EsaphColorTransitionShader(EsaphShadersColorArrays.COLORS_PURPLE_YELLOW, 3, null));
        esaphHashtagStateInfoViewTop.getEsaphCircleImageView().setBorderWidth(DisplayUtils.dp2px(2));

        esaphHashtagStateInfoViewTop.getTextViewTitel().setText(getResources().getString(R.string.txt_hashtTagChar, esaphHashtag.getHashtagName()));
        esaphHashtagStateInfoViewTop.getTextViewDetails().setText(getResources().getString(R.string.txt_beitraegeAnzahl, "" + esaphHashtag.getHashtagAnzahl()));

        if(esaphHashtag.getLastConversationMessage() != null)
        {
            EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                    esaphHashtag.getLastConversationMessage().getIMAGE_ID(),
                    esaphHashtagStateInfoViewTop.getEsaphCircleImageView(),
                    null,
                    new EsaphDimension(esaphHashtagStateInfoViewTop.getEsaphCircleImageView().getWidth(),
                            esaphHashtagStateInfoViewTop.getEsaphCircleImageView().getHeight()),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_no_round));
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), HashtagViewSingleAll.totalCellCount);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new GridViewEndlessScroll(gridLayoutManager)
        {
            @Override
            public void onLoadMore(int current_page)
            {
                loadMore();
            }

            @Override
            public void onScrolledVertical(int offset)
            {
                if(viewHeight == 0)
                    return;

                float translateValue = ((float)offset / (float) viewHeight);
                float p = translateValue * viewHeight;

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) esaphHashtagStateInfoViewTop.getLayoutParams();
                layoutParams.height = (int) (viewHeight - p);
                esaphHashtagStateInfoViewTop.setLayoutParams(layoutParams);
            }
        });

        hashtagAllRecylerViewAdapter = new HashtagAllRecylerViewAdapter(getContext(),
                new MomentsFragmentClickListener()
        {
            @Override
            public void onItemClick(View view, long time, int position)
            {
                bigGalleryViewUniversal = BigGalleryViewUniversal.getInstance(HashtagViewSingleAll.this,
                        hashtagAllRecylerViewAdapter,
                        (int) position);

                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                        .replace(R.id.frameLayoutGalleryViewDate, bigGalleryViewUniversal)
                        .commit();
            }
        }, new WeakReference[]{});

        recyclerView.setAdapter(hashtagAllRecylerViewAdapter);
        loadMore();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_hashtag_view_all, container, false);
        frameLayoutAttachedTo = (EsaphDragableViewFragment) getActivity().findViewById(R.id.esaphMainFrameLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.galleryViewDateRecylerView);
        esaphHashtagStateInfoViewTop = (EsaphHashtagStateInfoViewTop) rootView.findViewById(R.id.topView);
        return rootView;
    }

    private BigGalleryViewUniversal bigGalleryViewUniversal = null;

    private AtomicBoolean obLock = new AtomicBoolean(false);
    private ExecutorService executorService = null;
    private void loadMore()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;

        executorService.execute(new RunnableLoadMoreSavedConversationsByHashtag(
                getContext(),
                this,
                hashtagAllRecylerViewAdapter,
                obLock,
                esaphHashtag.getHashtagName()));
    }

    private EsaphGlobalCommunicationFragment getCurrentFragmentUserSeeing()
    {
        return (EsaphGlobalCommunicationFragment) getChildFragmentManager().findFragmentById(R.id.frameLayoutGalleryViewDate);
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = getCurrentFragmentUserSeeing();
        if(esaphGlobalCommunicationFragment != null && esaphGlobalCommunicationFragment.isVisible())
        {
            EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(getActivity());
            return esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed();
        }
        else
        {
            FragmentActivity activity = getActivity();
            if(frameLayoutAttachedTo != null && activity != null)
            {
                frameLayoutAttachedTo.killFragmentAnimated(activity.getSupportFragmentManager().beginTransaction()
                        .remove(HashtagViewSingleAll.this));
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


        return new RunnableLoadMoreSavedConversationsByHashtag(
                getContext(),
                this,
                hashtagAllRecylerViewAdapter,
                obLock,
                esaphHashtag.getHashtagName());
    }

    @Override
    public void onNoDataAvaiable()
    {
        if(isAdded())
        {
            executorService.execute(new SynchSpotLightDataSetComplete(getContext(), new SynchSpotLightDataSetComplete.SynchSpotLightListener()
            {
                @Override
                public void onNewData()
                {
                    loadMore();
                }

                @Override
                public void onNoDataAvaiable()
                {
                    //schluss, wird eh wieder aufgerufen, sobald die liste erneut ganz nach unten gescrollt wurde.
                }
            }, hashtagAllRecylerViewAdapter));
        }
    }
}
