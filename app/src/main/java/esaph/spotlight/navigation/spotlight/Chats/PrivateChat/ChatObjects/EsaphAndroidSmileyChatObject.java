/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects;

import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class EsaphAndroidSmileyChatObject extends ConversationMessage
{
    private EsaphEmojie esaphEmojie;

    public EsaphAndroidSmileyChatObject(long _ID, long absender, long ID_CHAT, long uhrzeit, short messageStatus, EsaphEmojie esaphEmojie, String Absender,
                                        String esaphPloppInformations)
    {
        super(_ID, absender, ID_CHAT, uhrzeit, messageStatus, CMTypes.FEMO, Absender, esaphPloppInformations);
        this.esaphEmojie = esaphEmojie;
    }

    public EsaphEmojie getEsaphEmojie() {
        return esaphEmojie;
    }

    public void setEsaphEmojie(EsaphEmojie esaphEmojie) {
        this.esaphEmojie = esaphEmojie;
    }
}
