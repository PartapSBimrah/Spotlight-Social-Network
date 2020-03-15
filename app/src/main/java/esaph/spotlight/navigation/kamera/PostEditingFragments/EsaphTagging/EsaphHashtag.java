/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging;

import java.io.Serializable;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class EsaphHashtag implements Serializable
{
    public static final int VIEWTYPE_NORMAL_HASHTAG = 0;
    public static final int VIEWTYPE_NEW_HASHTAG = 1;
    private ConversationMessage conversationMessage;
    private String HashtagName;
    private int HashtagAnzahl;

    private int currentViewType = VIEWTYPE_NORMAL_HASHTAG;

    public EsaphHashtag(String HashtagName, ConversationMessage conversationMessage, int HashtagAnzahl)
    {
        this.HashtagName = HashtagName;
        this.conversationMessage = conversationMessage;
        this.HashtagAnzahl = HashtagAnzahl;
    }

    public String getHashtagName() {
        return HashtagName;
    }

    public int getHashtagAnzahl() {
        return HashtagAnzahl;
    }

    public ConversationMessage getLastConversationMessage()
    {
        return conversationMessage;
    }

    public int getCurrentViewType() {
        return currentViewType;
    }

    public void setCurrentViewType(int currentViewType) {
        this.currentViewType = currentViewType;
    }
}
