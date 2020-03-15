package esaph.spotlight.einstellungen;

public class SpotLightMaxStorageSize
{
    public static final int SIZE_UNLIMITED = 1;
    public static final int SIZE_50_MB = 2;
    public static final int SIZE_150MB = 3;
    public static final int SIZE_300MB = 4;
    public static final int SIZE_500MB = 5;
    public static final int SIZE_3GB = 6;


    public static long getAsBytes(int SIZE_LIMIT)
    {
        switch (SIZE_LIMIT)
        {
            case SIZE_50_MB:
                return 52428800L;

            case SIZE_150MB:
                return 157286400L;

            case SIZE_300MB:
                return 314572800L;

            case SIZE_500MB:
                return 524288000L;

            case SIZE_3GB:
                return 3221225472L;

                default:
                    return 0;
        }
    }
}
