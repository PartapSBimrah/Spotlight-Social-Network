package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragment;

public class EsaphSmileyPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<String> listDisplay;
    private Context context;
    private EsaphSmileyViewBASEFragment esaphSmileyViewBASEFragment;
    private EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor;

    public EsaphSmileyPickerAdapter(EsaphSmileyViewBASEFragment esaphGlobalCommunicationFragment,
                                    EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor,
                                    List<String> smileys)
    {
        this.context = esaphGlobalCommunicationFragment.getContext();
        this.onSmileySelectListenerCameraEditor = onSmileySelectListenerCameraEditor;
        this.esaphSmileyViewBASEFragment = esaphGlobalCommunicationFragment;
        this.listDisplay = smileys;
        this.inflater = LayoutInflater.from(context);
    }

    public void addAll(List<String> new_list)
    {
        this.listDisplay.addAll(new_list);
        notifyDataSetChanged();
    }

    private static class ViewHolderSmiley extends RecyclerView.ViewHolder
    {
        private TextView textViewSmiley;

        public ViewHolderSmiley(View view)
        {
            super(view);
            this.textViewSmiley = (TextView) view.findViewById(R.id.textViewSmiley);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View viewPost = inflater.inflate(R.layout.layout_smiley_view, parent, false);
        viewHolder = new ViewHolderSmiley(viewPost);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final String stringSmiley = listDisplay.get(position);
        ViewHolderSmiley viewHolderSmiley = (ViewHolderSmiley) holder;

        viewHolderSmiley.textViewSmiley.setText(stringSmiley);
        viewHolderSmiley.textViewSmiley.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSmileySelectListenerCameraEditor.onSmileySelected(new EsaphEmojie(stringSmiley));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDisplay.size();
    }

    public String getItem(int pos)
    {
        return listDisplay.get(pos);
    }
}
