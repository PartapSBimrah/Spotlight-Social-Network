/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class EsaphStickerPagerAdapter extends FragmentStatePagerAdapter
{
    private Context context;
    private List<EsaphStickerViewBASEFragmentDialog> fragments;
    private View viewNoData;

    public EsaphStickerPagerAdapter(Context context,
                                    View viewNoData,
                                    FragmentManager fm)
    {
        super(fm);
        this.viewNoData = viewNoData;
        this.context = context;
        this.fragments = new ArrayList<>();
    }

    public void clear()
    {
        fragments.clear();
        notifyDataSetChanged();
    }

    public void addData(List<EsaphStickerViewBASEFragmentDialog> esaphStickerViewBASEFragmentDialogs)
    {
        this.fragments.addAll(esaphStickerViewBASEFragmentDialogs);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        if(fragments == null || fragments.isEmpty())
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
        return fragments.get(position).getStickerPack().getPACK_NAME();
    }

    @Override
    public EsaphStickerViewBASEFragmentDialog getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
