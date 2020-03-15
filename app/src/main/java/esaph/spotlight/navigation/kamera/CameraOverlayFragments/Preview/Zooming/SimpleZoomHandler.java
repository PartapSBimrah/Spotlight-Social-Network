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


