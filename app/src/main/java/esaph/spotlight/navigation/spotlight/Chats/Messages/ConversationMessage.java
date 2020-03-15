/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.Messages;

import org.json.JSONObject;

import java.io.Serializable;

public class ConversationMessage implements Serializable
{
    private String esaphPloppInformationsJSONString;
    private long MESSAGE_ID_SERVER;
    private long MESSAGE_ID;
    private long ABS_ID;
    private long ID_CHAT;

    private String Absender;

    private long Uhrzeit;
    private short messageStatus; //2 possibilitys, die handelt je nachdem ob own message oder nicht.
    private short type;

    private String IMAGE_ID;



    /*
    ownMessage == TRUE
    -1 failed to send
    0 means, sending
    1 means, currently sent
    2 means, user seen it, but not saved.

    ownMessage == FALSE
    1 means, new message
    0 means, message has been seen.
     */

    public ConversationMessage(long MESSAGE_ID,
                               long ABS_ID,
                               long ID_CHAT,
                               long Uhrzeit,
                               short messageStatus,
                               short type,
                               String Absender,
                               String esaphPloppInformations)
    {
        this.MESSAGE_ID = MESSAGE_ID;
        this.ABS_ID = ABS_ID;
        this.ID_CHAT = ID_CHAT;
        this.Uhrzeit = Uhrzeit;
        this.messageStatus = messageStatus;
        this.type = type;
        this.Absender = Absender;
        this.esaphPloppInformationsJSONString = esaphPloppInformations;
    }

    public ConversationMessage() //NOT DEFINED, can be used while creating hashtags.
    {
        this.ABS_ID = -1;
        this.ID_CHAT = -1;
        this.Uhrzeit = 0;
        this.messageStatus = 0;
        this.type = -1;
        this.Absender = "";
    }

    public void setEsaphPloppInformationsJSONString(String esaphPloppInformationsJSONString) {
        this.esaphPloppInformationsJSONString = esaphPloppInformationsJSONString;
    }

    public JSONObject getEsaphPloppInformationsJSON()
    {
        try
        {
            return new JSONObject(esaphPloppInformationsJSONString);
        }
        catch (Exception ec)
        {
            return new JSONObject();
        }
    }

    public String getEsaphPloppInformationsJSONString() {
        return esaphPloppInformationsJSONString;
    }

    public void setMESSAGE_ID_SERVER(long MESSAGE_ID_SERVER) {
        this.MESSAGE_ID_SERVER = MESSAGE_ID_SERVER;
    }

    public long getMESSAGE_ID_SERVER() {
        return MESSAGE_ID_SERVER;
    }

    public void setMESSAGE_ID(long MESSAGE_ID) {
        this.MESSAGE_ID = MESSAGE_ID;
    }

    public String getAbsender() {
        return Absender;
    }

    public long getMESSAGE_ID() {
        return MESSAGE_ID;
    }

    public short getMessageStatus() {
        return messageStatus;
    }

    public short getType() {
        return type;
    }

    public long getMessageTime()
    {
        return Uhrzeit;
    }

    public long getABS_ID()
    {
        return ABS_ID;
    }

    public long getID_CHAT()
    {
        return ID_CHAT;
    }

    public void setMessageStatus(short status)
    {
        this.messageStatus = status;
    }

    public String getIMAGE_ID() {
        return IMAGE_ID;
    }

    public void setIMAGE_ID(String IMAGE_ID) {
        this.IMAGE_ID = IMAGE_ID;
    }

    public void setABS_ID(long ABS_ID) {
        this.ABS_ID = ABS_ID;
    }

    public void setID_CHAT(long ID_CHAT) {
        this.ID_CHAT = ID_CHAT;
    }

    public void setAbsender(String absender) {
        Absender = absender;
    }

    public void setUhrzeit(long uhrzeit) {
        Uhrzeit = uhrzeit;
    }
}
