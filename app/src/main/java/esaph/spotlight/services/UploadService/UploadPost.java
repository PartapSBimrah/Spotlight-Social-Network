/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.UploadService;

import org.json.JSONArray;
import java.util.ArrayList;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;

public class UploadPost
{
    private static final String HASHTAG_CHAR = "#";
    private long MESSAGE_ID;
    private String allHashtagsTogether;
    private String PreviewForList;
    private String Beschreibung;
    private String PID;
    private JSONArray jsonArrayWAMP;
    private long shootTime;
    private short type;
    private int progressUploading = 0;

    private ArrayList<EsaphHashtag> esaphHashtag;

    public UploadPost(long MESSAGE_ID,
                      short type,
                      String PID,
                      String PreviewForList,
                      String Beschreibung,
                      long shootTime,
                      JSONArray jsonArrayWAMP,
                      ArrayList<EsaphHashtag> esaphHashtag)
    {
        this.esaphHashtag = esaphHashtag;
        this.jsonArrayWAMP = jsonArrayWAMP;
        this.MESSAGE_ID = MESSAGE_ID;
        this.PID = PID;
        this.PreviewForList = PreviewForList;
        this.Beschreibung = Beschreibung;
        this.shootTime = shootTime;
        this.type = type;

        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(UploadPost.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }

        this.allHashtagsTogether = stringBuilder.toString();
    }

    public long getMESSAGE_ID() {
        return MESSAGE_ID;
    }

    public String getBeschreibung()
    {
        return Beschreibung;
    }

    public String getPreviewForList() {
        return PreviewForList;
    }

    public String getPID() {
        return PID;
    }

    public JSONArray getJsonArrayWAMP()
    {
        return jsonArrayWAMP;
    }

    public long getShootTime() {
        return shootTime;
    }

    public short getType() {
        return type;
    }

    public ArrayList<EsaphHashtag> getEsaphHashtag() {
        return esaphHashtag;
    }

    public String getHashtagsTogether()
    {
        return allHashtagsTogether;
    }

    public void setProgressUploading(int progressUploading) {
        this.progressUploading = progressUploading;
    }

    public int getProgressUploading() {
        return progressUploading;
    }
}
