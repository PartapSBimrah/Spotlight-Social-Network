/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MemoryObjects;

public class InternMomentMember
{
    private String Benutzername;
    private boolean hasPosts;
    private boolean hasNewPostingsInMoment = false;
    private boolean canISaveSomething;
    private String postAnzahl;

    public InternMomentMember(String Username, int hasPosts, int hasNewPosts, boolean canISaveSomething)
    {
        this.canISaveSomething = canISaveSomething;
        this.Benutzername = Username;
        this.postAnzahl = ""+hasPosts;
        if(hasPosts > 0)
        {
            this.hasPosts = true;
        }
        else
        {
            this.hasPosts = false;
        }

        if(hasNewPosts == 1)
        {
            this.hasNewPostingsInMoment = true;
        }
    }

    public boolean haveUserPosts()
    {
        return hasPosts;
    }

    public String getBenutzername()
    {
        return Benutzername;
    }

    public boolean hasNewPosts()
    {
        return hasNewPostingsInMoment;
    }

    public boolean canISaveAllPosts()
    {
        return canISaveSomething;
    }

    public String getPostAnzahl()
    {
        return postAnzahl;
    }

}
