package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.FriendStatusListener;
import esaph.spotlight.navigation.spotlight.Chats.DialogChooseOperationLongClickUser;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.RunnableAlgorythmLoadPrivatePicturesByAbsenderID;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.RunnableLoadMoreFromPrivateUserMomentPosts;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.SynchPrivateMomentsBetweenUsers;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.BigGalleryViewUniversal;
import esaph.spotlight.navigation.spotlight.Moments.MomentPostClickListener;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

import static esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.ShowUserMomentsPrivate.CurrentSortingAlgorythmPrivate.SHOW_ALL;

public class ShowUserMomentsPrivate extends EsaphGlobalCommunicationFragment implements ILoader, DataBaseLoadWaiter
{
    private ChatPartner chatPartner;
    private static final String extra_chatPartner = "esaph.mented.showusermomentsprivate.extra.chatPartner";

    private LinearLayout linearLayoutNoSearchResults;
    private ImageView imageViewBack;
    private TextView textViewMomentWith;
    private RecyclerView recylerViewMainList;
    private ArrayAdapterPrivateUserMomentsAll arrayAdapterPrivateUserMomentsAll;
    private ImageView imageViewProfilbildPartner;
    private TabLayout tabLayout;
    private EditText editTextSearching;
    private ImageView imageViewClearSearching;
    private CurrentSortingAlgorythmPrivate currentSortingAlgorythm = SHOW_ALL;
    private TextView textViewShowProfile;

    private static final int total_column_count = 3;

