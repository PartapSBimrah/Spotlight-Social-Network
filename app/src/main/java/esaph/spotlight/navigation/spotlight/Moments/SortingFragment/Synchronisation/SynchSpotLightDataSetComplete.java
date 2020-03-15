/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.Synchronisation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

//Cant be used, because when single friend syched and syched all there becomes to a collision.

@Deprecated
public class SynchSpotLightDataSetComplete implements Runnable
{
    private static final Object obLock = new Object();
    private WeakReference<Context> contextWeakReference;
    private WeakReference<EsaphMomentsRecylerView> esaphMomentsRecylerViewWeakReference;
    private SoftReference<SynchSpotLightListener> synchSpotLightListenerSoftReference;

    public SynchSpotLightDataSetComplete(Context context, SynchSpotLightListener synchSpotLightListener,
                                         EsaphMomentsRecylerView esaphMomentsRecylerView)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.esaphMomentsRecylerViewWeakReference = new WeakReference<>(esaphMomentsRecylerView );
        this.synchSpotLightListenerSoftReference = new SoftReference<>(synchSpotLightListener);
    }

    public interface SynchSpotLightListener
    {
        void onNewData();
        void onNoDataAvaiable();
    }

    private boolean Synched = false;

    @Override
    public void run()
    {
        synchronized (SynchSpotLightDataSetComplete.obLock)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewWeakReference.get();
                    if(esaphMomentsRecylerView != null)
                    {
                        esaphMomentsRecylerView.addFooter();
                    }
                }
            });

            SQLFriends sqlFriends = null;
            SQLChats sqlChats = null;

            try
            {
                sqlFriends = new SQLFriends(this.contextWeakReference.get());
                sqlChats = new SQLChats(this.contextWeakReference.get());

                SocketResources socketResources = new SocketResources();
                JSONObject jsonObjectLastConv = new JSONObject();
                jsonObjectLastConv.put("ISC", "FSAPFA");
                jsonObjectLastConv.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObjectLastConv.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObjectLastConv.put("C", -90);

                SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), socketResources.getServerAddress(), socketResources.getServerPortFServer());
                sidSocket.setSoTimeout(15000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sidSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sidSocket.getOutputStream()));
                writer.println(jsonObjectLastConv.toString());
                writer.flush();

                String result = reader.readLine();
                if(result.equals("1"))
                {
                    JSONArray jsonArrayChatPosts = new JSONArray(reader.readLine());
                    for(int counter = 0; counter < jsonArrayChatPosts.length(); counter++)
                    {
                        JSONObject jsonObjectPost = jsonArrayChatPosts.getJSONObject(counter);

                        ArrayList<EsaphHashtag> esaphHashtag = new ArrayList<>();
                        JSONArray jsonArray = jsonObjectPost.getJSONArray("ARR_EHT");
                        for (int counterHashtag = 0; counterHashtag < jsonArray.length(); counterHashtag++)
                        {
                            JSONObject jsonObjectHashtag = jsonArray.getJSONObject(counterHashtag);
                            esaphHashtag.add(new EsaphHashtag(jsonObjectHashtag.getString("TAG"),
                                    null,
                                    0));
                        }

                        JSONArray jsonArraySaved = jsonObjectPost.getJSONArray("ARS"); //Must be there, because its. when i saved a post it should be saved there.
                        List<SavedInfo> listSavedAllUsers = new ArrayList<>();
                        for(int counterSaved = 0; counterSaved < jsonArraySaved.length(); counterSaved++)
                        {
                            JSONObject jsonObjectSaver = jsonArraySaved.getJSONObject(counterSaved);
                            listSavedAllUsers.add(new SavedInfo(-1,
                                    jsonObjectSaver.getLong("UID_SAVED"),
                                    sqlFriends.lookUpUsername(jsonObjectSaver.getLong("UID_SAVED"))));
                        }

                        JSONArray jsonArrayReceivers = jsonObjectPost.getJSONArray("ARR_REC");

                        String absenderUsername;
                        if(jsonObjectPost.getLong("ABS") == SpotLightLoginSessionHandler.getLoggedUID())
                        {
                            absenderUsername = SpotLightLoginSessionHandler.getLoggedUsername();
                        }
                        else
                        {
                            absenderUsername = sqlFriends.lookUpUsername(jsonObjectPost.getLong("ABS"));
                        }

                        if((short)jsonObjectPost.getInt("TYPE") == CMTypes.FPIC)
                        {
                            ChatImage chatImage = new ChatImage(
                                    jsonObjectPost.getLong("PPID"),
                                    -1,
                                    jsonObjectPost.getLong("ABS"),
                                    -1,
                                    jsonObjectPost.getLong("TIME"),
                                    (short)-3,
                                    jsonObjectPost.getString("DES"),
                                    jsonObjectPost.getString("PID"),
                                    absenderUsername);

                            sqlChats.insertNewConversationMessage(chatImage,
                                    esaphHashtag,
                                    listSavedAllUsers,
                                    jsonArrayReceivers);
                        }
                        else if((short)jsonObjectPost.getInt("TYPE") == CMTypes.FVID)
                        {
                            ChatVideo chatVideo = new ChatVideo(
                                    jsonObjectPost.getLong("PPID"),
                                    -1,
                                    jsonObjectPost.getLong("ABS"),
                                    -1,
                                    jsonObjectPost.getLong("TIME"),
                                    (short) -3,
                                    jsonObjectPost.getString("DES"),
                                    jsonObjectPost.getString("PID"),
                                    absenderUsername);

                            sqlChats.insertNewConversationMessage(chatVideo,
                                    esaphHashtag,
                                    listSavedAllUsers,
                                    jsonArrayReceivers);
                        }
                    }

                    if(jsonArrayChatPosts.length() > 0)
                    {
                        Synched = true;
                    }
                }
                sidSocket.close();
                reader.close();
                writer.close();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "SynchSpotLightDataSetComplete run() failed: " + ec);
            }
            finally
            {
                if(sqlChats != null)
                {
                    sqlChats.close();
                }

                if(sqlFriends != null)
                {
                    sqlFriends.close();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewWeakReference.get();
                        if(esaphMomentsRecylerView != null)
                        {
                            esaphMomentsRecylerView.removeFooter();
                        }

                        SynchSpotLightListener synchSpotLightListener = synchSpotLightListenerSoftReference.get();
                        if(Synched)
                        {
                            if(synchSpotLightListener != null)
                            {
                                synchSpotLightListener.onNewData();
                            }
                        }
                        else
                        {
                            if(synchSpotLightListener != null)
                            {
                                synchSpotLightListener.onNoDataAvaiable();
                            }
                        }
                    }
                });
            }
        }
    }
}
