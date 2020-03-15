package esaph.spotlight.spots.SpotMaker.Models;

import android.text.Layout;
import android.view.Gravity;

public class SpotTextAlignment
{
    public static final short ALIGNMENT_START = 0;
    public static final short ALIGNMENT_CENTER = 1;
    public static final short ALIGNMENT_END = 2;

    public static Layout.Alignment getAlignmentForApiVersion(short textAlignment)
    {
        switch (textAlignment)
        {
            case SpotTextAlignment.ALIGNMENT_START:
                return Layout.Alignment.ALIGN_NORMAL;

            case SpotTextAlignment.ALIGNMENT_CENTER:
                return Layout.Alignment.ALIGN_CENTER;

            case SpotTextAlignment.ALIGNMENT_END:
                return Layout.Alignment.ALIGN_OPPOSITE;
        }

        return Layout.Alignment.ALIGN_CENTER;
    }

    public static int getGravityForApiVersion(short textAlignment)
    {
        switch (textAlignment)
        {
            case SpotTextAlignment.ALIGNMENT_START:
                return Gravity.START;

            case SpotTextAlignment.ALIGNMENT_CENTER:
                return Gravity.CENTER;

            case SpotTextAlignment.ALIGNMENT_END:
                return Gravity.END;
        }

        return Gravity.CENTER;
    }
}
