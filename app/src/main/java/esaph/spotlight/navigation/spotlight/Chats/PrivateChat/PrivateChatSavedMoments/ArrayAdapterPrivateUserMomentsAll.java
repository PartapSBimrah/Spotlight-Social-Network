package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
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
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatImage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatVideo;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MomentPostClickListener;

public class ArrayAdapterPrivateUserMomentsAll extends EsaphMomentsRecylerView
{
    private MomentPostClickListener momentPostClickListener;
    private ShowUserMomentsPrivate showUserMomentsPrivate;
    private LayoutInflater inflater;
    private List<Object> listDataDisplay;
    private List<Object> listFooter;
    private View viewNoData;

    private List<Object> listDataOriginal;

    private Context context;
    private SparseArrayCompat<String> sparseArrayCompatSelected = new SparseArrayCompat<String>();
    private int imageCount = 0;

    public ArrayAdapterPrivateUserMomentsAll(Context context,
                                             View viewNoData,
                                             ShowUserMomentsPrivate showUserMomentsPrivate,
                                             MomentPostClickListener momentPostClickListener)
    {
        super(new WeakReference[]{});
        this.viewNoData = viewNoData;
        this.showUserMomentsPrivate = showUserMomentsPrivate;
        this.context = context;
        this.listDataDisplay = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.momentPostClickListener = momentPostClickListener;
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

        if(listDataDisplay.isEmpty())
        {
            viewNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            viewNoData.setVisibility(View.GONE);
        }
    }


