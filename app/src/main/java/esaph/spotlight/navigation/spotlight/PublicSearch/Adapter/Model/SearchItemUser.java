/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model;

public class SearchItemUser
{
    private String mainLocation;
    private String Username;
    private String Vorname;
    private short WatchingStatus;
    private long UID;

    public SearchItemUser(long UID, String Username, String Vorname, short watchingStatus, String Land) //For User //Letzte Variable ist Useless.
    {
        this.UID = UID;
        this.Username = Username;
        this.Vorname = Vorname;
        this.WatchingStatus = watchingStatus;
        this.mainLocation = Land;
    }

    public long getUID() {
        return UID;
    }

    public void setFriendStatus(short newStatus)
    {
        this.WatchingStatus = newStatus;
    }

    public short getWatchingStatus()
    {
        return WatchingStatus;
    }

    public String getMainLocation()
    {
        return mainLocation;
    }

    public String getUsername() {
        return Username;
    }

    public String getVorname() {
        return Vorname;
    }
}
