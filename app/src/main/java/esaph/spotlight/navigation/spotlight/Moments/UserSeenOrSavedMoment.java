package esaph.spotlight.navigation.spotlight.Moments;

public class UserSeenOrSavedMoment
{
    private long MESSAGE_ID_SERVER;
    private long KEY_CHAT;
    private String Username;
    private boolean PartnerSaved;
    private int MessageStatus;
    private String PID;
    private long ID_POST;
    private long ID_SAVED;

    public UserSeenOrSavedMoment(long KEY_CHAT, String Username, int MessageStatus, boolean PartnerSaved, long MESSAGE_ID_SERVER,
                                 String PID, long ID_POST, long ID_SAVED)
    {
        this.KEY_CHAT = KEY_CHAT;
        this.Username = Username;
        this.MessageStatus = MessageStatus;
        this.PartnerSaved = PartnerSaved;
        this.MESSAGE_ID_SERVER = MESSAGE_ID_SERVER;
        this.PID = PID;
        this.ID_POST = ID_POST;
        this.ID_SAVED = ID_SAVED;
    }

    public long getMESSAGE_ID_SERVER() {
        return MESSAGE_ID_SERVER;
    }

    public long getID_SAVED() {
        return ID_SAVED;
    }

    public long getID_POST() {
        return ID_POST;
    }

    public String getPID() {
        return PID;
    }

    public long getKEY_CHAT() {
        return KEY_CHAT;
    }

    public String getUsername() {
        return Username;
    }

    public int getMessageStatus() {
        return MessageStatus;
    }

    public void setPartnerSaved(boolean partnerSaved) {
        PartnerSaved = partnerSaved;
    }

    public boolean didPartnerSaved()
    {
        return this.PartnerSaved;
    }
}
