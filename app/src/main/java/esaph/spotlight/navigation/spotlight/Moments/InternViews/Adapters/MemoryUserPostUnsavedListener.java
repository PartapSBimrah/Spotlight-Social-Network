package esaph.spotlight.navigation.spotlight.Moments.InternViews.Adapters;

public interface MemoryUserPostUnsavedListener
{
    void onUsersMomentUnsaved(String Username);
    void onUsersMomentsFailedToUsave(String Username);
    void onNotAvaiable(String Username);
}

