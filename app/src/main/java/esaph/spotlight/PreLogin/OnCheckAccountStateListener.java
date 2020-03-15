/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface OnCheckAccountStateListener
{
    void onAccountSuccess(GoogleSignInAccount googleSignInAccount);
    void onAccountFailed(GoogleSignInAccount googleSignInAccount);
}
