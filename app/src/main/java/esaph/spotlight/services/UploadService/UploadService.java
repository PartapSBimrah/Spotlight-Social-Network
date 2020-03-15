package esaph.spotlight.services.UploadService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class UploadService extends IntentService
{
    private static final String intentServiceName = "UploadServiceThing";

    private static List<Long> listUploading = new ArrayList<>();
    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private final IBinder binder = new MyLocalBinder();
    private static List<Object> uploadServiceCallBacks = new ArrayList<>();

    public class MyLocalBinder extends Binder
    {
        public UploadService getService(UploadServiceCallBacksNormal uploadServiceCallBacks)
        {
            UploadService.uploadServiceCallBacks.add(uploadServiceCallBacks);
            return UploadService.this;
        }
    }

    public void removeUploadServiceCallbacks(UploadServiceCallBacksNormal uploadServiceCallBacks)
    {
        UploadService.uploadServiceCallBacks.remove(uploadServiceCallBacks);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    public UploadService()
    {
        super(UploadService.intentServiceName);
    }

    public static final String extraP_ID = "esaph.spotlight.uploadservice.ID";
    public static final String ACTION_TYPE_LIFECLOUD_UPLOAD = "esaph.spotlight.uploadservice.CAMERA_UPLOAD";
    public static final String ACTION_TYPE_SPOTLIGHT_POST_UPLOAD = "esaph.spotlight.uploadservice.POST_UPLOAD";

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        try
        {
            if(intent != null && intent.getAction() != null)
            {
                if(intent.getAction().equals(UploadService.ACTION_TYPE_LIFECLOUD_UPLOAD))
                {
                    final long ID = intent.getLongExtra(UploadService.extraP_ID, -1); if(ID < 0) return;

                    if(!isUploading(ID))
                    {
                        synchronized (UploadService.lockObject)
                        {
                            UploadService.listUploading.add(ID);
                            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(getApplicationContext());
                            UploadService.executor.execute(new UploadLifeCloudPost(getApplication(), sqlLifeCloud.getLifeCloudUploadByID(ID)));
                            sqlLifeCloud.close();
                        }
                    }
                }
                else if(intent.getAction().equals(UploadService.ACTION_TYPE_SPOTLIGHT_POST_UPLOAD))
                {
                    final long ID = intent.getLongExtra(UploadService.extraP_ID, -1); if(ID < 0) return;

                    if(!isUploading(ID))
                    {
                        synchronized (UploadService.lockObject)
                        {
                            UploadService.listUploading.add(ID);
                            UploadService.executor.execute(new UploadData(ID));
                        }
                    }
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "onHandleIntent failed for upload Picture: " + ec);
        }
    }

    public void startNewUpload(UploadPost uploadPost)
    {
        if(!isUploading(uploadPost.getMESSAGE_ID()))
        {
            synchronized (UploadService.lockObject)
            {
                UploadService.listUploading.add(uploadPost.getMESSAGE_ID());
                UploadService.executor.execute(new UploadData(uploadPost.getMESSAGE_ID()));
            }
        }
    }

    private static final Object lockObject = new Object();

    public boolean isUploading(long ID)
    {
        synchronized (UploadService.lockObject)
        {
            return UploadService.listUploading.contains(ID);
        }
    }

    private class UploadData implements Runnable
    {
        private UploadPost uploadPost;
        private long ID;

        public UploadData(long ID)
        {
            this.ID = ID;
        }

        private String getUserCountry(Context context)
        {
            try
            {
                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                final String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2)
                {
                    return simCountry.toUpperCase(Locale.getDefault());
                }
                else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
                { // device is not 3G (would be unreliable)
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2)
                    { // network country code is available
                        return networkCountry.toUpperCase(Locale.getDefault());
                    }
                }

                return "N/A";
            }
            catch (Exception e)
            {
                Log.i(getClass().getName(), "getUserCountry() failed: " + e);
                return "N/A";
            }
        }

        private void updateDATABASEPostUploaded(UploadPost uploadPost, String NEW_PID, long NEW_SERVER_ID) throws JSONException, SpotLightLoginSessionHandler.UserNotLoggedException
        {
            SQLChats sqlChats = new SQLChats(getApplicationContext());

            JSONArray jsonArrayReceiversPrivate = uploadPost.getJsonArrayWAMP().getJSONArray(0);

            int jsonSize = jsonArrayReceiversPrivate.length();
            for(int counter = 0; counter < jsonSize; counter++)
            {
                JSONObject jsonObjectReceiverUser = jsonArrayReceiversPrivate.getJSONObject(counter);
                jsonObjectReceiverUser.put("ST", ConversationStatusHelper.STATUS_SENT);
                jsonArrayReceiversPrivate.put(counter, jsonObjectReceiverUser);
            }

            if(uploadPost.getType() == CMTypes.FPIC)
            {
                ChatImage chatImage = new ChatImage(
                        NEW_SERVER_ID,
                        -1,
                        SpotLightLoginSessionHandler.getLoggedUID(),
                        -1,
                        uploadPost.getShootTime(),
                        ConversationStatusHelper.STATUS_SENT,
                        uploadPost.getBeschreibung(),
                        NEW_PID,
                        SpotLightLoginSessionHandler.getLoggedUsername());

                sqlChats.insertNewConversationMessage(chatImage,
                        uploadPost.getEsaphHashtag(),
                        new ArrayList<SavedInfo>(),
                        jsonArrayReceiversPrivate);
            }
            else if(uploadPost.getType() == CMTypes.FVID)
            {
                ChatVideo chatVideo = new ChatVideo(
                        NEW_SERVER_ID,
                        -1,
                        SpotLightLoginSessionHandler.getLoggedUID(),
                        -1,
                        uploadPost.getShootTime(),
                        ConversationStatusHelper.STATUS_SENT,
                        uploadPost.getBeschreibung(),
                        NEW_PID,
                        SpotLightLoginSessionHandler.getLoggedUsername());

                sqlChats.insertNewConversationMessage(chatVideo,
                        uploadPost.getEsaphHashtag(),
                        new ArrayList<SavedInfo>(),
                        jsonArrayReceiversPrivate);
            }
            sqlChats.close();
        }


        private File exportDataToCacheImage(String PID) throws IOException
        {
            SQLUploads sqlUploads = new SQLUploads(getApplicationContext());
            File file = sqlUploads.getFileToUploadHQ(PID);
            sqlUploads.close();
            return file;
        }


        private File exportDataToCacheVideo(String PID) throws IOException
        {
            SQLUploads sqlUploads = new SQLUploads(getApplicationContext());
            File file = sqlUploads.getFileToUploadHQ(PID);
            sqlUploads.close();
            return file;
        }

        private JSONArray createJSONArrayFromHashtags(ArrayList<EsaphHashtag> esaphHashtags) throws JSONException
        {
            JSONArray jsonArray = new JSONArray();
            for(int counter = 0; counter < esaphHashtags.size(); counter++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TAG", esaphHashtags.get(counter).getHashtagName());
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }

        @Override
        public void run()
        {
            try
            {
                SQLUploads sqlUploadsPre = new SQLUploads(getApplicationContext());
                this.uploadPost = sqlUploadsPre.getPostByID(ID);
                sqlUploadsPre.close();
                broadCastToAllCallbacks(UploadService.onPostUploading, this.uploadPost, -1, -1);

                if(this.uploadPost.getType() == (CMTypes.FPIC))
                {
                    JSONArray jsonArrayHashtags = createJSONArrayFromHashtags(this.uploadPost.getEsaphHashtag());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                    jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                    jsonObject.put("PLSC", "PLPNB");//Command.
                    jsonObject.put("WAMP", this.uploadPost.getJsonArrayWAMP());
                    jsonObject.put("DES", this.uploadPost.getBeschreibung());
                    jsonObject.put("CC", this.getUserCountry(getApplication()));
                    jsonObject.put("ARR_EHT", jsonArrayHashtags);

                    File file = exportDataToCacheImage(this.uploadPost.getPID());
                    if(file == null)
                    {
                        broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.uploadPost, -1, -1);
                        return;
                    }

                    SocketResources resources = new SocketResources();
                    SSLSocket postUploadPictureSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), resources.getServerAddress(), resources.getServerPortPServer());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(postUploadPictureSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(postUploadPictureSocket.getOutputStream()));
                    writer.println(jsonObject.toString());
                    writer.flush();

                    Log.i(getClass().getName(), "Bild wird hochgeladen bitte warten...");
                    postUploadPictureSocket.setSoTimeout(20000);
                    if(reader.readLine().equals("1"))
                    {
                        System.out.println("PATH: " + file.getAbsolutePath());
                        long length = StorageHandler.fileLength(file);
                        System.out.println("LENGTH: " + length);
                        Log.i(getClass().getName(), "Bild größe wird übertragen.");
                        writer.println(length);
                        writer.flush();
                        Log.i(getClass().getName(), "Bild größe wurde übertragen.");

                        if(reader.readLine().equals("1"))
                        {
                            byte[] originalBytes = new byte[1024];
                            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                            OutputStream outputStream = postUploadPictureSocket.getOutputStream();

                            int transmittedBytes = 0;
                            int count;
                            while ((count = inputStream.read(originalBytes)) > 0)
                            {
                                outputStream.write(originalBytes, 0, count);
                                outputStream.flush();
                                transmittedBytes += count;
                                int progress = (int) (((float) transmittedBytes / (float) length) * 100);
                                broadCastToAllCallbacks(UploadService.onPostProgressChange, this.uploadPost, -1, progress);
                            }

                            inputStream.close();

                            JSONObject jsonObjectReply = new JSONObject(reader.readLine());

                            if(!jsonObjectReply.has("ERR"))
                            {
                                Log.i(getClass().getName(), "Bild wurde erfolgreich hochgeladen.");
                                updateDATABASEPostUploaded(this.uploadPost,
                                        jsonObjectReply.getString("PID"),
                                        jsonObjectReply.getLong("PPID"));

                                StorageHandler.updateInternPidWithServerPid(getApplicationContext(),
                                        uploadPost.getPID(),
                                        jsonObjectReply.getString("PID"),
                                        StorageHandler.FOLDER__SPOTLIGHT);

                                broadCastToAllCallbacks(UploadService.onPostUploadSuccess, this.uploadPost, jsonObjectReply.getLong("PPID"), -1);
                                SQLUploads sqlUploads = new SQLUploads(getApplicationContext());
                                sqlUploads.removeSinglePostCompletly(this.uploadPost.getPID()); //SUCCESFULL
                                sqlUploads.close();
                            }
                            else
                            {
                                broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.uploadPost, -1, -1);
                            }
                        }
                    }

                    postUploadPictureSocket.close();
                    reader.close();
                    writer.close();
                }
                else if(this.uploadPost.getType() == (CMTypes.FVID))
                {
                    JSONArray jsonArrayHashtags = createJSONArrayFromHashtags(this.uploadPost.getEsaphHashtag());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                    jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                    jsonObject.put("PLSC", "PLPNV");//Command.
                    jsonObject.put("DES", this.uploadPost.getBeschreibung());
                    jsonObject.put("WAMP", this.uploadPost.getJsonArrayWAMP());
                    jsonObject.put("CC", this.getUserCountry(getApplication()));
                    jsonObject.put("ARR_EHT", jsonArrayHashtags);

                    File file = exportDataToCacheVideo(this.uploadPost.getPID());
                    if(file == null)
                    {
                        broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.uploadPost, -1, -1);
                        return;
                    }

                    SocketResources resources = new SocketResources();
                    SSLSocket postUploadPictureSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), resources.getServerAddress(), resources.getServerPortPServer());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(postUploadPictureSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(postUploadPictureSocket.getOutputStream()));
                    writer.println(jsonObject.toString());
                    writer.flush();

                    Log.i(getClass().getName(), "Video wird hochgeladen bitte warten...");
                    postUploadPictureSocket.setSoTimeout(20000);
                    if(reader.readLine().equals("1"))
                    {
                        long length = StorageHandler.fileLength(file);
                        Log.i(getClass().getName(), "Video größe wird übertragen.");
                        writer.println(length);
                        writer.flush();
                        Log.i(getClass().getName(), "Video größe wurde übertragen.");

                        if(reader.readLine().equals("1"))
                        {
                            byte[] originalBytes = new byte[1024];
                            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                            OutputStream outputStream = postUploadPictureSocket.getOutputStream();

                            int transmittedBytes = 0;
                            int count;
                            while ((count = inputStream.read(originalBytes)) > 0)
                            {
                                Log.i(getClass().getName(), "Sending...");
                                outputStream.write(originalBytes, 0, count);
                                outputStream.flush();

                                transmittedBytes += count;
                                int progress = (int) (((float) transmittedBytes / (float) length) * 100);
                                broadCastToAllCallbacks(UploadService.onPostProgressChange, this.uploadPost, -1, progress);
                            }

                            inputStream.close();

                            JSONObject jsonObjectReply = new JSONObject(reader.readLine());

                            if(!jsonObjectReply.has("ERR"))
                            {
                                Log.i(getClass().getName(), "Video wurde erfolgreich hochgeladen.");
                                updateDATABASEPostUploaded(
                                        this.uploadPost,
                                        jsonObjectReply.getString("PID"),
                                        jsonObjectReply.getLong("PPID"));

                                broadCastToAllCallbacks(UploadService.onPostUploadSuccess,
                                        this.uploadPost,
                                        jsonObjectReply.getLong("PPID"), -1);

                                StorageHandler.updateInternPidWithServerPid(getApplicationContext(),
                                        this.uploadPost.getPID(),
                                        jsonObjectReply.getString("PID"),
                                        StorageHandler.FOLDER__SPOTLIGHT);


                                SQLUploads sqlUploads = new SQLUploads(getApplicationContext());
                                sqlUploads.removeSinglePostCompletly(this.uploadPost.getPID()); //SUCCESFULL
                                sqlUploads.close();
                            }
                            else
                            {
                                broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.uploadPost, -1, -1);
                            }
                        }
                    }

                    postUploadPictureSocket.close();
                    reader.close();
                    writer.close();
                }
            }
            catch (Exception ec)
            {
                SQLChats sqlChats = new SQLChats(getApplicationContext());
                broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.uploadPost, -1, -1);
                sqlChats.close();
                Log.i(getClass().getName(), "UploadData: " + ec);
            }
            finally
            {
                synchronized (UploadService.lockObject)
                {
                    if(UploadService.listUploading != null && this.uploadPost != null)
                    {
                        UploadService.listUploading.remove(this.uploadPost.getMESSAGE_ID());
                    }
                }
            }
        }
    }

    private class UploadLifeCloudPost implements Runnable //Do not use stupid worker class !
    {
        private boolean success = false;
        private Context context;
        private LifeCloudUpload lifeCloudUpload;

        public UploadLifeCloudPost(Context context, LifeCloudUpload lifeCloudUpload)
        {
            this.context = context;
            this.lifeCloudUpload = lifeCloudUpload;
        }

        private JSONArray createJSONArrayFromHashtags(ArrayList<EsaphHashtag> esaphHashtags) throws JSONException
        {
            JSONArray jsonArray = new JSONArray();
            for(int counter = 0; counter < esaphHashtags.size(); counter++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TAG", esaphHashtags.get(counter).getHashtagName());
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }

        @Override
        public void run()
        {
            try
            {
                broadCastToAllCallbacks(UploadService.onPostUploading, this.lifeCloudUpload, null);
                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(getApplicationContext());
                try
                {
                    File file = StorageHandler.getFile(context,
                            StorageHandler.FOLDER__LIFECLOUD,
                            lifeCloudUpload.getCLOUD_PID(),
                            null,
                            StorageHandler.IMAGE_PREFIX);

                    if(!StorageHandler.fileExists(file))
                        return;

                    SocketResources resources = new SocketResources();

                    JSONArray jsonArrayHashtags = createJSONArrayFromHashtags(this.lifeCloudUpload.getEsaphHashtag());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                    jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                    jsonObject.put("DES", lifeCloudUpload.getCLOUD_POST_DESCRIPTION());

                    if(lifeCloudUpload.getCLOUD_POST_TYPE() == CMTypes.FPIC)
                    {
                        jsonObject.put("LCS","LCULCI");
                    }
                    else if(lifeCloudUpload.getCLOUD_POST_TYPE() == CMTypes.FVID)
                    {
                        jsonObject.put("LCS","LCULCV");
                    }

                    jsonObject.put("ARR_EHT", jsonArrayHashtags);

                    SSLSocket sslSocket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortLCServer());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
                    writer.println(jsonObject.toString());
                    writer.flush();

                    if(reader.readLine().equals("1"))
                    {
                        long length = StorageHandler.fileLength(file);
                        writer.println(length);
                        writer.flush();

                        if(reader.readLine().equals("1"))
                        {
                            byte[] originalBytes = new byte[(int)length];
                            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                            OutputStream outputStream = sslSocket.getOutputStream();

                            int count;
                            while ((count = inputStream.read(originalBytes)) > 0)
                            {
                                outputStream.write(originalBytes, 0, count);
                                outputStream.flush();
                            }

                            inputStream.close();

                            String NEW_PID = reader.readLine();
                            if(NEW_PID != null && !NEW_PID.isEmpty())
                            {
                                sqlLifeCloud.updateLifeCloudPidAndStatusSetUploaded(lifeCloudUpload, NEW_PID);

                                StorageHandler.updateInternPidWithServerPid(context,
                                        lifeCloudUpload.getCLOUD_PID(),
                                        NEW_PID,
                                        StorageHandler.FOLDER__LIFECLOUD);
                                this.success = true;

                                // TODO: 26.06.2019 this is not working, need to change the database
                                /*
                                broadCastToAllCallbacks(UploadService.onPostUploadSuccess, lifeCloudUpload,
                                        sqlLifeCloud.getLifeCloudUploadByID(NEW_PID));*/
                            }
                        }
                    }

                    sslSocket.close();
                    writer.close();
                    reader.close();
                }
                catch (Exception ec)
                {
                    try
                    {
                        sqlLifeCloud.updateLifeCloudPostStatus(lifeCloudUpload, LifeCloudUpload.LifeCloudStatus.STATE_FAILED_NOT_UPLOADED);
                    }
                    catch (Exception e)
                    {
                    }
                    Log.i(getClass().getName(), "UploadLifeCloudPost() failed: " + ec);
                    Thread.sleep(2000);
                }
                finally
                {
                    sqlLifeCloud.close();
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "UploadLifeCloudPost() failed sleeping clausel: " + ec);
            }
            finally
            {
                if(!success)
                {
                    broadCastToAllCallbacks(UploadService.onPostFailedUpload, this.lifeCloudUpload, null);
                }
            }
        }
    }


    private static final int onPostFailedUpload = 0;
    private static final int onPostUploading = 1;
    private static final int onPostUploadSuccess = 2;
    private static final int onPostProgressChange = 3;


    private void broadCastToAllCallbacks(int type, final UploadPost uploadPost, final long PPID, final int progress)
    {
        Handler handler = new Handler(Looper.getMainLooper());

        if(uploadPost == null)
            return;

        for(int counter = 0; counter < uploadServiceCallBacks.size(); counter++)
        {
            Object object = uploadServiceCallBacks.get(counter);
            if(object instanceof UploadServiceCallBacksNormal)
            {
                final UploadServiceCallBacksNormal myCallback = (UploadServiceCallBacksNormal) object;
                if(type == UploadService.onPostFailedUpload)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallback.onPostFailedUpload(uploadPost);
                        }
                    });
                }
                else if(type == UploadService.onPostUploading)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallback.onPostUploading(uploadPost);
                        }
                    });
                }
                else if(type == UploadService.onPostUploadSuccess)
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            myCallback.onPostUploadSuccess(uploadPost, PPID);
                        }
                    });
                }
                else if(type == UploadService.onPostProgressChange)
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run() {
                            myCallback.onProgressUpdate(uploadPost, progress);
                        }
                    });
                }
            }
        }
    }

    private void broadCastToAllCallbacks(int type, final LifeCloudUpload lifeCloudUpload, final LifeCloudUpload lifeCloudUploadServer)
    {
        Handler handler = new Handler(Looper.getMainLooper());

        for(int counter = 0; counter < uploadServiceCallBacks.size(); counter++)
        {
            System.out.println("DEBUG LIFECLOUD BROADCAST: " + type);

            Object object = uploadServiceCallBacks.get(counter);
            if(object instanceof UploadServiceCallbacksLifeCloud)
            {
                final UploadServiceCallbacksLifeCloud myCallback = (UploadServiceCallbacksLifeCloud) object;
                if(type == UploadService.onPostFailedUpload)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallback.onPostFailedUpload(lifeCloudUpload);
                        }
                    });
                }
                else if(type == UploadService.onPostUploading)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallback.onPostUploading(lifeCloudUpload);
                        }
                    });
                }
                else if(type == UploadService.onPostUploadSuccess)
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run() {
                            myCallback.onPostUploadSuccess(lifeCloudUpload, lifeCloudUploadServer);
                        }
                    });
                }
            }
        }
    }

    // TODO: 26.06.2019 another function that need to be matched for a database update
    /*
    public void startWorkerUploadLifeCloud(final LifeCloudUpload lifeCloudUpload)
    {
        if(!isUploading(lifeCloudUpload.getCLOUD_PID()))
        {
            synchronized (UploadService.lockObject)
            {
                UploadService.listUploading.add(lifeCloudUpload.getCLOUD_PID());
                UploadService.executor.execute(new UploadLifeCloudPost(getApplication(), lifeCloudUpload));
            }
        }
    }*/
}
