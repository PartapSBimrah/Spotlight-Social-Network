package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.ChatTemplatesView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.SpotLightTextualTemplates;

public class ChatTemplatePageItemBase extends EsaphGlobalCommunicationFragment
{
    private static final String extraInterfaceTemplateSelectListener = "esaph.spotlight.interface.templateselect.listener";
    private static final String KEY_LIST_ARRAY_ID = "esaph.spotlight.list.templates.id";
    private final static int SPAN_COUNT = 2;
    private RecyclerView recylerView;
    private ChatTemplatePageViewAdapter chatTemplatePageViewAdapter;
    private ChatTemplatesView.TemplateChatSelectedListener templateChatSelectedListener;
    private int arrayID;

    public ChatTemplatePageItemBase()
    {
        // Required empty public constructor
    }

    public static ChatTemplatePageItemBase getInstance(int ARRAY_ID,
                                                       ChatTemplatesView.TemplateChatSelectedListener templateChatSelectedListener)
    {
        ChatTemplatePageItemBase fragment = new ChatTemplatePageItemBase();
        Bundle bundle = new Bundle();
        bundle.putInt(ChatTemplatePageItemBase.KEY_LIST_ARRAY_ID, ARRAY_ID);
        bundle.putSerializable(ChatTemplatePageItemBase.extraInterfaceTemplateSelectListener, templateChatSelectedListener);
        fragment.setArguments(bundle);

        return fragment;
    }

    public ChatTemplatesView.TemplateChatSelectedListener getTemplateChatSelectedListener()
    {
        return templateChatSelectedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            templateChatSelectedListener = (ChatTemplatesView.TemplateChatSelectedListener)
                    bundle.getSerializable(ChatTemplatePageItemBase.extraInterfaceTemplateSelectListener);

            arrayID = (int) bundle.getInt(
                    ChatTemplatePageItemBase.KEY_LIST_ARRAY_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_template_preview, container, false);

        recylerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), ChatTemplatePageItemBase.SPAN_COUNT);
        recylerView.setLayoutManager(gridLayoutManagerVertical);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<ChatTextMessage> list = new ArrayList<>();
                try
                {
                    list.addAll(SpotLightTextualTemplates.getTemplateList(getContext(), arrayID));
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Load Templates BaseView failed: " + ec);
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
                                chatTemplatePageViewAdapter = new ChatTemplatePageViewAdapter(ChatTemplatePageItemBase.this,
                                        list);

                                recylerView.setAdapter(chatTemplatePageViewAdapter);
                            }
                        }
                    });
                }
            }
        }).start();

        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }


}
