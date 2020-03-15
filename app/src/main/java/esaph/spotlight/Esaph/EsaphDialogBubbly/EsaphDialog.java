/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphDialogBubbly;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import esaph.spotlight.R;

public class EsaphDialog extends Dialog
{
    private TextView textViewTitle;
    private TextView textViewDetails;
    private Button button;

    private String Title;
    private String Detail;

    public EsaphDialog(@NonNull Context context,
                       String Title,
                       String Details) {
        super(context);

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        this.Title = Title;
        this.Detail = Details;
    }

    public EsaphDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EsaphDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_esaph_dialog);
        textViewTitle = (TextView) findViewById(R.id.textViewEsaphDialogTitle);
        textViewDetails = (TextView) findViewById(R.id.textViewEsaphDialogDetails);

        textViewTitle.setText(Title);
        textViewDetails.setText(Detail);
    }
}
