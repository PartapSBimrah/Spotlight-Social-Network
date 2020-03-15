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
 * Created by Tuyen Nguyen on 2/12/17.
 */

public class Line extends GraphicObject {
  private PointF point1;
  private PointF point2;

  public void setPoint1(PointF point1) {
    this.point1 = point1;
  }

  public void setPoint2(PointF point2) {
    this.point2 = point2;
  }

  public PointF getPoint1() {
    return point1;
  }

  public PointF getPoint2() {
    return point2;
  }

  @Override public void draw(Canvas canvas) {
    canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
  }
}
