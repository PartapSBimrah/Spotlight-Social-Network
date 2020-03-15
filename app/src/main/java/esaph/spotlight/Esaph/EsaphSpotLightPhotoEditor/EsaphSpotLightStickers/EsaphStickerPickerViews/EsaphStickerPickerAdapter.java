package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.StickerRequest;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;

public class EsaphStickerPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<EsaphSpotLightSticker> listDisplay;
    private Context context;
    private EsaphStickerViewBASEFragmentDialog esaphStickerViewBASEFragment;

    public EsaphStickerPickerAdapter(EsaphStickerViewBASEFragmentDialog esaphGlobalCommunicationFragment,
                                     List<EsaphSpotLightSticker> stickers)
    {
        this.context = esaphGlobalCommunicationFragment.getContext();
        this.esaphStickerViewBASEFragment = esaphGlobalCommunicationFragment;
        this.listDisplay = stickers;
        this.inflater = LayoutInflater.from(context);
    }

    private static class ViewHolderSticker extends RecyclerView.ViewHolder
    {
        private ImageView imageViewSticker;

        public ViewHolderSticker(View view)
        {
            super(view);
            this.imageViewSticker = (ImageView) view.findViewById(R.id.imageViewSticker);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View viewPost = inflater.inflate(R.layout.layout_sticker_picker_item, parent, false);
        viewHolder = new ViewHolderSticker(viewPost);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final EsaphSpotLightSticker esaphSpotLightSticker = listDisplay.get(position);
        final ViewHolderSticker viewHolderSticker = (ViewHolderSticker) holder;

        viewHolderSticker.imageViewSticker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog onStickerSelectedListener = esaphStickerViewBASEFragment.getOnStickerSelectedListener();
                if(onStickerSelectedListener != null)
                {
                    Drawable drawable = viewHolderSticker.imageViewSticker.getDrawable();
                    if(drawable != null)
                    {
                        Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
                        onStickerSelectedListener.onStickerSelected(esaphSpotLightSticker, bm);
                    }
                }
            }
        });

        EsaphGlobalImageLoader.with(context).displayImage(
                StickerRequest.builder(
                        esaphSpotLightSticker.getIMAGE_ID(),
                viewHolderSticker.imageViewSticker,
                null,
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_sticker_missing));
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size();
    }
}
