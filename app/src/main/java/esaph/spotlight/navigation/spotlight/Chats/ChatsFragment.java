package esaph.spotlight.navigation.spotlight.Chats;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphColorCircle.EsaphSweepGradientShader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.Esaph.EsaphImageCropper.CropImage;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.PreLogin.Dialogs.BottomSheetRegister;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.einstellungen.AppPreferencesMain;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.FriendStatusListener;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.AdapterChats;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;
import esaph.spotlight.navigation.spotlight.Chats.Profilbild.DialogChangeProfilbild;
import esaph.spotlight.navigation.spotlight.Moments.InternViews.Aktuelle.MomentSynchCheckListener;
import esaph.spotlight.navigation.spotlight.PhoneContact;
import esaph.spotlight.navigation.spotlight.PublicSearch.PublicSearchFragment;
import esaph.spotlight.services.NotificationAndMessageHandling.MessageHandler;
import esaph.spotlight.services.OtherWorkers.UploadNewProfilbild;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UploadService.UploadPost;
import esaph.spotlight.services.UploadService.UploadService;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

public class ChatsFragment extends EsaphGlobalCommunicationFragment
{
    private EsaphCircleImageView esaphCircleImageViewProfilbild;
    private View viewNoChats;

    private ListView listViewChats;
    private AdapterChats adapterChats;
    private EditText editTextSearchChats;
    private ImageView imageViewSearchPersons;
    private ImageView imageViewOptions;
    private TextView textViewAnfragenCount;

    public static ChatsFragment getInstance()
    {
        return new ChatsFragment();
    }

    public ChatsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        textViewAnfragenCount = null;
        viewNoChats = null;
        adapterChats = null;
        dialogShowPictureUploading = null;
        mInterstitialAd = null;
        imageViewSearchPersons = null;
        esaphCircleImageViewProfilbild = null;
        if(editTextSearchChats != null)
        {
            editTextSearchChats.addTextChangedListener(null);
        }
        editTextSearchChats = null;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(dialogShowPictureUploading != null && dialogShowPictureUploading.isShowing())
        {
            dialogShowPictureUploading.virtualCallPauseMediaPlayer();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(dialogShowPictureUploading != null && dialogShowPictureUploading.isShowing())
        {
            dialogShowPictureUploading.virtualCallResumeMediaPlayer();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        esaphCircleImageViewProfilbild.setEsaphShaderBackground(new EsaphSweepGradientShader(getResources().getIntArray(R.array.colorGradient_Chats)));

        listViewChats.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void loadMore()
            {
                loadMoreChatPartners();
            }
        });

