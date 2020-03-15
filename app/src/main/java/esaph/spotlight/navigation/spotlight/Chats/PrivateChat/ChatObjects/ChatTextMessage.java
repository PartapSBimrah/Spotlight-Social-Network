/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects;

import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class ChatTextMessage extends ConversationMessage
{
    private String TextMessage;

    public ChatTextMessage(String TextMessage, long _ID, long ABS_ID, long ID_CHAT, long Uhrzeit, short messageStatus, String Absender,
                           String esaphPloppInformations)
    {
        super(_ID, ABS_ID, ID_CHAT, Uhrzeit, messageStatus, CMTypes.FTEX, Absender, esaphPloppInformations);
        this.TextMessage = TextMessage;
    }

    public String getTextMessage() {
        return TextMessage;
    }
}
