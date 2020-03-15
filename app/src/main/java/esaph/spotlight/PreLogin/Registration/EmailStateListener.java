package esaph.spotlight.PreLogin.Registration;

public interface EmailStateListener
{
    void onEmailCanBeSent();
    void onEmailLimitReached();
}
