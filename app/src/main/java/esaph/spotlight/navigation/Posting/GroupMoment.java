package esaph.spotlight.navigation.Posting;

public class GroupMoment
{
    private String GID;
    private String MIID;
    private String Title;
    private String LASTPOST_PID;
    private String type;
    private String groupName;
    private long PostTime;
    private boolean isSelected = false;

    public GroupMoment(String GID, String MIID, String Title, String type, String LASTPOST_PID, String groupName, long PostTime)
    {
        this.GID = GID;
        this.MIID = MIID;
        this.Title = Title;
        this.type = type;
        this.LASTPOST_PID = LASTPOST_PID;
        this.groupName = groupName;
        this.PostTime = PostTime;
    }

    public String getGID()
    {
        return GID;
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }

    public boolean isSelectedHurensohn()
    {
        return isSelected;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String getLASTPOST_PID() {
        return LASTPOST_PID;
    }

    public String getType()
    {
        return type;
    }

    public String getMIID() {
        return MIID;
    }

    public String getTitle() {
        return Title;
    }

    public long getPostTime() {
        return PostTime;
    }
}
