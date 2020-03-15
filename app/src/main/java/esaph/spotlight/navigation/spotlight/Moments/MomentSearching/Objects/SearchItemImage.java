package esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects;

public class SearchItemImage extends SearchItemMainMoments
{
    private String mPID;
    private String reasonFound;
    private String mUsername;

    public SearchItemImage(String mPID, String reasonFound, String mUsername)
    {
        super(TYPE_IMAGE);
        this.mPID = mPID;
        this.reasonFound = reasonFound;
        this.mUsername = mUsername;
    }


    public String getmPID() {
        return mPID;
    }

    public String getReasonFound() {
        return reasonFound;
    }

    public String getmUsername() {
        return mUsername;
    }
}
