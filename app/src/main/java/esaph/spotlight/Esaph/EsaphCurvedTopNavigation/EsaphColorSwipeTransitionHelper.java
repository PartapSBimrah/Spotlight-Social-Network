package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.graphics.Color;
import android.graphics.Paint;

public class EsaphColorSwipeTransitionHelper
{
    private static class EsaphMainSwipeColors
    {
        private static final int[] colorsMoments = {
                0xFFFF5B5E,
                0xFFFF5B5E,
                0xFFFF9F55,
                0xFFFF9F55};

        private static final int[] colorsCamera = {
                0x00000000,
                0x00000000,
                0x00000000,
                0x00000000,};

        private static final int[] colorsCameraMiddleTab = {
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFFFFFFFF};

        private static final int[] colorsChat = {
                0xFF7BB4FF,
                0xFF7BB4FF,
                0xFFB1AEF9,
                0xFFB1AEF9};

        private static final int[] colorsPublic = {
                0xFF13547A,
                0xFF80D0C7,
                0xFF80D0C7,
                0xFF80D0C7};
    }

    private static int[][] applyColorFilterPositionZero_CACHED = new int[100][4];
    private static int[][] applyColorFilterPositionOne_CACHED = new int[100][4];
    private static int[][] applyColorFilterPositionOneMiddleTabIcon_CACHED = new int[100][4];
    private static int[][] applyColorFilterPositionTwo_CACHED = new int[100][4];

    public static int[] applyColorFilterPositionZero(Paint paint, float positionOffset)
    {
        float negativOffset = 1 - positionOffset;
        paint.setAlpha((int) (255*negativOffset));
        int ACCESKEY = (int)(100*positionOffset);
        if(ACCESKEY >= 0 && ACCESKEY > 0 && ACCESKEY < applyColorFilterPositionZero_CACHED.length)
        {
            int[] a = applyColorFilterPositionZero_CACHED[ACCESKEY];
            if(a[0] != 0 && a[1] != 0)
            {
                return a;
            }
        }

        int[] colorArray = new int[] {0,0,0,0};

        if(positionOffset > 0.50f) //scroll left side in
        {
            for(int counter = 0; counter < 4; counter++)
            {
                int colorFrom = EsaphMainSwipeColors.colorsCamera[counter];
                int colorTo = EsaphMainSwipeColors.colorsMoments[counter];
                colorArray[counter] = interpolateColor(colorFrom, colorTo, negativOffset);
            }
        }
        else //Scrolling right side in.
        {
            for(int counter = 0; counter < 4; counter++)
            {
                int colorFrom = EsaphMainSwipeColors.colorsMoments[counter];
                int colorTo = EsaphMainSwipeColors.colorsCamera[counter];
                colorArray[counter] = interpolateColor(colorFrom, colorTo, positionOffset);
            }
        }

        if(ACCESKEY >= 0 && ACCESKEY > 0 && ACCESKEY < applyColorFilterPositionZero_CACHED.length)
        {
            applyColorFilterPositionZero_CACHED[ACCESKEY] = colorArray;
        }
        return colorArray;
    }


    public static int[]applyColorFilterPositionOne(Paint paint, float positionOffset)
    {
        paint.setAlpha((int) (255*positionOffset));
        int ACCESKEY = (int)(100*positionOffset);
        if(ACCESKEY >= 0 && ACCESKEY < applyColorFilterPositionOne_CACHED.length)
        {
            int[] a = applyColorFilterPositionOne_CACHED[ACCESKEY];
            if(a[0] != 0 && a[1] != 0)
            {
                return a;
            }

            float negativOffset = 1 - positionOffset;

            int[] colorArray = new int[] {0,0,0,0};

            if(positionOffset > 0.50f) //scroll left side in
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsChat[counter];
                    int colorTo = EsaphMainSwipeColors.colorsCamera[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, negativOffset);
                }
            }
            else //Scrolling right side in.
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsCamera[counter];
                    int colorTo = EsaphMainSwipeColors.colorsChat[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, positionOffset);
                }
            }

            applyColorFilterPositionOne_CACHED[ACCESKEY] = colorArray;
            return colorArray;
        }

        return new int[]{0,0};
    }

    public static int[]applyColorFilterPositionOneMiddleTabIcon(Paint paint, float positionOffset)
    {
        paint.setAlpha((int) (255*positionOffset));
        int ACCESKEY = (int)(100*positionOffset);
        if(ACCESKEY >= 0 && ACCESKEY < applyColorFilterPositionOneMiddleTabIcon_CACHED.length)
        {
            int[] a = applyColorFilterPositionOneMiddleTabIcon_CACHED[ACCESKEY];
            if(a[0] != 0 && a[1] != 0)
            {
                return a;
            }

            float negativOffset = 1 - positionOffset;
            int[] colorArray = new int[] {0,0,0,0};

            if(positionOffset > 0.50f) //scroll left side in
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsChat[counter];
                    int colorTo = EsaphMainSwipeColors.colorsCameraMiddleTab[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, negativOffset);
                }
            }
            else //Scrolling right side in.
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsCameraMiddleTab[counter];
                    int colorTo = EsaphMainSwipeColors.colorsChat[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, positionOffset);
                }
            }

            applyColorFilterPositionOneMiddleTabIcon_CACHED[ACCESKEY] = colorArray;
            return colorArray;
        }

        return new int[]{0,0};
    }


    public static int[] applyColorFilterPositionTwo(Paint paint, float positionOffset)
    {
        float negativOffset = 1 - positionOffset;
        paint.setAlpha((int) (255*negativOffset));
        int ACCESKEY = (int)(100*positionOffset);
        if(ACCESKEY >= 0 && ACCESKEY < applyColorFilterPositionTwo_CACHED.length)
        {
            int[] a = applyColorFilterPositionTwo_CACHED[ACCESKEY];
            if(a[0] != 0 && a[1] != 0)
            {
                return a;
            }

            int[] colorArray = new int[] {0,0,0,0};

            if(positionOffset > 0.50f) //scroll left side in
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsPublic[counter];
                    int colorTo = EsaphMainSwipeColors.colorsChat[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, negativOffset);
                }
            }
            else //Scrolling right side in.
            {
                for(int counter = 0; counter < 4; counter++)
                {
                    int colorFrom = EsaphMainSwipeColors.colorsChat[counter];
                    int colorTo = EsaphMainSwipeColors.colorsPublic[counter];
                    colorArray[counter] = interpolateColor(colorFrom, colorTo, positionOffset);
                }
            }

            applyColorFilterPositionTwo_CACHED[ACCESKEY] = colorArray;
            return colorArray;
        }

        return new int[]{0,0};
    }


    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    private static int interpolateColor(int a, int b, float proportion)
    {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }
}
