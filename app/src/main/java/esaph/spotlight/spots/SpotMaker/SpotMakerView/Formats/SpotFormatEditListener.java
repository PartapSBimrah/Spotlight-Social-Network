package esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats;

import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;

public interface SpotFormatEditListener
{
    void onStartEditing(SpotMakerEdittext spotMakerEdittext);
    void onViewTouchedOutsideBounds();
}
