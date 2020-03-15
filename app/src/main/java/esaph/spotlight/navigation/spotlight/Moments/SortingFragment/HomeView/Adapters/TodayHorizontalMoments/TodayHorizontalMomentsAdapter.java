package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphDays;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphHours;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphMinutes;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.AsyncSaveAllPostFromToday;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Adapters.TodayHorizontalMoments.Model.TodayMomentsUser;

public class TodayHorizontalMomentsAdapter extends EsaphMomentsRecylerView
{
    private ExecutorService threadPoolSaveAllTodayStorys = Executors.newFixedThreadPool(2);
    private List<Object> listDisplay;
    private LayoutInflater inflater;
    private Context context;
    private TodayVerticalMomentsAdapterOnClickListener todayVerticalMomentsAdapterOnClickListener;
    private Bitmap fakeBitmap;


    public TodayHorizontalMomentsAdapter(Context context,
                                         TodayVerticalMomentsAdapterOnClickListener todayVerticalMomentsAdapterOnClickListener,
                                         WeakReference<View>[] views) {
        super(views);
        this.fakeBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.todayVerticalMomentsAdapterOnClickListener = todayVerticalMomentsAdapterOnClickListener;
        this.listDisplay = new ArrayList<>();
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        super.notifyDataSetChangeBypass();
    }


    @Override
    public void notifyOnDataChangeInViewpager() {
        notifyDataSetChangeBypass();
    }

    public interface TodayVerticalMomentsAdapterOnClickListener
    {
        void onClick(int pos);
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public List<Object> getListDataDisplay()
    {
        return listDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data) {
        listDisplay.addAll(data);
    }

    @Override
    public void clearAllWithNotify()
    {
    }

    @Override
    public void removeSinglePostByPID(String PID)
    {
    }


    @Override
    public int[] getObjectCountsThreadSafe() {
        return new int[0];
    }

    @Override
    public boolean isEmpty() {
        return listDisplay.isEmpty();
    }

    @Override
    public void addFooter() {

    }

    @Override
    public void removeFooter() {

    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = this.listDisplay.get(position);

        if(object instanceof TodayMomentsUser)
        {
            return 0;
        }
        else if(object instanceof LifeCloudUpload)
        {
            return 1;
        }

        return 0;
    }

    private static class ViewHolderPartnerStory extends RecyclerView.ViewHolder
    {
        private RoundedImageView roundedImageView;
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private TextView textViewUsername;
        private TextView textViewTime;
        private TextView textViewSaveAll;

        public ViewHolderPartnerStory(View view)
        {
            super(view);
            this.avLoadingIndicatorView = view.findViewById(R.id.avIndicationLoading);
            this.roundedImageView = view.findViewById(R.id.imageViewLastPic);
            this.textViewUsername = view.findViewById(R.id.textViewUsername);
            this.textViewTime = view.findViewById(R.id.textViewLastActionTime);
            this.textViewSaveAll = view.findViewById(R.id.textViewSaveAll);
        }
    }

    private static class ViewHolderLifeCloud extends RecyclerView.ViewHolder
    {
        private RoundedImageView imageView;
        private TextView textViewTime;
        private TextView textViewLifeCloudText;

        public ViewHolderLifeCloud(View view)
        {
            super(view);
            this.imageView = view.findViewById(R.id.imageViewLastPic);
            this.textViewTime = view.findViewById(R.id.textViewLastActionTime);
            this.textViewLifeCloudText = view.findViewById(R.id.textViewLifeCloudText);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewItemConversation = inflater.inflate(R.layout.layout_partner_today_pics, parent, false);
                viewHolder = new ViewHolderPartnerStory(viewItemConversation);
                break;

            case 1:
                View viewItemConversationOwn = inflater.inflate(R.layout.layout_lifecloud_today_pics_own, parent, false);
                viewHolder = new ViewHolderLifeCloud(viewItemConversationOwn);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        switch (getItemViewType(position))
        {
            case 0:
                final ViewHolderPartnerStory viewHolderMain = (ViewHolderPartnerStory) holder;
                final TodayMomentsUser todayMomentsUser = (TodayMomentsUser) listDisplay.get(position);
                viewHolderMain.roundedImageView.setImageBitmap(fakeBitmap);
                viewHolderMain.textViewUsername.setText(todayMomentsUser.getPartner_Username());

                viewHolderMain.roundedImageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Object object = listDisplay.get(viewHolderMain.getAdapterPosition());
                        if(object instanceof TodayMomentsUser)
                        {
                            if(((TodayMomentsUser)object).getLastConversationMessage() != null)
                            {
                                todayVerticalMomentsAdapterOnClickListener.onClick(viewHolderMain.getAdapterPosition());
                            }
                        }
                    }
                });

