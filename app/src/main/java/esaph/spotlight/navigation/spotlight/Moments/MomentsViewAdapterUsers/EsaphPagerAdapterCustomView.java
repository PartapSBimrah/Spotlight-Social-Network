/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MomentsViewAdapterUsers.FragmentUserSite.MomentsUserSite;

public class EsaphPagerAdapterCustomView extends FragmentPagerAdapter
{
    private Context context;
    private TabLayout esaphTabLayoutBottom;
    private EsaphGlobalCommunicationFragment currentFragment = null;
    private LayoutInflater layoutInflater;
    private List<UserSiteItem> userSiteItemList = new ArrayList<>();

    public EsaphPagerAdapterCustomView(FragmentManager fm, Context context, TabLayout esaphTabLayoutBottom) {
        super(fm);
        this.layoutInflater = LayoutInflater.from(context);
        this.esaphTabLayoutBottom = esaphTabLayoutBottom;
        this.context = context;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();

        for (int i = 0; i < esaphTabLayoutBottom.getTabCount(); i++)
        {
            TabLayout.Tab tab = esaphTabLayoutBottom.getTabAt(i);
            if(tab == null)
                continue;

            tab.setCustomView(getTabView(i));
        }
    }

    public EsaphGlobalCommunicationFragment getCurrentFragment()
    {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        if (getCurrentFragment() != object)
        {
            currentFragment = ((EsaphGlobalCommunicationFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public List<UserSiteItem> getUserSiteItemList() {
        return userSiteItemList;
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        UserSiteItem userSiteItem = userSiteItemList.get(position);
        View v = layoutInflater.inflate(R.layout.layout_user_site_moments_tab_item, null);

        TextView textView = v.findViewById(R.id.textViewTabUsername);
        ImageView imageView = v.findViewById(R.id.imageViewTab);

        textView.setText(userSiteItem.getUsername());


        ConversationMessage conversationMessage = userSiteItem.getConversationMessageLast();
        if(conversationMessage != null)
        {
            EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                    conversationMessage.getIMAGE_ID(),
                    imageView,
                    null,
                    new EsaphDimension(imageView.getWidth(),
                            imageView.getHeight()),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_rounded_corners));
        }


        return v;
    }

    @Override
    public EsaphGlobalCommunicationFragment getItem(int position)
    {
        return MomentsUserSite.getInstance(userSiteItemList.get(position));
    }

    @Override
    public int getCount() {
        return userSiteItemList.size();
    }
}
