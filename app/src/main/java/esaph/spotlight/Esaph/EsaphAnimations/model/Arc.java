/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations.model;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Tuyen Nguyen on 2/13/17.
 */

public class Arc extends GraphicObject {
  private RectF oval;
  private float startAngle;
  private float sweepAngle;
  private boolean useCenter;

  public void setOval(RectF oval) {
    this.oval = oval;
  }

  public void setStartAngle(float startAngle) {
    this.startAngle = startAngle;
  }

  public void setSweepAngle(float sweepAngle) {
    this.sweepAngle = sweepAngle;
  }

  public void setUseCenter(boolean useCenter) {
    this.useCenter = useCenter;
  }

  public float getStartAngle() {
    return startAngle;
  }

  @Override public void draw(Canvas canvas) {
    canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
  }
}
