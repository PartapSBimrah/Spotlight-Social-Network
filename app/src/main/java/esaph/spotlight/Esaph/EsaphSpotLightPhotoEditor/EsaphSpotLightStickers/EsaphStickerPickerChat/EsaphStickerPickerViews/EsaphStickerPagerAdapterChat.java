/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class EsaphStickerPagerAdapterChat extends FragmentStatePagerAdapter
{
    private View viewNoData;
    private Context context;
    private List<EsaphStickerViewBASEFragmentChat> listFragments;

    public EsaphStickerPagerAdapterChat(Context context,
                                        View viewNoData,
                                        FragmentManager fm)
    {
        super(fm);
        this.viewNoData = viewNoData;
        this.context = context;
        this.listFragments = new ArrayList<>();
    }

    public void addData(List<EsaphStickerViewBASEFragmentChat> esaphStickerViewBASEFragmentChats)
    {
        listFragments.addAll(esaphStickerViewBASEFragmentChats);
        notifyDataSetChanged();
    }

    public void clear()
    {
        listFragments.clear();
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        if(listFragments == null || listFragments.isEmpty())
        {
            viewNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            viewNoData.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return listFragments.get(position).getStickerPack().getPACK_NAME();
    }

    @Override
    public EsaphStickerViewBASEFragmentChat getItem(int position)
    {
        return listFragments.get(position);
    }

    @Override
    public int getCount()
    {
        return listFragments.size();
    }
}
