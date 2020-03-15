/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.InternViews.Aktuelle;

import android.content.Context;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class ArrayAdapterUsersSeenAndSaved extends BaseAdapter
{
    private Context context;
    private List<UserSeenOrSavedMoment> list;
    private LayoutInflater inflater;
    private String UsernameMakeBold;

    public ArrayAdapterUsersSeenAndSaved(Context context)
    {
        this.list = new ArrayList<>();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.UsernameMakeBold = SpotLightLoginSessionHandler.getLoggedUsername();
        this.notifyDataSetChanged();
    }

    public void setNewList(List<UserSeenOrSavedMoment> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public UserSeenOrSavedMoment getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private static class ViewHolderUserSeenOrSaved //Vielleicht kennst du den ?
    {
        private TextView textViewState;
        private CheckBox checkBoxState;
        private TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolderUserSeenOrSaved viewHolderSeenOrSaved;
        UserSeenOrSavedMoment userSeenOrSavedMoment = list.get(position);

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.layout_moments_user_seen_or_saved, parent, false);
            viewHolderSeenOrSaved = new ViewHolderUserSeenOrSaved();
            viewHolderSeenOrSaved.textViewState = (TextView) convertView.findViewById(R.id.textViewState);
            viewHolderSeenOrSaved.textView = (TextView) convertView.findViewById(R.id.textViewUserSeenOrSaved);
            viewHolderSeenOrSaved.checkBoxState = (CheckBox) convertView.findViewById(R.id.checkBoxUserAddedToGallery);
            convertView.setTag(viewHolderSeenOrSaved);
        }
        else
        {
            viewHolderSeenOrSaved = (ViewHolderUserSeenOrSaved) convertView.getTag();
        }

        if(UsernameMakeBold.equals(userSeenOrSavedMoment.getUsername()))
        {
            viewHolderSeenOrSaved.textView.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            viewHolderSeenOrSaved.textView.setTypeface(null, Typeface.NORMAL);
        }

        viewHolderSeenOrSaved.textView.setText(userSeenOrSavedMoment.getUsername());

        if(userSeenOrSavedMoment.getMessageStatus() == ConversationStatusHelper.STATUS_SENT)
        {
            if(!userSeenOrSavedMoment.didPartnerSaved()) //SEEN
            {
                viewHolderSeenOrSaved.checkBoxState.setChecked(false);
                viewHolderSeenOrSaved.checkBoxState.jumpDrawablesToCurrentState();
                viewHolderSeenOrSaved.textViewState.setText(context.getResources().getString(R.string.txt_chat_SHORT_Hochgeladen));
            }
        }
        else if(userSeenOrSavedMoment.getMessageStatus() == ConversationStatusHelper.STATUS_CHAT_OPENED)
        {
            if(userSeenOrSavedMoment.didPartnerSaved()) //SAVED
            {
                viewHolderSeenOrSaved.checkBoxState.setChecked(true);
                viewHolderSeenOrSaved.textViewState.setText(context.getResources().getString(R.string.txt_chat_SHORT_Gespeichert));
            }
            else //SEEN
            {
                viewHolderSeenOrSaved.checkBoxState.setChecked(false);
                viewHolderSeenOrSaved.checkBoxState.jumpDrawablesToCurrentState();
                viewHolderSeenOrSaved.textViewState.setText(context.getResources().getString(R.string.txt_chat_SHORT_Gesehen));
            }
        }

        return convertView;
    }
}
