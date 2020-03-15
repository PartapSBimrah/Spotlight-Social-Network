/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class ChatShared extends ConversationMessage
{
    private ConversationMessage conversationMessageShared;

    public ChatShared(int _ID, int absender, int KEY_CHAT_PARTNER, long uhrzeit, short messageStatus, short type, ConversationMessage conversationMessageShared, String Absender)
    {
        super(_ID, absender, KEY_CHAT_PARTNER, uhrzeit, messageStatus, type, Absender, null);
        this.conversationMessageShared = conversationMessageShared;
    }

    public ConversationMessage getConversationMessageShared() {
        return conversationMessageShared;
    }
}
