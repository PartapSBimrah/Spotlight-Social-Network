/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph;

public class EsaphLockable
{
    private boolean locked = false;

    public synchronized boolean isLocked()
    {
        return locked;
    }

    public synchronized void setLocked(boolean locked) {
        this.locked = locked;
    }
}
