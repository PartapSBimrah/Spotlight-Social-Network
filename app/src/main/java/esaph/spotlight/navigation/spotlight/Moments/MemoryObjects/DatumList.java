package esaph.spotlight.navigation.spotlight.Moments.MemoryObjects;

public class DatumList
{
    private String datumSpotLightStyle;
    private String UniqueDatumFormat;
    private long normalTimeInMillis;

    public DatumList(String datumSpotLightStyle, String UniqueDatumFormat, long normalTimeInMillis)
    {
        this.datumSpotLightStyle = datumSpotLightStyle;
        this.UniqueDatumFormat = UniqueDatumFormat;
        this.normalTimeInMillis = normalTimeInMillis;
    }

    public String getNormalUniqueDatumFormat()
    {
        return this.UniqueDatumFormat;
    }

    public long getMillis()
    {
        return this.normalTimeInMillis;
    }

    public String getDatumSpotLightStyle()
    {
        return this.datumSpotLightStyle;
    }
}
