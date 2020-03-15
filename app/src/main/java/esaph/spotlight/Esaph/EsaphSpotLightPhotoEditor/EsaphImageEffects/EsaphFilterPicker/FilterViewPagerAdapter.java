/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class FilterViewPagerAdapter extends FragmentStatePagerAdapter
{
    private Context context;
    private List<EsaphFilterPickerViewBASEFragment> fragments;

    public FilterViewPagerAdapter(Context context,
                                  FragmentManager fm,
                                  List<EsaphFilterPickerViewBASEFragment> fragments)
    {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "NORMAL_FILTER";
        }

        return "";
    }

    @Override
    public EsaphFilterPickerViewBASEFragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
