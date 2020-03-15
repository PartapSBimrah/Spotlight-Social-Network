/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.R;

public class EsaphSmileyPickerAdapterPlopp extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<String> listDisplay;
    private Context context;
    private EsaphSmileyViewBASEFragment esaphSmileyViewBASEFragment;

    public EsaphSmileyPickerAdapterPlopp(EsaphSmileyViewBASEFragment esaphGlobalCommunicationFragment,
                                         List<String> smileys)
    {
        this.context = esaphGlobalCommunicationFragment.getContext();
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
                esaphSmileyViewBASEFragment.getOnSmileySelectedListener().onSmileySelected(new EsaphEmojie(stringSmiley));
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
