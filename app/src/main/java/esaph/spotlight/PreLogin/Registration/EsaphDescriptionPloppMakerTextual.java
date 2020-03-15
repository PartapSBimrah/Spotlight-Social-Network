/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin.Registration;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphBubbleSeekBar.BubbleSeekBar;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontFamilyChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontShaderChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontStyleChooser;
import esaph.spotlight.spots.SpotMaker.ChooseEditingMode;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.EsaphPloppEditingAnimationHelper;
import esaph.spotlight.spots.SpotMaker.EsaphPloppMakerTextualFragment;
import esaph.spotlight.spots.SpotMaker.EsaphSpotMakerListener;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotFormatEditListener;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotMakerFormatTextDefault;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;

public class EsaphDescriptionPloppMakerTextual extends EsaphGlobalCommunicationFragment
{
    private EsaphColorSlider esaphColorSlider;
    private ChatPartner currentChatPartner;
    private EsaphSpotMakerListener esaphSpotMakerListener;
    private TextView textViewSent;
    private ImageView imageViewEditBackground;
    private ImageView imageViewEditText;
    private TextView textViewDone;
    private BubbleSeekBar seekBarFontSize;
    private RelativeLayout relativeLayoutBottomEditingTools;
    private RecyclerView recyclerViewHorizontalChooseEffect;
    private FrameLayout frameLayoutUnderOptions;
    private String transmittedTemplate;
    private SpotMakerView spotMakerView;

    public EsaphDescriptionPloppMakerTextual()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            ConversationMessage conversationMessage = spotMakerView.getSpotMessage();
            conversationMessage.setMESSAGE_ID(System.currentTimeMillis());

