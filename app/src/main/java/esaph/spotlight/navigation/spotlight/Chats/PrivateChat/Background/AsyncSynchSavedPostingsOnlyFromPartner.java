package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

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
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

@Deprecated
public class AsyncSynchSavedPostingsOnlyFromPartner extends AsyncTask<Void, Void, Boolean>
{
    private static final Object obLock = new Object();
    private ProgressDialog progressDialog;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<OnSynchSavedPostingsPartnerListener> onSynchSavedPostingsPartnerListenerWeakReference;
    private long ChatPartner;
    private long LastPostTime;

    public AsyncSynchSavedPostingsOnlyFromPartner(Context context, long ChatPartner, OnSynchSavedPostingsPartnerListener onSynchSavedPostingsPartnerListener)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.ChatPartner = ChatPartner;
        this.onSynchSavedPostingsPartnerListenerWeakReference = new WeakReference<OnSynchSavedPostingsPartnerListener>(onSynchSavedPostingsPartnerListener);
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.contextWeakReference.get());
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setTitle(this.contextWeakReference.get().getResources().getString(R.string.txt__alertUnoMomento));
        this.progressDialog.setMessage(this.contextWeakReference.get().getResources().getString(R.string.txt_alertLogInDatenbankAbgleich));
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        //This File should be deleted.
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBool)
    {
        super.onPostExecute(aBool);
        this.progressDialog.cancel();
        OnSynchSavedPostingsPartnerListener onSynchSavedPostingsPartnerListener = this.onSynchSavedPostingsPartnerListenerWeakReference.get();
        if(onSynchSavedPostingsPartnerListener != null)
        {
            if(aBool != null && aBool)
            {
                onSynchSavedPostingsPartnerListener.onSynchedSuccess();
            }
            else
            {
                onSynchSavedPostingsPartnerListener.onSynchFailed();
            }
        }
    }

}
