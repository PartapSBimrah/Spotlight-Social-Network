package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphImageCropper.CropImage;
import esaph.spotlight.Esaph.EsaphImageCropper.CropImageView;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile.AdapterBottomMain.AdapterBottomProfilList;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile.Model.UserAccountProfile;
import esaph.spotlight.services.OtherWorkers.UploadNewProfilbild;

import static android.app.Activity.RESULT_OK;

public class UserProfilVisitShow extends EsaphGlobalCommunicationFragment
{
    public static final String extraProfilUID = "esaph.spotlight.profil.profiluid";

    private static final int TOTAL_CELL_CONT = 4;
    private EsaphCircleImageView imageViewProfilBild;
    private ImageView imageViewChangeProfilbild;
    private ImageView imageViewBack;
    private ImageView imageViewOptions;
    private RecyclerView recyclerViewMainBottom;
    private RecyclerView recyclerViewHorizontalTop;
    private AdapterBottomProfilList adapterBottomProfilList;
    private UserAccountProfile userAccountProfile;

    private TextView textViewUsername;
    private TextView textViewDescription;
    private TextView textViewFollower;
    private TextView textViewFollows;
    private TextView textViewAufrufe;

    private long PROFIL_UID;
    private ProfilPolicy profilPolicy;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        imageViewChangeProfilbild = null;
        imageViewProfilBild = null;
        imageViewBack = null;
        imageViewOptions = null;
        recyclerViewMainBottom = null;
        recyclerViewHorizontalTop = null;
        textViewUsername = null;
        textViewDescription = null;
        textViewFollower = null;
        textViewFollows = null;
        textViewAufrufe = null;
    }

    public enum ProfilPolicy
    {
        SHOW_PRIVATE, SHOW_PUBLIC
    }


    public UserProfilVisitShow()
    {
    }


    public static UserProfilVisitShow getInstance(long UID)
    {
        Bundle bundle = new Bundle();
        bundle.putLong(UserProfilVisitShow.extraProfilUID, UID);
        UserProfilVisitShow userProfil = new UserProfilVisitShow();
        userProfil.setArguments(bundle);
        return userProfil;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            PROFIL_UID = bundle.getLong(UserProfilVisitShow.extraProfilUID, -1);
        }

        imageViewChangeProfilbild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilbild();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
            }
        });

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), UserProfilVisitShow.TOTAL_CELL_CONT);

        adapterBottomProfilList = new AdapterBottomProfilList(getContext(),
                new AdapterBottomProfilList.ProfilImageClickListener()
                {
                    @Override
                    public void onPress(int pos)
                    {

                    }
                },
                new WeakReference[]{});

        recyclerViewMainBottom.setLayoutManager(gridLayoutManager);
        recyclerViewMainBottom.setAdapter(adapterBottomProfilList);
        setUpAccount();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_user_profil, container, false);

        imageViewProfilBild = rootView.findViewById(R.id.imageViewProfilBild);
        imageViewChangeProfilbild = rootView.findViewById(R.id.accountChangeProfilbild);
        imageViewBack = rootView.findViewById(R.id.imageViewBack);
        imageViewOptions = rootView.findViewById(R.id.accountOptionsArrow);
        recyclerViewHorizontalTop = rootView.findViewById(R.id.recylerViewHorizontal);
        recyclerViewMainBottom = rootView.findViewById(R.id.recylerViewAccountMain);
        textViewUsername = rootView.findViewById(R.id.textViewAccountName);
        textViewDescription = rootView.findViewById(R.id.textViewAccountDescription);
        textViewFollower = rootView.findViewById(R.id.textViewFollower);
        textViewFollows = rootView.findViewById(R.id.textViewFollowing);
        textViewAufrufe = rootView.findViewById(R.id.textViewAufrufe);

        return rootView;
    }


    private ExecutorService executorService;
    private void setUpAccount()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();


        if(userAccountProfile != null)
        {
            EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(imageViewProfilBild,
                    null,
                    userAccountProfile.getUID(),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_circle,
                    StorageHandlerProfilbild.FOLDER_PROFILBILD);
        }
        else
        {
            executorService.submit(new RunnableLoadUserProfile(getContext(),
                    PROFIL_UID,
                    new RunnableLoadUserProfile.ProfilLoaderListener()
                    {
                        @Override
                        public void onProfileLoaded(UserAccountProfile userAccountProfile)
                        {
                            UserProfilVisitShow.this.userAccountProfile = userAccountProfile;
                            setUpAccount();
                        }

                        @Override
                        public void onFailed()
                        {
                        }
                    }));
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }



    private void changeProfilbild()
    {
        Context context = getContext();
        if(context != null)
        {
            CropImage.activity()
                    .setMinCropResultSize(640,640)
                    .setMaxCropResultSize(640,640)
                    .setAutoZoomEnabled(false)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(context, this);
        }
    }

    private static final String WORK_PB_ID = "WORK:PROFIL";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();

                Constraints constraints = new Constraints.Builder()
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                Data dataBuilder = new Data.Builder()
                        .putString(UploadNewProfilbild.paramFilePath, resultUri.toString())
                        .build();

                OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(UploadNewProfilbild.class)
                        .setInputData(dataBuilder)
                        .setConstraints(constraints)
                        .build();

                WorkManager.getInstance().beginUniqueWork(UserProfilVisitShow.WORK_PB_ID,
                        ExistingWorkPolicy.REPLACE,
                        simpleRequest).enqueue();

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
