package esaph.spotlight.navigation.spotlight.Moments.MemoryObjects;

import esaph.spotlight.navigation.spotlight.Moments.MomentPost;

public class AktuelleGruppe
{
    private MomentPost momentPost;
    private String MIID;
    private String Title;
    private String BeitragAnzahl;
    private String type;
    private String Admin;
    private String creator;
    private long PostTime;
    private boolean isSelected = false;
    private boolean leaved;

    public AktuelleGruppe(String MIID, String BeitragAnzahl, String Title, String type, long PostTime, String Admin, String creator, boolean leaved, MomentPost momentPost)
    {
        this.momentPost = momentPost;
        this.MIID = MIID;
        this.Title = Title;
        this.BeitragAnzahl = BeitragAnzahl;
        this.type = type;
        this.PostTime = PostTime;
        this.Admin = Admin;
        this.creator = creator;
        this.leaved = leaved;
    }

    public boolean didILeaved()
    {
        return leaved;
    }

    public String getAdmin()
    {
        return Admin;
    }

    public String getCreator()
    {
        return creator;
    }

    public boolean hasSomePosts()
    {
        if(momentPost != null)
        {
            return true;
        }
        return false;
    }

    public MomentPost getLastMomentPost()
    {
        return momentPost;
    }

    public String getLastMomentPostPid()
    {
        return momentPost.getPID();
    }

    public String getLastMomentPostType()
    {
        return momentPost.getFORMAT();
    }

    public String getLastPostUsername()
    {
        return momentPost.getUsername();
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }

    public boolean isSelectedHurensohn()
    {
        return isSelected;
    }

    public void setBeitragAnzahl(String BeitragAnzahl)
    {
        this.BeitragAnzahl = BeitragAnzahl;
    }

    public String getType()
    {
        return type;
    }

    public String getMIID() {
        return MIID;
    }

    public String getTitle() {
        return Title;
    }

    public String getBeitragAnzahl() {
        return BeitragAnzahl;
    }

    public long getLastPostTime() {
        return PostTime;
    }
}
