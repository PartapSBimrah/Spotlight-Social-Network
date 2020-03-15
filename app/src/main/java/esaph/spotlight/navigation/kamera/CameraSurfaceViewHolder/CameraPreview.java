package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;
import esaph.spotlight.navigation.kamera.ImageTakenListener;
import esaph.spotlight.navigation.kamera.RunnableProceedPicture;

public class CameraPreview extends ViewGroup implements Camera.PreviewCallback
{
    private static final String TAG = "CameraPreview";

    private ImageTakenListener imageTakenListener;
    private CameraViewStateListener cameraViewStateListener;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSourceEsaph mCameraSource;

    private GraphicOverlay mOverlay;

    public CameraPreview(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;
        this.imageTakenListener = (ImageTakenListener) context;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    public void start(CameraSourceEsaph cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            mCameraSource.setPreviewCallBackListener(this);
            startIfReady();
        }
    }

    public void start(CameraSourceEsaph cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop()
    {
        if (mCameraSource != null)
        {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @SuppressLint("MissingPermission")
    private void startIfReady() throws IOException
    {
        if (mStartRequested && mSurfaceAvailable)
        {
            mCameraSource.start(mSurfaceView.getHolder());
            requestLayout();
            if (mOverlay != null)
            {
                //changeStupidLayoutHeights();
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
                if(cameraViewStateListener != null)
                {
                    cameraViewStateListener.onCameraReadyListener(mCameraSource.getmCamera());
                }
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback
    {
        @Override
        public void surfaceCreated(SurfaceHolder surface)
        {
            mSurfaceAvailable = true;
            try
            {
                startIfReady();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface)
        {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (mCameraSource != null)
        {
            Size size = mCameraSource.getPreviewSize();

            if (size != null)
            {
                Log.e(TAG,"SURFACEVIEW_onLayout: " + size.getHeight() + " - " + size.getWidth());
                for (int i = 0; i < getChildCount(); ++i)
                {
                    int width = size.getHeight();
                    int height = size.getWidth();

                    float ratio = (float) width / (float) height;
                    float difference = getHeight() - height;
                    float nWidth = (ratio * difference) + width;

                    getChildAt(i).layout(left, top, (int) nWidth, getHeight());
                }
            }
            else
            {
                Log.e(TAG,"SURFACEVIEW_: NULL");
                for (int i = 0; i < getChildCount(); ++i)
                {
                    getChildAt(i).layout(left, top, right, bottom);
                }
            }
        }
        else
        {
            Log.e(TAG,"SURFACEVIEW_: NULL CAMERA SOURCE");
            for (int i = 0; i < getChildCount(); ++i)
            {
                getChildAt(i).layout(left, top, right, bottom);
            }
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }


    public CameraSourceEsaph getmCameraSource() {
        return mCameraSource;
    }

    private boolean needPic = false;

    private String mLightning = "";

    public void setLightning(String lightning) {
        mLightning = lightning;
    }

    public void takePic()
    {
        if(mLightning.equals(Camera.Parameters.FLASH_MODE_ON))
        {
            Camera.Parameters parameters = mCameraSource.getmCamera().getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCameraSource.getmCamera().setParameters(parameters);

            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if(imageTakenListener != null)
        {
            imageTakenListener.onImageTaken();
            System.out.println("onTouch called preview normal: ON IMAGE TAKEN");
        }

        needPic = true;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if(needPic)
        {
            needPic = false;
            executorService.submit(new Thread(new RunnableProceedPicture(data, getContext(), CameraPreview.this)));
        }
    }

    public String getmLightning()
    {
        return mLightning;
    }

    public ImageTakenListener getImageTakenListener() {
        return imageTakenListener;
    }

    public void setCameraViewStateListener(CameraViewStateListener cameraViewStateListener) {
        this.cameraViewStateListener = cameraViewStateListener;
    }

    public SurfaceView getmSurfaceView() {
        return mSurfaceView;
    }
}

