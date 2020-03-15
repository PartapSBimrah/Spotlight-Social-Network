/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.StorageManagment.StorageHandlerSticker;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFeed;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;


public class LoginAsync extends AsyncTask<String, Integer, String>
{
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<LoginListener> loginListenerWeakReference;
    private WeakReference<EditText> editTextWeakReferencePassword;
    private WeakReference<EditText> editTextWeakReferenceUsername;

    private boolean serverOnline = false;
    private String username;
    private long UID;
    private String password;
    private ProgressDialog progressDialog;
    private boolean maxConn = false;
    private String SessionID;

    public LoginAsync(Activity activity,
                       EditText editTextUsername,
                       EditText editTextPassword,
                       LoginListener loginListener)
    {
        this.activityWeakReference = new WeakReference<>(activity);
        this.editTextWeakReferenceUsername = new WeakReference<>(editTextUsername);
        this.editTextWeakReferencePassword = new WeakReference<>(editTextPassword);
        this.loginListenerWeakReference = new WeakReference<>(loginListener);
    }

    public interface LoginListener
    {
        void onLoginSuccess();
        void onLoginFailed(String Error_Code);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        Context context = activityWeakReference.get();
        if(context != null)
        {
            EditText editTextUsername = editTextWeakReferenceUsername.get();
            EditText editTextPassword = editTextWeakReferencePassword.get();

            if(editTextPassword != null && editTextUsername != null)
            {
                this.username = editTextUsername.getText().toString();
                this.password = editTextPassword.getText().toString();
                editTextPassword.setAlpha(0.5f);
                editTextUsername.setAlpha(0.5f);
                editTextPassword.setClickable(false);
                editTextUsername.setClickable(false);
            }

            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
            this.progressDialog.setMessage(context.getResources().getString(R.string.txt__alertUnoMomento));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }
    }


    private void clearUserData()
    {
        try
        {
            Context context = activityWeakReference.get();
            if(context != null)
            {
                CLPreferences preferences = new CLPreferences(context);
                preferences.logOut();

                StorageHandler.dropAllFiles(context);
                StorageHandlerProfilbild.dropAllFiles(context);

                SQLFriends friends = new SQLFriends(context);
                friends.dropTableWatcher();
                friends.close();

                SQLChats chats = new SQLChats(context);
                chats.dropTableChats();
                chats.close();

                SQLSticker sqlSticker = new SQLSticker(context);
                sqlSticker.dropTableStickers();
                sqlSticker.close();

                SQLHashtags sqlHashtags = new SQLHashtags(context);
                sqlHashtags.dropTableHashtags();
                sqlHashtags.close();

                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(context);
                sqlLifeCloud.dropTableLifeCloud();
                sqlLifeCloud.close();

                SQLGroups sqlMemorys = new SQLGroups(context);
                sqlMemorys.dropAllDataMoments();
                sqlMemorys.close();

                SQLUploads sqlUploads = new SQLUploads(context);
                sqlUploads.dropTables();
                sqlUploads.close();

                SQLFeed sqlFeed = new SQLFeed(context);
                sqlFeed.dropAllData();
                sqlFeed.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "clearUserData() failed: " + ec);
        }
    }

