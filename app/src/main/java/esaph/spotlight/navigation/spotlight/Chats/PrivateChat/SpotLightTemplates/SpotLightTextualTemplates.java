package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;

public class SpotLightTextualTemplates
{
    public static List<ChatTextMessage> getTemplateList(Context context, int arrayID)
    {
        List<ChatTextMessage> chatTextMessages = new ArrayList<ChatTextMessage>();
        String[] arrayStrings = context.getResources().getStringArray(arrayID);
        String[] arraySpotTemplates = context.getResources().getStringArray(R.array.SPOT_TEMPLATES);

        int length = arrayStrings.length;
        for(int counter = 0; counter < length; counter++)
        {
            chatTextMessages.add(
                    new ChatTextMessage(arrayStrings[counter],
                            context.getResources().getResourceEntryName(arrayID).hashCode() + counter, //Use negativ numbers, to prevent conflicts on images. Because the id´s in the server are always positive.
                            -1,
                            -1,
                            -1,
                            (short)-1,
                            "",
                            arraySpotTemplates[counter]));
        }

        return chatTextMessages;
    }
}
