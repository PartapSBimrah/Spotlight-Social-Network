package esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.ImageRequest;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;

public class EsaphTagAdapterVertical extends EsaphMomentsRecylerView implements Filterable
{
    private LayoutInflater inflater;
    private List<Object> listDisplay;
    private List<Object> listOriginal;
    private List<Object> listFooter;
    private Context context;
    private EsaphTagFragment esaphTagFragment;

    public EsaphTagAdapterVertical(
            EsaphTagFragment esaphTagFragment,
            WeakReference<View>[] views) {
        super(views);
        this.context = (Context) esaphTagFragment.getContext();
        this.esaphTagFragment = esaphTagFragment;
        this.listDisplay = new ArrayList<>();
        this.listOriginal = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChangeBypass()
    {
        hashtagDataBaseCount = 0;
        int size = listOriginal.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(listOriginal.get(counter) instanceof EsaphHashtag)
            {
                hashtagDataBaseCount++;
            }
        }

        super.notifyDataSetChangeBypass();
    }


    public boolean containsInList(String value)
    {
        int size = listDisplay.size();
        for(int counter = 0; counter < size; counter++)
        {
            Object object = listDisplay.get(counter);
            if(object instanceof EsaphHashtag)
            {
                EsaphHashtag esaphHashtag = (EsaphHashtag) object;
                if(esaphHashtag.getHashtagName().equals(value))
                {
                    return true;
                }
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
                List<Object> listFiltered = (ArrayList<Object>) results.values; // has the filtered values
                if(!containsInList(constraint.toString())) //If there exists a hashtag with the name, do not show to prevent double same hashtag names.
                {
                    insertHashtagByTypingPreview(listFiltered, constraint.toString());
                }

                listDisplay = listFiltered;
                notifyDataSetChangeBypass();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Object> FilteredArrList = new ArrayList<Object>();

                if (constraint == null || constraint.length() == 0)
                {
                    // set the Original result to return
                    results.count = listOriginal.size();
                    results.values = listOriginal;
                }
                else
                {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listOriginal.size(); i++)
                    {
                        Object treshHold = listOriginal.get(i);
                        if(treshHold instanceof EsaphHashtag)
                        {
                            EsaphHashtag esaphHashtag = (EsaphHashtag) treshHold;
                            if(esaphHashtag.getHashtagName().toLowerCase().startsWith(constraint.toString()))
                            {
                                EsaphHashtag esaphHashtagIntern = new EsaphHashtag(esaphHashtag.getHashtagName(),
                                    esaphHashtag.getLastConversationMessage(),
                                    esaphHashtag.getHashtagAnzahl());

                                FilteredArrList.add(esaphHashtagIntern);
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
    public List<Object> getListDataDisplay()
    {
        return listDisplay;
    }

    @Override
    public void pushNewDataInAdapterThreadSafe(List<Object> data)
    {
        this.listDisplay.addAll(data);
        this.listOriginal = new ArrayList<Object>(listDisplay); // saves the original data in mOriginalValues
    }

    @Override
    public void clearAllWithNotify() {

    }

    @Override
    public void removeSinglePostByPID(String PID) {

    }

    private int hashtagDataBaseCount = 0; //While creating a new hashtag. Its not in the database until it gets post. So load more could work right like it should.

    @Override
    public int[] getObjectCountsThreadSafe()
    {
        return new int[]{hashtagDataBaseCount};
    }

    @Override
    public boolean isEmpty()
    {
        return getObjectCountsThreadSafe()[0] <= 0;
    }

    @Override
    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(listDisplay.size()+listFooter.size()-1);
        }
    }

    @Override
    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(listDisplay.size()+listFooter.size()+1);
    }

    public void addRecentlyAdded(List<EsaphHashtag> esaphHashtags)
    {
        this.listDisplay.addAll(esaphHashtags);
        this.listOriginal = new ArrayList<Object>(listDisplay); // saves the original data in mOriginalValues
        notifyDataSetChanged(); //<---MUST BE !!
    }

    @Override
    public void notifyOnDataChangeInViewpager() {

    }

    private static class ViewHolderHashTag extends RecyclerView.ViewHolder
    {
        private TextView textViewHashTagName;
        private TextView textViewHashtagAnzahl;
        private ImageView imageViewStateAuswahl;
        private EsaphCircleImageView esaphCircleImageView;
        private ProgressBar progressBar;

        public ViewHolderHashTag(View view)
        {
            super(view);
            this.textViewHashTagName = (TextView) view.findViewById(R.id.textViewHashtagName);
            this.imageViewStateAuswahl = (ImageView) view.findViewById(R.id.imageViewSentListCheckedItem);
            this.textViewHashtagAnzahl = (TextView) view.findViewById(R.id.textViewHashtagAnzahl);
            this.esaphCircleImageView = (EsaphCircleImageView) view.findViewById(R.id.imageViewLastHashtag);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBarSmallViewLoading);
        }
    }

    private static class ViewHolderHashTagNew extends RecyclerView.ViewHolder
    {
        private View viewRootView;
        private TextView textViewHashTagName;

        public ViewHolderHashTagNew(View view)
        {
            super(view);
            this.textViewHashTagName = (TextView) view.findViewById(R.id.textViewHashtagName);
            this.viewRootView = view.findViewById(R.id.sentListUserItemRootView);
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                View viewPost = inflater.inflate(R.layout.layout_listview_hashtag_item_vertical, parent, false);
                viewHolder = new ViewHolderHashTag(viewPost);
                break;

            case 1:
                View viewNewHashtagsCreated = inflater.inflate(R.layout.layout_listview_hashtag_item_vertical_create_new, parent, false);
                viewHolder = new ViewHolderHashTagNew(viewNewHashtagsCreated);
                break;

            case 2:
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
        if(position >= listDisplay.size())
        {
            object = null;
        }
        else
        {
            object = this.listDisplay.get(position);
        }

        switch (getItemViewType(position))
        {
            case 0:
                final EsaphHashtag esaphHashtag = (EsaphHashtag) object;
                final ViewHolderHashTag viewHolderHashTag = (ViewHolderHashTag) holder;

                if(esaphHashtag.getHashtagAnzahl() > 0)
                {
                    viewHolderHashTag.textViewHashtagAnzahl.setText(
                            context.getResources().getQuantityString(R.plurals.txtBeitragCount,
                                    esaphHashtag.getHashtagAnzahl(),
                                    String.valueOf(esaphHashtag.getHashtagAnzahl())));
                }

                viewHolderHashTag.esaphCircleImageView.setBorderColorBackground(android.R.color.transparent);

                SpannableString str1= new SpannableString(context.getResources().getString(R.string.txt_hashtTagChar, esaphHashtag.getHashtagName()));
                str1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryChat)), 0, 1, 0);

                viewHolderHashTag.textViewHashTagName.setText(str1);
                viewHolderHashTag.esaphCircleImageView.getRootView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        esaphTagFragment.handleSelectHashtagClick(esaphHashtag);
                    }
                });

                if(esaphHashtag.getLastConversationMessage() != null)
                {
                    viewHolderHashTag.esaphCircleImageView.setDisableCircularTransformation(false);
                    if(esaphTagFragment.containsInList(esaphHashtag.getHashtagName()))
                    {
                        viewHolderHashTag.imageViewStateAuswahl.setVisibility(View.VISIBLE);
                        viewHolderHashTag.esaphCircleImageView.setPadding(
                                DisplayUtils.dp2px(6),
                                DisplayUtils.dp2px(6),
                                DisplayUtils.dp2px(6),
                                DisplayUtils.dp2px(6));
                    }
                    else
                    {
                        viewHolderHashTag.imageViewStateAuswahl.setVisibility(View.GONE);
                        viewHolderHashTag.esaphCircleImageView.setEsaphShaderBackground(null);
                        viewHolderHashTag.esaphCircleImageView.setPadding(
                                DisplayUtils.dp2px(0),
                                DisplayUtils.dp2px(0),
                                DisplayUtils.dp2px(0),
                                DisplayUtils.dp2px(0)
                        );
                    }

                    EsaphGlobalImageLoader.with(context).displayImage(ImageRequest.builder(
                            esaphHashtag.getLastConversationMessage().getIMAGE_ID(),
                            viewHolderHashTag.esaphCircleImageView,
                            viewHolderHashTag.progressBar,
                            new EsaphDimension(viewHolderHashTag.esaphCircleImageView.getWidth(),
                                    viewHolderHashTag.esaphCircleImageView.getHeight()),
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle));
                }
                else
                {
                    if(esaphTagFragment.containsInList(esaphHashtag.getHashtagName()))
                    {
                        viewHolderHashTag.imageViewStateAuswahl.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolderHashTag.imageViewStateAuswahl.setVisibility(View.GONE);
                    }

                    viewHolderHashTag.esaphCircleImageView.setDisableCircularTransformation(true);
                    viewHolderHashTag.esaphCircleImageView.setPadding(
                            DisplayUtils.dp2px(18),
                            DisplayUtils.dp2px(18),
                            DisplayUtils.dp2px(18),
                            DisplayUtils.dp2px(18)
                    );

                    Glide.with(context).load(R.drawable.ic_hashtag_only).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderHashTag.esaphCircleImageView.setImageDrawable(resource);
                            }
                        }
                    });
                }

                if(esaphHashtag.getHashtagAnzahl() == 0)
                {
                    viewHolderHashTag.textViewHashtagAnzahl.setText(context.getResources().getString(R.string.txt_NEW));
                }

                break;

            case 1:
                final EsaphHashtag esaphHashtagSecond = (EsaphHashtag) object;
                final ViewHolderHashTagNew viewHolderHashTagNew = (ViewHolderHashTagNew) holder;
                viewHolderHashTagNew.viewRootView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        esaphTagFragment.onStartNewHashtag();
                    }
                });

                viewHolderHashTagNew.textViewHashTagName.setText(esaphHashtagSecond.getHashtagName());
                break;

            case 2:
                ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;
                viewHolderFooter.avLoadingIndicatorView.smoothToShow();
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size() + listFooter.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        if(position >= listDisplay.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 2;
        }
        else
        {
            return ((EsaphHashtag)listDisplay.get(position)).getCurrentViewType();
        }
    }

    private void insertHashtagByTypingPreview(List<Object> list, String Hashtagname)
    {
        if(Hashtagname.isEmpty())
            return;

        EsaphHashtag esaphHashtag = new EsaphHashtag(Hashtagname,
                new ConversationMessage(),
                0);

        esaphHashtag.setCurrentViewType(EsaphHashtag.VIEWTYPE_NEW_HASHTAG);
        list.add(0, esaphHashtag);
    }

    public void insertNew(EsaphHashtag esaphHashtag)
    {
        listOriginal.add(esaphHashtag);
        listDisplay.add(esaphHashtag);
        notifyDataSetChangeBypass();
    }
}