        imageViewSearchPersons.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(SpotLightLoginSessionHandler.isDemoMode())
                {
                    BottomSheetRegister.getInstance().show(getChildFragmentManager(), BottomSheetRegister.class.getName());
                }
                else
                {
                    Intent intent = new Intent(getContext(), PublicSearchFragment.class);
                    startActivity(intent);
                }
            }
        });

        imageViewOptions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivityForResult(new Intent(getContext(), AppPreferencesMain.class), 100);
            }
        });

        esaphCircleImageViewProfilbild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(SpotLightLoginSessionHandler.isDemoMode())
                {
                    BottomSheetRegister.getInstance().show(getChildFragmentManager(), BottomSheetRegister.class.getName());
                }
                else
                {
                    Context context = getContext();
                    if(context != null && isAdded())
                    {
                        new DialogChangeProfilbild(context,
                                ChatsFragment.this).show();
                    }
                }
            }
        });

        editTextSearchChats.addTextChangedListener(textWatcherMainInput);

        setProfilbild();
        setAnfragenCounter();

        viewNoChats.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Context context = getContext();
                if(context != null)
                {
                    Intent intentShare = new Intent();
                    intentShare.setAction(Intent.ACTION_SEND);
                    intentShare.putExtra(Intent.EXTRA_TEXT,
                            getResources().getString(R.string.XX_txt_share_own_app) + " - " + SpotLightLoginSessionHandler.getLoggedUsername());
                    intentShare.setType("text/plain");
                    startActivity(intentShare);
                }
            }
        });

        listViewChats.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                stopSearching();

                Object object = adapterChats.getItem(position);

                if(object instanceof ChatPartner)
                {
                    ChatPartner partner = (ChatPartner) object;

                    FragmentActivity activity = getActivity();
                    if(activity != null)
                    {
                        if(mInterstitialAd != null && mInterstitialAd.isLoaded())
                        {
                            mInterstitialAd.show();
                        }
                        else
                        {
                            loadNewAd();
                        }

                        Intent intent = new Intent(getContext(), PrivateChat.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PrivateChat.extraChatPartner, partner);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, PrivateChat.REQUEST_CODE_FINISHED);
                    }
                }
                else if(object instanceof UploadPost)
                {
                    UploadPost uploadPost = (UploadPost) object;
                    adapterChats.getUploadService().startNewUpload(uploadPost);
                }
                else if(object instanceof PhoneContact)
                {
                    Context context = getContext();
                    if(context != null)
                    {
                        Intent intentShare = new Intent();
                        intentShare.setAction(Intent.ACTION_SEND);
                        intentShare.putExtra(Intent.EXTRA_TEXT,
                                getResources().getString(R.string.XX_txt_share_own_app) + " - " + SpotLightLoginSessionHandler.getLoggedUsername());
                        intentShare.setType("text/plain");
                        startActivity(intentShare);
                    }
                }
            }
        });

        listViewChats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object object = adapterChats.getItem(position);

                if(object instanceof ChatPartner)
                {
                    ChatPartner chatPartner = (ChatPartner) object;
                    dialogCurrentShowing =
                            new DialogChooseOperationLongClickUser(
                                    getActivity(),
                                    ChatsFragment.this,
                                    chatPartner,
                                    friendStatusListenerRemoveFriendForBlockedOrSomenthing);

                    dialogCurrentShowing.show();
                }
                else if(object instanceof UploadPost)
                {
                    UploadPost uploadPost = (UploadPost) object;
                    if(dialogShowPictureUploading != null)
                    {
                        if(dialogShowPictureUploading.isShowing())
                        {
                            dialogShowPictureUploading.cancel();
                        }
                    }

                    dialogShowPictureUploading = new DialogShowPictureUploading(getActivity(), uploadPost, adapterChats);
                    dialogShowPictureUploading.setCanceledOnTouchOutside(false);
                    dialogShowPictureUploading.show();
                }
                return true;
            }
        });

        adapterChats = new AdapterChats(getActivity(), new DialogCallBackLifeCyle() {
            @Override
            public void onStartDialog(UploadPost uploadPost) {
                if(dialogShowPictureUploading != null)
                {
                    if(dialogShowPictureUploading.isShowing())
                    {
                        dialogShowPictureUploading.cancel();
                    }
                }

                dialogShowPictureUploading = new DialogShowPictureUploading(getActivity(), uploadPost, adapterChats);
                dialogShowPictureUploading.setCanceledOnTouchOutside(false);
                dialogShowPictureUploading.show();
            }

            @Override
            public void onPostFailedUpload(UploadPost uploadPost)
            {
                if(dialogShowPictureUploading != null)
                {
                    if(dialogShowPictureUploading.isShowing())
                    {
                        dialogShowPictureUploading.onPostFailedUpload(uploadPost);
                    }
                }
            }

            @Override
            public void onPostUploading(String PID)
            {
                if(dialogShowPictureUploading != null)
                {
                    if(dialogShowPictureUploading.isShowing())
                    {
                        dialogShowPictureUploading.onPostUploading(PID);
                    }
                }
            }

            @Override
            public void onPostUploadSuccess(UploadPost uploadPost, long PPID)
            {
                if(dialogShowPictureUploading != null)
                {
                    if(dialogShowPictureUploading.isShowing())
                    {
                        dialogShowPictureUploading.onPostUploadSuccess(uploadPost, PPID);
                    }
                }
            }
        },this);


        listViewChats.setAdapter(adapterChats);
        loadMoreChatPartners();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.navigation_child_chats, container, false);
        listViewChats = (ListView) rootView.findViewById(R.id.chatListView);
        editTextSearchChats = (EditText) rootView.findViewById(R.id.editTextSearchChats);
        viewNoChats = (View) rootView.findViewById(R.id.linearLayoutNoSearchResults);
        esaphCircleImageViewProfilbild = (EsaphCircleImageView) rootView.findViewById(R.id.imageViewOwnProfilbild);
        imageViewSearchPersons = (ImageView) rootView.findViewById(R.id.imageViewSearchForPersons);
        imageViewOptions = (ImageView) rootView.findViewById(R.id.imageViewOptions);
        textViewAnfragenCount = (TextView) rootView.findViewById(R.id.textViewAnfragenCount);
        return rootView;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return false;
    }

    public void setCurrentFragment(EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment)
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                    .replace(R.id.esaphMainFrameLayout, esaphGlobalCommunicationFragment)
                    .commit();
        }
    }


    public interface DialogCallBackLifeCyle
    {
        void onStartDialog(UploadPost uploadPost);
        void onPostFailedUpload(UploadPost uploadPost);
        void onPostUploading(String PID);
        void onPostUploadSuccess(UploadPost uploadPost, long PPID);
    }

    private DialogShowPictureUploading dialogShowPictureUploading = null;

    private final FriendStatusListener friendStatusListenerRemoveFriendForBlockedOrSomenthing = new FriendStatusListener()
    {
        @Override
        public void onStatusReceived(long UID_CHAT_PARTNER, short Status)
        {
            setAnfragenCounter();
            if(Status == ServerPolicy.POLICY_DETAIL_CASE_NOTHING
                    || Status == ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE
                    || Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED)
            {
                adapterChats.removeChatPartner(UID_CHAT_PARTNER);
            }
            else
            {
                SQLChats sqlChats = new SQLChats(getContext());
                adapterChats.updateChatPartner(sqlChats.getSingleChatPartner(UID_CHAT_PARTNER));
                sqlChats.close();
            }
        }

        @Override
        public void onStatusFailed(long UID_CHAT_PARTNER)
        {
            Context context = getContext();
            if(context != null)
            {
                AlertDialog.Builder alter = new AlertDialog.Builder(getContext());
                alter.setTitle(getContext().getResources().getString(R.string.txt_removeFriend_UserTitle));
                alter.setMessage(getContext().getResources().getString(R.string.txt_alertPinFailedToDownloadDetails));
                alter.show();
            }
        }
    };

    public void loadMoreChatPartners()
    {
        if(!searchingActivated)
        {
            new Thread(new LoadAllKontaktsFromDb()).start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        setAnfragenCounter();

        if(data != null)
        {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();

                    final Constraints constraints = new Constraints.Builder()
                            .setRequiresCharging(false)
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    Data dataBuilder = new Data.Builder()
                            .putString(UploadNewProfilbild.paramFilePath, resultUri.toString())
                            .build();

                    OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(UploadNewProfilbild.class)
                            .setInputData(dataBuilder)
                            .setConstraints(constraints)
                            .build();

                    esaphCircleImageViewProfilbild.setAlpha(0.5f);

                    WorkManager.getInstance().getWorkInfoByIdLiveData(simpleRequest.getId())
                            .observe(this, new Observer<WorkInfo>()
                            {
                                @Override
                                public void onChanged(@Nullable WorkInfo workInfo)
                                {
                                    if(workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED)
                                    {
                                        if(isAdded() && esaphCircleImageViewProfilbild != null)
                                        {
                                            esaphCircleImageViewProfilbild.setAlpha(1.0f);
                                            esaphCircleImageViewProfilbild.setProgress(0);

                                            EsaphGlobalProfilbildLoader.with(getContext()).invalidateCaches();
                                            EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(esaphCircleImageViewProfilbild,
                                                    null,
                                                    SpotLightLoginSessionHandler.getLoggedUID(),
                                                    EsaphImageLoaderDisplayingAnimation.BLINK,
                                                    R.drawable.ic_no_image_circle,
                                                    StorageHandlerProfilbild.FOLDER_PROFILBILD);
                                        }
                                    }
                                }
                            });

                    WorkManager.getInstance().beginUniqueWork(ChatsFragment.WORK_PB_ID,
                            ExistingWorkPolicy.REPLACE,
                            simpleRequest).enqueue();
                }
            }
            else if(requestCode == PrivateChat.REQUEST_CODE_FINISHED)
            {
                Bundle bundle = data.getExtras();
                if(adapterChats != null && bundle != null)
                {
                    ChatPartner chatPartner = (ChatPartner) bundle.getSerializable(PrivateChat.extraChatPartner);
                    if(chatPartner != null)
                    {
                        SQLChats sqlChats = new SQLChats(getContext());
                        adapterChats.updateChatPartner(sqlChats.getSingleChatPartner(chatPartner.getUID_CHATPARTNER()));
                        sqlChats.close();

                        adapterChats.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void setNoCurrentChats()
    {
        viewNoChats.setVisibility(View.VISIBLE);
    }

    private void disableNoCurrentChats()
    {
        viewNoChats.setVisibility(View.GONE);
    }


    private static final Object lock = new Object();
    private static boolean ALLOW_LOADING_CONTACTS = true;
    private class LoadAllKontaktsFromDb implements Runnable
    {
        private LoadAllKontaktsFromDb()
        {
        }

        @Override
        public void run()
        {
            synchronized (ChatsFragment.lock)
            {
                try
                {
                    SQLChats chats = new SQLChats(getContext());
                    SQLUploads sqlUploads = new SQLUploads(getContext());

                    final List<Object> list = new ArrayList<>();
                    list.addAll(chats.getAktuelleChats(adapterChats.getCountChatPartners()));
                    // TODO: 09.03.2020 remove this when working, need server host
                    list.add(new ChatPartner("Vanessa","Vanessa", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],1,null, false));
                    list.add(new ChatPartner("Tim_03","Tim", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],2,null, false));
                    list.add(new ChatPartner("trimfactory","Sebastian", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],3,null, false));
                    list.add(new ChatPartner("exTrop","Julian", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],4,null, false));
                    list.add(new ChatPartner("aida","AIDA", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],5,null, false));
                    list.add(new ChatPartner("happy200","Tristan", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],6,null, false));
                    list.add(new ChatPartner("jafa06","Jana", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],7,null, false));
                    list.add(new ChatPartner("Vivien","Vivien", getResources().getStringArray(R.array.SPOT_TEMPLATES)[5],8,null, false));
                    list.addAll(sqlUploads.getPostsThatNotBeenUploadedYet(adapterChats.getCountUploadPosts()));
                    chats.close();
                    sqlUploads.close();

                    if(list.isEmpty() && ALLOW_LOADING_CONTACTS)
                    {
                        list.addAll(getContactList(Integer.MAX_VALUE, false, new Random().nextInt(3) + 1));
                        ALLOW_LOADING_CONTACTS = false;
                    }
                    else
                    {
                        //list.addAll(getContactList(5, true, new Random().nextInt(2) + 1));
                    }

                    Activity activity = getActivity();

                    if(activity != null)
                    {
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(isAdded())
                                {
                                    if(!list.isEmpty())
                                    {
                                        disableNoCurrentChats();
                                        adapterChats.pushFriendsInList(list);
                                    }
                                    else
                                    {
                                        if(adapterChats.getCount() <= 0)
                                        {
                                            setNoCurrentChats();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "LoadAllKontaktsFromDb() failed: " + ec);
                }
            }
        }
    }

    private final MomentSynchCheckListener momentSynchCheckListenerAktuelleMomente = new MomentSynchCheckListener()
    {
        @Override
        public void onMomentsUpdated()
        {
            Log.i(getClass().getName(), "On Moments updated.");
            adapterChats.clearAll();
            loadMoreChatPartners();
        }

        @Override
        public void onMomentsOk()
        {
            Log.i(getClass().getName(), "On Moments OK.");
        }

        @Override
        public void onMomentsSynchFailed()
        {
            Log.i(getClass().getName(), "On Moments update failed.");
            if(getActivity() != null)
            {
                final AlertDialog d = new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.txt__alertLogIn))
                        .setPositiveButton(getResources().getString(R.string.txt_alertDbPositiveButtonTryAgain), null) //Set to null. We override the onclick
                        .setNegativeButton(getResources().getString(R.string.txt_alertCountryDbDetailsCancel), null)
                        .setMessage(getResources().getString(R.string.txt_alertLogInFriendSyncFailed)).create();

                d.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {
                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View view)
                            {
                                Context context = getContext();
                                if(context != null)
                                {
                                    CLPreferences preferences = new CLPreferences(context);
                                    if(!preferences.isAccountSynchronized())
                                    {
                                        new AsyncSynchroniseAktuelleChatsAndMoments(getContext(), momentSynchCheckListenerAktuelleMomente).execute();
                                    }
                                }
                                d.dismiss();
                            }
                        });

                        Button n = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                        n.setOnClickListener(new View.OnClickListener()
                        {

                            @Override
                            public void onClick(final View view)
                            {
                                d.dismiss();
                                getActivity().finish();
                            }
                        });
                    }
                });

                d.setCanceledOnTouchOutside(true); // TODO: 09.03.2020 change this to false
                d.setCancelable(true);
                d.show();
            }
        }
    };

    private BroadcastReceiver mainBroadCastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && intent.getAction() != null)
            {
                if(intent.getAction().equals(MessageHandler.actionBroadCastUserLoaded))
                {
                    SQLFriends sqlWatcher = new SQLFriends(context);
                    final long FUID = intent.getLongExtra(MessageHandler.extraLongUidLoadedForLoaded, -1);

                    SQLChats sqlChats = new SQLChats(getContext());
                    ChatPartner chatPartner = sqlChats.getSingleChatPartner(FUID);
                    sqlChats.close();
                    sqlWatcher.close();

                    adapterChats.updateChatPartner(chatPartner);
                }
                else if(intent.getAction().equals(MessageHandler.ACTION_FRIEND_UPDATE))
                {
                    setAnfragenCounter();
                }
            }
        }
    };


    @Override
    public void onStart()
    {
        if(!checkPermissionsWithoutRequestDialog())
        {
            requestPerm();
        }

        if(adapterChats != null)
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MessageHandler.actionBroadCastUserLoaded);
            intentFilter.addAction(MessageHandler.ACTION_FRIEND_UPDATE);

            Activity activity = getActivity();
            if(activity != null)
            {
                activity.registerReceiver(this.mainBroadCastReceiver, intentFilter);
            }

            ServiceConnection serviceConnection = adapterChats.getConnectionUploadService();
            if(serviceConnection != null && getActivity() != null)
            {
                Intent intent = new Intent(getContext(), UploadService.class);
                getActivity().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            }

            ServiceConnection serviceConnectionSendingText = adapterChats.getServiceConnectionSendingText();
            if(serviceConnectionSendingText != null)
            {
                Intent intentSending = new Intent(getContext(), MsgServiceConnection.class);
                getActivity().bindService(intentSending, serviceConnectionSendingText, BIND_AUTO_CREATE);
            }
        }
        super.onStart();
    }

    @Override
    public void onStop()
    {
        if(adapterChats != null)
        {
            Activity activity = getActivity();

            if(activity != null)
            {
                try
                {
                    activity.unregisterReceiver(this.mainBroadCastReceiver);
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "onStop(Chats) failed to unregister receiver: " + ec);
                }

                try
                {
                    ServiceConnection serviceConnection = adapterChats.getConnectionUploadService();
                    if(serviceConnection != null && adapterChats.isUploadServiceBound())
                    {
                        adapterChats.getUploadService().removeUploadServiceCallbacks(adapterChats);
                        activity.unbindService(serviceConnection);
                    }

                    ServiceConnection serviceConnectionSendingText = adapterChats.getServiceConnectionSendingText();
                    if(serviceConnectionSendingText != null && adapterChats.isBoundSendingConnection())
                    {
                        adapterChats.getMessageService().removeMsgServiceCallback(adapterChats);
                        activity.unbindService(serviceConnectionSendingText);
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "onStop() chatsFragmentServiceConnnections failed: " + ec);
                }
            }
        }
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getContext(), getResources().getString(R.string.ADS_APP_ID));

        Activity activity = getActivity();
        if(activity != null)
        {
            CLPreferences preferences = new CLPreferences(activity.getApplicationContext());
            if(!preferences.isAccountSynchronized())
            {
                new AsyncSynchroniseAktuelleChatsAndMoments(getContext(), this.momentSynchCheckListenerAktuelleMomente).execute();
            }
        }
    }

    private List<PhoneContact> getContactList(int max, boolean randomize, int randomCanAdd)
    {
        if(checkPermissionsWithoutRequestDialog())
        {
            List<PhoneContact> contactsList = new ArrayList<>();
            try
            {
                int countDown = max;
                Context context = getContext();
                ContentResolver cr = null;
                if(context != null)
                {
                    cr = context.getContentResolver();
                }

                if(cr != null)
                {
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            ContactsContract.Contacts.TIMES_CONTACTED);

                    if ((cur != null ? cur.getCount() : 0) > 0)
                    {
                        if(randomize)
                        {
                            int randomStartIndex = new Random().nextInt(cur.getCount()-5);
                            if(cur.getCount() != 0 && randomStartIndex >= 0)
                            {
                                cur.moveToPosition(randomStartIndex);
                            }
                        }

                        while (cur.moveToNext())
                        {
                            countDown--;
                            if(countDown <= 0)
                            {
                                break;
                            }

                            String id = cur.getString(
                                    cur.getColumnIndex(ContactsContract.Contacts._ID));

                            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));

                            String name = cur.getString(cur.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));


                            randomCanAdd--;
                            if(randomCanAdd > 0)
                            {
                                contactsList.add(new PhoneContact(name, true, uri));
                            }
                            else
                            {
                                contactsList.add(new PhoneContact(name, false, uri));
                            }
                        }
                    }

                    if(cur!=null)
                    {
                        cur.close();
                    }
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "getContactList() failed: " + ec);
            }
            return contactsList;
        }

        return new ArrayList<>();
    }



    private boolean checkPermissionsWithoutRequestDialog()
    {
        Activity activity = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null)
        {
            return activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }
        else
        {
            return true;
        }
    }

    private void requestPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]
                    {
                            android.Manifest.permission.READ_CONTACTS,
                    }, ChatsFragment.PERMISSION_REQUESTCODE);
        }
    }

    private static final int PERMISSION_REQUESTCODE = 320;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean granted = false;
        if(grantResults.length > 0)
        {
            granted = true;
        }

        for (int result : grantResults) {
            if(result == PackageManager.PERMISSION_DENIED)
            {
                granted = false;
                break;
            }
        }

        if(granted)
        {
            adapterChats.clearAll();
            loadMoreChatPartners();
        }
    }

    private final TextWatcher textWatcherMainInput = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            adapterChats.getFilter().filter(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };

    public void setProfilbild()
    {
        Context context = getContext();
        if(context != null && isAdded())
        {
            EsaphGlobalProfilbildLoader.with(getContext()).displayProfilbild(esaphCircleImageViewProfilbild,
                    null,
                    SpotLightLoginSessionHandler.getLoggedUID(),
                    EsaphImageLoaderDisplayingAnimation.BLINK,
                    R.drawable.ic_no_image_circle,
                    StorageHandlerProfilbild.FOLDER_PROFILBILD);
        }
    }

    public synchronized void setAnfragenCounter()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int count = 0;
                SQLFriends sqlFriends = null;
                try
                {
                    sqlFriends = new SQLFriends(getActivity());
                    count = sqlFriends.getNumberOfNewFriendAnfragen();
                    sqlFriends.close();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "setAnfragenCounter_runnable() failed: " + ec);
                }
                finally
                {
                    if(sqlFriends != null)
                    {
                        sqlFriends.close();
                    }

                    Activity activity = getActivity();
                    if(activity != null)
                    {
                        final int schrottCount = count;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                if(isAdded() && textViewAnfragenCount != null)
                                {
                                    if(schrottCount > 0)
                                    {
                                        String result = "" + schrottCount;
                                        textViewAnfragenCount.setText(result);
                                        textViewAnfragenCount.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        textViewAnfragenCount.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }


    private static final String WORK_PB_ID = "WORK:PROFIL";
    public void stopSearching()
    {
        if(!searchingActivated) return;
        if(!isVisible()) return;
        closeKeyBoard();
        searchingActivated = false;
    }

    private boolean searchingActivated = false;


    private void closeKeyBoard()
    {
        Activity activity = getActivity();
        if (editTextSearchChats != null && activity != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
            {
                imm.hideSoftInputFromWindow(editTextSearchChats.getWindowToken(), 0);
            }
            editTextSearchChats.clearFocus();
        }
    }


    private DialogChooseOperationLongClickUser dialogCurrentShowing = null;
    public void onFriendUpdate(short FRIEND_STATUS, ChatPartner chatPartner)
    {
        if(!isAdded()) return;


        friendStatusListenerRemoveFriendForBlockedOrSomenthing.onStatusReceived(chatPartner.getUID_CHATPARTNER(), FRIEND_STATUS);
        if(dialogCurrentShowing != null && dialogCurrentShowing.isShowing())
        {
            dialogCurrentShowing.onFriendUpdate(FRIEND_STATUS, chatPartner);
        }
    }

    //AD Handling.
    private InterstitialAd mInterstitialAd;
    private void loadNewAd()
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            mInterstitialAd = new InterstitialAd(activity);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.AD_INTERSTIAL_CHAT));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mainBroadCastReceiver = null;
        listViewChats.setOnScrollListener(null);
        listViewChats = null;
    }
}
