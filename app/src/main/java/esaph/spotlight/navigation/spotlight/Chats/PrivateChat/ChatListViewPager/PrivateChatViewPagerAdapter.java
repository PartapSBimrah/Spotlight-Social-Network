package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentAudio;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentEmojie;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentImage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentText;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentVideo;

public class PrivateChatViewPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String KEY_EXTRA_CHAT_ITEM_FRAGMENT_CONVERSATIONMESSAGE = "esaph.spotlight.chatitemfragment.key.conversationmessage";
    private List<Object> list = new ArrayList<>();
    public PrivateChatViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<Object> getList() {
        return list;
    }

    public void updateConversationMessageByID(ConversationMessage conversationMessage)
    {
        for(int counter = list.size() - 1; counter >= 0 ; counter--)
        {
            Object object = list.get(counter);
            if (object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessageIntern = (ConversationMessage) object;
                if(conversationMessageIntern.getMESSAGE_ID() == conversationMessage.getMESSAGE_ID())
                {
                    list.set(counter, conversationMessage);
                    break;
                }
            }
        }
    }

    public void removeItemById(long MESSAGE_ID)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if (object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessage = (ConversationMessage) object;
                if (conversationMessage.getMESSAGE_ID() == MESSAGE_ID)
                {
                    list.remove(counter);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        ConversationMessage conversationMessage = (ConversationMessage) list.get(position);
        switch (conversationMessage.getType())
        {
            case CMTypes.FTEX:
                return ChatItemFragmentText.getInstance(conversationMessage);

            case CMTypes.FEMO:
                return ChatItemFragmentEmojie.getInstance(conversationMessage);

            case CMTypes.FAUD:
                return ChatItemFragmentAudio.getInstance(conversationMessage);

            case CMTypes.FPIC:
                return ChatItemFragmentImage.getInstance(conversationMessage);

            case CMTypes.FVID:
                return ChatItemFragmentVideo.getInstance(conversationMessage);
        }
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
