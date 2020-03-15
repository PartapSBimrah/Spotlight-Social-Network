/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import esaph.spotlight.R;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraPreview;

import static esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph.CAMERA_FACING_FRONT;

public class RunnableProceedPicture implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<CameraPreview> cameraViewWeakReference;
    private byte[] data;

    public RunnableProceedPicture(byte[] data,
                                  Context context,
                                  CameraPreview cameraView)
    {
        this.data = data;
        this.contextWeakReference = new WeakReference<>(context);
        this.cameraViewWeakReference = new WeakReference<>(cameraView);
    }

    @Override
    public void run()
    {
        try
        {
            Camera mCamera = cameraViewWeakReference.get().getmCameraSource().getmCamera();
            Camera.Parameters parameters = mCamera.getParameters();

            if(this.cameraViewWeakReference.get().getmLightning().equals(Camera.Parameters.FLASH_MODE_ON))
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }

            Rect rect = new Rect(0, 0, parameters.getPreviewSize().width, parameters.getPreviewSize().height);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            YuvImage yuvImage = new YuvImage(data, parameters.getPreviewFormat(), parameters.getPreviewSize().width, parameters.getPreviewSize().height,
                    null);
            yuvImage.compressToJpeg(rect, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


            Matrix matrix = new Matrix();
            if(cameraViewWeakReference.get().getmCameraSource().getCameraFacing() == CAMERA_FACING_FRONT)
            {
                matrix.preScale(-1.0f, 1.0f);
            }
            matrix.postRotate(90);
            final Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    CameraPreview cameraPreview = cameraViewWeakReference.get();
                    if(cameraPreview != null)
                    {
                        cameraPreview.getImageTakenListener().onImageReady(finalBitmap);
                    }
                }
            });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Error Taking Picture: " + ec);
            AlertDialog.Builder dialog = new AlertDialog.Builder(contextWeakReference.get());
            dialog.setTitle(contextWeakReference.get().getResources().getString(R.string.txt_alertCameraCantOpenTitle));
            dialog.setMessage(contextWeakReference.get().getResources().getString(R.string.txt_alertCameraCantTakePictureDetails) + ec);
            dialog.show();
            cameraViewWeakReference.get().getImageTakenListener().onImageTakenFailed();
        }
    }
}
