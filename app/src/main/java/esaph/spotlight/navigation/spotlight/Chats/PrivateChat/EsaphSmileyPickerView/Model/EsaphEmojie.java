/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model;

import java.io.Serializable;

public class EsaphEmojie implements Serializable
{
    private String EMOJIE;

    public EsaphEmojie(String EMOJIE) {
        this.EMOJIE = EMOJIE;
    }

    public String getEMOJIE() {
        return EMOJIE;
    }
}
