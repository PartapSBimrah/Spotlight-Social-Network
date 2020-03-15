package esaph.spotlight.navigation.spotlight.PublicPostsNavigation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.Esaph.EsaphDotTabs.DotsIndicator;
import esaph.spotlight.R;

public class PublicPostsNavigation extends Fragment
{
    private DotsIndicator dotsIndicator;
    private ViewPager viewPager;
    private ViewPagerAdapterPublicPostsNavigation viewPagerAdapterPublicPostsNavigation;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager = null;
        viewPagerAdapterPublicPostsNavigation = null;
        dotsIndicator = null;
    }


    public PublicPostsNavigation() {
        // Required empty public constructor
    }



    public static PublicPostsNavigation getInstance()
    {
        return new PublicPostsNavigation();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_public_posts_navigation, container, false);

        viewPager = rootView.findViewById(R.id.viewPager);
        dotsIndicator = rootView.findViewById(R.id.dots_indicatorMain);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPagerAdapterPublicPostsNavigation = new ViewPagerAdapterPublicPostsNavigation(getChildFragmentManager());
        dotsIndicator.setViewPager(viewPager);
        viewPager.setAdapter(viewPagerAdapterPublicPostsNavigation);
    }


}
