package esaph.spotlight.navigation.globalActions;

public abstract class ConversationStatusHelper
{
    public static final short STATUS_FAILED_TO_SEND_OR_RECEIVE = -1;
    public static final short STATUS_SENDING = 0;
    public static final short STATUS_SENT = 1;
    public static final short STATUS_CHAT_OPENED = 2; //BOT DIRECTIONS, WENN DU ÖFFNEST ODER DEINER GEÖFFNET WURDE.
    public static final short STATUS_NEW_MESSAGE = 3;
}

    /*
    ownMessage == TRUE
    -1 failed to send
    0 means, sending
    1 means, currently sent
    2 means, user seen it.


    ownMessage == FALSE
    3 means, new message
    2 means, message has been seen.
     */