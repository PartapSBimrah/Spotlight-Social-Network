package esaph.spotlight.navigation.Posting;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Displayer.EsaphImageLoaderDisplayingAnimation;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalProfilbildLoader;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.AktuelleGruppe;
import esaph.spotlight.SpotLightUser;

public class ArrayAdapterListFriends extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private Context context;
    private List<Object> listDataToDisplay;
    private LayoutInflater layoutInflater;
    private List<Object> listDataOriginal;
    private DialogSendInfo dialogSendInfo;

    public ArrayAdapterListFriends(Context context,
                                   TextView textView,
                                   TextView textViewInfoNoData,
                                   ImageView imageView,
                                   DialogSendInfo dialogSendInfo)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.dialogSendInfo = dialogSendInfo;
        this.listDataToDisplay = new ArrayList<>();
        this.listDataOriginal = new ArrayList<>();
        this.textViewNoData = textView;
        this.textViewInfoNoData = textViewInfoNoData;
        this.imageViewNoData = imageView;
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
                listDataToDisplay = (ArrayList<Object>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Object> FilteredArrList = new ArrayList<Object>();

                if (listDataOriginal == null)
                {
                    listDataOriginal = new ArrayList<Object>(listDataToDisplay); // saves the original data in mOriginalValues
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
                        Object treshHold = listDataOriginal.get(i);
                        if(treshHold instanceof AktuelleGruppe)
                        {
                            AktuelleGruppe aktuellerMoment = (AktuelleGruppe) treshHold;
                            if(aktuellerMoment.getTitle().toLowerCase().startsWith(constraint.toString()))
                            {
                                if(aktuellerMoment.hasSomePosts())
                                {
                                    FilteredArrList.add(new AktuelleGruppe(aktuellerMoment.getMIID(),
                                            aktuellerMoment.getBeitragAnzahl(),
                                            aktuellerMoment.getTitle(),
                                            aktuellerMoment.getType(),
                                            aktuellerMoment.getLastPostTime(),
                                            aktuellerMoment.getAdmin(),
                                            aktuellerMoment.getCreator(),
                                            aktuellerMoment.didILeaved(),
                                            aktuellerMoment.getLastMomentPost()));
                                }
                                else
                                {
                                    FilteredArrList.add(new AktuelleGruppe(aktuellerMoment.getMIID(),
                                            aktuellerMoment.getBeitragAnzahl(),
                                            aktuellerMoment.getTitle(),
                                            aktuellerMoment.getType(),
                                            aktuellerMoment.getLastPostTime(),
                                            aktuellerMoment.getAdmin(),
                                            aktuellerMoment.getCreator(),
                                            aktuellerMoment.didILeaved(),
                                            null));
                                }
                            }
                        }
                        else if(treshHold instanceof SpotLightUser)
                        {
                            SpotLightUser spotLightUser = (SpotLightUser) treshHold;
                            if(spotLightUser.getBenutzername().toLowerCase().startsWith(constraint.toString()))
                            {
                                FilteredArrList.add(new SpotLightUser(
                                        spotLightUser.getUID(),
                                        spotLightUser.getBenutzername(),
                                        spotLightUser.getVorname(),
                                        spotLightUser.getAlter(),
                                        spotLightUser.getRegion(),
                                        spotLightUser.wasFriends(),
                                        spotLightUser.getDescriptionPlopp()));
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

    private int ChatCount = 0;
    private int GroupCount = 0;

    public int getChatCount()
    {
        return ChatCount;
    }

    public int getGroupCount()
    {
        return GroupCount;
    }

    private boolean searchingActivated;
    private TextView textViewNoData;
    private TextView textViewInfoNoData;
    private ImageView imageViewNoData;

    public void setSearchingActivatedOrNot(boolean searchingActivated)
    {
        this.searchingActivated = searchingActivated;
    }

    public void pushData(List<Object> data)
    {
        if(!data.isEmpty())
        {
            this.ChatCount += data.size();
            this.listDataToDisplay.addAll(data);
            this.listDataOriginal.addAll(data);
        }

        if(!searchingActivated)
        {
            if(getOriginalCount() == 0)
            {
                textViewInfoNoData.setVisibility(View.VISIBLE);
                textViewNoData.setVisibility(View.VISIBLE);
                imageViewNoData.setVisibility(View.VISIBLE);
            }
            else
            {
                textViewInfoNoData.setVisibility(View.GONE);
                textViewNoData.setVisibility(View.GONE);
                imageViewNoData.setVisibility(View.GONE);
            }
        }
        else
        {
            textViewInfoNoData.setVisibility(View.GONE);
            textViewNoData.setVisibility(View.GONE);
            imageViewNoData.setVisibility(View.GONE);
        }

        notifyDataSetChanged();
    }

    public Object getItem(int position)
    {
        return listDataToDisplay.get(position);
    }

    public List<Object> getList()
    {
        return this.listDataToDisplay;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return listDataToDisplay.size();
    }

    public int getOriginalCount()
    {
        return listDataOriginal.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0: //First item listDataDisplay
                View viewFirstItem = layoutInflater.inflate(R.layout.sent_list_user_item, parent, false);
                viewHolder = new ViewHolderUser(viewFirstItem);
                break;

            case 1: //Memory title placeholder.
                View viewPlaceholder = layoutInflater.inflate(R.layout.sent_list_titel_layout, parent, false);
                viewHolder = new ViewHolderTopTitel(viewPlaceholder);
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
                final ViewHolderUser viewHolderUser = (ViewHolderUser) holder;
                final SpotLightUser spotLightUser = (SpotLightUser) listDataToDisplay.get(position);

                if(dialogSendInfo.getSelectedUsers().contains(spotLightUser))
                {
                    System.out.println("Ist ausgewählt");
                    viewHolderUser.textViewSelectedState.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    viewHolderUser.textViewSelectedState.setText(context.getResources().getString(R.string.txt_dialog_send_info_state_gesendet));

                    Glide.with(context).load(R.drawable.dialog_send_info_state_background_not_selected).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUser.textViewSelectedState.setBackground(resource);
                            }
                        }
                    });
                }
                else
                {
                    viewHolderUser.textViewSelectedState.setTextColor(ContextCompat.getColor(context, R.color.colorDarkerGrey));
                    viewHolderUser.textViewSelectedState.setText(context.getResources().getString(R.string.txt_dialog_send_info_state_senden));

                    Glide.with(context).load(R.drawable.dialog_send_info_state_background_selected).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                viewHolderUser.textViewSelectedState.setBackground(resource);
                            }
                        }
                    });
                }

                viewHolderUser.textViewUsername.setText(spotLightUser.getBenutzername());
                viewHolderUser.textViewVorname.setText(spotLightUser.getVorname());
                viewHolderUser.relativeLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        dialogSendInfo.onRecyclerViewClick(viewHolderUser.getAdapterPosition());
                    }
                });

                EsaphGlobalProfilbildLoader.with(context).displayProfilbild(
                        viewHolderUser.imageViewMain,
                        null,
                        spotLightUser.getUID(),
                        EsaphImageLoaderDisplayingAnimation.BLINK,
                        R.drawable.ic_no_image_circle,
                        StorageHandlerProfilbild.FOLDER_PROFILBILD);
                break;

            case 1: //Memory title placeholder.
                ViewHolderTopTitel viewHolderTopTitel = (ViewHolderTopTitel) holder;
                TopSchriftPlaceholder topSchriftPlaceholder = (TopSchriftPlaceholder) listDataToDisplay.get(position);
                viewHolderTopTitel.textView.setText(topSchriftPlaceholder.getTopSchrift());
                break;
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        Object treshHold = this.listDataToDisplay.get(position);
        if(treshHold.getClass().equals(SpotLightUser.class))
        {
            return 0;
        }
        else if(treshHold.getClass().equals(TopSchriftPlaceholder.class))
        {
            return 1;
        }

        return -1;
    }

    public static class TopSchriftPlaceholder
    {
        String schrift;
        public TopSchriftPlaceholder(String schrift)
        {
            this.schrift = schrift;
        }

        public String getTopSchrift()
        {
            return this.schrift;
        }
    }

    private static class ViewHolderUser extends RecyclerView.ViewHolder
    {
        private TextView textViewUsername;
        private TextView textViewVorname;
        private TextView textViewSelectedState;
        private EsaphCircleImageView imageViewMain;
        private RelativeLayout relativeLayout;

        private ViewHolderUser(View view)
        {
            super(view);
            this.textViewUsername = (TextView) view.findViewById(R.id.dialogSendInfoItemUserUsername);
            this.textViewVorname = (TextView) view.findViewById(R.id.dialogSendInfoItemUserVorname);
            this.textViewSelectedState = (TextView) view.findViewById(R.id.dialogSendInfoTextViewSelectedState);
            this.imageViewMain = (EsaphCircleImageView) view.findViewById(R.id.imageViewLastPicPreview);
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.sentListUserItemRootView);
        }
    }

    private static class ViewHolderTopTitel extends RecyclerView.ViewHolder
    {
        private TextView textView;

        private ViewHolderTopTitel(View view)
        {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.titlePlaceHolderTextView);
        }
    }


    private static class ViewHolderMemoryPost
    {
        private TextView textViewTitleMemory;
        private TextView textViewPostCounter;
        private ImageView imageViewSelected;
    }
}
