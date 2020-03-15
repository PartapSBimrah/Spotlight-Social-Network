/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.einstellungen;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import esaph.spotlight.R;

public class DialogAccountDelete extends Dialog
{
    public DialogAccountDelete(@NonNull Context context)
    {
        super(context);
        setContentView(R.layout.dialog_delete_account);
        setCanceledOnTouchOutside(false);
    }
}
