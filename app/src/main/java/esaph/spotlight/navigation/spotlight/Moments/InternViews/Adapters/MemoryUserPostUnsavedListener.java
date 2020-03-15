/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.InternViews.Adapters;

public interface MemoryUserPostUnsavedListener
{
    void onUsersMomentUnsaved(String Username);
    void onUsersMomentsFailedToUsave(String Username);
    void onNotAvaiable(String Username);
}

