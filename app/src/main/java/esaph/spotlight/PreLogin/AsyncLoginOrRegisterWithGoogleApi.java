package esaph.spotlight.PreLogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLFeed;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

@Deprecated
public class AsyncLoginOrRegisterWithGoogleApi extends AsyncTask<Void, Void, Boolean>
{
    private boolean isNewUserLogin;
    private boolean showDialog;
    private ProgressDialog progressDialog;
    private GoogleSignInAccount googleSignInAccount;
    private WeakReference<OnCheckAccountStateListener> onCheckAccountStateListenerWeakReference;
    private WeakReference<Context> contextWeakReference;

    public AsyncLoginOrRegisterWithGoogleApi(Context context, GoogleSignInAccount googleSignInAccount, OnCheckAccountStateListener onCheckAccountStateListener,
                                             boolean showDialog, boolean isNewUserLogin)
    {
        this.googleSignInAccount = googleSignInAccount;
        this.onCheckAccountStateListenerWeakReference = new WeakReference<OnCheckAccountStateListener>(onCheckAccountStateListener);
        this.contextWeakReference = new WeakReference<Context>(context);
        this.showDialog = showDialog;
        this.isNewUserLogin = isNewUserLogin;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        try
        {
            if(this.showDialog)
            {
                this.progressDialog = new ProgressDialog(this.contextWeakReference.get());
                this.progressDialog.setCanceledOnTouchOutside(false);
                this.progressDialog.setTitle(this.contextWeakReference.get().getResources().getString(R.string.txt__alertLogIn));
                this.progressDialog.setMessage(this.contextWeakReference.get().getResources().getString(R.string.txt__alertUnoMomento));
                this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                this.progressDialog.show();
            }
        }
        catch (Exception ec)
        {
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        try
        {
            CLPreferences preferences = new CLPreferences(this.contextWeakReference.get());
            String token = null;

            if(!preferences.getUsername().isEmpty() && isNewUserLogin)
            {
                SQLUploads sqlUploads = new SQLUploads(this.contextWeakReference.get());
                sqlUploads.dropTables();
                sqlUploads.close();

                SQLFeed sqlFeed = new SQLFeed(this.contextWeakReference.get());
                sqlFeed.dropAllData();
                sqlFeed.close();

                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(this.contextWeakReference.get());
                sqlLifeCloud.dropTableLifeCloud();
                sqlLifeCloud.close();

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

            JSONObject jsonObjectRegister = new JSONObject();
            jsonObjectRegister.put("LRC", "LRHGA");
            jsonObjectRegister.put("IDTS", this.googleSignInAccount.getIdToken());
            jsonObjectRegister.put("EMAIL", this.googleSignInAccount.getEmail());
            jsonObjectRegister.put("FCMT", token);

            SocketResources resources = new SocketResources();
            SSLSocket registerSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(registerSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(registerSocket.getOutputStream()));
            writer.println(jsonObjectRegister.toString());
            writer.flush();

            String result = reader.readLine();

            if (result.equals("LRLT"))
            {
                Log.i(getClass().getName(), "Login Successfull.");
                preferences.setFCMToken(token);

                JSONObject jsonObject = new JSONObject(reader.readLine());

                if(jsonObject.length() > 0)
                {
                    SpotLightLoginSessionHandler.setSpotLightUserLogged(jsonObject.getString("SID"),
                            jsonObject.getString("USRN"),
                            jsonObject.getLong("UID"),
                            false);

                    //Missing setup not working

                    return Boolean.TRUE;
                }
            }
            else
            {
                preferences.setFCMToken("NT");
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoginOrRegisterWithGoogleApi() failed: " + ec);
        }
        return Boolean.FALSE;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(this.showDialog)
        {
            if(this.progressDialog != null && this.progressDialog.isShowing())
            {
                this.progressDialog.cancel();
            }
        }


        OnCheckAccountStateListener onCheckAccountStateListener = this.onCheckAccountStateListenerWeakReference.get();
        if(aBoolean != null && onCheckAccountStateListener != null && aBoolean)
        {
            onCheckAccountStateListener.onAccountSuccess(this.googleSignInAccount);
        }
        else
        {
            if(onCheckAccountStateListener != null)
            {
                onCheckAccountStateListener.onAccountFailed(this.googleSignInAccount);
            }
        }
    }
}
