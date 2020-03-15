package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditorView;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat.EsaphSmileyPickerAdapterChat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.EsaphSmileyPickerFragmentPlopp;

public class EsaphSmileyViewBASEFragment extends EsaphGlobalCommunicationFragment
{
    public static final String EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER = "esaph.spotlight.smiley.picker.interface.listener";
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    public EsaphSmileyPickerAdapter esaphSmileyPickerAdapter;

    public EsaphSmileyViewBASEFragment() {
        // Required empty public constructor
    }

    public EsaphSmileyPickerAdapter getEsaphSmileyPickerAdapter()
    {
        return esaphSmileyPickerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_smiley_view, container, false);
        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(),
                EsaphSmileyViewBASEFragment.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);
        recylerView.setAdapter(esaphSmileyPickerAdapter);

        return rootView;
    }

    public interface OnSmileySelectListenerCameraEditor extends Serializable
    {
        void onSmileySelected(EsaphEmojie esaphEmojie);
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
