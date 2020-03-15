/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.Zooming;

import android.view.View;

public class SimpleZoomHandlerBuilder {

    private SimpleZoomHandler simpleZoomHandler;

    public static SimpleZoomHandlerBuilder forView(View touchableView) {
        return new SimpleZoomHandlerBuilder(touchableView);
    }

    private SimpleZoomHandlerBuilder(View touchableView) {
        simpleZoomHandler = new SimpleZoomHandler(touchableView);
    }

    public SimpleZoomHandlerBuilder setZoomListener(SimpleZoomHandler.IZoomHandlerListener listener) {
        simpleZoomHandler.setZoomHandlerListener(listener);
        return this;
    }

    public SimpleZoomHandlerBuilder setMaxZoom(float maxZoom) {
        simpleZoomHandler.setMaxZoom(maxZoom);
        return this;
    }

    public SimpleZoomHandler build() {
        return simpleZoomHandler;
    }
}



