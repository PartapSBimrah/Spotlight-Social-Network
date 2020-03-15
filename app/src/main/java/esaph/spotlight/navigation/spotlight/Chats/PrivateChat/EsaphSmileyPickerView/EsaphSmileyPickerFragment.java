package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentActivity;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentFlags;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentFood;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentNature;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentObjects;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentPeople;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentSymbols;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.SmileyViewPagerViews.EsaphSmileyViewFragmentTravel;

public class EsaphSmileyPickerFragment extends BottomSheetDialogFragment
{
    private SmileyViewPagerAdapter smileyViewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EsaphSmileyPickerFragment()
    {

    }

    public static EsaphSmileyPickerFragment getInstance(EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphSmileyViewBASEFragment.EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER, onSmileySelectListenerCameraEditor);
        EsaphSmileyPickerFragment esaphSmileyPickerFragment = new EsaphSmileyPickerFragment();
        esaphSmileyPickerFragment.setArguments(bundle);
        return esaphSmileyPickerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor onSmileySelectListenerCameraEditor =
                    (EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor) bundle.getSerializable(EsaphSmileyViewBASEFragment.EXTRA_INTERFACE_ON_SMILEY_SELECT_LISTENER);

            List<EsaphSmileyViewBASEFragment> list = new ArrayList<>();
            list.add(EsaphSmileyViewFragmentActivity.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentFlags.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentFood.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentNature.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentObjects.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentPeople.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentSymbols.getInstance(onSmileySelectListenerCameraEditor));
            list.add(EsaphSmileyViewFragmentTravel.getInstance(onSmileySelectListenerCameraEditor));

            smileyViewPagerAdapter = new SmileyViewPagerAdapter(getContext(),
                    getChildFragmentManager(),
                    list);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_esaph_smiley_picker_fragment_camera, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(smileyViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
}