                viewHolderMain.textViewSaveAll.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!todayMomentsUser.isLocked())
                        {
                            todayMomentsUser.setLocked(true);
                            threadPoolSaveAllTodayStorys.submit(new AsyncSaveAllPostFromToday((EsaphActivity) context,
                                    todayMomentsUser,
                                    TodayHorizontalMomentsAdapter.this,
                                    viewHolderMain.roundedImageView,
                                    viewHolderMain.avLoadingIndicatorView));
                        }
                    }
                });

                if(todayMomentsUser.getLastConversationMessage() != null)
                {
                    viewHolderMain.roundedImageView.setVisibility(View.VISIBLE);

                    viewHolderMain.textViewTime.setText(getDateDiffOnlyHoursAndMinutes(context,
                            todayMomentsUser.getLastConversationMessage().getMessageTime(),
                            System.currentTimeMillis()));

                    if(!todayMomentsUser.hasSavedAll()) //Not all where saved.
                    {
                        viewHolderMain.textViewSaveAll.setText(context.getResources().getString(R.string.txt_save_all));
                        viewHolderMain.textViewSaveAll.setClickable(true);
                        viewHolderMain.textViewSaveAll.setFocusable(true);
                        viewHolderMain.textViewSaveAll.setVisibility(View.VISIBLE);
                        viewHolderMain.textViewSaveAll.setTypeface(viewHolderMain.textViewSaveAll.getTypeface(), Typeface.BOLD);

                    }
                    else //All images were already saved.
                    {
                        viewHolderMain.textViewSaveAll.setTypeface(viewHolderMain.textViewSaveAll.getTypeface(), Typeface.NORMAL);
                        viewHolderMain.textViewSaveAll.setVisibility(View.GONE);
                        viewHolderMain.textViewSaveAll.setText(context.getResources().getString(R.string.txt_nothing_new));
                        viewHolderMain.textViewSaveAll.setClickable(false);
                        viewHolderMain.textViewSaveAll.setFocusable(false);
                    }

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            todayMomentsUser.getLastConversationMessage().getIMAGE_ID(),
                            viewHolderMain.roundedImageView,
                            null,
                            new EsaphDimension(viewHolderMain.roundedImageView.getWidth(),
                                    viewHolderMain.roundedImageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle));
                }
                else
                {
                    viewHolderMain.textViewTime.setText("");
                    viewHolderMain.textViewSaveAll.setTypeface(viewHolderMain.textViewSaveAll.getTypeface(), Typeface.NORMAL);
                    viewHolderMain.textViewSaveAll.setVisibility(View.GONE);
                    viewHolderMain.textViewSaveAll.setText(context.getResources().getString(R.string.txt_nothing_new));
                    viewHolderMain.textViewSaveAll.setClickable(false);
                    viewHolderMain.textViewSaveAll.setFocusable(false);

                    Glide.with(context).load(R.drawable.ic_no_image_no_round).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderMain.roundedImageView.setImageDrawable(resource);
                            }
                        }
                    });
                }

                break;

            case 1:
                final ViewHolderLifeCloud viewHolderMainOwn = (ViewHolderLifeCloud) holder;
                final LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) listDisplay.get(position);

                viewHolderMainOwn.imageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        todayVerticalMomentsAdapterOnClickListener.onClick(viewHolderMainOwn.getAdapterPosition());
                    }
                });

                if(!lifeCloudUpload.isEmptyCloudToday()) //LifeCloud data in.
                {
                    viewHolderMainOwn.textViewTime.setText(getDateDiffOnlyHoursAndMinutes(context,
                            lifeCloudUpload.getCLOUD_TIME_UPLOADED(),
                            System.currentTimeMillis()));

                    if(lifeCloudUpload.getCLOUD_MESSAGE_STATUS() == LifeCloudUpload.LifeCloudStatus.STATE_UPLOADED)
                    {
                        viewHolderMainOwn.textViewLifeCloudText.setClickable(true);
                        viewHolderMainOwn.textViewLifeCloudText.setFocusable(true);
                    }
                    else
                    {
                        System.out.println("NOT UPLOADED SO NO DISPLAY");

                        //What tha heelll???!
                        //Latest post not uploaded.
                    }

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            lifeCloudUpload.getCLOUD_PID(),
                            viewHolderMainOwn.imageView,
                            null,
                            new EsaphDimension(viewHolderMainOwn.imageView.getWidth(),
                                    viewHolderMainOwn.imageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle));
                }

                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size();
    }

    private String getDateDiffOnlyHoursAndMinutes(Context context, long date1, long date2)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Date dateTime = new Date(date1);
        Date dateTime2 = new Date(date2);

        long hours = EsaphHours.hoursBetween(dateTime, dateTime2);
        long minutes = EsaphMinutes.minutesBetween(dateTime, dateTime2);
        long days = EsaphDays.daysBetween(dateTime, dateTime2);

        if(hours <= 0 && minutes <= 0)
        {
            stringBuilder.append(context.getResources().getString(R.string.txt_gerade));
            return stringBuilder.toString();
        }

        if(hours > 0)
        {
            if(minutes > 0)
            {
                stringBuilder.append(context.getResources().getString(R.string.txt_sHoursSingle, hours));
            }
            else
            {
                stringBuilder.append(context.getResources().getString(R.string.txt_sHoursWithMinutes, hours, minutes));
            }
        }
        else
        {
            if(minutes > 0)
            {
                stringBuilder.append(context.getResources().getString(R.string.txt_sMinutes, minutes));
            }
        }

        return stringBuilder.toString();
    }

}
