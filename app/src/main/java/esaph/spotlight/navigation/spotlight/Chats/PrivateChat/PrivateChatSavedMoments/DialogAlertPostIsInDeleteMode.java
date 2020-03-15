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
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import jp.wasabeef.blurry.Blurry;

public class DialogAlertPostIsInDeleteMode extends Dialog
{
    private ConversationMessage conversationMessage = null;
    private TextView textViewTopTitle;
    private ViewGroup viewGroupRoot;

    public DialogAlertPostIsInDeleteMode(@NonNull Context context,
                                         ViewGroup viewGroup,
                                         ConversationMessage conversationMessage)
    {
        super(context);
        setContentView(R.layout.layout_alter_dialog_post_in_delete_mode);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }

        this.viewGroupRoot = viewGroup;
        this.conversationMessage = conversationMessage;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        textViewTopTitle = (TextView) findViewById(R.id.textViewUsername);
        textViewTopTitle.setText(conversationMessage.getAbsender());
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Blurry.delete(viewGroupRoot);
    }
}