    public void clearAll()
    {
        this.listDataDisplay.clear();
        this.listFooter.clear();
        this.imageCount = 0;
        notifyDataSetChangeBypass();
    }

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


    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        this.listFooter.clear();
        this.imageCount = 0;
        notifyDataSetChangeBypass();
    }

    @Override
    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(listDataDisplay.size()+listFooter.size()-1);
        }
    }

    @Override
    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(listDataDisplay.size()+listFooter.size()+1);
    }

    public List<Object> getListDataDisplay()
    {
        return listDataDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        if(showUserMomentsPrivate.isSearching()) return;

        listDataDisplay.addAll(data);
        listDataOriginal = new ArrayList<>(listDataDisplay);
    }

    private int getPositionOfItem(String PID)
    {
        for(int counter = 0; counter < sparseArrayCompatSelected.size(); counter++)
        {
            int key = sparseArrayCompatSelected.keyAt(counter);
            if(PID.equals(sparseArrayCompatSelected.get(key)))
            {
                return key;
            }
        }
        return -1;
    }

    public int getCount()
    {
        return this.listDataDisplay.size();
    }

    private boolean findMatchingHashtags(List<EsaphHashtag> esaphHashtagList, String toSearch)
    {
        int size = esaphHashtagList.size();
        for(int counter = 0; counter < size; counter++)
        {
            EsaphHashtag esaphHashtag = esaphHashtagList.get(counter);
            if(esaphHashtag.getHashtagName().toLowerCase().startsWith(toSearch))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results)
            {
                listDataDisplay = (ArrayList<Object>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Object> FilteredArrList = new ArrayList<Object>();

                if (listDataOriginal == null)
                {
                    listDataOriginal = new ArrayList<Object>(listDataDisplay); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0)
                {
                    // set the Original result to return
                    results.count = listDataOriginal.size();
                    results.values = listDataOriginal;
                }
                else
                {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listDataOriginal.size(); i++)
                    {
                        Object object = listDataOriginal.get(i);
                        if(object instanceof ConversationMessage)
                        {
                            ConversationMessage conversationMessage = (ConversationMessage) object;

                            ConversationMessage conNewInstance = null;
                            if(conversationMessage instanceof ChatImage)
                            {
                                ChatImage chatImage = (ChatImage) conversationMessage;
                                if (chatImage.getAbsender().toLowerCase().startsWith(constraint.toString()))
                                {
                                    conNewInstance = new ChatImage(
                                            chatImage.getSERVER_ID(),
                                            chatImage.getMESSAGE_ID(),
                                            chatImage.getABS_ID(),
                                            chatImage.getID_CHAT(),
                                            chatImage.getMessageTime(),
                                            chatImage.getMessageStatus(),
                                            chatImage.getBeschreibung(),
                                            chatImage.getIMAGE_ID(),
                                            chatImage.getAbsender());

                                    FilteredArrList.add(conNewInstance);
                                }
                            }
                            else if(conversationMessage instanceof ChatVideo)
                            {
                                ChatVideo chatVideo = (ChatVideo) conversationMessage;

                                if (chatVideo.getAbsender().toLowerCase().startsWith(constraint.toString()))
                                {
                                    conNewInstance = new ChatVideo(
                                            chatVideo.getSERVER_ID(),
                                            chatVideo.getMESSAGE_ID(),
                                            chatVideo.getABS_ID(),
                                            chatVideo.getID_CHAT(),
                                            chatVideo.getMessageTime(),
                                            chatVideo.getMessageStatus(),
                                            chatVideo.getBeschreibung(),
                                            chatVideo.getIMAGE_ID(),
                                            chatVideo.getAbsender());

                                    FilteredArrList.add(conNewInstance);
                                }
                            }
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    @Override
    public void notifyOnDataChangeInViewpager()
    {
        notifyDataSetChangeBypass();
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

    private static class ViewHolderFooter extends RecyclerView.ViewHolder
    {
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private ViewHolderFooter(View view)
        {
            super(view);
            avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.footerView);
        }
    }

    private static class ViewHolderDatum extends RecyclerView.ViewHolder
    {
        private TextView textViewDatum;

        private ViewHolderDatum(View view)
        {
            super(view);
            this.textViewDatum = (TextView) view.findViewById(R.id.textViewMemoryDatum);
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return listDataDisplay.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.listDataDisplay.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewPost = inflater.inflate(R.layout.display_image_raw_layout, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewPost);
                break;

            case 1:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
                break;

            case 2:
                View viewBigImage = inflater.inflate(R.layout.layout_user_memorys_first_pic_big, parent, false);
                viewHolder = new ViewHolderMemoryPost(viewBigImage);
                break;

            case 3:
                View viewFooter = inflater.inflate(R.layout.footer_layout_private, parent, false);
                viewHolder = new ViewHolderFooter(viewFooter);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Object object;
        if(position >= listDataDisplay.size())
        {
            object = null;
        }
        else
        {
            object = this.listDataDisplay.get(position);
        }

        switch (getItemViewTypePerformence(object, position))
        {
            case 0:
                final ViewHolderMemoryPost viewHolderMemoryPost = (ViewHolderMemoryPost) holder;
                final ConversationMessage conversationMessage = (ConversationMessage) object;
                viewHolderMemoryPost.PostPicture.setTag(conversationMessage.getIMAGE_ID());

                viewHolderMemoryPost.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        momentPostClickListener.onItemClickGridView(v, viewHolderMemoryPost.getLayoutPosition());
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
                        R.drawable.ic_no_image_no_round));
                break;


            case 1:
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                final DatumList datumHolder = (DatumList) object;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
                break;


            case 2:
                final ViewHolderMemoryPost viewHolderMemoryPostBig = (ViewHolderMemoryPost) holder;
                final ConversationMessage memoryPostBig = (ConversationMessage) object;
                viewHolderMemoryPostBig.PostPicture.setTag(memoryPostBig.getIMAGE_ID());

                viewHolderMemoryPostBig.PostPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        
                        momentPostClickListener.onItemClickGridView(v, viewHolderMemoryPostBig.getLayoutPosition());
                    }
                });

                if(memoryPostBig.getType() == (CMTypes.FVID))
                {
                    viewHolderMemoryPostBig.imageViewVideoOrBild.setVisibility(View.VISIBLE);
                    Glide.with(context).load(R.drawable.ic_record).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderMemoryPostBig.imageViewVideoOrBild.setImageDrawable(resource);
                            }
                        }
                    });
                    System.out.println("Video is visible");
                }
                else
                {
                    viewHolderMemoryPostBig.imageViewVideoOrBild.setVisibility(View.GONE);
                    System.out.println("Video is not visible");
                }

                EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                        memoryPostBig.getIMAGE_ID(),
                        viewHolderMemoryPostBig.PostPicture,
                        viewHolderMemoryPostBig.progressBar,
                        new EsaphDimension(viewHolderMemoryPostBig.PostPicture.getWidth(),
                                viewHolderMemoryPostBig.PostPicture.getHeight()),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_no_round));
                break;


            case 3:
                ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;
                viewHolderFooter.avLoadingIndicatorView.smoothToShow();
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            Object objectType = listDataDisplay.get(position);
            if(objectType instanceof ConversationMessage) //Unten die memorys
            {
                if(position == 0)
                {
                    return 2;
                }
                else
                {
                    return 0;
                }
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }
        return -1;
    }

    private int getItemViewTypePerformence(Object objectType, int position)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            if(objectType instanceof ConversationMessage) //Unten die memorys
            {
                if(position == 0)
                {
                    return 2;
                }
                else
                {
                    return 0;
                }
            }
            else if(objectType instanceof DatumList)
            {
                return 1;
            }
        }

        return -1;
    }

}
