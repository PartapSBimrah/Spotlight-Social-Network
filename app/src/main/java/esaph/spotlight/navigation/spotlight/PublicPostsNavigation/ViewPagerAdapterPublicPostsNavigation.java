package esaph.spotlight.navigation.spotlight.PublicPostsNavigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.PublicHomeFragment;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicWorld.PublicWorldFragment;

//Switch between public and private world.s

public class ViewPagerAdapterPublicPostsNavigation extends FragmentStatePagerAdapter
{
    private static final int PAGE_COUNT = 2;
    private Fragment currentFragment = null;

    public ViewPagerAdapterPublicPostsNavigation(FragmentManager manager)
    {
        super(manager);
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
        switch (position)
        {
            case 0:
                return PublicHomeFragment.getInstance();

            case 1:
                return PublicWorldFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount()
    {
        return ViewPagerAdapterPublicPostsNavigation.PAGE_COUNT;
    }
}
