package esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils;

import esaph.spotlight.R;

public class EsaphGradientChooser
{
    public static int obtainGradient(int pos)
    {
        if(pos % 2 == 0)
        {
            return R.drawable.gradient_orange_pink;
        }
        else if(pos % 3 == 0)
        {
            return R.drawable.gradient_blue_green;
        }
        else if(pos % 4 == 0)
        {
            return R.drawable.gradient_blue_purple;
        }

        return R.drawable.gradient_yellow_green;
    }
}
