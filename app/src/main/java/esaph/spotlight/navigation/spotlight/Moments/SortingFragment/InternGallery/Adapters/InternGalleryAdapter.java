/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Adapters;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.InternGalleryFragment;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Model.InternImageFolder;

public class InternGalleryAdapter extends EsaphMomentsRecylerView
{
    private LayoutInflater inflater;
    private ArrayList<Object> list;
    private Activity context;
    private InternGalleryFragment.GridViewClickListener gridViewClickListener;

    public InternGalleryAdapter(Activity localContext, InternGalleryFragment.GridViewClickListener gridViewClickListener,
                                WeakReference<View>[] views) {
        super(views);
        this.inflater = LayoutInflater.from(localContext);
        this.context = localContext;
        this.gridViewClickListener = gridViewClickListener;
        this.list = new ArrayList<>();
        this.list.addAll(getAllImagesFoldered());
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        super.notifyDataSetChangeBypass();
    }


    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public List<Object> getListDataDisplay()
    {
        return list;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data) {
        list.addAll(data);
    }

    @Override
    public void clearAllWithNotify() {

    }

    @Override
    public void removeSinglePostByPID(String PID) {

    }

    @Override
    public int[] getObjectCountsThreadSafe() {
        return new int[0];
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void addFooter() {

    }

    @Override
    public void removeFooter() {

    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = this.list.get(position);

        if(object instanceof InternImageFolder)
        {
            return 0;
        }

        return 1;
    }

    @Override
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    private static class ViewHolderMain extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;

        public ViewHolderMain(View view)
        {
            super(view);
            this.imageView = view.findViewById(R.id.imageView);
            this.textView = view.findViewById(R.id.textViewFolderName);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewItemConversation = inflater.inflate(R.layout.layout_intern_gallery_image, parent, false);
                viewHolder = new ViewHolderMain(viewItemConversation);
                break;


            case 1:

                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        switch (getItemViewType(position))
        {
            case 0:
                final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
                final InternImageFolder internImageFolder = (InternImageFolder) list.get(position);

                viewHolderMain.textView.setText(((InternImageFolder) list.get(position)).getFolderName());

                viewHolderMain.imageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        gridViewClickListener.onClick(viewHolderMain.getAdapterPosition(), internImageFolder);
                    }
                });

                Glide.with(context)
                        .load(internImageFolder.getArrayListAllImagesPath().get(0))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_no_image_no_round).error(R.drawable.ic_no_image_no_round).centerCrop())
                        .into(viewHolderMain.imageView);
                break;


            case 1:

                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public ArrayList<InternImageFolder> getAllImagesFoldered()
    {
        boolean boolean_folder = false;
        ArrayList<InternImageFolder> imageFolders = new ArrayList<>();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i < imageFolders.size(); i++) {
                if (imageFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(imageFolders.get(int_position).getArrayListAllImagesPath());
                al_path.add(absolutePathOfImage);
                imageFolders.get(int_position).setAllImagesPath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                InternImageFolder obj_model = new InternImageFolder(cursor.getString(column_index_folder_name),
                        al_path);
                imageFolders.add(obj_model);


            }
        }

        return imageFolders;
    }


    private ArrayList<String> getAllShownImagesPath(Activity activity)
    {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}
