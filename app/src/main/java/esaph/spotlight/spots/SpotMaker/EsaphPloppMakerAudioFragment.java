package esaph.spotlight.spots.SpotMaker;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphAudioRecorder.EsaphAudioRecorderSampler;
import esaph.spotlight.Esaph.EsaphAudioRecorder.EsaphAudioRecorderVisualizerView;
import esaph.spotlight.Esaph.EsaphColorSlider.EsaphColorSlider;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.ConversationReceiverHelper;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotMakerFormatAudioDefault;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.SpotMakerView;

public class EsaphPloppMakerAudioFragment extends EsaphGlobalCommunicationFragment
{
    private static SimpleDateFormat simpleDateFormatAudioMillis = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private RelativeLayout relativeLayoutRecordingAudio;
    private ChatPartner currentChatPartner;
    private EsaphSpotMakerListener esaphSpotMakerListener;
    private TextView textViewSent;
    private EsaphColorSlider esaphColorSlider;
    private ImageView imageViewEditBackground;
    private RelativeLayout relativeLayoutBottomEditingTools;
    private TextView textViewRecordingStateInfo;
    private SpotMakerView spotMakerView;

    public EsaphPloppMakerAudioFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(getActivity());
    }

    public static EsaphPloppMakerAudioFragment showWith(ChatPartner chatPartner,
                                                        EsaphSpotMakerListener esaphSpotMakerListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PrivateChat.KEY_CHAT_PARTNER_SER, chatPartner);
        bundle.putSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER, esaphSpotMakerListener);
        EsaphPloppMakerAudioFragment esaphPloppMakerAudio = new EsaphPloppMakerAudioFragment();
        esaphPloppMakerAudio.setArguments(bundle);
        return esaphPloppMakerAudio;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        relativeLayoutRecordingAudio = null;
        esaphSpotMakerListener = null;
        textViewSent = null;
        textViewRecordingStateInfo = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(shouldDeleteFile && StorageHandler.fileExists(fileRecordedAudio)) //Deleting recorded audio file, when onDestroy is Called.
        {
            fileRecordedAudio.delete();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            currentChatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.KEY_CHAT_PARTNER_SER);
            esaphSpotMakerListener = (EsaphSpotMakerListener) bundle.getSerializable(PrivateChat.KEY_CHAT_INTERFACE_SPOT_MAKER_FINISH_LISTENER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_plopp_maker_audio, container, false);
        relativeLayoutRecordingAudio = rootView.findViewById(R.id.esaphAudioRecordingLayout);
        spotMakerView = rootView.findViewById(R.id.spotMakerView);
        barVisualizer = rootView.findViewById(R.id.VisualizerRecordingAudioView);
        imageViewTryAgainRecord = rootView.findViewById(R.id.imageViewTryAgainRecordingAudio);
        textViewSent = rootView.findViewById(R.id.imageViewSent);
        textViewRecordingStateInfo = rootView.findViewById(R.id.textViewAudioInfoState);
        esaphColorSlider = rootView.findViewById(R.id.esaphColorSliderSliding);
        imageViewEditBackground = rootView.findViewById(R.id.imageViewBackgroundColor);
        relativeLayoutBottomEditingTools = rootView.findViewById(R.id.relativLayoutBottomEditingTools);

        rootView.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EsaphActivity esaphActivity = getEsaphActivity();
                if(esaphActivity != null)
                {
                    esaphActivity.onActivityDispatchBackPressEvent();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        SpotMakerFormatAudioDefault spotMakerFormatAudio = new SpotMakerFormatAudioDefault(getContext());
        spotMakerView.setFormat(spotMakerFormatAudio);

        barVisualizer.setmRenderColor(Color.WHITE);

        relativeLayoutRecordingAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                relativeLayoutRecordingAudio.setClickable(false);
                relativeLayoutRecordingAudio.setFocusable(false);
                relativeLayoutRecordingAudio.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(relativeLayoutRecordingAudio != null)
                        {
                            relativeLayoutRecordingAudio.setClickable(true);
                            relativeLayoutRecordingAudio.setFocusable(true);
                        }
                    }
                }, 1000);
                checkPermissionsAndStartOrStop();
            }
        });

        imageViewEditBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyElementEditingPartMode(ChooseEditingMode.MODE_BACKGROUND);
            }
        });

        textViewSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAudioMessage();
            }
        });

        spotMakerView.obtainAudio(); //To init json.
        spotMakerView.obtainBackground(); //To init json.
        spotMakerView.commit();
    }

    private ChooseEditingMode chooseEditingMode = ChooseEditingMode.NONE;
    private void applyElementEditingPartMode(ChooseEditingMode chooseEditingMode)
    {
        this.chooseEditingMode = chooseEditingMode;

        switch (this.chooseEditingMode)
        {
            case NONE:
                updateUIEditingPartMode(null);
                EsaphPloppEditingAnimationHelper.removeColorPicker(relativeLayoutBottomEditingTools);
                break;

            case MODE_BACKGROUND:
                applyBackgroundEditingMode();
                break;
        }
    }

    private void updateUIEditingPartMode(View viewSelected)
    {
        imageViewEditBackground.setBackground(null);
        Context context = getContext();
        if(context != null && viewSelected != null)
        {
            viewSelected.setBackground(ContextCompat.getDrawable(context, R.drawable.background_spot_editor_selected_under_option));
        }
    }

    private void applyBackgroundEditingMode()
    {
        esaphColorSlider.setListener(new EsaphColorSlider.OnColorSelectedListener()
        {
            @Override
            public void onColorChanged(int position, int color)
            {
                try
                {
                    spotMakerView.obtainBackground().setBackgroundColor(color);
                    spotMakerView.commit();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReleasedColorPicker() {

            }
        });

        updateUIEditingPartMode(imageViewEditBackground);
        EsaphPloppEditingAnimationHelper.showColorPicker(relativeLayoutBottomEditingTools);
    }

    private File fileRecordedAudio;
    private EsaphAudioRecorderVisualizerView barVisualizer;
    private EsaphAudioRecorderSampler recordingSampler;
    private ImageView imageViewTryAgainRecord;
    public void startRecording()
    {
        try
        {
            timer = new Timer();
            removeSentButton();

            if(StorageHandler.fileExists(fileRecordedAudio))
            {
                fileRecordedAudio.delete();
            }

            if(recordingSampler != null && recordingSampler.isRecording())
                return;

            if(recordingSampler == null)
                recordingSampler = new EsaphAudioRecorderSampler();

            recordingSampler.setVolumeListener(new EsaphAudioRecorderSampler.CalculateVolumeListener()
            {
                @Override
                public void onCalculateVolume(int volume)
                {
                }
            });  // for custom implements
            recordingSampler.link(barVisualizer); // link to visualizer

            fileRecordedAudio = StorageHandler.getFile(getContext(),
                    StorageHandler.FOLDER__SPOTLIGHT_AUDIO,
                    UUID.randomUUID().toString(),
                    null,
                    StorageHandler.AUDIO_PREFIX);

            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    handlerUIThread.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            textViewRecordingStateInfo.setText(simpleDateFormatAudioMillis.format(System.currentTimeMillis() - recordingSampler.getStartMillis()));
                        }
                    });
                }

            },0,1000);//Update text every second


            recordingSampler.startRecording(new FileOutputStream(fileRecordedAudio).getFD());
            textViewRecordingStateInfo.setText(getResources().getString(R.string.txt_speek_somethink));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "startRecording() failed: " + ec);
            stopRecording();
        }
    }


    public void stopRecording()
    {
        long totalRecordTime = 0;
        if(recordingSampler != null && recordingSampler.isRecording())
        {
            timer.cancel();
            timer.purge();
            totalRecordTime = recordingSampler.stopRecording();
            recordingSampler = null;
        }

        relativeLayoutRecordingAudio.setOnClickListener(null);
        imageViewTryAgainRecord.setVisibility(View.VISIBLE);
        imageViewTryAgainRecord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                relativeLayoutRecordingAudio.setOnClickListener(new View.OnClickListener() //Its getting removed, when recording stopped. That he wont start again.
                {
                    @Override
                    public void onClick(View v)
                    {
                        checkPermissionsAndStartOrStop();
                    }
                });

                textViewRecordingStateInfo.setText(getResources().getString(R.string.txt_tab_to_record));
                imageViewTryAgainRecord.setVisibility(View.GONE);
                imageViewTryAgainRecord.setOnClickListener(null);
                removeSentButton();
            }
        });

        textViewRecordingStateInfo.setText(simpleDateFormatAudioMillis.format(totalRecordTime));
        showSentButton();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopRecording();
    }

    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 2711;
    private void checkPermissionsAndStartOrStop()
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSION_RECORD_AUDIO);
            }
            else
            {
                if(recordingSampler != null && recordingSampler.isRecording())
                {
                    stopRecording();
                }
                else
                {
                    startRecording();
                }
            }
        }
    }


    private Timer timer;
    private Handler handlerUIThread = new Handler(Looper.getMainLooper());

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean shouldDeleteFile = true;
    private void sendAudioMessage()
    {
        final AudioMessage audioMessage = (AudioMessage) spotMakerView.getSpotMessage();

        audioMessage.setABS_ID(SpotLightLoginSessionHandler.getLoggedUID());
        audioMessage.setID_CHAT(currentChatPartner.getUID_CHATPARTNER());
        audioMessage.setAID(fileRecordedAudio.getName().replace(StorageHandler.AUDIO_PREFIX, ""));
        audioMessage.setAbsender(SpotLightLoginSessionHandler.getLoggedUsername());
        shouldDeleteFile = false;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLChats sqlChats = null;
                try
                {
                    sqlChats = new SQLChats(getContext());
                    sqlChats.insertNewAudio(audioMessage, ConversationReceiverHelper.getReceiverFromMessage(audioMessage));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(sqlChats != null)
                    {
                        sqlChats.close();
                    }
                }

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(esaphSpotMakerListener != null)
                        {
                            esaphSpotMakerListener.onDoneEditingSent(audioMessage);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }




    private void showSentButton()
    {
        textViewSent.animate().alpha(1.0f).setDuration(125).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textViewSent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }


    private void removeSentButton()
    {
        textViewSent.animate().alpha(0.0f).setDuration(125).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textViewSent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                textViewSent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
