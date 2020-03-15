package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.AdapterSearchHashtag;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchItemHashtag;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchItemUser;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchStatus;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.EsaphSearchTabAdapter;

public class ArrayAdapterSearchingHashtag extends EsaphSearchTabAdapter
{
    private ExecutorService executorService;
    private LayoutInflater layoutInflater;
    private List<Object> list;
    private Context context;

    public ArrayAdapterSearchingHashtag(Context context, ExecutorService executorService)
    {
        this.context = context;
        this.executorService = executorService;
        this.list = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void clearAll() {
        list.clear();
    }

    public void addAll(List<Object> items)
    {
        this.list.addAll(items);
    }

    public void clear()
    {
        this.list.clear();
    }

    @Override
    public void notifyDataSetChanged()
    {
        if(list.size() > 1)
        {
            list.remove(0);
        }
        super.notifyDataSetChanged();
    }

    public void addSearchStatus()
    {
        this.list.add(0, new SearchStatus(context.getResources().getString(R.string.txt_searchStatus_Suche), 0));
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    private static class ViewHolderHashtag //6 sachen
    {
        private ProgressBar progressBar;
        private ImageView imageViewHashtagPreview;
        private TextView textViewUsername;
        private TextView textViewVorname;
        private TextView textViewMainLocation;
    }

    private static class ViewHolderSearchingAndNotFound
    {
        private AVLoadingIndicatorView progressBarLoading;
        private TextView textView;
    }

    private static class ViewHolderDividerTitle
    {
        private TextView textViewDividerTitle;
    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = this.list.get(position);
        if(object instanceof SearchItemUser)
        {
            return 0;
        }
        else if(object instanceof SearchStatus)
        {
            return 1;
        }
        else if(object instanceof TitleList)
        {
            return 2;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        switch (getItemViewType(position))
        {
            case 0:
                final SearchItemHashtag searchItemHashtag = (SearchItemHashtag) list.get(position);
                final ViewHolderHashtag viewHolderHashtag;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.search_sheet_list_item_hashtag, parent, false);
                    viewHolderHashtag = new ViewHolderHashtag();
                    viewHolderHashtag.textViewMainLocation = (TextView) convertView.findViewById(R.id.textViewSearchUserProvince);
                    viewHolderHashtag.textViewUsername = (TextView) convertView.findViewById(R.id.textViewSearchUserUsername);
                    viewHolderHashtag.textViewVorname = (TextView) convertView.findViewById(R.id.textViewSearchUserVorname);
                    viewHolderHashtag.imageViewHashtagPreview = (ImageView) convertView.findViewById(R.id.imageViewHashtagPreview);

                    convertView.setTag(viewHolderHashtag);
                }
                else
                {
                    viewHolderHashtag = (ViewHolderHashtag) convertView.getTag();
                }

                break;


            case 1:
                final SearchStatus searchStatus = (SearchStatus) list.get(position);
                final ViewHolderSearchingAndNotFound viewHolderSearchingAndNotFound;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_search_status_item, parent, false);
                    viewHolderSearchingAndNotFound = new ViewHolderSearchingAndNotFound();
                    viewHolderSearchingAndNotFound.textView = (TextView) convertView.findViewById(R.id.textViewSearchStatusText);
                    viewHolderSearchingAndNotFound.progressBarLoading = (AVLoadingIndicatorView) convertView.findViewById(R.id.progressBarSearchStatus);
                    convertView.setTag(viewHolderSearchingAndNotFound);
                }
                else
                {
                    viewHolderSearchingAndNotFound = (ViewHolderSearchingAndNotFound) convertView.getTag();
                }


                if(searchStatus.getState() == 0) //Loading
                {
                    viewHolderSearchingAndNotFound.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

                    Glide.with(context).load(R.drawable.background_add_inverted).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderSearchingAndNotFound.textView.setBackground(resource);
                            }
                        }
                    });

                    viewHolderSearchingAndNotFound.progressBarLoading.smoothToShow();
                    viewHolderSearchingAndNotFound.textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()));
                }
                else if(searchStatus.getState() == 1) //Na error
                {
                    viewHolderSearchingAndNotFound.textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));

                    Glide.with(context).load(R.drawable.background_add).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderSearchingAndNotFound.textView.setBackground(resource);
                            }
                        }
                    });

                    viewHolderSearchingAndNotFound.progressBarLoading.smoothToHide();
                    viewHolderSearchingAndNotFound.textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()));
                }

                viewHolderSearchingAndNotFound.textView.setText(searchStatus.getStatus());
                break;


            case 2:
                TitleList memoryDividerPlaceholder = (TitleList) list.get(position);
                final ViewHolderDividerTitle viewHolderDividerTitle;
                if(convertView == null)
                {
                    convertView = layoutInflater.inflate(R.layout.layout_memorys_item_memory_placeholder, parent, false);
                    viewHolderDividerTitle = new ViewHolderDividerTitle();
                    viewHolderDividerTitle.textViewDividerTitle = (TextView) convertView.findViewById(R.id.textViewMemoryItemTitlePlaceholder);
                    convertView.setTag(viewHolderDividerTitle);
                }
                else
                {
                    viewHolderDividerTitle = (ViewHolderDividerTitle) convertView.getTag();
                }

                viewHolderDividerTitle.textViewDividerTitle.setText(memoryDividerPlaceholder.getTextTitle());
                break;
        }
        return convertView;
    }

}
