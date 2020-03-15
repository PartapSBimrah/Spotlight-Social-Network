package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.google.android.gms.vision.face.Face;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.BitmapUtils;

public class FaceCropperForSticker
{
    private static final float FACE_POSITION_RADIUS = 10.0f;

    public static Bitmap crop(Face face, Bitmap originalFrame)
    {
        Bitmap fixedBitmap = BitmapUtils.forceEvenBitmapSize(originalFrame);
        fixedBitmap = BitmapUtils.forceConfig565(fixedBitmap);
        Bitmap mutableBitmap = fixedBitmap.copy(Bitmap.Config.RGB_565, true);


        Canvas canvas;

        /*
        Paint paintDrawTransparentBitmap = new Paint();
        paintDrawTransparentBitmap.setStyle(Paint.Style.FILL);
        paintDrawTransparentBitmap.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paintDrawTransparentBitmap.setColor(Color.TRANSPARENT);*/


        Paint paintDrawingRounded = new Paint();
        paintDrawingRounded.setAntiAlias(true);

        Bitmap output = Bitmap.createBitmap(mutableBitmap.getWidth(),
                mutableBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(output);

        //canvas.drawRect(0F, 0F, (float) output.getWidth(), (float) output.getHeight(), paintDrawTransparentBitmap);


        canvas.drawARGB(0, 0, 0, 0);
        Rect rect = new Rect(0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());

        float x = face.getPosition().x + face.getWidth() / 2;
        float y = face.getPosition().y + face.getHeight() / 2;

        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, paintDrawingRounded);
        paintDrawingRounded.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mutableBitmap, rect, rect, paintDrawingRounded);


        float faceWidth = face.getWidth();
        float faceHeight = face.getHeight();


        if(x + faceWidth > output.getWidth())
        {
            faceWidth = faceWidth - ((x+faceWidth) - output.getWidth());
        }

        if(y + faceHeight > output.getHeight())
        {
            faceHeight = faceHeight - ((y+faceHeight) - output.getHeight());
        }

        if(x <= 0)
        {
            faceWidth = faceWidth + x;
            x = 0;
        }

        if(y <= 0)
        {
            faceHeight = faceHeight + y;
            y = 0;
        }

        Bitmap bitmapFinally = Bitmap.createBitmap(output, (int) x, (int) y, (int) faceWidth, (int) faceHeight);
        output = null;
        return bitmapFinally;
    }

}
