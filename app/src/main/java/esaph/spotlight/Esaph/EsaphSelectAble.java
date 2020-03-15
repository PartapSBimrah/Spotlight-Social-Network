package esaph.spotlight.Esaph;

public abstract class EsaphSelectAble
{
    private boolean selected = false;

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelection(boolean selected)
    {
        this.selected = selected;
    }
}
