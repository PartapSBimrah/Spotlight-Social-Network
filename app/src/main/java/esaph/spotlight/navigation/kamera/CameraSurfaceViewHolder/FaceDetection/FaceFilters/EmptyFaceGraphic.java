package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceFilters;

import android.graphics.Canvas;

import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.FaceGraphic;
import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

public class EmptyFaceGraphic extends FaceGraphic
{
    public EmptyFaceGraphic() {
    }

    @Override
    public void draw(GraphicOverlay graphicOverlay, Canvas canvas) {
        super.draw(graphicOverlay, canvas);
    }
}
