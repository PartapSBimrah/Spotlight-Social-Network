package esaph.spotlight.spots.SpotMaker;

import java.io.Serializable;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public interface EsaphSpotMakerListener extends Serializable
{
    void onDoneEditingSent(ConversationMessage conversationMessage);
}
