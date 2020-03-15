package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Preview.CameraPreviewFaceHunt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;

public class FaceHuntFacesAdapter extends RecyclerView.Adapter<FaceHuntFacesAdapter.ViewHolderFaceHunt>
{
    private Context context;
    private List<EsaphSpotLightSticker> list;
    private LayoutInflater inflater;

    public FaceHuntFacesAdapter(Context context)
    {
        this.context = context;
        this.list = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void addItem(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        list.add(esaphSpotLightSticker);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderFaceHunt onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderFaceHunt(inflater.inflate(R.layout.layout_face_hunt_faces_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFaceHunt holder, int position)
    {
        EsaphGlobalImageLoader.with(context)
                .displayImage(StickerRequest.builder(
                        list.get(position).getIMAGE_ID(),
                        holder.imageView,
                        null,
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.drawable_face_hunt_faces_adapter_item_no_image));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }


    public static class ViewHolderFaceHunt extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        public ViewHolderFaceHunt(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.faceHuntFacesAdapterImageViewFace);
        }
    }

}
