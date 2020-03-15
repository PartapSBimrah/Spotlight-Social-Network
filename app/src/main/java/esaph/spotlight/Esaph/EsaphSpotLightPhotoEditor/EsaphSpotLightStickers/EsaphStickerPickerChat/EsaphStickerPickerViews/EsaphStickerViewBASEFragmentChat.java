/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphStickerViewBASEFragmentChat extends EsaphGlobalCommunicationFragment
{
    private static final String KEY_PACK_LIST_PARCEL = "esaph.spotlight.parcel.esaphstickerpack";
    private static final String extraInterfaceStickerListener = "esaph.spotlight.stickerpicker.interface.stickerlistener";
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    private EsaphSpotLightStickerPack esaphSpotLightStickerPack;
    public EsaphStickerPickerAdapterChat esaphStickerPickerAdapter;
    private EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat onStickerSelectedListenerChat;

    public EsaphStickerViewBASEFragmentChat()
    {
        // Required empty public constructor
    }


    public static EsaphStickerViewBASEFragmentChat getInstance(EsaphSpotLightStickerPack packList,
                                                               EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat onStickerSelectedListenerChat)
    {
        EsaphStickerViewBASEFragmentChat fragment = new EsaphStickerViewBASEFragmentChat();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphStickerViewBASEFragmentChat.KEY_PACK_LIST_PARCEL, packList);
        bundle.putSerializable(EsaphStickerViewBASEFragmentChat.extraInterfaceStickerListener, onStickerSelectedListenerChat);
        fragment.setArguments(bundle);

        return fragment;
    }

    public EsaphStickerPickerAdapterChat getEsaphStickerPickerAdapter()
    {
        return esaphStickerPickerAdapter;
    }

    public EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat getOnStickerSelectedListenerChat() {
        return onStickerSelectedListenerChat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            onStickerSelectedListenerChat = (EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat)
                    bundle.getSerializable(EsaphStickerViewBASEFragmentChat.extraInterfaceStickerListener);

            esaphSpotLightStickerPack = (EsaphSpotLightStickerPack) bundle.getSerializable(
                    EsaphStickerViewBASEFragmentChat.KEY_PACK_LIST_PARCEL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_smiley_view, container, false);

        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), EsaphStickerViewBASEFragmentChat.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);

        esaphStickerPickerAdapter = new EsaphStickerPickerAdapterChat(EsaphStickerViewBASEFragmentChat.this,
                esaphSpotLightStickerPack.getEsaphSpotLightStickers());

        recylerView.setAdapter(esaphStickerPickerAdapter);

        return rootView;
    }

    public EsaphSpotLightStickerPack getStickerPack()
    {
        return esaphSpotLightStickerPack;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
