/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import esaph.spotlight.R;

public class SQLCountrys extends SQLiteOpenHelper
{
    private Context context;
    private static final String DATABASE_NAME = "LocationAPI.db";

    private static final String tableName = "Locations";
    private static final String loc_Name = "LocationName";
    private static final String loc_ShortName = "LocationID";




    public SQLCountrys(Context context)
    {
        super(context, DATABASE_NAME, null, SQLSpotlight.getDatabaseVersion());
        this.context = context;
    }


    private static final String createTable = "create table if not exists " + tableName +
            "(" +
            loc_Name + " TEXT, " +
            loc_ShortName + " TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public void onUpgradeExternalCall(SQLiteDatabase db)
    {

    }


    private static final int countryCount = 230;
    public boolean checkIfDataBaseIsUpToDate()
    {
        try
        {
            SQLiteDatabase db = SQLCountrys.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE 1", null);
            cursor.moveToFirst();
            if(cursor.getCount() != countryCount || cursor.getCount() < countryCount)
            {
                Log.i(getClass().getName(), "Datenbank nicht aktuell, aktualisiere.");
                db.close();
                cursor.close();
                return setupDatabase();
            }
            else
            {
                db.close();
                cursor.close();
                Log.i(getClass().getName(), "Datenbank ok.");
                return true;
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "checkIfdataBaseIsUpToDate(EXCEPTION): " + ec);
            return false;
        }
    }


    private boolean setupDatabase()
    {
        try
        {
            SQLiteDatabase db = SQLCountrys.super.getReadableDatabase();
            String[] arrayListName = context.getResources().getStringArray(R.array.XX_Locations_Country);
            String[] arrayListNameID = context.getResources().getStringArray(R.array.XX_Locations_CountryID);
            ContentValues cv = new ContentValues();
            Log.i(getClass().getName(), "Land_L: " + arrayListName.length + " UIC_L: " + arrayListNameID.length);
            for(int counter = 0; counter < arrayListNameID.length; counter++)
            {
                Log.i(getClass().getName(), "Land: " + arrayListName[counter] + " UIC: " + arrayListNameID[counter]);
                cv.put(loc_Name, arrayListName[counter]);
                cv.put(loc_ShortName, arrayListNameID[counter]);
                db.insert(tableName, null, cv);
                cv.clear();
            }

            db.close();
            return true;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "");
            return false;
        }
    }





    public String lookUpShortNameForCountry(String country)
    {
        try
        {
            SQLiteDatabase db = SQLCountrys.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + loc_ShortName + " FROM " + tableName + " WHERE " + loc_Name + "='" + country + "'", null);
            String shortName = null;
            if(cursor.moveToFirst())
            {
                shortName = cursor.getString(cursor.getColumnIndex(loc_ShortName));
                db.close();
                cursor.close();
                return shortName;
            }
            db.close();
            cursor.close();
            return "NOCOUNTRYNAME";
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "lookUpShortName(EXEPTION): " + ec);
            return null;
        }
    }



    public String lookUpLongNameForCountry(String countryCode)
    {
        try
        {
            SQLiteDatabase db = SQLCountrys.super.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + loc_Name + " FROM " + tableName + " WHERE " + loc_ShortName + "='" + countryCode + "'", null);
            String longName = null;
            if(cursor.moveToFirst())
            {
                longName = cursor.getString(cursor.getColumnIndex(loc_Name));
                db.close();
                cursor.close();
                return longName;
            }
            db.close();
            cursor.close();
            return "NOSHORTCOUNTRYNAME";
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "lookUpLongName(EXEPTION): " + ec);
            return null;
        }
    }
}
