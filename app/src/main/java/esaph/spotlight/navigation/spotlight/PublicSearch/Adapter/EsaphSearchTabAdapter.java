package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter;

import android.widget.BaseAdapter;

import java.util.List;

public abstract class EsaphSearchTabAdapter extends BaseAdapter
{
    public abstract void clearAll();
    public abstract void addAll(List<Object> list);
}
