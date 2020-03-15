/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.navigationLeftSite.VerticalViewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapterLeftSite extends FragmentStatePagerAdapter
{
    private static final int PAGE_COUNT = 2;
    private Fragment currentFragment = null;

    public ViewPagerAdapterLeftSite(FragmentManager manager)
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
                //Dicke schrottelung public posts
                return null;

            case 1:
                //Schrottelung
                return null;
        }
        return null;
    }

    @Override
    public int getCount()
    {
        return ViewPagerAdapterLeftSite.PAGE_COUNT;
    }
}
