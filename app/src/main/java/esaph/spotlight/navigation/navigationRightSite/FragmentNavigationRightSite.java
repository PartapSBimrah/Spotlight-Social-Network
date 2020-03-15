package esaph.spotlight.navigation.navigationRightSite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.PreLogin.Dialogs.BottomSheetRegister;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class FragmentNavigationRightSite extends EsaphGlobalCommunicationFragment
{
    private BottomNavigationViewEx bottomNavigationViewEx;
    private boolean isInitalized = false;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        bottomNavigationViewEx = null;
    }

    public static FragmentNavigationRightSite getInstance()
    {
        return new FragmentNavigationRightSite();
    }

    public FragmentNavigationRightSite()
    {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fragment_navigation_right_site, container, false);
        bottomNavigationViewEx = rootView.findViewById(R.id.bottomNavigation);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !isInitalized)
        {
            if(bottomNavigationViewEx != null)
            {
                bottomNavigationViewEx.setCurrentItem(0);
                isInitalized = true;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationViewEx.setItemIconTintList(null);
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
        bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if(currentFragmentItemID == item.getItemId()) return false;

                switch (item.getItemId())
                {
                    case R.id.menu_chat:
                        applyFragment(ChatsFragment.getInstance());
                        break;

                    case R.id.menu_moments:
                        if(SpotLightLoginSessionHandler.isDemoMode())
                        {
                            BottomSheetRegister.getInstance().show(getChildFragmentManager(), BottomSheetRegister.class.getName());
                            return false;
                        }
                        else
                        {
                            applyFragment(MomentsFragment.getInstance());
                        }
                        break;
                }

                currentFragmentItemID = item.getItemId();
                return true;
            }
        });
    }


    private int currentFragmentItemID = -1;
    private void applyFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }
}