            if(conversationMessage instanceof ChatTextMessage)
            {
                ChatTextMessage chatTextMessage = (ChatTextMessage) conversationMessage;
                JSONObject jsonObject = chatTextMessage.getEsaphPloppInformationsJSON();

                Context context = getContext();
                if(context != null)
                {
                    SpotTextDefinitionBuilder.create(jsonObject).setText(chatTextMessage.getTextMessage());
                    new CLPreferences(getContext()).setLastEditedDescription((jsonObject));
                }
            }
        }
        catch (Exception ec)
        {
        }
    }


    public static EsaphDescriptionPloppMakerTextual show()
    {
        return new EsaphDescriptionPloppMakerTextual();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof EsaphSpotMakerListener)
        {
            esaphSpotMakerListener = (EsaphSpotMakerListener) context;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_description_maker_textual, container, false);

        textViewDone = rootView.findViewById(R.id.textViewDone);
        spotMakerView = rootView.findViewById(R.id.spotMakerView);
        esaphColorSlider = rootView.findViewById(R.id.esaphColorSliderSliding);
        recyclerViewHorizontalChooseEffect = rootView.findViewById(R.id.recylerViewEffektChoose);
        frameLayoutUnderOptions = rootView.findViewById(R.id.frameLayoutUnderOptions);
        textViewSent = rootView.findViewById(R.id.imageViewSent);
        imageViewEditBackground = rootView.findViewById(R.id.imageViewBackgroundColor);
        imageViewEditText = rootView.findViewById(R.id.imageViewTextStyle);
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

        final SpotMakerFormatTextDefault spotMakerFormatNormal = new SpotMakerFormatTextDefault(getContext());
        spotMakerFormatNormal.setEditListener(new SpotFormatEditListener()
        {
            @Override
            public void onStartEditing(final SpotMakerEdittext spotMakerEdittext)
            {
                spotMakerEdittext.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Activity activity = getActivity();
                        if(activity != null)
                        {
                            activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
                            spotMakerEdittext.setFocusableInTouchMode(true);
                            spotMakerEdittext.requestFocus();

                            final InputMethodManager inputMethodManager = (InputMethodManager) activity
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);

                            if(inputMethodManager != null)
                            {
                                inputMethodManager.showSoftInput(spotMakerEdittext, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    }
                });

                applyElementEditingPartMode(ChooseEditingMode.MODE_TEXT);
            }

            @Override
            public void onViewTouchedOutsideBounds()
            {
                //Closing the keyboard when not the text was focused.
                Activity activity = getActivity();
                if(activity != null)
                {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = activity.getCurrentFocus();

                    if (view == null)
                    {
                        view = new View(activity);
                    }
                    view.clearFocus();

                    if(imm != null)
                    {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

                applyElementEditingPartMode(ChooseEditingMode.NONE);
            }
        });

        spotMakerFormatNormal.getSpotMakerEdittext().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                setUpButtonAllowSent();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spotMakerView.setFormat(spotMakerFormatNormal);

        imageViewEditText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                applyElementEditingPartMode(ChooseEditingMode.MODE_TEXT);
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

        textViewDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ChatTextMessage chatTextMessage = (ChatTextMessage) spotMakerView.getSpotMessage();
                if(chatTextMessage != null && chatTextMessage.getEsaphPloppInformationsJSON() != null
                 && chatTextMessage.getTextMessage() != null && !chatTextMessage.getTextMessage().isEmpty())
                {
                    onDescriptionEditDonte();
                }
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
                    spotMakerView.obtainText().setTextSize(progress);
                    spotMakerView.commit();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontalChooseEffect.setLayoutManager(layoutManager);
        initViewValues();

        try
        {
            Context context = getContext();
            if(context != null)
            {
                JSONObject jsonObject = new CLPreferences(getContext()).getLastEditedDescription();
                if(jsonObject == null || jsonObject.length() == 0)
                {
                    spotMakerView.obtainText(); //To init json.
                    spotMakerView.obtainBackground(); //To init json.
                    spotMakerView.commit();
                }
                else
                {
                    spotMakerView.from(jsonObject);
                    spotMakerView.commit();
                    spotMakerFormatNormal.getSpotMakerEdittext().setText(SpotTextDefinitionBuilder.getText(jsonObject));
                }
            }
            else
            {
                spotMakerView.obtainText(); //To init json.
                spotMakerView.obtainBackground(); //To init json.
                spotMakerView.commit();
            }
        }
        catch (Exception ec)
        {
            spotMakerView.obtainText(); //To init json.
            spotMakerView.obtainBackground(); //To init json.
            spotMakerView.commit();
        }
    }



    private void initViewValues()
    {
        seekBarFontSize.setProgress(seekBarFontSize.getProgress()); //Calling to set in jsonobject.
        esaphColorSlider.selectColor(esaphColorSlider.getSelectedColor()); //Calling to set in jsonobject.
    }

    private ChooseEditingMode currentChoosenEditingMode = ChooseEditingMode.NONE;
    private void applyElementEditingPartMode(ChooseEditingMode chooseEditingMode)
    {
        this.currentChoosenEditingMode = chooseEditingMode;

        switch (this.currentChoosenEditingMode)
        {
            case NONE:
                EsaphPloppEditingAnimationHelper.removeColorPicker(relativeLayoutBottomEditingTools);
                removeUnderLayoutOptions();
                break;

            case MODE_TEXT:
                applyTextEditingMode();
                break;

            case MODE_BACKGROUND:
                applyBackgroundEditingMode();
                break;
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

        EsaphPloppEditingAnimationHelper.showColorPicker(relativeLayoutBottomEditingTools);
        applyUnderOptionsLayout(R.layout.layout_spot_under_options_background);
    }

    private void applyTextEditingMode()
    {
        esaphColorSlider.setListener(new EsaphColorSlider.OnColorSelectedListener()
        {
            @Override
            public void onColorChanged(int position, int color)
            {
                try
                {
                    spotMakerView.obtainText().removeTextShader();
                    spotMakerView.obtainText().setTextColor(color);
                    spotMakerView.commit();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReleasedColorPicker()
            {
            }
        });
        EsaphPloppEditingAnimationHelper.showColorPickerWithFontSizePicker(relativeLayoutBottomEditingTools);
        applyUnderOptionsLayout(R.layout.layout_spot_under_options_text);
    }

    private void onDescriptionEditDonte()
    {
        try
        {
            final ConversationMessage conversationMessage = spotMakerView.getSpotMessage();
            conversationMessage.setMESSAGE_ID(System.currentTimeMillis());

            if(conversationMessage instanceof ChatTextMessage)
            {
                ChatTextMessage chatTextMessage = (ChatTextMessage) conversationMessage;
                JSONObject jsonObject = chatTextMessage.getEsaphPloppInformationsJSON();

                Context context = getContext();
                if(context != null)
                {
                    SpotTextDefinitionBuilder.create(jsonObject).setText(chatTextMessage.getTextMessage());
                    new CLPreferences(getContext()).setLastEditedDescription((jsonObject));
                }
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(esaphSpotMakerListener != null)
                    {
                        esaphSpotMakerListener.onDoneEditingSent(conversationMessage);
                        EsaphActivity esaphActivity = getEsaphActivity();
                        if(esaphActivity != null)
                        {
                            esaphActivity.onActivityDispatchBackPressEvent();
                        }
                    }
                }
            });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onDescriptionEditDonte() failed: " + ec);
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(viewCurrentSelected != null)
        {
            if(handleCurrentUnderOptionsSelectingMode(viewCurrentSelected)) recyclerViewHorizontalChooseEffect.setAdapter(null);
            return true;
        }
        else if(currentChoosenEditingMode != ChooseEditingMode.NONE)
        {
            applyElementEditingPartMode(ChooseEditingMode.NONE);
            return true;
        }
        return false;
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
            ImageView imageViewFontStyle = frameLayoutUnderOptions.findViewById(R.id.imageViewFontStyle);
            ImageView imageViewFontFamilie = frameLayoutUnderOptions.findViewById(R.id.imageViewFontFamilie);
            ImageView imageViewTextAlignment = frameLayoutUnderOptions.findViewById(R.id.imageViewTextAlignment);
            ImageView imageViewFontShaders = frameLayoutUnderOptions.findViewById(R.id.imageViewFontShaders);

            arrayViewsCanBeSelected = new View[]{imageViewFontStyle,
                    imageViewFontFamilie,
                    imageViewTextAlignment,
                    imageViewFontShaders};

            imageViewFontStyle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(handleCurrentUnderOptionsSelectingMode(v))
                        {
                            recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontStyleChooser(getContext(),
                                    SpotTextFontStyle.options().preview(getContext()),
                                    new SpotMakerAdapterFontStyleChooser.SpotMakerAdapterFontStyleClickListener()
                                    {
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
                        else
                        {
                            recyclerViewHorizontalChooseEffect.setAdapter(null);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            imageViewFontFamilie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(handleCurrentUnderOptionsSelectingMode(v))
                        {
                            recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontFamilyChooser(getContext(),
                                    SpotTextFontFamlie.options().preview(getContext()),
                                    new SpotMakerAdapterFontFamilyChooser.SpotMakerAdapterFontFamilyClickListener() {
                                        @Override
                                        public void onFamilySelected(String FONT_FAMILY) {
                                            try
                                            {
                                                spotMakerView.obtainText().setFontFamily(FONT_FAMILY);
                                                spotMakerView.commit();
                                            }
                                            catch (Exception ec)
                                            {

                                            }
                                        }
                                    }
                            ));
                        }
                        else
                        {
                            recyclerViewHorizontalChooseEffect.setAdapter(null);
                        }
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "FrameLayoutUnderOptions fontfamilie: " + ec);
                    }
                }
            });

            imageViewTextAlignment.setOnClickListener(new View.OnClickListener()
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

            imageViewFontShaders.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(handleCurrentUnderOptionsSelectingMode(v))
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
                        else
                        {
                            recyclerViewHorizontalChooseEffect.setAdapter(null);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void removeUnderLayoutOptions()
    {
        frameLayoutUnderOptions.removeAllViews();
        recyclerViewHorizontalChooseEffect.setAdapter(null);
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

    private void setUpButtonAllowSent()
    {
        ChatTextMessage chatTextMessage = (ChatTextMessage) spotMakerView.getSpotMessage();
        if(chatTextMessage != null && chatTextMessage.getEsaphPloppInformationsJSON() != null
                && chatTextMessage.getTextMessage() != null && !chatTextMessage.getTextMessage().isEmpty())
        {
            textViewDone.setAlpha(1.0f);
            textViewDone.setClickable(true);
        }
        else
        {
            textViewDone.setAlpha(0.5f);
            textViewDone.setClickable(false);
        }
    }



    private View viewCurrentSelected;
    private View[] arrayViewsCanBeSelected;
    private void onSelectedEditingTextEditing(final View viewSelected)
    {
        System.out.println("DEBUG TANSLATE: TOP OF LAYOUT = " + frameLayoutUnderOptions.getTop());
        System.out.println("DEBUG TANSLATE: TOP OF VIEW = " + viewSelected.getTop());

        int TRANSLATION_FOR_SELECTED_CHILD = -(viewSelected.getTop() - frameLayoutUnderOptions.getTop())-viewSelected.getHeight();
        System.out.println("DEBUG TANSLATE: OFFSET = " + TRANSLATION_FOR_SELECTED_CHILD);
        int length = arrayViewsCanBeSelected.length;
        for(int counter = 0; counter < length; counter++)
        {
            View view = arrayViewsCanBeSelected[counter];
            if(viewSelected == view) continue;

            view.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .start();
        }


        viewSelected.animate()
                .translationY(TRANSLATION_FOR_SELECTED_CHILD)
                .setDuration(150)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        Context context = getContext();
                        if(context != null)
                        {
                            viewSelected.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_spot_editor_selected_under_option));
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        viewCurrentSelected = viewSelected;
    }

    private void onDESelectedEditingTextEditing(final View viewUnselected)
    {
        int length = arrayViewsCanBeSelected.length;
        for(int counter = 0; counter < length; counter++)
        {
            View view = arrayViewsCanBeSelected[counter];
            if(viewUnselected == view) continue;

            view.animate()
                    .alpha(1.0f)
                    .setDuration(100)
                    .start();
        }


        viewUnselected.animate()
                .translationY(0)
                .setDuration(150)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        viewUnselected.setBackground(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {
                        viewUnselected.setBackground(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        viewCurrentSelected = null;
    }


    private boolean handleCurrentUnderOptionsSelectingMode(View viewToHandle)
    {
        if(viewToHandle.getTag() != null && viewToHandle.getTag().equals(Boolean.TRUE))
        {
            viewToHandle.setTag(Boolean.FALSE);
            onDESelectedEditingTextEditing(viewToHandle);
            return false;
        }
        else
        {
            viewToHandle.setTag(Boolean.TRUE);
            onSelectedEditingTextEditing(viewToHandle);
            return true;
        }
    }


}
