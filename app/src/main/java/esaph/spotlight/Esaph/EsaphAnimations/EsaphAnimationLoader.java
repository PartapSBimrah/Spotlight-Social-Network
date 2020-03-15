/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import esaph.spotlight.Esaph.EsaphAnimations.callback.InvalidateListener;
import esaph.spotlight.Esaph.EsaphAnimations.type.LoaderView;
import esaph.spotlight.Esaph.EsaphAnimations.util.LoaderGenerator;
import esaph.spotlight.R;

public class EsaphAnimationLoader extends View implements InvalidateListener {
  private LoaderView loaderView;

  public EsaphAnimationLoader(Context context)
  {
    super(context);
    initialize(context, null, 0);
  }

  public EsaphAnimationLoader(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs, 0);
  }

  public EsaphAnimationLoader(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs, defStyleAttr);
  }

  @RequiresApi(21)
  public EsaphAnimationLoader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  private void initialize(Context context, AttributeSet attrs, int defStyleAttr)
  {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EsaphAnimationLoader);
    int loaderType = typedArray.getInt(R.styleable.EsaphAnimationLoader_am_type,-1);

    loaderView = LoaderGenerator.generateLoaderView(loaderType);
    loaderView.setColor(typedArray.getColor(R.styleable.EsaphAnimationLoader_am_color, Color.parseColor("#ffffff")));

    typedArray.recycle();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int measuredWidth = resolveSize(loaderView.getDesiredWidth(), widthMeasureSpec);
    final int measuredHeight = resolveSize(loaderView.getDesiredHeight(), heightMeasureSpec);

    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    loaderView.setSize(getWidth(), getHeight());
    loaderView.initializeObjects();
    loaderView.setUpAnimation();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    loaderView.draw(canvas);
  }

  @Override public void reDraw() {
    invalidate();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (loaderView != null && loaderView.isDetached()) {
      loaderView.setInvalidateListener(this);
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (loaderView != null) {
      loaderView.onDetach();
    }
  }
}
