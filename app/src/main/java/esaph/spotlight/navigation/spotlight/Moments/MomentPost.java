package esaph.spotlight.navigation.spotlight.Moments;

public class MomentPost
{
    private String Username;
    private String MomentName;
    private String PID;
    private String MIID;
    private String FORMAT;
    private String Beschreibung;
    private int postStatus;
    private long timeAgo;
    private String infoMessage;

    public MomentPost(String Username, String PID, String MIID, String FORMAT, String Beschreibung, int postStatus, long timeAgo, String infoMessage)
    {
        this.Username = Username;
        this.PID = PID;
        this.MIID = MIID;
        this.FORMAT = FORMAT;
        this.Beschreibung = Beschreibung;
        this.postStatus = postStatus;
        this.timeAgo = timeAgo;
        this.infoMessage = infoMessage;
    }

    public long getTime()
    {
        return timeAgo;
    }

    public int getPostStatus()
    {
        return postStatus;
    }

    public String getFORMAT() {
        return FORMAT;
    }

    public String getUsername()
    {
        return Username;
    }

    public String getPID()
    {
        return PID;
    }

    public String getMIID()
    {
        return MIID;
    }

    public String getBeschreibung()
    {
        return Beschreibung;
    }

    public String getInfoMessage()
    {
        return infoMessage;
    }

    public void setPostStatus(int status)
    {
        this.postStatus = status;
    }
}
