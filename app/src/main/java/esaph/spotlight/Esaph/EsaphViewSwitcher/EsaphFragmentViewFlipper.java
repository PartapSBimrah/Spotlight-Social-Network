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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class EsaphFragmentViewFlipper extends FrameLayout
{
    private FragmentManager fragmentManager;
    private ViewSwitcherClickListener viewSwitcherClickListener;
    private List<Fragment> listViews;
    private int currentPost = 0;

    public EsaphFragmentViewFlipper(Context context) {
        super(context);
        listViews = new ArrayList<>();
    }

    public EsaphFragmentViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        listViews = new ArrayList<>();
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

    public void setData(List<Fragment> listViews, FragmentManager fragmentManager)
    {
        this.listViews = listViews;
        this.fragmentManager = fragmentManager;
        currentPost = -1;
        showNext();
    }

    public void showNext()
    {
        ++currentPost;
        if(currentPost >= listViews.size())
        {
            if(viewSwitcherClickListener != null)
            {
                viewSwitcherClickListener.onEndReached();
            }
            return;
        }

        fragmentManager
                .beginTransaction()
                .replace(this.getId(), listViews.get(currentPost))
                .commit();
    }

    public void showPrevious()
    {
        --currentPost;
        if(currentPost < 0) return;

        fragmentManager
                .beginTransaction()
                .replace(this.getId(), listViews.get(currentPost))
                .commit();
    }

    private int viewWidth;
    private float triggerPoint;
    private float downX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
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
                        viewSwitcherClickListener.onNextClicked(currentPost);
                    }
                }
                else //Previews
                {
                    showPrevious();
                    if(viewSwitcherClickListener != null)
                    {
                        viewSwitcherClickListener.onPreviusClicked(currentPost);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface ViewSwitcherClickListener
    {
        void onEndReached();
        void onNextClicked(int pos);
        void onPreviusClicked(int pos);
    }

}
