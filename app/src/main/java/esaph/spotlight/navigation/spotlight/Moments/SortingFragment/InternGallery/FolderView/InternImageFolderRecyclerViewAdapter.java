package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.FolderView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Model.InternImageFolder;

import java.util.List;

public class InternImageFolderRecyclerViewAdapter extends RecyclerView.Adapter<InternImageFolderRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private final LayoutInflater layoutInflater;
    private List<String> list;
    private InternImageFolder internImageFolder;

    public InternImageFolderRecyclerViewAdapter(Context context, InternImageFolder internImageFolder)
    {
        this.context = context;
        this.internImageFolder = internImageFolder;
        this.list = internImageFolder.getArrayListAllImagesPath();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = layoutInflater.inflate(R.layout.fragment_internimagefolder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        //holder.textView.setText(list.get(position);

        Glide.with(context)
                .load(list.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.ic_no_image_no_round).error(R.drawable.ic_no_image_no_round).centerCrop())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View view)
        {
            super(view);
            this.imageView = view.findViewById(R.id.imageView);
            this.textView = view.findViewById(R.id.textViewFolderName);
        }
    }
}
