/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp;

import android.content.Context;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphXMLSmileyParser;
import esaph.spotlight.R;

public class SmileyViewPagerAdapter extends FragmentStatePagerAdapter
{
    private Context context;
    private List<EsaphSmileyViewBASEFragment> fragments;

    public SmileyViewPagerAdapter(Context context,
                                  FragmentManager fm,
                                  List<EsaphSmileyViewBASEFragment> fragments)
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
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.activity);

            case 1:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.flags);

            case 2:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.food);

            case 3:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.nature);

            case 4:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.objects);

            case 5:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.people);

            case 6:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.symbols);

            case 7:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.travel);
        }

        return "";
    }

    @Override
    public EsaphSmileyViewBASEFragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
