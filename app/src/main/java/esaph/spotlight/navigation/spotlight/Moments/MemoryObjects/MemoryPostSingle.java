/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MemoryObjects;

public class MemoryPostSingle
{
    private String MIID;
    private String Title;
    private String BeitragAnzahl;
    private String type;
    private long PostTime;
    private boolean isSelected = false;

    public MemoryPostSingle(String MIID, String BeitragAnzahl, String Title, String type, long PostTime)
    {
        this.MIID = MIID;
        this.Title = Title;
        this.BeitragAnzahl = BeitragAnzahl;
        this.type = type;
        this.PostTime = PostTime;
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }

    public boolean isSelectedHurensohn()
    {
        return isSelected;
    }

    public void setBeitragAnzahl(String BeitragAnzahl)
    {
        this.BeitragAnzahl = BeitragAnzahl;
    }

    public String getType()
    {
        return type;
    }

    public String getMIID() {
        return MIID;
    }

    public String getTitle() {
        return Title;
    }

    public String getBeitragAnzahl() {
        return BeitragAnzahl;
    }

    public long getPostTime() {
        return PostTime;
    }
}