    @Override
    protected String doInBackground(String[] params)
    {
        try
        {
            SocketResources resources = new SocketResources();
            CLPreferences preferences = new CLPreferences(activityWeakReference.get());
            String token = null;
            if(!preferences.getUsername().isEmpty() && !preferences.getUsername().equals(this.username))
            {
                clearUserData();

                token = FirebaseInstanceId.getInstance().getToken();
            }
            else
            {
                if(!preferences.getFCMToken().isEmpty() && !preferences.getFCMToken().equals("NT"))
                {
                    token = preferences.getFCMToken();
                }
                else
                {
                    token = FirebaseInstanceId.getInstance().getToken();
                }
            }

            if(token == null)
                return "FatalERR";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LRC", "LRL");
            jsonObject.put("USRN", this.username);
            jsonObject.put("PW", this.password);
            jsonObject.put("FCMT", token);

            SSLSocket loginSocket = EsaphSSLSocket.getSSLInstance(activityWeakReference.get(), resources.getServerAddress(), resources.getServerPortLRServer());
            Log.i(getClass().getName(), "Login into server by: " + resources.getServerAddress() + ", " + resources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(loginSocket.getOutputStream()));

            writer.println(jsonObject.toString());
            writer.flush();
            this.serverOnline = true;

            String reply = reader.readLine();

            if (reply.equals("LRLT"))
            {
                Log.i(getClass().getName(), "Login Successfull.");
                JSONObject jsonObjectSessionPayload = new JSONObject(reader.readLine());
                this.SessionID = jsonObjectSessionPayload.getString("SID");
                this.UID = jsonObjectSessionPayload.getLong("UID");
                preferences.setFCMToken(token);
            }
            else
            {
                preferences.setFCMToken("NT");
            }
            reader.close();
            writer.close();
            loginSocket.close();
            return reply;
        }
        catch (Exception ec)
        {
            Log.e(getClass().getName(), "Exception: " + ec);
            return "FatalERR";
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        EditText editTextUsername = editTextWeakReferenceUsername.get();
        EditText editTextPassword = editTextWeakReferencePassword.get();
        if(editTextPassword != null && editTextUsername != null)
        {
            editTextPassword.setAlpha(1f);
            editTextUsername.setAlpha(1f);
            editTextPassword.setClickable(true);
            editTextUsername.setClickable(true);
        }


        Context context = activityWeakReference.get();
        LoginListener loginListener = loginListenerWeakReference.get();
        if(context != null && loginListener != null)
        {
            if (maxConn)
            {
                this.progressDialog.cancel();
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                dialog.setMessage(context.getResources().getString(R.string.txt__alertMaxConnections));
                dialog.show();
            }
            else
            {
                if (result != null)
                {
                    if (!result.equals("LRVE"))
                    {
                        if (result.equals("LRLT") && this.SessionID != null)
                        {
                            if(!this.SessionID.isEmpty())
                            {
                                this.progressDialog.setMessage(context.getResources().getString(R.string.txt_alertAnmeldungSuccesfull));
                                this.progressDialog.cancel();

                                SpotLightLoginSessionHandler.setSpotLightUserLogged(this.SessionID, username, UID, false);
                                CLPreferences preferences = new CLPreferences(context);
                                preferences.setUpUser(this.UID, this.username, this.password, false);
                                loginListener.onLoginSuccess();
                            }
                        }
                        else
                        {
                            if (result.equals("LRLF"))
                            {
                                this.progressDialog.cancel();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                                dialog.setMessage(context.getResources().getString(R.string.txt_alertWrongPassword));
                                dialog.show();
                            }
                            else
                            {
                                this.progressDialog.cancel();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                                dialog.setMessage(context.getResources().getString(R.string.txt__alertNeverHappens));
                                dialog.show();
                            }
                        }
                    }
                    else
                    {
                        if (result.equals("LRVE"))
                        {
                            this.progressDialog.cancel();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                            dialog.setMessage(context.getResources().getString(R.string.txt_alertEmailVerification));
                            dialog.show();
                        }
                        else
                        {
                            this.progressDialog.cancel();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                            dialog.setMessage(context.getResources().getString(R.string.txt__alertNeverHappens));
                            dialog.show();
                        }
                    }
                }
                else
                {
                    this.progressDialog.cancel();
                    if (!serverOnline)
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setMessage(context.getResources().getString(R.string.txt_alertServerOffline));
                        dialog.setTitle(context.getResources().getString(R.string.app_name));
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle(context.getResources().getString(R.string.txt__alertLogIn));
                        dialog.setMessage(context.getResources().getString(R.string.txt__alertNeverHappens));
                        dialog.show();
                    }
                }
            }
        }
    }
}