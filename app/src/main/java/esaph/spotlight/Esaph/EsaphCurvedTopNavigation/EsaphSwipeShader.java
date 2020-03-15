package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import androidx.viewpager.widget.ViewPager;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;

public class EsaphSwipeShader extends EsaphShader
{
    private LinearGradient mLinearGradient;
    private int view_width;
    private int view_height;
    private final ViewPager viewPager;
    private int alphaStupidBackground;

    public EsaphSwipeShader(int view_width, int view_height, ViewPager viewPager)
    {
        this.view_width = view_width;
        this.view_height = view_height;
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this.onPageChangeListener);
    }

    public void onSizeChange(int view_width, int view_height)
    {
        this.view_width = view_width;
        this.view_height = view_height;
    }

    private void onCalculateBackgroundAlpha(int position, float positionOffset)
    {
        System.out.println("ALPHAB_POS:" + position);
        System.out.println("ALPHAB_OFFSET:" + positionOffset);
        float negativ = 1-positionOffset;

        if(position == 0)
        {
            alphaStupidBackground = Color.argb((int)(255*negativ),255,255,255);
        }
        else if(position == 1)
        {
            alphaStupidBackground = Color.argb((int)(255*positionOffset),255,255,255);
        }
        else if(position == 2)
        {
            alphaStupidBackground = Color.argb((int)(255*negativ),255,255,255);
        }
    }

    @Override
    public void onDrawShader(Paint paint)
    {
        paint.setShader(mLinearGradient);
    }

    @Override
    public void onLayout(int height, int width) {

    }

    public int getAlphaStupidBackground()
    {
        return alphaStupidBackground;
    }

    public final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            /*
            onCalculateBackgroundAlpha(position, positionOffset);
            if(esaphSwipeShaderCommunication != null)
            {
                mLinearGradient = esaphSwipeShaderCommunication.onCalculateGradient(position, positionOffset);
                esaphSwipeShaderCommunication.onInvalidate();
            }*/

        }

        @Override
        public void onPageSelected(int position)
        {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private EsaphColorTransitionAnimateAble esaphSwipeShaderCommunication;

    public void setEsaphSwipeShaderCommunication(EsaphColorTransitionAnimateAble esaphSwipeShaderCommunication) {
        this.esaphSwipeShaderCommunication = esaphSwipeShaderCommunication;
    }
}
