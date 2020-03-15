package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

public class EsaphDimension
{
    private int width;
    private int height;

    public EsaphDimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getComparedDimensions()
    {
        return "" + width + height;
    }

    public int getComparedDimensionsInt()
    {
        return width + height;
    }
}
