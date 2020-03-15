/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations.type;

import android.animation.ValueAnimator;
import android.graphics.Canvas;

import esaph.spotlight.Esaph.EsaphAnimations.model.Circle;

/**
 * Created by Tuyen Nguyen on 2/12/17.
 */

public class FishSpinner extends LoaderView {
  private Circle[] circles;
  private int numberOfCircle;
  private float[] rotates;

  public FishSpinner() {
    numberOfCircle = 5;
    rotates = new float[numberOfCircle];
  }

  @Override public void initializeObjects() {
    final float size = Math.min(width, height);
    final float circleRadius = size / 10.0f;
    circles = new Circle[numberOfCircle];

    for (int i = 0; i < numberOfCircle; i++) {
      circles[i] = new Circle();
      circles[i].setCenter(center.x, circleRadius);
      circles[i].setColor(color);
      circles[i].setRadius(circleRadius - circleRadius * i / 6);
    }
  }

  @Override public void setUpAnimation() {
    for (int i = 0; i < numberOfCircle; i++) {
      final int index = i;

      ValueAnimator fadeAnimator = ValueAnimator.ofFloat(0, 360);
      fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
      fadeAnimator.setDuration(1700);
      fadeAnimator.setStartDelay(index * 100);
      fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          rotates[index] = (float)animation.getAnimatedValue();
          if (invalidateListener != null) {
            invalidateListener.reDraw();
          }
        }
      });

      fadeAnimator.start();
    }
  }

  @Override public void draw(Canvas canvas) {
    for (int i = 0; i < numberOfCircle; i++) {
      canvas.save();
      canvas.rotate(rotates[i], center.x, center.y);
      circles[i].draw(canvas);
      canvas.restore();
    }
  }
}
