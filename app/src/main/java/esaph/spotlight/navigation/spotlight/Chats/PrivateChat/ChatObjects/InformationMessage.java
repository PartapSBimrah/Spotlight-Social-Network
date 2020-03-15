/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects;

public class InformationMessage
{
    private String textInformation;
    public InformationMessage(String textInformation)
    {
        this.textInformation = textInformation;
    }

    public String getTextInformation() {
        return textInformation;
    }
}
