/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class EsaphSmileyPickerAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<String> listDisplay;
    private Context context;
    private EsaphSmileyViewBASEFragmentChat esaphSmileyViewBASEFragment;
    private OnSmileySelectedListenerChat onSmileySelectedListenerChat;

    public EsaphSmileyPickerAdapterChat(EsaphSmileyViewBASEFragmentChat esaphGlobalCommunicationFragment,
                                        List<String> smileys)
    {
        this.context = esaphGlobalCommunicationFragment.getContext();
        this.esaphSmileyViewBASEFragment = esaphGlobalCommunicationFragment;
        this.listDisplay = smileys;
        this.inflater = LayoutInflater.from(context);
    }


    public void setOnSmileySelectedListenerChat(OnSmileySelectedListenerChat onSmileySelectedListenerChat) {
        this.onSmileySelectedListenerChat = onSmileySelectedListenerChat;
    }

    public interface OnSmileySelectedListenerChat extends Serializable
    {
        void onSmileySelected(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject);
        void onSmileyLongClick(EsaphEmojie esaphEmojie);
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
        View viewPost = inflater.inflate(R.layout.layout_smiley_view_chat, parent, false);
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
                OnSmileySelectedListenerChat onSmileySelectedListenerChat = esaphSmileyViewBASEFragment.getOnSmileySelectedListenerChat();
                if(onSmileySelectedListenerChat != null)
                {
                    onSmileySelectedListenerChat.onSmileySelected(new EsaphAndroidSmileyChatObject(
                        -1,
                        SpotLightLoginSessionHandler.getLoggedUID(),
                        -1,
                        System.currentTimeMillis(),
                        ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE,
                        new EsaphEmojie(stringSmiley),
                        SpotLightLoginSessionHandler.getLoggedUsername(),
                            ""));
                }
            }
        });

        viewHolderSmiley.textViewSmiley.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                OnSmileySelectedListenerChat onSmileySelectedListenerChat = esaphSmileyViewBASEFragment.getOnSmileySelectedListenerChat();
                if(onSmileySelectedListenerChat != null)
                {
                    onSmileySelectedListenerChat.onSmileyLongClick(new EsaphEmojie(stringSmiley));
                }

                return true;
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
