package esaph.spotlight.Esaph.EsaphDotTabs;

import android.graphics.drawable.GradientDrawable;

public class DotsGradientDrawable extends GradientDrawable {

    private int currentColor;

    @Override
    public void setColor(int argb) {
        super.setColor(argb);

        currentColor = argb;
    }

    public int getCurrentColor() {
        return currentColor;
    }
}