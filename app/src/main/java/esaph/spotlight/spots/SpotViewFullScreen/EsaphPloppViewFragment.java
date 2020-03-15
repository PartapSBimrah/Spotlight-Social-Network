package esaph.spotlight.spots.SpotViewFullScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphViewSwitcher.EsaphFragmentViewFlipper;
import esaph.spotlight.R;

public class EsaphPloppViewFragment extends EsaphGlobalCommunicationFragment
{
    private EsaphFragmentViewFlipper esaphViewFlipper;

    private static List<Fragment> list;


    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        esaphViewFlipper = null;
    }

    public EsaphPloppViewFragment()
    {
    }

    public static final String extraInterfaceShowingNewMessagesFinisheListener = "esaph.spotlight.interface.newmessages.finish";
    public interface ShowNewMessagesFinishListener extends Serializable
    {
        void onFinishedShowingNewMessages();
    }

    public static EsaphPloppViewFragment getInstance(List<Fragment> list, ShowNewMessagesFinishListener showNewMessagesFinishListener)
    {
        EsaphPloppViewFragment.list = list;
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphPloppViewFragment.extraInterfaceShowingNewMessagesFinisheListener, showNewMessagesFinishListener);
        EsaphPloppViewFragment esaphPloppViewFragment = new EsaphPloppViewFragment();
        esaphPloppViewFragment.setArguments(bundle);
        return esaphPloppViewFragment;
    }

    private ShowNewMessagesFinishListener showNewMessagesFinishListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(EsaphPloppViewFragment.list != null)
        {
            esaphViewFlipper.setData(list, getChildFragmentManager());
        }

        esaphViewFlipper.setViewSwitcherClickListener(new EsaphFragmentViewFlipper.ViewSwitcherClickListener()
        {
            @Override
            public void onNextClicked(int pos)
            {
            }

            @Override
            public void onPreviusClicked(int pos)
            {
            }

            @Override
            public void onEndReached()
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent(); //Closing fragment, because reached end.
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            showNewMessagesFinishListener = (ShowNewMessagesFinishListener) bundle.getSerializable(EsaphPloppViewFragment.extraInterfaceShowingNewMessagesFinisheListener);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_fragment_ploppview, container, false);
        esaphViewFlipper = (EsaphFragmentViewFlipper) rootView.findViewById(R.id.esaphFlipper);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(EsaphPloppViewFragment.this)
                    .commit();

            return true;
        }

        return false;
    }



    /*
    @Override
    public void onPause()
    {
        super.onPause();
        virtualCallPauseMediaPlayer();
    }

    private boolean invokedIsVisible = true;
    @Override
    public void onResume()
    {
        super.onResume();
        if(invokedIsVisible)
        {
            virtualCallResumeMediaPlayer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        this.invokedIsVisible = isVisibleToUser;
        if(isVisibleToUser)
        {
            virtualCallResumeMediaPlayer();
        }
        else
        {
            virtualCallPauseMediaPlayer();
        }
    }*/

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(showNewMessagesFinishListener != null)
        {
            showNewMessagesFinishListener.onFinishedShowingNewMessages();
        }
    }
}