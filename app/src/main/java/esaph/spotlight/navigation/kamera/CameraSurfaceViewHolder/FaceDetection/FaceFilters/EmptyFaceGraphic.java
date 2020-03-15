/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

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
