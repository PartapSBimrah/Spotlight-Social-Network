package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.DisplayingFragment;

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
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model.PublicConversationMessage;

public class PictureFragmentPublic extends Fragment
{
    private static final String IMAGE_PUBLIC_EXTRA_POST = "esaph.spotlight.public.post";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textViewTryAgain;
    private TextView textViewFailInfo;


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



    public PictureFragmentPublic() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.imageView = null;
        this.textViewTryAgain = null;
        this.textViewFailInfo = null;
        this.progressBar = null;
        if(executorService != null)
        {
            executorService.shutdown();
        }
        this.executorService = null;

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

    public static PictureFragmentPublic getInstance(PublicConversationMessage publicConversationMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureFragmentPublic.IMAGE_PUBLIC_EXTRA_POST, publicConversationMessage);
        PictureFragmentPublic pictureFragmentPublic = new PictureFragmentPublic();
        pictureFragmentPublic.setArguments(bundle);
        return pictureFragmentPublic;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_picture_fragment_public, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.imageViewMainPictureFragment);
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
                tryAgainDownloadImage();
                removeFailed();
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
            final PublicConversationMessage publicConversationMessage = (PublicConversationMessage) getArguments().getSerializable(PictureFragmentPublic.IMAGE_PUBLIC_EXTRA_POST);
            if(publicConversationMessage != null)
            {
                this.publicConversationMessage = publicConversationMessage;
                tryAgainDownloadImage();
            }
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
        if(publicConversationMessage != null)
        {
            EsaphGlobalImageLoader.with(getContext()).displayImage(ImageRequest.builder(
                    publicConversationMessage.getPID(),
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
        else
        {
            setFailed();
        }
    }
}
