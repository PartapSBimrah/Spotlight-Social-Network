package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model;

import java.io.Serializable;
import java.util.ArrayList;

import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;

public class PublicConversationMessage implements Serializable
{
    private long UID;
    private String Username;
    private static final String HASHTAG_CHAR = "#";
    private ArrayList<EsaphHashtag> esaphHashtag;
    private String allHashtagsTogether;
    private String Beschreibung;
    private long SERVER_ID_POST;
    private String PID;
    private long Uhrzeit;
    private short type;
    private String COUNT_Comments;
    private String COUNT_Saved;
    private String COUNT_Shared;
    private boolean iSaved = false;


    public PublicConversationMessage(long UID, long SERVER_ID_POST,
                                     String Username, ArrayList<EsaphHashtag> esaphHashtag, String beschreibung, String PID, long uhrzeit, short type, String COUNT_Comments, String COUNT_Saved, String COUNT_Shared, boolean iSaved)
    {
        this.Username = Username;
        this.UID = UID;
        this.SERVER_ID_POST = SERVER_ID_POST;
        this.esaphHashtag = esaphHashtag;
        this.Beschreibung = beschreibung;
        this.PID = PID;
        this.Uhrzeit = uhrzeit;
        this.type = type;
        this.COUNT_Comments = COUNT_Comments;
        this.COUNT_Saved = COUNT_Saved;
        this.COUNT_Shared = COUNT_Shared;
        this.iSaved = iSaved;


        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(PublicConversationMessage.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }

        this.allHashtagsTogether = stringBuilder.toString();

    }


    public long getSERVER_ID_POST() {
        return SERVER_ID_POST;
    }

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return Username;
    }

    public static String getHashtagChar() {
        return HASHTAG_CHAR;
    }

    public ArrayList<EsaphHashtag> getEsaphHashtag() {
        return esaphHashtag;
    }

    public String getAllHashtagsTogether() {
        return allHashtagsTogether;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public String getPID() {
        return PID;
    }

    public long getUhrzeit() {
        return Uhrzeit;
    }

    public short getType() {
        return type;
    }

    public String getCOUNT_Comments() {
        return COUNT_Comments;
    }

    public String getCOUNT_Saved() {
        return COUNT_Saved;
    }

    public String getCOUNT_Shared() {
        return COUNT_Shared;
    }

    public boolean isiSaved() {
        return iSaved;
    }
}
