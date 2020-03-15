package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowAll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class SeasonRecylerViewAdapterAll extends EsaphMomentsRecylerView
{
    private static SeasonRecylerViewOnClickListener momentsRecylerViewAdapterAllClickListener;
    private LayoutInflater inflater;
    private List<Object> listDataDisplay;
    private Context context;

    public SeasonRecylerViewAdapterAll(Context context,
                                       SeasonRecylerViewOnClickListener momentsRecylerViewAdapterAllClickListener,
                                       WeakReference<View>[] views) {
        super(views);
        this.context = context;
        this.listDataDisplay = new ArrayList<>();
        SeasonRecylerViewAdapterAll.momentsRecylerViewAdapterAllClickListener = momentsRecylerViewAdapterAllClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        imageCount = 0;
        int size = listDataDisplay.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(listDataDisplay.get(counter) instanceof ConversationMessage)
            {
                imageCount++;
            }
        }
        super.notifyDataSetChangeBypass();
    }


    @Override
    public void notifyOnDataChangeInViewpager() {

    }

    public interface SeasonRecylerViewOnClickListener
    {
        void onItemClickNormal(View view, long postTimeOfDay, int position);
    }

    @Override
    public void removeSinglePostByPID(String PID)
    {
        for(int counterIntern = 0; counterIntern < listDataDisplay.size(); counterIntern++)
        {
            Object object = listDataDisplay.get(counterIntern);
            if(object instanceof ConversationMessage)
            {
                if(((ConversationMessage)object).getIMAGE_ID().equals(PID))
                {
                    if(counterIntern - 1 > -1 && listDataDisplay.get(counterIntern-1) instanceof DatumList)
                    {
                        listDataDisplay.remove(counterIntern-1);
                        notifyItemRemoved(counterIntern-1);
                        listDataDisplay.remove(counterIntern-1);
                        this.imageCount--;
                        notifyItemRemoved(counterIntern-1);
                    }
                    else
                    {
                        listDataDisplay.remove(counterIntern);
                        this.imageCount--;
                        notifyItemRemoved(counterIntern);
                    }
                }
            }
        }
    }

    @Override
    public int[] getObjectCountsThreadSafe() {
        return new int[]{imageCount};
    }

    @Override
    public boolean isEmpty() {
        return imageCount <= 0;
    }

    @Override
    public void addFooter() {

    }

    @Override
    public void removeFooter() {

    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data) {
        this.listDataDisplay.addAll(data);
    }


    @Override
    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        imageCount = 0;
        notifyDataSetChangeBypass();
    }

    public void clearAllWithOutNotify()
    {
        this.listDataDisplay.clear();
        imageCount = 0;
    }

    private int imageCount = 0;

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return listDataDisplay.size();
    }

    public Object getItem(int pos)
    {
        return this.listDataDisplay.get(pos);
    }

    private static class ViewHolderMemoryPost extends RecyclerView.ViewHolder
    {
        private ProgressBar progressBar;
        private RelativeLayout relativeLayout;
        private ImageView PostPicture;
        private ImageView imageViewVideoOrBild;

        private ViewHolderMemoryPost(View view)
        {
            super(view);
            this.PostPicture = (ImageView) view.findViewById(R.id.gridViewPrivateMomentMainBildMain);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
            this.imageViewVideoOrBild = (ImageView) view.findViewById(R.id.gridViewPrivateMomentVideoOrBildImageView);
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.display_image_raw_layout_ID);
        }
    }

    private static class ViewHolderDatum extends RecyclerView.ViewHolder
    {
        private TextView textViewDatum;

        private ViewHolderDatum(View view)
        {
            super(view);
            textViewDatum = (TextView) view.findViewById(R.id.textViewMemoryDatum);
        }
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewItemConversation = inflater.inflate(R.layout.display_image_raw_layout, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewItemConversation);
                break;

            case 1:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Object object = this.listDataDisplay.get(position);
        switch (getItemViewTypePerformence(object))
        {
            case 0:
                final ViewHolderMemoryPost viewHolderMemoryPost = (ViewHolderMemoryPost) holder;
                final ConversationMessage conversationMessage = (ConversationMessage) object;

                viewHolderMemoryPost.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        momentsRecylerViewAdapterAllClickListener.onItemClickNormal(v, 0, viewHolderMemoryPost.getAdapterPosition());
                    }
                });

                if(conversationMessage.getType() == (CMTypes.FVID))
                {
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderMemoryPost.imageViewVideoOrBild.setImageDrawable(resource);
                            }
                        }
                    });
                    viewHolderMemoryPost.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                    System.out.println("Video is visible");
                }
                else
                {
                    viewHolderMemoryPost.imageViewVideoOrBild.setVisibility(View.GONE);
                    System.out.println("Video is not visible");
                }

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        conversationMessage.getIMAGE_ID(),
                        viewHolderMemoryPost.PostPicture,
                        viewHolderMemoryPost.progressBar,
                        new EsaphDimension(viewHolderMemoryPost.PostPicture.getWidth(),
                                viewHolderMemoryPost.PostPicture.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_rounded_corners));
                break;

            case 1:
                DatumList datumHolder = (DatumList) object;
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        Object objectType = listDataDisplay.get(position);

        if(objectType instanceof ConversationMessage)
        {
            return 0;
        }
        else if(objectType instanceof DatumList)
        {
            return 1;
        }

        return -1;
    }

    private int getItemViewTypePerformence(Object objectType)
    {
        if(objectType instanceof ConversationMessage)
        {
            return 0;
        }
        else if(objectType instanceof DatumList)
        {
            return 1;
        }

        return -1;
    }

}
