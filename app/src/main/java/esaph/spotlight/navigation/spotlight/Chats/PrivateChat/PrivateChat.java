/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.Esaph.EsaphDialogBubbly.EsaphDialog;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphListView.EsaphListViewKeepPosition;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.EsaphStickerPickerAdapterChat;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.EsaphStickerPickerFragmentChat;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.AsyncDeletePrivateMomentPost;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveSinglePostFromPrivateUser;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.spotlight.Chats.EndlessScrollListenerDirectionTop;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncLoadAllPrivateMessages;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncSendUpdateTypingState;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.AsyncSynchAktuellePostingsOnlyFromPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.LoadedConversationMessagesListener;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.OnSynchAktullePostingsPartnerListener;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.RunnableChatOpenened;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.RunnableRemoveAllChatMessagesFromUsers;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Dialogs.DialogPrivateChatBlockedInfo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat.EsaphSmileyPickerAdapterChat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat.EsaphSmileyPickerFragmentChat;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background.SynchPrivateMomentsBetweenUsers;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.ChatTemplatesView.ChatTemplatesView;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.TodayOurStory.TodayOurStory;
import esaph.spotlight.services.NotificationAndMessageHandling.ActivityMessageHandlerCallBack;
import esaph.spotlight.services.NotificationAndMessageHandling.GlobalNotificationDisplayer;
import esaph.spotlight.services.SpotLightMessageConnection.MessageServiceCallBacks;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UndeliveredMessageHandling.UndeliveredChatSeenMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotEmojieDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotsStickerDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.EsaphPloppMakerAudioFragment;
import esaph.spotlight.spots.SpotMaker.EsaphPloppMakerEmojieFragment;
import esaph.spotlight.spots.SpotMaker.EsaphPloppMakerStickerFragment;
import esaph.spotlight.spots.SpotMaker.EsaphPloppMakerTextualFragment;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.EsaphSmileyPickerFragmentPlopp;
import esaph.spotlight.spots.SpotMaker.EsaphSpotMakerListener;
import esaph.spotlight.spots.SpotMaker.FragmentFinishListener;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotTextRenderView;
import esaph.spotlight.spots.SpotViewFullScreen.EsaphPloppViewFragment;

