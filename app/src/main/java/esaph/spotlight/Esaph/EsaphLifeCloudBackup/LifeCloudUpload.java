/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphLifeCloudBackup;

import java.util.ArrayList;

import esaph.spotlight.Esaph.EsaphLockable;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;

public class LifeCloudUpload extends EsaphLockable
{
    private static final String HASHTAG_CHAR = "#";
    private ArrayList<EsaphHashtag> esaphHashtag;
    private boolean emptyCloudToday = true;
    private String allHashtagsTogether;
    private String CLOUD_POST_DESCRIPTION;
    private String CLOUD_PID; //In database pid in cloud an in conversations when sending someone is the same. The server is inserting a normal post but marked as lifecloud share.
    private long CLOUD_TIME_UPLOADED;
    private short CLOUD_MESSAGE_STATUS; //2 possibilitys, die handelt je nachdem ob own message oder nicht.
    private short CLOUD_POST_TYPE;
    private short CLOUD_TYPE;

    public class LifeCloudStatus
    {
        public static final short STATE_FAILED_NOT_UPLOADED = 0;
        public static final short STATE_UPLOADING = 1;
        public static final short STATE_UPLOADED = 2;
    }

    public class EsaphLifeCloudTypeHelper
    {
        public static final short LIFECLOUD_TYPE_SPOTLIGHT_CAM = 0;
        public static final short LIFECLOUD_TYPE_USERGALLERY = 1;
    }

    public LifeCloudUpload() //When nothing from today was uploaded.
    {
        this.emptyCloudToday = true;
    }

    public LifeCloudUpload(ArrayList<EsaphHashtag> esaphHashtag,
                           String beschreibung,
                           String PID,
                           long uhrzeit,
                           short messageStatus,
                           short CLOUD_POST_TYPE,
                           short CLOUD_TYPE)
    {
        this.esaphHashtag = esaphHashtag;
        this.CLOUD_POST_DESCRIPTION = beschreibung;
        this.CLOUD_PID = PID;
        this.CLOUD_TIME_UPLOADED = uhrzeit;
        this.CLOUD_MESSAGE_STATUS = messageStatus;
        this.CLOUD_POST_TYPE = CLOUD_POST_TYPE;
        this.CLOUD_TYPE = CLOUD_TYPE;

        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(LifeCloudUpload.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(" ");
            }
        }
        this.allHashtagsTogether = stringBuilder.toString();
        this.emptyCloudToday = false;
    }

    public boolean isEmptyCloudToday() {
        return emptyCloudToday;
    }

    public ArrayList<EsaphHashtag> getEsaphHashtag() {
        return esaphHashtag;
    }

    public String getAllHashtagsTogether() {
        return allHashtagsTogether;
    }

    public String getCLOUD_POST_DESCRIPTION() {
        return CLOUD_POST_DESCRIPTION;
    }

    public String getCLOUD_PID() {
        return CLOUD_PID;
    }

    public long getCLOUD_TIME_UPLOADED() {
        return CLOUD_TIME_UPLOADED;
    }

    public short getCLOUD_MESSAGE_STATUS() {
        return CLOUD_MESSAGE_STATUS;
    }

    public void setCLOUD_MESSAGE_STATUS(short CLOUD_MESSAGE_STATUS) {
        this.CLOUD_MESSAGE_STATUS = CLOUD_MESSAGE_STATUS;
    }

    public short getCLOUD_TYPE_WHICH() {
        return CLOUD_TYPE;
    }

    public short getCLOUD_POST_TYPE() {
        return CLOUD_POST_TYPE;
    }
}