    public ShowUserMomentsPrivate()
    {
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageViewClearSearching = null;
        textViewShowProfile = null;
        linearLayoutNoSearchResults = null;
        editTextSearching = null;
        imageViewBack = null;
        imageViewProfilbildPartner = null;
        textViewMomentWith = null;
        recylerViewMainList = null;
        arrayAdapterPrivateUserMomentsAll = null;
        tabLayout = null;
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
                chatPartner.getUID_CHATPARTNER(),
                arrayAdapterPrivateUserMomentsAll,
                new SynchPrivateMomentsBetweenUsers.SynchListenerUsersPosts()
                {
                    @Override
                    public void onNewData()
                    {
                        loadMoreByCurrentSorting();
                    }

                    @Override
                    public void onFailedOrReachedEnd()
                    {
                    }
                }));
    }

    public static ShowUserMomentsPrivate getInstance(ChatPartner chatPartner)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShowUserMomentsPrivate.extra_chatPartner, chatPartner);
        ShowUserMomentsPrivate showUserMomentsPrivate = new ShowUserMomentsPrivate();
        showUserMomentsPrivate.setArguments(bundle);
        return showUserMomentsPrivate;
    }

    public enum CurrentSortingAlgorythmPrivate
    {
        SHOW_ALL, SHOW_ONLY_OWN, SHOW_ONLY_PARTNER
    }

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_show_user_moments_private, container, false);

        textViewShowProfile = (TextView) rootView.findViewById(R.id.textViewAddFriendAgain);
        linearLayoutNoSearchResults = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoSearchResults);
        imageViewProfilbildPartner = (ImageView) rootView.findViewById(R.id.imageViewProfilbildSmall);
        editTextSearching = (EditText) rootView.findViewById(R.id.editTextSearchPrivateUserMoments);
        imageViewClearSearching = (ImageView) rootView.findViewById(R.id.imageViewResetSearching);
        tabLayout = (TabLayout) rootView.findViewById(R.id.esaphTabViewChooseSortingAlgorythm);
        textViewMomentWith = (TextView) rootView.findViewById(R.id.textViewPrivateMomentFromWho);
        textViewMomentWith.setText(getResources().getString(R.string.txt_momente_with, chatPartner.getPartnerUsername()));
        imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewPrivateMomentsBack);
        recylerViewMainList = (RecyclerView) rootView.findViewById(R.id.gridViewShowUserMomentsPrivate);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(chatPartner.isHideChat())
        {
            textViewShowProfile.setVisibility(View.VISIBLE);
            textViewShowProfile.setClickable(true);
            textViewShowProfile.setFocusableInTouchMode(true);
            textViewShowProfile.setFocusable(true);
        }
        else
        {
            textViewShowProfile.setVisibility(View.GONE);
            textViewShowProfile.setClickable(false);
            textViewShowProfile.setFocusableInTouchMode(false);
            textViewShowProfile.setFocusable(false);
        }

        editTextSearching.addTextChangedListener(textWatcherSearching);

        textViewShowProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Activity activity = getActivity();
                if(activity != null)
                {
                    DialogChooseOperationLongClickUser dialogChooseOperationLongClickUser =
                            new DialogChooseOperationLongClickUser(activity,
                                    null,
                                    chatPartner,
                                    new FriendStatusListener()
                                    {
                                        @Override
                                        public void onStatusReceived(long CHAT_PARTNER_UID, short Status)
                                        {

                                        }

                                        @Override
                                        public void onStatusFailed(long CHAT_PARTNER_UID)
                                        {
                                            Context context = getContext();
                                            if(context != null)
                                            {
                                                AlertDialog.Builder alter = new AlertDialog.Builder(getContext());
                                                alter.setTitle(getContext().getResources().getString(R.string.txt_removeFriend_UserTitle));
                                                alter.setMessage(getContext().getResources().getString(R.string.txt_alertPinFailedToDownloadDetails));
                                                alter.show();
                                            }
                                        }
                                    });

                    dialogChooseOperationLongClickUser.show();
                }
            }
        });

        imageViewClearSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stopSearching();
            }
        });

        editTextSearching.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startSearching();
            }
        });

        recylerViewMainList.setNestedScrollingEnabled(true);

        imageViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent();
                }
            }
        });

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), ShowUserMomentsPrivate.total_column_count);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (arrayAdapterPrivateUserMomentsAll.getItemViewType(position))
                {
                    default:
                        return 3;

                    case 0:
                        return 1;

                    case 2:
                        return 1;
                }
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(
                getResources().getStringArray(R.array.arraySortingMechanismsPrivate)[0]));

        tabLayout.addTab(tabLayout.newTab().setText(
                getResources().getStringArray(R.array.arraySortingMechanismsPrivate)[1]));

        tabLayout.addTab(tabLayout.newTab().setText(
                chatPartner.getPartnerUsername().toUpperCase(Locale.getDefault())));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab != null)
                {
                    switch (tab.getPosition())
                    {
                        case 0:
                            applyNewSorting(CurrentSortingAlgorythmPrivate.SHOW_ALL);
                            break;

                        case 1:
                            applyNewSorting(CurrentSortingAlgorythmPrivate.SHOW_ONLY_OWN);
                            break;

                        case 2:
                            applyNewSorting(CurrentSortingAlgorythmPrivate.SHOW_ONLY_PARTNER);
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });

        if(checkIfGalleryNeedSynchronisation())
        {
            /*
            new AsyncSynchSavedPostingsOnlyFromPartner(getContext(), currentUserPartnerID,
                    new OnSynchSavedPostingsPartnerListener()
                    {
                        @Override
                        public void onSynchedSuccess()
                        {
                            Activity activity = getActivity();
                            if(activity != null)
                            {
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(momentPostClickListener != null && recylerViewMainList != null)
                                        {
                                            arrayAdapterPrivateUserMomentsAll = new ArrayAdapterPrivateUserMomentsAll(getActivity(),
                                                    momentPostClickListener,
                                                    esaphGlobalImageLoader,
                                                    new WeakReference[]{new WeakReference(textViewNoPosts),
                                                            new WeakReference(imageViewNoPosts)}
                                            );

                                            recylerViewMainList.setLayoutManager(gridLayoutManager);
                                            recylerViewMainList.setAdapter(arrayAdapterPrivateUserMomentsAll);
                                            applyNewSorting(currentSortingAlgorythm);
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onSynchFailed()
                        {
                            final FragmentActivity activity = getActivity();
                            if(activity != null)
                            {
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(activity != null)
                                        {
                                            activity.getSupportFragmentManager().beginTransaction().remove(ShowUserMomentsPrivate.this).commit();
                                        }
                                    }
                                });
                            }
                        }
                    }
            ).execute();*/

        }
        else
        {
            arrayAdapterPrivateUserMomentsAll = new ArrayAdapterPrivateUserMomentsAll(getContext(),
                    linearLayoutNoSearchResults,
                    this,
                    this.momentPostClickListener);

            recylerViewMainList.setLayoutManager(gridLayoutManager);
            recylerViewMainList.setAdapter(arrayAdapterPrivateUserMomentsAll);
            applyNewSorting(currentSortingAlgorythm);
        }

        EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(
                imageViewProfilbildPartner,
                null,
                chatPartner.getUID_CHATPARTNER(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null)
        {
            chatPartner = (ChatPartner) getArguments().getSerializable(ShowUserMomentsPrivate.extra_chatPartner);
        }
    }

    private void setScrollingListener(GridLayoutManager gridLayoutManager)
    {
        recylerViewMainList.clearOnScrollListeners();
        recylerViewMainList.addOnScrollListener(new GridViewEndlessScroll(gridLayoutManager)
        {
            @Override
            public void onScrolledVertical(int offset) {

            }

            @Override
            public void onLoadMore(int current_page)
            {
                loadMoreByCurrentSorting();
            }
        });
    }

    private boolean checkIfGalleryNeedSynchronisation()
    {
        SQLFriends sqlWatcher = new SQLFriends(getContext());
        boolean need = sqlWatcher.needFriendSynchSaved(chatPartner.getPartnerUsername());
        sqlWatcher.close();
        return need;
    }

    private BigGalleryViewUniversal savedMomentViewerPartner = null;

    private AtomicBoolean obLock = new AtomicBoolean(false);
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public void loadMoreAll()
    {
        if(!isAdded())
            return;

        if(!this.obLock.compareAndSet(false, true))
            return;

        int startFrom = arrayAdapterPrivateUserMomentsAll.getObjectCounts()[0];
        if(startFrom > 0)
        {
            Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                executorService.execute(new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                        this,
                        arrayAdapterPrivateUserMomentsAll,
                        this.chatPartner.getUID_CHATPARTNER(),
                        getLastMillisOfDay(conversationMessage.getMessageTime()), true, obLock));
            }
        }
        else
        {
            executorService.execute(new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                    this,
                    arrayAdapterPrivateUserMomentsAll,
                    chatPartner.getUID_CHATPARTNER(),
                    -1, true, obLock));
        }
    }

    private void loadMoreOnlyMy()
    {
        if(!isAdded())
            return;

        if(!this.obLock.compareAndSet(false, true))
            return;

        int startFrom = arrayAdapterPrivateUserMomentsAll.getObjectCounts()[0];
        if(startFrom > 0)
        {
            Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;

                executorService.execute(new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                        this,
                        arrayAdapterPrivateUserMomentsAll,
                        obLock,
                        SpotLightLoginSessionHandler.getLoggedUID(),
                        chatPartner.getUID_CHATPARTNER(),
                        conversationMessage.getMessageTime()));
            }
        }
        else
        {
            executorService.execute(new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                    this,
                    arrayAdapterPrivateUserMomentsAll,
                    obLock,
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    chatPartner.getUID_CHATPARTNER(),
                    -1));
        }
    }

    private void loadMoreOnlyPartner()
    {
        if(!isAdded())
            return;

        if(!this.obLock.compareAndSet(false, true))
            return;

        int startFrom = arrayAdapterPrivateUserMomentsAll.getObjectCounts()[0];
        if(startFrom > 0)
        {
            Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                executorService.execute(new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                        this,
                        arrayAdapterPrivateUserMomentsAll,
                        obLock,
                        chatPartner.getUID_CHATPARTNER(),
                        chatPartner.getUID_CHATPARTNER(),
                        getLastMillisOfDay(conversationMessage.getMessageTime())));
            }
        }
        else
        {
            executorService.execute(new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                    this,
                    arrayAdapterPrivateUserMomentsAll,
                    obLock,
                    chatPartner.getUID_CHATPARTNER(),
                    chatPartner.getUID_CHATPARTNER(),
                    -1));
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

    private final MomentPostClickListener momentPostClickListener = new MomentPostClickListener()
    {
        @Override
        public void onItemClickGridView(View view, int position)
        {
            savedMomentViewerPartner = BigGalleryViewUniversal.getInstance(ShowUserMomentsPrivate.this,
                    arrayAdapterPrivateUserMomentsAll,
                    (int) position);

            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutGalleryViewPrivateUser, savedMomentViewerPartner)
                        .addToBackStack(null)
                        .commit();
            }
        }
    };

    private void applyNewSorting(CurrentSortingAlgorythmPrivate currentSortingAlgorythm)
    {
        this.currentSortingAlgorythm = currentSortingAlgorythm;
        if(arrayAdapterPrivateUserMomentsAll != null)
        {
            arrayAdapterPrivateUserMomentsAll.clearAllWithNotify();
        }

        switch (currentSortingAlgorythm)
        {
            case SHOW_ALL:
                tabLayout.getTabAt(0).select();
                break;

            case SHOW_ONLY_OWN:
                tabLayout.getTabAt(1).select();
                break;

            case SHOW_ONLY_PARTNER:
                tabLayout.getTabAt(2).select();
                break;
        }
        setScrollingListener((GridLayoutManager) recylerViewMainList.getLayoutManager());
        loadMoreByCurrentSorting();
    }

    private synchronized void loadMoreByCurrentSorting()
    {
        if(isSearching()) return;

        switch (currentSortingAlgorythm)
        {
            case SHOW_ALL:
                ShowUserMomentsPrivate.this.loadMoreAll();
                break;

            case SHOW_ONLY_OWN:
                loadMoreOnlyMy();
                break;

            case SHOW_ONLY_PARTNER:
                loadMoreOnlyPartner();
                break;
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

        int startFrom = arrayAdapterPrivateUserMomentsAll.getObjectCounts()[0];
        switch (currentSortingAlgorythm)
        {
            case SHOW_ALL:
                if(startFrom > 0)
                {
                    Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
                    if(object instanceof ConversationMessage)
                    {
                        ConversationMessage conversationMessage = (ConversationMessage) object;

                        return new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                                this,
                                arrayAdapterPrivateUserMomentsAll,
                                chatPartner.getUID_CHATPARTNER(),
                                getLastMillisOfDay(conversationMessage.getMessageTime()),
                                true,
                                obLock);
                    }
                }
                else
                {
                    return new RunnableLoadMoreFromPrivateUserMomentPosts(getContext(),
                            this,
                            arrayAdapterPrivateUserMomentsAll,
                            chatPartner.getUID_CHATPARTNER(),
                            -1,
                            true,
                            obLock);
                }
                break;


            case SHOW_ONLY_OWN:
                if(startFrom > 0)
                {
                    Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
                    if(object instanceof ConversationMessage)
                    {
                        ConversationMessage conversationMessage = (ConversationMessage) object;

                        new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                                this,
                                arrayAdapterPrivateUserMomentsAll,
                                obLock,
                                SpotLightLoginSessionHandler.getLoggedUID(),
                                chatPartner.getUID_CHATPARTNER(),
                                conversationMessage.getMessageTime());
                    }
                }
                else
                {
                    new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                            this,
                            arrayAdapterPrivateUserMomentsAll,
                            obLock,
                            SpotLightLoginSessionHandler.getLoggedUID(),
                            chatPartner.getUID_CHATPARTNER(),
                            -1);
                }

            case SHOW_ONLY_PARTNER:
                if(startFrom > 0)
                {
                    Object object = arrayAdapterPrivateUserMomentsAll.getItem(arrayAdapterPrivateUserMomentsAll.getCount() - 1);
                    if(object instanceof ConversationMessage)
                    {
                        ConversationMessage conversationMessage = (ConversationMessage) object;

                        return new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                                this,
                                arrayAdapterPrivateUserMomentsAll,
                                obLock,
                                chatPartner.getUID_CHATPARTNER(),
                                chatPartner.getUID_CHATPARTNER(),
                                getLastMillisOfDay(conversationMessage.getMessageTime()));
                    }
                }
                else
                {
                    return new RunnableAlgorythmLoadPrivatePicturesByAbsenderID(getContext(),
                            this,
                            arrayAdapterPrivateUserMomentsAll,
                            obLock,
                            chatPartner.getUID_CHATPARTNER(),
                            chatPartner.getUID_CHATPARTNER(),
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
    public boolean onActivityDispatchedBackPressed()
    {
        if(savedMomentViewerPartner != null && savedMomentViewerPartner.isVisible())
        {
            if(!savedMomentViewerPartner.onActivityDispatchedBackPressed())
            {
                FragmentActivity activity = getActivity();
                if(activity != null)
                {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(savedMomentViewerPartner)
                            .commit();
                    setupSystemUI();
                    return true;
                }
            }

            return false;
        }
        else
        {
            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                        .remove(ShowUserMomentsPrivate.this).commit();

                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                return true;
            }
        }

        return false;
    }




    private TextWatcher textWatcherSearching = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            arrayAdapterPrivateUserMomentsAll.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void startSearching()
    {

    }


    private void stopSearching()
    {
        editTextSearching.removeTextChangedListener(textWatcherSearching);
        editTextSearching.setText("");
        arrayAdapterPrivateUserMomentsAll.getFilter().filter("");
    }

    public boolean isSearching()
    {
        return !editTextSearching.getText().toString().isEmpty();
    }
}