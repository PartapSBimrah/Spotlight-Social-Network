package esaph.spotlight.navigation.spotlight.Account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class AccountFragment extends EsaphGlobalCommunicationFragment
{
    public AccountFragment()
    {
        // Required empty public constructor
    }

    public static AccountFragment getInstance()
    {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }
}