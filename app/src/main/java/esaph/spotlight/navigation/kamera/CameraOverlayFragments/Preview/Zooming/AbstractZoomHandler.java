/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming;

import android.view.MotionEvent;
import android.view.View;

public abstract class AbstractZoomHandler implements View.OnTouchListener {

    private static final int FINGER_SPACING_DELTA_FOR_ZOOM = 25;
    private static final int FINGER_SPACING_ZOOM_INCREMENT = 5;

    private static final float DEFAULT_ZOOM_HARDNESS = 0.4f;

    private float lastFingerSpacingTime;
    private float fingerSpacingBuffer;

    protected int zoomLevel;
    protected float zoomIncrement;
    protected float maxZoom;
    private View.OnTouchListener onTouchListenerSecond;

    public AbstractZoomHandler(View touchableView) {
        touchableView.setOnTouchListener( this );
        this.lastFingerSpacingTime = 0;
        this.fingerSpacingBuffer = 0;
        this.zoomLevel = 1;
        setZoomHardness( DEFAULT_ZOOM_HARDNESS );
    }

    private static boolean activateZoom = true;

    public static void setActivateZoom(boolean activateZoom) {
        AbstractZoomHandler.activateZoom = activateZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public void setZoomHardness(float zoomHardness) {
        this.zoomIncrement = Math.round( zoomHardness * FINGER_SPACING_ZOOM_INCREMENT);
    }

    public AbstractZoomHandler setOnTouchListenerSecond(View.OnTouchListener onTouchListenerSecond)
    {
        this.onTouchListenerSecond = onTouchListenerSecond;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        System.out.println("HANDLING EVENT______: " + event.getAction());

        if(!AbstractZoomHandler.activateZoom)
        {
            if(onTouchListenerSecond != null)
            {
                onTouchListenerSecond.onTouch(v, event);
                return true;
            }
        }
        else
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                fingerSpacingBuffer = 0;
            }

            if (isTwoFingersTouchEvent( event ))
            {
                int newZoomLevel = performTwoFingersZoom(event);
                recalculateZoom(newZoomLevel);
            }

            if(onTouchListenerSecond != null)
            {
                return onTouchListenerSecond.onTouch(v, event);
            }
        }

        return false;
    }

    private boolean isTwoFingersTouchEvent(MotionEvent event) {
        return event.getPointerCount() == 2;
    }

    private int performTwoFingersZoom(MotionEvent event) {
        int newZoomLevel = zoomLevel;
        float currentFingerSpacingTime = getFingerSpacing( event );
        fingerSpacingBuffer += currentFingerSpacingTime - lastFingerSpacingTime;
        if ( fingerSpacingBuffer >= FINGER_SPACING_DELTA_FOR_ZOOM && maxZoom > zoomLevel ) {
            newZoomLevel += zoomIncrement;
            fingerSpacingBuffer = 0;
        } else if ( fingerSpacingBuffer <= -FINGER_SPACING_DELTA_FOR_ZOOM && zoomLevel > 1 ) {
            newZoomLevel -= zoomIncrement;
            ;
            fingerSpacingBuffer = 0;
        }
        lastFingerSpacingTime = currentFingerSpacingTime;
        return newZoomLevel;
    }

    private void recalculateZoom(int newZoomLevel)
    {
        if(newZoomLevel > maxZoom) return;

        if ( newZoomLevel == zoomLevel ) {
            return;
        }
        zoomLevel = newZoomLevel;
        notifyZoomChanged( zoomLevel );
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX( 0 ) - event.getX( 1 );
        float y = event.getY( 0 ) - event.getY( 1 );
        return (float) Math.sqrt( x * x + y * y );
    }

    public abstract void notifyZoomChanged(int zoom);

    public abstract boolean isPrepared();

}
