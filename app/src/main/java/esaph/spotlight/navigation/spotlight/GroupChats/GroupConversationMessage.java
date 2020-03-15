/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.GroupChats;

import java.io.Serializable;

public class GroupConversationMessage implements Serializable
{
    private String Absender;
    private String Beschreibung;
    private String GIID;
    private String PID;
    private short Format;
    private String MessageText;
    private boolean ISAVED;
    private int MessageStatus;
    private long MessageHash;
    private long Uhrzeit;

    public GroupConversationMessage(String Absender, String GIID, String PID, short Format, String MessageText, String Beschreibung, boolean ISAVED, int MessageStatus, long MessageHash, long Uhrzeit)
    {
        this.Absender = Absender;
        this.Beschreibung = Beschreibung;
        this.GIID = GIID;
        this.PID = PID;
        this.Format = Format;
        this.MessageText = MessageText;
        this.ISAVED = ISAVED;
        this.MessageStatus = MessageStatus;
        this.MessageHash = MessageHash;
        this.Uhrzeit = Uhrzeit;
    }

    public String getBeschreibung()
    {
        return Beschreibung;
    }

    public String getAbsender()
    {
        return Absender;
    }

    public String getGIID() {
        return GIID;
    }

    public String getPID() {
        return PID;
    }

    public short getType() {
        return Format;
    }

    public String getMessageText() {
        return MessageText;
    }

    public boolean getISAVED() {
        return ISAVED;
    }

    public int getMessageStatus() {
        return MessageStatus;
    }

    public long getMessageHash() {
        return MessageHash;
    }

    public long getUhrzeit() {
        return Uhrzeit;
    }

    public void setNewMessageHash(long newMessageHash)
    {
        this.MessageHash = newMessageHash;
    }

    public void setMessageStatus(int newStatus)
    {
        this.MessageStatus = newStatus;
    }
}
