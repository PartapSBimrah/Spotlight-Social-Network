package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.OverLayEffect;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.GraphicOverlay;

public abstract class OverLayEffect extends GraphicOverlay.Graphic
{
    private Canvas canvasHolder;
    private GraphicOverlay graphicOverlay;
    private ValueAnimator animator;

    private boolean isAnimating = false;

    public abstract ValueAnimator getAnimator();

    @Override
    public void draw(GraphicOverlay graphicOverlay, Canvas canvas)
    {
        System.out.println("OverLayTextMessage: CANVAS " + canvas);

        if(canvasHolder != null && canvasHolder != canvas)
        {
            System.out.println("OverLayTextMessage: Removing canvas");
            canvasHolder.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvasHolder = null;
            animator.cancel();
            isAnimating = false;
        }

        if(!isAnimating)
        {
            isAnimating = true;
            this.canvasHolder = canvas;
            this.graphicOverlay = graphicOverlay;
            animator = getAnimator();
            animator.start();
        }
    }

    public Canvas getCanvas() {
        return canvasHolder;
    }

    public GraphicOverlay getGraphicOverlay() {
        return graphicOverlay;
    }
}
