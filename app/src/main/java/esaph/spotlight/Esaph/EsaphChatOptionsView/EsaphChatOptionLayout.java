package esaph.spotlight.Esaph.EsaphChatOptionsView;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;

public class EsaphChatOptionLayout extends FrameLayout
{
    public EsaphChatOptionLayout(@NonNull Context context)
    {
        super(context);
        currentViewState = ViewState.COLLAPSED;
    }

    public EsaphChatOptionLayout(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        currentViewState = ViewState.COLLAPSED;
    }

    public EsaphChatOptionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        currentViewState = ViewState.COLLAPSED;
    }

    @RequiresApi(21)
    public EsaphChatOptionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        currentViewState = ViewState.COLLAPSED;
    }

    private ViewState currentViewState;

    private enum ViewState
    {
        EXPANDED, COLLAPSED
    }

    private static final String ONE_SHOT_TAG_SELECTED = "SE";
    private static final String ONE_SHOT_TAG_UNSELECTED = "UN";

    private View triggerView;
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

    public void setTriggerView(View triggerView)
    {
        if(this.triggerView != null)
        {
            this.triggerView.setOnTouchListener(null);
        }

        this.triggerView = triggerView;

        if(triggerView != null)
        {
            this.triggerView.setOnTouchListener(new OnTouchListener()
            {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(final View v, MotionEvent event)
                {
                    return dispatchTouchEvent(event);
                }
            });
        }

        invalidate();
    }

    @Override
    public void addView(View child)
    {
        super.addView(child);
        initView(child);
    }

    private List<View> esaphChatOptionInterfaceList = new ArrayList<>();
    private void initView(View viewAdded)
    {
        ViewGroup viewGroupCurrentFragment = (ViewGroup) viewAdded;
        if(viewGroupCurrentFragment != null)
        {
            int count = viewGroupCurrentFragment.getChildCount();
            for (int i = count-1; i >= 0; i--)
            {
                View v = viewGroupCurrentFragment.getChildAt(i);
                if(v instanceof ViewGroup)
                {
                    int countViewGroup = ((ViewGroup) v).getChildCount();
                    for (int iG = countViewGroup-1; iG >= 0; iG--)
                    {
                        View viewInGroup = ((ViewGroup) v).getChildAt(iG);
                        if(viewInGroup instanceof EsaphChatOptionInterface)
                        {
                            esaphChatOptionInterfaceList.add(viewInGroup);
                        }
                    }
                }
                else if(v instanceof EsaphChatOptionInterface)
                {
                    esaphChatOptionInterfaceList.add(v);
                }
            }
        }
    }


    private boolean checkViewsHitBox(int x, int y, int ACTION)
    {
        if(getChildCount() > 0)
        {
            for(View v: esaphChatOptionInterfaceList)
            {
                if(v != null)
                {
                    if(isViewUnder(v, x, y))
                    {
                        if(v instanceof EsaphChatOptionInterface)
                        {
                            if(ACTION == MotionEvent.ACTION_UP)
                            {
                                ((EsaphChatOptionInterface)v).onViewInHitBoxSelected();
                            }
                            else if(ACTION == MotionEvent.ACTION_MOVE)
                            {
                                if(v.getTag() == null || !v.getTag().equals(EsaphChatOptionLayout.ONE_SHOT_TAG_SELECTED))
                                {

                                    v.setTag(EsaphChatOptionLayout.ONE_SHOT_TAG_SELECTED);
                                    ((EsaphChatOptionInterface)v).onViewInHitBox();
                                }
                            }
                        }
                    }
                    else
                    {
                        if(v instanceof EsaphChatOptionInterface)
                        {
                            if(v.getTag() == null || !v.getTag().equals(EsaphChatOptionLayout.ONE_SHOT_TAG_UNSELECTED))
                            {
                                v.setTag(EsaphChatOptionLayout.ONE_SHOT_TAG_UNSELECTED);
                                ((EsaphChatOptionInterface)v).onViewOutOfHitBox();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        int ACTION = ev.getAction();
        float x = ev.getRawX();
        float y = ev.getRawY();

        switch (ACTION)
        {
            case MotionEvent.ACTION_DOWN:
                if(isViewUnder(this, (int) x, (int) y));
            {
                if(currentViewState == ViewState.EXPANDED)
                {
                    if(onViewTriggerViewSelected != null)
                    {
                        return true;
                    }
                }
                else
                {
                    if(isViewUnder(triggerView, (int) x, (int) y))
                    {
                        return true;
                    }
                }
            }

            case MotionEvent.ACTION_MOVE:
                if(currentViewState == ViewState.EXPANDED)
                {
                    System.out.println("OPTIONLAYOUT: INTERCEPTING");
                    return true;
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
        int ACTION = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();

        switch (ACTION)
        {
            case MotionEvent.ACTION_DOWN:
                if(isViewUnder(this, (int) x, (int) y));
            {
                if(currentViewState == ViewState.EXPANDED)
                {
                    return true;
                }
                else
                {
                    if(isViewUnder(triggerView, (int) x, (int) y))
                    {
                        currentViewState = ViewState.EXPANDED;
                        onViewTriggerViewSelected.onTriggerSelected(triggerView);
                        return true;
                    }
                }
            }

            case MotionEvent.ACTION_MOVE:
                checkViewsHitBox((int) x, (int) y, ACTION);
                break;

            case MotionEvent.ACTION_UP:

                if(triggerView instanceof EsaphCircleImageView)
                {
                    ((EsaphCircleImageView) triggerView).setDisableCircularTransformation(true);
                    ((EsaphCircleImageView) triggerView).setEsaphShaderBackground(null);
                }

                currentViewState = ViewState.COLLAPSED;
                checkViewsHitBox((int) x, (int) y, ACTION);
                if(onViewTriggerViewSelected != null)
                {
                    onViewTriggerViewSelected.onTriggerReleased(triggerView);
                }
                break;

                case MotionEvent.ACTION_CANCEL:

                    if(triggerView instanceof EsaphCircleImageView)
                    {
                        ((EsaphCircleImageView) triggerView).setDisableCircularTransformation(true);
                        ((EsaphCircleImageView) triggerView).setEsaphShaderBackground(null);
                    }

                    currentViewState = ViewState.COLLAPSED;
                    checkViewsHitBox((int) x, (int) y, ACTION);
                    if(onViewTriggerViewSelected != null)
                    {
                        onViewTriggerViewSelected.onTriggerReleased(triggerView);
                    }

                    break;
        }

        return super.onTouchEvent(event);
    }

    private boolean isViewUnder(@Nullable View view, int rx, int ry) {
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
