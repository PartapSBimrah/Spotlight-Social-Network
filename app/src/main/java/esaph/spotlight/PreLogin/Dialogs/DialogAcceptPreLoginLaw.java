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
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import esaph.spotlight.R;
import esaph.spotlight.rechtliches.Haftungsauschluss;
import esaph.spotlight.rechtliches.Datenschutz;

public class DialogAcceptPreLoginLaw extends Dialog
{
    private ShitAcceptingInterface shitAcceptingInterface;


    public DialogAcceptPreLoginLaw(@NonNull Context context, ShitAcceptingInterface shitAcceptingInterface)
    {
        super(context);
        setContentView(R.layout.layout_hurensohn);

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        this.shitAcceptingInterface = shitAcceptingInterface;
    }

    public interface ShitAcceptingInterface
    {
        void onAcceptedShitRules();
        void onDeclinedShitRules();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView textViewDatenschutz = (TextView) findViewById(R.id.registerDatenschutz);
        textViewDatenschutz.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getContext().startActivity(new Intent(getContext(), Datenschutz.class));
            }
        });

        TextView textViewHaftungsausschluss = (TextView) findViewById(R.id.registerHaftungsausschluss);
        textViewHaftungsausschluss.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getContext().startActivity(new Intent(getContext(), Haftungsauschluss.class));
            }
        });

        TextView textViewAccept = (TextView) findViewById(R.id.buttonDialogAccept);
        textViewAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                shitAcceptingInterface.onAcceptedShitRules();
                dismiss();
            }
        });


        TextView textViewCancel = (TextView) findViewById(R.id.buttonDialogCancel);
        textViewCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                shitAcceptingInterface.onDeclinedShitRules();
                dismiss();
            }
        });
    }
}
