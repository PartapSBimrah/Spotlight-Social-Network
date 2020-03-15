/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphChatOptionsView;

import android.content.Context;
import android.util.AttributeSet;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;

public class EsaphHitBoxImageViewCircular extends EsaphCircleImageView implements EsaphChatOptionInterface
{
    public EsaphHitBoxImageViewCircular(Context context) {
        super(context);
    }

    public EsaphHitBoxImageViewCircular(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EsaphHitBoxImageViewCircular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EsaphChatOptionInterface esaphChatOptionInterface;
    public void setInterface(EsaphChatOptionInterface esaphChatOptionInterface)
    {
        this.esaphChatOptionInterface = esaphChatOptionInterface;
    }

    @Override
    public void onViewInHitBox()
    {
        if(esaphChatOptionInterface != null)
        {
            esaphChatOptionInterface.onViewInHitBox();
        }
    }

    @Override
    public void onViewOutOfHitBox() {
        if(esaphChatOptionInterface != null)
        {
            esaphChatOptionInterface.onViewOutOfHitBox();
        }
    }

    @Override
    public void onViewInHitBoxSelected() {
        if(esaphChatOptionInterface != null)
        {
            esaphChatOptionInterface.onViewInHitBoxSelected();
        }
    }
}
