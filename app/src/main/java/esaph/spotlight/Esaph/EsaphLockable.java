package esaph.spotlight.Esaph;

public class EsaphLockable
{
    private boolean locked = false;

    public synchronized boolean isLocked()
    {
        return locked;
    }

    public synchronized void setLocked(boolean locked) {
        this.locked = locked;
    }
}
