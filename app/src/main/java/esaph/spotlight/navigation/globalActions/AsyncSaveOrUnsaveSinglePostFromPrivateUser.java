package esaph.spotlight.navigation.globalActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentBroadcasts;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncSaveOrUnsaveSinglePostFromPrivateUser implements Runnable
{
    private SoftReference<PostStateListener> postStateListenerSoftReference;
    private WeakReference<Context> contextWeakReference;
    private ConversationMessage conversationMessage;
    private int saveStatus = -1;
    private boolean shouldDeleted = false;

    public AsyncSaveOrUnsaveSinglePostFromPrivateUser(Context context,
                                                      PostStateListener postStateListener,
                                                      ConversationMessage conversationMessage)
    {
        this.postStateListenerSoftReference = new SoftReference<PostStateListener>(postStateListener);
        this.contextWeakReference = new WeakReference<Context>(context);
        this.conversationMessage = conversationMessage;
    }

    public interface PostStateListener
    {
        void onAddedToGallery(ConversationMessage conversationMessage);
        void onRemovedFromGallery(ConversationMessage conversationMessage);
        void onPostDied(ConversationMessage conversationMessage);
        void onFailed(ConversationMessage conversationMessage);
    }

    @Override
    public void run()
    {
        try
        {
            this.shouldDeleted = isPostInDeleteMode(conversationMessage, contextWeakReference.get());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLSPP");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("PPID", this.conversationMessage.getMESSAGE_ID_SERVER()); //We are so smart, that we only need the PPID id, no context of users, its getting by absender and ThreadUID.

            if(this.conversationMessage.getABS_ID() == this.conversationMessage.getID_CHAT())
            {
                jsonObject.put("REC_ID", SpotLightLoginSessionHandler.getLoggedUID());
            }
            else
            {
                jsonObject.put("REC_ID", this.conversationMessage.getID_CHAT());
            }


            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();


            String result = reader.readLine();
            System.out.println("PoST FROM PRIVATE USER WAS SUCCESFULLY SAVED: " + result);
            if(result.equals("1")) //SAVED
            {
                this.saveStatus = 1;
                SQLChats sqlChats = new SQLChats(this.contextWeakReference.get());

                long SAVED_ID = sqlChats.insertPostSaved(SpotLightLoginSessionHandler.getLoggedUID(),
                        conversationMessage.getMESSAGE_ID());

                sqlChats.close();

                if(conversationMessage instanceof ChatVideo)
                {
                    ChatVideo chatVideo = (ChatVideo) conversationMessage;

                        /*
                        chatVideo.getListUserSaved().add(new SavedInfo(chatVideo.getID_CHAT(),
                                SAVED_ID,
                                SpotLightLoginSessionHandler.getLoggedUID(),
                                SpotLightLoginSessionHandler.getLoggedUsername()));
                                */
                }
                else if(conversationMessage instanceof ChatImage)
                {
                    ChatImage chatImage = (ChatImage) conversationMessage;

                        /*
                        chatImage.getListUserSaved().add(new SavedInfo(chatImage.getID_CHAT(),
                                SAVED_ID,
                                SpotLightLoginSessionHandler.getLoggedUID(),
                                SpotLightLoginSessionHandler.getLoggedUsername()));
                                */
                }


                handleResult(Boolean.TRUE);
                return;
            }
            else if(result.equals("2"))
            {
                this.saveStatus = 2;
                SQLChats sqlChats = new SQLChats(this.contextWeakReference.get());
                long SAVED_ID = sqlChats.getPostSavedId(SpotLightLoginSessionHandler.getLoggedUID(), conversationMessage.getMESSAGE_ID());


                sqlChats.unSavePrivateMomentPostDeleteWhenPassedTime(
                        SAVED_ID,
                        conversationMessage.getMESSAGE_ID(),
                        this.shouldDeleted,
                        conversationMessage.getID_CHAT());
                sqlChats.close();
                handleResult(Boolean.TRUE);
                return;
            }

            handleResult(Boolean.FALSE);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSaveOrUnsaveSinglePostFromPrivateUser() failed: " + ec);
            handleResult(Boolean.FALSE);
        }
    }

    private void handleResult(final Boolean aBoolean)
    {
        ((Activity) contextWeakReference.get()).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                PostStateListener postStateListener = postStateListenerSoftReference.get();
                if(postStateListener != null)
                {
                    if(shouldDeleted && aBoolean)
                    {
                        Context context = contextWeakReference.get();
                        if(context != null)
                        {
                            Intent intent = new Intent();
                            intent.setAction(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY);
                            context.sendBroadcast(intent);
                        }

                        postStateListener.onPostDied(conversationMessage);
                    }
                    else
                    {
                        if(saveStatus > -1)
                        {
                            if(saveStatus == 1)
                            {
                                if(aBoolean)
                                {
                                    Context context = contextWeakReference.get();
                                    if(context != null)
                                    {
                                        Intent intent = new Intent();
                                        intent.setAction(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY);
                                        context.sendBroadcast(intent);
                                    }

                                    postStateListener.onAddedToGallery(conversationMessage);
                                }
                                else
                                {
                                    postStateListener.onFailed(conversationMessage);
                                }
                            }
                            else if(saveStatus == 2)
                            {
                                if(aBoolean)
                                {
                                    Context context = contextWeakReference.get();
                                    if(context != null)
                                    {
                                        Intent intent = new Intent();
                                        intent.setAction(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY);
                                        context.sendBroadcast(intent);
                                    }

                                    postStateListener.onRemovedFromGallery(conversationMessage);
                                }
                                else
                                {
                                    postStateListener.onFailed(conversationMessage);
                                }
                            }
                        }
                        else
                        {
                            postStateListener.onFailed(conversationMessage);
                        }
                    }
                }
            }
        });
    }


    public static boolean isPostInDeleteMode(ConversationMessage conversationMessage, Context context)
    {
        long PARTNER_UID;
        if(conversationMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID())
        {
            PARTNER_UID = conversationMessage.getID_CHAT();
        }
        else
        {
            PARTNER_UID = SpotLightLoginSessionHandler.getLoggedUID();
        }

        SQLChats sqlChats = new SQLChats(context);
        boolean iSAVED = sqlChats.hasSaved(conversationMessage.getMESSAGE_ID(), SpotLightLoginSessionHandler.getLoggedUID());
        boolean pSAVED = sqlChats.hasSaved(conversationMessage.getMESSAGE_ID(), PARTNER_UID);
        sqlChats.close();

        if(iSAVED && !pSAVED)
        {
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.HOUR_OF_DAY, -24);
            long timeMinusOneDay = calendar.getTimeInMillis();

            if(timeMinusOneDay >= conversationMessage.getMessageTime())
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return false;
    }

}
