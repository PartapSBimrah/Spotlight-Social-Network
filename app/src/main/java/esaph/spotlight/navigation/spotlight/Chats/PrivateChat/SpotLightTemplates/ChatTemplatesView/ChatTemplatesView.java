/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.ChatTemplatesView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.SpotLightTextualTemplates;

public class ChatTemplatesView extends Fragment
{
    private static final String extraInterfaceTemplateSelectListener = "esaph.spotlight.interface.templateselect.listener";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChatTemplatesViewPagerAdapter chatTemplatesAdapter;
    private TemplateChatSelectedListener templateChatSelectedListener;

    public ChatTemplatesView()
    {
        // Required empty public constructor
    }

    public interface TemplateChatSelectedListener extends Serializable
    {
        void onTemplateSelected(ChatTextMessage chatTextMessage);
        void onTemplateLongClick(ChatTextMessage chatTextMessage);
    }


    public static ChatTemplatesView getInstance(TemplateChatSelectedListener templateChatSelectedListener)
    {
        ChatTemplatesView chatTemplatesView = new ChatTemplatesView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatTemplatesView.extraInterfaceTemplateSelectListener, templateChatSelectedListener);
        chatTemplatesView.setArguments(bundle);
        return chatTemplatesView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            templateChatSelectedListener = (TemplateChatSelectedListener)
                    bundle.getSerializable(ChatTemplatesView.extraInterfaceTemplateSelectListener);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chat_templates_view, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayoutTemplates);
        viewPager = rootView.findViewById(R.id.viewPagerChatTemplates);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        tabLayout.setupWithViewPager(viewPager);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<ChatTemplatePageItemBase> list = new ArrayList<>();
                try
                {
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_EMOTIONS,
                            templateChatSelectedListener));
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_ANIMALS,
                            templateChatSelectedListener));
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_EVENTS,
                            templateChatSelectedListener));
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_FRIENDS,
                            templateChatSelectedListener));
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_FRUITS,
                            templateChatSelectedListener));
                    list.add(ChatTemplatePageItemBase.getInstance(R.array.TEXT_TEMPLATES_SG,
                            templateChatSelectedListener));
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Failed to load ChatTemplates: " + ec);
                }
                finally
                {
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(isAdded())
                            {
                                chatTemplatesAdapter = new ChatTemplatesViewPagerAdapter(getContext(),
                                        getChildFragmentManager(),
                                        list);

                                viewPager.setAdapter(chatTemplatesAdapter);
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
