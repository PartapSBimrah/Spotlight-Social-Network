package esaph.spotlight;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.spots.SpotMaker.FragmentFinishListener;

public abstract class EsaphGlobalCommunicationFragment extends Fragment
{
    abstract public boolean onActivityDispatchedBackPressed();

    private EsaphActivity esaphActivity;
    private FragmentFinishListener fragmentFinishListener;

    public EsaphActivity getEsaphActivity() {
        return esaphActivity;
    }

    public EsaphGlobalCommunicationFragment setFragmentFinishListener(FragmentFinishListener fragmentFinishListener) {
        this.fragmentFinishListener = fragmentFinishListener;
        return this;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(fragmentFinishListener != null)
        {
            fragmentFinishListener.onFragmentFinished();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof EsaphActivity)
        {
            esaphActivity = (EsaphActivity) context;
        }
    }

    public void refreshData()
    {

    }

    public void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if(inputMethodManager != null && activity.getCurrentFocus() != null) //No focus, if visibility is set to View.GONE!
        {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //setupUI(getView());
    }
}
