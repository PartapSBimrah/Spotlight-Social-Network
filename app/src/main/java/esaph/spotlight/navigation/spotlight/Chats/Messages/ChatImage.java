/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.Messages;

import esaph.spotlight.navigation.globalActions.CMTypes;

public class ChatImage extends ConversationMessage
{
    private String Beschreibung;
    private String IMAGE_ID;
    private long SERVER_ID;

    public ChatImage(
            long SERVER_ID,
            long _ID,
            long ABS_ID,
            long ID_CHAT,
            long uhrzeit,
            short status,
            String Beschreibung,
            String IMAGE_ID,
            String Absender)
    {
        super(_ID, ABS_ID, ID_CHAT, uhrzeit, status, CMTypes.FPIC, Absender, null);
        this.SERVER_ID = SERVER_ID;
        this.Beschreibung = Beschreibung;
        this.IMAGE_ID = IMAGE_ID;

        /*
        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(ChatImage.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }*/

        super.setIMAGE_ID(IMAGE_ID);
        super.setMESSAGE_ID_SERVER(SERVER_ID);
    }

    public long getSERVER_ID() {
        return SERVER_ID;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public String getIMAGE_ID() {
        return IMAGE_ID;
    }
}
