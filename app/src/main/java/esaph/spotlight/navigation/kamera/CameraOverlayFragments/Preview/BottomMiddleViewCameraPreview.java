package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.ImageFetcher;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.adapters.MainImageAdapter;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.interfaces.OnSelectionListener;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.modals.Img;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.utility.CameraBottomSheetUtility;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.SpotLightFancyImageEditorActivity;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.EsaphBottomMiddleViewFragment;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters.EmptyFaceGraphic;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters.FilterSmileys;
import esaph.spotlight.navigation.kamera.NavigationCamera;

public class BottomMiddleViewCameraPreview extends EsaphBottomMiddleViewFragment
{
    private FrameLayout frameLayoutRootMiddlePreview;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private ImageView imageViewFlashOnOff;
    private CardView cardView;
    private ImageView imageViewChangeCamera;
    private TextView textViewAccessRightSite;
    private NavigationCamera navigationCamera;
    private TabLayout esaphTabLayoutEffects;
    private TabLayout tabLayoutTopMoreOptions;
    private RecyclerView recyclerViewUserGallery;
    private MainImageAdapter mainImageAdapter;
    private View bottomSheet;

    public BottomMiddleViewCameraPreview()
    {
        // Required empty public constructor
    }

    public static BottomMiddleViewCameraPreview getInstance()
    {
        return new BottomMiddleViewCameraPreview();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageViewChangeCamera = null;
        textViewAccessRightSite = null;
        imageViewFlashOnOff = null;
        esaphTabLayoutEffects = null;
        navigationCamera = null;
        tabLayoutTopMoreOptions = null;
        mainImageAdapter = null;
    }

    public void setFlashIcon(final ImageView imageView)
    {
        if(NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN == 0) //BACK
        {
            int idCurrentFlashOptionDrawAbleCode;
            if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON)) //FLASH
            {
                idCurrentFlashOptionDrawAbleCode = R.drawable.ic_flash_on;
            }
            else
            {
                idCurrentFlashOptionDrawAbleCode = R.drawable.ic_flash_off;
            }

