package esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects;

public class SearchItemVideo extends SearchItemMainMoments
{
    private String mPID;
    private String reasonFound;
    private String mUsername;

    public SearchItemVideo(String mPID, String reasonFound, String mUsername)
    {
        super(TYPE_VIDEO);
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
