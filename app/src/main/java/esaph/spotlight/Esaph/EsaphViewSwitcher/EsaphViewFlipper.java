/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphViewSwitcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class EsaphViewFlipper extends ViewFlipper
{
    private ViewSwitcherClickListener viewSwitcherClickListener;

    public EsaphViewFlipper(Context context) {
        super(context);
    }

    public EsaphViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        triggerPoint = w * 0.40f;
    }

    public void setViewSwitcherClickListener(ViewSwitcherClickListener viewSwitcherClickListener)
    {
        this.viewSwitcherClickListener = viewSwitcherClickListener;
    }

    public ViewSwitcherClickListener getViewSwitcherClickListener() {
        return viewSwitcherClickListener;
    }

    private int viewWidth;
    private float triggerPoint;
    private float downX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(getChildCount() <= 1)
            return super.onTouchEvent(event);

        float x = event.getX();
        int ACTION = event.getAction();
        switch (ACTION)
        {
            case ACTION_DOWN:
                downX = x;
                return true;

            case ACTION_UP:
                if(downX > triggerPoint) //Next
                {
                    showNext();
                    if(viewSwitcherClickListener != null)
                    {
                        viewSwitcherClickListener.onNextClicked(getChildAt(getDisplayedChild()), getDisplayedChild());
                    }
                }
                else //Previews
                {
                    showPrevious();
                    if(viewSwitcherClickListener != null)
                    {
                        viewSwitcherClickListener.onPreviusClicked(getChildAt(getDisplayedChild()), getDisplayedChild());
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface ViewSwitcherClickListener
    {
        void onNextClicked(View view, int pos);
        void onPreviusClicked(View view, int pos);
    }

}
