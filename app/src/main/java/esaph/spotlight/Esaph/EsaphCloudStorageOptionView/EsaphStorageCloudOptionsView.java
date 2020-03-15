/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphCloudStorageOptionView;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphColorTransitionShader;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShadersColorArrays;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.Esaph.EsaphGradientTextView.EsaphGradientTextView;
import esaph.spotlight.R;

public class EsaphStorageCloudOptionsView extends RelativeLayout
{
    private EsaphCircleImageView esaphCircleImageView;
    private TextView textViewDetails;
    private TextView textViewTitel;
    private EsaphGradientTextView esaphTextViewColoredBehindCircular;
    private ImageView imageViewBack;


    public EsaphStorageCloudOptionsView(Context context)
    {
        super(context);
        init(context);
    }

    public EsaphStorageCloudOptionsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public EsaphStorageCloudOptionsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(21)
    public EsaphStorageCloudOptionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        View view = inflate(context, R.layout.layout_backup_top_view_storage_cloud, null);
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

    public TextView getTextViewDetails()
    {
        return textViewDetails;
    }

    public TextView getTextViewTitel()
    {
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

    public void showStatistics(String Titel, String Details, int total, Bitmap bitmap)
    {
        esaphCircleImageView.setImageBitmap(bitmap);
        esaphCircleImageView.setBorderWidth(DisplayUtils.dp2px(3));
        esaphCircleImageView.setBorderColorBackground(ContextCompat.getColor(getContext(), R.color.colorMomentsGreyText));
        esaphCircleImageView.setEsaphShaderProgress(new EsaphColorTransitionShader(EsaphShadersColorArrays.COLORS_BLUE_LILA, 3, null));
        esaphCircleImageView.setProgress(100);
        esaphTextViewColoredBehindCircular.setText(""+total);
        textViewTitel.setText(Titel);
        textViewDetails.setText(Details);
    }
}
