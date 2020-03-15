/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.BottomMiddleViewCameraPreview;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraEffectsEnum;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt.CameraPreviewFaceHunt;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewNormal;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.AbstractZoomHandler;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraPreview;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceGraphic;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicFaceTracker;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph.CAMERA_FACING_BACK;
import static esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph.CAMERA_FACING_FRONT;

public class NavigationCamera extends EsaphGlobalCommunicationFragment
{
    private BottomMiddleViewCameraPreview bottomMiddleViewCameraPreview;
    private SwipeNavigation swipeNavigation;

    private CameraPreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private CameraSourceEsaph mCameraSource = null;

    public static int PREF_CACHE_CAMERA_CHOOSEN = 0;
    public static String PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
    public static boolean PREF_CACHE_HAPTIC_FEEDBACK = false;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof SwipeNavigation)
        {
            swipeNavigation = (SwipeNavigation) context;
        }
    }

    public void onFragmentAdded()
    {
        setCameraEffectMode(currentCameraEffectsEnum);

        FragmentActivity activity = getActivity();
        if(activity != null && !activity.isFinishing())
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutBottomOptionsCameraTools, bottomMiddleViewCameraPreview)
                    .commit();
        }
    }

    public NavigationCamera()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if(context != null)
        {
            CLPreferences preferences = new CLPreferences(context);
            NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN = preferences.getCamOrientation();
            NavigationCamera.PREF_CACHE_FLASH_OPTION = preferences.getFlashOption();
            NavigationCamera.PREF_CACHE_HAPTIC_FEEDBACK = preferences.getHapticFeedBack();
        }

        bottomMiddleViewCameraPreview = BottomMiddleViewCameraPreview.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if(bottomMiddleViewCameraPreview != null)
        {
            bottomMiddleViewCameraPreview.onHorizontalSwipeListener.onPageSelected(0);
        }

        checkPermissions();
        setCameraEffectMode(currentCameraEffectsEnum);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        Context context = getContext();
        if(context != null && activity != null)
        {
            new BubbleShowCaseBuilder(activity)
                    .title(getResources().getString(R.string.txt_TutTakePicAndVid)) //Any title for the bubble view
                    .backgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryChat))
                    .showOnce("TAG_TUT_CAM")
                    .show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_navigation_camera, container, false);
        mPreview = rootView.findViewById(R.id.preview);
        mGraphicOverlay = rootView.findViewById(R.id.faceOverlay);
        return rootView;
    }

    public void switchCamera(ImageView imageView, ImageView imageViewFlash)
    {
        try
        {
            if(mCameraSource.getCameraFacing() == CAMERA_FACING_FRONT) //Front
            {
                NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN = CAMERA_FACING_BACK;
            }
            else if(mCameraSource.getCameraFacing() == CAMERA_FACING_BACK) //Back
            {
                NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN = CAMERA_FACING_FRONT;
            }

            handleFlashIconOnCameraSwitch(imageViewFlash);

            mPreview.stop();
            createCameraSource();
            startCameraSource();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "switchCamera() failed: " + ec);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Context context = getContext();
        if(context != null)
        {
            CLPreferences preferences = new CLPreferences(context);
            preferences.setCamOrientation(NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN);
            preferences.setNewFlashOption(NavigationCamera.PREF_CACHE_FLASH_OPTION);
            preferences.setNewHapticFeedBack(NavigationCamera.PREF_CACHE_HAPTIC_FEEDBACK);
        }

        mPreview.stop();
    }

    public void handleFlashIconOnCameraSwitch(ImageView imageView)
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            if(NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN == 0)
            {
                if(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                {
                    if(mCameraSource != null)
                    {
                        NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_ON;
                        mPreview.setLightning(NavigationCamera.PREF_CACHE_FLASH_OPTION);
                    }
                }
                else
                {
                    NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
                    mPreview.setLightning(NavigationCamera.PREF_CACHE_FLASH_OPTION);
                }
            }
            else
            {
                NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
                mPreview.setLightning(NavigationCamera.PREF_CACHE_FLASH_OPTION);
            }

            bottomMiddleViewCameraPreview.setFlashIcon(imageView);
        }
    }

    public void switchFlash(ImageView imageView)
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            if(NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN == 0)
            {
                if(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                {
                    if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_OFF))
                    {
                        NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_ON;
                    }
                    else if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                    {
                        NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
                    }

                    if(mCameraSource != null)
                    {
                        mPreview.setLightning(NavigationCamera.PREF_CACHE_FLASH_OPTION);
                    }
                }
                else
                {
                    NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
                }
            }
            else
            {
                NavigationCamera.PREF_CACHE_FLASH_OPTION = Camera.Parameters.FLASH_MODE_OFF;
            }

            mPreview.setLightning(NavigationCamera.PREF_CACHE_FLASH_OPTION);
            bottomMiddleViewCameraPreview.setFlashIcon(imageView);
        }
    }

    private void checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Activity activity = getActivity();
            if (activity != null && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
            {
                swipeNavigation.setCameraViewPermissionGranted();
                createCameraSource();
            }
            else
            {
                swipeNavigation.setCameraViewNoPermissionsGranted();
                showPermissionsDialog();
            }
        }
    }

    private void requestPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]
                    {
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.CHANGE_NETWORK_STATE,
                            android.Manifest.permission.WAKE_LOCK,
                    }, NavigationCamera.PERMISSION_REQUESTCODE);
        }
    }


    public void showPermissionsDialog()
    {
        Context context = getContext();
        if(context != null)
        {
            AlertDialog.Builder dialogA = new AlertDialog.Builder(context);
            dialogA.setTitle(getResources().getString(R.string.txt_alertPermissionMissingTitle));
            dialogA.setMessage(getResources().getString(R.string.txt_alertPermissionMissing));
            dialogA.setPositiveButton(getResources().getString(R.string.txt_alertPermissionButtonTrue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    requestPerm();
                    Log.i(getClass().getName(), "Permission granted.");
                }
            });

            dialogA.setNegativeButton(getResources().getString(R.string.txt_alertPermissionButtonFalse), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Log.i(getClass().getName(), "Permission not granted.");
                }
            });

            dialogA.show();
        }
    }

    private static final int PERMISSION_REQUESTCODE = 354;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean granted = false;
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

        if (granted)
        {
            swipeNavigation.setCameraViewPermissionGranted();
            createCameraSource();
        }
        else
        {
            swipeNavigation.setCameraViewNoPermissionsGranted();
        }
    }


    private void createCameraSource()
    {
        Context context = getContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        mCameraSource = new CameraSourceEsaph.Builder(context, detector)
                .setRequestedPreviewSize(1920, 1080)
                .setFacing(NavigationCamera.PREF_CACHE_CAMERA_CHOOSEN)
                .setRequestedFps(30.0f)
                .build();
    }


    public CameraSourceEsaph getmCameraSource()
    {
        return mCameraSource;
    }

    private CameraEffectsEnum currentCameraEffectsEnum = CameraEffectsEnum.NONE;
    //Setting effects like, pauseAndSeekToZero and go recording, only working for video. Or maybe image.
    public void setCameraEffectMode(CameraEffectsEnum cameraEffectMode)
    {
        AbstractZoomHandler.setActivateZoom(true);
        this.currentCameraEffectsEnum = cameraEffectMode;
        switch (currentCameraEffectsEnum)
        {
            case NONE:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutCameraPreviewHandler, CameraPreviewNormal.getInstance())
                        .commit();
                break;

            case OVERLAY_FILTER:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutCameraPreviewHandler, CameraPreviewNormal.getInstance())
                        .commit();
                break;

                /*
            case CUTABLE:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutCameraPreviewHandler, CameraPreviewCutable.getInstance())
                        .commit();
                break;*/

            case FACE_HUNT:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutCameraPreviewHandler, CameraPreviewFaceHunt.getInstance())
                        .commit();
                break;
        }
    }




    private static final int RC_HANDLE_GMS = 2;
    private void startCameraSource()
    {
        // check that the device has play services available.
        Context context = getContext();
        if(context != null)
        {
            int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            if (code != ConnectionResult.SUCCESS)
            {
                Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
                dlg.show();
            }

            if (mCameraSource != null)
            {
                try
                {
                    mPreview.start(mCameraSource, mGraphicOverlay);
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }
        }
    }

    private FaceGraphic faceGraphic;
    private GraphicFaceTracker graphicFaceTracker;
    public void setOverLayoutFilterEffect(FaceGraphic faceGraphic)
    {
        if(this.mGraphicOverlay != null)
        {
            this.mGraphicOverlay.clear();
            faceGraphic.setmOverlay(mGraphicOverlay);
        }

        if(graphicFaceTracker != null)
        {
            graphicFaceTracker.setmFaceGraphic(faceGraphic);
        }

        this.faceGraphic = faceGraphic;
    }

    public GraphicOverlay getmGraphicOverlay() {
        return mGraphicOverlay;
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>
    {
        @Override
        public Tracker<Face> create(Face face)
        {
            if(mGraphicOverlay != null)
            {
                mGraphicOverlay.clear();
                faceGraphic.setmOverlay(mGraphicOverlay);
            }

            graphicFaceTracker = new GraphicFaceTracker(mGraphicOverlay);
            mCameraSource.setGraphicFaceTracker(graphicFaceTracker);
            graphicFaceTracker.setmFaceGraphic(faceGraphic);
            return graphicFaceTracker;
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(bottomMiddleViewCameraPreview.isAdded())
        {
            return bottomMiddleViewCameraPreview.onActivityDispatchedBackPressed();
        }
        
        return false;
    }
}
