package esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects;

public class SearchItemMainMoments
{
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int TYPE;

    public SearchItemMainMoments(int TYPE) {
        this.TYPE = TYPE;
    }

    public int getTYPE() {
        return TYPE;
    }
}
