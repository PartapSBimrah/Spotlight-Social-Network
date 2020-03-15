
/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapUtils
{
    public static Bitmap forceEvenBitmapSize(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width % 2 == 1) {
            width++;
        }
        if (height % 2 == 1) {
            height++;
        }

        Bitmap fixedBitmap = original;
        if (width != original.getWidth() || height != original.getHeight()) {
            fixedBitmap = Bitmap.createScaledBitmap(original, width, height, false);
        }

        if (fixedBitmap != original) {
            original.recycle();
        }

        return fixedBitmap;
    }

    public static Bitmap forceConfig565(Bitmap original) {
        Bitmap convertedBitmap = original;
        if (original.getConfig() != Bitmap.Config.RGB_565) {
            convertedBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(convertedBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawBitmap(original, 0, 0, paint);

            if (convertedBitmap != original) {
                original.recycle();
            }
        }

        return convertedBitmap;
    }
}
