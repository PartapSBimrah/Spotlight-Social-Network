package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import esaph.spotlight.Esaph.EsaphPositionTabView.EsaphCameraTabJana;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.SimpleZoomHandler;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.SimpleZoomHandlerBuilder;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraPreview;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraViewStateListener;
import esaph.spotlight.navigation.kamera.MediaRecorderFileHandler;
import esaph.spotlight.navigation.kamera.NavigationCamera;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.CountDownTimerWithPause;

public class CameraPreviewCutable extends Fragment implements CameraViewStateListener
{
    private View rootView;
    private CameraPreview mCameraView;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private SwipeNavigation swipeNavigation;

    private int fingerBoxCenterX;
    private int fingerBoxCenterY;
    private ProgressBar progressBarRecordingTime;
    private TextView textViewFinishRecording;
    private EsaphCameraTabJana esaphCameraTabJana;
    private Vibrator v;
    private MediaRecorder mediaRecorder;
    private float mDist;
    private File fileStandardCurrentVideoPath;

    public CameraPreviewCutable()
    {
        // Required empty public constructor
    }

    public static CameraPreviewCutable getInstance()
    {
        return new CameraPreviewCutable();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof SwipeNavigation)
        {
            swipeNavigation = (SwipeNavigation) context;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        esaphLockAbleViewPager.setSwipeAllowed(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_camera_preview_cutable, container, false);
        progressBarRecordingTime = rootView.findViewById(R.id.progressBarCutableProgress);
        textViewFinishRecording = rootView.findViewById(R.id.imageViewFinishCutableVideo);
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null)
        {
            v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();
        if(activity != null)
        {
            mCameraView = getActivity().findViewById(R.id.preview);
            esaphLockAbleViewPager = (EsaphLockAbleViewPager) activity.findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        }


        mCameraView.setCameraViewStateListener(this);
        if(mCamera != null)
        {
            setupZoomHandler(mCamera.getParameters());
        }
        else if(mCameraView.getmCameraSource() != null && mCameraView.getmCameraSource().getmCamera() != null)
        {
            mCamera = mCameraView.getmCameraSource().getmCamera();
            setupZoomHandler(mCamera.getParameters());
        }

        textViewFinishRecording.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(swipeNavigation != null)
                {
                    swipeNavigation.setPreviewModeCameraEditor(System.currentTimeMillis(),
                            new File(fileStandardCurrentVideoPath.getAbsolutePath()));
                }
            }
        });

        esaphLockAbleViewPager.setSwipeAllowed(false);
        calculateFingerTabAnimations();
    }


    private long downTime;
    private final View.OnTouchListener onTouchListenerSurfaceViewZoom = new View.OnTouchListener()
    {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) //Should return true when isRecording or taking picture.
        {
            try
            {
                int action = event.getAction();

                if (event.getPointerCount() > 1)
                {
                    return true;
                }
                else
                {
                    // handle single touch events
                    switch(action)
                    {
                        case MotionEvent.ACTION_DOWN:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_SHOW);
                            esaphCameraTabJana.setTranslationX(event.getRawX() - fingerBoxCenterX);
                            esaphCameraTabJana.setTranslationY(event.getRawY() - fingerBoxCenterY);
                            downTime = System.currentTimeMillis();
                            break;

                        case MotionEvent.ACTION_UP:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_HIDE);
                            insertNewRecordTimePeriod(downTime - startMillisVideo, downTime - System.currentTimeMillis());
                            break;

                        case MotionEvent.ACTION_MOVE:
                            esaphCameraTabJana.setTranslationX(event.getRawX() - fingerBoxCenterX);
                            esaphCameraTabJana.setTranslationY(event.getRawY() - fingerBoxCenterY);
                            if(isRecording)
                            {
                                return true;
                            }
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_HIDE);
                            break;
                    }
                }
            }
            catch (Exception ec)
            {
                System.out.println("Taking touch pic failed: " + ec);
            }
            return false;
        }
    };

    private void calculateFingerTabAnimations()
    {
        esaphCameraTabJana = new EsaphCameraTabJana(getContext());
        ViewGroup.LayoutParams layoutParamsFrameLayoutFingerTab =
                new RelativeLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        150, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                150, getResources().getDisplayMetrics()));

        fingerBoxCenterX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                75,
                getResources().getDisplayMetrics());
        fingerBoxCenterY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                75,
                getResources().getDisplayMetrics());

        ((ViewGroup) rootView.getRootView()).addView(esaphCameraTabJana, layoutParamsFrameLayoutFingerTab);
    }


    /** Determine the space between the first two fingers */
    public float getFingerSpacing(MotionEvent event)
    {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void vibrateDeviceTouch()
    {
        if(!NavigationCamera.PREF_CACHE_HAPTIC_FEEDBACK)
            return;

        if(v != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                v.vibrate(VibrationEffect.createOneShot(25, 1));
            }
            else {
                //deprecated in API 26
                v.vibrate(25);
            }
        }
    }

    private long[][] recordedTimePeriods = new long[][]{};

    private boolean isRecording = false;


    private void insertNewRecordTimePeriod(long startTime, long endTime)
    {
        int index = recordedTimePeriods.length-1;
        if(index < 0)
        {
            index = 0;
        }

        recordedTimePeriods[index][0] = startTime;
        recordedTimePeriods[index][1] = endTime;
    }

    private long startMillisVideo;
    public void handleRecorderState() //Recording video
    {
        try
        {
            vibrateDeviceTouch();
            if (isRecording)
            {
                if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                {
                    Camera.Parameters parameters = mCameraView.getmCameraSource().getmCamera().getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCameraView.getmCameraSource().getmCamera().setParameters(parameters);
                }

                mediaRecorder.stop();

                countDownTimerWithPause.pause();
                esaphCameraTabJana.setIsRecording(false);
                isRecording = false;

                initRecorder();
            }
            else
            {
                Log.i(getClass().getName(), "Recorder started");
                try
                {
                    if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                    {
                        Camera.Parameters parameters = mCameraView.getmCameraSource().getmCamera().getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCameraView.getmCameraSource().getmCamera().setParameters(parameters);
                    }

                    esaphCameraTabJana.setIsRecording(true);
                    mediaRecorder.start();
                    startMillisVideo = System.currentTimeMillis();
                    countDownTimerWithPause.resume();
                    isRecording = true;
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Cant start isRecording video: " + ec);
                    if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                    {
                        Camera.Parameters parameters = mCameraView.getmCameraSource().getmCamera().getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCameraView.getmCameraSource().getmCamera().setParameters(parameters);
                    }
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "handleRecorderState() failed: " + ec);
        }
    }

    private CountDownTimerWithPause countDownTimerWithPause = new CountDownTimerWithPause(21000, 1000, false)
    {
        @Override
        public void onTick(long millisUntilFinished) {
            int seconds = (int) ((21000 - millisUntilFinished) / 1000) % 60;

            final int newProgress = (int) ((seconds * 100) / 21);

            if(esaphCameraTabJana != null)
            {
                esaphCameraTabJana.setProgress(100 - newProgress);
                progressBarRecordingTime.setProgress(100-newProgress);
            }
        }

        @Override
        public void onFinish()
        {
            if(isRecording)
            {
                handleRecorderState();

                if(swipeNavigation != null)
                {
                    swipeNavigation.setPreviewModeCameraEditor(System.currentTimeMillis(),
                            new File(fileStandardCurrentVideoPath.getAbsolutePath()));
                }
            }
        }
    };

    private void initRecorder()
    {
        try
        {
            mediaRecorder.reset();

            Log.i(getClass().getName(), "MediaRecorder INITALIZING...");

            fileStandardCurrentVideoPath = MediaRecorderFileHandler.getFile(getContext(), CameraEffectsEnum.NONE);

            int currentCameraOpened = mCameraView.getmCameraSource().getCameraFacing();
            if(currentCameraOpened == 0)
            {
                Log.i(getClass().getName(), "MediaRecorder INITALIZING WITH: 0/90");
                mCameraView.getmCameraSource().getmCamera().setDisplayOrientation(90);
                mediaRecorder.setOrientationHint(90);
            }
            else if(currentCameraOpened == 1)
            {
                Log.i(getClass().getName(), "MediaRecorder INITALIZING WITH: 1/270");
                mCameraView.getmCameraSource().getmCamera().setDisplayOrientation(90);
                mediaRecorder.setOrientationHint(270);
            }

            mediaRecorder.setCamera(mCameraView.getmCameraSource().getmCamera());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile.get(mCameraView.getmCameraSource().getCameraFacing(), CamcorderProfile.QUALITY_480P));
            mediaRecorder.setOutputFile(fileStandardCurrentVideoPath.getAbsolutePath());

            mediaRecorder.setVideoSize(1920, 1080);
            mediaRecorder.setVideoFrameRate(18);
            mediaRecorder.setMaxDuration(21000); // Set max duration 21 sec.

            Log.i(getClass().getName(), "Surface Created, preparing recorder ...");
            try
            {
                mediaRecorder.prepare();
                Log.i(getClass().getName(), "Surface Created, prepared recorder.");
            }
            catch (IllegalStateException e)
            {
                Log.i(getClass().getName(), "prepareRecorder failed(): " + e);
            }
            catch (IOException e)
            {
                Log.i(getClass().getName(), "prepareRecorder failed(): " + e);
            }

            Log.i(getClass().getName(), "MediaRecorder INITALIZING... OK");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Cant prepared media recorder: " + ec);
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mediaRecorder = new MediaRecorder();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mediaRecorder != null)
        {
            mediaRecorder.reset();
            mediaRecorder.release();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(isRecording)
        {
            handleRecorderState();
        }
    }

    @Override
    public void onCameraReadyListener(Camera camera)
    {
        if(isAdded())
        {
            mCamera = camera;
            initRecorder();
            setupZoomHandler(camera.getParameters());
        }
    }

    private Camera mCamera;
    private void setupZoomHandler(final Camera.Parameters parameters)
    {
        if ( parameters.isZoomSupported() ) {
            SimpleZoomHandlerBuilder.forView(esaphLockAbleViewPager)
                    .setMaxZoom( parameters.getMaxZoom() )
                    .setZoomListener( new SimpleZoomHandler.IZoomHandlerListener() {
                        @Override
                        public void onZoomChanged(int newZoom) {
                            Camera.Parameters params = mCameraView.getmCameraSource().getmCamera().getParameters();
                            params.setZoom(newZoom);
                            mCameraView.getmCameraSource().getmCamera().setParameters(params);
                            float zoomPercentage = (float) newZoom / (float) params.getMaxZoom();
                            float newStrokeWidth = (DisplayUtils.dp2px(8f) * zoomPercentage) + DisplayUtils.dp2px(2); //MAX 10 dp, min 2 dp hehe #genius boss
                            esaphCameraTabJana.setStrokeWidth((int) newStrokeWidth);
                        }
                    } )
                    .build().setOnTouchListenerSecond(onTouchListenerSurfaceViewZoom).setZoomHardness(0.7f);
        }
    }

}
