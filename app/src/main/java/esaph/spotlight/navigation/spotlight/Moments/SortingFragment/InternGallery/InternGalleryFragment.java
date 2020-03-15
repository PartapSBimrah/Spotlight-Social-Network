/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphDragable.EsaphDragableViewFragment;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Adapters.InternGalleryAdapter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.FolderView.InternGalleryFolderViewAllFragment;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Model.InternImageFolder;

public class InternGalleryFragment extends EsaphGlobalCommunicationFragment
{
    private static final int totalCellCount = 3;
    private RecyclerView recyclerViewMain;
    private InternGalleryAdapter internGalleryAdapter;
    private EsaphActivity esaphActivity;

    public InternGalleryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recyclerViewMain = null;
        esaphActivity = null;
        internGalleryAdapter = null;
    }

    public static InternGalleryFragment getInstance()
    {
        return new InternGalleryFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof EsaphActivity)
        {
            esaphActivity = (EsaphActivity) context;
        }
    }

    private static final int REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE = 535;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                internGalleryAdapter = new InternGalleryAdapter(getActivity(), new GridViewClickListener()
                {
                    @Override
                    public void onClick(int position, Object object)
                    {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.esaphMainFrameLayout, InternGalleryFolderViewAllFragment.getInstance((InternImageFolder) object))
                                .commit();
                    }
                },
                        new WeakReference[]{});
            }
            else
            {
                requestPerm();
            }
        }
        else
        {
            internGalleryAdapter = new InternGalleryAdapter(getActivity(), new GridViewClickListener()
            {
                @Override
                public void onClick(int position, Object object)
                {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.esaphMainFrameLayout, InternGalleryFolderViewAllFragment.getInstance((InternImageFolder) object))
                            .commit();
                }
            }, new WeakReference[]{});
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), InternGalleryFragment.totalCellCount);
        recyclerViewMain.setLayoutManager(gridLayoutManager);
        recyclerViewMain.setAdapter(internGalleryAdapter);
    }

    private void requestPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]
                    {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    }, InternGalleryFragment.REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean granted = false;

        if(requestCode == InternGalleryFragment.REQUEST_CODE_PERM_READ_EXTERNAL_STORAGE)
        {
            if(grantResults.length > 0)
            {
                granted = true;
            }

            for(int count = 0; count < grantResults.length; count++)
            {
                if(grantResults[count] == PackageManager.PERMISSION_DENIED)
                {
                    granted = false;
                    break;
                }
            }
        }

        if(granted)
        {
            internGalleryAdapter = new InternGalleryAdapter(getActivity(), new GridViewClickListener()
            {
                @Override
                public void onClick(int position, Object object)
                {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.esaphMainFrameLayout, InternGalleryFolderViewAllFragment.getInstance((InternImageFolder) object))
                            .commit();
                }
            }, new WeakReference[]{});
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_intern_gallery, container, false);
        recyclerViewMain = (RecyclerView) rootView.findViewById(R.id.momentsMainRecylerView);
        return rootView;
    }


    public interface GridViewClickListener
    {
        void onClick(int position, Object object);
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        EsaphActivity esaphActivity = getEsaphActivity();
        if(esaphActivity != null)
        {
            ((EsaphDragableViewFragment)esaphActivity.findViewById(R.id.esaphMainFrameLayout)).killFragmentAnimated(esaphActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(InternGalleryFragment.this));
            return true;
        }

        return false;
    }



    @Override
    public void refreshData()
    {
        super.refreshData();
        //Are intern images from phone. Do not refresh!
    }
}
