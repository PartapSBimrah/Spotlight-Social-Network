/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.Esaph.ZoomOutPageTransformer;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncSetPostSeen;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogUnsaveOrSavePictureOrVideoFromPartner;
import esaph.spotlight.navigation.spotlight.DisplayingFragment.BackgroundLoading.GetUsersSeenOrSavedYourPostings;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.Aktuelle.ArrayAdapterUsersSeenAndSaved;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public abstract class SpotLightBigViewMasterClass extends EsaphGlobalCommunicationFragment implements ListDataChangedListener,
        GetUsersSeenOrSavedYourPostings.UserSeenOrSavedLoadingListener
{
    private long LOGGED_UID;
    private ExecutorService executorServiceLoadingReceiverList;
    private ViewPager viewPager;
    private SpotLightViewPagerAdapter spotLightViewPagerAdapter;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(executorServiceLoadingReceiverList != null)
        {
            executorServiceLoadingReceiverList.shutdown();
        }
        executorServiceLoadingReceiverList = null;

        if(executorServiceLoadingContent != null)
        {
            executorServiceLoadingContent.shutdown();
        }


        executorServiceLoadingContent = null;
        arrayAdapterUsersSeenAndSaved = null;
        viewPager = null;
        spotLightViewPagerAdapter = null;

        this.linearLayoutMiddleOptions = null;
        this.linearLayoutChangeAlphaOptions = null;
        this.listViewUsersSeenOrSaved = null;
        this.linearLayoutBottomSheet = null;
        this.textViewBeschreibungPanel = null;
        this.textViewUnderImage = null;
        this.textViewCountSavedSent = null;
        this.imageViewImage = null;
        this.progressBarLoadingOptionView = null;
        this.textViewHashtagsPanel = null;
    }

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    public SpotLightViewPagerAdapter getSpotLightViewPagerAdapter()
    {
        return spotLightViewPagerAdapter;
    }

    public ViewPager getViewPager()
    {
        return viewPager;
    }

    private ExecutorService executorServiceLoadingContent;
    private void loadMore()
    {
        if(executorServiceLoadingContent == null)
        {
            executorServiceLoadingContent = Executors.newSingleThreadExecutor();
        }

        executorServiceLoadingContent.submit(extendingFragmentStartLoadingMore(viewPager,
                spotLightViewPagerAdapter));
    }

    public ViewPager.OnPageChangeListener getViewPagerOnPageChangeListener()
    {
        return mainPagerListener;
    }

    private final ViewPager.OnPageChangeListener mainPagerListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if(spotLightViewPagerAdapter.getCount() == 0) return;

            Object object = spotLightViewPagerAdapter.getItemFromList(viewPager.getCurrentItem());
            if(object instanceof ChatImage || object instanceof ChatVideo)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if(conversationMessage.getABS_ID() == LOGGED_UID)
                {
                    linearLayoutBottomSheet.setVisibility(View.VISIBLE);
                    linearLayoutBottomSheet.setClickable(true);
                    linearLayoutBottomSheet.setFocusable(true);
                    linearLayoutBottomSheet.setFocusableInTouchMode(true);

                    executorServiceLoadingReceiverList.execute(new GetUsersSeenOrSavedYourPostings(getContext(),
                            (ConversationMessage) object, SpotLightBigViewMasterClass.this,
                            position));
                }
                else
                {
                    if(conversationMessage.getMessageStatus() == ConversationStatusHelper.STATUS_NEW_MESSAGE)
                    {
                        conversationMessage.setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);

                        SQLChats sqlChats = new SQLChats(getContext());
                        PostSeenUntransmitted postSeenUntransmitted = sqlChats.insertISeenNewPost(conversationMessage);
                        sqlChats.updateStatusByID(conversationMessage);
                        sqlChats.close();
                        new AsyncSetPostSeen(getContext(),
                                postSeenUntransmitted).execute();
                    }


                    linearLayoutBottomSheet.setVisibility(View.GONE);
                    linearLayoutBottomSheet.setClickable(false);
                    linearLayoutBottomSheet.setFocusable(false);
                    linearLayoutBottomSheet.setFocusableInTouchMode(false);
                }
            }
            else //Any other message, do not have a second receiver yet.
            {
                linearLayoutBottomSheet.setVisibility(View.GONE);
                linearLayoutBottomSheet.setClickable(false);
                linearLayoutBottomSheet.setFocusable(false);
                linearLayoutBottomSheet.setFocusableInTouchMode(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    public abstract Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, //This is the key for nice projects structure.
                                                               ViewPagerAdapterGetList viewPagerAdapterGetList);

    public abstract List<Object> extendedGetList();

    public abstract int extendedGetPositionClicked();

    public abstract ViewPagerDataSetChangedListener extendedGetListener();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                if(!isAdded())
                    return;

                int translateY = (int) (1-(TRANSLATION_Y_LAYOUT * slideOffset));

                relativeLayoutTopRootOptions.setTranslationY(translateY);
                relativeLayoutBottomOptions.setTranslationY(translateY);
                linearLayoutMiddleOptions.setTranslationY(translateY);
                dividerDragView.setTranslationY(translateY);

                linearLayoutChangeAlphaOptions.setAlpha(slideOffset);
            }
        });

        int translateY = (int) (1-(TRANSLATION_Y_LAYOUT * 0));

        relativeLayoutTopRootOptions.setTranslationY(translateY);
        relativeLayoutBottomOptions.setTranslationY(translateY);
        linearLayoutMiddleOptions.setTranslationY(translateY);
        dividerDragView.setTranslationY(translateY);

        linearLayoutChangeAlphaOptions.setAlpha(0);


        listViewUsersSeenOrSaved.setOnItemClickListener(this.itemClickListener);

        arrayAdapterUsersSeenAndSaved = new ArrayAdapterUsersSeenAndSaved(getContext());
        listViewUsersSeenOrSaved.setAdapter(arrayAdapterUsersSeenAndSaved);

        spotLightViewPagerAdapter = new SpotLightViewPagerAdapter(
                getChildFragmentManager(),
                getContext(),
                extendedGetListener(),
                extendedGetList(),
                mainPagerListener,
                viewPager);

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(this.mainPagerListener);
        viewPager.setAdapter(spotLightViewPagerAdapter);
        viewPager.setCurrentItem(extendedGetPositionClicked());

        mainPagerListener.onPageSelected(viewPager.getCurrentItem());
    }


    private BottomSheetBehavior sheetBehavior;
    private ArrayAdapterUsersSeenAndSaved arrayAdapterUsersSeenAndSaved;
    private LinearLayout linearLayoutBottomSheet;
    private ListView listViewUsersSeenOrSaved;

    private RelativeLayout relativeLayoutTopRootOptions;
    private RelativeLayout relativeLayoutBottomOptions;
    private LinearLayout linearLayoutMiddleOptions;
    private LinearLayout linearLayoutChangeAlphaOptions;
    private View dividerDragView;

    private ProgressBar progressBarLoadingOptionView;
    private TextView textViewHashtagsPanel;
    private TextView textViewBeschreibungPanel;
    private TextView textViewUnderImage;
    private TextView textViewCountSavedSent;
    private EsaphCircleImageView imageViewImage;


    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            UserSeenOrSavedMoment userSeenOrSavedMoment = arrayAdapterUsersSeenAndSaved.getItem(position);

            Activity activity = getActivity();
            if(activity != null)
            {
                Object object = spotLightViewPagerAdapter.getItemFromList(viewPager.getCurrentItem());

                if(object instanceof ConversationMessage)
                {
                    DialogUnsaveOrSavePictureOrVideoFromPartner dialogUnsaveOrSavePictureOrVideo = new DialogUnsaveOrSavePictureOrVideoFromPartner(getActivity(),
                            (ConversationMessage) object,
                            arrayAdapterUsersSeenAndSaved,
                            userSeenOrSavedMoment);

                    dialogUnsaveOrSavePictureOrVideo.show();
                }
            }
        }
    };

    public static final String TRANS_NAME_BIG_GALLERY_UNIVERSAL = "esaph.spotlight.transitionname.biggallery.name";
    private String TRANSISTION_NAME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            TRANSISTION_NAME = bundle.getString(SpotLightBigViewMasterClass.TRANS_NAME_BIG_GALLERY_UNIVERSAL);
        }

        LOGGED_UID = SpotLightLoginSessionHandler.getLoggedUID();
        executorServiceLoadingReceiverList = Executors.newFixedThreadPool(2);
    }

    private static float TRANSLATION_Y_LAYOUT = DisplayUtils.dp2px(20);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_spotlight_big_view_master_layout, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerSavedMainIntern);

        progressBarLoadingOptionView = (ProgressBar) rootView.findViewById(R.id.progressBarSmallViewLoading);
        textViewUnderImage = (TextView) rootView.findViewById(R.id.textViewBelowImage);
        textViewHashtagsPanel = (TextView) rootView.findViewById(R.id.textViewHashtags);
        textViewBeschreibungPanel = (TextView) rootView.findViewById(R.id.textViewDesciption);
        textViewCountSavedSent = (TextView) rootView.findViewById(R.id.textViewReceiverCount);
        imageViewImage = (EsaphCircleImageView) rootView.findViewById(R.id.imageViewTop);

        relativeLayoutTopRootOptions = (RelativeLayout) rootView.findViewById(R.id.topViewSpotLightOptions);
        linearLayoutMiddleOptions = (LinearLayout) rootView.findViewById(R.id.linearLayoutSpotLightOptionsMiddle);
        relativeLayoutBottomOptions = (RelativeLayout) rootView.findViewById(R.id.relativLayoutBottomSpotLightOptions);
        linearLayoutChangeAlphaOptions = (LinearLayout) rootView.findViewById(R.id.linearLayoutOptionsSpotLightChangeAlpha);
        dividerDragView = (View) rootView.findViewById(R.id.dividerViewShowCanDrag);
        listViewUsersSeenOrSaved = rootView.findViewById(R.id.listViewSavedFromUsers);
        linearLayoutBottomSheet = (LinearLayout) rootView.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(linearLayoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        initShowView(rootView);
        return rootView;
    }

    public abstract void initShowView(View view);

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        for(int i = 0; i < getChildFragmentManager().getBackStackEntryCount(); ++i)
        {
            getChildFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



    @Override
    public void onListDataChanged()
    {
        if(spotLightViewPagerAdapter != null && isAdded())
        {
            if(spotLightViewPagerAdapter.getCount() == 0) //If no items there, kill this shit.
            {
                FragmentActivity activity = getActivity();
                if(activity != null)
                {
                    activity.getSupportFragmentManager().beginTransaction().remove(SpotLightBigViewMasterClass.this).commit();
                }
                return;
            }
            spotLightViewPagerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaded(List<UserSeenOrSavedMoment> list, int callingPosition)
    {
        if((!isAdded() || viewPager == null) || callingPosition != viewPager.getCurrentItem())
            return;

        if(isAdded())
        {
            Object object = spotLightViewPagerAdapter.getItemFromList(viewPager.getCurrentItem());

            if(object instanceof ChatImage)
            {
                ChatImage chatImage = (ChatImage) object;

                textViewCountSavedSent.setText(getResources().getQuantityString(R.plurals.txtSentToPersons, list.size(), String.valueOf(list.size())));
                textViewBeschreibungPanel.setText(chatImage.getBeschreibung());
                textViewUnderImage.setText(chatImage.getAbsender());

                if(textViewHashtagsPanel.getText().toString().isEmpty())
                {
                    textViewHashtagsPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewHashtagsPanel.setVisibility(View.VISIBLE);
                }

                if(textViewBeschreibungPanel.getText().toString().isEmpty())
                {
                    textViewBeschreibungPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewBeschreibungPanel.setVisibility(View.VISIBLE);
                }

                EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                        chatImage.getIMAGE_ID(),
                        imageViewImage,
                        progressBarLoadingOptionView,
                        new EsaphDimension(imageViewImage.getWidth(),
                                imageViewImage.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));

                arrayAdapterUsersSeenAndSaved.setNewList(list);
            }
            else if(object instanceof ChatVideo)
            {
                ChatVideo chatVideo = (ChatVideo) object;


                textViewCountSavedSent.setText(getResources().getQuantityString(R.plurals.txtSentToPersons, list.size(), String.valueOf(list.size())));
                textViewBeschreibungPanel.setText(chatVideo.getBeschreibung());
                textViewUnderImage.setText(chatVideo.getAbsender());

                if(textViewHashtagsPanel.getText().toString().isEmpty())
                {
                    textViewHashtagsPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewHashtagsPanel.setVisibility(View.VISIBLE);
                }

                if(textViewBeschreibungPanel.getText().toString().isEmpty())
                {
                    textViewBeschreibungPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewBeschreibungPanel.setVisibility(View.VISIBLE);
                }

                EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                        chatVideo.getIMAGE_ID(),
                        imageViewImage,
                        progressBarLoadingOptionView,
                        new EsaphDimension(imageViewImage.getWidth(),
                                imageViewImage.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));

                arrayAdapterUsersSeenAndSaved.setNewList(list);
            }
        }
    }

}
