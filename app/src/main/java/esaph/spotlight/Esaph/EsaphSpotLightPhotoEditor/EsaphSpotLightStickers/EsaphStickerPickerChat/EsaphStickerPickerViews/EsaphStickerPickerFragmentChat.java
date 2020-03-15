/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews;

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

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.Background.AsyncLoadStickersFromServer;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLSticker;

public class EsaphStickerPickerFragmentChat extends EsaphGlobalCommunicationFragment
{
    public static final String extraInterfaceOnStickerSelectedListener = "esaph.spotlight.stickerpicker.interface.stickerselect";
    private EsaphStickerPagerAdapterChat esaphStickerPagerAdapterChat;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View viewNoData;
    private EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat onStickerSelectedListenerChat;

    public EsaphStickerPickerFragmentChat()
    {
    }

    public static EsaphStickerPickerFragmentChat getInstance(EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat onStickerSelectedListenerChat)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphStickerPickerFragmentChat.extraInterfaceOnStickerSelectedListener, onStickerSelectedListenerChat);

        EsaphStickerPickerFragmentChat esaphStickerPickerFragmentChat =
                new EsaphStickerPickerFragmentChat();
        esaphStickerPickerFragmentChat.setArguments(bundle);

        return esaphStickerPickerFragmentChat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            onStickerSelectedListenerChat =
                    (EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat)
                            bundle.getSerializable(EsaphStickerPickerFragmentChat.extraInterfaceOnStickerSelectedListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.layout_esaph_sticker_picker_fragment_chat, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewNoData = rootView.findViewById(R.id.linearLayoutNoSearchResults);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab != null)
                {
                    if(tab.getPosition() == tabLayout.getTabCount()) //-1?
                    {
                        new AsyncLoadStickersFromServer(getContext(), new AsyncLoadStickersFromServer.OnStickerPackSynchronizedListener() {
                            @Override
                            public void onStickerPackSynchronized() {
                                loadAllStickerPacks();
                            }
                        }).execute();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });

        esaphStickerPagerAdapterChat = new EsaphStickerPagerAdapterChat(getContext(),
                viewNoData,
                getChildFragmentManager());
        viewPager.setAdapter(esaphStickerPagerAdapterChat);

        loadAllStickerPacks();
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(EsaphStickerPickerFragmentChat.this).commit();
            return true;
        }

        return false;
    }



    private void loadAllStickerPacks()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<EsaphSpotLightStickerPack> list;

                SQLSticker sqlSticker = new SQLSticker(getContext());
                list = sqlSticker.getAllStickerPackLimiteOrderByTime();
                sqlSticker.close();

                final List<EsaphStickerViewBASEFragmentChat> listBaseFragments = new ArrayList<>();

                int count = list.size();
                for(int counter = 0; counter < count; counter++)
                {
                    listBaseFragments.add(EsaphStickerViewBASEFragmentChat.getInstance(list.get(counter), onStickerSelectedListenerChat));
                }

                if(viewPager != null)
                {
                    viewPager.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(viewPager != null)
                            {
                                esaphStickerPagerAdapterChat.clear();
                                esaphStickerPagerAdapterChat.addData(listBaseFragments);

                                if(listBaseFragments.isEmpty())
                                {
                                    new AsyncLoadStickersFromServer(getContext(), new AsyncLoadStickersFromServer.OnStickerPackSynchronizedListener() {
                                        @Override
                                        public void onStickerPackSynchronized()
                                        {
                                            loadAllStickerPacks();
                                        }
                                    }).execute();
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
