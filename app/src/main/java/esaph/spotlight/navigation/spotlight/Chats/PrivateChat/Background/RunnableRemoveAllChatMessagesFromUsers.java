package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.ref.WeakReference;

import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ArrayAdapterPrivateChat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;

public class RunnableRemoveAllChatMessagesFromUsers implements Runnable
{
    private WeakReference<PrivateChat> privateChatWeakReference;
    private long UID;

    public RunnableRemoveAllChatMessagesFromUsers(PrivateChat privateChat, long UID)
    {
        this.privateChatWeakReference = new WeakReference<PrivateChat>(privateChat);
        this.UID = UID;
    }

    @Override
    public void run()
    {
        try
        {
            SQLChats sqlChats = new SQLChats(this.privateChatWeakReference.get());
            sqlChats.removeAllChatMessagesBetweenPartner(UID);
            sqlChats.close();
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    PrivateChat privateChat = privateChatWeakReference.get();
                    if(privateChat != null)
                    {
                        ArrayAdapterPrivateChat arrayAdapterPrivateChat = privateChat.getArrayAdapterPrivateChat();
                        arrayAdapterPrivateChat.clear();
                        privateChatWeakReference.get().loadMoreDataChat(
                                0);
                    }
                }
            });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableRemoveAllChatMessagesFromUsers() failed: " + ec);
        }
    }
}
