package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile.Model;

import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile.UserProfilVisitShow;

public class UserAccountProfile
{
    private long UID;
    private String PBPID;
    private String Username;
    private UserProfilVisitShow.ProfilPolicy profilPolicy;
    private String Vorname;
    private String Beschreibung;
    private String FOLLOWER;
    private String FOLLOWING;
    private String AUFRUFE;

    public UserAccountProfile(long UID, String username, String PBPID, int profilPublicity, String vorname, String beschreibung, String FOLLOWER, String FOLLOWING, String AUFRUFE)
    {
        this.UID = UID;
        this.PBPID = PBPID;
        this.Username = username;
        this.profilPolicy = UserProfilVisitShow.ProfilPolicy.values()[profilPublicity];
        this.Vorname = vorname;
        this.Beschreibung = beschreibung;
        this.FOLLOWER = FOLLOWER;
        this.FOLLOWING = FOLLOWING;
        this.AUFRUFE = AUFRUFE;
    }

    public String getPBPID() {
        return PBPID;
    }

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return Username;
    }

    public UserProfilVisitShow.ProfilPolicy getProfilPolicy() {
        return profilPolicy;
    }

    public String getVorname() {
        return Vorname;
    }

    public String getBeschreibung() {
        return Beschreibung;
    }

    public String getFOLLOWER() {
        return FOLLOWER;
    }

    public String getFOLLOWING() {
        return FOLLOWING;
    }

    public String getAUFRUFE() {
        return AUFRUFE;
    }
}
