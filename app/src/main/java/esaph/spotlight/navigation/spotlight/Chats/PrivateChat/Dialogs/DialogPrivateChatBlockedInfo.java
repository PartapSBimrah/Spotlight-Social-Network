/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.AsyncHandleFollowStateWithServer;
import esaph.spotlight.navigation.globalActions.FriendStatusListener;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;

public class DialogPrivateChatBlockedInfo extends Dialog
{
    private Thread threadAddOrUnfollow;
    private ChatPartner chatPartner;
    private TextView textViewCurrentFriendStatus;
    private ImageView imageViewLastPicOrPb;
    private ImageView imageViewAnfrageCancel;

    public DialogPrivateChatBlockedInfo(@NonNull Context context,
                                        ChatPartner chatPartner)
    {
        super(context);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }
        this.chatPartner = chatPartner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(chatPartner == null)
        {
            dismiss();
        }

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        setContentView(R.layout.layout_dialog_private_chat_blocked_info);

        ((TextView)findViewById(R.id.textViewUsernameDialog)).setText(chatPartner.getPartnerUsername());
        imageViewAnfrageCancel = (ImageView) findViewById(R.id.imageViewUserAccountDeclineAnfrage);
        textViewCurrentFriendStatus = (TextView) findViewById(R.id.textViewCurrentFriendStatus);
        imageViewLastPicOrPb = (ImageView) findViewById(R.id.imageViewTopLastSavedPic);

        textViewCurrentFriendStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                threadAddOrUnfollow = new Thread(new AsyncHandleFollowStateWithServer(getOwnerActivity(),
                        new SocialFriendNegotiation(chatPartner.getUID_CHATPARTNER(),
                                chatPartner.getPartnerUsername(),
                                chatPartner.getVorname(),
                                (short) 0,
                                ""),
                        new FriendStatusListener() {
                    @Override
                    public void onStatusReceived(long CHAT_PARTNER_UID, short Status)
                    {
                        updateFriendStatus(Status);
                        dismiss();
                    }

                    @Override
                    public void onStatusFailed(long UID_CHAT_PARTNER)
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(getContext().getResources().getString(R.string.txt_failedToHandleFriends));
                        dialog.setMessage(getContext().getResources().getString(R.string.txt_failedToHandleFriendsDetails));
                        dialog.show();
                    }
                }, textViewCurrentFriendStatus));
                threadAddOrUnfollow.start();
            }
        });

        setFriendStatus();
        setUpUserLastPicMessages();
    }

    private void setUpUserLastPicMessages()
    {
        ResizeAnimation resizeAnimation = new ResizeAnimation(imageViewLastPicOrPb,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics()),
                imageViewLastPicOrPb.getWidth()
        );

        resizeAnimation.setDuration(150);

        resizeAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                ViewGroup.LayoutParams layoutParams = imageViewLastPicOrPb.getLayoutParams();
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
                imageViewLastPicOrPb.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });
        imageViewLastPicOrPb.startAnimation(resizeAnimation);

        EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(
                imageViewLastPicOrPb,
                null,
                chatPartner.getUID_CHATPARTNER(),
                EsaphImageLoaderDisplayingAnimation.BLINK,
                R.drawable.ic_no_image_circle,
                StorageHandlerProfilbild.FOLDER_PROFILBILD);
    }

    private void updateFriendStatus(short FriendStatus)
    {
        switch(FriendStatus)
        {
            case ServerPolicy.POLICY_DETAIL_CASE_OWN:
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setVisibility(View.INVISIBLE);
                textViewCurrentFriendStatus.setClickable(false);
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_FRIENDS:
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSearchingFriends));
                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_friendship));
                Glide.with(getContext()).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE:
                disableDismissAnfrage();

                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

                Glide.with(getContext()).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });

                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_i_blocked));
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED:
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setText("");
                textViewCurrentFriendStatus.setVisibility(View.INVISIBLE);
                textViewCurrentFriendStatus.setClickable(false);
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE: //Anfrage an nutzer wurde verschickt.
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSearchingFriends));

                Glide.with(getContext()).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });

                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_anfrageVerschickt));
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT: //Hat mir eine anfrage geschickt.
                enableDismissAnfrage();
                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSearchingFriends));

                Glide.with(getContext()).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });

                textViewCurrentFriendStatus.setVisibility(View.VISIBLE);
                textViewCurrentFriendStatus.setClickable(true);
                textViewCurrentFriendStatus.setFocusable(true);
                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_i_got_anfrage));
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_NOTHING: //Keine beziehungen, der Anfrage button wird angezeigt.
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_follow));

                Glide.with(getContext()).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });
                break;

            case ServerPolicy.POLICY_DETAIL_FOLLOWS_ME:
                disableDismissAnfrage();

                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_follow_too));
                Glide.with(getContext()).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });

                break;

            case ServerPolicy.POLICY_DETAIL_I_FOLLOW:
                disableDismissAnfrage();

                textViewCurrentFriendStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                textViewCurrentFriendStatus.setText(getContext().getResources().getString(R.string.txt_friend_status_unfollow));
                Glide.with(getContext()).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            textViewCurrentFriendStatus.setBackground(resource);
                        }
                    }
                });
                break;

            default:
                break;
        }
    }

    private void enableDismissAnfrage()
    {
        imageViewAnfrageCancel.setClickable(true);
        imageViewAnfrageCancel.setFocusable(true);
        imageViewAnfrageCancel.setVisibility(View.VISIBLE);
    }

    private void disableDismissAnfrage()
    {
        imageViewAnfrageCancel.setClickable(false);
        imageViewAnfrageCancel.setFocusable(false);
        imageViewAnfrageCancel.setVisibility(View.INVISIBLE);
    }

    private void setFriendStatus()
    {
        SQLFriends sqlWatcher = new SQLFriends(getContext());
        short anfragenStatus = sqlWatcher.getAnfragenStatusAndCheckIfFriendship(chatPartner.getUID_CHATPARTNER());
        sqlWatcher.close();
        updateFriendStatus(anfragenStatus);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Activity activity = getOwnerActivity();
        if(activity instanceof EsaphActivity)
        {
            ((EsaphActivity) activity).onActivityDispatchBackPressEvent();
        }
        dismiss();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        if(threadAddOrUnfollow != null)
        {
            threadAddOrUnfollow.interrupt();
        }
    }


    private class ResizeAnimation extends Animation
    {
        final int targetHeight;
        View view;
        int startHeight;

        public ResizeAnimation(View view, int targetHeight, int startHeight)
        {
            this.view = view;
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            int newHeight = (int) (startHeight + targetHeight * interpolatedTime);

            Log.i(getClass().getName(), "New Height: " + newHeight);
            view.getLayoutParams().width = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight)
        {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds()
        {
            return true;
        }
    }

}
