package esaph.spotlight.navigation.spotlight.Chats.Messages;

import esaph.spotlight.navigation.globalActions.CMTypes;

public class ChatVideo extends ConversationMessage
{
    private long SERVER_ID;
    private String Beschreibung;
    private String PID;

    public ChatVideo(
            long SERVER_ID,
            long _ID,
            long ABS_ID,
            long ID_CHAT,
            long uhrzeit,
            short status,
            String Beschreibung,
            String PID,
            String Absender)
    {
        super(_ID, ABS_ID, ID_CHAT, uhrzeit, status, CMTypes.FVID, Absender, null);
        this.SERVER_ID = SERVER_ID;
        this.Beschreibung = Beschreibung;
        this.PID = PID;

        /*
        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(ChatVideo.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }*/

        super.setIMAGE_ID(PID);
        super.setMESSAGE_ID_SERVER(SERVER_ID);
    }

    public long getSERVER_ID() {
        return SERVER_ID;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public String getIMAGE_ID() {
        return PID;
    }
}
