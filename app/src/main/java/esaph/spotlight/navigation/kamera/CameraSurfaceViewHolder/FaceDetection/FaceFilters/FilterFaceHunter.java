package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceGraphic;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

public class FilterFaceHunter extends FaceGraphic
{
    private Paint paint;

    public FilterFaceHunter()
    {
        super();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(30,0,0, paint.getColor());
        paint.setStrokeWidth(DisplayUtils.dp2px(6));
        paint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));
    }

    @Override
    public void draw(GraphicOverlay graphicOverlay, Canvas canvas)
    {
        Face face = getmFace();

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);


        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
