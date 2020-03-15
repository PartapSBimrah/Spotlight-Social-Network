package esaph.spotlight.navigation.globalActions;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class PostSeenUntransmitted
{
    private long _ID;
    private ConversationMessage conversationMessage;

    public PostSeenUntransmitted(long _ID, ConversationMessage conversationMessage) {
        this._ID = _ID;
        this.conversationMessage = conversationMessage;
    }

    public long get_ID() {
        return _ID;
    }

    public ConversationMessage getConversationMessage() {
        return conversationMessage;
    }
}
