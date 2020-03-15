package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.VerticalViewPager;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.navigation.EmptyFragmentHURENSOHN;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model.PublicConversationMessage;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.DisplayingFragment.PictureFragmentPublic;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.DisplayingFragment.VideoFragmentPublic;

public class ViewPagerAdapterPublicHome extends FragmentStatePagerAdapter
{
    private List<PublicConversationMessage> list;
    private Fragment currentFragment = null;
    private FragmentManager fragmentManager;
    private Context context;

    public ViewPagerAdapterPublicHome(FragmentManager manager, Context context)
    {
        super(manager);
        this.fragmentManager = manager;
        this.context = context;
        this.list = new ArrayList<>();
    }

    public List<PublicConversationMessage> getList()
    {
        return list;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        if (getCurrentFragment() != object)
        {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment()
    {
        return currentFragment;
    }

    @Override
    public Fragment getItem(int position)
    {
        PublicConversationMessage publicConversationMessage = list.get(position);
        if(publicConversationMessage.getType() == CMTypes.FPIC)
        {
            return PictureFragmentPublic.getInstance(publicConversationMessage);
        }
        else if(publicConversationMessage.getType() == CMTypes.FVID)
        {
            return VideoFragmentPublic.getInstance(publicConversationMessage);
        }

        return new EmptyFragmentHURENSOHN();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }
}
