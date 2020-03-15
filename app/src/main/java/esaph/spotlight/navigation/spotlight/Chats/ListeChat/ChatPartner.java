package esaph.spotlight.navigation.spotlight.Chats.ListeChat;

import java.io.Serializable;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class ChatPartner implements Serializable
{
    private long UID_CHATPARTNER;
    private boolean isTypingAMessage = false;
    private boolean hideChat; //If blocked, or friendship died.
    private String PartnerUsername;
    private String Vorname;
    private ConversationMessage lastConversationMessage;
    private String descriptionPlopp;
    private boolean hasConversations;

    public ChatPartner(String Partner, String Vorname,
                       String DescriptionSpot,
                       long _UID, ConversationMessage conversationMessage, boolean hideChat)
    {
        this.Vorname = Vorname;
        this.descriptionPlopp = DescriptionSpot;
        this.PartnerUsername = Partner;
        this.UID_CHATPARTNER = _UID;
        this.hideChat = hideChat;
        this.lastConversationMessage = conversationMessage;
        this.hasConversations = conversationMessage != null;
    }

    public String getDescriptionPlopp() {
        return descriptionPlopp;
    }

    public String getVorname() {
        return Vorname;
    }

    public long getUID_CHATPARTNER() {
        return UID_CHATPARTNER;
    }

    public boolean isHideChat()
    {
        return hideChat;
    }

    public ConversationMessage getLastConversationMessage()
    {
        return this.lastConversationMessage;
    }

    public boolean hasConversations()
    {
        return hasConversations;
    }

    public String getPartnerUsername()
    {
        return PartnerUsername;
    }

    public boolean isTypingAMessage() {
        return isTypingAMessage;
    }

    public void setTypingAMessage(boolean typingAMessage) {
        this.isTypingAMessage = typingAMessage;
    }

    public void setLastConversationMessage(ConversationMessage conversationMessage)
    {
        lastConversationMessage = conversationMessage;
        if(this.lastConversationMessage != null)
        {
            this.hasConversations = true;
        }
        else
        {
            this.hasConversations = false;
        }
    }
}
