package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.AudioMessagePlayer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class EsaphAudioMessagePlayer
{
    private static EsaphAudioMessagePlayer audioMessagePlayer;
    private SocketResources resources = new SocketResources();
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService;
    private List<String> listAudiosDownloading = new ArrayList<>();

    private EsaphAudioMessagePlayer(Context context)
    {
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    public static EsaphAudioMessagePlayer with(Context context)
    {
        if(EsaphAudioMessagePlayer.audioMessagePlayer == null)
            audioMessagePlayer = new EsaphAudioMessagePlayer(context);

        return EsaphAudioMessagePlayer.audioMessagePlayer;
    }


    public interface AudioHandlerCallBack
    {
        void onAudioMessageAvailable(File file);
        void onAudioMessageDownloadFailed();
    }

    public void handleAudioMessage(AudioMessage audioMessage, AudioHandlerCallBack audioHandlerCallBack) //Check if need to be downloaded
    {
        if(listAudiosDownloading.contains(audioMessage.getAID()))
        {

        }
        else
        {
            listAudiosDownloading.add(audioMessage.getAID());
            executorService.submit(new HandleAudioMessage(new AudioMessageHandleObject(audioMessage.getAID(), new WeakReference<AudioHandlerCallBack>(audioHandlerCallBack))));
        }
    }

    private class AudioMessageHandleObject
    {
        private String AID;
        private WeakReference<AudioHandlerCallBack> audioHandlerCallBackWeakReference;

        public AudioMessageHandleObject(String AID, WeakReference<AudioHandlerCallBack> weakReference)
        {
            this.AID = AID;
            this.audioHandlerCallBackWeakReference = weakReference;
        }
    }

    private class HandleAudioMessage implements Runnable
    {
        private AudioMessageHandleObject audioMessageHandleObject;

        public HandleAudioMessage(AudioMessageHandleObject audioMessageHandleObject)
        {
            this.audioMessageHandleObject = audioMessageHandleObject;
        }

        private File downloadAudioFile(File file)
        {
            try
            {
                JSONObject json = new JSONObject();
                json.put("PLSC", "PLDAF");
                json.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                json.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                json.put("AID", audioMessageHandleObject.AID);

                if(reusedReference())
                    return null;

                SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(json.toString());
                writer.flush();


                long filesize = Long.parseLong(reader.readLine());

                DataInputStream dateiInputStream = new DataInputStream(socket.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int read = 0;
                int totalRead = 0;
                long remaining = filesize;

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                            /*
                            ProgressBar progressBar = globalImageLoading.progressBar;
                            if(progressBar != null)
                            {
                                progressBar.clearAnimation();
                                progressBar.animate().cancel();
                                animY.cancel();
                            }*/
                    }
                });

                while((read = dateiInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0)
                {
                    if(reusedReference())
                        break;

                    totalRead += read;
                    remaining -= read;

                    final int newProgress = (int) ((totalRead * 100) / filesize);
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                                /*
                                ProgressBar progressBar = globalImageLoading.progressBar;
                                if(progressBar != null)
                                {
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), newProgress);
                                    animation.setDuration(100);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                }*/
                        }
                    });

                    fileOutputStream.write(buffer, 0, read);
                }
                dateiInputStream.close();

                if(filesize != totalRead)
                {
                    context.deleteFile(file.getName());
                }
                else
                {
                    return file;
                }


                reader.close();
                writer.close();
                socket.close();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "downloadAudioFile() failed: " + ec);
                context.deleteFile(file.getName());
            }
            return null;
        }

        private boolean reusedReference()
        {
            if(this.audioMessageHandleObject.audioHandlerCallBackWeakReference.get() != null)
            {
                return false;
            }

            return true;
        }

        @Override
        public void run()
        {
            try
            {
                File fileAudio = StorageHandler.getFile(context,
                        StorageHandler.FOLDER__SPOTLIGHT_AUDIO,
                        audioMessageHandleObject.AID,
                        null,
                        StorageHandler.AUDIO_PREFIX);

                if(!StorageHandler.fileExists(fileAudio))
                {
                    fileAudio = downloadAudioFile(fileAudio);
                }

                if(!reusedReference())
                {
                    handler.post(new DisplayResult(this.audioMessageHandleObject, fileAudio));
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "HandleAudioMessage_run() failed: " + ec);
                handler.post(new DisplayResult(this.audioMessageHandleObject, null));
            }
            finally
            {
                listAudiosDownloading.remove(this.audioMessageHandleObject.AID);
            }
        }
    }


    private class DisplayResult implements Runnable
    {
        private AudioMessageHandleObject audioMessageHandleObject;
        private File fileAudio;

        public DisplayResult(AudioMessageHandleObject audioMessageHandleObject, File fileAudio)
        {
            this.audioMessageHandleObject = audioMessageHandleObject;
            this.fileAudio = fileAudio;
        }

        private boolean reusedReference()
        {
            if(this.audioMessageHandleObject.audioHandlerCallBackWeakReference.get() != null)
            {
                return false;
            }

            return true;
        }

        @Override
        public void run()
        {
            try
            {
                if(!reusedReference() && fileAudio != null)
                {
                    if(StorageHandler.fileExists(fileAudio))
                    {
                        this.audioMessageHandleObject.audioHandlerCallBackWeakReference.get().onAudioMessageAvailable(this.fileAudio);
                    }
                    else
                    {
                        this.audioMessageHandleObject.audioHandlerCallBackWeakReference.get().onAudioMessageDownloadFailed();
                    }
                }
                else if(!reusedReference() && fileAudio == null)
                {
                    this.audioMessageHandleObject.audioHandlerCallBackWeakReference.get().onAudioMessageDownloadFailed();
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "DisplayResult_run() failed: " + ec);
            }
        }
    }




}
