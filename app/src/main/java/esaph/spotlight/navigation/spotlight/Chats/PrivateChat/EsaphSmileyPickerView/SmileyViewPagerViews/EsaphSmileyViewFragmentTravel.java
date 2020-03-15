package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileyPickerAdapter;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileyViewBASEFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphXMLSmileyParser;
import esaph.spotlight.R;

public class EsaphSmileyViewFragmentTravel extends EsaphSmileyViewBASEFragment
{
    public EsaphSmileyViewFragmentTravel()
    {

    }

    public static EsaphSmileyViewFragmentTravel getInstance(OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphSmileyViewBASEFragment.EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER, onSmileySelectListenerCameraEditor);
        EsaphSmileyViewFragmentTravel esaphSmileyViewFragmentTravel = new EsaphSmileyViewFragmentTravel();
        esaphSmileyViewFragmentTravel.setArguments(bundle);
        return esaphSmileyViewFragmentTravel;
    }


    private OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            onSmileySelectListenerCameraEditor =
                    (OnSmileySelectListenerCameraEditor) bundle.getSerializable(EsaphSmileyViewBASEFragment.EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.esaphSmileyPickerAdapter = new EsaphSmileyPickerAdapter(EsaphSmileyViewFragmentTravel.this,
                onSmileySelectListenerCameraEditor,
                EsaphXMLSmileyParser.parse(getContext(), R.xml.activity));

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
