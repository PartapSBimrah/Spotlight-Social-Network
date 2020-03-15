/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.UndeliveredMessageHandling;

public class UndeliveredChatSeenMessage
{
    private long _ID;
    private long Timeopened;
    private long UID_CHAT_PARTNER;

    public UndeliveredChatSeenMessage(long _ID, long UID_CHAT_PARTNER, long Timeopened)
    {
        this._ID = _ID;
        this.Timeopened = Timeopened;
        this.UID_CHAT_PARTNER = UID_CHAT_PARTNER;
    }

    public long get_ID() {
        return _ID;
    }

    public long getTimeopened() {
        return Timeopened;
    }

    public long getUID_CHAT_PARTNER() {
        return UID_CHAT_PARTNER;
    }
}
