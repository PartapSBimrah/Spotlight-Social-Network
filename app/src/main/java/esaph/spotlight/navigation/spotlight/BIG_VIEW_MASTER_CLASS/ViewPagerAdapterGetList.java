/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

public abstract class ViewPagerAdapterGetList extends FragmentStatePagerAdapter
{
    public ViewPagerAdapterGetList(FragmentManager fm)
    {
        super(fm);
    }

    public abstract List<Object> getList();

    @Deprecated
    public abstract int[] getObjectsCount();

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (position >= getCount())
        {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            if(manager != null)
            {
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }
        }
        else
        {
            super.destroyItem(container, position, object);
        }
    }
}
