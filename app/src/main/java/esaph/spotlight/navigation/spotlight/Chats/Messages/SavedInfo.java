/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.Messages;

import java.io.Serializable;

public class SavedInfo implements Serializable
{
    private long USER_ID_SAVED;
    private long SAVED_ID;
    private String Username;

    public SavedInfo(long SAVED_ID, long USER_ID_SAVED, String username) {
        this.SAVED_ID = SAVED_ID;
        this.USER_ID_SAVED = USER_ID_SAVED;
        this.Username = username;
    }

    public SavedInfo(long USER_ID_SAVED) //Constructor for equals method.
    {
        this.USER_ID_SAVED = USER_ID_SAVED;
    }

    public long getSAVED_ID() {
        return SAVED_ID;
    }

    public long getUSER_ID_SAVED() {
        return USER_ID_SAVED;
    }

    public String getUsername() {
        return Username;
    }


    @Override
    public boolean equals(Object object)
    {
        System.out.println("Equals Debug: Called");
        boolean sameSame = false;

        if (object instanceof SavedInfo)
        {
            System.out.println("Equals Debug: Instance of long");
            SavedInfo savedInfo = (SavedInfo) object;
            sameSame = this.USER_ID_SAVED == savedInfo.getUSER_ID_SAVED();
        }

        System.out.println("Equals Debug: Result = " + sameSame);
        return sameSame;
    }
}
