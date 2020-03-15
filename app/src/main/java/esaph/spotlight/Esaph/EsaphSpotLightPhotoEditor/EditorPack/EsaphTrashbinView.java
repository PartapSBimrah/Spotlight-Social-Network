/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;

public class EsaphTrashbinView extends AppCompatImageView
{
    private Rect outRect;
    public EsaphTrashbinView(Context context)
    {
        super(context);
        init(context);
    }

    public EsaphTrashbinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EsaphTrashbinView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ImageView imageViewClose;
    public void setImageViewClose(ImageView imageViewClose)
    {
        this.imageViewClose = imageViewClose;
    }

    private void init(Context context)
    {
        setPadding(DisplayUtils.dp2px(10),
                DisplayUtils.dp2px(10),
                DisplayUtils.dp2px(10),
                DisplayUtils.dp2px(10));

        outRect = new Rect(0, 0, 0, 0);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_button);
        if(mDrawable != null)
        {
            setImageDrawable(mDrawable);
        }

        setAlpha(0.0f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        outRect = new Rect(
                getLeft(),
                getTop(),
                getRight(),
                getBottom());
    }

    public void openTrashbin()
    {
        if(imageViewClose != null)
        {
            imageViewClose.animate().alpha(0.0f).setDuration(100)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
        }

        animate().alpha(1.0f).setDuration(100).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void closeTrashbin()
    {
        if(imageViewClose != null)
        {
            imageViewClose.animate().alpha(1.0f).setDuration(100).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }

        animate().alpha(0.0f).setDuration(100)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }


    private AtomicBoolean animRunningexpandTrash = new AtomicBoolean(false);
    public void expandTrash()
    {
        if(animRunningexpandTrash.get())
            return;

        animRunningexpandTrash.set(true);

        animate().translationX(1.5f).setDuration(100).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.width = (int) ((float) layoutParams.width * 0.50f);
                animRunningexpandTrash.set(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.width = (int) ((float) layoutParams.width * 0.50f);
                animRunningexpandTrash.set(false);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private AtomicBoolean animRunningdepandTrash = new AtomicBoolean(false);
    public void depandTrash()
    {
        if(animRunningdepandTrash.get())
            return;

        animRunningdepandTrash.set(true);

        animate().translationX(1.0f).setDuration(100).setListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.width = (int) ((float) layoutParams.width / 0.50f);
                animRunningdepandTrash.set(false);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.width = (int) ((float) layoutParams.width / 0.50f);
                animRunningdepandTrash.set(false);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }


    private int[] location = new int[2];
    public boolean isViewInBounds(View view, int x_VIEWLOCATION, int y_VIEWLOCATION)
    {
        location[0] = x_VIEWLOCATION;
        location[1] = y_VIEWLOCATION;
        boolean isInBounds = outRect.contains(location[0], location[1]);

        if(isInBounds)
        {
            expandTrash();
        }
        else
        {
            depandTrash();
        }
        return isInBounds;
    }
}
