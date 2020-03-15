package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.FaceDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerSticker;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class EsaphImageFaceDetector {

    public enum SizeMode { FaceMarginPx, EyeDistanceFactorMargin };

    private static final int MAX_FACES = 8;
    private static final int MIN_FACE_SIZE = 100;

    private int mFaceMinSize = MIN_FACE_SIZE;
    private int mFaceMarginPx = 10;
    private float mEyeDistanceFactorMargin = 2f;
    private int mMaxFaces = MAX_FACES;
    private SizeMode mSizeMode = SizeMode.EyeDistanceFactorMargin;
    private boolean mDebug;
    private Paint mDebugPainter;
    private Paint mDebugAreaPainter;
    private List<EsaphSpotLightSticker> listStickersTemporalySaved = new ArrayList<>();

    public EsaphImageFaceDetector() {
        initPaints();
    }

    public EsaphImageFaceDetector(int faceMarginPx) {
        setFaceMarginPx(faceMarginPx);
        initPaints();
    }

    public EsaphImageFaceDetector(float eyesDistanceFactorMargin) {
        setEyeDistanceFactorMargin(eyesDistanceFactorMargin);
        initPaints();
    }

    private void initPaints()
    {
        mDebugPainter = new Paint();
        mDebugPainter.setColor(Color.RED);
        mDebugPainter.setAlpha(80);

        mDebugAreaPainter = new Paint();
        mDebugAreaPainter.setColor(Color.GREEN);
        mDebugAreaPainter.setAlpha(80);
    }

    public int getMaxFaces() {
        return mMaxFaces;
    }

    public void setMaxFaces(int maxFaces) {
        this.mMaxFaces = maxFaces;
    }

    public int getFaceMinSize() {
        return mFaceMinSize;
    }

    public void setFaceMinSize(int faceMinSize) {
        mFaceMinSize = faceMinSize;
    }

    public int getFaceMarginPx() {
        return mFaceMarginPx;
    }

    public void setFaceMarginPx(int faceMarginPx) {
        mFaceMarginPx = faceMarginPx;
        mSizeMode = SizeMode.FaceMarginPx;
    }

    public SizeMode getSizeMode() {
        return mSizeMode;
    }

    public float getEyeDistanceFactorMargin() {
        return mEyeDistanceFactorMargin;
    }

    public void setEyeDistanceFactorMargin(float eyeDistanceFactorMargin) {
        mEyeDistanceFactorMargin = eyeDistanceFactorMargin;
        mSizeMode = SizeMode.EyeDistanceFactorMargin;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    protected List<Bitmap> cropFaces(Bitmap original)
    {
        List<Bitmap> listCroppedFaces = new ArrayList<>();

        Bitmap fixedBitmap = BitmapUtils.forceEvenBitmapSize(original);
        fixedBitmap = BitmapUtils.forceConfig565(fixedBitmap);
        Bitmap mutableBitmap = fixedBitmap.copy(Bitmap.Config.RGB_565, true);

        FaceDetector faceDetector = new FaceDetector(
                mutableBitmap.getWidth(), mutableBitmap.getHeight(),
                mMaxFaces);

        FaceDetector.Face[] faces = new FaceDetector.Face[mMaxFaces];

        // The bitmap must be in 565 format (for now).
        int faceCount = faceDetector.findFaces(mutableBitmap, faces);

        if (faceCount == 0)
        {
            return listCroppedFaces;
        }

        PointF centerFace = new PointF();
        Canvas canvas;

        /*
        Paint paintDrawTransparentBitmap = new Paint();
        paintDrawTransparentBitmap.setStyle(Paint.Style.FILL);
        paintDrawTransparentBitmap.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paintDrawTransparentBitmap.setColor(Color.TRANSPARENT);*/


        Paint paintDrawingRounded = new Paint();
        paintDrawingRounded.setAntiAlias(true);

        for (int i = 0; i < faceCount; i++)
        {
            FaceDetector.Face face = faces[i];

            Bitmap output = Bitmap.createBitmap(mutableBitmap.getWidth(),
                    mutableBitmap.getHeight(), Bitmap.Config.ARGB_8888);

            canvas = new Canvas(output);

            //canvas.drawRect(0F, 0F, (float) output.getWidth(), (float) output.getHeight(), paintDrawTransparentBitmap);

            face.getMidPoint(centerFace);


            canvas.drawARGB(0, 0, 0, 0);
            Rect rect = new Rect(0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());
            canvas.drawCircle(centerFace.x, centerFace.y, face.eyesDistance() * 1.5f, paintDrawingRounded);
            paintDrawingRounded.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(mutableBitmap, rect, rect, paintDrawingRounded);

            int x = (int) centerFace.x - (int) ((face.eyesDistance() * 1.5f));
            int y = (int) centerFace.y - (int) ((face.eyesDistance() * 1.5f));

            int faceWidth = (int) ((face.eyesDistance() * 1.5f) * 2); //Breite Gesicht.
            int faceHeight = (int) ((face.eyesDistance() * 1.5f) * 2); //Höhe Gesicht.

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

            Bitmap bitmapFinally = Bitmap.createBitmap(output, x, y, faceWidth, faceHeight);
            output = null;
            listCroppedFaces.add(bitmapFinally);
        }

        return listCroppedFaces;
    }

    private Bitmap getCroppedBitmap(CropResult cropResult)
    {
        Bitmap croppedBitmap = Bitmap.createBitmap(cropResult.getBitmap(),
                cropResult.getInit().x,
                cropResult.getInit().y,
                cropResult.getEnd().x - cropResult.getInit().x,
                cropResult.getEnd().y - cropResult.getInit().y);

        Bitmap output = Bitmap.createBitmap(croppedBitmap.getWidth(),
                croppedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(
                croppedBitmap.getWidth() / 2f,
                croppedBitmap.getHeight() / 2f,
                croppedBitmap.getWidth() / 2f,
                paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(croppedBitmap, rect, rect, paint);

        return output;
    }

    public List<Bitmap> findFaces(Bitmap bitmap)
    {
        return cropFaces(bitmap);
    }

    public List<EsaphSpotLightSticker> getListStickersTemporalySaved()
    {
        return listStickersTemporalySaved;
    }

    public void saveFacesTemp(Context context, List<Bitmap> listFaces)
    //Generate stickers and bind them with the image save location together.
    {
        StorageHandler.dropTempFiles(context);
        listStickersTemporalySaved.clear();
        for (Bitmap bitmapIterate : listFaces)
        {
            EsaphSpotLightSticker esaphSpotLightSticker = new EsaphSpotLightSticker(
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    System.currentTimeMillis(),
                    -1,
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis());

            File stickerFile = StorageHandler.getFile(context,
                    StorageHandler.FOLDER__TEMP,
                    esaphSpotLightSticker.getIMAGE_ID(),
                    null,
                    StorageHandler.STICKER_PREFIX);

            StorageHandler.saveToResolutionsWithCompression(context,
                    StorageHandlerSticker.scaleSticker(bitmapIterate),
                    stickerFile,
                    EsaphGlobalValues.COMP_RATE_STICKER);

            listStickersTemporalySaved.add(esaphSpotLightSticker);
        }
    }

    protected class CropResult
    {
        Bitmap mBitmap;
        Point mInit;
        Point mEnd;

        public CropResult(Bitmap bitmap, Point init, Point end) {
            mBitmap = bitmap;
            mInit = init;
            mEnd = end;
        }

        public CropResult(Bitmap bitmap) {
            mBitmap = bitmap;
            mInit = new Point(0, 0);
            mEnd = new Point(bitmap.getWidth(), bitmap.getHeight());
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public Point getInit() {
            return mInit;
        }

        public Point getEnd() {
            return mEnd;
        }
    }
}
