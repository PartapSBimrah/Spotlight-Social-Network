/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.Profilbild;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphImageCropper.CropImage;
import esaph.spotlight.Esaph.EsaphImageCropper.CropImageView;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class DialogChangeProfilbild extends Dialog
{
    private ChatsFragment chatsFragment;
    private TextView textViewRemoveProfilbild;
    private TextView textViewChangeProfibild;
    private EsaphCircleImageView esaphCircleImageView;

    public DialogChangeProfilbild(@NonNull Context context, ChatsFragment chatsFragment)
    {
        super(context);
        this.chatsFragment = chatsFragment;

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    public DialogChangeProfilbild(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogChangeProfilbild(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_change_profilbild);
        textViewChangeProfibild = (TextView) findViewById(R.id.textViewChangeProfilbild);
        textViewRemoveProfilbild = (TextView) findViewById(R.id.textViewDeleteProfilbild);
        esaphCircleImageView = (EsaphCircleImageView) findViewById(R.id.imageViewCurrentProfilbild);

        textViewChangeProfibild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context = chatsFragment.getContext();
                if(context != null && chatsFragment.isAdded())
                {
                    CropImage.activity()
                            .setMinCropResultSize(640,640)
                            .setMaxCropResultSize(640,640)
                            .setAutoZoomEnabled(false)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(context, chatsFragment);
                    dismiss();
                }
            }
        });

        textViewRemoveProfilbild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(chatsFragment != null && chatsFragment.isAdded())
                {
                    new AsyncRemoveProfibild(getContext(), new AsyncRemoveProfibild.OnRemovingPbListener() {
                        @Override
                        public void onSuccess()
                        {
                            if(chatsFragment != null && chatsFragment.isAdded())
                            {
                                EsaphGlobalProfilbildLoader.with(getContext()).invalidateCaches();
                                chatsFragment.setProfilbild();
                                dismiss();
                            }
                        }

                        @Override
                        public void onFailed()
                        {
                            try
                            {
                                if(chatsFragment != null && chatsFragment.isAdded())
                                {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                    dialog.setTitle(getContext().getResources().getString(R.string.txt_ups));
                                    dialog.setMessage(getContext().getResources().getString(R.string.txt__alertNeverHappens));
                                    dialog.show();
                                    dismiss();
                                }
                            }
                            catch (Exception ec)
                            {
                                Log.i(DialogChangeProfilbild.this.getClass().getName(), "DialogChangeProfilbild, onFailed(): " + ec);
                            }
                        }
                    }).execute();
                }
            }
        });

        EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(esaphCircleImageView,
                null,
                SpotLightLoginSessionHandler.getLoggedUID(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);
    }
}
