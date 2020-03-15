/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming;

import android.view.View;

public class SimpleZoomHandler extends AbstractZoomHandler {

    private IZoomHandlerListener zoomHandlerListener;

    public SimpleZoomHandler(View touchableView) {
        super(touchableView);
    }

    public void setZoomHandlerListener(IZoomHandlerListener zoomHandlerListener) {
        this.zoomHandlerListener = zoomHandlerListener;
    }

    @Override
    public void notifyZoomChanged(int zoom) {
        zoomHandlerListener.onZoomChanged( zoom );
    }

    @Override
    public boolean isPrepared() {
        return zoomHandlerListener != null;
    }

    public interface IZoomHandlerListener {

        void onZoomChanged(int newZoom);

    }
}


