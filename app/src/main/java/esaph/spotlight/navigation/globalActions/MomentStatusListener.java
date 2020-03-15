package esaph.spotlight.navigation.globalActions;

public interface MomentStatusListener
{
    void onMomentLeaved(String MIID, boolean complete);
    void onFailed(String MIID);
}
