/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import esaph.spotlight.R;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class DialogShareApp extends Dialog
{
    public DialogShareApp(@NonNull Context context)
    {
        super(context);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_share_own_app);

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView textViewShareIT = findViewById(R.id.textViewShareIt);
        TextView textViewIDontWannaShare = findViewById(R.id.textViewIDontWannaShare);
        final CheckBox checkBox = findViewById(R.id.checkBoxDoNotDisplayAgain);

        textViewShareIT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context = getContext();
                if(context != null)
                {
                    Intent intentShare = new Intent();
                    intentShare.setAction(Intent.ACTION_SEND);
                    intentShare.putExtra(Intent.EXTRA_TEXT,
                            getContext().getResources().getString(R.string.XX_txt_share_own_app) + " - " + SpotLightLoginSessionHandler.getLoggedUsername());
                    intentShare.setType("text/plain");

                    getContext().startActivity(intentShare);
                    CLPreferences preferences = new CLPreferences(getContext());
                    preferences.setShouldDisplaySharingDialog(false);
                }
            }
        });

        textViewIDontWannaShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CLPreferences preferences = new CLPreferences(getContext());
                preferences.setShouldDisplaySharingDialog(!checkBox.isChecked());
                dismiss();
            }
        });

    }
}
