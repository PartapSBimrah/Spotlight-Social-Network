/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphLifeCloudBackUpView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphColorTransitionShader;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShadersColorArrays;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;

public class EsaphLifeCloudBackUpView extends RelativeLayout
{
    private AccelerateDecelerateInterpolator mSmoothInterpolator;
    private EsaphCircleImageView esaphCircleImageView;
    private EditText editTextSearching;
    private TextView textViewBigText;
    private TextView textViewSmallText;
    private TextView textViewTitel;
    private ImageView imageViewBack;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeaderHeight = getHeight();
        mMinHeaderTranslation = -mHeaderHeight + DisplayUtils.dp2px(50);
    }

    public EsaphLifeCloudBackUpView(Context context)
    {
        super(context);
        init(context);
    }

    public EsaphLifeCloudBackUpView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public EsaphLifeCloudBackUpView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(21)
    public EsaphLifeCloudBackUpView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        View view = inflate(context, R.layout.layout_backup_top_view_state, null);
        if(view != null)
        {
            textViewTitel = (TextView) view.findViewById(R.id.textViewTitle);
            imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);

            esaphCircleImageView = (EsaphCircleImageView) view.findViewById(R.id.imageViewMain);
            textViewBigText = (TextView) view.findViewById(R.id.textViewBelowImageBigText);
            textViewSmallText = (TextView) view.findViewById(R.id.textViewBelowBigText);
            editTextSearching = (EditText) view.findViewById(R.id.editTextFilterSearch);
        }
        addView(view);
    }


    public EsaphCircleImageView getEsaphCircleImageView()
    {
        return esaphCircleImageView;
    }

    public TextView getTextViewSmallText()
    {
        return textViewSmallText;
    }

    public TextView getTextViewTitel()
    {
        return textViewTitel;
    }

    public ImageView getImageViewBack()
    {
        return imageViewBack;
    }

    public void showStatistics(String Titel, String BigText, String Details,
                               String editTextHint,
                               int backuped,
                               int total,
                               int colorCode)
    {
        ImageViewCompat.setImageTintList(imageViewBack, ColorStateList.valueOf(colorCode));
        esaphCircleImageView.setBorderWidth(DisplayUtils.dp2px(2));
        esaphCircleImageView.setCircleShouldIgnorePadding(true);
        esaphCircleImageView.setPadding(DisplayUtils.dp2px(2),
                DisplayUtils.dp2px(2),
                DisplayUtils.dp2px(2),
                DisplayUtils.dp2px(2));
        esaphCircleImageView.setBorderColorBackground(ContextCompat.getColor(getContext(), R.color.colorMomentsGreyText));
        esaphCircleImageView.setEsaphShaderProgress(new EsaphColorTransitionShader(EsaphShadersColorArrays.COLORS_BLUE_LILA, 3, null));
        esaphCircleImageView.setProgress((int) (((float)backuped / (float)total) * 100));

        textViewTitel.setText(Titel);
        textViewBigText.setText(BigText);
        textViewSmallText.setText(Details);
        editTextSearching.setHint(editTextHint);
    }


    private void onScrolledVertical(int offset)
    {
        System.out.println("Setting translation offset to: " + offset);

        float translateY = Math.max(-offset, mMinHeaderTranslation);
        setTranslationY(translateY);
        System.out.println("Setting translation to: " + translateY);

        float ratio = clamp(getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
        interpolate(esaphCircleImageView, imageViewBack, mSmoothInterpolator.getInterpolation(ratio));
        //actionbar title alpha
        //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
        //---------------------------------
        //better way thanks to @cyrilmottier
       // setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min,Math.min(value, max));
    }

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();
    private void interpolate(View view1, View view2, float interpolation)
    {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }
}
