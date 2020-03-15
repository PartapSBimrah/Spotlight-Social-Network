package esaph.spotlight.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class EsaphLockAbleViewPager extends ViewPager
{
    private boolean allowSwipping = true;

    public EsaphLockAbleViewPager(@NonNull Context context)
    {
        super(context);
    }

    public EsaphLockAbleViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setSwipeAllowed(boolean allowSwipping)
    {
        this.allowSwipping = allowSwipping;
        if(allowSwipping) reactivate();
    }

    public void paralyse()
    {
        setFocusable(false);
        setEnabled(false);
        setFocusableInTouchMode(false);
    }

    private void reactivate()
    {
        setFocusable(true);
        setEnabled(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(allowSwipping)
        {
            return super.dispatchTouchEvent(ev);
        }
        else
        {
            super.dispatchTouchEvent(ev);
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        // Never allow swiping to switch between pages

        if(allowSwipping)
        {
            return super.onInterceptTouchEvent(event);
        }
        else
        {
            super.onInterceptTouchEvent(event);
            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {
            if(allowSwipping)
            {
                return super.onTouchEvent(event);
            }
            else
            {
                super.onTouchEvent(event);
                return false;
            }
        }
        catch (Exception ec)
        {
            return false;
        }
    }

    private List<OnPageChangeListener> listAdded = new ArrayList<OnPageChangeListener>();
    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener)
    {
        if(!listAdded.contains(listener))
        {
            super.addOnPageChangeListener(listener);
        }
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener)
    {
        int pos = 0;
        for(OnPageChangeListener onPageChangeListener : listAdded) //Not realldy nessecerry, beause adding at top a listener, preventing adding the same.
        {
            pos++;
            if(onPageChangeListener == listener)
            {
                listAdded.remove(pos);
            }
        }
        super.removeOnPageChangeListener(listener);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l)
    {
        System.out.println("Setting on touch listener: " + l);
        super.setOnTouchListener(l);
    }
}
