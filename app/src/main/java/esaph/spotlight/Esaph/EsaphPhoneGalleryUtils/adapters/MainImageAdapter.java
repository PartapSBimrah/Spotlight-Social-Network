package esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.utility.CameraBottomSheetUtility;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.interfaces.OnSelectionListener;
import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.modals.Img;
import esaph.spotlight.R;

public class MainImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int SPAN_COUNT = 4;

    private ArrayList<Img> list;
    private OnSelectionListener onSelectionListener;
    private LayoutInflater inflater;
    private RequestManager glide;
    private RequestOptions options;

    public MainImageAdapter(Context context)
    {
        this.list = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        int size = CameraBottomSheetUtility.WIDTH / SPAN_COUNT;
        options = new RequestOptions().placeholder(R.drawable.ic_no_image_no_round).override(300).transform(new CenterCrop());
        glide = Glide.with(context);
    }

    public ArrayList<Img> getItemList() {
        return list;
    }

    public MainImageAdapter addImage(Img image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public void addOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public void addImageList(ArrayList<Img> images) {
        list.addAll(images);
        notifyDataSetChanged();
    }

    public void clearList() {
        list.clear();
    }

    public void select(boolean selection, int pos) {
        list.get(pos).setSelected(selection);
        notifyItemChanged(pos);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getContentUrl().hashCode();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.main_image, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Img image = list.get(position);
        Holder imageHolder = (Holder) holder;
        glide.load(image.getContentUrl()).apply(options).into(imageHolder.preview);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private ImageView preview;

        Holder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.onClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.onLongClick(list.get(id), view, id);
            return true;
        }
    }
}
