/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphHashtagInfoStateViewTop;

import android.content.Context;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGradientTextView.EsaphGradientTextView;
import esaph.spotlight.R;

public class EsaphHashtagStateInfoViewTop extends RelativeLayout
{
    private EsaphCircleImageView esaphCircleImageView;
    private TextView textViewDetails;
    private TextView textViewTitel;
    private EsaphGradientTextView esaphTextViewColoredBehindCircular;
    private ImageView imageViewBack;


    public EsaphHashtagStateInfoViewTop(Context context)
    {
        super(context);
        init(context);
    }

    public EsaphHashtagStateInfoViewTop(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public EsaphHashtagStateInfoViewTop(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(21)
    public EsaphHashtagStateInfoViewTop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        View view = inflate(context, R.layout.layout_backup_top_view_state_hashtag, null);
        if(view != null)
        {
            esaphCircleImageView = (EsaphCircleImageView) view.findViewById(R.id.imageViewEsaphRoundedSaveAll);
            textViewDetails = (TextView) view.findViewById(R.id.textViewSavedImageCount);
            textViewTitel = (TextView) view.findViewById(R.id.textViewText);
            esaphTextViewColoredBehindCircular = (EsaphGradientTextView) view.findViewById(R.id.textViewColoredBehindHitBox);
            imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);
        }
        addView(view);
    }


    public EsaphCircleImageView getEsaphCircleImageView()
    {
        return esaphCircleImageView;
    }

    public TextView getTextViewDetails() {
        return textViewDetails;
    }

    public TextView getTextViewTitel() {
        return textViewTitel;
    }

    public EsaphGradientTextView getEsaphTextViewColoredBehindCircular()
    {
        return esaphTextViewColoredBehindCircular;
    }

    public ImageView getImageViewBack()
    {
        return imageViewBack;
    }
}
