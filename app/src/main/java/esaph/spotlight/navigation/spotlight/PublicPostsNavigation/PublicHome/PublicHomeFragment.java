package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.Background.LoadMoreHomeFeed;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicHome.VerticalViewPager.ViewPagerAdapterPublicHome;
import me.kaelaela.verticalviewpager.VerticalViewPager;

public class PublicHomeFragment extends EsaphGlobalCommunicationFragment
{
    private VerticalViewPager verticalViewPager;
    private ViewPagerAdapterPublicHome viewPagerAdapterPublicHome;




    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        verticalViewPager = null;
        viewPagerAdapterPublicHome = null;
    }

    public PublicHomeFragment()
    {
    }

    public static PublicHomeFragment getInstance()
    {
        return new PublicHomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_public_home, container, false);
        verticalViewPager = rootView.findViewById(R.id.verticalViewPager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPagerAdapterPublicHome = new ViewPagerAdapterPublicHome(getChildFragmentManager(), getContext());
        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position >= viewPagerAdapterPublicHome.getCount()-1)
                {
                    loadMore();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
        verticalViewPager.setAdapter(viewPagerAdapterPublicHome);
    }

    private ExecutorService executorService;
    private void loadMore()
    {
        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new LoadMoreHomeFeed());
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
