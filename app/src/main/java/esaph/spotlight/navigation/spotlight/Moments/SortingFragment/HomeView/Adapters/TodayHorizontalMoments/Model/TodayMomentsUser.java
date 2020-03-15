package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.Model;

import esaph.spotlight.Esaph.EsaphLockable;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class TodayMomentsUser extends EsaphLockable
{
    private int UID;
    private String Partner_Username;
    private ConversationMessage lastConversationMessage;
    private boolean hasSavedAll = true;

    public TodayMomentsUser(int UID, String Partner_Username, ConversationMessage conversationMessage, boolean hasSavedAll)
    {
        this.UID = UID;
        this.Partner_Username = Partner_Username;
        this.lastConversationMessage = conversationMessage;
        this.hasSavedAll = hasSavedAll;
    }

    public int getUID() {
        return UID;
    }

    public String getPartner_Username() {
        return Partner_Username;
    }

    public boolean hasSavedAll() {
        return hasSavedAll;
    }

    public void setHasSavedAll(boolean hasSavedAll) {
        this.hasSavedAll = hasSavedAll;
    }

    public ConversationMessage getLastConversationMessage() {
        return lastConversationMessage;
    }
}
