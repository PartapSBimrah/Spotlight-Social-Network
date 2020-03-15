package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.TodayOurStory.Background;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightViewPagerAdapter;

public class RunnableLoadMoreStoryViewerTodayBetweenPartners implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<SpotLightViewPagerAdapter> spotLightViewPagerAdapterWeakReference;
    private WeakReference<ViewPager> viewPagerWeakReference;
    private WeakReference<ViewPager.OnPageChangeListener> onPageChangeListenerWeakReference;
    private long UID;

    public RunnableLoadMoreStoryViewerTodayBetweenPartners(Context context,
                                                           long UID,
                                                           SpotLightViewPagerAdapter spotLightViewPagerAdapter,
                                                           ViewPager viewPager,
                                                           ViewPager.OnPageChangeListener onPageChangeListener)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.UID = UID;
        this.spotLightViewPagerAdapterWeakReference = new WeakReference<>(spotLightViewPagerAdapter);
        this.viewPagerWeakReference = new WeakReference<>(viewPager);
        this.onPageChangeListenerWeakReference = new WeakReference<>(onPageChangeListener);
    }

    @Override
    public void run()
    {
        SQLChats sqlChats = null;
        try
        {
            sqlChats = new SQLChats(this.contextWeakReference.get());
            final List<Object> list = sqlChats.getTodayConversationMessagesBetweenPartners(
                    UID,
                    spotLightViewPagerAdapterWeakReference.get().getCount());

            if(list.isEmpty())
                return;

            ((AppCompatActivity)contextWeakReference.get()).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        spotLightViewPagerAdapterWeakReference.get().getList().addAll(list);
                        spotLightViewPagerAdapterWeakReference.get().notifyDataSetChanged();
                        viewPagerWeakReference.get().post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onPageChangeListenerWeakReference.get().onPageSelected(viewPagerWeakReference.get().getCurrentItem());
                            }
                        });
                    }
                    catch (Exception ec)
                    {
                    }
                }
            });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreLifeCloudTodayPosts run() failed: " + ec);
        }
        finally
        {
            if(sqlChats != null)
            {
                sqlChats.close();
            }
        }
    }
}
