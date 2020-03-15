/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncGetNewPassword;

public class DialogPasswortVergessen extends Dialog
{
    private EditText editTextUsername;
    private TextView textViewOK;

    public DialogPasswortVergessen(@NonNull Context context)
    {
        super(context);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.layout_dialog_passwort_vergessen);
        editTextUsername = (EditText) findViewById(R.id.editTextEmailPasswortVergessen);
        textViewOK = (TextView) findViewById(R.id.textViewPasswortRequestAbschicken);

        textViewOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                textViewOK.setClickable(false);
                String benutzerName = editTextUsername.getText().toString();
                if(!benutzerName.isEmpty())
                {
                    new AsyncGetNewPassword(getContext(), benutzerName, onNewPasswordRequestedListener).execute();
                }
            }
        });
    }

    private final OnNewPasswordRequestedListener onNewPasswordRequestedListener = new OnNewPasswordRequestedListener()
    {
        @Override
        public void onRequestedResult(String result)
        {
            try
            {
                Activity activity = getOwnerActivity();
                if(activity == null || activity.isFinishing()) return;

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textViewOK.setClickable(true);
                    }
                });

                if(result.equals("ES")) //EMAIL SENT.
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(getContext().getResources().getString(R.string.txt_newPwRequestTITLE));
                    dialog.setMessage(getContext().getResources().getString(R.string.txt_newPwRequested));
                    dialog.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dismiss();
                        }
                    });

                    dialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(result.equals("AR")) //ALREADY REQUESTED.
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(getContext().getResources().getString(R.string.txt_newPwRequestTITLE));
                    dialog.setMessage(getContext().getResources().getString(R.string.txt_newPwAlreadyRequested));
                    dialog.show();
                }
                else
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(getContext().getResources().getString(R.string.txt_newPwRequestTITLE));
                    dialog.setMessage(getContext().getResources().getString(R.string.txt_failed_newPwRequest));
                    dialog.show();
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "OnNewPasswordRequestedListener, onRequestedResult() failed: " + ec);
            }
        }
    };

    public interface OnNewPasswordRequestedListener
    {
        void onRequestedResult(String result);
    }
}
