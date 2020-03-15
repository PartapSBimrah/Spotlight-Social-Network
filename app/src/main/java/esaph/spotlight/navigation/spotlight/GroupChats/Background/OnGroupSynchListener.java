package esaph.spotlight.navigation.spotlight.GroupChats.Background;

public interface OnGroupSynchListener
{
    void onGroupSynchronized(String MIID);
    void onFailedToSynchGroup(String MIID);
}
