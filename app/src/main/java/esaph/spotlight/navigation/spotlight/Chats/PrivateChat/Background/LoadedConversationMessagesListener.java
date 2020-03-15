package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import java.util.List;

public interface LoadedConversationMessagesListener
{
    void onMessagesLoaded(List<Object> list,
                          int... loadCounts);
}
