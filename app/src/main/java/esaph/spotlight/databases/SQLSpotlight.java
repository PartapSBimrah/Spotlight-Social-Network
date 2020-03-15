/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.databases;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SQLSpotlight extends SQLiteOpenHelper
{
    private Context context;
    private static final int DATA_BASE_VERSION = 3;

    public static int getDatabaseVersion() {
        return DATA_BASE_VERSION;
    }

    public SQLSpotlight(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public SQLSpotlight(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        SQLHashtags sqlHashtags = new SQLHashtags(context);
        sqlHashtags.onOnCreateDatabaseExternalCall(db);
        sqlHashtags.close();

        SQLUploads sqlUploads = new SQLUploads(context);
        sqlUploads.onOnCreateDatabaseExternalCall(db);
        sqlUploads.close();

        SQLChats sqlChats = new SQLChats(context);
        sqlChats.onOnCreateDatabaseExternalCall(db);
        sqlChats.close();

        SQLFriends sqlWatcher = new SQLFriends(context);
        sqlWatcher.onOnCreateDatabaseExternalCall(db);
        sqlWatcher.close();

        SQLGroups sqlGroups = new SQLGroups(context);
        sqlGroups.onOnCreateDatabaseExternalCall(db);
        sqlGroups.close();

        SQLSticker sqlSticker = new SQLSticker(context);
        sqlSticker.onOnCreateDatabaseExternalCall(db);
        sqlSticker.close();

        SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(context);
        sqlLifeCloud.onOnCreateDatabaseExternalCall(db);
        sqlLifeCloud.close();

        SQLPublicPosts sqlPublicPosts = new SQLPublicPosts(context);
        sqlPublicPosts.onOnCreateDatabaseExternalCall(db);
        sqlPublicPosts.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        SQLHashtags sqlHashtags = new SQLHashtags(context);
        sqlHashtags.onUpgradeExternalCall(db);
        sqlHashtags.close();

        SQLUploads sqlUploads = new SQLUploads(context);
        sqlUploads.onUpgradeExternalCall(db);
        sqlUploads.close();

        SQLChats sqlChats = new SQLChats(context);
        sqlChats.onUpgradeExternalCall(db);
        sqlChats.close();

        SQLFriends sqlWatcher = new SQLFriends(context);
        sqlWatcher.onUpgradeExternalCall(db);
        sqlWatcher.close();

        SQLGroups sqlGroups = new SQLGroups(context);
        sqlGroups.onUpgradeExternalCall(db);
        sqlGroups.close();

        SQLSticker sqlSticker = new SQLSticker(context);
        sqlSticker.onUpgradeExternalCall(db);
        sqlSticker.close();

        SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(context);
        sqlLifeCloud.onUpgradeExternalCall(db);
        sqlLifeCloud.close();

        SQLPublicPosts sqlPublicPosts = new SQLPublicPosts(context);
        sqlPublicPosts.onUpgradeExternalCall(db);
        sqlPublicPosts.close();
    }
}
