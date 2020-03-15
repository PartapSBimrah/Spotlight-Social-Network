/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphDragable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphDragableViewFragment extends FrameLayout
{
    private EsaphActivity esaphActivity;
    private static float SHOW_PANEL_PERCENTAGE = 1f;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int mDraggingState = 0;
    private View mViewTargeting;
    private EsaphViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mPanelHeightCollapsed;
    private int dragViewId;
    private boolean mIsOpen;
    private EsaphDragableFinishListener esaphDragableFinishListener;

    public interface EsaphDragableFinishListener
    {
        void onDragableFragmentClosed();
    }

    public EsaphDragableViewFragment(@NonNull Context context)
    {
        super(context);
        init(context, null);
    }

    public EsaphDragableViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EsaphDragableViewFragment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(21)
    public EsaphDragableViewFragment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public void addView(View child)
    {
        super.addView(child);

        esaphGlobalCommunicationFragmentCurrentAttachedTo = (EsaphGlobalCommunicationFragment)
                esaphActivity.getSupportFragmentManager().findFragmentById(EsaphDragableViewFragment.this.getId());

        ViewCompat.offsetTopAndBottom(this, mPanelHeightCollapsed);

        if(mDragHelper.smoothSlideViewTo(EsaphDragableViewFragment.this, 0, 0))
        {
            ViewCompat.postInvalidateOnAnimation(EsaphDragableViewFragment.this);
        }
    }

    private void init(Context context, AttributeSet attributeSet)
    {
        if(context instanceof EsaphActivity)
        {
            this.esaphActivity = (EsaphActivity) context;
        }

        if(context instanceof EsaphDragableFinishListener)
        {
            this.esaphDragableFinishListener = (EsaphDragableFinishListener) context;
        }

        mIsOpen = false;
        dragViewId = EsaphDragableViewFragment.this.getId();

        if(attributeSet != null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EsaphDragableViewFragment);
            SHOW_PANEL_PERCENTAGE = typedArray.getFloat(R.styleable.EsaphDragableViewFragment_df_panelDownMaxPercentage,1);
            typedArray.recycle();
        }
    }

    public class DragHelperCallback extends ViewDragHelper.Callback
    {
        @Override
        public void onViewDragStateChanged(int state)
        {
            if (state == mDraggingState)
            { // no change
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE)
            {
                // the view stopped from moving.
                if (mDraggingBorder == 0)
                {
                    onStopDraggingToClosed();
                }
                else if (mDraggingBorder == mPanelHeightCollapsed)
                {
                    mIsOpen = true;
                    finishFragment();
                }
            }

            if (state == ViewDragHelper.STATE_DRAGGING)
            {
                onStartDragging();
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mDraggingBorder = top;
        }

        public int getViewVerticalDragRange(@NonNull View child)
        {
            return mPanelHeightCollapsed;
        }

        @Override
        public boolean tryCaptureView(@NonNull View view, int i)
        {
            return true;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy)
        {
            final int topBound = getPaddingTop();
            final int bottomBound = mPanelHeightCollapsed;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel)
        {
            final float rangeToCheck = mPanelHeightCollapsed;
            if (mDraggingBorder == 0)
            {
                mIsOpen = false;
                return;
            }

            if (mDraggingBorder == rangeToCheck)
            {
                mIsOpen = true;
                return;
            }
            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mPanelHeightCollapsed : 0;

            if(mDragHelper.settleCapturedViewAt(0, settleDestY))
            {
                ViewCompat.postInvalidateOnAnimation(EsaphDragableViewFragment.this);
            }
        }
    }

    @Override
    protected void onFinishInflate()
    {
        mViewTargeting = EsaphDragableViewFragment.this;
        mDragHelper = EsaphViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        mIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mPanelHeightCollapsed = (int) (h * SHOW_PANEL_PERCENTAGE);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void onStopDraggingToClosed()
    {
    }

    private void onStartDragging() {

    }

    private boolean isQueenTarget(MotionEvent event)
    {
        int[] queenLocation = new int[2];
        mViewTargeting.getLocationOnScreen(queenLocation);
        int upperLimit = queenLocation[1] + mViewTargeting.getMeasuredHeight();
        int lowerLimit = queenLocation[1];
        int y = (int) event.getRawY();
        return (y > lowerLimit && y < upperLimit);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (isQueenTarget(event)
                && mDragHelper.shouldInterceptTouchEvent(event)
                && !containsScrollAbleItems((int)event.getX(), (int)event.getY()))
        {
            return true;
        }
        else
        {
            return super.onInterceptTouchEvent(event);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if ((isQueenTarget(event) && !containsScrollAbleItems((int) event.getX(), (int) event.getY())) && isMoving())
        {
            mDragHelper.processTouchEvent(event);
            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll()
    { // needed for automatic settling.
        if (mDragHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private int oldTop;
    private int oldBottom;
    private int oldLeft;
    private int oldRight;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        // Reapply VDH offsets

        if (isMoving()) {
            EsaphDragableViewFragment.this.setTop(oldTop);
            EsaphDragableViewFragment.this.setBottom(oldBottom);
            EsaphDragableViewFragment.this.setLeft(oldLeft);
            EsaphDragableViewFragment.this.setRight(oldRight);
        }

        oldTop = top;
        oldBottom = bottom;
        oldLeft = left;
        oldRight = right;
    }

    public boolean isMoving()
    {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen()
    {
        return mIsOpen;
    }

    private EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragmentCurrentAttachedTo;
    private void finishFragment()
    {
        if(esaphActivity != null)
        {
            esaphActivity.getSupportFragmentManager().beginTransaction().remove(esaphGlobalCommunicationFragmentCurrentAttachedTo).commit();
        }

        if(esaphDragableFinishListener != null)
        {
            esaphDragableFinishListener.onDragableFragmentClosed();
        }
    }

    public void killFragmentAnimated(FragmentTransaction fragmentTransaction)
    {
        if(mDragHelper.smoothSlideViewTo(EsaphDragableViewFragment.this, 0, mPanelHeightCollapsed))
        {
            ViewCompat.postInvalidateOnAnimation(EsaphDragableViewFragment.this);
        }
        fragmentTransaction.commit();
    }

    private boolean containsScrollAbleItems(int x, int y)
    {
        if(getChildCount() > 0)
        {
            ViewGroup viewGroupCurrentFragment = (ViewGroup) getChildAt(getChildCount()-1);
            if(viewGroupCurrentFragment != null)
            {
                int count = viewGroupCurrentFragment.getChildCount();
                for (int i = count-1; i >= 0; i--)
                {
                    View v = viewGroupCurrentFragment.getChildAt(i);

                    if(!checkContaining(viewGroupCurrentFragment.getChildAt(i), x, y))
                    {
                        if(v instanceof ViewGroup)
                        {
                            int countViewGroup = ((ViewGroup) v).getChildCount();
                            for (int iG = countViewGroup-1; iG >= 0; iG--)
                            {
                                if(checkContaining(((ViewGroup) v).getChildAt(iG), x, y))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean checkContaining(View v, int x, int y)
    {
        if (v instanceof ListView)
        {
            if(mDragHelper.isViewUnder(v, x, y) && !listIsAtTop((ListView) v))
            {
                return true;
            }
        }
        else if(v instanceof RecyclerView)
        {
            RecyclerView recyclerView = (RecyclerView) v;
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if(mDragHelper.isViewUnder(v, x, y)
                    && linearLayoutManager != null
                    && linearLayoutManager.getChildCount() > 0
                    && linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0)
            {
                return true;
            }
        }
        else if(v instanceof EsaphDragableViewFragment)
        {
            if(mDragHelper.isViewUnder(v, x, y) && ((EsaphDragableViewFragment) v).getChildCount() > 0)
            {
                return true;
            }
        }
        else if(v instanceof EsaphDragingUpLayout)
        {
            if(mDragHelper.isViewUnder(v, x, y)
                    && BottomSheetBehavior.from(v).getState() == BottomSheetBehavior.STATE_DRAGGING
                    || BottomSheetBehavior.from(v).getState() == BottomSheetBehavior.STATE_SETTLING
                    || BottomSheetBehavior.from(v).getState() == BottomSheetBehavior.STATE_EXPANDED)
            {
                return true;
            }
        }

        return false;
    }

    private boolean listIsAtTop(ListView listView)
    {
        if(listView.getChildCount() == 0) return true;
        return listView.getChildAt(0).getTop() == 0;
    }


}
