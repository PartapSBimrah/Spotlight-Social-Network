package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;

public class SQLSticker extends SQLSpotlight
{
    private Context context;
    private static final String DATABASE_NAME = "Spotlight.db";
    private static final String TABLE_NAME_STICKERS = "Stickers";


    private static final String TAG_STICKERS_CREATOR = "CREATOR";
    private static final String TAG_STICKERS_PACK_ID = "LSPID";
    private static final String TAG_STICKERS_STICKER_ID = "LSID";
    private static final String TAG_STICKERS_IMAGE_PID = "IMPID";
    private static final String TAG_TIME_CREATED_STICKER = "TIME_SP";


    private static final String TABLE_NAME_STICKER_PACK = "StickerPacks";
    private static final String TAG_STICKERPACK_PACKNAME = "LPN";
    private static final String TAG_TIME_CREATED_STICKER_PACK = "TIME_SP";
    private static final String TAG_STICKER_PACK_CREATOR = "CREATOR";


    private static final String createTableStickers = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_STICKERS +
            "(" +
            TAG_STICKERS_STICKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            TAG_STICKERS_IMAGE_PID + " TEXT, " +
            TAG_STICKERS_PACK_ID + " INTEGER, " +
            TAG_TIME_CREATED_STICKER + " DATETIME, " +
            TAG_STICKERS_CREATOR + " INTEGER)";


    private static final String createTableStickerPacks = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_STICKER_PACK +
            "(" +
            TAG_STICKERS_PACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            TAG_STICKERPACK_PACKNAME + " TEXT, " +
            TAG_TIME_CREATED_STICKER_PACK + " DATETIME, " +
            TAG_STICKER_PACK_CREATOR + " INTEGER)";



    public SQLSticker(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }

    public void dropTableStickers()
    {
        try
        {
            SQLiteDatabase db = SQLSticker.super.getWritableDatabase();
            db.delete(TABLE_NAME_STICKERS, null, null);
            db.delete(TABLE_NAME_STICKER_PACK, null, null);
            db.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "dropTableStickers(): failed: " + ec);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
    }

    public void onOnCreateDatabaseExternalCall(SQLiteDatabase db)
    {
        db.execSQL(createTableStickers);
        db.execSQL(createTableStickerPacks);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {
    }

    public void addSticker(EsaphSpotLightStickerPack esaphSpotLightStickerPack, EsaphSpotLightSticker esaphSpotLightSticker)
    {
        long stickerPackID = createStickerPackIfNotExists(esaphSpotLightStickerPack);
        esaphSpotLightSticker.setSTICKER_PACK_ID(stickerPackID);
        addSticker(esaphSpotLightSticker);
    }

    protected void addSticker(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLSticker.TAG_TIME_CREATED_STICKER, esaphSpotLightSticker.getStickerTimeCreated());
        contentValues.put(SQLSticker.TAG_STICKERS_CREATOR, esaphSpotLightSticker.getUIDCreator());
        contentValues.put(SQLSticker.TAG_STICKERS_PACK_ID, esaphSpotLightSticker.getSTICKER_PACK_ID());
        contentValues.put(SQLSticker.TAG_STICKERS_IMAGE_PID, esaphSpotLightSticker.getIMAGE_ID());
        sqLiteDatabase.insert(SQLSticker.TABLE_NAME_STICKERS,
                null,
                contentValues);
    }

    private long createStickerPackIfNotExists(EsaphSpotLightStickerPack esaphSpotLightStickerPack)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " +
                SQLSticker.TAG_STICKERS_PACK_ID +

