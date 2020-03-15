package esaph.spotlight.navigation.spotlight.PublicPostsNavigation;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class NavigationPublic extends EsaphGlobalCommunicationFragment
{
    public NavigationPublic() {
        // Required empty public constructor
    }

    public static NavigationPublic getInstance()
    {
        return new NavigationPublic();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_navigation_public, container, false);



        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
