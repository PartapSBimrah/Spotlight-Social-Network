package esaph.spotlight.spots.SpotMaker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotMakerAdapterFontStyleChooser extends RecyclerView.Adapter<SpotMakerAdapterFontStyleChooser.ViewHolderMain>
{
    private List<JSONObject> list;
    private Context context;
    private SpotMakerAdapterFontStyleClickListener spotMakerAdapterFontStyleClickListener;
    private LayoutInflater inflater;

    public SpotMakerAdapterFontStyleChooser(Context context,
                                            List<JSONObject> list,
                                            SpotMakerAdapterFontStyleClickListener spotMakerAdapterFontStyleClickListener)
    {
        this.context = context;
        this.spotMakerAdapterFontStyleClickListener = spotMakerAdapterFontStyleClickListener;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        notifyDataSetChanged();
    }

    public interface SpotMakerAdapterFontStyleClickListener
    {
        void onStyleClicked(int FONT_STYLE);
    }

    public static class ViewHolderMain extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        public ViewHolderMain(@NonNull View itemView)
        {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.imageViewPreviewSpotOptions);
        }
    }

    @NonNull
    @Override
    public ViewHolderMain onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderMain(inflater.inflate(R.layout.layout_spot_item_imageview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMain holder, int position)
    {
        final JSONObject jsonObject = list.get(position);

        try
        {
            EsaphGlobalImageLoader.with(context)
                    .canvasMode(CanvasRequest.builder(holder.roundedImageView,
                            new EsaphDimension(holder.roundedImageView.getWidth(),
                                    holder.roundedImageView.getHeight()),
                            new ChatTextMessage(SpotTextDefinitionBuilder.getText(jsonObject),
                                    SpotTextDefinitionBuilder.getFontStyle(jsonObject),
                                    -1,
                                    -1,
                                    -1,
                                    (short) -1,
                                    "",
                                    jsonObject.toString())));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    spotMakerAdapterFontStyleClickListener.onStyleClicked(SpotTextDefinitionBuilder.getFontStyle(jsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
