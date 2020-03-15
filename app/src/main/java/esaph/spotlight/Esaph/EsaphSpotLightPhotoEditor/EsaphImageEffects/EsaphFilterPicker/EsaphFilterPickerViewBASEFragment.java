/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditorView;
import esaph.spotlight.R;

public abstract class EsaphFilterPickerViewBASEFragment extends EsaphGlobalCommunicationFragment
{
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    private PhotoEditor photoEditorRoot;
    public RecyclerView.Adapter esaphFilterPickerAdapter;

    public EsaphFilterPickerViewBASEFragment() {
        // Required empty public constructor
    }

    public abstract RecyclerView.Adapter onInitAdapter();

    public RecyclerView.Adapter getEsaphFilterPickerAdapter()
    {
        return esaphFilterPickerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_filter_picker_view_base, container, false);

        photoEditorRoot = ((PhotoEditorView) getActivity().findViewById(R.id.mIdPhotoEditorView)).getPhotoEditor();

        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), EsaphFilterPickerViewBASEFragment.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);
        esaphFilterPickerAdapter = onInitAdapter();
        recylerView.setAdapter(esaphFilterPickerAdapter);

        return rootView;
    }

    public PhotoEditor getPhotoEditorRoot() {
        return photoEditorRoot;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
