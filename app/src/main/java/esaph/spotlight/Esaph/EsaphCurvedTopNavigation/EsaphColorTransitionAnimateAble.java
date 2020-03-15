package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.graphics.LinearGradient;

public interface EsaphColorTransitionAnimateAble
{
    public LinearGradient onCalculateGradient(int position, float positionOffset);
    public void onInvalidate();
}
