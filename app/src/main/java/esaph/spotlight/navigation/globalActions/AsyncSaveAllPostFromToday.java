/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphDialogBubbly.EsaphDialog;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.Model.TodayMomentsUser;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncSaveAllPostFromToday implements Runnable
{
    private WeakReference<EsaphActivity> esaphActivityWeakReference;
    private WeakReference<AVLoadingIndicatorView> avLoadingIndicatorViewWeakReference;
    private WeakReference<ImageView> imageViewWeakReference;
    private WeakReference<EsaphMomentsRecylerView> esaphMomentsRecylerViewWeakReference;
    private TodayMomentsUser todayMomentsUser;
    private int saveStatus = -1;

    public AsyncSaveAllPostFromToday(EsaphActivity esaphActivity,
                                     TodayMomentsUser todayMomentsUser,
                                     EsaphMomentsRecylerView esaphMomentsRecylerView,
                                     ImageView imageView,
                                     AVLoadingIndicatorView avLoadingIndicatorView)
    {
        this.todayMomentsUser = todayMomentsUser;
        this.esaphActivityWeakReference = new WeakReference<EsaphActivity>(esaphActivity);
        this.esaphMomentsRecylerViewWeakReference = new WeakReference<EsaphMomentsRecylerView>(esaphMomentsRecylerView);
        this.avLoadingIndicatorViewWeakReference = new WeakReference<AVLoadingIndicatorView>(avLoadingIndicatorView);
        this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
    }

    private void displayProgress()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                AVLoadingIndicatorView avLoadingIndicatorView = avLoadingIndicatorViewWeakReference.get();
                if(avLoadingIndicatorView != null)
                {
                    avLoadingIndicatorView.smoothToShow();
                }
            }
        });
    }

    private List<ConversationMessage> getList()
    {
        SQLChats sqlChats = new SQLChats(esaphActivityWeakReference.get());
        List<ConversationMessage> list = sqlChats.getALLUnsavedFromMeConversationMessagesTodayUNSORTED(this.todayMomentsUser.getUID());
        sqlChats.close();
        return list;
    }

    @Override
    public void run()
    {
        try
        {
            displayProgress();

            Activity activity = (Activity)esaphActivityWeakReference.get();
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    final ImageView imageView = imageViewWeakReference.get();
                    if(imageView != null)
                    {
                        imageView.animate().scaleX(0.8f).scaleY(0.8f).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imageView.setScaleX(0.8f);
                                imageView.setScaleY(0.8f);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                imageView.setScaleX(0.8f);
                                imageView.setScaleY(0.8f);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                    }
                }
            });

            List<ConversationMessage> listToSave = getList();

            for(ConversationMessage conversationMessage : listToSave)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PLSC", "PLSPP");
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                if(SpotLightLoginSessionHandler.getLoggedUID() == conversationMessage.getABS_ID()) //Immer mpefanger des postings.
                {
                    jsonObject.put("FUSRN", conversationMessage.getID_CHAT());
                }
                else
                {
                    jsonObject.put("FUSRN", conversationMessage.getABS_ID());
                }
                jsonObject.put("PPID", conversationMessage.getMESSAGE_ID_SERVER());

                SocketResources resources = new SocketResources();
                SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.esaphActivityWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                String result = reader.readLine();
                if(result.equals("1")) //SAVED
                {
                    this.saveStatus = 1;
                    SQLChats sqlChats = new SQLChats(this.esaphActivityWeakReference.get());
                    sqlChats.insertPostSaved(SpotLightLoginSessionHandler.getLoggedUID(),
                            conversationMessage.getMESSAGE_ID());
                    sqlChats.close();
                    handleResult(Boolean.TRUE);
                    return;
                }
            }
            handleResult(Boolean.FALSE);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSaveAllPostFromToday() failed: " + ec);
            handleResult(Boolean.FALSE);
        }
    }

    private void handleResult(final Boolean aBoolean)
    {
        Activity activity = (Activity) esaphActivityWeakReference.get();
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AVLoadingIndicatorView avLoadingIndicatorView = avLoadingIndicatorViewWeakReference.get();
                if(avLoadingIndicatorView != null)
                {
                    avLoadingIndicatorView.smoothToHide();
                }

                final ImageView imageView = imageViewWeakReference.get();
                if(imageView != null)
                {
                    imageView.animate().scaleX(1.0f).scaleY(1.0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            imageView.setScaleX(1.0f);
                            imageView.setScaleY(1.0f);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            imageView.setScaleX(1.0f);
                            imageView.setScaleY(1.0f);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {
                        }
                    }).start();
                }

                if(aBoolean != null && aBoolean)
                {
                    EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewWeakReference.get();
                    if(esaphMomentsRecylerView != null)
                    {
                        esaphMomentsRecylerView.notifyDataSetChanged();
                    }

                    todayMomentsUser.setHasSavedAll(true);
                }
                else
                {
                    EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewWeakReference.get();
                    if(esaphMomentsRecylerView != null)
                    {
                        esaphMomentsRecylerView.notifyDataSetChanged();
                    }

                    EsaphActivity esaphActivity = esaphActivityWeakReference.get();
                    if(esaphActivity != null)
                    {
                        EsaphDialog esaphDialog =
                                new EsaphDialog(
                                        esaphActivity,
                                        esaphActivity.getResources().getString(R.string.txt_ups),
                                        esaphActivity.getResources().getString(R.string.txt_something_went_wrong));
                        esaphDialog.show();
                    }
                }
                todayMomentsUser.setLocked(false);
            }
        });
    }
}
