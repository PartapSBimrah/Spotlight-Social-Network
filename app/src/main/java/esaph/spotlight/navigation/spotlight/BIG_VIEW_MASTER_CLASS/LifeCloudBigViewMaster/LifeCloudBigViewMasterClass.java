/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.LifeCloudBigViewMaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.Esaph.ZoomOutPageTransformer;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.services.UploadService.UploadService;
import esaph.spotlight.services.UploadService.UploadServiceCallbacksLifeCloud;

public abstract class LifeCloudBigViewMasterClass extends EsaphGlobalCommunicationFragment implements UploadServiceCallbacksLifeCloud
{
    private BottomSheetBehavior sheetBehavior;

    private ViewPager viewPager;
    private LifeCloudBigViewAdapter lifeCloudBigViewAdapter;

    private TextView textViewLifeCloudTitle;
    private ImageView imageViewDisplayFailedStatus;
    private TextView textViewCurrentPostTimeAgo;
    private TextView textViewBeschreibung;
    private TextView textViewHashtags;
    private TextView textViewTryAgainUpload;

    private ProgressBar progressBarLoadingOptionView;
    private TextView textViewHashtagsPanel;
    private TextView textViewBeschreibungPanel;
    private TextView textViewUnderImage;
    private TextView textViewCountSavedSent;
    private EsaphCircleImageView imageViewImage;


    private EsaphActivity esaphActivityBackEndReference;


