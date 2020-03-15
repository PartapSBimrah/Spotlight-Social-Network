package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.FolderView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphLifeCloudBackUpView.EsaphLifeCloudBackUpView;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Model.InternImageFolder;

public class InternGalleryFolderViewAllFragment extends EsaphGlobalCommunicationFragment
{
    private static InternImageFolder internImageFolder;
    private InternImageFolderRecyclerViewAdapter internImageFolderRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private static final int mColumnCount = 3;
    private EsaphLifeCloudBackUpView esaphImageFolderBackUpView;

    public InternGalleryFolderViewAllFragment()
    {
    }

    public static InternGalleryFolderViewAllFragment getInstance(InternImageFolder internImageFolder)
    {
        InternGalleryFolderViewAllFragment.internImageFolder = internImageFolder;
        return new InternGalleryFolderViewAllFragment();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Activity activity = getActivity();
        if(activity != null)
        {
            EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(activity);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        internImageFolderRecyclerViewAdapter = new InternImageFolderRecyclerViewAdapter(getActivity(), InternGalleryFolderViewAllFragment.internImageFolder);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_internimagefolder_list, container, false);

        esaphImageFolderBackUpView = (EsaphLifeCloudBackUpView) rootView.findViewById(R.id.topView);

        Context context = getContext();
        if(context != null)
        {
            Glide.with(getContext())
                    .load(internImageFolder.getArrayListAllImagesPath().get(0))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_image_no_round).error(R.drawable.ic_no_image_no_round).centerCrop())
                    .into(esaphImageFolderBackUpView.getEsaphCircleImageView());
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), InternGalleryFolderViewAllFragment.mColumnCount));
        recyclerView.setAdapter(internImageFolderRecyclerViewAdapter);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(InternGalleryFolderViewAllFragment.this)
                    .commit();
            return true;
        }

        return false;
    }


}
