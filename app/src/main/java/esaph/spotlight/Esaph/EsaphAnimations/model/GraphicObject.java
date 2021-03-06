/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations.model;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Tuyen Nguyen on 2/12/17.
 */

public abstract class GraphicObject {
  protected Paint paint;

  public GraphicObject() {
    paint = new Paint();
    paint.setAntiAlias(true);
  }

  public void setColor(int color) {
    paint.setColor(color);
  }

  public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
  }

  public void setWidth(float width) {
    paint.setStrokeWidth(width);
  }

  public void setStyle(Paint.Style style) {
    paint.setStyle(style);
  }

  public abstract void draw(Canvas canvas);
}