public class PrivateChat extends EsaphActivity implements
        ILoader,
        AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener,
        EsaphSpotMakerListener,
        MessageServiceCallBacks,
        EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener,
        EsaphStickerPickerAdapterChat.OnStickerSelectedListenerChat,
        EsaphSmileyPickerAdapterChat.OnSmileySelectedListenerChat,
        FragmentFinishListener,
        ChatTemplatesView.TemplateChatSelectedListener,
        EsaphPloppViewFragment.ShowNewMessagesFinishListener
{
    public static final String KEY_CHAT_PARTNER_SER = "esaph.spotlight.plopp.view.key.chatpartner";
    public static final String KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER = "esaph.spotlight.plopp.view.key.interface.spotmakerfinishlistener";
    public static final String KEY_CHAT_PREDEFINED_SPOT_INFORMATION = "esaph.spotlight.plopp.view.key.spotinformation.predefined";

    private Handler handlerUI = new Handler(Looper.getMainLooper());
    private Handler handlerUIUserTyping = new Handler(Looper.getMainLooper());

    private final ThreadPoolExecutor threadPoolExecutorLoadMessagesQueue = new ThreadPoolExecutor(1,
            1,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static final String extraChatPartner = "esaph.spotlight.chat.chatpartner";
    public static final int REQUEST_CODE_FINISHED = 186;

    private TextView textViewName;
    private ImageView imageViewBack;
    private SpotTextRenderView spotTextRenderView;
    private EsaphCircleImageView imageViewProfilbild;
    private ArrayAdapterPrivateChat arrayAdapterPrivateChat;
    private EsaphListViewKeepPosition esaphListViewKeepPosition;
    private EsaphColorableEdittext esaphColorableEdittextInput;
    private EsaphColorSlider esaphColorSlider;
    private ImageView imageViewSent;
    private TextView textViewVorname;
    private FrameLayout frameLayoutKeyBoardView;
    private ImageView imageViewOptions;

    private ImageView imageViewOpenStickerBottomKeyboardPicker;
    private ImageView imageViewOpenEmojieBottomKeyboardPicker;
    private ImageView imageViewOpenAudioEditor;
    private ImageView imageViewOpenTemplateKeyBoardPicker;

    private LinearLayout linearLayoutMoreOptions;


    private View spotChooseBackgroundColor;
    private View spotChooseTextColor;

    private ChatPartner chatPartner;


    private GestureDetector gestureDetector;

    @Override
    public boolean onActivityDispatchBackPressEvent()
    {
        if(currentBottomFragmentFullscreen != null && currentBottomFragmentFullscreen.isVisible())
        {
            if(!currentBottomFragmentFullscreen.onActivityDispatchedBackPressed())
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                        .remove(currentBottomFragmentFullscreen)
                        .commit();
            }
            return true; //Always true, because if disptach is false, its consumend at top, and when top is true, it is consumend in bottomFragmentFullscreen.
        }
        else
        {
            if(frameLayoutKeyBoardView.getVisibility() == View.VISIBLE)
            {
                removeKeyBoardFragment();
                return true;
            }
            else
            {
                killSelf();
                return true;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!onActivityDispatchBackPressEvent())
        {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    public ChatPartner getChatPartner()
    {
        return chatPartner;
    }

    private View viewChooseColorPalette;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_private_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
        imageViewOptions = (ImageView) findViewById(R.id.imageViewMenuChat);
        textViewVorname = findViewById(R.id.textViewAtVorname);
        linearLayoutMoreOptions = findViewById(R.id.linearLayoutMoreOptions);
        spotChooseBackgroundColor = findViewById(R.id.viewChooseBackground);
        spotChooseTextColor = findViewById(R.id.viewChooseTextColor);
        esaphListViewKeepPosition = findViewById(R.id.listViewKeepPosition);
        textViewName = (TextView) findViewById(R.id.textViewTopName);
        spotTextRenderView = (SpotTextRenderView) findViewById(R.id.spotTextRenderViewDescription);
        imageViewBack = (ImageView) findViewById(R.id.imageViewChatBack);
        imageViewProfilbild = (EsaphCircleImageView) findViewById(R.id.imageViewPrivateChatTodayStory);
        esaphColorableEdittextInput = findViewById(R.id.editTextWriteText);
        esaphColorSlider = findViewById(R.id.esaphColorSliderSliding);
        frameLayoutKeyBoardView = findViewById(R.id.frameLayoutKeyBoardFragmentChat);
        imageViewSent = findViewById(R.id.imageViewSent);
        imageViewOpenStickerBottomKeyboardPicker = findViewById(R.id.imageViewSentSticker);
        imageViewOpenTemplateKeyBoardPicker = findViewById(R.id.imageViewTemplatePicker);
        imageViewOpenEmojieBottomKeyboardPicker = findViewById(R.id.imageViewSentEmojie);
        imageViewOpenAudioEditor = findViewById(R.id.imageViewSentAudio);
        viewChooseColorPalette = findViewById(R.id.viewChooseColorPalettePresets);

        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                this.chatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.extraChatPartner);
            }
        }

        viewChooseColorPalette.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                generateRandomColors();
            }
        });

        esaphColorableEdittextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s != null && !s.toString().isEmpty())
                {
                    Context context = getApplicationContext();
                    if(context != null)
                    {
                        Glide.with(context).load(R.drawable.ic_sent_message).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    imageViewSent.setImageDrawable(resource);
                                }
                            }
                        });
                    }

                    if(!postedMessageStayAlive)
                    {
                        postedMessageStayAlive = true;
                        handlerUIUserTyping.removeCallbacks(runnableCode);
                        handlerUIUserTyping.post(runnableCode);
                    }
                }
                else
                {
                    Context context = getApplicationContext();
                    if(context != null)
                    {
                        Glide.with(context).load(R.drawable.ic_plus_open_chat).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    imageViewSent.setImageDrawable(resource);
                                }
                            }
                        });
                    }

                    if(postedMessageStayAlive)
                    {
                        handlerUIUserTyping.removeCallbacks(runnableCode);
                        postedMessageStayAlive = false;
                        new Thread(new AsyncSendUpdateTypingState(getApplicationContext(),
                                chatPartner.getUID_CHATPARTNER(),
                                false)).start();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        esaphColorableEdittextInput.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        imageViewOptions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imageViewOptions);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.chatItemDeleteAll:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                                dialog.setTitle(PrivateChat.this.getResources().getString(R.string.txt_clear_Chat));
                                dialog.setMessage(PrivateChat.this.getResources().getString(R.string.txt_info_clear_chat));
                                dialog.setNegativeButton(R.string.txt_Abbrechen, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setPositiveButton(R.string.txt_remove, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        new Thread(new RunnableRemoveAllChatMessagesFromUsers(
                                                PrivateChat.this,
                                                chatPartner.getUID_CHATPARTNER())).start();
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_chat);
                popupMenu.show();
            }
        });


        imageViewSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(esaphColorableEdittextInput.getText() != null &&
                        !esaphColorableEdittextInput.getText().toString().isEmpty())
                {
                    sendTextMessage();
                }
                else
                {
                    handleOptionMenuClick();
                }
            }
        });

        textViewVorname.setText(getResources().getString(R.string.txt_at_placeholder, chatPartner.getVorname()));


        spotChooseTextColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                esaphColorSlider.selectColor(esaphColorableEdittextInput.getCurrentTextColor());

                openColorSlider();
                esaphColorSlider.setListener(new EsaphColorSlider.OnColorSelectedListener()
                {
                    @Override
                    public void onColorChanged(int position, int color)
                    {
                        esaphColorableEdittextInput.setTextColor(color);
                        esaphColorableEdittextInput.setHintTextColor(color);

                        Drawable mDrawable = spotChooseTextColor.getBackground().mutate();
                        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        spotChooseTextColor.setBackground(mDrawable);
                    }

                    @Override
                    public void onReleasedColorPicker()
                    {
                        closeColorSlider();
                    }
                });
            }
        });

        spotChooseBackgroundColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                esaphColorSlider.selectColor(esaphColorableEdittextInput.getPaintBackgroundColor());
                openColorSlider();

                esaphColorSlider.setListener(new EsaphColorSlider.OnColorSelectedListener()
                {
                    @Override
                    public void onColorChanged(int position, int color)
                    {
                        esaphColorableEdittextInput.setPaintBackgroundColor(color);

                        Drawable mDrawable = spotChooseBackgroundColor.getBackground().mutate();
                        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        spotChooseBackgroundColor.setBackground(mDrawable);
                    }

                    @Override
                    public void onReleasedColorPicker()
                    {
                        closeColorSlider();
                    }
                });
            }
        });

        imageViewOpenStickerBottomKeyboardPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showKeyBoardFragment(EsaphStickerPickerFragmentChat.getInstance(PrivateChat.this));
            }
        });

        imageViewOpenTemplateKeyBoardPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showKeyBoardFragment(ChatTemplatesView.getInstance(PrivateChat.this));
            }
        });

        imageViewOpenEmojieBottomKeyboardPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showKeyBoardFragment(EsaphSmileyPickerFragmentChat.getInstance(PrivateChat.this));
            }
        });

        imageViewOpenAudioEditor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setBottomFragmentFullscreen(EsaphPloppMakerAudioFragment.showWith(chatPartner, PrivateChat.this));
            }
        });

        imageViewProfilbild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(hasTodayStory)
                {
                    setBottomFragmentFullscreen(TodayOurStory.getInstance(chatPartner.getUID_CHATPARTNER()));
                }
                else
                {
                    EsaphDialog esaphDialog = new EsaphDialog(getApplicationContext(),
                            getResources().getString(R.string.txt_alert_noMomentsTodayWithTitle),
                            getResources().getString(R.string.txt_alert_noMomentsTodayWithDetails, chatPartner.getPartnerUsername()));
                    esaphDialog.show();
                }
            }
        });

        esaphListViewKeepPosition.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onItemClickTriggered(position);
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                killSelf();
            }
        });

        esaphListViewKeepPosition.setOnScrollListener(new EndlessScrollListenerDirectionTop() {
            @Override
            public void onLoadMore(int page, int totalItemsCount)
            {
                loadMoreDataChat(arrayAdapterPrivateChat.getCount());
            }
        });

        textViewName.setText(chatPartner.getPartnerUsername());

        if(chatPartner.isHideChat())
        {
            setFriendshipDied();
        }
        else
        {
            allowContactAgain();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                gestureDetector = new GestureDetector(getApplicationContext(), new DetectSwipeGestureListener().setOnSwipeGestureListener(new DetectSwipeGestureListener.OnSwipeGestureListener() {
                    @Override
                    public void onLeft()
                    {

                    }

                    @Override
                    public void onRight() {

                    }

                    @Override
                    public void onUp()
                    {
                        setBottomFragmentFullscreen(EsaphPloppMakerTextualFragment.showWith(chatPartner, PrivateChat.this));
                    }

                    @Override
                    public void onDown() {

                    }
                }));
            }
        }, 200);

        EsaphGlobalProfilbildLoader.with(getApplicationContext()).displayProfilbild(imageViewProfilbild,
                null,
                chatPartner.getUID_CHATPARTNER(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);

        markAllMessagesAsRead();
        setDefaultColors();
        loadMoreDataChat(0);


        new BubbleShowCaseBuilder(PrivateChat.this)
                .title(getResources().getString(R.string.txt_privateChatTutorialExpandTextualEditor)) //Any title for the bubble view
                .backgroundColor(ContextCompat.getColor(PrivateChat.this, R.color.colorBlack))
                .showOnce("PRC1")
                .targetView(esaphColorableEdittextInput).show();
    }

    private void generateRandomColors()
    {
        Random random = new Random();

        System.out.println("Random COLOR: Called");

        int[] mColors = esaphColorSlider.getmColors();
        int randomColorBackground = mColors[random.nextInt(mColors.length-1)];
        int randomColorText = mColors[random.nextInt(mColors.length-1)];

        if(randomColorBackground == randomColorText)
        {
            System.out.println("Random COLOR: Is same Color");
            generateRandomColors();
        }
        else
        {
            System.out.println("Random COLOR: Different color");
        }

        esaphColorableEdittextInput.setPaintBackgroundColor(randomColorBackground);
        esaphColorableEdittextInput.setTextColor(randomColorText);
        esaphColorableEdittextInput.setHintTextColor(randomColorText);

        Drawable mDrawableBackground = spotChooseBackgroundColor.getBackground().mutate();
        mDrawableBackground.setColorFilter(new PorterDuffColorFilter(esaphColorableEdittextInput.getPaintBackgroundColor(), PorterDuff.Mode.MULTIPLY));
        spotChooseBackgroundColor.setBackground(mDrawableBackground);

        Drawable mDrawableText = spotChooseTextColor.getBackground().mutate();
        mDrawableText.setColorFilter(new PorterDuffColorFilter(esaphColorableEdittextInput.getCurrentTextColor(), PorterDuff.Mode.MULTIPLY));
        spotChooseTextColor.setBackground(mDrawableText);
    }

    private void setDefaultColors()
    {
        esaphColorableEdittextInput.setPaintBackgroundColor(Color.WHITE);
        esaphColorableEdittextInput.setTextColor(Color.BLACK);
        esaphColorableEdittextInput.setHintTextColor(Color.BLACK);

        Drawable mDrawableBackground = spotChooseBackgroundColor.getBackground().mutate();
        mDrawableBackground.setColorFilter(new PorterDuffColorFilter(esaphColorableEdittextInput.getPaintBackgroundColor(), PorterDuff.Mode.MULTIPLY));
        spotChooseBackgroundColor.setBackground(mDrawableBackground);

        Drawable mDrawableText = spotChooseTextColor.getBackground().mutate();
        mDrawableText.setColorFilter(new PorterDuffColorFilter(esaphColorableEdittextInput.getCurrentTextColor(), PorterDuff.Mode.MULTIPLY));
        spotChooseTextColor.setBackground(mDrawableText);
    }

    private DialogPrivateChatBlockedInfo dialogPrivateChatBlockedInfo = null;
    public void setFriendshipDied()
    {
        if(dialogPrivateChatBlockedInfo != null)
        {
            dialogPrivateChatBlockedInfo.dismiss();
            dialogPrivateChatBlockedInfo = null;
        }

        Context context = getApplicationContext();
        if(context != null)
        {
            dialogPrivateChatBlockedInfo = new DialogPrivateChatBlockedInfo(getApplicationContext(), chatPartner);
            dialogPrivateChatBlockedInfo.setCancelable(false);
            dialogPrivateChatBlockedInfo.show();
        }

        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.removeImageAndVideosFromPartner();
        }
    }

    public void allowContactAgain()
    {
        if(dialogPrivateChatBlockedInfo != null)
        {
            dialogPrivateChatBlockedInfo.dismiss();
        }

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                SQLFriends sqlFriends = new SQLFriends(getApplicationContext());
                boolean needSynchAktuelle = sqlFriends.needFriendSynchAktuelle(chatPartner.getUID_CHATPARTNER());
                sqlFriends.close();

                if(needSynchAktuelle)
                {
                    new AsyncSynchAktuellePostingsOnlyFromPartner(getApplicationContext(), chatPartner.getUID_CHATPARTNER(), new OnSynchAktullePostingsPartnerListener()
                    {
                        @Override
                        public void onSynchedSuccess()
                        {
                            handlerUI.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    arrayAdapterPrivateChat = new ArrayAdapterPrivateChat(PrivateChat.this, chatPartner, esaphListViewKeepPosition);
                                    esaphListViewKeepPosition.setAdapter(arrayAdapterPrivateChat);
                                    loadMoreDataChat(0);
                                }
                            });
                        }

                        @Override
                        public void onSynchFailed()
                        {
                            handlerUI.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    // TODO: 09.03.2020 uncomment this
                                   // killSelf();
                                }
                            });
                        }
                    }).execute();
                }
            }
        }, 150);
    }


    public void onItemClickTriggered(int position)
    {
        Object object = arrayAdapterPrivateChat.getItem(position);
        if(object instanceof ConversationMessage)
        {
            ConversationMessage conversationMessage = (ConversationMessage) arrayAdapterPrivateChat.getItem(position);
            if(conversationMessage.getType() == CMTypes.FSTI)
                return;

            if(conversationMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID()) //MY MESSAGES.
            {
                ChatSingleImageorVideoView chatSingleImageorVideoView = ChatSingleImageorVideoView.getInstance(arrayAdapterPrivateChat.getListDataDisplay(),
                        new AsyncDeletePrivateMomentPost.PostDeleteListener() {
                            @Override
                            public void onDeletedSuccess(ConversationMessage conversationMessage)
                            {
                                if(!isFinishing() && arrayAdapterPrivateChat != null)
                                {
                                    arrayAdapterPrivateChat.removeItemById(conversationMessage.getMESSAGE_ID());
                                }
                            }

                            @Override
                            public void onFailedDelete(ConversationMessage conversationMessage) {
                                //Do not show here a diaog, is showed in viewpager.
                            }
                        },
                        conversationMessage.getMESSAGE_ID());

                arrayAdapterPrivateChat.setListDataChangedListenerWeakReference(chatSingleImageorVideoView);
                setBottomFragmentFullscreen(chatSingleImageorVideoView);
            }
            else
            {
                ChatSingleImageorVideoView chatSingleImageorVideoView = ChatSingleImageorVideoView.getInstance(arrayAdapterPrivateChat.getListDataDisplay(),
                        new AsyncDeletePrivateMomentPost.PostDeleteListener() {
                            @Override
                            public void onDeletedSuccess(ConversationMessage conversationMessage)
                            {
                                if(!isFinishing() && arrayAdapterPrivateChat != null)
                                {
                                    arrayAdapterPrivateChat.removeItemById(conversationMessage.getMESSAGE_ID());
                                }
                            }

                            @Override
                            public void onFailedDelete(ConversationMessage conversationMessage) {
                                //Do not show here a diaog, is showed in viewpager.
                            }
                        },
                        conversationMessage.getMESSAGE_ID());

                arrayAdapterPrivateChat.setListDataChangedListenerWeakReference(chatSingleImageorVideoView);
                setBottomFragmentFullscreen(chatSingleImageorVideoView);
            }
        }
    }

    private boolean hasTodayStory = false;

    public void scrollMyListViewToBottomHard()
    {
        esaphListViewKeepPosition.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                esaphListViewKeepPosition.setSelection(arrayAdapterPrivateChat.getCount() - 1);
            }
        });
    }

    private AtomicBoolean obLock = new AtomicBoolean(false);
    public void loadMoreDataChat(int startFrom)
    {
        if(arrayAdapterPrivateChat == null)
        {
            arrayAdapterPrivateChat = new ArrayAdapterPrivateChat(
                    PrivateChat.this,
                    chatPartner,
                    esaphListViewKeepPosition);
            esaphListViewKeepPosition.setAdapter(arrayAdapterPrivateChat);
        }

        if(!this.obLock.compareAndSet(false, true) || esaphListViewKeepPosition.isShouldBlockChilds())
            return;

        threadPoolExecutorLoadMessagesQueue.execute(
                new AsyncLoadAllPrivateMessages(getApplicationContext(),
                        startFrom,
                        chatPartner.getUID_CHATPARTNER(),
                        this.loadedConversationMessagesListener,
                        obLock));
    }

    private final LoadedConversationMessagesListener loadedConversationMessagesListener = new LoadedConversationMessagesListener()
    {
        @Override
        public void onMessagesLoaded(final List<Object> list, final int... dataCounts)
        {
            arrayAdapterPrivateChat.pushData(list, esaphListViewKeepPosition);
            arrayAdapterPrivateChat.notifyDataSetChanged();
        }
    };

    private void markAllMessagesAsRead()
    {
        if(chatPartner.getLastConversationMessage() != null && chatPartner.getLastConversationMessage().getType() != CMTypes.FPIC
        && chatPartner.getLastConversationMessage().getType() != CMTypes.FVID && chatPartner.getLastConversationMessage().getABS_ID() == chatPartner.getUID_CHATPARTNER())
        {
            chatPartner.getLastConversationMessage().setMessageStatus(ConversationStatusHelper.STATUS_CHAT_OPENED);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = new SQLChats(getApplicationContext());
                sqlChats.updateAllChatMessagesTextAsRead(chatPartner.getUID_CHATPARTNER());

                UndeliveredChatSeenMessage undeliveredChatSeenMessage =
                        sqlChats.insertChatWasSeen(chatPartner.getUID_CHATPARTNER(), System.currentTimeMillis());
                sqlChats.close();

                new RunnableChatOpenened(getApplicationContext(),
                        undeliveredChatSeenMessage).run();
            }
        }).start();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(esaphColorableEdittextInput != null &&
                esaphColorableEdittextInput.getText() != null &&
                !esaphColorableEdittextInput.getText().toString().isEmpty())
        {
            if(!postedMessageStayAlive)
            {
                postedMessageStayAlive = true;
                handlerUIUserTyping.removeCallbacks(runnableCode);
                handlerUIUserTyping.post(runnableCode);
            }
        }


        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.onResume();
        }

        if(msgServiceConnection != null)
        {
            setUserTyping(msgServiceConnection.isUserTyping(chatPartner.getUID_CHATPARTNER()));
        }

        GlobalNotificationDisplayer.setActivityMessageHandlerCallBack(new ActivityMessageHandlerCallBack()
        {
            @Override
            public boolean isActivityAlive(String UsernameChatPartner)
            {
                try
                {
                    if(activityRunning && chatPartner.getPartnerUsername().equals(UsernameChatPartner))
                    {
                        return true;
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "FailedActivityMessageHandlerCallBack: " + ec);
                }

                return false;
            }
        });

        removeNotification();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(PrivateChat.this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.onPause();
        }

        if(postedMessageStayAlive)
        {
            System.out.println("ASUTS: postedAlive");
            handlerUIUserTyping.removeCallbacks(runnableCode);
            postedMessageStayAlive = false;
            new Thread(new AsyncSendUpdateTypingState(getApplicationContext(),
                    chatPartner.getUID_CHATPARTNER(),
                    false)).start();
        }

        GlobalNotificationDisplayer.setActivityMessageHandlerCallBack(null);
        PrivateChat.activityRunning = false;
    }

    private static boolean activityRunning = false;

    @Override
    public void onStart()
    {
        super.onStart();

        setupConnectionToMsgService();
        ServiceConnection serviceConnectionSendingText = myConnectionSendingText;
        Context context = getApplicationContext();
        if(serviceConnectionSendingText != null && context != null)
        {
            Intent intentSending = new Intent(getApplicationContext(), MsgServiceConnection.class);
            context.bindService(intentSending, serviceConnectionSendingText, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        try
        {
            if(arrayAdapterPrivateChat != null)
            {
                arrayAdapterPrivateChat.onStop();
            }

            ServiceConnection serviceConnectionSendingText = myConnectionSendingText;
            Context context = getApplicationContext();
            if(serviceConnectionSendingText != null && isBoundSendingConnection && context != null)
            {
                msgServiceConnection.removeMsgServiceCallback(this);
                context.unbindService(serviceConnectionSendingText);
            }
        }
        catch (Exception ec)
        {
            //Normal, when service not in use.
        }
    }


    private void removeNotification()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                SQLFriends sqlWatcher = new SQLFriends(getApplicationContext());
                int notifyUserID = sqlWatcher.getFriendNotifyId(chatPartner.getUID_CHATPARTNER());
                sqlWatcher.close();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if(notificationManager != null)
                {
                    notificationManager.cancel(notifyUserID);
                }
            }
        }, 150);
    }

    private EsaphGlobalCommunicationFragment currentBottomFragmentFullscreen;
    public void setBottomFragmentFullscreen(EsaphGlobalCommunicationFragment displayMe)
    {
        displayMe.setFragmentFinishListener(PrivateChat.this);
        currentBottomFragmentFullscreen = displayMe;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .replace(R.id.esaphFragmentFrameLayoutChat, currentBottomFragmentFullscreen, displayMe.getClass().getName())
                .commit();
    }

    private void startLoadingFirstSavedMoments()
    {
        new Thread(new SynchPrivateMomentsBetweenUsers(getApplicationContext(),
                        chatPartner.getUID_CHATPARTNER(),
                        null,
                        new SynchPrivateMomentsBetweenUsers.SynchListenerUsersPosts() {
                            @Override
                            public void onNewData()
                            {
                                if(!isFinishing())
                                {

                                }
                            }

                            @Override
                            public void onFailedOrReachedEnd() {

                            }
                        })).start();
    }

    public void setUserTyping(boolean typing)
    {
        if(typing)
        {
            Context context = getApplicationContext();
            if(context != null)
            {
                textViewVorname.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                textViewVorname.setTypeface(textViewVorname.getTypeface(), Typeface.BOLD);
                textViewVorname.setText(context.getResources().getString(R.string.txtUserTypingTrippleDot));
            }
        }
        else
        {
            Context context = getApplicationContext();
            if(context != null)
            {
                textViewVorname.setTextColor(ContextCompat.getColor(context, R.color.colorDarkerGrey));
                textViewVorname.setTypeface(textViewVorname.getTypeface(), Typeface.NORMAL);
                textViewVorname.setText(chatPartner.getVorname());
            }
        }
    }

    public ArrayAdapterPrivateChat getArrayAdapterPrivateChat() {
        return arrayAdapterPrivateChat;
    }

    @Override
    public Runnable getLoadingTask()
    {
        if(!this.obLock.compareAndSet(false, true))
            return new Runnable() {
                @Override
                public void run() {

                }
            };

        return new AsyncLoadAllPrivateMessages(getApplicationContext(),
                arrayAdapterPrivateChat.getCount(),
                this.chatPartner.getUID_CHATPARTNER(),
                this.loadedConversationMessagesListener,
                obLock);
    }

    @Override
    public void onAddedToGallery(ConversationMessage conversationMessage) {

    }

    @Override
    public void onRemovedFromGallery(ConversationMessage conversationMessage)
    {
    }

    @Override
    public void onPostDied(ConversationMessage conversationMessage)
    {
        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.removeItemById(conversationMessage.getMESSAGE_ID());
        }
    }

    @Override
    public void onFailed(ConversationMessage conversationMessage)
    {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDoneEditingSent(ConversationMessage conversationMessage)
    {
        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.pushSingleMessage(conversationMessage);
            arrayAdapterPrivateChat.notifyDataSetChanged();
            chatPartner.setLastConversationMessage(conversationMessage); //Boss performence.
            scrollMyListViewToBottomHard();

            msgServiceConnection.sendMessageToServer(conversationMessage);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                    .remove(currentBottomFragmentFullscreen).commit();
        }
    }

    @Override
    public void onFriendUpdate(short FRIEND_STATUS, ChatPartner chatPartner)
    {
        if(chatPartner != null && chatPartner.getUID_CHATPARTNER() == chatPartner.getUID_CHATPARTNER())
        {
            if(FRIEND_STATUS == ServerPolicy.POLICY_DETAIL_CASE_NOTHING
                    || FRIEND_STATUS == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED)
            {
                setFriendshipDied();
            }
            else if(FRIEND_STATUS == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS)
            {
                allowContactAgain();
                markAllMessagesAsRead();
            }
        }
    }

    @Override
    public void onUserRemovedPost(ChatPartner chatPartner, long MESSAGE_ID)
    {
        arrayAdapterPrivateChat.removeItemById(MESSAGE_ID);
    }

    @Override
    public void onUserAllowedToSeePostAgain(ChatPartner chatPartner, ChatInfoStateMessage infoStateMessage, long MESSAGE_ID)
    {
        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.pushSingleMessage(infoStateMessage);
            chatPartner.setLastConversationMessage(infoStateMessage); //Boss performence.
            markAllMessagesAsRead();
        }
    }

    @Override
    public void onUserDisallowedToSeePost(ChatPartner chatPartner, ChatInfoStateMessage infoStateMessage, long MESSAGE_ID)
    {
        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.removeItemById(MESSAGE_ID);
            arrayAdapterPrivateChat.pushSingleMessage(infoStateMessage);
            chatPartner.setLastConversationMessage(infoStateMessage); //Boss performence.
            markAllMessagesAsRead();
        }
    }

    @Override
    public void onUserUpdateInsertNewContent(ChatPartner chatPartner, ConversationMessage conversationMessage)
    {
        if(conversationMessage != null && arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.pushSingleMessage(conversationMessage);
            chatPartner.setLastConversationMessage(conversationMessage); //Boss performence.
            markAllMessagesAsRead();
        }
    }

    @Override
    public void onMessageUpdate(final ConversationMessage conversationMessage, final ChatInfoStateMessage chatInfoStateMessage)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                arrayAdapterPrivateChat.updateConversationMessageByID(conversationMessage);
                if(chatInfoStateMessage != null)
                {
                    arrayAdapterPrivateChat.pushSingleMessage(chatInfoStateMessage);
                    chatPartner.setLastConversationMessage(conversationMessage); //Boss performence.
                    markAllMessagesAsRead();
                }
            }
        });
    }

    @Override
    public void onMessageUpdate(final ChatTextMessage chatTextMessage)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                arrayAdapterPrivateChat.updateConversationMessageByID(chatTextMessage);
            }
        });
    }

    @Override
    public void onMessageUpdate(final AudioMessage audioMessage)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                arrayAdapterPrivateChat.updateConversationMessageByID(audioMessage);
            }
        });
    }

    @Override
    public void onMessageUpdate(final EsaphStickerChatObject esaphStickerChatObject)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                arrayAdapterPrivateChat.updateConversationMessageByID(esaphStickerChatObject);
            }
        });
    }

    @Override
    public void onMessageUpdate(final EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                arrayAdapterPrivateChat.updateConversationMessageByID(esaphAndroidSmileyChatObject);
            }
        });
    }

    @Override
    public void onUpdateUserTyping(long UID, boolean typing)
    {
        if(chatPartner.getUID_CHATPARTNER() == UID)
        {
            setUserTyping(typing);
        }
    }

    private boolean isBoundSendingConnection = false;

    private MsgServiceConnection msgServiceConnection;
    private ServiceConnection myConnectionSendingText;

    private void setupConnectionToMsgService()
    {
        myConnectionSendingText = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                MsgServiceConnection.MyLocalBinder binder = (MsgServiceConnection.MyLocalBinder) service;
                msgServiceConnection = binder.getService(PrivateChat.this);
                isBoundSendingConnection = true;
                setUserTyping(msgServiceConnection.isUserTyping(chatPartner.getUID_CHATPARTNER()));
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0)
            {
                isBoundSendingConnection = false;
            }
        };
    }

    private void sendTextMessage()
    {
        JSONObject jsonObjectPloppValues = new JSONObject();
        try
        {
            SpotTextDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext()).setTextColor(esaphColorableEdittextInput.getCurrentTextColor());
            SpotBackgroundDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext()).setBackgroundColor(esaphColorableEdittextInput.getPaintBackgroundColor());
        }
        catch (JSONException je)
        {
            Log.i(getClass().getName(), "build json for sending failed(): " + je);
        }

        Editable editable = esaphColorableEdittextInput.getText();
        if(editable == null) return;

        String message = editable.toString();
        if(!message.isEmpty())
        {
            int startPoint = message.lastIndexOf("\n");
            int realStartPointOfFirst = message.indexOf("\n");

            if (message.matches("[\\n\\r]+"))
            {
                return;
            }

            if(startPoint > 0)
            {
                if(startPoint+1 == message.length())
                {
                    message = message.substring(0, realStartPointOfFirst) + message.substring(startPoint, message.length()).replace("\n", "").replace("\r", "");
                }
            }

            ChatTextMessage chatTextMessage = new ChatTextMessage(
                    message,
                    -1, //No id speciefed yet.
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    chatPartner.getUID_CHATPARTNER(),
                    System.currentTimeMillis(),
                    ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                    SpotLightLoginSessionHandler.getLoggedUsername(),
                    jsonObjectPloppValues.toString());

            SQLChats sqlChats = null;
            try
            {
                sqlChats = new SQLChats(getApplicationContext());
                sqlChats.insertNewPrivateChatMessage(chatTextMessage, ConversationReceiverHelper.getReceiverFromMessage(chatTextMessage));
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

            esaphColorableEdittextInput.setText("");
            arrayAdapterPrivateChat.pushSingleMessage(chatTextMessage);
            arrayAdapterPrivateChat.notifyDataSetChanged();
            chatPartner.setLastConversationMessage(chatTextMessage); //Boss performence.
            scrollMyListViewToBottomHard();

            msgServiceConnection.sendMessageToServer(chatTextMessage);
        }
    }

    private void openColorSlider()
    {
        esaphColorSlider.animate().setDuration(100).alpha(1.0f).setListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                esaphColorSlider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        }).start();
    }

    private void closeColorSlider()
    {
        esaphColorSlider.animate().setDuration(100).alpha(0.0f).setListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                esaphColorSlider.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                esaphColorSlider.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        }).start();
    }

    public void showNoChats(boolean show)
    {
        if(show)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(chatPartner.getDescriptionPlopp());
                spotTextRenderView.setVisibility(View.VISIBLE);
                spotTextRenderView.onValuesChanged(jsonObject, SpotTextDefinitionBuilder.getText(jsonObject));

            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "showNoChats(true) failed: " + ec);
            }
        }
        else
        {
            spotTextRenderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSmileySelected(EsaphEmojie esaphEmojie)
    {
        if(currentBottomFragmentFullscreen instanceof EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener && currentBottomFragmentFullscreen.isAdded())
        {
            EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener onSmileySelectedListener = (EsaphSmileyPickerFragmentPlopp.OnSmileySelectedListener) currentBottomFragmentFullscreen;
            onSmileySelectedListener.onSmileySelected(esaphEmojie);
        }
    }


    private boolean chatMenuExpanded = false;
    private void handleOptionMenuClick()
    {
        chatMenuExpanded = !chatMenuExpanded;
        if(chatMenuExpanded)
        {
            expandChatOptions();
        }
        else
        {
            closeChatOptions();
        }
    }

    private void expandChatOptions()
    {
        imageViewOpenEmojieBottomKeyboardPicker.animate().scaleX(1.0f).scaleX(1.0f).setDuration(320).start();
        imageViewOpenAudioEditor.animate().scaleX(1.0f).scaleX(1.0f).setDuration(320).start();
        imageViewOpenStickerBottomKeyboardPicker.animate().scaleX(1.0f).scaleX(1.0f).setDuration(320).start();
        imageViewOpenTemplateKeyBoardPicker.animate().scaleX(1.0f).scaleX(1.0f).setDuration(320).start();

        imageViewOpenEmojieBottomKeyboardPicker.setClickable(true);
        imageViewOpenStickerBottomKeyboardPicker.setClickable(true);
        imageViewOpenTemplateKeyBoardPicker.setClickable(true);
        imageViewOpenAudioEditor.setClickable(true);

        linearLayoutMoreOptions.animate().translationX(0).alpha(1.0f).setDuration(100).start();
        esaphColorableEdittextInput.animate().alpha(0.0f).translationY(DisplayUtils.dp2px(50)).setDuration(100).start();
    }

    private void closeChatOptions()
    {
        imageViewOpenEmojieBottomKeyboardPicker.animate().scaleX(0.0f).scaleX(0.0f).setDuration(320).start();
        imageViewOpenAudioEditor.animate().scaleX(0.0f).scaleX(0.0f).setDuration(320).start();
        imageViewOpenStickerBottomKeyboardPicker.animate().scaleX(0.0f).scaleX(0.0f).setDuration(320).start();
        imageViewOpenTemplateKeyBoardPicker.animate().scaleX(0.0f).scaleX(0.0f).setDuration(320).start();

        imageViewOpenEmojieBottomKeyboardPicker.setClickable(false);
        imageViewOpenStickerBottomKeyboardPicker.setClickable(false);
        imageViewOpenTemplateKeyBoardPicker.setClickable(false);
        imageViewOpenAudioEditor.setClickable(false);

        linearLayoutMoreOptions.animate().translationX(DisplayUtils.dp2px(100)).alpha(0.0f).setDuration(150).start();
        esaphColorableEdittextInput.animate().alpha(1.0f).translationY(0).setDuration(150).start();

        removeKeyBoardFragment();
    }

    private Fragment currentKeyBoardFragment;
    private void showKeyBoardFragment(Fragment fragment)
    {
        hideKeyboard(PrivateChat.this); //this must be above the return function.

        if(frameLayoutKeyBoardView.getVisibility() == View.VISIBLE && currentKeyBoardFragment != null && currentKeyBoardFragment.getClass().equals(fragment.getClass())) return;

        currentKeyBoardFragment = fragment;
        frameLayoutKeyBoardView.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutKeyBoardFragmentChat, fragment)
                .commit();
    }

    private void removeKeyBoardFragment()
    {
        if(currentKeyBoardFragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentKeyBoardFragment)
                    .commit();
        }

        frameLayoutKeyBoardView.setVisibility(View.GONE);
    }

    private void sendSticker(EsaphStickerChatObject esaphStickerChatObject)
    {
        JSONObject jsonObjectPloppValues = new JSONObject();
        try
        {
            SpotsStickerDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext());
            SpotBackgroundDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext()).setBackgroundColor(esaphColorableEdittextInput.getPaintBackgroundColor());
        }
        catch (JSONException je)
        {
            Log.i(getClass().getName(), "build json for sending failed(): " + je);
        }

        esaphStickerChatObject.setID_CHAT(chatPartner.getUID_CHATPARTNER());
        esaphStickerChatObject.setEsaphPloppInformationsJSONString(jsonObjectPloppValues.toString());

        if(msgServiceConnection != null)
        {
            SQLChats sqlChats = new SQLChats(getApplicationContext());
            try
            {
                sqlChats.insertNewSticker(esaphStickerChatObject, ConversationReceiverHelper.getReceiverFromMessage(esaphStickerChatObject));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            sqlChats.close();

            arrayAdapterPrivateChat.pushSingleMessage(esaphStickerChatObject);
            arrayAdapterPrivateChat.scrollMyListViewToBottom();

            msgServiceConnection.sendMessageToServer(esaphStickerChatObject);
        }
    }

    private void sendEmojie(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        JSONObject jsonObjectPloppValues = new JSONObject();
        try
        {
            SpotEmojieDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext());
            SpotBackgroundDefinitionBuilder.create(jsonObjectPloppValues).resetToInitState(getApplicationContext()).setBackgroundColor(esaphColorableEdittextInput.getPaintBackgroundColor());
        }
        catch (JSONException je)
        {
            Log.i(getClass().getName(), "build json for sending failed(): " + je);
        }

        esaphAndroidSmileyChatObject.setEsaphPloppInformationsJSONString(jsonObjectPloppValues.toString());
        esaphAndroidSmileyChatObject.setID_CHAT(chatPartner.getUID_CHATPARTNER());

        if(msgServiceConnection != null)
        {
            SQLChats sqlChats = new SQLChats(getApplicationContext());
            try {
                sqlChats.insertNewEmojieMessage(esaphAndroidSmileyChatObject, ConversationReceiverHelper.getReceiverFromMessage(esaphAndroidSmileyChatObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sqlChats.close();

            arrayAdapterPrivateChat.pushSingleMessage(esaphAndroidSmileyChatObject);
            arrayAdapterPrivateChat.scrollMyListViewToBottom();

            msgServiceConnection.sendMessageToServer(esaphAndroidSmileyChatObject);
        }
    }

    @Override
    public void onStickerSelected(EsaphStickerChatObject esaphStickerChatObject)
    {
        sendSticker(esaphStickerChatObject);
    }

    @Override
    public void onStickerLongClick(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        setBottomFragmentFullscreen(EsaphPloppMakerStickerFragment.showWith(chatPartner, esaphSpotLightSticker, PrivateChat.this));
    }

    @Override
    public void onSmileySelected(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        sendEmojie(esaphAndroidSmileyChatObject);
    }

    @Override
    public void onSmileyLongClick(EsaphEmojie esaphEmojie)
    {
        setBottomFragmentFullscreen(EsaphPloppMakerEmojieFragment.showWith(chatPartner, esaphEmojie, PrivateChat.this));
    }

    private void hideKeyboard(Activity activity)
    {
        if(activity == null) return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
        {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onFinishedShowingNewMessages()
    {
        if(arrayAdapterPrivateChat != null)
        {
            arrayAdapterPrivateChat.clear();
        }

        loadMoreDataChat(0);
    }

    private void killSelf()
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.extraChatPartner, chatPartner);
        intent.putExtras(bundle);

        setResult(PrivateChat.REQUEST_CODE_FINISHED, intent);
        finish();
    }

    @Override
    public void onTemplateSelected(ChatTextMessage chatTextMessage)
    {
        ChatTextMessage chatTextMessageValid = new ChatTextMessage(
                chatTextMessage.getTextMessage(),
                -1, //No id speciefed yet.
                SpotLightLoginSessionHandler.getLoggedUID(),
                chatPartner.getUID_CHATPARTNER(),
                System.currentTimeMillis(),
                ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                SpotLightLoginSessionHandler.getLoggedUsername(),
                chatTextMessage.getEsaphPloppInformationsJSONString());

        SQLChats sqlChats = null;
        try
        {
            sqlChats = new SQLChats(getApplicationContext());
            sqlChats.insertNewPrivateChatMessage(chatTextMessageValid, ConversationReceiverHelper.getReceiverFromMessage(chatTextMessageValid));
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

        esaphColorableEdittextInput.setText("");
        arrayAdapterPrivateChat.pushSingleMessage(chatTextMessageValid);
        arrayAdapterPrivateChat.notifyDataSetChanged();
        chatPartner.setLastConversationMessage(chatTextMessageValid); //Boss performence.
        scrollMyListViewToBottomHard();

        msgServiceConnection.sendMessageToServer(chatTextMessageValid);
    }

    @Override
    public void onTemplateLongClick(ChatTextMessage chatTextMessage)
    {
        try
        {
            JSONObject object = chatTextMessage.getEsaphPloppInformationsJSON();
            SpotTextDefinitionBuilder.create(object).setText(chatTextMessage.getTextMessage());
            chatTextMessage.setEsaphPloppInformationsJSONString(object.toString());

            setBottomFragmentFullscreen(EsaphPloppMakerTextualFragment.showWith(
                    chatPartner,
                    PrivateChat.this,
                    chatTextMessage.getEsaphPloppInformationsJSONString()));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onTemplateLongClick() failed: " + ec);
        }
    }

    @Override
    public void onFragmentFinished()
    {
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(PrivateChat.this);
    }

    private Handler handlerHandlingStayAliveWriting = new Handler();
    private static final int WAIT_TIME = 30*1000;
    private boolean postedMessageStayAlive = false;

    private Runnable runnableCode = new Runnable()
    {
        @Override
        public void run()
        {
            if(esaphColorableEdittextInput != null &&
                    esaphColorableEdittextInput.getText() != null &&
                    !esaphColorableEdittextInput.getText().toString().isEmpty())
            {
                new Thread(new AsyncSendUpdateTypingState(getApplicationContext(),
                        chatPartner.getUID_CHATPARTNER(),
                        true)).start();
            }
            handlerHandlingStayAliveWriting.postDelayed(runnableCode, PrivateChat.WAIT_TIME);
        }
    };

}
