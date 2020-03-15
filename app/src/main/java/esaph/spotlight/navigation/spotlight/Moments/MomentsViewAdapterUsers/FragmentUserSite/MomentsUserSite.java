package esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.FragmentUserSite;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.SynchPrivateMomentsBetweenUsers;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.RunnableLoadMoreFromPrivateUserMomentPosts;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.BigGalleryViewUniversal;
import esaph.spotlight.navigation.spotlight.Moments.MomentPostClickListener;
import esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.FragmentUserSite.Adapter.ArrayAdapterUserSite;
import esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.UserSiteItem;

public class MomentsUserSite extends EsaphGlobalCommunicationFragment implements DataBaseLoadWaiter
{
    private static final String EXTRA_USER_SITE_ITEM = "esaph.spotlight.extra.usersiteitem";
    private static final int total_column_count = 3;

    private ArrayAdapterUserSite arrayAdapterUserSite;
    private RecyclerView recylerViewMainList;
    private ImageView imageViewNoPosts;
    private TextView textViewNoPosts;

    private UserSiteItem userSiteItem;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        arrayAdapterUserSite = null;
        recylerViewMainList = null;
        imageViewNoPosts = null;
        textViewNoPosts = null;
    }

    public MomentsUserSite() {
        // Required empty public constructor
    }


    public static MomentsUserSite getInstance(UserSiteItem userSiteItem)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MomentsUserSite.EXTRA_USER_SITE_ITEM, userSiteItem);
        MomentsUserSite momentsUserSite = new MomentsUserSite();
        momentsUserSite.setArguments(bundle);
        return momentsUserSite;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            Object object = bundle.getSerializable(MomentsUserSite.EXTRA_USER_SITE_ITEM);
            if(object instanceof UserSiteItem)
            {
                userSiteItem = (UserSiteItem) object;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moments_user_site, container, false);
        recylerViewMainList = (RecyclerView) rootView.findViewById(R.id.recylerView);
        imageViewNoPosts = (ImageView) rootView.findViewById(R.id.nagivationGroupImageViewNoChats);
        textViewNoPosts = (TextView) rootView.findViewById(R.id.nagivationGroupTextViewNoChats);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        arrayAdapterUserSite = new ArrayAdapterUserSite(getContext(),
                new MomentPostClickListener() {
                    @Override
                    public void onItemClickGridView(View view, int position)
                    {
                        BigGalleryViewUniversal bigGalleryViewUniversal = BigGalleryViewUniversal.getInstance(MomentsUserSite.this,
                                arrayAdapterUserSite,
                                (int) position);

                        FragmentActivity activity = getActivity();
                        if(activity != null)
                        {
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                    .add(R.id.esaphMainFrameLayout, bigGalleryViewUniversal)
                                    .commit();
                        }
                    }
                },
                new WeakReference[]{new WeakReference(textViewNoPosts),
                        new WeakReference(imageViewNoPosts)});

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), MomentsUserSite.total_column_count);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (arrayAdapterUserSite.getItemViewType(position))
                {
                    case 3:
                        return MomentsUserSite.total_column_count;

                    default:
                        return 1;
                }
            }
        });
        recylerViewMainList.setLayoutManager(gridLayoutManager);
        recylerViewMainList.setAdapter(arrayAdapterUserSite);


        recylerViewMainList.addOnScrollListener(new GridViewEndlessScroll(gridLayoutManager)
        {
            @Override
            public void onScrolledVertical(int offset) {

            }

            @Override
            public void onLoadMore(int current_page)
            {
                loadMore();
            }
        });


        loadMore();
    }

    private final AtomicBoolean obLock = new AtomicBoolean(false);
    private ExecutorService executorService;
    private void loadMore()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();


        if(!this.obLock.compareAndSet(false, true))
            return;


        int startFrom = arrayAdapterUserSite.getObjectCounts()[0];
        if(startFrom > 0)
        {
            Object object = arrayAdapterUserSite.getItem(arrayAdapterUserSite.getCount() - 1);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                executorService.execute(new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                        this,
                        arrayAdapterUserSite,
                        userSiteItem.getUID(),
                        getLastMillisOfDay(conversationMessage.getMessageTime()), false,
                        this.obLock));
            }
        }
        else
        {
            executorService.execute(new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                    this,
                    arrayAdapterUserSite,
                    userSiteItem.getUID(),
                    -1, false,
                    this.obLock));
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

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



    @Override
    public void onNoDataAvaiable()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new SynchPrivateMomentsBetweenUsers(
                getContext(),
                userSiteItem.getUID(),
                arrayAdapterUserSite,
                new SynchPrivateMomentsBetweenUsers.SynchListenerUsersPosts()
                {
                    @Override
                    public void onNewData()
                    {
                        loadMore();
                    }

                    @Override
                    public void onFailedOrReachedEnd()
                    {
                    }
                }));

    }
}
