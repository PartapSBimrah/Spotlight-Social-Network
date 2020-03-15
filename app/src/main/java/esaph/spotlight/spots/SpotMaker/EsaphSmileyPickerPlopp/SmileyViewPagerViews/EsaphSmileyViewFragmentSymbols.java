package esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.SmileyViewPagerViews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphXMLSmileyParser;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.EsaphSmileyPickerAdapterPlopp;
import esaph.spotlight.spots.SpotMaker.EsaphSmileyPickerPlopp.EsaphSmileyViewBASEFragment;
import esaph.spotlight.R;

public class EsaphSmileyViewFragmentSymbols extends EsaphSmileyViewBASEFragment
{
    public EsaphSmileyViewFragmentSymbols()
    {

    }

    public static EsaphSmileyViewFragmentSymbols getInstance()
    {
        return new EsaphSmileyViewFragmentSymbols();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.esaphSmileyPickerAdapter = new EsaphSmileyPickerAdapterPlopp(EsaphSmileyViewFragmentSymbols.this,
                EsaphXMLSmileyParser.parse(getContext(), R.xml.symbols));

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
