package esaph.spotlight.navigation.globalActions;

public class SocialFriendNegotiation
{
    private long UID;
    private String PBPID;
    private String Username;
    private String Vorname;
    private String Region;
    private short AnfragenStatus;

    public SocialFriendNegotiation(long UID, String Username, String Vorname, short AnfragenStatus,
                                   String Region)
    {
        this.UID = UID;
        this.PBPID = PBPID;
        this.Region = Region;
        this.Username = Username;
        this.Vorname = Vorname;
        this.AnfragenStatus = AnfragenStatus;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public void setPBPID(String PBPID) {
        this.PBPID = PBPID;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setVorname(String vorname) {
        Vorname = vorname;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getRegion() {
        return Region;
    }

    public String getVorname() {
        return Vorname;
    }

    public String getPBPID() {
        return PBPID;
    }

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return Username;
    }

    public short getAnfragenStatus()
    {
        return AnfragenStatus;
    }

    public void setAnfragenStatus(short AnfragenStatus)
    {
        this.AnfragenStatus = AnfragenStatus;
    }
}
