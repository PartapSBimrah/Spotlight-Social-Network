/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentActivity;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentFlags;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentFood;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentNature;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentObjects;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentPeople;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentSymbols;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews.EsaphSmileyViewFragmentTravel;
import esaph.spotlight.R;

public class EsaphSmileyPickerFragmentPlopp extends BottomSheetDialogFragment
{
    private SmileyViewPagerAdapter smileyViewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EsaphSmileyPickerFragmentPlopp()
    {

    }


    public interface OnSmileySelectedListener
    {
        public void onSmileySelected(EsaphEmojie esaphEmojie);
    }


    public static EsaphSmileyPickerFragmentPlopp getInstance()
    {
        return new EsaphSmileyPickerFragmentPlopp();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        List<EsaphSmileyViewBASEFragment> list = new ArrayList<>();
        list.add(EsaphSmileyViewFragmentActivity.getInstance());
        list.add(EsaphSmileyViewFragmentFlags.getInstance());
        list.add(EsaphSmileyViewFragmentFood.getInstance());
        list.add(EsaphSmileyViewFragmentNature.getInstance());
        list.add(EsaphSmileyViewFragmentObjects.getInstance());
        list.add(EsaphSmileyViewFragmentPeople.getInstance());
        list.add(EsaphSmileyViewFragmentSymbols.getInstance());
        list.add(EsaphSmileyViewFragmentTravel.getInstance());

        smileyViewPagerAdapter = new SmileyViewPagerAdapter(getContext(),
                getChildFragmentManager(),
                list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_esaph_smiley_picker_fragment_chat, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(smileyViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
}
