/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight;

import android.net.Uri;

public class PhoneContact
{
    private boolean canAdd;
    private String Name;
    private Uri uriThumpnail;

    public PhoneContact(String Name, boolean canAdd, Uri uriThumpnail)
    {
        this.Name = Name;
        this.canAdd = canAdd;
        this.uriThumpnail = uriThumpnail;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public Uri getUriThumpnail() {
        return uriThumpnail;
    }

    public String getName() {
        return Name;
    }
}
