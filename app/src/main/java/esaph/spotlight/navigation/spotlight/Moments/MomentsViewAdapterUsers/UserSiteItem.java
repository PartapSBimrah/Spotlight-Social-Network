/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers;

import java.io.Serializable;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class UserSiteItem implements Serializable
{
    private String Username;
    private long UID;
    private ConversationMessage conversationMessageLast;

    public UserSiteItem(long UID, String username, ConversationMessage conversationMessageLast) {
        this.UID = UID;
        this.Username = username;
        this.conversationMessageLast = conversationMessageLast;
    }

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return Username;
    }

    public ConversationMessage getConversationMessageLast() {
        return conversationMessageLast;
    }
}
