/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncSaveOrUnsaveMYPOSTBYPARTNER;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.Aktuelle.ArrayAdapterUsersSeenAndSaved;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;

public class DialogUnsaveOrSavePictureOrVideoFromPartner extends Dialog
{
    private ConversationMessage conversationMessage = null;
    private ArrayAdapterUsersSeenAndSaved arrayAdapterUsersSeenAndSaved;
    private UserSeenOrSavedMoment userSeenOrSavedMoment;


    private ProgressBar progressBar;
    private TextView textViewTopTitle;
    private TextView textViewStartHandling;

    public DialogUnsaveOrSavePictureOrVideoFromPartner(@NonNull Context context,
                                                       ConversationMessage conversationMessage,
                                                       ArrayAdapterUsersSeenAndSaved arrayAdapterUsersSeenAndSaved,
                                                       UserSeenOrSavedMoment userSeenOrSavedMoment)
    {
        super(context);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }

        this.conversationMessage = conversationMessage;
        this.arrayAdapterUsersSeenAndSaved = arrayAdapterUsersSeenAndSaved;
        this.userSeenOrSavedMoment = userSeenOrSavedMoment;
        setCanceledOnTouchOutside(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        setContentView(R.layout.layout_dialog_unsave_or_save_picture_or_video);
        progressBar = (ProgressBar) findViewById(R.id.progressBarDialogSaveOrUnsave);
        textViewTopTitle = (TextView) findViewById(R.id.textViewUsername);
        textViewStartHandling = (TextView) findViewById(R.id.textViewUnsaveSaveDialogStartAction);

        textViewTopTitle.setText(userSeenOrSavedMoment.getUsername());

        textViewStartHandling.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                setLoading();
                startHandlingWithServer();
            }
        });

        updateUserStatusUI();
    }


    private void setLoading()
    {
        textViewStartHandling.setClickable(false);
        textViewStartHandling.setFocusable(false);
        textViewStartHandling.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkerGrey));
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(R.drawable.background_rounded_loading_grey).into(new SimpleTarget<Drawable>()
        {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    textViewStartHandling.setBackground(resource);
                }
            }
        });
    }



    private void removeLoading()
    {
        textViewStartHandling.setClickable(true);
        textViewStartHandling.setFocusable(true);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        textViewStartHandling.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        Glide.with(getContext()).load(R.drawable.background_rounded_state_true).into(new SimpleTarget<Drawable>()
        {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    textViewStartHandling.setBackground(resource);
                }
            }
        });
    }


    private void setBlocked()
    {
        textViewStartHandling.setClickable(false);
        progressBar.setVisibility(View.GONE);
        textViewStartHandling.setFocusable(false);
        textViewStartHandling.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkerGrey));
        Glide.with(getContext()).load(R.drawable.background_rounded_loading_grey).into(new SimpleTarget<Drawable>()
        {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    textViewStartHandling.setBackground(resource);
                }
            }
        });
    }


    private void updateUserStatusUI()
    {
        if(userSeenOrSavedMoment.getMessageStatus() == ConversationStatusHelper.STATUS_CHAT_OPENED)
        {
            if(userSeenOrSavedMoment.didPartnerSaved())
            {
                textViewStartHandling.setText(getContext().getResources().getString(R.string.txt_bildEntspeichern));
            }
            else
            {
                textViewStartHandling.setText(getContext().getResources().getString(R.string.txt_bildFreigeben));
            }
        }
        else
        {
            textViewStartHandling.setText(getContext().getResources().getString(R.string.txt_ImageNotOpened));
            setBlocked();
        }
    }





    private void startHandlingWithServer()
    {
        new AsyncSaveOrUnsaveMYPOSTBYPARTNER(getContext(),
                new AsyncSaveOrUnsaveMYPOSTBYPARTNER.SaveOrUnsaveMYPOSTBYPARTNERListener()
                {
                    @Override
                    public void onUpdate(UserSeenOrSavedMoment userSeenOrSavedMoment)
                    {
                        arrayAdapterUsersSeenAndSaved.notifyDataSetChanged();
                        removeLoading();
                        updateUserStatusUI();
                        dismiss();
                    }

                    @Override
                    public void onFatalError(UserSeenOrSavedMoment userSeenOrSavedMoment) {
                        Activity activity = getOwnerActivity();
                        if(activity != null)
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getOwnerActivity());
                                    dialog.setTitle(getContext().getResources().getString(R.string.txt_failedToHandleFriends));
                                    dialog.setMessage(getContext().getResources().getString(R.string.txt_failedToHandleFriendsDetails));
                                    dialog.show();
                                }
                            });
                            removeLoading();
                        }

                    }
                },
                userSeenOrSavedMoment,
                progressBar
        ).execute();
    }
}
