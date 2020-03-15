package esaph.spotlight.spots.SpotMaker;

import android.animation.Animator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphBubbleSeekBar.BubbleSeekBar;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.CanvasSpotGeneratorStatic;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontFamilyChooser;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontStyleChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontShaderChooser;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotFormatEditListener;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotMakerFormatTextDefault;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;

public class EsaphPloppMakerTextualFragment extends EsaphGlobalCommunicationFragment
{
    private EsaphColorSlider esaphColorSlider;
    private ChatPartner currentChatPartner;
    private EsaphSpotMakerListener esaphSpotMakerListener;
    private TextView textViewSent;
    private ImageView imageViewEditBackground;
    private ImageView imageViewEditText;
    private BubbleSeekBar seekBarFontSize;
    private RecyclerView recyclerViewHorizontalChooseEffect;
    private RelativeLayout relativeLayoutBottomEditingTools;
    private FrameLayout frameLayoutUnderOptions;
    private String transmittedTemplate;
    private SpotMakerView spotMakerView;
    private ImageView imageViewShare;
    private ImageView imageViewExport;

    public EsaphPloppMakerTextualFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    public static EsaphPloppMakerTextualFragment showWith(ChatPartner chatPartner,
                                                          EsaphSpotMakerListener esaphSpotMakerListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.KEY_CHAT_PARTNER_SER, chatPartner);
        bundle.putSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER, esaphSpotMakerListener);
        EsaphPloppMakerTextualFragment esaphPloppMakerTextual = new EsaphPloppMakerTextualFragment();
        esaphPloppMakerTextual.setArguments(bundle);
        return esaphPloppMakerTextual;
    }

    public static EsaphPloppMakerTextualFragment showWith(ChatPartner chatPartner,
                                                          EsaphSpotMakerListener esaphSpotMakerListener,
                                                          String PredefinedSpotInformations)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.KEY_CHAT_PARTNER_SER, chatPartner);
        bundle.putSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER, esaphSpotMakerListener);
        bundle.putString(PrivateChat.KEY_CHAT_PREDEFINED_SPOT_INFORMATION, PredefinedSpotInformations);
        EsaphPloppMakerTextualFragment esaphPloppMakerTextual = new EsaphPloppMakerTextualFragment();
        esaphPloppMakerTextual.setArguments(bundle);
        return esaphPloppMakerTextual;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            transmittedTemplate = bundle.getString(PrivateChat.KEY_CHAT_PREDEFINED_SPOT_INFORMATION, "");
            currentChatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.KEY_CHAT_PARTNER_SER);
            esaphSpotMakerListener = (EsaphSpotMakerListener) bundle.getSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_plopp_maker_textual, container, false);
        imageViewExport = rootView.findViewById(R.id.imageViewSave);
        imageViewShare = rootView.findViewById(R.id.imageViewShare);
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

        textViewSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sentTextMessage();
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

        imageViewExport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            ConversationMessage conversationMessage = spotMakerView.getSpotMessage();
                            conversationMessage.setMESSAGE_ID(System.currentTimeMillis());
                            System.out.println("SPOT MAKER THIS IS SHIT: " + conversationMessage.getEsaphPloppInformationsJSONString());


                            Bitmap bitmapRendered = CanvasSpotGeneratorStatic.createText(getContext(),
                                    conversationMessage,
                                    false, new EsaphDimension(500, 500));

                            if(bitmapRendered == null) return;


                            Context context = getContext();
                            if(context != null)
                            {
                                bitmapRendered = CanvasSpotGeneratorStatic.mark(bitmapRendered, context.getResources().getString(R.string.app_name),
                                        new Point(12, 12),
                                        ContextCompat.getColor(context, R.color.colorWhite),
                                        (int) (255*0.40f),
                                        DisplayUtils.dp2px(12),
                                        false);
                            }


                            File fileSaveLocation = new File(
                                    Environment.getExternalStorageDirectory()
                                            + File.separator + getResources().getString(R.string.app_name)
                                            + File.separator + getResources().getString(R.string.app_name) + "#" + System.currentTimeMillis() + ".jpg");
                            try
                            {
                                if(fileSaveLocation.createNewFile())
                                {
                                    FileOutputStream out = new FileOutputStream(fileSaveLocation);
                                    bitmapRendered.compress(Bitmap.CompressFormat.PNG, 100, out);

                                    Activity activity = getActivity();
                                    if(activity != null)
                                    {
                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.Images.Media.DATA, fileSaveLocation.getAbsolutePath());
                                        values.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
                                        activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                        Toast.makeText(activity, activity.getResources().getString(R.string.txt_chat_SHORT_Gespeichert),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            catch (Exception ec)
                            {
                                Log.i(getClass().getName(), "SpotMakerTextual_export created text failed: " + ec);
                            }
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "SpotMakerTextual_export created text failed second try: " + ec);
                        }
                    }
                });

                thread.start();
            }
        });

        imageViewShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            ConversationMessage conversationMessage = spotMakerView.getSpotMessage();
                            conversationMessage.setMESSAGE_ID(System.currentTimeMillis());


                            Bitmap bitmapRendered = CanvasSpotGeneratorStatic.createText(getContext(),
                                    conversationMessage,
                                    false, new EsaphDimension(500, 500));

                            if(bitmapRendered == null) return;


                            Context context = getContext();
                            if(context != null)
                            {
                                bitmapRendered = CanvasSpotGeneratorStatic.mark(bitmapRendered, context.getResources().getString(R.string.app_name),
                                        new Point(12, 12),
                                        ContextCompat.getColor(context, R.color.colorWhite),
                                        (int) (255*0.40f),
                                        DisplayUtils.dp2px(12),
                                        false);
                            }


                            File fileSaveLocation = new File(
                                    Environment.getExternalStorageDirectory()
                                            + File.separator + getResources().getString(R.string.app_name)
                                            + File.separator + getResources().getString(R.string.app_name) + "#" + System.currentTimeMillis() + ".jpg");
                            try
                            {
                                if(fileSaveLocation.createNewFile())
                                {
                                    FileOutputStream out = new FileOutputStream(fileSaveLocation);
                                    bitmapRendered.compress(Bitmap.CompressFormat.PNG, 100, out);

                                    Activity activity = getActivity();
                                    if(activity != null)
                                    {
                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.Images.Media.DATA, fileSaveLocation.getAbsolutePath());
                                        values.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
                                        final Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                        new Handler(Looper.getMainLooper()).post(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                Context context = getContext();
                                                if(isAdded() && context != null && uri != null)
                                                {
                                                    Intent shareIntent = new Intent();
                                                    shareIntent.setAction(Intent.ACTION_SEND);
                                                    shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.app_name));
                                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                    shareIntent.setType("image/jpeg");
                                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    startActivity(Intent.createChooser(shareIntent, "send"));
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            catch (Exception ec)
                            {
                                Log.i(getClass().getName(), "SpotMakerTextual_export_SHARE created text failed: " + ec);
                            }
                        }
                        catch (Exception ec)
                        {
                            Log.i(getClass().getName(), "SpotMakerTextual_export_SHARE created text failed second try: " + ec);
                        }
                    }
                });

                thread.start();
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontalChooseEffect.setLayoutManager(layoutManager);
        initViewValues();

        if(transmittedTemplate != null)
        {
            try
            {
                spotMakerView.from(new JSONObject(transmittedTemplate)).commit();
                spotMakerFormatNormal.getSpotMakerEdittext().setText(spotMakerView.obtainText().getText()); //Maybe recalculate gradient again?
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                try
                {
                    spotMakerView.obtainText().setTextSize(18); //To init json.
                    spotMakerView.obtainBackground(); //To init json.
                    spotMakerView.commit();
                }
                catch (Exception ec)
                {
                }
            }
        }
        else
        {
            try
            {
                spotMakerView.obtainText().setTextSize(18); //To init json.
                spotMakerView.obtainBackground(); //To init json.
                spotMakerView.commit();
            }
            catch (Exception ec)
            {
            }
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
                updateUIEditingPartMode(null);
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


    private void updateUIEditingPartMode(View viewSelected)
    {
        imageViewEditBackground.setBackground(null);
        imageViewEditText.setBackground(null);
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
        updateUIEditingPartMode(imageViewEditText);
        EsaphPloppEditingAnimationHelper.showColorPickerWithFontSizePicker(relativeLayoutBottomEditingTools);
        applyUnderOptionsLayout(R.layout.layout_spot_under_options_text);
    }

    private void sentTextMessage()
    {
        final ConversationMessage conversationMessage = spotMakerView.getSpotMessage();

        conversationMessage.setABS_ID(SpotLightLoginSessionHandler.getLoggedUID());
        conversationMessage.setID_CHAT(currentChatPartner.getUID_CHATPARTNER());
        conversationMessage.setAbsender(SpotLightLoginSessionHandler.getLoggedUsername());

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = null;
                try
                {
                    sqlChats = new SQLChats(getContext());
                    sqlChats.insertNewPrivateChatMessage((ChatTextMessage) conversationMessage, ConversationReceiverHelper.getReceiverFromMessage(conversationMessage));
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
                            esaphSpotMakerListener.onDoneEditingSent(conversationMessage);
                        }
                    }
                });
            }
        }).start();
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
            textViewSent.setAlpha(1.0f);
            textViewSent.setClickable(true);
        }
        else
        {
            textViewSent.setAlpha(0.5f);
            textViewSent.setClickable(false);
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
