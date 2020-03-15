package esaph.spotlight.navigation.navigationLeftSite.VerticalViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import esaph.spotlight.Esaph.EsaphDotTabs.DotsIndicator;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class FragmentNavigationLeftSite extends EsaphGlobalCommunicationFragment
{
    private DotsIndicator dotsIndicator;
    private ViewPager viewPager;
    private ViewPagerAdapterLeftSite viewPagerAdapterLeftSite;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager = null;
        viewPagerAdapterLeftSite = null;
        dotsIndicator = null;
    }

    public FragmentNavigationLeftSite() {
        // Required empty public constructor
    }


    public static FragmentNavigationLeftSite getInstance()
    {
        return new FragmentNavigationLeftSite();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fragment_navigation_left_site, container, false);
        viewPager = rootView.findViewById(R.id.viewPager);
        dotsIndicator = rootView.findViewById(R.id.dots_indicatorMain);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPagerAdapterLeftSite = new ViewPagerAdapterLeftSite(getChildFragmentManager());
        dotsIndicator.setViewPager(viewPager);
        viewPager.setAdapter(viewPagerAdapterLeftSite);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
