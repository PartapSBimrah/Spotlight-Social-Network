package esaph.spotlight.navigation.spotlight.Moments.AktuellePrivateMomentView;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class DatumHolderFragment extends Fragment
{
    public DatumHolderFragment()
    {
        // Required empty public constructor
    }

    private static DatumList datumHolder;
    public static DatumHolderFragment getInstance(DatumList datumHolder)
    {
        DatumHolderFragment.datumHolder = datumHolder;
        return new DatumHolderFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_datum_holder, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewDisplayDate);
        textView.setText(datumHolder.getNormalUniqueDatumFormat());
        return rootView;
    }

}