    public LifeCloudBigViewMasterClass()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        viewPager = null;
        lifeCloudBigViewAdapter = null;
        if(executorServiceLoadingContent != null)
        {
            executorServiceLoadingContent.shutdown();
        }
        linearLayoutMiddleOptions = null;
        linearLayoutBottomSheet = null;
        linearLayoutChangeAlphaOptions = null;
        executorServiceLoadingContent = null;
        textViewHashtags = null;
        textViewBeschreibung = null;
        textViewLifeCloudTitle = null;
        textViewCurrentPostTimeAgo = null;
        viewPager = null;
        esaphActivityBackEndReference = null;
        progressBarLoadingOptionView = null;
        textViewHashtagsPanel = null;
        textViewBeschreibungPanel = null;
        textViewUnderImage = null;
        textViewCountSavedSent = null;
        imageViewImage = null;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        esaphActivityBackEndReference = (EsaphActivity) context;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Activity activity = getActivity();
        if(activity != null)
        {
            EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(activity);
        }
    }

    private RelativeLayout relativeLayoutTopRootOptions;
    private RelativeLayout relativeLayoutBottomOptions;
    private LinearLayout linearLayoutMiddleOptions;
    private LinearLayout linearLayoutChangeAlphaOptions;
    private LinearLayout linearLayoutBottomSheet;
    private View dividerDragView;
    private float TRANSLATION_Y_LAYOUT = DisplayUtils.dp2px(20);

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        textViewTryAgainUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Object object = lifeCloudBigViewAdapter.getList().get(viewPager.getCurrentItem());
                Activity activity = getActivity();
                if(activity != null && object instanceof LifeCloudUpload)
                {
                    LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                    Intent intent = new Intent(activity, UploadService.class);
                    intent.setAction(UploadService.ACTION_TYPE_LIFECLOUD_UPLOAD);
                    intent.putExtra(UploadService.extraP_ID, lifeCloudUpload.getCLOUD_PID());
                    activity.startService(intent);
                }
            }
        });


        lifeCloudBigViewAdapter = new LifeCloudBigViewAdapter(getChildFragmentManager(), getContext(), extendedGetList(),
                mainPagerListener,
                viewPager);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                int translateY = (int) (1-(TRANSLATION_Y_LAYOUT * slideOffset));

                relativeLayoutTopRootOptions.setTranslationY(translateY);
                relativeLayoutBottomOptions.setTranslationY(translateY);
                linearLayoutMiddleOptions.setTranslationY(translateY);
                dividerDragView.setTranslationY(translateY);

                linearLayoutChangeAlphaOptions.setAlpha(slideOffset);
            }
        });

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setUpPagerListener();
        viewPager.setAdapter(lifeCloudBigViewAdapter);
        viewPager.setCurrentItem(extendedGetPositionClicked());
        mainPagerListener.onPageSelected(viewPager.getCurrentItem());
    }

    public abstract void initShowView(View rootView);

    public ViewPager getViewPager()
    {
        return viewPager;
    }

    public LifeCloudBigViewAdapter getLifeCloudBigViewAdapter()
    {
        return lifeCloudBigViewAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_big_view_lifecloud_masterlayout, container, false);

        textViewCurrentPostTimeAgo = (TextView) rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        textViewLifeCloudTitle = (TextView) rootView.findViewById(R.id.textViewLifeCloud);
        imageViewDisplayFailedStatus = (ImageView) rootView.findViewById(R.id.imageViewFailedUpload);
        textViewBeschreibung = (TextView) rootView.findViewById(R.id.textViewBeschreibung);
        textViewTryAgainUpload = (TextView) rootView.findViewById(R.id.textViewTryAgain);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerDayliPictures);
        textViewCurrentPostTimeAgo = (TextView) rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        textViewBeschreibung = (TextView) rootView.findViewById(R.id.textViewBeschreibung);
        textViewHashtags = (TextView) rootView.findViewById(R.id.textViewHashtagsOverImage);

        relativeLayoutTopRootOptions = (RelativeLayout) rootView.findViewById(R.id.topViewSpotLightOptions);
        linearLayoutMiddleOptions = (LinearLayout) rootView.findViewById(R.id.linearLayoutSpotLightOptionsMiddle);
        relativeLayoutBottomOptions = (RelativeLayout) rootView.findViewById(R.id.relativLayoutBottomSpotLightOptions);
        linearLayoutChangeAlphaOptions = (LinearLayout) rootView.findViewById(R.id.linearLayoutOptionsSpotLightChangeAlpha);
        dividerDragView = (View) rootView.findViewById(R.id.dividerViewShowCanDrag);

        initShowView(rootView);
        linearLayoutBottomSheet = (LinearLayout) rootView.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(linearLayoutBottomSheet);

        progressBarLoadingOptionView = (ProgressBar) rootView.findViewById(R.id.progressBarSmallViewLoading);
        textViewUnderImage = (TextView) rootView.findViewById(R.id.textViewBelowImage);
        textViewHashtagsPanel = (TextView) rootView.findViewById(R.id.textViewHashtags);
        textViewBeschreibungPanel = (TextView) rootView.findViewById(R.id.textViewDesciption);
        textViewCountSavedSent = (TextView) rootView.findViewById(R.id.textViewReceiverCount);
        imageViewImage = (EsaphCircleImageView) rootView.findViewById(R.id.imageViewTop);

        return rootView;
    }

    private void setupIfFailed(LifeCloudUpload lifeCloudUpload)
    {
        if(lifeCloudUpload.getCLOUD_MESSAGE_STATUS() == LifeCloudUpload.LifeCloudStatus.STATE_FAILED_NOT_UPLOADED)
        {
            textViewLifeCloudTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFail));
            textViewCurrentPostTimeAgo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFail));
            textViewTryAgainUpload.setVisibility(View.VISIBLE);
            imageViewDisplayFailedStatus.setVisibility(View.VISIBLE);
        }
        else if(lifeCloudUpload.getCLOUD_MESSAGE_STATUS() == LifeCloudUpload.LifeCloudStatus.STATE_UPLOADED)
        {
            textViewLifeCloudTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            textViewCurrentPostTimeAgo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

            imageViewDisplayFailedStatus.setVisibility(View.GONE);
            textViewTryAgainUpload.setVisibility(View.GONE);
        }
        else if(lifeCloudUpload.getCLOUD_MESSAGE_STATUS() == LifeCloudUpload.LifeCloudStatus.STATE_UPLOADING)
        {
            textViewLifeCloudTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            textViewCurrentPostTimeAgo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

            imageViewDisplayFailedStatus.setVisibility(View.GONE);
            textViewTryAgainUpload.setVisibility(View.GONE);
        }
    }

    public void setUpPagerListener()
    {
        viewPager.addOnPageChangeListener(this.mainPagerListener);
    }

    private ExecutorService executorServiceLoadingContent;
    public void loadMore()
    {
        if(executorServiceLoadingContent == null)
        {
            executorServiceLoadingContent = Executors.newSingleThreadExecutor();
        }

        executorServiceLoadingContent.submit(extendingFragmentStartLoadingMore(viewPager,
                lifeCloudBigViewAdapter,
                mainPagerListener));
    }

    public abstract Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, //This is the key for nice projects structure.
                                                               ViewPagerAdapterGetList viewPagerAdapterGetList,
                                                               ViewPager.OnPageChangeListener onPageChangeListener);

    public abstract List<Object> extendedGetList();

    public abstract int extendedGetPositionClicked();

    private final ViewPager.OnPageChangeListener mainPagerListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if(positionOffset < 0.50f)
            {
                Object objectPost = lifeCloudBigViewAdapter.getItemFromList(position);
                if(objectPost instanceof LifeCloudUpload)
                {
                    LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) objectPost;

                    if(lifeCloudUpload.getCLOUD_POST_DESCRIPTION() != null && !lifeCloudUpload.getCLOUD_POST_DESCRIPTION().isEmpty())
                    {
                        textViewBeschreibung.setVisibility(View.VISIBLE);
                        textViewBeschreibung.setText(lifeCloudUpload.getCLOUD_POST_DESCRIPTION());
                    }
                    else
                    {
                        textViewBeschreibung.setVisibility(View.GONE);
                    }

                    if(!lifeCloudUpload.getEsaphHashtag().isEmpty())
                    {
                        textViewHashtags.setText(lifeCloudUpload.getAllHashtagsTogether());
                    }
                    else
                    {
                        textViewHashtags.setText("");
                    }

                    textViewCurrentPostTimeAgo.setText(TimeDifferenceHelperClass.getDateDiff(getResources(),
                            lifeCloudUpload.getCLOUD_TIME_UPLOADED(),
                            System.currentTimeMillis()));
                }

                textViewHashtags.setAlpha(1-positionOffset);
                textViewBeschreibung.setAlpha(1-positionOffset);
                textViewCurrentPostTimeAgo.setAlpha(1-positionOffset);
            }
            else
            {
                Object objectPostPlusOne = lifeCloudBigViewAdapter.getItemFromList(position+1);
                if(objectPostPlusOne instanceof LifeCloudUpload)
                {
                    LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) objectPostPlusOne;

                    if(!lifeCloudUpload.getEsaphHashtag().isEmpty())
                    {
                        textViewHashtags.setText(lifeCloudUpload.getAllHashtagsTogether());
                    }
                    else
                    {
                        textViewHashtags.setText("");
                    }

                    textViewCurrentPostTimeAgo.setText(TimeDifferenceHelperClass.getDateDiff(getResources(),
                            lifeCloudUpload.getCLOUD_TIME_UPLOADED(),
                            System.currentTimeMillis()));
                }

                textViewBeschreibung.setAlpha(positionOffset);
                textViewHashtags.setAlpha(positionOffset);
                textViewCurrentPostTimeAgo.setAlpha(positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if(lifeCloudBigViewAdapter == null)
                return;

            if(position >= lifeCloudBigViewAdapter.getCount() - 1)
            {
                loadMore();
            }

            if(lifeCloudBigViewAdapter.getCount() == 0)
            {
                return;
            }


            int translateY = (int) (1-(TRANSLATION_Y_LAYOUT * 0));

            relativeLayoutTopRootOptions.setTranslationY(translateY);
            relativeLayoutBottomOptions.setTranslationY(translateY);
            linearLayoutMiddleOptions.setTranslationY(translateY);
            dividerDragView.setTranslationY(translateY);
            linearLayoutChangeAlphaOptions.setAlpha(0);


            Object object = lifeCloudBigViewAdapter.getItemFromList(position);
            if(object instanceof LifeCloudUpload)
            {
                textViewHashtags.setVisibility(View.VISIBLE);
                textViewCurrentPostTimeAgo.setVisibility(View.VISIBLE);
                textViewLifeCloudTitle.setVisibility(View.VISIBLE);
                linearLayoutBottomSheet.setVisibility(View.VISIBLE);
                LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;
                setupIfFailed(lifeCloudUpload);

                if(lifeCloudUpload.getCLOUD_POST_DESCRIPTION() != null && !lifeCloudUpload.getCLOUD_POST_DESCRIPTION().isEmpty())
                {
                    textViewBeschreibung.setVisibility(View.VISIBLE);
                    textViewBeschreibung.setText(lifeCloudUpload.getCLOUD_POST_DESCRIPTION());
                }
                else
                {
                    textViewBeschreibung.setVisibility(View.GONE);
                }

                if(!lifeCloudUpload.getEsaphHashtag().isEmpty())
                {
                    textViewHashtags.setText(lifeCloudUpload.getAllHashtagsTogether());
                }
                else
                {
                    textViewHashtags.setText("");
                }

                textViewCurrentPostTimeAgo.setText(TimeDifferenceHelperClass.getDateDiff(getResources(),
                        lifeCloudUpload.getCLOUD_TIME_UPLOADED(),
                        System.currentTimeMillis()));

                textViewHashtagsPanel.setText(lifeCloudUpload.getAllHashtagsTogether());
                if(textViewHashtagsPanel.getText().toString().isEmpty())
                {
                    textViewHashtagsPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewHashtagsPanel.setVisibility(View.VISIBLE);
                }
                textViewBeschreibungPanel.setText(lifeCloudUpload.getCLOUD_POST_DESCRIPTION());

                if(textViewBeschreibungPanel.getText().toString().isEmpty())
                {
                    textViewBeschreibungPanel.setVisibility(View.GONE);
                }
                else
                {
                    textViewBeschreibungPanel.setVisibility(View.VISIBLE);
                }

                textViewUnderImage.setText("");

                EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                        lifeCloudUpload.getCLOUD_PID(),
                        imageViewImage,
                        progressBarLoadingOptionView,
                        new EsaphDimension(imageViewImage.getWidth(),
                                imageViewImage.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle));
            }
            else
            {
                linearLayoutBottomSheet.setVisibility(View.GONE);
                textViewBeschreibung.setVisibility(View.GONE);
                textViewHashtags.setVisibility(View.GONE);
                textViewTryAgainUpload.setVisibility(View.GONE);
                textViewCurrentPostTimeAgo.setVisibility(View.GONE);
                textViewLifeCloudTitle.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                || sheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING)
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        else
        {
            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(LifeCloudBigViewMasterClass.this).commit();
                return true;
            }
        }

        return false;
    }



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
    public void onPostFailedUpload(LifeCloudUpload lifeCloudUpload)
    {
        if(lifeCloudBigViewAdapter != null)
        {
            lifeCloudBigViewAdapter.updatePostByPid(lifeCloudUpload);
        }
    }

    @Override
    public void onPostUploading(LifeCloudUpload lifeCloudUpload)
    {
        System.out.println("DEBUG LIFECLOUD: onPostUploading");
        if(lifeCloudBigViewAdapter != null)
        {
            lifeCloudBigViewAdapter.updatePostByPid(lifeCloudUpload);
        }
    }

    @Override
    public void onPostUploadSuccess(LifeCloudUpload lifeCloudUploadInternPid, LifeCloudUpload lifeCloudUploadPidServer)
    {
        if(lifeCloudBigViewAdapter != null)
        {
            lifeCloudBigViewAdapter.updatePostByPid(lifeCloudUploadInternPid, lifeCloudUploadPidServer);
        }
    }
}
