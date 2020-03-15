/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker.FilterViewPagerViews;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.Arrays;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoFilter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker.EsaphFilterPickerAdapter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker.EsaphFilterPickerViewBASEFragment;

public class EsaphFilterListNormal extends EsaphFilterPickerViewBASEFragment
{
    private static final String extraImageFilterListener = "esaph.spotlight.interface.filterpickerlistener";
    private EsaphFilterPickerListener esaphFilterPickerListener;

    public EsaphFilterListNormal()
    {
    }

    public static EsaphFilterListNormal getInstance(EsaphFilterPickerListener esaphFilterPickerListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphFilterListNormal.extraImageFilterListener, esaphFilterPickerListener);
        EsaphFilterListNormal esaphFilterListNormal = new EsaphFilterListNormal();
        esaphFilterListNormal.setArguments(bundle);
        return esaphFilterListNormal;
    }

    public interface EsaphFilterPickerListener extends Serializable
    {
        void onFilterSelected(PhotoFilter photoFilter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            esaphFilterPickerListener = (EsaphFilterPickerListener) bundle.getSerializable(EsaphFilterListNormal.extraImageFilterListener);
        }
    }

    @Override
    public RecyclerView.Adapter onInitAdapter()
    {
        // TODO: 25.01.2019 make this effects donte
        return new EsaphFilterPickerAdapter(
                EsaphFilterListNormal.this,
                ((BitmapDrawable) getPhotoEditorRoot().getPhotoEditorView().getSource().getDrawable()).getBitmap(),
                Arrays.asList(PhotoFilter.values()),
                esaphFilterPickerListener);
    }
}
