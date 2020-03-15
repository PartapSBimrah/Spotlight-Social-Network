/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model;

public class SearchStatus
{
    private int state;
    private String Status;

    public SearchStatus(String Status, int state)
    {
        this.Status = Status;
        this.state = state;
    }

    public String getStatus()
    {
        return Status;
    }

    public int getState()
    {
        return state;
    }
}
