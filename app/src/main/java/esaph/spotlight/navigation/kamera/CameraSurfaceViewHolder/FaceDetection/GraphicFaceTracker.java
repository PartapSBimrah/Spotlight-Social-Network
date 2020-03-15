package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class GraphicFaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private boolean huntFace = false;

    public GraphicFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    public void setmFaceGraphic(FaceGraphic mFaceGraphic) {
        this.mFaceGraphic = mFaceGraphic;
    }

    public GraphicOverlay getmOverlay() {
        return mOverlay;
    }

    public interface FaceHunterCallback
    {
        void onHuntedFace(Face face);
    }

    private FaceHunterCallback faceHunterCallback;
    public void huntFace(FaceHunterCallback faceHunterCallback)
    {
        this.faceHunterCallback = faceHunterCallback;
        this.huntFace = true;
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(int faceId, Face item)
    {
        mFaceGraphic.setId(faceId);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face)
    {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);
        if(huntFace)
        {
            huntFace = false;
            faceHunterCallback.onHuntedFace(face);
            faceHunterCallback = null;
        }
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }

    public FaceGraphic getmFaceGraphic() {
        return mFaceGraphic;
    }
}