                " FROM " + TABLE_NAME_STICKER_PACK
        + " WHERE " + TAG_STICKERS_PACK_ID + "=" + esaphSpotLightStickerPack.getLSPID() + " LIMIT 1", null);

        long stickerPackID = -1;

        if(cursor.moveToFirst())
        {
            stickerPackID = cursor.getLong(cursor.getColumnIndex(SQLSticker.TAG_STICKERS_PACK_ID));
        }
        else
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLSticker.TAG_STICKERPACK_PACKNAME, esaphSpotLightStickerPack.getPACK_NAME());
            contentValues.put(SQLSticker.TAG_STICKER_PACK_CREATOR, esaphSpotLightStickerPack.getUIDCreator());
            contentValues.put(SQLSticker.TAG_TIME_CREATED_STICKER_PACK, esaphSpotLightStickerPack.getTimeCreated());
            stickerPackID = sqLiteDatabase.insert(TABLE_NAME_STICKER_PACK,
                    null,
                    contentValues);
        }

        cursor.close();
        return stickerPackID;
    }

    private boolean stickerPackDied(long LSPID)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT 1 FROM " + TABLE_NAME_STICKERS
                + " WHERE " + TAG_STICKERS_PACK_ID + "=" + LSPID + " LIMIT 1", null);

        if(cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    protected void deleteStickerPack(long LSPID)
    {
        try
        {
            SQLiteDatabase db = SQLSticker.super.getWritableDatabase();

            db.delete(TABLE_NAME_STICKER_PACK,
                    TAG_STICKERS_PACK_ID + "=" + LSPID,
                    null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "deleteStickerPack() failed: " + ec);
        }
    }

    public void deleteSticker(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        try
        {
            SQLiteDatabase db = SQLSticker.super.getWritableDatabase();

            db.delete(TABLE_NAME_STICKERS,
                    TAG_STICKERS_STICKER_ID + "=" + esaphSpotLightSticker.getSTICKER_ID()
                    ,null);

            if(stickerPackDied(esaphSpotLightSticker.getSTICKER_PACK_ID()))
            {
                deleteStickerPack(esaphSpotLightSticker.getSTICKER_PACK_ID());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "deleteSticker() failed: " + ec);
        }
    }

    public boolean containsSticker(EsaphSpotLightSticker esaphSpotLightSticker)
    {
        SQLiteDatabase db = SQLSticker.super.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME_STICKERS + " WHERE " + TAG_STICKERS_STICKER_ID
                + "=" + esaphSpotLightSticker.getSTICKER_ID() +
                " AND " + TAG_STICKERS_PACK_ID + "=" +
                        esaphSpotLightSticker.getSTICKER_PACK_ID() +
                " LIMIT 1", null);

        if(cursor.moveToFirst())
        {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    public void addStickerWithStickerPack(EsaphSpotLightStickerPack esaphSpotLightStickerPack)
    {
        try
        {
            createStickerPackIfNotExists(esaphSpotLightStickerPack);

            List<EsaphSpotLightSticker> esaphSpotLightSticker = esaphSpotLightStickerPack.getEsaphSpotLightStickers();
            if(esaphSpotLightSticker != null)
            {
                int count = esaphSpotLightSticker.size();
                for(int counter = 0; counter < count; counter++)
                {
                    addSticker(esaphSpotLightSticker.get(counter));
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "addStickerWithStickerPack() failed: " + ec);
        }
    }

    public List<EsaphSpotLightStickerPack> getAllStickerPackLimiteOrderByTime()
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLSticker.super.getReadableDatabase();
            List<EsaphSpotLightStickerPack> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT "
                    + TAG_STICKERS_PACK_ID +
                    ", " + TAG_STICKERS_CREATOR +
                    ", " + TAG_STICKER_PACK_CREATOR +
                    ", " + TAG_TIME_CREATED_STICKER_PACK +
                    ", " + TAG_STICKERPACK_PACKNAME +
                    " FROM " + TABLE_NAME_STICKER_PACK
                    + " WHERE 1 GROUP BY " + TAG_STICKERS_PACK_ID + " ORDER BY " + TAG_TIME_CREATED_STICKER + " DESC",null);

            if(cursor.moveToFirst())
            {
                do {
                    list.add(new EsaphSpotLightStickerPack(
                            cursor.getString(cursor.getColumnIndex(TAG_STICKERPACK_PACKNAME)),
                            cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_PACK_ID)),
                            cursor.getLong(cursor.getColumnIndex(TAG_STICKER_PACK_CREATOR)),
                            cursor.getLong(cursor.getColumnIndex(TAG_TIME_CREATED_STICKER_PACK)),
                            getAllStickersFromPackAsList(cursor.getString(cursor.getColumnIndex(TAG_STICKERS_PACK_ID)))));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getAllStickerPackLimiteOrderByTime() failed: " + ec);
            return new ArrayList<>();
        }
        finally
        {
        }
    }

    public int getCountStickerPacks()
    {
        try
        {
            SQLiteDatabase sqLiteDatabase = SQLSticker.super.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM "
                    + TABLE_NAME_STICKER_PACK
                    + " WHERE 1 GROUP BY " + TAG_STICKERS_PACK_ID, null);

            int countStickerPacks = 0;
            if(cursor.moveToFirst())
            {
                countStickerPacks = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
            }

            cursor.close();
            return countStickerPacks;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getCountStickerPacks() failed: " + ec);
            return 0;
        }
        finally
        {
        }
    }

    public List<EsaphSpotLightSticker> getAllStickersFromPackAsList(String LSPID)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getReadableDatabase();
        List<EsaphSpotLightSticker> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_STICKERS + " WHERE " + TAG_STICKERS_PACK_ID + "=" + LSPID + " ORDER BY " +
                TAG_TIME_CREATED_STICKER, null);

        if(cursor.moveToFirst())
        {
            do {
                list.add(new EsaphSpotLightSticker(
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_CREATOR)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_STICKER_ID)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_PACK_ID)),
                        cursor.getString(cursor.getColumnIndex(TAG_STICKERS_IMAGE_PID)),
                        cursor.getLong(cursor.getColumnIndex(TAG_TIME_CREATED_STICKER))));
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public EsaphSpotLightSticker getSticker(String LSID, String LSPID)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getReadableDatabase();
        EsaphSpotLightSticker esaphSpotLightSticker = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_STICKERS + " WHERE " + TAG_STICKERS_PACK_ID + "=" + LSPID
                + " AND " + TAG_STICKERS_STICKER_ID + "=" + LSID
                + " ORDER BY " +
                TAG_TIME_CREATED_STICKER + " LIMIT 1", null);

        if(cursor.moveToFirst())
        {
            do {
                esaphSpotLightSticker = new EsaphSpotLightSticker(
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_CREATOR)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_STICKER_ID)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_PACK_ID)),
                        cursor.getString(cursor.getColumnIndex(TAG_STICKERS_IMAGE_PID)),
                        cursor.getLong(cursor.getColumnIndex(TAG_TIME_CREATED_STICKER)));
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        return esaphSpotLightSticker;
    }


    public EsaphSpotLightStickerPack getStickerPack(long LSPID)
    {
        SQLiteDatabase sqLiteDatabase = SQLSticker.super.getReadableDatabase();
        EsaphSpotLightStickerPack esaphSpotLightStickerPack = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_STICKER_PACK + " WHERE "
                + TAG_STICKERS_PACK_ID + "=" + LSPID + " LIMIT 1", null);


        if(cursor.moveToFirst())
        {
            esaphSpotLightStickerPack = new EsaphSpotLightStickerPack(
                        cursor.getString(cursor.getColumnIndex(TAG_STICKERPACK_PACKNAME)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKERS_PACK_ID)),
                        cursor.getLong(cursor.getColumnIndex(TAG_STICKER_PACK_CREATOR)),
                        cursor.getLong(cursor.getColumnIndex(TAG_TIME_CREATED_STICKER_PACK)),
                        getAllStickersFromPackAsList(cursor.getString(cursor.getColumnIndex(TAG_STICKERS_PACK_ID))));

        }

        cursor.close();
        return esaphSpotLightStickerPack;
    }


    public void updateStickerMessageLSIDAndPacket(EsaphStickerChatObject esaphStickerChatObject,
                                                  long NEW_LSID,
                                                  long NEW_LSPID)
    {
        try
        {
            SQLiteDatabase db = SQLSticker.super.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_STICKERS_PACK_ID, NEW_LSPID);

            db.update(TABLE_NAME_STICKERS,
                    contentValues,
                    TAG_STICKERS_PACK_ID + "=" + esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_PACK_ID(), null);

            db.update(TABLE_NAME_STICKER_PACK,
                    contentValues,
                    TAG_STICKERS_PACK_ID + "=" + esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_PACK_ID(), null);


            ContentValues contentValuesStickers = new ContentValues();
            contentValuesStickers.put(TAG_STICKERS_STICKER_ID, NEW_LSID);

            db.update(TABLE_NAME_STICKERS,
                    contentValuesStickers,
                    TAG_STICKERS_STICKER_ID + "=" + esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_ID(), null);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "updateStickerMessageLSIDAndPacket() failed: " + ec);
        }
    }
}
