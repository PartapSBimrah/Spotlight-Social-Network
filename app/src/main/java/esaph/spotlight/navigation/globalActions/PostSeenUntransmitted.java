/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class PostSeenUntransmitted
{
    private long _ID;
    private ConversationMessage conversationMessage;

    public PostSeenUntransmitted(long _ID, ConversationMessage conversationMessage) {
        this._ID = _ID;
        this.conversationMessage = conversationMessage;
    }

    public long get_ID() {
        return _ID;
    }

    public ConversationMessage getConversationMessage() {
        return conversationMessage;
    }
}
