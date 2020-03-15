/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphBubbleSeekBar.BubbleSeekBar;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerViewBASEFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerPickerFragmentDialog;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotMakerFormatStickerDefault;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerView;

public class EsaphPloppMakerStickerFragment extends EsaphGlobalCommunicationFragment
{
    public static final String KEY_SPOTLIGHT_STICKER_OBJECT = "esaph.spotlight.plopp.view.key.spotlightsticker";

    private ChatPartner currentChatPartner;
    private EsaphSpotLightSticker selectedEsaphSpotLightSticker;
    private EsaphSpotMakerListener esaphSpotMakerListener;

    private EsaphColorSlider esaphColorSlider;
    private ImageView imageViewEditBackground;
    private RelativeLayout relativeLayoutBottomEditingTools;
    private BubbleSeekBar seekBarFontSize;
    private SpotMakerView spotMakerView;
    private SpotMakerFormatStickerDefault spotMakerFormatStickerDefault;

    private TextView textViewSent;


    public EsaphPloppMakerStickerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    public static EsaphPloppMakerStickerFragment showWith(ChatPartner chatPartner,
                                                          EsaphSpotLightSticker esaphSpotLightSticker,
                                                          EsaphSpotMakerListener esaphSpotMakerListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.KEY_CHAT_PARTNER_SER, chatPartner);
        bundle.putSerializable(EsaphPloppMakerStickerFragment.KEY_SPOTLIGHT_STICKER_OBJECT, esaphSpotLightSticker);
        bundle.putSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER, esaphSpotMakerListener);
        EsaphPloppMakerStickerFragment esaphPloppMakerSticker = new EsaphPloppMakerStickerFragment();
        esaphPloppMakerSticker.setArguments(bundle);
        return esaphPloppMakerSticker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            currentChatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.KEY_CHAT_PARTNER_SER);
            selectedEsaphSpotLightSticker = (EsaphSpotLightSticker) bundle.getSerializable(EsaphPloppMakerStickerFragment.KEY_SPOTLIGHT_STICKER_OBJECT);
            esaphSpotMakerListener = (EsaphSpotMakerListener) bundle.getSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_plopp_maker_sticker, container, false);

        spotMakerView = rootView.findViewById(R.id.spotMakerView);
        textViewSent = rootView.findViewById(R.id.imageViewSent);
        esaphColorSlider = rootView.findViewById(R.id.esaphColorSliderSliding);
        imageViewEditBackground = rootView.findViewById(R.id.imageViewBackgroundColor);
        relativeLayoutBottomEditingTools = rootView.findViewById(R.id.relativLayoutBottomEditingTools);
        seekBarFontSize = rootView.findViewById(R.id.seekbarTextSize);
        rootView.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        textViewSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sentStickerMessage();
            }
        });

        imageViewEditBackground.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                applyElementEditingPartMode(ChooseEditingMode.MODE_BACKGROUND);
            }
        });

        textViewSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sentStickerMessage();
            }
        });

        seekBarFontSize.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener()
        {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser)
            {
                seekBarFontSize.correctOffsetWhenContainerOnScrolling();
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat)
            {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser)
            {

            }
        });

        spotMakerFormatStickerDefault = new SpotMakerFormatStickerDefault(getContext());
        spotMakerFormatStickerDefault.getImageViewStickerHolder().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showBottomFragment(EsaphStickerPickerFragmentDialog.getInstance(new EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog() {
                    @Override
                    public void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap)
                    {
                        selectedEsaphSpotLightSticker = esaphSpotLightSticker;
                        spotMakerFormatStickerDefault.setEsaphSpotLightSticker(selectedEsaphSpotLightSticker);
                    }
                }));
            }
        });

        spotMakerFormatStickerDefault.setEsaphSpotLightSticker(selectedEsaphSpotLightSticker);
        spotMakerView.setFormat(spotMakerFormatStickerDefault);

        spotMakerView.obtainSticker(); //To init json.
        spotMakerView.obtainBackground(); //To init json.
        spotMakerView.commit();
    }

    private ChooseEditingMode chooseEditingMode = ChooseEditingMode.NONE;
    private void applyElementEditingPartMode(ChooseEditingMode chooseEditingMode)
    {
        this.chooseEditingMode = chooseEditingMode;

        switch (this.chooseEditingMode)
        {
            case NONE:
                updateUIEditingPartMode(null);
                EsaphPloppEditingAnimationHelper.removeColorPicker(relativeLayoutBottomEditingTools);
                break;

            case MODE_BACKGROUND:
                applyBackgroundEditingMode();
                break;
        }
    }

    private void updateUIEditingPartMode(View viewSelected)
    {
        imageViewEditBackground.setBackground(null);
        Context context = getContext();
        if(context != null && viewSelected != null)
        {
            viewSelected.setBackground(ContextCompat.getDrawable(context, R.drawable.background_spot_editor_selected_under_option));
        }
    }

    private void applyBackgroundEditingMode()
    {
        esaphColorSlider.setListener(new EsaphColorSlider.OnColorSelectedListener()
        {
            @Override
            public void onColorChanged(int position, int color)
            {
                try
                {
                    spotMakerView.obtainBackground().setBackgroundColor(color);
                    spotMakerView.commit();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReleasedColorPicker() {

            }
        });

        updateUIEditingPartMode(imageViewEditBackground);
        EsaphPloppEditingAnimationHelper.showColorPickerWithFontSizePicker(relativeLayoutBottomEditingTools);
    }

    private BottomSheetDialogFragment currentBottomSheetDialogFragment;
    private void showBottomFragment(BottomSheetDialogFragment bottomSheetDialogFragment)
    {
        this.currentBottomSheetDialogFragment = bottomSheetDialogFragment;
        currentBottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getClass().getName());
    }

    private void sentStickerMessage()
    {
        final EsaphStickerChatObject esaphStickerChatObject = (EsaphStickerChatObject) spotMakerView.getSpotMessage();
        esaphStickerChatObject.setABS_ID(SpotLightLoginSessionHandler.getLoggedUID());
        esaphStickerChatObject.setID_CHAT(currentChatPartner.getUID_CHATPARTNER());
        esaphStickerChatObject.setAbsender(SpotLightLoginSessionHandler.getLoggedUsername());

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = null;
                try
                {
                    sqlChats = new SQLChats(getContext());
                    sqlChats.insertNewSticker(esaphStickerChatObject, ConversationReceiverHelper.getReceiverFromMessage(esaphStickerChatObject));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(sqlChats != null)
                    {
                        sqlChats.close();
                    }
                }

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(esaphSpotMakerListener != null)
                        {
                            esaphSpotMakerListener.onDoneEditingSent(esaphStickerChatObject);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
