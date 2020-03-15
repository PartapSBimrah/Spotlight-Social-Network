/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations.model;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Created by Tuyen Nguyen on 2/10/17.
 */

public class Circle extends GraphicObject {
  private PointF center;
  private float radius;

  public Circle() {
    center = new PointF();
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public void setCenter(float x, float y) {
    center.set(x, y);
  }

  @Override public void draw(Canvas canvas) {
    canvas.drawCircle(center.x, center.y, radius, paint);
  }
}
