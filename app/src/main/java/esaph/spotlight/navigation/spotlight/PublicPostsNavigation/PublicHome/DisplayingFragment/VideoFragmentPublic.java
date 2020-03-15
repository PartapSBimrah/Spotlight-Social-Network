package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.DisplayingFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.VideoRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model.PublicConversationMessage;

public class VideoFragmentPublic extends Fragment
{
    private static final String VIDEO_PUBLIC_EXTRA_POST = "esaph.spotlight.public.post";

    private EsaphTextureVideoView esaphTextureVideoView;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;
    private ProgressBar progressBar;
    private boolean surfaceUseable = false;
    private boolean invokedIsVisible = true;
    private PublicConversationMessage publicConversationMessage;


    private TextView textViewUsername;
    private TextView textViewTime;
    private TextView textViewSaveCount;
    private TextView textViewCommentCount;
    private TextView textViewShareCount;
    private TextView textViewHashtags;
    private TextView textViewBeschreibung;
    private ImageView imageViewAccount;
    private CheckBox imageViewHeart;
    private ImageView imageViewShare;
    private ImageView imageViewComment;



    public VideoFragmentPublic() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pauseAndSeekToZero();
        }

        this.esaphTextureVideoView = null;
        this.textViewFailInfo = null;
        this.textViewTryAgain = null;
        this.progressBar = null;

        this.textViewUsername = null;
        this.textViewTime = null;
        this.textViewSaveCount = null;
        this.textViewCommentCount = null;
        this.textViewShareCount = null;
        this.imageViewAccount = null;
        this.imageViewHeart = null;
        this.imageViewShare = null;
        this.imageViewComment = null;
        this.textViewHashtags = null;
        this.textViewBeschreibung = null;
    }


    private void virtualCallPauseMediaPlayer()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pause();
            esaphTextureVideoView.setTag(null);
        }
    }


    private void virtualCallResumeMediaPlayer()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.play();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        virtualCallPauseMediaPlayer();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(invokedIsVisible)
        {
            virtualCallResumeMediaPlayer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        this.invokedIsVisible = isVisibleToUser;
        if(isVisibleToUser)
        {
            virtualCallResumeMediaPlayer();
        }
        else
        {
            virtualCallPauseMediaPlayer();
        }
    }

    public static VideoFragmentPublic getInstance(PublicConversationMessage publicConversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoFragmentPublic.VIDEO_PUBLIC_EXTRA_POST, publicConversationMessage);
        VideoFragmentPublic videoFragmentPublic = new VideoFragmentPublic();
        videoFragmentPublic.setArguments(bundle);
        return videoFragmentPublic;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_video_fragment_public, container, false);
        esaphTextureVideoView = (EsaphTextureVideoView) rootView.findViewById(R.id.imageViewMainVideoView);
        textViewTryAgain = (TextView) rootView.findViewById(R.id.privateMomentViewTryAgainButton);
        textViewFailInfo = (TextView) rootView.findViewById(R.id.textViewPrivateMomentInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBigViewDownloading);

        textViewCommentCount = rootView.findViewById(R.id.textViewCommentCount);
        textViewSaveCount = rootView.findViewById(R.id.textViewSaveCount);
        textViewShareCount = rootView.findViewById(R.id.textViewShareCount);
        textViewTime = rootView.findViewById(R.id.textViewChatMessageUhrzeit);
        textViewUsername = rootView.findViewById(R.id.textViewAbsenderName);
        textViewHashtags = rootView.findViewById(R.id.textViewHashtagsOverImage);
        textViewBeschreibung = rootView.findViewById(R.id.textViewBeschreibung);
        imageViewAccount = rootView.findViewById(R.id.imageViewProfil);
        imageViewComment = rootView.findViewById(R.id.imageViewComments);
        imageViewHeart = rootView.findViewById(R.id.imageViewAddToGallery);
        imageViewShare = rootView.findViewById(R.id.imageViewShare);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                removeFailed();
                tryAgainDownloadImage();
            }
        });


        textViewUsername.setText(publicConversationMessage.getUsername());
        textViewTime.setText(publicConversationMessage.getUsername());
        textViewShareCount.setText(publicConversationMessage.getCOUNT_Shared());
        textViewSaveCount.setText(publicConversationMessage.getCOUNT_Saved());
        textViewCommentCount.setText(publicConversationMessage.getCOUNT_Comments());
        textViewHashtags.setText(publicConversationMessage.getAllHashtagsTogether());
        textViewBeschreibung.setText(publicConversationMessage.getBeschreibung());


        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 28.02.2019 onClick listeners
            }
        });

        imageViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageViewHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null)
        {
            final PublicConversationMessage publicConversationMessage = (PublicConversationMessage) getArguments().getSerializable(VideoFragmentPublic.VIDEO_PUBLIC_EXTRA_POST);
            if(publicConversationMessage != null)
            {
                this.publicConversationMessage = publicConversationMessage;
                tryAgainDownloadImage();
            }
        }
    }


    public void tryAgainDownloadImage()
    {
        if(publicConversationMessage != null)
        {
            EsaphGlobalImageLoader.with(getContext()).displayVideo(VideoRequest.builder(
                    publicConversationMessage.getPID(),
                    progressBar,
                    esaphTextureVideoView).setEsaphGlobalDownloadListener(new EsaphGlobalDownloadListener() {
                @Override
                public void onAvaiableImage(String PID) {
                }

                @Override
                public void onAvaiableVideo(File file)
                {
                    removeFailed();
                    prepareVideo(file);
                }

                @Override
                public void onFailed(String PID)
                {
                    try
                    {
                        setFailed();
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "maybe referce lost videofragment private user: " + ec);
                    }
                }
            }));
        }
        else
        {
            setFailed();
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

    private void prepareVideo(File mainFileOfVideoCache)
    {
        try
        {
            esaphTextureVideoView.setScaleType(EsaphTextureVideoView.ScaleType.CENTER_CROP);
            FileInputStream fis = new FileInputStream(mainFileOfVideoCache);
            esaphTextureVideoView.setDataSource(fis.getFD());
            esaphTextureVideoView.setLooping(true);

            if(invokedIsVisible)
            {
                System.out.println("MEDIADE prepareVideo: PLAY CALLED FROM: " + this);
                esaphTextureVideoView.play();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "prepareVideo() failed: " + ec);
        }
    }


}
