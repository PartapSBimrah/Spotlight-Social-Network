package esaph.spotlight.spots.SpotMaker.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotMakerAdapterFontShaderChooser extends RecyclerView.Adapter<SpotMakerAdapterFontShaderChooser.ViewHolderMain>
{
    private List<JSONObject> list;
    private Context context;
    private SpotMakerAdapterFontShaderChooserClickListener spotMakerAdapterFontShaderChooserClickListener;
    private LayoutInflater inflater;

    public SpotMakerAdapterFontShaderChooser(Context context,
                                             List<JSONObject> list,
                                             SpotMakerAdapterFontShaderChooserClickListener spotMakerAdapterFontShaderChooserClickListener)
    {
        this.context = context;
        this.spotMakerAdapterFontShaderChooserClickListener = spotMakerAdapterFontShaderChooserClickListener;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        notifyDataSetChanged();
    }

    public interface SpotMakerAdapterFontShaderChooserClickListener
    {
        void onShaderClicked(JSONObject jsonObjectShader);
        void onShaderLongClicked();
        void onRemoveAllShaders();
    }

    public static class ViewHolderMain extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        public ViewHolderMain(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.textViewPreviewSpotOptions);
        }
    }

    @NonNull
    @Override
    public ViewHolderMain onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolderMain(inflater.inflate(R.layout.layout_spot_item_textview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderMain holder, int position)
    {
        final JSONObject jsonObject = list.get(position);

        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holder.getAdapterPosition() == 0)
                {
                    spotMakerAdapterFontShaderChooserClickListener.onRemoveAllShaders();
                }
                else
                {
                    spotMakerAdapterFontShaderChooserClickListener.onShaderClicked(jsonObject);
                }
            }
        });

        try
        {
            EsaphGlobalImageLoader.with(context)
                    .canvasMode(CanvasRequest.builder(holder.imageView,
                            new EsaphDimension(holder.imageView.getWidth(),
                                    holder.imageView.getHeight()),
                            new ChatTextMessage(SpotTextDefinitionBuilder.getText(jsonObject),
                                    jsonObject.hashCode(),
                                    -1,
                                    -1,
                                    -1,
                                    (short) -1,
                                    "",
                                    jsonObject.toString())));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SpotMakerAdapterFontShaderChooser onBindViewHolder failed: " + ec);
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
