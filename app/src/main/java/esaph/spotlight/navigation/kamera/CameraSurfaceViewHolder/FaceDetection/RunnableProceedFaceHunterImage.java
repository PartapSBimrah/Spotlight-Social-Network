package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.BitmapUtils;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerSticker;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.CameraPreview;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

import static esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.CameraSourceEsaph.CAMERA_FACING_FRONT;

public class RunnableProceedFaceHunterImage implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<FaceCroppingDoneListener> faceCroppingDoneListenerWeakReference;
    private WeakReference<CameraPreview> cameraViewWeakReference;
    private byte[] data;
    private Face face;

    public RunnableProceedFaceHunterImage(byte[] data,
                                          Face face,
                                          Context context,
                                          FaceCroppingDoneListener faceCroppingDoneListener,
                                          CameraPreview cameraView)
    {
        this.data = data;
        this.face = face;
        this.contextWeakReference = new WeakReference<>(context);
        this.faceCroppingDoneListenerWeakReference = new WeakReference<>(faceCroppingDoneListener);
        this.cameraViewWeakReference = new WeakReference<>(cameraView);
    }

    public interface FaceCroppingDoneListener
    {
        void onHuntedFaceCropped(Bitmap bitmap);
    }

    @Override
    public void run()
    {
        try
        {
            Camera mCamera = cameraViewWeakReference.get().getmCameraSource().getmCamera();

            if(this.cameraViewWeakReference.get().getmLightning().equals(Camera.Parameters.FLASH_MODE_ON))
            {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }

            Rect rect = new Rect(0, 0, mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            YuvImage yuvImage = new YuvImage(data, mCamera.getParameters().getPreviewFormat(), mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height,
                    null);
            yuvImage.compressToJpeg(rect, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            Bitmap rawBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


            Matrix matrix = new Matrix();
            if(cameraViewWeakReference.get().getmCameraSource().getCameraFacing() == CAMERA_FACING_FRONT)
            {
                matrix.preScale(-1.0f, 1.0f);
            }
            matrix.postRotate(90);
            Bitmap bitmapTransformationApplyd = Bitmap.createBitmap(rawBitmap,0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);

            FaceGraphic faceGraphic = cameraViewWeakReference.get().getmCameraSource().getGraphicFaceTracker().getmFaceGraphic();

            final Bitmap finalBitmap = cutFaceOutFromBitmap(faceGraphic, bitmapTransformationApplyd);

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    FaceCroppingDoneListener faceCroppingDoneListener = faceCroppingDoneListenerWeakReference.get();
                    if(faceCroppingDoneListener != null)
                    {
                        faceCroppingDoneListener.onHuntedFaceCropped(finalBitmap);
                    }
                }
            });

            EsaphSpotLightSticker esaphSpotLightSticker = new EsaphSpotLightSticker(
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    System.currentTimeMillis(),
                    -1,
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis());

            File file = StorageHandler.getFile(contextWeakReference.get(),
                    StorageHandler.FOLDER__SPOTLIGHT_STICKER,
                    esaphSpotLightSticker.getIMAGE_ID(),
                    null,
                    StorageHandler.STICKER_PREFIX);

            StorageHandler.saveToResolutions(contextWeakReference.get(),
                    StorageHandlerSticker.scaleSticker(finalBitmap),
                    file);
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
        finally
        {
            Log.i(getClass().getName(), "FINALLY");

            /*
            CameraView cameraView = this.cameraViewWeakReference.get();
            if(cameraView != null)
            {
                cameraView.setNeedPic(false);
            }*/
        }
    }


    private Bitmap cutFaceOutFromBitmap(FaceGraphic faceGraphic, Bitmap bitmapTransformationApplyd)
    {
        float x = faceGraphic.translateX(face.getPosition().x + face.getWidth() / 2); //Mittelpunkt des gesichts
        float y = faceGraphic.translateY(face.getPosition().y + face.getHeight() / 2); //Mittelpunkt des gesichts. Die breite durdch die hälfte gleich mitte des gesicht wird addiert mit dem linken start punkt auf dem bildschrim des gesichts.
        float xOffset = faceGraphic.scaleX(face.getWidth() / 2.0f);
        float yOffset = faceGraphic.scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;


        Bitmap fixedBitmap = BitmapUtils.forceEvenBitmapSize(bitmapTransformationApplyd);
        fixedBitmap = BitmapUtils.forceConfig565(fixedBitmap);
        Bitmap mutableBitmap = fixedBitmap.copy(Bitmap.Config.RGB_565, true);



        Bitmap output = Bitmap.createBitmap(mutableBitmap.getWidth(),
                mutableBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        canvas.drawARGB(0, 0, 0, 0);


        Rect rect = new Rect(0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());
        Paint paintDrawingRounded = new Paint();
        paintDrawingRounded.setAntiAlias(true);

        //canvas.drawRect(left, top, right, bottom, paintDrawingRounded);
        // TODO: 27.04.2019 moving shit

        canvas.drawCircle(x,
                y,
                face.getHeight() / 2, paintDrawingRounded);

        paintDrawingRounded.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mutableBitmap, rect, rect, paintDrawingRounded);


        //This code generates everytime a 512x512 image.
/*
        int faceWidth = (int) right / 2;
        int faceHeight = (int) bottom / 2;

        if(x + faceWidth > output.getWidth())
        {
            faceWidth = (int) (faceWidth - ((x+faceWidth) - output.getWidth()));
        }

        if(y + faceHeight > output.getHeight())
        {
            faceHeight = (int) (faceHeight - ((y+faceHeight) - output.getHeight()));
        }

        if(x <= 0)
        {
            faceWidth = (int) (faceWidth + x);
            x = 0;
        }

        if(y <= 0)
        {
            faceHeight = (int) (faceHeight + y);
            y = 0;
        }

        Bitmap bitmapFinally = Bitmap.createBitmap(output, (int) x/2, (int) y/2, faceWidth, faceHeight);
        output = null;
        */

        return output;
    }
}
