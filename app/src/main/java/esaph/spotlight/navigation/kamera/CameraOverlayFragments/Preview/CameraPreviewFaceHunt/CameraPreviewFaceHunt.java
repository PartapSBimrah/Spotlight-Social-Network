package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.face.Face;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import esaph.spotlight.Esaph.EsaphPositionTabView.EsaphCameraTabJana;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt.DisplayingStickerRecyclerView.RecyclerViewAdapterFaceHunter;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.SimpleZoomHandler;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming.SimpleZoomHandlerBuilder;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraPreview;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraViewStateListener;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters.FilterFaceHunter;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.RunnableProceedFaceHunterImage;
import esaph.spotlight.navigation.kamera.NavigationCamera;

public class CameraPreviewFaceHunt extends Fragment implements CameraViewStateListener
{
    private CameraPreview mCameraView;
    private View rootView;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;

    private int fingerBoxCenterX;
    private int fingerBoxCenterY;
    private EsaphCameraTabJana esaphCameraTabJana;
    private Vibrator v;
    private float mDist;
    private NavigationCamera navigationCamera;

    private RecyclerViewAdapterFaceHunter adapterFaceHunter;
    private RecyclerView recyclerViewStickers;


    public CameraPreviewFaceHunt()
    {
        // Required empty public constructor
    }

    public static CameraPreviewFaceHunt getInstance()
    {
        return new CameraPreviewFaceHunt();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_camera_preview_sticker_hunt, container, false);
        this.rootView = rootView;
        recyclerViewStickers = rootView.findViewById(R.id.recyclerViewDisplayingStickers);

        Activity activity = getActivity();
        if(activity != null)
        {
            mCameraView = activity.findViewById(R.id.preview);
            esaphLockAbleViewPager = (EsaphLockAbleViewPager) activity.findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        }
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

        FragmentActivity activity = getActivity();
        if(activity != null && !activity.isFinishing())
        {
            navigationCamera = (NavigationCamera) activity.getSupportFragmentManager().findFragmentByTag("TagNavigationCamera");
            mCameraView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(isAdded() && navigationCamera != null)
                    {
                        navigationCamera.setOverLayoutFilterEffect(new FilterFaceHunter());
                    }
                }
            });
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewStickers.setLayoutManager(linearLayoutManager);
        recyclerViewStickers.setAdapter(adapterFaceHunter);
        esaphLockAbleViewPager.setSwipeAllowed(false);
        calculateFingerTabAnimations();
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

        adapterFaceHunter = new RecyclerViewAdapterFaceHunter(getContext());
    }

    private final View.OnTouchListener onTouchListenerSurfaceViewZoom = new View.OnTouchListener()
    {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) //Should return true when recording or taking picture.
        {
            try
            {
                System.out.println("HANDLING EVENT: " + event.getAction());
                Rect editTextRect = new Rect();
                recyclerViewStickers.getHitRect(editTextRect);
                if (editTextRect.contains((int)event.getX(), (int)event.getY())) return false; //This is for, usage of the recyclerview

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

                            vibrateDeviceTouch();

                            mCameraView.getmCameraSource().huntFace(new CameraSourceEsaph.CameraSourceDataArrayFrameListener()
                            {
                                @Override
                                public void onFrameReceived(byte[] data, Face face)
                                {
                                    new Thread(new RunnableProceedFaceHunterImage(data,
                                            face,
                                            getContext(),
                                            new RunnableProceedFaceHunterImage.FaceCroppingDoneListener()
                                            {
                                                @Override
                                                public void onHuntedFaceCropped(Bitmap bitmap)
                                                {
                                                    showHuntedFace(bitmap);
                                                }
                                            }, mCameraView)).start();
                                }
                            });
                            break;

                        case MotionEvent.ACTION_MOVE:
                            esaphCameraTabJana.setTranslationX(event.getRawX() - fingerBoxCenterX);
                            esaphCameraTabJana.setTranslationY(event.getRawY() - fingerBoxCenterY);
                            break;

                        case MotionEvent.ACTION_UP:
                            esaphCameraTabJana.setState(EsaphCameraTabJana.STATE_HIDE);
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
            return true;
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


    private void showHuntedFace(Bitmap bitmap)
    {
        new Thread(new SaveHuntedFace(getContext(), bitmap, new SaveHuntedFace.OnHuntedFaceSavedListener()
        {
            @Override
            public void onSaved(EsaphSpotLightSticker esaphSpotLightSticker)
            {
                if(isAdded() && adapterFaceHunter != null)
                {
                    adapterFaceHunter.addFace(esaphSpotLightSticker);
                }
            }

            @Override
            public void onFailed()
            {
            }
        })).start();

        /*
        EsaphGlobalImageLoader.with(getContext())
                .displaySticker(esaphCircleImageView,
                        null,
                        new EsaphDimension(esaphCircleImageView.getWidth(),
                                esaphCircleImageView.getHeight()),
                        esaphSpotLightSticker,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.drawable_face_hunt_faces_adapter_item_no_image);*/
    }


    private Camera mCamera;
    private void setupZoomHandler(final Camera.Parameters parameters)
    {
        if (parameters.isZoomSupported())
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
                    }).build().setOnTouchListenerSecond(onTouchListenerSurfaceViewZoom).setZoomHardness(0.7f);
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
}
