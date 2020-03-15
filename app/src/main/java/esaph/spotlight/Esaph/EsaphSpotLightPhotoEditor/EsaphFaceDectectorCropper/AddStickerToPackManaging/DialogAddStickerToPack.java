package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.Background.AsyncLoadStickersFromServer;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class DialogAddStickerToPack extends Dialog
{
    private final static int SPAN_COUNT = 1;
    private AdapterShowStickerPacksForAdding adapterShowStickerPacks;
    private EditText editTextStickerPackName;
    private LinearLayout linearLayoutShowNoStickerPacks;
    private RecyclerView recyclerView;
    private EsaphSpotLightSticker esaphSpotLightStickerToAdd;
    private ItemSelectListener itemSelectListener;

    public DialogAddStickerToPack(@NonNull Context context,
                                  EsaphSpotLightSticker esaphSpotLightSticker,
                                  ItemSelectListener itemSelectListener)
    {
        super(context);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }

        this.itemSelectListener = itemSelectListener;
        this.esaphSpotLightStickerToAdd = esaphSpotLightSticker;
    }

    public interface ItemSelectListener
    {
        void onSelectionChanged(int totalCount);
    }

    public DialogAddStickerToPack(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }
    }

    public DialogAddStickerToPack(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }
    }

    public AdapterShowStickerPacksForAdding getAdapterShowStickerPacks()
    {
        return adapterShowStickerPacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_add_sticker_to_pack);

        editTextStickerPackName = (EditText) findViewById(R.id.editTextSearchOrCreateNewStickerPackName);
        editTextStickerPackName.setFilters(new InputFilter[]{new InputFilter()
        {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if(source.length() > 0)
                {
                    if((!editTextStickerPackName.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase("\n"))
                            || (!editTextStickerPackName.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase(" ")))
                    {
                        onStartNewStickerPack();
                        return "";
                    }
                    else if((editTextStickerPackName.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase("\n"))
                            || (editTextStickerPackName.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase(" ")))
                    {
                        return "";
                    }
                }
                return source;
            }
        }, new InputFilter.LengthFilter(30)});


        editTextStickerPackName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                adapterShowStickerPacks.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        linearLayoutShowNoStickerPacks = (LinearLayout) findViewById(R.id.linearLayoutEsaphNoStickerPacks);
        recyclerView = (RecyclerView) findViewById(R.id.recylerViewShowStickerPacks);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), DialogAddStickerToPack.SPAN_COUNT);
        recyclerView.setLayoutManager(gridLayoutManagerVertical);

        Activity activity = getOwnerActivity();
        if(activity instanceof SwipeNavigation)
        {
            adapterShowStickerPacks = new AdapterShowStickerPacksForAdding((SwipeNavigation) activity,
                    linearLayoutShowNoStickerPacks,
                    this,
                    esaphSpotLightStickerToAdd,
                    itemSelectListener);

            recyclerView.setAdapter(adapterShowStickerPacks);
        }

        loadStickerPacks();
    }


    private void loadStickerPacks()
    {
        SQLSticker sqlSticker = new SQLSticker(getContext());
        List<EsaphSpotLightStickerPack> esaphSpotLightStickerPacksList = sqlSticker.getAllStickerPackLimiteOrderByTime();
        sqlSticker.close();

        Activity activity = getOwnerActivity();

        if(activity instanceof SwipeNavigation)
        {
            if(esaphSpotLightStickerPacksList.isEmpty())
            {
                linearLayoutShowNoStickerPacks.setVisibility(View.VISIBLE);

                new AsyncLoadStickersFromServer(getContext(), new AsyncLoadStickersFromServer.OnStickerPackSynchronizedListener() {
                    @Override
                    public void onStickerPackSynchronized() {
                        loadStickerPacks();
                    }
                }).execute();
            }
            else
            {
                linearLayoutShowNoStickerPacks.setVisibility(View.GONE);
            }

            adapterShowStickerPacks.clear();
            adapterShowStickerPacks.addAll(esaphSpotLightStickerPacksList);
        }
        else
        {
            dismiss();
        }
    }

    public void onStartNewStickerPack()
    {
        //ADD THE LAST HASHTAG WHICH WAS EDITTED TO SPARSEARRAY!!
        try
        {
            String PACK_NAME_NEW = editTextStickerPackName.getText().toString();

            List<EsaphSpotLightSticker> stickers = new ArrayList<>();
            stickers.add(esaphSpotLightStickerToAdd);

            EsaphSpotLightStickerPack esaphSpotLightStickerPack = new EsaphSpotLightStickerPack(
                    PACK_NAME_NEW,
                    System.currentTimeMillis(),
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    System.currentTimeMillis(),
                    stickers);

            adapterShowStickerPacks.getSelectedItems().add(esaphSpotLightStickerPack);
            adapterShowStickerPacks.insertNew(esaphSpotLightStickerPack);

            editTextStickerPackName.setText("");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onStartNewStickerPack() failed: " + ec);
        }
    }
}
