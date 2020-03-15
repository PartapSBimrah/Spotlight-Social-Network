package esaph.spotlight.navigation.spotlight.Moments.AdapterSorting;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;

public class MomentsRecylerViewAdapterHashtag extends EsaphMomentsRecylerView implements Filterable
{
    private List<Object> listFooter;
    private ItemClickListener itemClickListener;
    private LayoutInflater inflater;
    private List<Object> listDataDisplay;
    private List<Object> listDataOriginal;
    private Context context;

    public MomentsRecylerViewAdapterHashtag(Context context,
                                            ItemClickListener itemClickListener,
                                            WeakReference<View>[] views)
    {
        super(views);
        this.context = context;
        this.listDataDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.itemClickListener = itemClickListener;
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
        notifyDataSetChangeBypass();
    }

    public interface ItemClickListener
    {
        void onItemClick(int pos);
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
        if(getCount() == 0)
        {
            this.listDataDisplay.add(new TitleList(context.getResources().getString(R.string.txt_your_hashtags)));
            this.listDataOriginal.add(new TitleList(context.getResources().getString(R.string.txt_your_hashtags)));
        }

        this.listDataDisplay.addAll(data);
        this.listDataOriginal.addAll(data);
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    public int getCount()
    {
        return this.listDataDisplay.size();
    }

    public void clearAllWithNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
        imageCount = 0;
        notifyDataSetChangeBypass();
    }

    public void clearAllWithoutNotify()
    {
        this.listDataDisplay.clear();
        this.listDataOriginal.clear();
        this.listFooter.clear();
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
        return listDataDisplay.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.listDataDisplay.get(pos);
    }

    private static class ViewHolderHashtag extends RecyclerView.ViewHolder
    {
        private View rootView;
        private TextView textViewHashtagName;
        private TextView textViewHashtagAnzahl;
        private ProgressBar progressBar;
        private RoundedImageView roundedImageViewMain;

        private ViewHolderHashtag(View view)
        {
            super(view);
            textViewHashtagAnzahl = (TextView) view.findViewById(R.id.textViewHashtagAnzahl);
            textViewHashtagName = (TextView) view.findViewById(R.id.textViewHashtagNameMemorys);
            rootView = (View) view.findViewById(R.id.rootViewMemorysItemHashtag);
            roundedImageViewMain = (RoundedImageView) view.findViewById(R.id.imageViewMemoryItemCollectionImage);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
        }
    }

    private static class ViewHolderDividerTitle extends RecyclerView.ViewHolder
    {
        private TextView textViewDividerTitle;

        private ViewHolderDividerTitle(View view)
        {
            super(view);
            textViewDividerTitle = (TextView) view.findViewById(R.id.textViewMemoryItemTitlePlaceholder);
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

    private static class ViewHolderFooter extends RecyclerView.ViewHolder
    {
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private ViewHolderFooter(View view)
        {
            super(view);
            avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.footerView);
        }
    }


    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0: //First item listDataDisplay
                View viewFirstItem = inflater.inflate(R.layout.layout_memorys_item_hashtag, parent, false);
                viewHolder = new ViewHolderHashtag(viewFirstItem);
                break;

            case 1: //Memory title placeholder.
                View viewPlaceholder = inflater.inflate(R.layout.layout_memorys_item_memory_placeholder, parent, false);
                viewHolder = new ViewHolderDividerTitle(viewPlaceholder);
                break;

            case 2:
                View viewDatum = inflater.inflate(R.layout.layout_moments_item_datum, parent, false);
                viewHolder = new ViewHolderDatum(viewDatum);
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

        switch (getItemViewTypePerformence(position, object))
        {
            case 0:
                EsaphHashtag esaphHashtag = (EsaphHashtag) object;
                final ViewHolderHashtag viewHolderHashtag = (ViewHolderHashtag) holder;

                viewHolderHashtag.textViewHashtagName.setText(context.getResources().getString(R.string.txt_hashtTagChar, esaphHashtag.getHashtagName()));

                viewHolderHashtag.rootView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        itemClickListener.onItemClick(viewHolderHashtag.getAdapterPosition());
                    }
                });

                String text = "" + esaphHashtag.getHashtagAnzahl();
                Spannable raw = new SpannableString(context.getResources().getString(R.string.txt_insgesamt, text));
                int index = TextUtils.indexOf(raw, text);
                while (index >= 0)
                {
                    raw.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryChat)),
                            index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index = TextUtils.indexOf(raw, text, index + text.length());
                }
                viewHolderHashtag.textViewHashtagAnzahl.setText(raw);

                if(esaphHashtag.getLastConversationMessage() != null)
                {
                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            esaphHashtag.getLastConversationMessage().getIMAGE_ID(),
                            viewHolderHashtag.roundedImageViewMain,
                            viewHolderHashtag.progressBar,
                            new EsaphDimension(viewHolderHashtag.roundedImageViewMain.getWidth(),
                                    viewHolderHashtag.roundedImageViewMain.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_rounded_corners));
                }
                break;

            case 1: //Memory placeholder.
                TitleList memoryDividerPlaceholder = (TitleList) object;
                ViewHolderDividerTitle viewHolderDividerTitle = (ViewHolderDividerTitle) holder;
                viewHolderDividerTitle.textViewDividerTitle.setText(memoryDividerPlaceholder.getTextTitle());
                break;

            case 2:
                DatumList datumHolder = (DatumList) object;
                ViewHolderDatum viewHolderDatum = (ViewHolderDatum) holder;
                viewHolderDatum.textViewDatum.setText(datumHolder.getDatumSpotLightStyle());
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

            if(objectType instanceof EsaphHashtag)
            {
                return 0;
            }
            else if(objectType instanceof TitleList) //Placeholder
            {
                return 1;
            }
            else if(objectType instanceof DatumList)
            {
                return 2;
            }
        }

        return -1;
    }

    private int getItemViewTypePerformence(int position, Object objectType)
    {
        if(position >= listDataDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            if(objectType instanceof EsaphHashtag)
            {
                return 0;
            }
            else if(objectType instanceof TitleList) //Placeholder
            {
                return 1;
            }
            else if(objectType instanceof DatumList)
            {
                return 2;
            }
        }
        return -1;
    }
}
