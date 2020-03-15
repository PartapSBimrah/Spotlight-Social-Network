/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphSmileyViewBASEFragment extends EsaphGlobalCommunicationFragment
{
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    public EsaphSmileyPickerAdapterPlopp esaphSmileyPickerAdapter;

    public EsaphSmileyViewBASEFragment()
    {
        // Required empty public constructor
    }

    public static EsaphSmileyViewBASEFragment getInstance()
    {
        return new EsaphSmileyViewBASEFragment();
    }


    private EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener onSmileySelectedListener;
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener)
        {
            onSmileySelectedListener = (EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener) context;
        }
    }


    public EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener getOnSmileySelectedListener() {
        return onSmileySelectedListener;
    }

    public EsaphSmileyPickerAdapterPlopp getEsaphSmileyPickerAdapter()
    {
        return esaphSmileyPickerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_smiley_view, container, false);

        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), EsaphSmileyViewBASEFragment.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);
        recylerView.setAdapter(esaphSmileyPickerAdapter);

        return rootView;
    }


    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



}
