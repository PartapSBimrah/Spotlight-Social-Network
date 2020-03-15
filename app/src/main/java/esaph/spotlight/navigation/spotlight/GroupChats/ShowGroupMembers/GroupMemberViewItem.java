/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.GroupChats.ShowGroupMembers;

public class GroupMemberViewItem
{
    private String Username;
    private String Vorname;
    private short WatchingStatus;
    private boolean isAdmin;

    public GroupMemberViewItem(String Username, String Vorname, short watchingStatus, boolean isAdmin)
    {
        this.Username = Username;
        this.Vorname = Vorname;
        this.WatchingStatus = watchingStatus;
        this.isAdmin = isAdmin;
    }

    public boolean getisAdmin()
    {
        return isAdmin;
    }

    public String getUsername() {
        return Username;
    }

    public String getVorname() {
        return Vorname;
    }

    public short getWatchingStatus() {
        return WatchingStatus;
    }

    public void setFriendStatus(short newStatus)
    {
        this.WatchingStatus = newStatus;
    }
}
