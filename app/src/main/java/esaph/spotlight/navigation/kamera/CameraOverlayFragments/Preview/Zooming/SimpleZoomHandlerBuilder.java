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



