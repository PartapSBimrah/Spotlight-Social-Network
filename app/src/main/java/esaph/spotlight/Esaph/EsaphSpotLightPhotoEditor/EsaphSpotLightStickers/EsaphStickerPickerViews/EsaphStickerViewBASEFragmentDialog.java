package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphStickerViewBASEFragmentDialog extends EsaphGlobalCommunicationFragment
{
    private static final String KEY_PACK_LIST_PARCEL = "esaph.spotlight.parcel.esaphstickerpack";
    private static final String KEY_STICKER_SELECT_INTERFACE = "esaph.spotlight.parcel.interface.stickerselectlistener";
    private final static int SPAN_COUNT = 5;
    private RecyclerView recylerView;
    private EsaphSpotLightStickerPack esaphSpotLightStickerPack;
    public EsaphStickerPickerAdapter esaphStickerPickerAdapter;
    private OnStickerSelectedListenerDialog onStickerSelectedListener;


    public EsaphStickerViewBASEFragmentDialog()
    {
        // Required empty public constructor
    }

    public static EsaphStickerViewBASEFragmentDialog getInstance(EsaphSpotLightStickerPack packList,
                                                                 OnStickerSelectedListenerDialog onStickerSelectedListener)
    {
        EsaphStickerViewBASEFragmentDialog fragment = new EsaphStickerViewBASEFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphStickerViewBASEFragmentDialog.KEY_PACK_LIST_PARCEL, packList);
        bundle.putSerializable(EsaphStickerViewBASEFragmentDialog.KEY_STICKER_SELECT_INTERFACE, onStickerSelectedListener);
        fragment.setArguments(bundle);

        return fragment;
    }


    public interface OnStickerSelectedListenerDialog extends Serializable
    {
        void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap);
    }

    public OnStickerSelectedListenerDialog getOnStickerSelectedListener() {
        return onStickerSelectedListener;
    }

    public EsaphStickerPickerAdapter getEsaphStickerPickerAdapter()
    {
        return esaphStickerPickerAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            esaphSpotLightStickerPack = (EsaphSpotLightStickerPack) bundle.getSerializable(
                    EsaphStickerViewBASEFragmentDialog.KEY_PACK_LIST_PARCEL);

            onStickerSelectedListener = (OnStickerSelectedListenerDialog) bundle.getSerializable(EsaphStickerViewBASEFragmentDialog.KEY_STICKER_SELECT_INTERFACE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        esaphStickerPickerAdapter = new EsaphStickerPickerAdapter(EsaphStickerViewBASEFragmentDialog.this,
                esaphSpotLightStickerPack.getEsaphSpotLightStickers());

        recylerView.setAdapter(esaphStickerPickerAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_smiley_view, container, false);
        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), EsaphStickerViewBASEFragmentDialog.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);
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
