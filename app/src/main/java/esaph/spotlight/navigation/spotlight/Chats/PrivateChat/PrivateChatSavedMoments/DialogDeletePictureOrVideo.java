/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import jp.wasabeef.blurry.Blurry;

public class DialogDeletePictureOrVideo extends Dialog
{
    private ViewGroup viewGroupRoot;

    public DialogDeletePictureOrVideo(@NonNull Context context, ViewGroup viewGroupRoot, ConversationMessage conversationMessage)
    {
        super(context);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }

        this.viewGroupRoot = viewGroupRoot;

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getWindow().setDimAmount(0.0f);
        }

        Blurry.with(getContext())
                .radius(25)
                .sampling(5)
                .color(Color.argb(35, 255, 255, 255))
                .animate(200)
                .onto(viewGroupRoot);

        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_delete_picture_or_video);

        ImageView imageView = (ImageView) findViewById(R.id.imageViewDialogDeletePicOrVideo);
        EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                conversationMessage.getIMAGE_ID(),
                imageView,
                null,
                new EsaphDimension(imageView.getWidth(),
                        imageView.getHeight()),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle));
    }

    public DialogDeletePictureOrVideo(@NonNull Context context, ViewGroup viewGroupRoot, LifeCloudUpload lifeCloudUpload)
    {
        super(context);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }

        this.viewGroupRoot = viewGroupRoot;

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getWindow().setDimAmount(0.0f);
        }

        Blurry.with(getContext())
                .radius(25)
                .sampling(5)
                .color(Color.argb(35, 255, 255, 255))
                .animate(200)
                .onto(viewGroupRoot);

        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_delete_picture_or_video);

        ImageView imageView = (ImageView) findViewById(R.id.imageViewDialogDeletePicOrVideo);
        EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                lifeCloudUpload.getCLOUD_PID(),
                imageView,
                null,
                new EsaphDimension(imageView.getWidth(),
                        imageView.getHeight()),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Blurry.delete(viewGroupRoot);
    }

}
