package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model;

public class SearchStatus
{
    private int state;
    private String Status;

    public SearchStatus(String Status, int state)
    {
        this.Status = Status;
        this.state = state;
    }

    public String getStatus()
    {
        return Status;
    }

    public int getState()
    {
        return state;
    }
}
