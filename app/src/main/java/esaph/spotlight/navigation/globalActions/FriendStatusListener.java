package esaph.spotlight.navigation.globalActions;

public interface FriendStatusListener
{
    void onStatusReceived(long CHAT_PARTNER_UID, short Status);
    void onStatusFailed(long CHAT_PARTNER_UID);
}
