/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphSmileyPickerFragmentChat extends EsaphGlobalCommunicationFragment
{
    private SmileyViewPagerAdapterChat smileyViewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EsaphSmileyPickerFragmentChat()
    {

    }

    public static EsaphSmileyPickerFragmentChat getInstance(EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat onSmileySelectedListenerChat)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphSmileyViewBASEFragmentChat.extraInterfaceListener, onSmileySelectedListenerChat);
        EsaphSmileyPickerFragmentChat esaphSmileyPickerFragmentChat = new EsaphSmileyPickerFragmentChat();
        esaphSmileyPickerFragmentChat.setArguments(bundle);
        return esaphSmileyPickerFragmentChat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat onSmileySelectedListenerChat = (EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat) bundle.getSerializable(EsaphSmileyViewBASEFragmentChat.extraInterfaceListener);

            List<EsaphSmileyViewBASEFragmentChat> list = new ArrayList<>();
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.activity, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.flags, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.food, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.nature, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.objects, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.people, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.symbols, onSmileySelectedListenerChat));
            list.add(EsaphSmileyViewBASEFragmentChat.getInstance(R.xml.travel, onSmileySelectedListenerChat));

            smileyViewPagerAdapter = new SmileyViewPagerAdapterChat(getContext(),
                    getChildFragmentManager(),
                    list);
        }
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

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(EsaphSmileyPickerFragmentChat.this)
                    .commit();
            return true;
        }

        return false;
    }


}
