package esaph.spotlight.navigation.spotlight.DisplayingFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphContainsUtils;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphTransactions.DetailsTransition;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveSinglePostFromPrivateUser;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightViewPagerAdapter;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.TimeDifferenceHelperClass;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogAlertPostIsInDeleteMode;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class PictureFragmentPrivateUser extends EsaphGlobalCommunicationFragment
{
    private long LOGGED_UID;
    private static final String extra_PID_POST = "esaph.livetemp.moment.private.viewpager.endless.key_post";
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;
    private ChatImage chatImage;
    private ImageView imageViewClose;
    private ImageView imageViewProfilbild;

    private SpotLightViewPagerAdapter spotLightViewPagerAdapter;

    public PictureFragmentPrivateUser()
    {
        // Required empty public constructor
    }

    public static PictureFragmentPrivateUser getInstance(ChatImage chatImage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureFragmentPrivateUser.extra_PID_POST, chatImage);
        PictureFragmentPrivateUser fragobj = new PictureFragmentPrivateUser();
        fragobj.setArguments(bundle);
        return fragobj;
    }

    public PictureFragmentPrivateUser setSpotLightViewPagerAdapter(SpotLightViewPagerAdapter spotLightViewPagerAdapter)
    {
        this.spotLightViewPagerAdapter = spotLightViewPagerAdapter;
        return this;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageViewClose = null;
        this.imageView = null;
        this.textViewTryAgain = null;
        this.textViewFailInfo = null;
        this.progressBar = null;
        this.textViewHashtags = null;
        this.textViewBeschreibung = null;
        this.textViewCurrentPosterUsername = null;
        this.textViewCurrentPostTimeAgo = null;
        this.checkBoxAddToGallery = null;
    }

    private CheckBox checkBoxAddToGallery;

    private TextView textViewCurrentPosterUsername;
    private TextView textViewCurrentPostTimeAgo;


    private TextView textViewBeschreibung;
    private TextView textViewHashtags;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_picture_fragment_private_user, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageViewMainPictureFragment);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBigViewDownloading);
        imageViewClose = (ImageView) rootView.findViewById(R.id.imageViewCloseMasterClassBigViewViewpager);


        textViewCurrentPosterUsername = (TextView) rootView.findViewById(R.id.textViewAbsenderName);
        textViewCurrentPostTimeAgo = (TextView) rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        textViewBeschreibung = (TextView) rootView.findViewById(R.id.textViewBeschreibung);
        textViewHashtags = (TextView) rootView.findViewById(R.id.textViewHashtagsOverImage);
        checkBoxAddToGallery = rootView.findViewById(R.id.imageViewAddToGallery);
        imageViewProfilbild = (ImageView) rootView.findViewById(R.id.imageViewProfilbild);

        //initShowView(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewClose.setOnClickListener(new View.OnClickListener()
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

        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                tryAgainDownloadImage();
                removeFailed();
            }
        });

        checkBoxAddToGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkBoxAddToGallery.setClickable(false);

                final boolean isInDeleteMode = AsyncSaveOrUnsaveSinglePostFromPrivateUser.isPostInDeleteMode(chatImage, getContext());

                if(!isInDeleteMode)
                {
                    new Thread(new AsyncSaveOrUnsaveSinglePostFromPrivateUser(
                            getContext(),
                            new AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener() {
                                @Override
                                public void onAddedToGallery(ConversationMessage conversationMessage)
                                {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                    }
                                }

                                @Override
                                public void onRemovedFromGallery(ConversationMessage conversationMessage) {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                    }
                                }

                                @Override
                                public void onPostDied(ConversationMessage conversationMessage)
                                {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                        if(postStateListenerPrivateChat != null)
                                        {
                                            postStateListenerPrivateChat.onPostDied(conversationMessage);
                                        }

                                        spotLightViewPagerAdapter.removePostByPid(conversationMessage.getMESSAGE_ID());
                                        if(spotLightViewPagerAdapter.getObjectsCount()[0] <= 0)
                                        {
                                            EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                            if(esaphActivity != null)
                                            {
                                                esaphActivity.onActivityDispatchBackPressEvent();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(ConversationMessage conversationMessage)
                                {
                                    if(isAdded())
                                    {
                                        checkBoxAddToGallery.setClickable(true);
                                    }
                                }
                            },
                            chatImage)).start();
                }
                else
                {
                    checkBoxAddToGallery.setChecked(true);
                    checkBoxAddToGallery.jumpDrawablesToCurrentState();
                    Activity activity = getActivity();
                    if(activity != null)
                    {
                        final DialogAlertPostIsInDeleteMode dialogAlertPostIsInDeleteMode = new DialogAlertPostIsInDeleteMode(activity,
                                (ViewGroup) getView().getRootView(),
                                chatImage);

                        final TextView textViewConfirm = dialogAlertPostIsInDeleteMode.findViewById(R.id.textViewUnsaveSaveDialogStartAction);
                        textViewConfirm.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Context context = getContext();
                                if(context != null)
                                {
                                    Glide.with(context).load(R.drawable.background_rounded_loading_grey).into(new SimpleTarget<Drawable>()
                                    {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                            {
                                                textViewConfirm.setBackground(resource);
                                            }
                                        }
                                    });
                                }

                                new Thread(new AsyncSaveOrUnsaveSinglePostFromPrivateUser(
                                        getContext(),
                                        new AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener() {
                                            @Override
                                            public void onAddedToGallery(ConversationMessage conversationMessage) {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                                }
                                            }

                                            @Override
                                            public void onRemovedFromGallery(ConversationMessage conversationMessage)
                                            {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    spotLightViewPagerAdapter.updatePostByPid(conversationMessage);
                                                }
                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }

                                            @Override
                                            public void onPostDied(ConversationMessage conversationMessage)
                                            {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                    if(postStateListenerPrivateChat != null)
                                                    {
                                                        postStateListenerPrivateChat.onPostDied(conversationMessage);
                                                    }

                                                    spotLightViewPagerAdapter.removePostByPid(conversationMessage.getMESSAGE_ID());
                                                    if(spotLightViewPagerAdapter.getObjectsCount()[0] <= 0)
                                                    {
                                                        EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                                        if(esaphActivity != null)
                                                        {
                                                            esaphActivity.onActivityDispatchBackPressEvent();
                                                        }
                                                    }
                                                }

                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }

                                            @Override
                                            public void onFailed(ConversationMessage conversationMessage) {
                                                if(isAdded())
                                                {
                                                    checkBoxAddToGallery.setClickable(true);
                                                }
                                                dialogAlertPostIsInDeleteMode.dismiss();
                                            }
                                        },
                                        chatImage)).start();
                            }
                        });
                        dialogAlertPostIsInDeleteMode.show();
                    }
                }
            }
        });

        System.out.println("ABSENDER: " + chatImage.getAbsender());
        textViewCurrentPosterUsername.setText(chatImage.getAbsender());

        if(chatImage.getBeschreibung() != null && !chatImage.getBeschreibung().isEmpty())
        {
            textViewBeschreibung.setVisibility(View.VISIBLE);
            textViewBeschreibung.setText(chatImage.getBeschreibung());
        }
        else
        {
            textViewBeschreibung.setVisibility(View.GONE);
        }

        /*
        if(chatImage.hasHashtag())
        {
            textViewHashtags.setText(chatImage.getAllHashtagsTogether());
        }
        else
        {
            textViewHashtags.setText("");
        }*/

        textViewCurrentPostTimeAgo.setText(TimeDifferenceHelperClass.getDateDiff(getResources(), chatImage.getMessageTime(),
                System.currentTimeMillis()));

        EsaphContainsUtils.hasSaved(getContext(), new EsaphContainsUtils.SaverCodeExecutionCallback() {
                    @Override
                    public void onExecute(boolean hasSaved)
                    {
                        if(checkBoxAddToGallery == null) return;

                        if(hasSaved)
                        {
                            checkBoxAddToGallery.setChecked(true);
                            checkBoxAddToGallery.jumpDrawablesToCurrentState();
                        }
                        else
                        {
                            checkBoxAddToGallery.setChecked(false);
                            checkBoxAddToGallery.jumpDrawablesToCurrentState();
                        }
                    }
                },
                chatImage.getMESSAGE_ID(),
                LOGGED_UID);

        tryAgainDownloadImage();

        EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(imageViewProfilbild,
                null,
                chatImage.getABS_ID(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_no_round,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(new DetailsTransition());
        }
        LOGGED_UID = SpotLightLoginSessionHandler.getLoggedUID();

        if (getArguments() != null) {
            chatImage = (ChatImage) getArguments().getSerializable(PictureFragmentPrivateUser.extra_PID_POST);
        }
    }

    private void setFailed()
    {
        if(textViewTryAgain != null && textViewFailInfo != null)
        {
            textViewTryAgain.setVisibility(View.VISIBLE);
            textViewTryAgain.setClickable(true);
            textViewFailInfo.setVisibility(View.VISIBLE);
        }
    }

    private void removeFailed()
    {
        if(textViewTryAgain != null && textViewFailInfo != null)
        {
            textViewTryAgain.setVisibility(View.INVISIBLE);
            textViewTryAgain.setClickable(false);
            textViewFailInfo.setVisibility(View.INVISIBLE);
        }
    }

    public void tryAgainDownloadImage()
    {
        EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                chatImage.getIMAGE_ID(),
                imageView,
                progressBar,
                new EsaphDimension(imageView.getWidth(), imageView.getHeight()),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                -1).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener()
        {
            @Override
            public void onAvaiableImage(String PID) {
                removeFailed();
            }

            @Override
            public void onAvaiableVideo(File file) {
                removeFailed();
            }

            @Override
            public void onFailed(String PID) {
                setFailed();
            }
        }));
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



    private AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener postStateListenerPrivateChat;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener)
        {
            postStateListenerPrivateChat = (AsyncSaveOrUnsaveSinglePostFromPrivateUser.PostStateListener) context;
        }
    }
}
