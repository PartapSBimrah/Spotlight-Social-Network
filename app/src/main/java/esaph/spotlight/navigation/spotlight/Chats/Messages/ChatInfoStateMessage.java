package esaph.spotlight.navigation.spotlight.Chats.Messages;

import esaph.spotlight.navigation.globalActions.CMTypes;

public class ChatInfoStateMessage extends ConversationMessage
{
    private short STATE_CODE;
    private ConversationMessage conversationMessageFrom;

    public static class ChatInfoStates
    {
        public static final short STATE_PARTNER_SAVED = 0;
        public static final short STATE_PARTNER_UNSAVED = 1;
        public static final short STATE_PARTNER_ALLOWED_PERMISSION = 2;
        public static final short STATE_PARTNER_DECLINED_PERMISSION = 3;
    }

    public ChatInfoStateMessage(ConversationMessage conversationMessageFrom, long _ID, long ABS_ID, long ID_CHAT, long uhrzeit, short STATE_CODE, String Absender)
    {
        super(_ID, ABS_ID, ID_CHAT, uhrzeit, (short)-1, CMTypes.FINF, Absender, null);
        this.STATE_CODE = STATE_CODE;
        this.conversationMessageFrom = conversationMessageFrom;
    }

    public short getSTATE_CODE() {
        return STATE_CODE;
    }

    public ConversationMessage getConversationMessageFrom() {
        return conversationMessageFrom;
    }
}
