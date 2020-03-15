package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.ChatTemplatesView;

import android.content.Context;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;

public class ChatTemplatesViewPagerAdapter extends FragmentStatePagerAdapter
{
    private List<ChatTemplatePageItemBase> listFragments;
    private String[] arrayNames;

    public ChatTemplatesViewPagerAdapter(Context context, FragmentManager fm, List<ChatTemplatePageItemBase> listFragments)
    {
        super(fm);
        this.listFragments = listFragments;
        this.arrayNames = context.getResources().getStringArray(R.array.TEXT_TEMPLATES_NAMES);
    }

    public void clear()
    {
        listFragments.clear();
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return arrayNames[position];
    }

    @Override
    public ChatTemplatePageItemBase getItem(int position)
    {
        return listFragments.get(position);
    }

    @Override
    public int getCount()
    {
        return listFragments.size();
    }
}