            Activity activity = getActivity();
            if(activity != null)
            {
                Glide.with(activity).load(idCurrentFlashOptionDrawAbleCode).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageView.setImageDrawable(resource);
                        }
                    }
                });
            }
        }
        else
        {
            int idCurrentFlashOptionDrawAbleCode;
            idCurrentFlashOptionDrawAbleCode = R.drawable.ic_flash_off;

            Activity activity = getActivity();
            if(activity != null)
            {
                Glide.with(activity).load(idCurrentFlashOptionDrawAbleCode).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageView.setImageDrawable(resource);
                        }
                    }
                });
            }
        }


        mainImageAdapter = new MainImageAdapter(getContext());
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), MainImageAdapter.SPAN_COUNT);

        recyclerViewUserGallery.setLayoutManager(mLayoutManager);
        mainImageAdapter.addOnSelectionListener(new OnSelectionListener()
        {
            @Override
            public void onClick(Img Img, View view, int position)
            {
                Intent intent = new Intent(getContext(), SpotLightFancyImageEditorActivity.class);
                intent.putExtra(SpotLightFancyImageEditorActivity.SPOT_LIGHT_FANCY_IMAGE_EDITOR_EXTRA_URI, Img.getContentUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Img img, View view, int position)
            {
            }
        });
        recyclerViewUserGallery.setAdapter(mainImageAdapter);
        updateImages();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_bottom_middle_view_camera_preview, container, false);
        imageViewChangeCamera = (ImageView) rootView.findViewById(R.id.imageViewSwitchCamera);
        textViewAccessRightSite = (TextView) rootView.findViewById(R.id.imageViewStupipButtonForAccessingLeftSite);
        imageViewFlashOnOff = (ImageView) rootView.findViewById(R.id.imageViewFlash);
        frameLayoutRootMiddlePreview = (FrameLayout) rootView.findViewById(R.id.frameLayoutBottomCameraOptionsPreviewRootView);
        recyclerViewUserGallery = rootView.findViewById(R.id.recyclerView);
        bottomButtons = rootView.findViewById(R.id.relativLayoutBottomButtons);
        topbar = rootView.findViewById(R.id.topbar);
        topbarCollapsed = rootView.findViewById(R.id.topbarCollapsed);
        arrowDown = rootView.findViewById(R.id.selection_back);
        bottomSheet = rootView.findViewById(R.id.bottom_sheet);

        esaphTabLayoutEffects = (TabLayout) rootView.findViewById(R.id.tabLayoutChooseCameraEffect);
        tabLayoutTopMoreOptions = (TabLayout) rootView.findViewById(R.id.tabLayoutTopMoreOptions);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();
        if(activity != null && !activity.isFinishing())
        {
            navigationCamera = (NavigationCamera) activity.getSupportFragmentManager().findFragmentByTag("TagNavigationCamera");
        }

        setBottomSheetBehavior();

        setFlashIcon(imageViewFlashOnOff);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Context context = getContext();
                if(context != null)
                {
                    ListCameraEffects listCameraEffects = new ListCameraEffects(context);
                    listCameraEffects.initTabs(esaphTabLayoutEffects, listCameraEffects.getArrayCameraModes());
                }
            }
        }, 150);


        textViewAccessRightSite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                esaphLockAbleViewPager.setCurrentItem(1);
            }
        });

        esaphTabLayoutEffects.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                navigationCamera.setOverLayoutFilterEffect(new EmptyFaceGraphic());
                esaphLockAbleViewPager.setSwipeAllowed(true);
                if(tab != null && navigationCamera != null)
                {
                    clearTabLayoutMoreOptions();
                    if(tab.getPosition() == CameraEffectsEnum.NONE.ordinal())
                    {
                        enableBottomSheet();
                    }
                    else if(tab.getPosition() == CameraEffectsEnum.FACE_HUNT.ordinal())
                    {
                        disableBottomSheet();
                    }
                    else if(tab.getPosition() == CameraEffectsEnum.FILTER.ordinal())
                    {
                        disableBottomSheet();
                        setupTabLayoutMoreOptionsFilterEffectsComplete();
                    }
                    else if(tab.getPosition() == CameraEffectsEnum.OVERLAY_FILTER.ordinal())
                    {
                        disableBottomSheet();
                        setUpTabLayoutMoreOptionsFaceEffects();
                    }

                    navigationCamera.setCameraEffectMode(CameraEffectsEnum.values()[tab.getPosition()]);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imageViewChangeCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(navigationCamera != null)
                {
                    navigationCamera.switchCamera(imageViewChangeCamera, imageViewFlashOnOff);
                }
            }
        });

        imageViewFlashOnOff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                navigationCamera.switchFlash(imageViewFlashOnOff);
                v.setClickable(true);
            }
        });

        if(Camera.getNumberOfCameras() <= 1)
        {
            imageViewChangeCamera.setVisibility(View.INVISIBLE);
            imageViewChangeCamera.setClickable(false);
        }

        frameLayoutRootMiddlePreview.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(frameLayoutRootMiddlePreview != null)
                {
                    widthFromBottomOptions = frameLayoutRootMiddlePreview.getWidth();
                }
            }
        });

        topbarCollapsed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        arrowDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }


    @Override
    public void onAnimateScrolling(float alpha)
    {
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
        {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if(activity != null)
        {
            cardView = (CardView) activity.findViewById(R.id.cardViewNoPermissions);
            esaphLockAbleViewPager = (EsaphLockAbleViewPager) activity.findViewById(R.id.mainNavigationVerticalSwipeViewPager);
            esaphLockAbleViewPager.addOnPageChangeListener(this.onHorizontalSwipeListener);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        esaphLockAbleViewPager.removeOnPageChangeListener(this.onHorizontalSwipeListener);
    }

    private float widthFromBottomOptions = 0;

    public final ViewPager.OnPageChangeListener onHorizontalSwipeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            float negative = 1-positionOffset;

            /*
            if(position == 0)
            {
                if(positionOffset < 50.0f)
                {
                    if(cardView.getVisibility() == View.VISIBLE)
                    {
                        cardView.setAlpha(negative);
                        cardView.setTranslationX(
                                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (500.0f * (negative)), getResources().getDisplayMetrics()));
                    }

                    frameLayoutRootMiddlePreview.setAlpha(positionOffset);
                    frameLayoutRootMiddlePreview.setTranslationX((widthFromBottomOptions * (negative)));
                }
                else
                {
                    if(cardView.getVisibility() == View.VISIBLE)
                    {
                        cardView.setAlpha(positionOffset);
                        cardView.setTranslationX(
                                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (500.0f * (negative)), getResources().getDisplayMetrics()));
                    }

                    frameLayoutRootMiddlePreview.setAlpha(negative);
                    frameLayoutRootMiddlePreview.setTranslationX((widthFromBottomOptions * (negative)));
                }
            }*/

            if(position == 0)
            {
                if(positionOffset < 50.0f)
                {
                    if(cardView.getVisibility() == View.VISIBLE)
                    {
                        cardView.setAlpha(negative);
                        cardView.setTranslationX(
                                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-500.0f * (positionOffset)), getResources().getDisplayMetrics()));
                    }

                    frameLayoutRootMiddlePreview.setAlpha(negative);
                    frameLayoutRootMiddlePreview.setTranslationX((-widthFromBottomOptions * (positionOffset)));
                }
                else
                {
                    if(cardView.getVisibility() == View.VISIBLE)
                    {
                        cardView.setAlpha(positionOffset);
                        cardView.setTranslationX(
                                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-500.0f * (negative)), getResources().getDisplayMetrics()));
                    }

                    frameLayoutRootMiddlePreview.setAlpha(positionOffset);
                    frameLayoutRootMiddlePreview.setTranslationX((-widthFromBottomOptions * (negative)));
                }

            }
            else if(position == 1)
            {
                if(cardView.getVisibility() == View.VISIBLE)
                {
                    cardView.setAlpha(positionOffset);
                    cardView.setTranslationX(
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-500.0f * (negative)), getResources().getDisplayMetrics()));
                }

                frameLayoutRootMiddlePreview.setAlpha(positionOffset);
                frameLayoutRootMiddlePreview.setTranslationX((-widthFromBottomOptions * (negative)));
            }
        }

        @Override
        public void onPageSelected(int position)
        {
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };


    private void setupTabLayoutMoreOptionsFilterEffectsComplete()
    {
        Context context = getContext();
        if(context != null)
        {
            ListCameraEffects listCameraEffects = new ListCameraEffects(context);
            listCameraEffects.initTabs(tabLayoutTopMoreOptions, listCameraEffects.getArrayFilterEffects());

            tabLayoutTopMoreOptions.clearOnTabSelectedListeners();
            tabLayoutTopMoreOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    if(tab != null && navigationCamera != null)
                    {

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

            tabLayoutTopMoreOptions.setVisibility(View.VISIBLE);
        }
    }


    private void setUpTabLayoutMoreOptionsFaceEffects()
    {
        Context context = getContext();
        if(context != null)
        {
            ListCameraEffects listCameraEffects = new ListCameraEffects(context);
            listCameraEffects.initTabs(tabLayoutTopMoreOptions, listCameraEffects.getArrayEmojieOverLay());

            tabLayoutTopMoreOptions.clearOnTabSelectedListeners();
            tabLayoutTopMoreOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    if(tab != null && navigationCamera != null)
                    {
                        CameraSourceEsaph cameraSourceEsaph = navigationCamera.getmCameraSource();
                        if(cameraSourceEsaph != null)
                        {
                            CharSequence charSequence = tab.getText();
                            if(charSequence != null)
                            {
                                navigationCamera.setOverLayoutFilterEffect(new FilterSmileys(charSequence.toString()));
                            }
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

            tabLayoutTopMoreOptions.setVisibility(View.VISIBLE);
        }
    }


    private void clearTabLayoutMoreOptions()
    {
        if(tabLayoutTopMoreOptions.getTabCount() == 0)
            return;

        tabLayoutTopMoreOptions.clearOnTabSelectedListeners();
        tabLayoutTopMoreOptions.removeAllTabs();
        tabLayoutTopMoreOptions.setVisibility(View.GONE);
    }

    private BottomSheetBehavior mBottomSheetBehavior;
    private View mScrollbar, topbar, bottomButtons, topbarCollapsed, arrowDown;
    private void setBottomSheetBehavior()
    {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) (DisplayUtils.dp2px(194)));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                CameraBottomSheetUtility.manipulateVisibility(slideOffset,
                        recyclerViewUserGallery,
                        topbar,
                        topbarCollapsed,
                        textViewAccessRightSite,
                        bottomButtons);

                Activity activity = getActivity();
                if(activity == null) return;

                System.out.println("SLIDE OFFSET: " + slideOffset);

                if(slideOffset == 1.0f &&
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED))
                {
                    requestPerm();
                }
            }
        });
    }


    private void updateImages()
    {
        Activity activity = getActivity();
        if(activity == null) return;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            mainImageAdapter.clearList();
            Cursor cursor = CameraBottomSheetUtility.getCursor(activity);
            if (cursor == null)
            {
                return;
            }

            ImageFetcher imageFetcher = new ImageFetcher(new ImageFetcher.InternGalleryLoaderListener()
            {
                @Override
                public void onLoaded(ImageFetcher.ModelList imgs)
                {
                    if(isAdded() && imgs.getLIST() != null)
                    {
                        mainImageAdapter.addImageList(imgs.getLIST());
                    }
                }
            });

            imageFetcher.execute(CameraBottomSheetUtility.getCursor(activity));
            cursor.close();
        }
    }


    private void requestPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]
                    {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    }, BottomMiddleViewCameraPreview.REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE);
        }
    }


    private static final int REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE = 651;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean granted = false;

        if(requestCode == BottomMiddleViewCameraPreview.REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE)
        {
            if(grantResults.length > 0)
            {
                granted = true;
            }

            for(int count = 0; count < grantResults.length; count++)
            {
                if(grantResults[count] == PackageManager.PERMISSION_DENIED)
                {
                    granted = false;
                    break;
                }
            }
        }

        if(granted)
        {
            updateImages();
        }
    }


    private void enableBottomSheet()
    {
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet.setClickable(true);
        bottomSheet.setFocusable(true);
        bottomSheet.setFocusableInTouchMode(true);
        bottomSheet.setEnabled(true);
    }


    private void disableBottomSheet()
    {
        bottomSheet.setVisibility(View.GONE);
        bottomSheet.setClickable(false);
        bottomSheet.setFocusable(false);
        bottomSheet.setFocusableInTouchMode(false);
        bottomSheet.setEnabled(false);
    }


}
