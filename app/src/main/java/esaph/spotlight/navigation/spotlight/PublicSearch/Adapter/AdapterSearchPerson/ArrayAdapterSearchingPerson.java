/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.AdapterSearchPerson;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.navigation.globalActions.AsyncDeclineFriendAnfrage;
import esaph.spotlight.navigation.globalActions.AsyncHandleFollowStateWithServer;
import esaph.spotlight.navigation.globalActions.FriendStatusListener;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.EsaphSearchTabAdapter;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchItemUser;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchStatus;

public class ArrayAdapterSearchingPerson extends EsaphSearchTabAdapter
{
    private ExecutorService executorService;
    private LayoutInflater layoutInflater;
    private List<Object> list;
    private Context context;

    public ArrayAdapterSearchingPerson(Context context,
                                       ExecutorService executorService)
    {
        this.context = context;
        this.executorService = executorService;
        this.list = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void clearAll()
    {
        list.clear();
    }

    public void addAll(List<Object> items)
    {
        this.list.addAll(items);
    }

    @Override
    public void notifyDataSetChanged()
    {
        if(list.size() > 1)
        {
            list.remove(0);
        }
        super.notifyDataSetChanged();
    }

    public void addSearchStatus()
    {
        this.list.add(0, new SearchStatus(context.getResources().getString(R.string.txt_searchStatus_Suche), 0));
        notifyDataSetChanged();
    }

    public void clear()
    {
        this.list.clear();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    private static class ViewHolderUser //6 sachen
    {
        private ProgressBar progressBar;
        private ImageView imageViewProfilbild;
        private TextView textViewUsername;
        private TextView textViewVorname;
        private TextView textViewFollowFriendState;
        private ImageView imageViewCancelAnfrage;
        private TextView textViewMainLocation;
    }

    private static class ViewHolderSearchingAndNotFound
    {
        private AVLoadingIndicatorView progressBarLoading;
        private TextView textView;
    }

    private static class ViewHolderDividerTitle
    {
        private TextView textViewDividerTitle;
    }

    private static class ViewHolderFriendAnfrage
    {
        private ProgressBar progressBar;
        private TextView textViewUsername;
        private TextView textViewVorname;
        private TextView textViewFriendState;
        private ImageView imageViewCancelAnfrage;
        private ImageView imageViewProfilbild;
        private TextView textViewMainLocation;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = this.list.get(position);
        if(object instanceof SearchItemUser)
        {
            return 0;
        }
        else if(object instanceof SearchStatus)
        {
            return 1;
        }
        else if(object instanceof TitleList)
        {
            return 2;
        }
        else if(object instanceof SocialFriendNegotiation)
        {
            return 3;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        switch (getItemViewType(position))
        {
            case 0:
                final SearchItemUser searchItem = (SearchItemUser) list.get(position);
                final ViewHolderUser viewHolderUser;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.search_sheet_list_item_user, parent, false);
                    viewHolderUser = new ViewHolderUser();
                    viewHolderUser.textViewMainLocation = (TextView) convertView.findViewById(R.id.textViewSearchUserProvince);
                    viewHolderUser.textViewUsername = (TextView) convertView.findViewById(R.id.textViewSearchUserUsername);
                    viewHolderUser.textViewVorname = (TextView) convertView.findViewById(R.id.textViewSearchUserVorname);
                    viewHolderUser.textViewFollowFriendState = (TextView) convertView.findViewById(R.id.textViewSearchUserWatcher);
                    viewHolderUser.imageViewCancelAnfrage = (ImageView) convertView.findViewById(R.id.imageViewSearchUserCancelAnfrage);
                    viewHolderUser.imageViewProfilbild = (ImageView) convertView.findViewById(R.id.imageViewProfilbildPreview);
                    convertView.setTag(viewHolderUser);
                }
                else
                {
                    viewHolderUser = (ViewHolderUser) convertView.getTag();
                }

                viewHolderUser.imageViewCancelAnfrage.setVisibility(View.INVISIBLE);
                viewHolderUser.imageViewCancelAnfrage.setClickable(false);
                viewHolderUser.imageViewCancelAnfrage.setFocusable(false);

                viewHolderUser.textViewFollowFriendState.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        viewHolderUser.textViewFollowFriendState.setClickable(false);
                        viewHolderUser.textViewFollowFriendState.setFocusable(false);
                        executorService.execute(new AsyncHandleFollowStateWithServer(context,
                                new SocialFriendNegotiation(searchItem.getUID(),
                                        searchItem.getUsername(),
                                        searchItem.getVorname(),
                                        searchItem.getWatchingStatus(),
                                        searchItem.getMainLocation()),

                                friendStatusListener, v));
                    }
                });

