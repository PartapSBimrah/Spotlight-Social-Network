package esaph.spotlight.navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.PreLogin.Dialogs.DialogShareApp;
import esaph.spotlight.PreLogin.LoginActivity;
import esaph.spotlight.R;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.AbstractZoomHandler;
import esaph.spotlight.navigation.kamera.ImageTakenListener;
import esaph.spotlight.navigation.kamera.NavigationCamera;
import esaph.spotlight.navigation.kamera.PostEditingFragments.CameraEditorImage;
import esaph.spotlight.navigation.kamera.PostEditingFragments.CameraEditorVideo;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class SwipeNavigation extends EsaphActivity implements
        EsaphDragableViewFragment.EsaphDragableFinishListener,
        ImageTakenListener
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private TextView textViewNoInternet;
    private NavigationCamera fragmentKamera;

    private CameraEditorImage fragmentCameraEditorImage;
    private CameraEditorVideo fragmentCameraEditorVideo;

    private FrameLayout frameLayoutCameraEditorImage;
    private FrameLayout frameLayoutCameraEditorVideo;

    private ViewPagerNavigationMain viewPagerBottomMainNavigation;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;

    public static final String extra_ActionNeedLogin = "esaph.mainactivity.login.session.needSID";

    public enum PREVIEW_MODE
    {
        CAMERA, PREVIEW;
    }

    private PREVIEW_MODE currentPreview_mode = PREVIEW_MODE.CAMERA;

    public void setupSystemUIByViewPagerPosition()
    {
        if(currentPreview_mode == PREVIEW_MODE.CAMERA) //Camera preview
        {
            if(esaphLockAbleViewPager.getCurrentItem() == 0)
            {
                EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(SwipeNavigation.this);
            }
            else
            {
                EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(SwipeNavigation.this);
            }
        }
        else if(currentPreview_mode == PREVIEW_MODE.PREVIEW)
        {
            EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(SwipeNavigation.this);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.out.println("OnDestroy");
        cardView = null;
        textViewNoInternet = null;
        esaphLockAbleViewPager = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");

        setContentView(R.layout.activity_swipe_navigation);

        cardView = (CardView) findViewById(R.id.cardViewNoPermissions);
        textViewNoInternet = (TextView) findViewById(R.id.viewMainNoInternet);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) findViewById(R.id.mainNavigationVerticalSwipeViewPager);

        esaphLockAbleViewPager.addOnPageChangeListener(SwipeNavigation.this.onPageChangeListenerMAIN_HANDLER);
        esaphLockAbleViewPager.setOffscreenPageLimit(1);
        viewPagerBottomMainNavigation = new ViewPagerNavigationMain(getSupportFragmentManager(), SwipeNavigation.this);
        esaphLockAbleViewPager.setAdapter(viewPagerBottomMainNavigation);

        frameLayoutCameraEditorImage = findViewById(R.id.cameraEditorImage);
        frameLayoutCameraEditorVideo = findViewById(R.id.cameraEditorVideo);

        fragmentCameraEditorImage = new CameraEditorImage();
        fragmentCameraEditorVideo = new CameraEditorVideo();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cameraEditorImage, fragmentCameraEditorImage).commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cameraEditorVideo, fragmentCameraEditorVideo).commit();

        Intent intent = getIntent();
        if(intent != null && intent.getAction() != null)
        {
            if(intent.getAction().equals(SwipeNavigation.extra_ActionNeedLogin))
            {
                if(!loginIn && isOnline(getApplicationContext()))
                {
                    loginIn = true;

                    Constraints constraints = new Constraints.Builder()
                            .setRequiresCharging(false)
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(SpotLightLoginSessionHandler.class)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR,
                                    WorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS)
                            .addTag("SESSION_KILL")
                            .build();

                    WorkManager.getInstance().beginUniqueWork(SpotLightLoginSessionHandler.class.getName(),
                            ExistingWorkPolicy.KEEP,
                            simpleRequest).enqueue();
                }
            }
        }

        TextView textViewRequestPermissionsAgain = (TextView) findViewById(R.id.textViewRequestPermissionAgain);
        textViewRequestPermissionsAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentKamera.showPermissionsDialog();
            }
        });

        fragmentKamera = (NavigationCamera) getSupportFragmentManager().findFragmentByTag("TagNavigationCamera");

        CLPreferences preferences = new CLPreferences(SwipeNavigation.this);
        if(preferences.displaySharingDialog())
        {
            DialogShareApp dialogShareApp = new DialogShareApp(SwipeNavigation.this);
            dialogShareApp.show();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!onActivityDispatchBackPressEvent())
        {
            super.onBackPressed();
        }
    }

    public boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        return false;
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentPreview_mode == PREVIEW_MODE.CAMERA)
        {
            setCameraViewMode();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        setupSystemUIByViewPagerPosition();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.broadcastReceiverInetStatus, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(this.broadcastReceiverInetStatus);
    }

    private final BroadcastReceiver broadcastReceiverInetStatus = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null)
            {
                if(isOnline(context))
                {
                    handleInternetEventWhenUserInOffline();
                    setAppIsOnline();
                }
                else
                {
                    setAppIsOffline();
                }
            }
        }
    };

    private void handleInternetEventWhenUserInOffline()
    {
        if(isOnline(getApplicationContext())) //User need a new sid, check login. When login in offline mode the sid is everytime null.
        {
            if(!loginIn)
            {
                loginIn = true;
            }
        }
    }

    private boolean loginIn = false;

    private void setAppIsOffline()
    {
        textViewNoInternet.animate()
                .alpha(1.0f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        textViewNoInternet.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void setAppIsOnline()
    {
        textViewNoInternet.animate()
                .alpha(0.0f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        textViewNoInternet.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(data.getAction() != null && data.getAction().equals("LOGOUT"))
            {
                stopService(new Intent(SwipeNavigation.this, MsgServiceConnection.class));
                Intent intent = new Intent(SwipeNavigation.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onActivityDispatchBackPressEvent()
    {
        if(!viewPagerBottomMainNavigation.isSendInfoEnabled())
        {
            if(esaphLockAbleViewPager.getCurrentItem() == ViewPagerNavigationMain.POSITION_CAMERA)
            {
                return fragmentKamera.onActivityDispatchedBackPressed();
            }
            else if(esaphLockAbleViewPager.getCurrentItem() == ViewPagerNavigationMain.POSITION_SPOTLIGHT)
            {
                EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = FragmentsEventDispatcherContainerHolder.getCurrentFragmentVisibleToUser(SwipeNavigation.this);
                if(esaphGlobalCommunicationFragment != null)
                {
                    return esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed();
                }
                else
                {
                    esaphLockAbleViewPager.setCurrentItem(0);
                    return true;
                }
            }
        }
        else
        {
            if(frameLayoutCameraEditorVideo.getVisibility() == View.VISIBLE)
            {
                if(!fragmentCameraEditorVideo.onActivityDispatchedBackPressed())
                {
                    setCameraViewMode();
                }
            }
            else if(frameLayoutCameraEditorImage.getVisibility() == View.VISIBLE)
            {
                if(!fragmentCameraEditorImage.onActivityDispatchedBackPressed())
                {
                    setCameraViewMode();
                }
            }

            return true;
        }

        return false;
    }

    public void setPreviewModeCameraEditor(long millisTaken) //Image
    {
        AbstractZoomHandler.setActivateZoom(false);

        esaphLockAbleViewPager.setTranslationY(0);
        currentPreview_mode = PREVIEW_MODE.PREVIEW;
        frameLayoutCameraEditorImage.setVisibility(View.VISIBLE);
        fragmentCameraEditorImage.show();
        esaphLockAbleViewPager.setCurrentItem(0);
        esaphLockAbleViewPager.setSwipeAllowed(false);

        PagerAdapter pagerAdapter = esaphLockAbleViewPager.getAdapter();

        if(pagerAdapter instanceof ViewPagerNavigationMain)
        {
            ((ViewPagerNavigationMain) pagerAdapter).setUpDialogSendInfo();
        }
    }

    public void setPreviewModeCameraEditor(final long millisTaken, final File file) //Video
    {
        AbstractZoomHandler.setActivateZoom(false);
        currentPreview_mode = PREVIEW_MODE.PREVIEW;

        frameLayoutCameraEditorVideo.setVisibility(View.VISIBLE);
        fragmentCameraEditorVideo.show(file, millisTaken);

        esaphLockAbleViewPager.setTranslationY(0);
        esaphLockAbleViewPager.setCurrentItem(0);
        esaphLockAbleViewPager.setSwipeAllowed(false);

        PagerAdapter pagerAdapter = esaphLockAbleViewPager.getAdapter();
        if(pagerAdapter instanceof ViewPagerNavigationMain)
        {
            ((ViewPagerNavigationMain) pagerAdapter).setUpDialogSendInfo();
        }
    }

    public void setCameraViewMode()
    {
        //Removing the editor from layoutout.
        frameLayoutCameraEditorVideo.setVisibility(View.INVISIBLE); //Do not set to gone, because you will have the same problem with wait and lock shit.
        frameLayoutCameraEditorImage.setVisibility(View.INVISIBLE); //Controling this dataraces will be very tricky, better do not waste your time.
        fragmentCameraEditorVideo.clearAllData();
        fragmentCameraEditorImage.clearAllData();

        AbstractZoomHandler.setActivateZoom(true);
        currentPreview_mode = PREVIEW_MODE.CAMERA;
        fragmentKamera.onFragmentAdded();

        PagerAdapter pagerAdapter = esaphLockAbleViewPager.getAdapter();
        if(pagerAdapter instanceof ViewPagerNavigationMain)
        {
            ((ViewPagerNavigationMain) pagerAdapter).removePreviewFragment();
        }

        esaphLockAbleViewPager.setSwipeAllowed(true);
    }

    private final ViewPager.OnPageChangeListener onPageChangeListenerMAIN_HANDLER = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            if(currentPreview_mode == PREVIEW_MODE.CAMERA) //Camera preview
            {
                if(position == 1)
                {
                    EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(SwipeNavigation.this);
                }
                else
                {
                    EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(SwipeNavigation.this);
                }
            }
            else
            {
                if(position == 0)
                {
                    EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(SwipeNavigation.this);
                }
                else
                {
                    EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(SwipeNavigation.this);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    public static class FragmentsEventDispatcherContainerHolder
    {
        private static final int[] arrayFragmentHolder = new int[]
                {
                        R.id.frameLayoutPrivateChat,
                        R.id.esaphMainFrameLayout,
                };

        public static EsaphGlobalCommunicationFragment getCurrentFragmentVisibleToUser(AppCompatActivity appCompatActivity)
        {
            EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment = null;

            for(int counter = 0; counter < arrayFragmentHolder.length; counter++)
            {
                esaphGlobalCommunicationFragment = (EsaphGlobalCommunicationFragment) appCompatActivity.getSupportFragmentManager().findFragmentById(
                        FragmentsEventDispatcherContainerHolder.arrayFragmentHolder[counter]);

                if(esaphGlobalCommunicationFragment != null)
                    return esaphGlobalCommunicationFragment;
            }

            return null;
        }
    }

    @Override
    public void onDragableFragmentClosed()
    {
        setupSystemUIByViewPagerPosition();
    }

    @Override
    public void onImageTaken()
    {
        setPreviewModeCameraEditor(System.currentTimeMillis());
    }

    @Override
    public void onImageReady(Bitmap bitmap)
    {
        if(bitmap != null && fragmentCameraEditorImage != null)
        {
            fragmentCameraEditorImage.onImageReady(bitmap);
        }
    }

    @Override
    public void onImageTakenFailed()
    {
    }

    private CardView cardView;
    public void setCameraViewPermissionGranted()
    {
        cardView.setVisibility(View.GONE);
    }

    public void setCameraViewNoPermissionsGranted()
    {
        cardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
