package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;

import java.util.List;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.navigation.EmptyFragmentHURENSOHN;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentAudio;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentEmojie;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager.ChatListViewPagerFragments.ChatItemFragmentText;
import esaph.spotlight.navigation.spotlight.Moments.AktuellePrivateMomentView.DatumHolderFragment;
import esaph.spotlight.navigation.spotlight.Moments.AktuellePrivateMomentView.YearTimeHolderFragment;
import esaph.spotlight.navigation.spotlight.DisplayingFragment.PictureFragmentPrivateUser;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.DisplayingFragment.VideoFragmentPrivateUser;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;

public class SpotLightViewPagerAdapter extends ViewPagerAdapterGetList
{
    private ViewPagerDataSetChangedListener viewPagerDataSetChangedListener;
    private ViewPager.OnPageChangeListener mainPagerListener;
    private ViewPager viewPager;
    private EsaphGlobalCommunicationFragment currentFragment = null;
    private Context context;
    private List<Object> list;
    private int imageCount = 0;

    public SpotLightViewPagerAdapter(FragmentManager manager, Context context,
                                     ViewPagerDataSetChangedListener viewPagerDataSetChangedListener,
                                     List<Object> list,
                                     ViewPager.OnPageChangeListener mainPagerListener,
                                     ViewPager viewPager)
    {
        super(manager);
        this.viewPagerDataSetChangedListener = viewPagerDataSetChangedListener;
        this.list = list;
        this.imageCount = getImageCount(this.list);
        this.context = context;
        this.mainPagerListener = mainPagerListener;
        this.viewPager = viewPager;
    }

    private int getImageCount(List<Object> objects)
    {
        int imageCount = 0;
        int size = objects.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(list.get(counter) instanceof ConversationMessage)
            {
                imageCount++;
            }
        }

        return imageCount;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        this.imageCount = getImageCount(this.list);
        mainPagerListener.onPageSelected(viewPager.getCurrentItem());

        if(viewPagerDataSetChangedListener != null)
        {
            viewPagerDataSetChangedListener.notifyOnDataChangeInViewpager();
        }
    }

    public EsaphGlobalCommunicationFragment getCurrentFragment()
    {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        if (getCurrentFragment() != object && object instanceof EsaphGlobalCommunicationFragment)
        {
            currentFragment = ((EsaphGlobalCommunicationFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public void updatePostByPid(ConversationMessage conversationMessage)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof ConversationMessage)
            {
                ConversationMessage conversationMessageIntern = (ConversationMessage) object;
                if(conversationMessage.getIMAGE_ID().equals(conversationMessageIntern.getIMAGE_ID()))
                {
                    list.set(counter, conversationMessage);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void removePostByPid(long MESSAGE_ID)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof ConversationMessage)
            {
                if(((ConversationMessage)object).getMESSAGE_ID() == MESSAGE_ID)
                {
                    if(((counter - 1) > 0 && list.get(counter-1) instanceof DatumList) &&
                            ((counter + 1) > list.size() && list.get(counter+1) instanceof DatumList))
                    {
                        //Das bild ist in dem fall von beiden daten umgeben, und da es gelöscht wird muss das obere object mitgelöscht werden.
                        Object first = list.get(counter-1);
                        Object second = list.get(counter);
                        list.remove(first);
                        list.remove(second);
                    }
                    else
                    {
                        list.remove(counter);
                    }
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        Object object = getItemFromList(position);

        if(object instanceof ConversationMessage)
        {
            ConversationMessage conversationMessage = (ConversationMessage) object;

            if(conversationMessage.getType() == CMTypes.FVID)
            {
                return VideoFragmentPrivateUser.getInstance((ChatVideo) conversationMessage).setSpotLightViewPagerAdapter(this);
            }
            else if(conversationMessage.getType() == CMTypes.FPIC)
            {
                return PictureFragmentPrivateUser.getInstance((ChatImage) conversationMessage).setSpotLightViewPagerAdapter(this);
            }
            else if(conversationMessage.getType() == CMTypes.FAUD)
            {
                return ChatItemFragmentAudio.getInstance(conversationMessage);
            }
            else if(conversationMessage.getType() == CMTypes.FEMO)
            {
                return ChatItemFragmentEmojie.getInstance(conversationMessage);
            }
            else if(conversationMessage.getType() == CMTypes.FTEX)
            {
                return ChatItemFragmentText.getInstance(conversationMessage);
            }
        }
        else if(object instanceof DatumList)
        {
            DatumList datumHolder = (DatumList) object;
            return DatumHolderFragment.getInstance(datumHolder);
        }
        else if(object instanceof YearTime)
        {
            YearTime yearTime = (YearTime) object;
            return YearTimeHolderFragment.getInstance(yearTime);
        }
        else if(object instanceof TitleList)
        {
            return new EmptyFragmentHURENSOHN();
        }
        return null;
    }

    public Object getItemFromList(int position)
    {
        return list.get(position);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public List<Object> getList()
    {
        return this.list;
    }

    @Override
    public int[] getObjectsCount()
    {
        return new int[]{imageCount};
    }
}
