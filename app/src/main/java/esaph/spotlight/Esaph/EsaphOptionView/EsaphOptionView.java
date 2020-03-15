package esaph.spotlight.Esaph.EsaphOptionView;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class EsaphOptionView extends RelativeLayout
{
    private static final float TRIGGER_DISTANCE = 200f;

    public EsaphOptionView(Context context)
    {
        super(context);
        init();
    }

    public EsaphOptionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EsaphOptionView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(21)
    public EsaphOptionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
    }

    private void init()
    {
        currentViewState = ViewState.COLLAPSED;
        setTriggerView(EsaphOptionView.this);
    }

    private ViewState currentViewState;

    private enum ViewState
    {
        EXPANDED, COLLAPSED
    }

    private ViewTriggerSelectedInterface onViewTriggerViewSelected;

    public interface ViewTriggerSelectedInterface
    {
        void onTriggerSelected(View triggerView);
        void onTriggerReleased(View triggerView);
    }

    public void setOnViewTriggerInterface(ViewTriggerSelectedInterface onViewTriggerViewSelected)
    {
        this.onViewTriggerViewSelected = onViewTriggerViewSelected;
    }


    private OptionMoveListener optionMoveListener;
    public void setOptionMoveListener(OptionMoveListener optionMoveListener)
    {
        this.optionMoveListener = optionMoveListener;
    }

    public interface OptionMoveListener
    {
        //Returns a value between 0.0..1.0
        void onMoving(float distance);
    }

    @Override
    public void addView(View child)
    {
        super.addView(child);
        initView(child);
    }

    private View viewShowLayout;
    public void setShowLayout(View view)
    {
        this.viewShowLayout = view;
    }

    public View getViewShowLayout() {
        return viewShowLayout;
    }

    private View triggerView;
    public void setTriggerView(View triggerView)
    {
        this.triggerView = triggerView;
        invalidate();
    }

    private boolean enable = true;
    public void setEnabledOptionView(boolean enable)
    {
        this.enable = enable;
    }

    private List<View> esaphChatOptionInterfaceList = new ArrayList<>();
    private void initView(View viewAdded)
    {
        esaphChatOptionInterfaceList.clear();
        ViewGroup viewGroupCurrentFragment = (ViewGroup) viewAdded;
        if(viewGroupCurrentFragment != null)
        {
            for (int i=0;i<viewGroupCurrentFragment.getChildCount();i++)
            {
                View v1=viewGroupCurrentFragment.getChildAt(i);
                if (v1 instanceof ViewGroup) initView(v1);


                esaphChatOptionInterfaceList.add(v1);
            }
        }
    }

    private float y_start = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) //waiting until the distance is reached, then it passed all events to onTouchEvent. And it listenes for the Trigger.
    {
        if(!enable)
        {
            return super.onInterceptTouchEvent(ev);
        }

        int ACTION = ev.getAction();
        float x = ev.getRawX();
        float y = ev.getRawY();

        switch (ACTION)
        {
            case MotionEvent.ACTION_DOWN:
                if(isViewUnder(triggerView, (int) x, (int) y));
            {
                y_start = y;
            }
            break;

            case MotionEvent.ACTION_MOVE:

                double distance = Math.sqrt((y-y_start) * (y-y_start));
                if(optionMoveListener != null)
                {
                    optionMoveListener.onMoving((float) distance / TRIGGER_DISTANCE);
                }

                System.out.println("DISTANCE SCROLLED: " + distance);

                if(currentViewState == ViewState.EXPANDED)
                {
                    return false;
                }
                else
                {
                    if(isViewUnder(triggerView, (int) x, (int) y) && distance >= TRIGGER_DISTANCE)
                    {
                        currentViewState = ViewState.EXPANDED;

                        addView(viewShowLayout);
                        onViewTriggerViewSelected.onTriggerSelected(triggerView);
                        return true;
                    }
                }

                break;

        }
        System.out.println("OPTIONLAYOUT: NOT TOUCHED TRIGGER");

        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(!enable)
        {
            return super.onTouchEvent(event);
        }

        int ACTION = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();

        switch (ACTION)
        {
            case MotionEvent.ACTION_DOWN:
                if(currentViewState == ViewState.EXPANDED)
                {
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(!isViewUnder(viewShowLayout, (int) x, (int) y))
                {
                    currentViewState = ViewState.COLLAPSED;
                    removeView(viewShowLayout);
                    if(onViewTriggerViewSelected != null)
                    {
                        onViewTriggerViewSelected.onTriggerReleased(triggerView);
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:

                if(!isViewUnder(viewShowLayout, (int) x, (int) y))
                {
                    currentViewState = ViewState.COLLAPSED;
                    removeView(viewShowLayout);
                    if(onViewTriggerViewSelected != null)
                    {
                        onViewTriggerViewSelected.onTriggerReleased(triggerView);
                    }
                }

                break;
        }

        return super.onTouchEvent(event);
    }


    private boolean isViewUnder(@Nullable View view, int rx, int ry)
    {
        if(view == null)
            return false;

        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1] - (int) view.getTranslationY();
        int w = view.getWidth();
        int h = view.getHeight();

        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return false;
        }
        return true;
    }
}
