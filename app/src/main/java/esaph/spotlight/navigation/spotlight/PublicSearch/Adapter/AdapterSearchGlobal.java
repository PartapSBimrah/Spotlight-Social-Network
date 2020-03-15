package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class AdapterSearchGlobal extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private EsaphGlobalImageLoader esaphGlobalImageLoader;
    private LayoutInflater inflater;
    private List<Object> list;
    private List<Object> listFooter;
    private Context context;

    public AdapterSearchGlobal(Context context,
                                             EsaphGlobalImageLoader esaphGlobalImageLoader)
    {
        this.context = context;
        this.list = new ArrayList<>();
        this.listFooter = new ArrayList<>();
        this.esaphGlobalImageLoader = esaphGlobalImageLoader;
        this.inflater = LayoutInflater.from(context);
    }

    public void clearAllWithNotify()
    {
        this.list.clear();
        this.listFooter.clear();
    }

    public void addFooter()
    {
        if(listFooter.size() < 1)
        {
            listFooter.add(new Object());
            notifyItemInserted(list.size()+listFooter.size()-1);
        }
    }

    public void removeFooter()
    {
        listFooter.clear();
        notifyItemRemoved(list.size()+listFooter.size()+1);
    }

    public List<Object> getList()
    {
        return list;
    }

    public int getCount()
    {
        return this.list.size();
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

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return list.size() + listFooter.size();
    }

    public Object getItem(int pos)
    {
        return this.list.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                break;

            case 1:

                break;

            case 2:
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
        if(position >= list.size())
        {
            object = null;
        }
        else
        {
            object = this.list.get(position);
        }

        switch (getItemViewTypePerformence(object, position))
        {
            case 0:
                break;


            case 1:
                break;


            case 2:
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
        if(position >= list.size())
        {
            Log.i(getClass().getName(), "getItemViewType() FOOTER");
            return 3;
        }
        else
        {
            Object objectType = list.get(position);
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
        if(position >= list.size())
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
