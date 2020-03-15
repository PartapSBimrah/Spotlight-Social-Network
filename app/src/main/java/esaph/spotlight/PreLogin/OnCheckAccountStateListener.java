package esaph.spotlight.PreLogin;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface OnCheckAccountStateListener
{
    void onAccountSuccess(GoogleSignInAccount googleSignInAccount);
    void onAccountFailed(GoogleSignInAccount googleSignInAccount);
}
