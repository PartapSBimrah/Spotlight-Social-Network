/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicSearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLSocket;

import androidx.annotation.Nullable;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.SpotLightFancyImageEditorActivity;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.AdapterSearchPerson.ArrayAdapterSearchingPerson;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.EsaphSearchTabAdapter;
import esaph.spotlight.navigation.spotlight.PublicSearch.Background.AsyncSearchInMode;
import esaph.spotlight.services.NotificationAndMessageHandling.MessageHandler;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class PublicSearchFragment extends EsaphActivity
{
    private ExecutorService executorService;
    private ListView listView;
    private EsaphSearchTabAdapter esaphSearchTabAdapter;
    private TabLayout tabLayout;
    private TextView textViewSearchMessage;
    private ImageView imageViewSeachImage;
    private ImageView imageViewResetSearching;
    private ImageView imageViewCloseSearchingFragment;
    private EditText editTextSearching;
    private Handler handler = new Handler();
    private TabLayout.OnTabSelectedListener tabSelectedListener;

    /*
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        tabLayout = null;
        tabSelectedListener = null;
        imageViewResetSearching = null;
        esaphSearchTabAdapter = null;
        listView = null;
        if(executorService != null)
        {
            executorService.shutdown();
            executorService = null;
        }
        editTextSearching = null;
        imageViewSeachImage = null;
        textViewSearchMessage = null;

        try
        {
            if(searchSocket != null)
            {
                searchSocket.close();
            }

            if(writer != null)
            {
                writer.close();
            }

            if(reader != null)
            {
                reader.close();
            }
        }
        catch (Exception ec)
        {
        }
    }*/

    @Override
    protected void onStop()
    {
        super.onStop();
        try
        {
            if(searchSocket != null)
            {
                searchSocket.close();
            }

            if(writer != null)
            {
                writer.close();
            }

            if(reader != null)
            {
                reader.close();
            }
        }
        catch (Exception ec)
        {
        }
    }

    public PublicSearchFragment()
    {
        // Required empty public constructor
    }

    public static PublicSearchFragment getInstance()
    {
        return new PublicSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_public_search);
        executorService = Executors.newFixedThreadPool(3);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutSwitchSearch);
        listView = findViewById(R.id.listViewMainData);

        imageViewCloseSearchingFragment = (ImageView) findViewById(R.id.imageViewCloseSearchingFragment);
        textViewSearchMessage = (TextView) findViewById(R.id.SearchSheetErrorInfoTextGroupUserPerson);
        imageViewSeachImage = (ImageView) findViewById(R.id.SearchSheetErrorImageGroupUserPerson);
        listView = (ListView) findViewById(R.id.listViewMainData);
        editTextSearching = (EditText) findViewById(R.id.textViewTitleLayoutTop);
        editTextSearching.addTextChangedListener(textWatcher);
        editTextSearching.setHint(getResources().getString(R.string.txt_friends_Searching_person));
        imageViewResetSearching = (ImageView) findViewById(R.id.imageViewResetSearching);


        imageViewCloseSearchingFragment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.txt_person)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.txt_hashtag)));

        imageViewResetSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!editTextSearching.getText().toString().isEmpty())
                {
                    editTextSearching.setText("");
                }
                else
                {
                    finish();
                }
            }
        });

        tabSelectedListener = new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab != null) //Must be the Kamera position.
                {
                    switch (tab.getPosition())
                    {
                        case 0:
                            changeSearchAdapter(new ArrayAdapterSearchingPerson(getApplicationContext(), executorService), SearchSwitch.PERSON);
                            break;

                        case 1:
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_still_working), Toast.LENGTH_LONG).show();
                            // changeSearchAdapter(new ArrayAdapterSearchingHashtag(getContext(), executorService), SearchSwitch.HASHTAG);
                            break;

                        case 2:
                            break;

                        case 3:
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        };

        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        TabLayout.Tab tab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
        if(tab != null)
        {
            tabSelectedListener.onTabSelected(tab);
        }

        loadFollowAnfragen();
    }


    private void loadFollowAnfragen()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<Object> list = new ArrayList<>();
                SQLFriends sqlFriends = null;
                try
                {
                    sqlFriends = new SQLFriends(getApplicationContext());
                    list.addAll(sqlFriends.loadMoreFollowAnfragenToMe());
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "PublicSearchFragment failed to load requests: " + ec);
                }
                finally
                {
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(!isFinishing() && esaphSearchTabAdapter instanceof ArrayAdapterSearchingPerson)
                            {
                                esaphSearchTabAdapter.addAll(list);
                                esaphSearchTabAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                    if(sqlFriends != null)
                    {
                        sqlFriends.close();
                    }
                }
            }
        }).start();
    }

    private void changeSearchAdapter(EsaphSearchTabAdapter esaphSearchTabAdapter, SearchSwitch searchSwitch)
    {
        this.esaphSearchTabAdapter = esaphSearchTabAdapter;
        this.listView.setAdapter(this.esaphSearchTabAdapter);
        this.searchSwitch = searchSwitch;
        new Thread(new SetupSearchConnection(searchSwitch)).start();
    }

    private class StartSearching implements Runnable
    {
        private String s;
        private StartSearching(String s)
        {
            this.s = s;
        }

        @Override
        public void run()
        {
            if(isFinishing())
                return;

            if(!s.isEmpty())
            {
                removeSearchMessage();
                new AsyncSearchInMode(s,
                        getApplicationContext(),
                        esaphSearchTabAdapter,
                        searchSocket,
                        writer,
                        reader).execute();
            }
            else
            {
                setSearchMessage();
            }
        }
    }

    private StartSearching startSearching = new StartSearching("");

    private final TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count)
        {
            esaphSearchTabAdapter.clearAll();
            esaphSearchTabAdapter.notifyDataSetChanged();

            handler.removeCallbacks(startSearching);
            StartSearching startSearchingReference = new StartSearching(s.toString());
            handler.postDelayed(startSearchingReference, 500);
            startSearching = startSearchingReference;

            if(s.toString().isEmpty())
            {
                loadFollowAnfragen();
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };


    private void setSearchMessage()
    {
        if(isFinishing())
            return;

        textViewSearchMessage.setVisibility(View.VISIBLE);
        imageViewSeachImage.setVisibility(View.VISIBLE);
    }


    private void removeSearchMessage()
    {
        if(isFinishing())
            return;

        textViewSearchMessage.setVisibility(View.INVISIBLE);
        imageViewSeachImage.setVisibility(View.INVISIBLE);
    }


    private SSLSocket searchSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private AtomicBoolean settingUp = new AtomicBoolean(false);
    private SearchSwitch searchSwitch;

    private enum SearchSwitch
    {
        PERSON, HASHTAG
    }

    private class SetupSearchConnection implements Runnable
    {
        private SearchSwitch searchSwitch;
        private SetupSearchConnection(SearchSwitch searchSwitch)
        {
            this.searchSwitch = searchSwitch;
        }

        private String getCommandForSwitch()
        {
            switch (searchSwitch)
            {
                case PERSON:
                    return "PUGHP";

                case HASHTAG:
                    return "PUGHH";
            }

            return "";
        }

        @Override
        public void run()
        {
            if(isFinishing())
                return;


            if(!settingUp.compareAndSet(false, true))
                return;

            try
            {
                try
                {
                    searchSocket.close();
                    reader.close();
                    writer.close();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "SetupSearchConnection Exception(Ist normal): " + ec);
                }



                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PLSC", getCommandForSwitch());
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

                SocketResources resources = new SocketResources();
                searchSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), resources.getServerAddress(), resources.getServerPortPServer());
                searchSocket.setSoTimeout(10000);
                reader = new BufferedReader(new InputStreamReader(searchSocket.getInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(searchSocket.getOutputStream()));

                writer.println(jsonObject.toString());
                writer.flush();

                searchSocket.setSoTimeout(30000);
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "Cant connect to Search Server (User search): " + ec);
            }
            finally
            {
                settingUp.set(false);
            }
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(PublicSearchFragment.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageHandler.ACTION_FRIEND_UPDATE);
        registerReceiver(broadcastReceiverFriendAnfrage, intentFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(broadcastReceiverFriendAnfrage);
    }

    private final BroadcastReceiver broadcastReceiverFriendAnfrage = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && !isFinishing())
            {
                if(esaphSearchTabAdapter instanceof ArrayAdapterSearchingPerson)
                {
                    if(editTextSearching.getText().toString().isEmpty())
                    {
                        changeSearchAdapter(new ArrayAdapterSearchingPerson(getApplicationContext(), executorService), SearchSwitch.PERSON);
                        loadFollowAnfragen();
                    }
                    else
                    {
                        ArrayAdapterSearchingPerson arrayAdapterSearchingPerson = (ArrayAdapterSearchingPerson)
                                esaphSearchTabAdapter;
                        long UID_PARTNER = intent.getLongExtra(MessageHandler.ID_UID, -1);
                        short STATUS = intent.getShortExtra(MessageHandler.ID_FOLLOW_STATE, (short)-1);
                        arrayAdapterSearchingPerson.updateItem(UID_PARTNER, STATUS);
                    }
                }
            }
        }
    };

    @Override
    public boolean onActivityDispatchBackPressEvent() {
        return false;
    }
}
