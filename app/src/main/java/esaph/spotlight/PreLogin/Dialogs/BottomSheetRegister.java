/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import esaph.spotlight.PreLogin.LoginActivity;
import esaph.spotlight.PreLogin.Registration.RegisterActivity;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.StorageManagment.StorageHandlerSticker;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFeed;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;

public class BottomSheetRegister extends BottomSheetDialogFragment
{
    private ImageView imageViewCloseDown;
    private TextView textViewRegister;
    private TextView textViewLogin;

    public static BottomSheetRegister getInstance()
    {
        return new BottomSheetRegister();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_bottom_sheet_register, container, false);
        imageViewCloseDown = rootView.findViewById(R.id.imageViewBottomSheetDown);
        textViewRegister = rootView.findViewById(R.id.textViewBottomSheetRegisterRegister);
        textViewLogin = rootView.findViewById(R.id.textViewBottomSheetRegisterLogin);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewCloseDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });


        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                clearUserData();
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);

                Activity activity = getActivity();
                if(activity != null)
                {
                    dismiss();
                    activity.finish();
                }
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearUserData();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                Activity activity = getActivity();
                if(activity != null)
                {
                    dismiss();
                    activity.finish();
                }
            }
        });
    }


    private void clearUserData()
    {
        try
        {
            Context context = getContext();
            if(context != null)
            {
                CLPreferences preferences = new CLPreferences(context);
                preferences.logOut();

                StorageHandler.dropAllFiles(context);
                StorageHandlerProfilbild.dropAllFiles(context);

                SQLFriends friends = new SQLFriends(context);
                friends.dropTableWatcher();
                friends.close();

                SQLChats chats = new SQLChats(context);
                chats.dropTableChats();
                chats.close();

                SQLSticker sqlSticker = new SQLSticker(context);
                sqlSticker.dropTableStickers();
                sqlSticker.close();

                SQLHashtags sqlHashtags = new SQLHashtags(context);
                sqlHashtags.dropTableHashtags();
                sqlHashtags.close();

                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(context);
                sqlLifeCloud.dropTableLifeCloud();
                sqlLifeCloud.close();

                SQLGroups sqlMemorys = new SQLGroups(context);
                sqlMemorys.dropAllDataMoments();
                sqlMemorys.close();

                SQLUploads sqlUploads = new SQLUploads(context);
                sqlUploads.dropTables();
                sqlUploads.close();

                SQLFeed sqlFeed = new SQLFeed(context);
                sqlFeed.dropAllData();
                sqlFeed.close();

                context.stopService(new Intent(getActivity(), MsgServiceConnection.class));
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "clearUserData() failed: " + ec);
        }
    }
}
