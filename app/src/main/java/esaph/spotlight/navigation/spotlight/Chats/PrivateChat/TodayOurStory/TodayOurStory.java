package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.TodayOurStory;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncDeletePrivateMomentPost;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.SpotlightBigViewMaster.SpotLightBigViewMasterClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogDeletePictureOrVideo;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.TodayOurStory.Background.RunnableLoadMoreStoryViewerTodayBetweenPartners;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class TodayOurStory extends SpotLightBigViewMasterClass implements ILoader
{
    public static final String TODAY_OUR_STORY_EXTRA_PARTNER_USERNAME = "esaph.spotlight.todayoutstory.extra.PARTNER_USERNAME";

    public TodayOurStory()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Activity activity = getActivity();
        if(activity != null)
        {
            EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(activity);
        }
    }

    @Override
    public Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, ViewPagerAdapterGetList viewPagerAdapterGetList)
    {
        return getLoadingTask();
    }

    public static TodayOurStory getInstance(long UID)
    {
        Bundle bundle = new Bundle();
        bundle.putLong(TodayOurStory.TODAY_OUR_STORY_EXTRA_PARTNER_USERNAME, UID);
        TodayOurStory todayOurStory = new TodayOurStory();
        todayOurStory.setArguments(bundle);
        return todayOurStory;
    }

    private long UID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            UID = bundle.getLong(TodayOurStory.TODAY_OUR_STORY_EXTRA_PARTNER_USERNAME);
        }
    }

    @Override
    public List<Object> extendedGetList()
    {
        return new ArrayList<>();
    }

    @Override
    public int extendedGetPositionClicked()
    {
        return 0;
    }

    @Override
    public ViewPagerDataSetChangedListener extendedGetListener() {
        return null;
    }

    @Override
    public void initShowView(View rootView)
    {
        if(rootView != null)
        {
            TextView textViewEditPost = (TextView) rootView.findViewById(R.id.textViewEditImage);
            TextView textViewSharePost = (TextView) rootView.findViewById(R.id.textViewSharePost);
            TextView textViewDeletePost = (TextView) rootView.findViewById(R.id.textViewDelete);

            textViewEditPost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });


            textViewSharePost.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                }
            });


            textViewDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v)
                {
                    Object object = getSpotLightViewPagerAdapter().getItemFromList(getViewPager().getCurrentItem());
                    if(object instanceof ConversationMessage)
                    {
                        ConversationMessage conversationMessage = (ConversationMessage) object;
                        if(conversationMessage.getABS_ID() == SpotLightLoginSessionHandler.getLoggedUID()) //MEIN BEITRAG, CURRENT USERNAME IST CHAT PARTNER.
                        {
                            deleteMyPost(conversationMessage);
                        }
                    }
                }
            });
        }
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private void deleteMyPost(final ConversationMessage conversationMessage)
    {
        final DialogDeletePictureOrVideo dialogDeletePictureOrVideo = new DialogDeletePictureOrVideo(
                getActivity(),
                (ViewGroup) getView().getRootView(),
                conversationMessage);

        TextView textViewConfirm = (TextView) dialogDeletePictureOrVideo.findViewById(R.id.textViewDeletePicDialogReally);
        textViewConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                executorService.execute(new AsyncDeletePrivateMomentPost(getContext(),
                        new AsyncDeletePrivateMomentPost.PostDeleteListener() {
                            @Override
                            public void onDeletedSuccess(ConversationMessage conversationMessage)
                            {
                                if(isAdded())
                                {
                                    getSpotLightViewPagerAdapter().removePostByPid(conversationMessage.getMESSAGE_ID());
                                }

                                if(getSpotLightViewPagerAdapter().getObjectsCount()[0] <= 0)
                                {
                                    EsaphActivity esaphActivity = (EsaphActivity) getActivity();
                                    if(esaphActivity != null)
                                    {
                                        esaphActivity.onActivityDispatchBackPressEvent();
                                    }
                                }
                            }

                            @Override
                            public void onFailedDelete(ConversationMessage conversationMessage) {
                                Context context = getContext();
                                if(context != null)
                                {
                                    AlertDialog.Builder alter = new AlertDialog.Builder(getContext());
                                    alter.setTitle(getContext().getResources().getString(R.string.txt_delete));
                                    alter.setMessage(getContext().getResources().getString(R.string.txt_alertMomentFailedToDeleteTitleDetails));
                                    alter.show();
                                }
                            }
                        },
                        conversationMessage));

                dialogDeletePictureOrVideo.dismiss();
            }
        });
        dialogDeletePictureOrVideo.show();
    }

    private Vibrator v = null;
    private void vibrateDeviceTouch()
    {
        Activity activity = getActivity();
        if(v == null && activity != null)
            v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if(v != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                v.vibrate(VibrationEffect.createOneShot(25, 1));
            }
            else {
                //deprecated in API 26
                v.vibrate(25);
            }
        }
    }

    @Override
    public Runnable getLoadingTask()
    {
        return new RunnableLoadMoreStoryViewerTodayBetweenPartners(getActivity(),
                UID,
                getSpotLightViewPagerAdapter(),
                getViewPager(),
                getViewPagerOnPageChangeListener());
    }
}