                viewHolderUser.imageViewCancelAnfrage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if(searchItem.getWatchingStatus() == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT)
                        {
                            new AsyncDeclineFriendAnfrage(context,
                                    searchItem.getUID(),
                                    friendStatusListener).execute();
                        }
                    }
                });

                switch(searchItem.getWatchingStatus())
                {

                    case ServerPolicy.POLICY_DETAIL_CASE_OWN:
                        viewHolderUser.textViewFollowFriendState.setVisibility(View.INVISIBLE);
                        viewHolderUser.textViewFollowFriendState.setClickable(false);
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_FRIENDS:
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_friendship));
                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE:
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_i_blocked));
                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED:
                        viewHolderUser.textViewFollowFriendState.setText("");
                        viewHolderUser.textViewFollowFriendState.setVisibility(View.INVISIBLE);
                        viewHolderUser.textViewFollowFriendState.setClickable(false);
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE: //Anfrage an nutzer wurde verschickt.
                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_anfrageVerschickt));
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT: //Hat mir eine anfrage geschickt.
                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });
                        viewHolderUser.imageViewCancelAnfrage.setVisibility(View.VISIBLE);
                        viewHolderUser.imageViewCancelAnfrage.setClickable(true);
                        viewHolderUser.imageViewCancelAnfrage.setFocusable(true);
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_i_got_anfrage));
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_NOTHING: //Keine beziehungen, der Anfrage button wird angezeigt.
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_follow));

                        Glide.with(context).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });

                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                        break;

                    case ServerPolicy.POLICY_DETAIL_FOLLOWS_ME:
                        viewHolderUser.textViewFollowFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                        viewHolderUser.textViewFollowFriendState.setText(context.getResources().getString(R.string.txt_friend_status_follow_too));
                        Glide.with(context).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderUser.textViewFollowFriendState.setBackground(resource);
                                }
                            }
                        });


                        break;

                    default:
                        break;
                }

                viewHolderUser.textViewMainLocation.setText(searchItem.getMainLocation());
                viewHolderUser.textViewVorname.setText(searchItem.getVorname());
                viewHolderUser.textViewUsername.setText(searchItem.getUsername());

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(viewHolderUser.imageViewProfilbild,
                        null,
                        searchItem.getUID(),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_circle,
                    StorageHandlerProfilbild.FOLDER_PROFILBILD);

                break;


            case 1:
                final SearchStatus searchStatus = (SearchStatus) list.get(position);
                final ViewHolderSearchingAndNotFound viewHolderSearchingAndNotFound;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_search_status_item, parent, false);
                    viewHolderSearchingAndNotFound = new ViewHolderSearchingAndNotFound();
                    viewHolderSearchingAndNotFound.textView = (TextView) convertView.findViewById(R.id.textViewSearchStatusText);
                    viewHolderSearchingAndNotFound.progressBarLoading = (AVLoadingIndicatorView) convertView.findViewById(R.id.progressBarSearchStatus);
                    convertView.setTag(viewHolderSearchingAndNotFound);
                }
                else
                {
                    viewHolderSearchingAndNotFound = (ViewHolderSearchingAndNotFound) convertView.getTag();
                }

                if(searchStatus.getState() == 0) //Loading
                {
                    viewHolderSearchingAndNotFound.progressBarLoading.smoothToShow();
                    viewHolderSearchingAndNotFound.textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()));
                }
                else if(searchStatus.getState() == 1) //Na error
                {
                    viewHolderSearchingAndNotFound.progressBarLoading.smoothToHide();
                    viewHolderSearchingAndNotFound.textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()));
                }

                viewHolderSearchingAndNotFound.textView.setText(searchStatus.getStatus());
                break;


            case 2:
                TitleList memoryDividerPlaceholder = (TitleList) list.get(position);
                final ViewHolderDividerTitle viewHolderDividerTitle;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_memorys_item_memory_placeholder, parent, false);
                    viewHolderDividerTitle = new ViewHolderDividerTitle();
                    viewHolderDividerTitle.textViewDividerTitle = (TextView) convertView.findViewById(R.id.textViewMemoryItemTitlePlaceholder);
                    convertView.setTag(viewHolderDividerTitle);
                }
                else
                {
                    viewHolderDividerTitle = (ViewHolderDividerTitle) convertView.getTag();
                }

                viewHolderDividerTitle.textViewDividerTitle.setText(memoryDividerPlaceholder.getTextTitle());
                break;


            case 3:
                final SocialFriendNegotiation friendAnfrage = (SocialFriendNegotiation) list.get(position);
                final ViewHolderFriendAnfrage viewHolderFriendAnfrage;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_friend_anfrage, parent, false);
                    viewHolderFriendAnfrage = new ViewHolderFriendAnfrage();
                    viewHolderFriendAnfrage.textViewMainLocation = (TextView) convertView.findViewById(R.id.textViewSearchUserProvince);
                    viewHolderFriendAnfrage.textViewUsername = (TextView) convertView.findViewById(R.id.textViewSearchUserUsername);
                    viewHolderFriendAnfrage.textViewVorname = (TextView) convertView.findViewById(R.id.textViewSearchUserVorname);
                    viewHolderFriendAnfrage.textViewFriendState = (TextView) convertView.findViewById(R.id.textViewSearchUserWatcher);
                    viewHolderFriendAnfrage.imageViewCancelAnfrage = (ImageView) convertView.findViewById(R.id.imageViewSearchUserCancelAnfrage);
                    viewHolderFriendAnfrage.imageViewProfilbild = (ImageView) convertView.findViewById(R.id.imageViewProfilbildPreview);
                    convertView.setTag(viewHolderFriendAnfrage);
                }
                else
                {
                    viewHolderFriendAnfrage = (ViewHolderFriendAnfrage) convertView.getTag();
                }

                viewHolderFriendAnfrage.imageViewCancelAnfrage.setVisibility(View.INVISIBLE);
                viewHolderFriendAnfrage.imageViewCancelAnfrage.setClickable(false);
                viewHolderFriendAnfrage.imageViewCancelAnfrage.setFocusable(false);

                viewHolderFriendAnfrage.textViewFriendState.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        viewHolderFriendAnfrage.textViewFriendState.setClickable(false);
                        viewHolderFriendAnfrage.textViewFriendState.setFocusable(false);
                        executorService.execute(new AsyncHandleFollowStateWithServer(context,
                                friendAnfrage,
                                friendStatusListener, v));
                    }
                });

                viewHolderFriendAnfrage.imageViewCancelAnfrage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if(friendAnfrage.getAnfragenStatus() == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT)
                        {
                            new AsyncDeclineFriendAnfrage(context,
                                    friendAnfrage.getUID(),
                                    friendStatusListener).execute();
                        }
                    }
                });

                switch(friendAnfrage.getAnfragenStatus())
                {
                    case ServerPolicy.POLICY_DETAIL_CASE_OWN:
                        viewHolderFriendAnfrage.textViewFriendState.setVisibility(View.INVISIBLE);
                        viewHolderFriendAnfrage.textViewFriendState.setClickable(false);
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_FRIENDS:
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_friendship));
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE:
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_i_blocked));
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED:
                        viewHolderFriendAnfrage.textViewFriendState.setText("");
                        viewHolderFriendAnfrage.textViewFriendState.setVisibility(View.INVISIBLE);
                        viewHolderFriendAnfrage.textViewFriendState.setClickable(false);
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE: //Anfrage an nutzer wurde verschickt.
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_anfrageVerschickt));
                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT: //Hat mir eine anfrage geschickt.
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorSearchingFriends));
                        Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });
                        viewHolderFriendAnfrage.imageViewCancelAnfrage.setVisibility(View.VISIBLE);
                        viewHolderFriendAnfrage.imageViewCancelAnfrage.setClickable(true);
                        viewHolderFriendAnfrage.imageViewCancelAnfrage.setFocusable(true);
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_i_got_anfrage));

                        break;

                    case ServerPolicy.POLICY_DETAIL_CASE_NOTHING: //Keine beziehungen, der Anfrage button wird angezeigt.
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_follow));
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

                        Glide.with(context).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });
                        break;

                    case ServerPolicy.POLICY_DETAIL_FOLLOWS_ME:
                        viewHolderFriendAnfrage.textViewFriendState.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                        viewHolderFriendAnfrage.textViewFriendState.setText(context.getResources().getString(R.string.txt_friend_status_follow_too));
                        Glide.with(context).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    viewHolderFriendAnfrage.textViewFriendState.setBackground(resource);
                                }
                            }
                        });

                    default:
                        break;
                }
                viewHolderFriendAnfrage.textViewUsername.setText(friendAnfrage.getUsername());
                viewHolderFriendAnfrage.textViewVorname.setText(friendAnfrage.getVorname());
                viewHolderFriendAnfrage.textViewMainLocation.setText(friendAnfrage.getRegion());

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(viewHolderFriendAnfrage.imageViewProfilbild,
                        null,
                        friendAnfrage.getUID(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;
        }
        return convertView;
    }

    public void updateItem(long FUID, short NewStatus)
    {
        for(int counter = 0; counter < this.list.size(); counter++)
        {
            Object object = this.list.get(counter);
            if(object instanceof SearchItemUser)
            {
                SearchItemUser searchItemIntern = (SearchItemUser) object;
                if(searchItemIntern.getUID() == FUID)
                {
                    searchItemIntern.setFriendStatus(NewStatus);
                    this.list.set(counter, searchItemIntern);
                    notifyDataSetChanged();
                    break;
                }
            }
            else if(object instanceof SocialFriendNegotiation)
            {
                SocialFriendNegotiation searchItemIntern = (SocialFriendNegotiation) object;
                if(searchItemIntern.getUID() ==  FUID)
                {
                    searchItemIntern.setAnfragenStatus(NewStatus);
                    this.list.set(counter, searchItemIntern);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }


    private final FriendStatusListener friendStatusListener = new FriendStatusListener()
    {
        @Override
        public void onStatusReceived(long CHAT_PARTNER_UID, short Status)
        {
            updateItem(CHAT_PARTNER_UID, Status);
        }

        @Override
        public void onStatusFailed(long CHAT_PARTNER_UID)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getResources().getString(R.string.txt_failedToHandleFriends));
            dialog.setMessage(context.getResources().getString(R.string.txt_failedToHandleFriendsDetails));
            dialog.show();
        }
    };
}
