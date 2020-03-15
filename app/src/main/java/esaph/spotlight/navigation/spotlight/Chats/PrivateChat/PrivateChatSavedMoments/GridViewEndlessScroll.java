package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class GridViewEndlessScroll extends RecyclerView.OnScrollListener
{
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public GridViewEndlessScroll(LinearLayoutManager linearLayoutManager)
    {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public abstract void onScrolledVertical(int offset);


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);
        onScrolledVertical(recyclerView.computeVerticalScrollOffset());

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading)
        {
            if (totalItemCount > previousTotal)
            {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
        {
            current_page++;
            onLoadMore(totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}