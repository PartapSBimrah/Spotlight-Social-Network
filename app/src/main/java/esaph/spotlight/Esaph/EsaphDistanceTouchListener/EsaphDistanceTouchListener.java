package esaph.spotlight.Esaph.EsaphDistanceTouchListener;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;

public class EsaphDistanceTouchListener implements View.OnTouchListener
{
    private static final float MAX_DISTACE = DisplayUtils.dp2px(200f);
    private float y_start = 0;


    public interface DistanceTochInterface
    {     //Returns a value between 0.0..1.0
        void onTouchDown();
        void onMoving(float distance);
        void onTouchUp();
    }

    private DistanceTochInterface distanceTochInterface;

    public EsaphDistanceTouchListener setDistanceTochInterface(DistanceTochInterface distanceTochInterface)
    {
        this.distanceTochInterface = distanceTochInterface;
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int ACTION = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();

        switch (ACTION)
        {
            case MotionEvent.ACTION_DOWN:
                y_start = y;
                if(distanceTochInterface != null)
                {
                    distanceTochInterface.onTouchDown();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                double distance = Math.sqrt((y-y_start) * (y-y_start));
                if(distanceTochInterface != null)
                {
                    distanceTochInterface.onMoving((float) distance / EsaphDistanceTouchListener.MAX_DISTACE);
                }
                return true;

            case MotionEvent.ACTION_UP:
                if(distanceTochInterface != null)
                {
                    distanceTochInterface.onTouchUp();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if(distanceTochInterface != null)
                {
                    distanceTochInterface.onTouchUp();
                }
                break;
        }
        return false;
    }
}
