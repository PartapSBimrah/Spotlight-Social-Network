package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.LifeCloudShowAllBigView;

import android.app.Activity;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import esaph.spotlight.Esaph.EsaphDialogBubbly.EsaphDialog;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.ILoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncDeletePostFromLifeCloud;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.LifeCloudBigViewMaster.LifeCloudBigViewMasterClass;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.DialogDeletePictureOrVideo;

public class LifeCloudShowAllBigView extends LifeCloudBigViewMasterClass
{
    public LifeCloudShowAllBigView()
    {
        // Required empty public constructor
    }

    @Override
    public void initShowView(View rootView)
    {
        TextView textViewDeletePost = (TextView) rootView.findViewById(R.id.textViewDelete);

        textViewDeletePost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                

                Object object = getLifeCloudBigViewAdapter().getItemFromList(getViewPager().getCurrentItem());
                if(object instanceof LifeCloudUpload)
                {
                    final LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;

                    Activity activity = getActivity();
                    if(activity != null && getView() != null)
                    {
                        final DialogDeletePictureOrVideo dialogDeletePictureOrVideo = new DialogDeletePictureOrVideo(activity, (ViewGroup) getView().getRootView(), lifeCloudUpload);
                        TextView textViewConfirm = (TextView) dialogDeletePictureOrVideo.findViewById(R.id.textViewDeletePicDialogReally);
                        textViewConfirm.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                new AsyncDeletePostFromLifeCloud(getContext(), new AsyncDeletePostFromLifeCloud.LifeCloudDeleteListener()
                                {
                                    @Override
                                    public void onDeletedPost(LifeCloudUpload lifeCloudUpload)
                                    {

                                    }

                                    @Override
                                    public void onFailedDeletingPost(LifeCloudUpload lifeCloudUpload)
                                    {
                                        EsaphDialog esaphDialog = new EsaphDialog(getContext(), getResources().getString(R.string.txt_ups),
                                                getResources().getString(R.string.txt_alertMomentFailedToDeleteTitleDetails));
                                        esaphDialog.show();
                                    }
                                }, lifeCloudUpload).execute();

                                dialogDeletePictureOrVideo.dismiss();
                            }
                        });
                        dialogDeletePictureOrVideo.show();
                    }
                }

            }
        });
    }



    private static EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment;
    private static List<Object> list;
    private static int positionClicked = 0;

    public static LifeCloudShowAllBigView getInstance(EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment,
                                                      int positionClicked,
                                                      List<Object> list)
    {
        LifeCloudShowAllBigView.esaphGlobalCommunicationFragment = esaphGlobalCommunicationFragment;
        LifeCloudShowAllBigView.list = list;
        LifeCloudShowAllBigView.positionClicked = positionClicked;
        return new LifeCloudShowAllBigView();
    }

    @Override
    public List<Object> extendedGetList()
    {
        return LifeCloudShowAllBigView.list;
    }

    @Override
    public int extendedGetPositionClicked()
    {
        return LifeCloudShowAllBigView.positionClicked;
    }

    @Override
    public Runnable extendingFragmentStartLoadingMore(ViewPager viewPager, ViewPagerAdapterGetList viewPagerAdapterGetList, ViewPager.OnPageChangeListener onPageChangeListener)
    {
        //Return the runnable which should load the data types.
        if(esaphGlobalCommunicationFragment instanceof ILoader)
        {
            return ((ILoader)esaphGlobalCommunicationFragment).getLoadingTask();
        }

        return new Runnable()
        {
            @Override
            public void run()
            {
            }
        };
    }
}
