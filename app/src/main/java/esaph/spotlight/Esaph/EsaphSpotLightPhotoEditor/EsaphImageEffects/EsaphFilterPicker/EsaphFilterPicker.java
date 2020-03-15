package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphImageEffects.EsaphFilterPicker.FilterViewPagerViews.EsaphFilterListNormal;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EsaphFilterPicker extends EsaphGlobalCommunicationFragment
{
    private FilterViewPagerAdapter filterViewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EsaphFilterPicker()
    {
    }

    public static EsaphFilterPicker getInstance()
    {
        return new EsaphFilterPicker();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        List<EsaphFilterPickerViewBASEFragment> list = new ArrayList<>();
        list.add(EsaphFilterListNormal.getInstance(null));

        filterViewPagerAdapter = new FilterViewPagerAdapter(getContext(),
                getChildFragmentManager(),
                list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_filter_picker, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(filterViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        getChildFragmentManager()
                .beginTransaction()
                .remove(EsaphFilterPicker.this)
                .commit();

        return true;
    }


}
