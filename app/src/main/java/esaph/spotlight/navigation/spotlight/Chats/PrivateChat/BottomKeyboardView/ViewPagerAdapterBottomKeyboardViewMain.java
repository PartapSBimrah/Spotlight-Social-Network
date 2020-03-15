package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.BottomKeyboardView;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.List;

import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;

public class ViewPagerAdapterBottomKeyboardViewMain extends FragmentStatePagerAdapter
{
    private Context context;
    private List<EsaphGlobalCommunicationFragment> list;

    public ViewPagerAdapterBottomKeyboardViewMain(Context context, FragmentManager fm, List<EsaphGlobalCommunicationFragment> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return context.getResources().getString(R.string.txt_sticker);

            case 1:
                return context.getResources().getString(R.string.txt_emojie);
        }


        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
