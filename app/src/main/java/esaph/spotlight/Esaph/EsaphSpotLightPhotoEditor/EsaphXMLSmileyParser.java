/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EsaphXMLSmileyParser
{
    public static List<String> parse(Context context, int xmlData)
    {
        ArrayList<String> jokes = new ArrayList<String>();
        XmlResourceParser parser = context.getResources().getXml(xmlData);

        try
        {
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("value"))
                        {
                            jokes.add(parser.nextText());
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }
        catch (XmlPullParserException | IOException e)
        {
            Log.e("XmlPullParserException", e.toString());
        }
        parser.close();
        return jokes;
    }


    public static String parseOnlyFirst(Context context, int xmlData)
    {
        XmlResourceParser parser = context.getResources().getXml(xmlData);

        try
        {
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("value"))
                        {
                            return parser.nextText();
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }
        catch (XmlPullParserException | IOException e)
        {
            Log.e("XmlPullParserException", e.toString());
        }
        parser.close();
        return "";
    }
}
