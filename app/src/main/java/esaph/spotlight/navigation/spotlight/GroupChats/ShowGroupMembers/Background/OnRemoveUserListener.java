package esaph.spotlight.navigation.spotlight.GroupChats.ShowGroupMembers.Background;

public interface OnRemoveUserListener
{
    void onUserRemoved(String Username);
    void onUserFailed(String Username);
}
