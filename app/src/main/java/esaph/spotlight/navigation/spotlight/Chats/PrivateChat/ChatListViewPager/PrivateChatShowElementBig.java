package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import esaph.spotlight.R;
import me.kaelaela.verticalviewpager.VerticalViewPager;

public class PrivateChatShowElementBig extends Fragment
{
    private VerticalViewPager verticalViewPager;
    private PrivateChatViewPagerAdapter privateChatViewPagerAdapter;

    public PrivateChatShowElementBig() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_private_chat_show_element_big, container, false);
        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.verticalViewPagerChat);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        privateChatViewPagerAdapter = new PrivateChatViewPagerAdapter(getChildFragmentManager());
        verticalViewPager.setAdapter(privateChatViewPagerAdapter);
    }
}
