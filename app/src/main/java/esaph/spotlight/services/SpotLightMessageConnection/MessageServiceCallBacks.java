package esaph.spotlight.services.SpotLightMessageConnection;

import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;

public interface MessageServiceCallBacks
{
    public void onFriendUpdate(short FRIEND_STATUS, ChatPartner chatPartner);
    public void onUserRemovedPost(ChatPartner chatPartner, long MESSAGE_ID);
    public void onUserAllowedToSeePostAgain(ChatPartner chatPartner, ChatInfoStateMessage infoStateMessage, long MESSAGE_ID);
    public void onUserDisallowedToSeePost(ChatPartner chatPartner, ChatInfoStateMessage infoStateMessage, long MESSAGE_ID);
    public void onUserUpdateInsertNewContent(ChatPartner chatPartner, ConversationMessage conversationMessage);

    public void onMessageUpdate(ConversationMessage conversationMessage, ChatInfoStateMessage chatInfoStateMessage);
    public void onMessageUpdate(ChatTextMessage chatTextMessage);
    public void onMessageUpdate(AudioMessage audioMessage);
    public void onMessageUpdate(EsaphStickerChatObject esaphStickerChatObject);
    public void onMessageUpdate(EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject);
    public void onUpdateUserTyping(long USER_ID, boolean typing);
}
