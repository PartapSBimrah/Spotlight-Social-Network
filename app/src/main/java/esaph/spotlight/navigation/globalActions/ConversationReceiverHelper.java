/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class ConversationReceiverHelper
{
    public static JSONArray getReceiverFromMessage(ConversationMessage conversationMessage) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ST", conversationMessage.getMessageStatus());
        jsonObject.put("REC_ID", conversationMessage.getID_CHAT());
        jsonArray.put(jsonObject);
        return jsonArray;
    }

    public static JSONArray getOwnReceiver(ConversationMessage conversationMessage, long OWN_UID) throws JSONException //I am the receiver.
    {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ST", conversationMessage.getMessageStatus());
        jsonObject.put("REC_ID", OWN_UID);
        jsonArray.put(jsonObject);
        return jsonArray;
    }

}
