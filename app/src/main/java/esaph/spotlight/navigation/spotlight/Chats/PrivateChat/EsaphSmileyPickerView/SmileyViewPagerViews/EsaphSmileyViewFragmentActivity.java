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

public class EsaphSmileyViewFragmentActivity extends EsaphSmileyViewBASEFragment
{
    public EsaphSmileyViewFragmentActivity()
    {

    }

    public static EsaphSmileyViewFragmentActivity getInstance(OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphSmileyViewBASEFragment.EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER, onSmileySelectListenerCameraEditor);
        EsaphSmileyViewFragmentActivity esaphSmileyViewFragmentActivity = new EsaphSmileyViewFragmentActivity();
        esaphSmileyViewFragmentActivity.setArguments(bundle);
        return esaphSmileyViewFragmentActivity;
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
        super.esaphSmileyPickerAdapter = new EsaphSmileyPickerAdapter(EsaphSmileyViewFragmentActivity.this,
                onSmileySelectListenerCameraEditor,
                EsaphXMLSmileyParser.parse(getContext(), R.xml.activity));

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
