package esaph.spotlight.Esaph.EsaphListView;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class EsaphListViewKeepPosition extends ListView
{
    private boolean shouldBlockChilds;

    public EsaphListViewKeepPosition(Context context)
    {
        super(context);
    }

    public EsaphListViewKeepPosition(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EsaphListViewKeepPosition(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EsaphListViewKeepPosition(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setBlockLayoutChildren(boolean blockLayoutChildren)
    {
        this.shouldBlockChilds = blockLayoutChildren;
    }

    public boolean isShouldBlockChilds() {
        return shouldBlockChilds;
    }

    @Override
    protected void layoutChildren()
    {
        if (!shouldBlockChilds)
        {
            super.layoutChildren();
        }
    }
}
