/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphBubbleSeekBar.BubbleSeekBar;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.EsaphTrashbinView;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontShaderChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontStyleChooser;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.EsaphSmileyPickerFragmentPlopp;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotMakerFormatEmojieDefault;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerView;

public class EsaphPloppMakerEmojieFragment extends EsaphGlobalCommunicationFragment implements EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener
{
    public static final String KEY_SPOT_MAKER_CHOOSEN_EMOJIE_OBJECT = "esaph.spotlight.plopp.view.key.esaphemojie";
    private EsaphTrashbinView esaphTrashbinView;
    private EsaphColorSlider esaphColorSlider;
    private ChatPartner currentChatPartner;
    private EsaphEmojie selectedEsaphEmojie;
    private EsaphSpotMakerListener esaphSpotMakerListener;
    private TextView textViewSent;
    private RelativeLayout relativeLayoutBottomEditingTools;
    private ImageView imageViewEditBackground;
    private BubbleSeekBar seekBarFontSize;
    private SpotMakerView spotMakerView;
    private RecyclerView recyclerViewHorizontalChooseEffect;
    private FrameLayout frameLayoutUnderOptions;
    private SpotMakerFormatEmojieDefault spotMakerFormatEmojieDefault;


    private boolean isColorPickerDisplayed = false;

    public EsaphPloppMakerEmojieFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    public static EsaphPloppMakerEmojieFragment showWith(ChatPartner chatPartner, EsaphEmojie esaphEmojie,
                                                         EsaphSpotMakerListener esaphSpotMakerListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.KEY_CHAT_PARTNER_SER, chatPartner);
        bundle.putSerializable(EsaphPloppMakerEmojieFragment.KEY_SPOT_MAKER_CHOOSEN_EMOJIE_OBJECT, esaphEmojie);
        bundle.putSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER, esaphSpotMakerListener);
        EsaphPloppMakerEmojieFragment esaphPloppMakerEmojie = new EsaphPloppMakerEmojieFragment();
        esaphPloppMakerEmojie.setArguments(bundle);
        return esaphPloppMakerEmojie;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            currentChatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.KEY_CHAT_PARTNER_SER);
            selectedEsaphEmojie = (EsaphEmojie) bundle.getSerializable(EsaphPloppMakerEmojieFragment.KEY_SPOT_MAKER_CHOOSEN_EMOJIE_OBJECT);
            esaphSpotMakerListener = (EsaphSpotMakerListener) bundle.getSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_plopp_maker_emojie, container, false);
        spotMakerView = rootView.findViewById(R.id.spotMakerView);
        recyclerViewHorizontalChooseEffect = rootView.findViewById(R.id.recylerViewEffektChoose);
        frameLayoutUnderOptions = rootView.findViewById(R.id.frameLayoutUnderOptions);
        textViewSent = rootView.findViewById(R.id.imageViewSent);
        esaphTrashbinView = rootView.findViewById(R.id.esaphTrashbinView);
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showBottomFragment(EsaphSmileyPickerFragmentPlopp.getInstance(), "SmileyPickerSpotMakerEmojie");
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
                sentEmojieMessage();
            }
        });

        seekBarFontSize.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener()
        {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser)
            {
                seekBarFontSize.correctOffsetWhenContainerOnScrolling();

                try
                {
                    spotMakerView.obtainEmojie().setTextSize(progress);
                    spotMakerView.commit();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        spotMakerFormatEmojieDefault = new SpotMakerFormatEmojieDefault(getContext());
        spotMakerFormatEmojieDefault.getSpotMakerEdittextEmojie().setText(selectedEsaphEmojie.getEMOJIE());
        spotMakerView.setFormat(spotMakerFormatEmojieDefault);

        spotMakerView.obtainEmojie(); //To init json.
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
        EsaphPloppEditingAnimationHelper.showColorPickerWithFontSizePicker(relativeLayoutBottomEditingTools); //With font, because there is only one icon.
        applyUnderOptionsLayout(R.layout.layout_spot_under_options_background);
    }



    private void applyUnderOptionsLayout(@LayoutRes int LAYOUT_ID)
    {
        recyclerViewHorizontalChooseEffect.setAdapter(null);
        frameLayoutUnderOptions.removeAllViews();
        frameLayoutUnderOptions.addView(LayoutInflater.from(getContext()).inflate(LAYOUT_ID, null, false));

        if(LAYOUT_ID == R.layout.layout_spot_under_options_background)
        {
            frameLayoutUnderOptions.findViewById(R.id.imageViewSpotFormat).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            frameLayoutUnderOptions.findViewById(R.id.imageViewFontShaders).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
        else if(LAYOUT_ID == R.layout.layout_spot_under_options_text)
        {
            frameLayoutUnderOptions.findViewById(R.id.imageViewFontStyle).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontStyleChooser(getContext(),
                                SpotTextFontStyle.options().preview(getContext()),
                                new SpotMakerAdapterFontStyleChooser.SpotMakerAdapterFontStyleClickListener() {
                                    @Override
                                    public void onStyleClicked(int FONT_STYLE)
                                    {
                                        try
                                        {
                                            spotMakerView.obtainText().setFontStyle(FONT_STYLE);
                                            spotMakerView.commit();
                                        }
                                        catch (Exception ec)
                                        {

                                        }
                                    }
                                }));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            frameLayoutUnderOptions.findViewById(R.id.imageViewTextAlignment).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    try
                    {
                        currentTextAlignment = getNextTextAlignment(currentTextAlignment);
                        switch (currentTextAlignment)
                        {
                            case IT_LEFT:
                                spotMakerView.obtainText().setTextAlignment(SpotTextAlignment.ALIGNMENT_START);
                                Glide.with(v.getContext()).load(R.drawable.ic_left_alignment).into(new SimpleTarget<Drawable>()
                                {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                    {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                        {
                                            ((ImageView)v).setImageDrawable(resource);
                                        }
                                    }
                                });
                                break;

                            case IT_CENTER:
                                spotMakerView.obtainText().setTextAlignment(SpotTextAlignment.ALIGNMENT_CENTER);
                                Glide.with(v.getContext()).load(R.drawable.ic_center_alignment).into(new SimpleTarget<Drawable>()
                                {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                    {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                        {
                                            ((ImageView)v).setImageDrawable(resource);
                                        }
                                    }
                                });
                                break;

                            case IT_RIGHT:
                                spotMakerView.obtainText().setTextAlignment(SpotTextAlignment.ALIGNMENT_END);
                                Glide.with(v.getContext()).load(R.drawable.ic_right_alignment).into(new SimpleTarget<Drawable>()
                                {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                    {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                        {
                                            ((ImageView)v).setImageDrawable(resource);
                                        }
                                    }
                                });
                                break;
                        }
                        spotMakerView.commit();
                    }
                    catch (Exception ec)
                    {
                    }
                }
            });

            frameLayoutUnderOptions.findViewById(R.id.imageViewFontShaders).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontShaderChooser(getContext(),
                                SpotTextShader.options().preview(getContext()),
                                new SpotMakerAdapterFontShaderChooser.SpotMakerAdapterFontShaderChooserClickListener()
                                {
                                    @Override
                                    public void onShaderClicked(JSONObject jsonObjectShader)
                                    {
                                        EsaphPloppEditingAnimationHelper.showColorPicker(relativeLayoutBottomEditingTools);
                                        try
                                        {
                                            spotMakerView.obtainText().setTextShader(SpotTextDefinitionBuilder.getTextShader(jsonObjectShader));
                                            spotMakerView.commit();
                                        } catch (Exception e) {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }

                                    @Override
                                    public void onShaderLongClicked()
                                    {
                                        try
                                        {

                                        }
                                        catch (Exception e)
                                        {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }

                                    @Override
                                    public void onRemoveAllShaders()
                                    {
                                        try
                                        {
                                            spotMakerView.obtainText().removeTextShader();
                                            spotMakerView.commit();
                                        }
                                        catch (Exception e)
                                        {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }
                                }));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private enum TextAlignment
    {
        IT_LEFT,
        IT_CENTER,
        IT_RIGHT
    }

    private TextAlignment currentTextAlignment = TextAlignment.IT_CENTER;
    private TextAlignment[] textAlignments = TextAlignment.values();
    private int modeCountTextAlignment = textAlignments.length;

    private TextAlignment getNextTextAlignment(TextAlignment textAlignment)
    {
        int nextModeOrdinal = (textAlignment.ordinal() + 1) % modeCountTextAlignment;
        return textAlignments[nextModeOrdinal];
    }

    private BottomSheetDialogFragment currentBottomSheetDialogFragment;
    private void showBottomFragment(BottomSheetDialogFragment bottomSheetDialogFragment, String tag)
    {
        if(currentBottomSheetDialogFragment != null && currentBottomSheetDialogFragment.isAdded()) return;

        this.currentBottomSheetDialogFragment = bottomSheetDialogFragment;
        currentBottomSheetDialogFragment.show(getChildFragmentManager(), tag);
    }

    private void sentEmojieMessage()
    {
        final EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = (EsaphAndroidSmileyChatObject) spotMakerView.getSpotMessage();

        esaphAndroidSmileyChatObject.setABS_ID(SpotLightLoginSessionHandler.getLoggedUID());
        esaphAndroidSmileyChatObject.setID_CHAT(currentChatPartner.getUID_CHATPARTNER());
        esaphAndroidSmileyChatObject.setAbsender(SpotLightLoginSessionHandler.getLoggedUsername());

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = null;
                try
                {
                    sqlChats = new SQLChats(getContext());
                    sqlChats.insertNewEmojieMessage(esaphAndroidSmileyChatObject, ConversationReceiverHelper.getReceiverFromMessage(esaphAndroidSmileyChatObject));
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
                            esaphSpotMakerListener.onDoneEditingSent(esaphAndroidSmileyChatObject);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(currentBottomSheetDialogFragment != null && currentBottomSheetDialogFragment.isAdded())
        {
            currentBottomSheetDialogFragment.dismiss();
            return true;
        }
        return false;
    }



    @Override
    public void onSmileySelected(EsaphEmojie esaphEmojie)
    {
        spotMakerFormatEmojieDefault.getSpotMakerEdittextEmojie().setText(esaphEmojie.getEMOJIE());
        if(currentBottomSheetDialogFragment != null)
        {
            currentBottomSheetDialogFragment.dismiss();
        }
    }
}
