package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileeyPickerViewChat;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphXMLSmileyParser;
import esaph.spotlight.R;

public class SmileyViewPagerAdapterChat extends FragmentStatePagerAdapter
{
    private Context context;
    private List<EsaphSmileyViewBASEFragmentChat> fragments;

    public SmileyViewPagerAdapterChat(Context context,
                                      FragmentManager fm,
                                      List<EsaphSmileyViewBASEFragmentChat> fragments)
    {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.activity);

            case 1:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.flags);

            case 2:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.food);

            case 3:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.nature);

            case 4:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.objects);

            case 5:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.people);

            case 6:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.symbols);

            case 7:
                return EsaphXMLSmileyParser.parseOnlyFirst(context, R.xml.travel);
        }

        return "";
    }

    @Override
    public EsaphSmileyViewBASEFragmentChat getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
