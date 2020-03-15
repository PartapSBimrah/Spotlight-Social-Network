/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.Aktuelle.MomentSynchCheckListener;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncSynchroniseAktuelleChatsAndMoments extends AsyncTask<Void, Void, Boolean>
{
    private static final Object obLock = new Object();
    private MomentSynchCheckListener momentSynchCheckListener;
    private WeakReference<Context> context;
    private ProgressDialog progressDialog;

    public AsyncSynchroniseAktuelleChatsAndMoments(Context context, MomentSynchCheckListener momentSynchCheckListener)
    {
        this.context = new WeakReference<Context>(context);
        this.momentSynchCheckListener = momentSynchCheckListener;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.context.get());
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setTitle(context.get().getResources().getString(R.string.txt__alertLogIn));
        this.progressDialog.setMessage(context.get().getResources().getString(R.string.txt_alertLogInDatenbankAbgleich));
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.show();
    }

    private boolean SyncChats()
    {
        SQLFriends sqlFriends = null;
        SQLChats sqlChats = null;
        try
        {
            boolean synched = false;
            SocketResources socketResources = new SocketResources();

            sqlFriends = new SQLFriends(this.context.get());
            sqlChats = new SQLChats(this.context.get());

            JSONObject jsonObjectLastConv = new JSONObject();
            jsonObjectLastConv.put("ISC", "FGACM");
            jsonObjectLastConv.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObjectLastConv.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(this.context.get(), socketResources.getServerAddress(), socketResources.getServerPortFServer());
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
                    synched = true;
                }
            }
            else if(result.equals("0")) //MEANS EVERTHING OK, BUT NO CONVERSATIONS ARE THERE.
            {
                synched = true;
            }

            return synched;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SyncChats failed(): " + ec);
            return false;
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
        }
    }


    @Override
    protected Boolean doInBackground(Void... params)
    {
        synchronized (AsyncSynchroniseAktuelleChatsAndMoments.obLock)
        {
            try
            {
                SQLChats sqlChats = new SQLChats(context.get());
                sqlChats.dropTableChats();
                sqlChats.close();

                boolean synchedChats = this.SyncChats();

                if(synchedChats)
                {
                    Log.i(getClass().getName(), "AsyncSynchroniseAktuelleChatsAndMoments(): CHATS OK");
                }

                if(synchedChats)
                {
                    return true;
                }

                return false;
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "AsyncSynchroniseAktuelleChatsAndMoments() failed: " + ec);
                return false;
            }
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        this.progressDialog.cancel();
        CLPreferences preferences = new CLPreferences(this.context.get());
        if(aBoolean != null)
        {
            if(aBoolean)
            {
                preferences.setAccountIsSynchronized();
                momentSynchCheckListener.onMomentsUpdated();
            }
            else
            {
                preferences.setNeedSynchronisation();
                momentSynchCheckListener.onMomentsSynchFailed();
            }
        }
        else
        {
            preferences.setNeedSynchronisation();
            momentSynchCheckListener.onMomentsSynchFailed();
        }
    }
}
