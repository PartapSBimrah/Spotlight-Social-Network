package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model;

import java.io.Serializable;
import java.util.ArrayList;

import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;

public class PublicPost implements Serializable
{
    private static final String HASHTAG_CHAR = "#";
    private long _ID;
    private long UID;
    private String Username;
    private short State;
    private ArrayList<EsaphHashtag> esaphHashtag;
    private String allHashtagsTogether;
    private String Beschreibung;
    private long SERVER_ID_POST;
    private String PID;
    private long Uhrzeit;
    private short type;
    private boolean iSaved = false;


    public PublicPost(long _ID, long UID, long SERVER_ID_POST,
                      String Username, ArrayList<EsaphHashtag> esaphHashtag, String beschreibung, String PID, long uhrzeit, short type,
                      short State,
                      boolean iSaved)
    {
        this._ID = _ID;
        this.State = State;
        this.Username = Username;
        this.UID = UID;
        this.SERVER_ID_POST = SERVER_ID_POST;
        this.esaphHashtag = esaphHashtag;
        this.Beschreibung = beschreibung;
        this.PID = PID;
        this.Uhrzeit = uhrzeit;
        this.type = type;
        this.iSaved = iSaved;


        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(PublicPost.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }

        this.allHashtagsTogether = stringBuilder.toString();

    }


    public PublicPost(long _ID, long UID, long SERVER_ID_POST,
                      String Username,
                      String beschreibung,
                      String PID,
                      long uhrzeit,
                      short type,
                      short State,
                      boolean iSaved)
    {
        this._ID = _ID;
        this.State = State;
        this.Username = Username;
        this.UID = UID;
        this.SERVER_ID_POST = SERVER_ID_POST;
        this.Beschreibung = beschreibung;
        this.PID = PID;
        this.Uhrzeit = uhrzeit;
        this.type = type;
        this.iSaved = iSaved;
    }


    public void setEsaphHashtag(ArrayList<EsaphHashtag> esaphHashtag) {
        this.esaphHashtag = esaphHashtag;
        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < esaphHashtag.size(); counter++)
        {
            EsaphHashtag esaphHashtagIntern = esaphHashtag.get(counter);
            stringBuilder.append(PublicPost.HASHTAG_CHAR);
            stringBuilder.append(esaphHashtagIntern.getHashtagName());
            if((counter + 1) < esaphHashtag.size()) //HAS NEXT?
            {
                stringBuilder.append(", ");
            }
        }

        this.allHashtagsTogether = stringBuilder.toString();
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public short getState() {
        return State;
    }

    public long get_ID() {
        return _ID;
    }

    public long getUID() {
        return UID;
    }

    public long getSERVER_ID_POST() {
        return SERVER_ID_POST;
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

    public boolean isiSaved() {
        return iSaved;
    }
}
