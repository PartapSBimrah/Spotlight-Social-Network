package esaph.spotlight.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class EmptyFragmentHURENSOHN extends EsaphGlobalCommunicationFragment
{
    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_empty_fragment, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return false;
            }
        });
        return rootView;
    }
}
