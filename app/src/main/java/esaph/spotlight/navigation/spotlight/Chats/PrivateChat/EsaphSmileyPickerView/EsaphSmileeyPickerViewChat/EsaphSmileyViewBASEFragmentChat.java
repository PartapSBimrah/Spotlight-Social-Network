package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphXMLSmileyParser;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphSmileyViewBASEFragmentChat extends EsaphGlobalCommunicationFragment
{
    public static final String extraInterfaceListener = "esaph.spotlight.chat.keyboard.smiley.choosen.listener";
    public static final String extraSmileysXML = "esaph.spotlight.chat.keyboard.smiley.choosen.smileys.xml";
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    public EsaphSmileyPickerAdapterChat esaphSmileyPickerAdapter;
    private EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat onSmileySelectedListenerChat;

    public EsaphSmileyViewBASEFragmentChat() {
        // Required empty public constructor
    }

    public static EsaphSmileyViewBASEFragmentChat getInstance(int XML_ID, EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat onSmileySelectedListenerChat)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphSmileyViewBASEFragmentChat.extraInterfaceListener, onSmileySelectedListenerChat);
        bundle.putInt(EsaphSmileyViewBASEFragmentChat.extraSmileysXML, XML_ID);
        EsaphSmileyViewBASEFragmentChat esaphSmileyViewBASEFragmentChat = new EsaphSmileyViewBASEFragmentChat();
        esaphSmileyViewBASEFragmentChat.setArguments(bundle);
        return esaphSmileyViewBASEFragmentChat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            onSmileySelectedListenerChat
            = (EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat) bundle.getSerializable(EsaphSmileyViewBASEFragmentChat.extraInterfaceListener);

            int XML_RESOURCE = bundle.getInt(EsaphSmileyViewBASEFragmentChat.extraSmileysXML);
            esaphSmileyPickerAdapter = new EsaphSmileyPickerAdapterChat(EsaphSmileyViewBASEFragmentChat.this,
                    EsaphXMLSmileyParser.parse(getContext(), XML_RESOURCE));
        }
    }

    public EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat getOnSmileySelectedListenerChat() {
        return onSmileySelectedListenerChat;
    }

    public EsaphSmileyPickerAdapterChat getEsaphSmileyPickerAdapter()
    {
        return esaphSmileyPickerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_smiley_view, container, false);

        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), EsaphSmileyViewBASEFragmentChat.SPAN_COUNT);
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
