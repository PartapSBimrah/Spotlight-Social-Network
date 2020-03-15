package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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

public class CameraPreviewNormal extends Fragment implements CameraViewStateListener
{
    private CameraPreview mCameraView;
    private View rootView;
    private GestureDetector gestureDetector;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private SwipeNavigation swipeNavigation;


    private boolean wasVideoRecording = false;
    private boolean wasTakeImage = false;
    private int fingerBoxCenterX;
    private int fingerBoxCenterY;
    private EsaphCameraTabJana esaphCameraTabJana;
    private Vibrator v;
    private MediaRecorder mediaRecorder;
    private AtomicBoolean recording = new AtomicBoolean(false);
    private File fileStandardCurrentVideoPath;



    public CameraPreviewNormal()
    {
        // Required empty public constructor
    }

    public static CameraPreviewNormal getInstance()
    {
        return new CameraPreviewNormal();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_camera_preview_normal, container, false);
        this.rootView = rootView;
        mCameraView = getActivity().findViewById(R.id.preview);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) getActivity().findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
        calculateFingerTabAnimations();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(getContext(), onGestureListener);

        Activity activity = getActivity();
        if (activity != null)
        {
            v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e)
        {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            wasTakeImage = true;
            wasVideoRecording = false;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            if (e.getPointerCount() <= 1)
            {
                handleCameraRecorderState();
                wasTakeImage = false;
                wasVideoRecording = true;
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            return false;
        }
    };


    private final View.OnTouchListener onTouchListenerSurfaceViewZoom = new View.OnTouchListener()
    {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) //Should return true when recording or taking picture.
        {
            System.out.println("Touch event was clicked in camera preview normal.");
            try
            {
                gestureDetector.onTouchEvent(event);
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
                            return true;

                        case MotionEvent.ACTION_UP:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_HIDE);

                            if(wasTakeImage)
                            {
                                esaphLockAbleViewPager.setOnTouchListener(null);
                                vibrateDeviceTouch();
                                mCameraView.takePic();
                            }
                            else if(wasVideoRecording) //Stop video here
                            {
                                esaphLockAbleViewPager.setOnTouchListener(null);
                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        handleCameraRecorderState();
                                    }
                                }).start();
                            }

                            wasTakeImage = false;
                            wasVideoRecording = false;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            esaphCameraTabJana.setTranslationX(event.getRawX() - fingerBoxCenterX);
                            esaphCameraTabJana.setTranslationY(event.getRawY() - fingerBoxCenterY);
                            if(isRecording())
                            {
                                return true;
                            }
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_HIDE);

                            if(wasVideoRecording) //Stop video here
                            {
                                esaphLockAbleViewPager.setOnTouchListener(null);
                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        handleCameraRecorderState();
                                    }
                                }).start();
                            }

                            wasTakeImage = false;
                            wasVideoRecording = false;
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

    public boolean isRecording()
    {
        return recording.get();
    }

    public void handleCameraRecorderState() //Recording video
    {
        try
        {
            vibrateDeviceTouch();

            if(recording.compareAndSet(true, false))
            {
                if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                {
                    Camera.Parameters parameters = mCameraView.getmCameraSource().getmCamera().getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCameraView.getmCameraSource().getmCamera().setParameters(parameters);
                }

                esaphLockAbleViewPager.setSwipeAllowed(true);
                countDownTimer.cancel();
                esaphCameraTabJana.setIsRecording(false);

                mediaRecorder.stop(); //this line must be below the lines above. Because it can throw an exception if, the video is shorter than a second.
                mCamera.lock();
                mCameraView.getmCameraSource().resetPreviewCallback(); //Because of the camera is getting locked for a video, we have to set the preview callback again.

                if(swipeNavigation != null)
                {
                    swipeNavigation.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            swipeNavigation.setPreviewModeCameraEditor(System.currentTimeMillis(),
                                    new File(fileStandardCurrentVideoPath.getAbsolutePath()));
                        }
                    });
                }
            }
            else if(recording.compareAndSet(false, true))
            {
                esaphLockAbleViewPager.setSwipeAllowed(false);
                Log.i(getClass().getName(), "Recorder started");
                try
                {
                    initRecorder();
                    esaphCameraTabJana.setIsRecording(true);
                    mediaRecorder.start();
                    countDownTimer.start();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Cant start recording video: " + ec);
                }
                finally
                {
                    if(NavigationCamera.PREF_CACHE_FLASH_OPTION.equals(Camera.Parameters.FLASH_MODE_ON))
                    {
                        Camera.Parameters parameters = mCameraView.getmCameraSource().getmCamera().getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCameraView.getmCameraSource().getmCamera().setParameters(parameters);
                    }
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "handleRecorderState() failed: " + ec);
            if(isAdded() && mCamera != null)
            {
                setupZoomHandler(mCamera.getParameters());
            }
        }
    }

    private final CountDownTimer countDownTimer = new CountDownTimer(21000, 1000)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {
            int seconds = (int) ((21000 - millisUntilFinished) / 1000) % 60;

            final int newProgress = (int) ((seconds * 100) / 21);

            if(esaphCameraTabJana != null)
            {
                esaphCameraTabJana.setProgress(100 - newProgress);
            }
        }

        @Override
        public void onFinish()
        {
            if(isRecording())
            {
                handleCameraRecorderState();
            }
        }
    };



    private void initRecorder()
    {
        try
        {
            fileStandardCurrentVideoPath = MediaRecorderFileHandler.getFile(getContext(), CameraEffectsEnum.NONE);
            if(mediaRecorder != null)
            {
                mediaRecorder.reset();
            }

            mCamera.unlock();

            int currentCameraOpened = mCameraView.getmCameraSource().getCameraFacing();
            if(currentCameraOpened == 0)
            {
                //mCameraView.getmCameraSource().getmCamera().setDisplayOrientation(90);
                mediaRecorder.setOrientationHint(90);
            }
            else if(currentCameraOpened == 1)
            {
              //  mCameraView.getmCameraSource().getmCamera().setDisplayOrientation(90);
                mediaRecorder.setOrientationHint(270);
            }

            mediaRecorder.setCamera(mCamera);

            //Set audio source
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //set video source
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

            //set output format
            // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // set the size of video.
            // If the size is not applicable then throw the media recorder pauseAndSeekToZero
            // -19 error

            // mediaRecorder.setVideoSize(mCameraView.getmCameraSource().getPreviewSize().getWidth(),
            // mCameraView.getmCameraSource().getPreviewSize().getHeight());

            // Set the video encoding bit rate this changes for the high, low.
            // medium quality devices
            //mediaRecorder.setVideoEncodingBitRate(1700000);

            //Set the video frame rate
            //mediaRecorder.setVideoFrameRate(30);

            //set audio encoder format
            // mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            //set video encoder format
            // mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            //Show the display preview
           // mediaRecorder.setPreviewDisplay(mCameraView.getmCameraSource(.getSurface());

            //output file path
            mediaRecorder.setOutputFile(fileStandardCurrentVideoPath.getAbsolutePath());

            mediaRecorder.prepare();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
        if(isRecording())
        {
            handleCameraRecorderState();
        }
    }

    @Override
    public void onCameraReadyListener(Camera camera)
    {
        if(isAdded())
        {
            mCamera = camera;
            setupZoomHandler(camera.getParameters());
        }
    }

    private Camera mCamera;
    private void setupZoomHandler(Camera.Parameters parameters)
    {
        System.out.println("setupZoomhandler() ");
        if (parameters != null && parameters.isZoomSupported())
        {
            SimpleZoomHandlerBuilder.forView(esaphLockAbleViewPager)
                    .setMaxZoom(parameters.getMaxZoom())
                    .setZoomListener(new SimpleZoomHandler.IZoomHandlerListener()
                    {
                        @Override
                        public void onZoomChanged(int newZoom)
                        {
                            Camera.Parameters params = mCameraView.getmCameraSource().getmCamera().getParameters();
                            params.setZoom(newZoom);
                            mCameraView.getmCameraSource().getmCamera().setParameters(params);
                            float zoomPercentage = (float) newZoom / (float) params.getMaxZoom();
                            float newStrokeWidth = (DisplayUtils.dp2px(8f) * zoomPercentage) + DisplayUtils.dp2px(2); //MAX 10 dp, min 2 dp hehe #genius boss
                            esaphCameraTabJana.setStrokeWidth((int) newStrokeWidth);
                        }
                    } )
                    .build()
                    .setOnTouchListenerSecond(onTouchListenerSurfaceViewZoom).setZoomHardness(0.7f);
            System.out.println("setupZoomhandler() success");
        }
    }
}
