/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.cache.memory;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;

public class EsaphImageLoaderMemoryCache
{
    private Map<String, Bitmap> cachePostBild = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
    private long size = 0;
    private long limit = 5000000;

    public EsaphImageLoaderMemoryCache()
    {
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }


    public void setLimit(long new_limit)
    {
        System.out.println("Memory preview cache limit set to: " + new_limit);
        limit = new_limit;
    }

    public void removeFromCache(String ID, EsaphDimension esaphDimension)
    {
        try
        {
            Log.i(getClass().getName(), "REMOVING FROM CACHE");
            cachePostBild.remove(ID + esaphDimension.getComparedDimensions());
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "REMOVING FROM CACHE FAILED: " + ec);
        }
    }

    public Bitmap getDataFromKey(String ID, EsaphDimension esaphDimension)
    {
        try
        {
            Log.i(getClass().getName(), "ASKING FOR POST CACHE");
            if(!cachePostBild.containsKey(ID + esaphDimension.getComparedDimensions()))
            {
                return null;
            }
            else
            {
                return cachePostBild.get(ID + esaphDimension.getComparedDimensions());
            }
        }
        catch (Exception ec)
        {
            return null;
        }
    }


    public void putPostBild(String ID, EsaphDimension esaphDimension, Bitmap bitmap)
    {
        try
        {
            if(cachePostBild.containsKey(ID + esaphDimension.getComparedDimensions()))
            {
                size -= getSizeInBytes(cachePostBild.get(ID + esaphDimension.getComparedDimensions()));
            }
            cachePostBild.put(ID + esaphDimension.getComparedDimensions(), bitmap);
            size += getSizeInBytes(bitmap);
            checkSizePostBild();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "EsaphImageLoaderMemoryCache, putPostBild() failed: " + ec);
        }
    }


    private void checkSizePostBild()
    {
        if(size > limit)
        {
            Iterator<Map.Entry<String, Bitmap>> iterator = cachePostBild.entrySet().iterator();
            while(iterator.hasNext())
            {
                Map.Entry<String, Bitmap> entry = iterator.next();
                size -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if(size <= limit)
                {
                    break;
                }
            }
            clearCache();
        }
    }


    public void clearCache()
    {
        try
        {
            System.out.println("MEMORY: CLEARING CLEARING");
            cachePostBild.clear();
            size = 0;
        }
        catch (Exception np)
        {
            Log.i(getClass().getName(), "clearPostBildCache() failed: " + np);
        }
    }


    private long getSizeInBytes(Bitmap bitmap)
    {
        if(bitmap == null)
        {
            return 0;
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }



}
