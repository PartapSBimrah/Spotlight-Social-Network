package esaph.spotlight.navigation.spotlight.Moments;

public class GalleryItem
{
    private String datumSpotLightStyle;
    private String PID;
    private short Type;
    private long datumInMillis;
    private boolean IsChats;

    public GalleryItem(String datumSpotLightStyle, String PID, short Type, long datumInMillis, boolean IsChats)
    {
        this.datumSpotLightStyle = datumSpotLightStyle;
        this.PID = PID;
        this.Type = Type;
        this.IsChats = IsChats;
        this.datumInMillis = datumInMillis;
    }

    public boolean isObjectChats()
    {
        return IsChats;
    }

    public String getDatumSpotLightStyle() {
        return datumSpotLightStyle;
    }

    public String getPID() {
        return PID;
    }

    public short getType()
    {
        return Type;
    }

    public long getDatumInMillis() {
        return datumInMillis;
    }
}
