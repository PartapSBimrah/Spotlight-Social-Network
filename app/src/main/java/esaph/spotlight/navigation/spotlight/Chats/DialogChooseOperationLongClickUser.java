package esaph.spotlight.navigation.spotlight.Chats;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.R;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.AsyncBlockUser;
import esaph.spotlight.navigation.globalActions.AsyncDeclineFriendAnfrage;
import esaph.spotlight.navigation.globalActions.AsyncHandleFollowStateWithServer;
import esaph.spotlight.navigation.globalActions.FriendStatusListener;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.ShowUserMomentsPrivate;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class DialogChooseOperationLongClickUser extends Dialog
{
    private Thread threadAddOrUnfollow;
    private FriendStatusListener friendStatusListenerCallback;
    private ChatPartner chatPartner;
    private ImageView imageViewLastPicOrPb;
    private TextView textViewUsername;
    private TextView textViewRealLifeName;
    private TextView textViewOpenMoments;
    private TextView textViewCurrentFriendStatus;
    private TextView textViewBlockFriend;
    private ImageView imageViewAnfrageCancel;
    private RelativeLayout relativLayoutBlocked;
    private ChatsFragment chatsFragment;

    public DialogChooseOperationLongClickUser(Activity activity,
                                              ChatsFragment chatsFragment,
                                              ChatPartner chatPartner,
                                              FriendStatusListener friendStatusListener)
    {
        super(activity);
        setOwnerActivity(activity);

        this.chatsFragment = chatsFragment;
        this.chatPartner = chatPartner;
        this.friendStatusListenerCallback = friendStatusListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_choose_user_info);
        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        imageViewAnfrageCancel = (ImageView) findViewById(R.id.imageViewUserAccountDeclineAnfrage);
        imageViewLastPicOrPb = (ImageView) findViewById(R.id.imageViewDialogLastPicOrProfilbild);
        textViewUsername = (TextView) findViewById(R.id.textViewDialogUsername);
        textViewRealLifeName = (TextView) findViewById(R.id.textViewDialogRealName);
        textViewOpenMoments = (TextView) findViewById(R.id.textViewDialogOpenMoments);
        textViewCurrentFriendStatus = (TextView) findViewById(R.id.textViewCurrentFriendStatus);
        textViewBlockFriend = (TextView) findViewById(R.id.textViewDialogBlockUser);
        relativLayoutBlocked = (RelativeLayout) findViewById(R.id.shitHappens);

        imageViewAnfrageCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AsyncDeclineFriendAnfrage(getContext(),
                        chatPartner.getUID_CHATPARTNER(),
                        friendStatusListener).execute();
            }
        });

        textViewBlockFriend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getContext().getResources().getString(R.string.txt_block_UserTitle));
                builder.setSingleChoiceItems(getContext().getResources().getStringArray(R.array.array_select_eraseAll), 0, null);
                builder.setPositiveButton(getContext().getResources().getString(R.string.txt_block_UserTitle), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        System.out.println("ERASING ALL: " + selectedPosition);
                        new AsyncBlockUser(getContext(),
                                chatPartner,
                                selectedPosition,
                                friendStatusListener).execute();
                    }
                });

                builder.setNegativeButton(getContext().getResources().getString(R.string.txt_Abbrechen), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        textViewCurrentFriendStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(currentFriendStatus == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getContext().getResources().getString(R.string.txt_removeFriend_UserTitle));
                    builder.setPositiveButton(getContext().getResources().getString(R.string.txt_remove), new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            threadAddOrUnfollow = new Thread(new AsyncHandleFollowStateWithServer(getOwnerActivity(),
                                    new SocialFriendNegotiation(chatPartner.getUID_CHATPARTNER(),
                                            chatPartner.getPartnerUsername(),
                                            chatPartner.getVorname(),
                                            (short) 0,
                                            ""),
                                    friendStatusListener,
                                    textViewCurrentFriendStatus));
                            threadAddOrUnfollow.start();
                        }
                    });

                    builder.setNegativeButton(getContext().getResources().getString(R.string.txt_Abbrechen), new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else
                {
                    threadAddOrUnfollow = new Thread(new AsyncHandleFollowStateWithServer(getOwnerActivity(),
                            new SocialFriendNegotiation(chatPartner.getUID_CHATPARTNER(),
                                    chatPartner.getPartnerUsername(),
                                    chatPartner.getVorname(),
                                    (short) 0,
                                    ""),
                            friendStatusListener,
                            textViewCurrentFriendStatus));
                    threadAddOrUnfollow.start();
                }
            }
        });

        textViewUsername.setText(chatPartner.getPartnerUsername());

        textViewOpenMoments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                openUserPrivateImageGalleryAndFinish();
            }
        });

        SQLFriends watcher = new SQLFriends(getContext());
        SpotLightUser spotLightUser = watcher.lookUpWatcherAndFriendshipDiedWatcher(chatPartner.getPartnerUsername());
        watcher.close();

        if(spotLightUser == null)
        {
            this.cancel();
        }
        else
        {
            textViewRealLifeName.setText(spotLightUser.getVorname());
            setUpUserLastPicMessages();
            setFriendStatus();
        }
    }

    private void setFriendStatus()
    {
        SQLFriends sqlWatcher = new SQLFriends(getContext());
        short anfragenStatus = sqlWatcher.getAnfragenStatusAndCheckIfFriendship(chatPartner.getUID_CHATPARTNER());
        sqlWatcher.close();
        updateFriendStatus(anfragenStatus);
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

    private void removeBlockedThing()
    {
        textViewBlockFriend.setVisibility(View.INVISIBLE);
        textViewBlockFriend.setClickable(false);
        textViewBlockFriend.setFocusable(false);
        ViewGroup.LayoutParams layoutParams = relativLayoutBlocked.getLayoutParams();
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
        relativLayoutBlocked.setLayoutParams(layoutParams);
        relativLayoutBlocked.setVisibility(View.INVISIBLE);
    }

    private void enableBlockThing()
    {
        textViewBlockFriend.setVisibility(View.VISIBLE);
        textViewBlockFriend.setClickable(true);
        textViewBlockFriend.setFocusable(true);
        ViewGroup.LayoutParams layoutParams = relativLayoutBlocked.getLayoutParams();
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
        layoutParams.width =  MATCH_PARENT;
        relativLayoutBlocked.setLayoutParams(layoutParams);
        relativLayoutBlocked.setVisibility(View.VISIBLE);
    }

    private short currentFriendStatus = -1;
    private void updateFriendStatus(short FriendStatus)
    {
        currentFriendStatus = FriendStatus;
        removeBlockedThing();
        switch(FriendStatus)
        {
            case ServerPolicy.POLICY_DETAIL_CASE_OWN:
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setVisibility(View.INVISIBLE);
                textViewCurrentFriendStatus.setClickable(false);
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_FRIENDS:
                enableBlockThing();
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
                enableBlockThing();
                disableDismissAnfrage();
                textViewCurrentFriendStatus.setText("");
                textViewCurrentFriendStatus.setVisibility(View.INVISIBLE);
                textViewCurrentFriendStatus.setClickable(false);
                break;

            case ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE: //Anfrage an nutzer wurde verschickt.
                enableBlockThing();
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
                enableBlockThing();
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
                enableBlockThing();
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
                enableBlockThing();
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
                enableBlockThing();
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


    private final FriendStatusListener friendStatusListener = new FriendStatusListener()
    {
        @Override
        public void onStatusReceived(long UID_CHAT_PARTNER, short Status)
        {
            if(UID_CHAT_PARTNER != chatPartner.getUID_CHATPARTNER()) return;
            SQLFriends sqlFriends = new SQLFriends(getContext());

            if(Status == ServerPolicy.POLICY_DETAIL_CASE_NOTHING) //KEINE VERBINDUNG.
            {
                SQLChats sqlChats = new SQLChats(getContext());
                sqlChats.removeAllUserData(UID_CHAT_PARTNER);
                sqlChats.close();

                sqlFriends.removeAllUserData(UID_CHAT_PARTNER);
            }
            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE) //ICH HAB JEMANDEN GEBLOCKT.
            {
                SQLChats sqlChats = new SQLChats(getContext());
                sqlChats.removeAllUserData(UID_CHAT_PARTNER);
                sqlChats.close();
            }
            else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED) //ICH WURDE GEBLOCKT.
            {
                SQLChats sqlChats = new SQLChats(getContext());
                sqlChats.removeAllUserData(UID_CHAT_PARTNER);
                sqlChats.close();

                sqlFriends.removeAllUserData(UID_CHAT_PARTNER);
            }

            sqlFriends.close();
            updateFriendStatus(Status);

            if(Status == ServerPolicy.POLICY_DETAIL_CASE_NOTHING
                    || Status == ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE
                    || Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED) //KEINE VERBINDUNG.
            {
                System.out.println("on clicked on blocked or antything.");
                friendStatusListenerCallback.onStatusReceived(UID_CHAT_PARTNER, Status);
            }
        }

        @Override
        public void onStatusFailed(long UID_CHAT_PARTNER)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(getContext().getResources().getString(R.string.txt_failedToHandleFriends));
            dialog.setMessage(getContext().getResources().getString(R.string.txt_failedToHandleFriendsDetails));
            dialog.show();
        }
    };


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

    private void openUserPrivateImageGalleryAndFinish()
    {
        if(chatsFragment != null)
        {
            chatsFragment.setCurrentFragment(ShowUserMomentsPrivate.getInstance(chatPartner));
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

    public void onFriendUpdate(short NEW_STATE, ChatPartner chatPartner)
    {
        if(this.chatPartner.getUID_CHATPARTNER() == chatPartner.getUID_CHATPARTNER())
        {
            updateFriendStatus(NEW_STATE);
        }
    }
}
