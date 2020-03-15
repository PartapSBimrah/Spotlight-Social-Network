package esaph.spotlight.navigation.spotlight.GroupChats.ShowGroupMembers;

public class GroupMemberViewItem
{
    private String Username;
    private String Vorname;
    private short WatchingStatus;
    private boolean isAdmin;

    public GroupMemberViewItem(String Username, String Vorname, short watchingStatus, boolean isAdmin)
    {
        this.Username = Username;
        this.Vorname = Vorname;
        this.WatchingStatus = watchingStatus;
        this.isAdmin = isAdmin;
    }

    public boolean getisAdmin()
    {
        return isAdmin;
    }

    public String getUsername() {
        return Username;
    }

    public String getVorname() {
        return Vorname;
    }

    public short getWatchingStatus() {
        return WatchingStatus;
    }

    public void setFriendStatus(short newStatus)
    {
        this.WatchingStatus = newStatus;
    }
}
