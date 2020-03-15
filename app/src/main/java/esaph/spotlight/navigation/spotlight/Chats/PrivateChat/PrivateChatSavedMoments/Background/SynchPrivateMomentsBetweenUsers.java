/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background;

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

public class SynchPrivateMomentsBetweenUsers implements Runnable
{
    private static final Object obLock = new Object();
    private long chatPartner;
    private SoftReference<SynchListenerUsersPosts> synchListenerUsersPostsSoftReference;
    private WeakReference<EsaphMomentsRecylerView> esaphMomentsRecylerViewWeakReference;
    private WeakReference<Context> contextWeakReference;

    public SynchPrivateMomentsBetweenUsers(Context context,
                                           long chatPartner,
                                           EsaphMomentsRecylerView esaphMomentsRecylerView,
                                           SynchListenerUsersPosts synchListenerUsersPosts)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.synchListenerUsersPostsSoftReference = new SoftReference<SynchListenerUsersPosts>(synchListenerUsersPosts);
        this.esaphMomentsRecylerViewWeakReference = new WeakReference<EsaphMomentsRecylerView>(esaphMomentsRecylerView);
        this.chatPartner = chatPartner;
    }

    public interface SynchListenerUsersPosts
    {
        public void onNewData();
        public void onFailedOrReachedEnd();
    }

    private boolean synched = false;
    @Override
    public void run()
    {
        synchronized (SynchPrivateMomentsBetweenUsers.obLock)
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

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PLSC", "PLSCPM");
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("FUSRN", this.chatPartner);
                int st = sqlChats.getPersonalMomentsSavedCount(this.chatPartner);
                jsonObject.put("BEC", st);
                System.out.println("Starting from schrottelung: " + st);


                SocketResources resources = new SocketResources();
                SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();


                JSONArray jsonArrayChatPosts = new JSONArray(reader.readLine());
                System.out.println("--------------------------");
                System.out.println("Starting from schrottelung Length: " + jsonArrayChatPosts.toString());
                System.out.println("--------------------------");

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
                    synched = true;
                }

                socket.close();
                writer.close();
                reader.close();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "SynchPrivateMomentsBetweenUsers() failed: " + ec);
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

                        SynchListenerUsersPosts synchListenerUsersPosts = synchListenerUsersPostsSoftReference.get();

                        if(synchListenerUsersPosts != null)
                        {
                            if(synched)
                            {
                                synchListenerUsersPosts.onNewData();
                            }
                            else
                            {
                                synchListenerUsersPosts.onFailedOrReachedEnd();
                            }
                        }
                    }
                });
            }
        }
    }


}
