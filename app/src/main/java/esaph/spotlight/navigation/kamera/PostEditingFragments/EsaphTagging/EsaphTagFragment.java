package esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.GridViewEndlessScroll;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowHashtags.Background.RunnableSyncHashtags;

public class EsaphTagFragment extends BottomSheetDialogFragment implements DataBaseLoadWaiter
{
    private ArrayList<EsaphHashtag> selectedHashtags = new ArrayList<>(); //Do not remove when fragment onDestroyView();!
    private ArrayList<EsaphHashtag> newCreatedHashtags = new ArrayList<>(); //Do not remove when fragment onDestroyView();!
    private RecyclerView recyclerViewVertical;
    private LinearLayout linearLayoutNoData;
    private EditText editTextHashtag;

    private EsaphTagAdapterVertical esaphTagAdapterVertical;

    public EsaphTagFragment()
    {
        // Required empty public constructor
    }

    public static EsaphTagFragment getInstance()
    {
        return new EsaphTagFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        esaphTagAdapterVertical.addRecentlyAdded(newCreatedHashtags);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_tag, container, false);
        recyclerViewVertical = (RecyclerView) rootView.findViewById(R.id.listViewEsaphTagFragmentVertical);
        linearLayoutNoData = (LinearLayout) rootView.findViewById(R.id.relativLayoutEsaphNoTags);
        editTextHashtag = (EditText) rootView.findViewById(R.id.edittextAddHashtag);

        editTextHashtag.setFilters(new InputFilter[]{new InputFilter()
        {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if(source.length() > 0)
                {
                    if((!editTextHashtag.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase("\n"))
                            || (!editTextHashtag.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase(" ")))
                    {
                        onStartNewHashtag();
                        return "";
                    }
                    else if((editTextHashtag.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase("\n"))
                            || (editTextHashtag.getText().toString().isEmpty() && source.subSequence(source.length()-1, source.length()).toString().equalsIgnoreCase(" ")))
                    {
                        return "";
                    }
                }
                return source;
            }
        }, new InputFilter.LengthFilter(30)});

        editTextHashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                esaphTagAdapterVertical.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        TextView textViewCreateHashtag = (TextView) rootView.findViewById(R.id.textViewCreateHashtagInfo);
        textViewCreateHashtag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editTextHashtag.requestFocus();
                if(getActivity() != null)
                {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null)
                    {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            }
        });

        GridLayoutManager gridLayoutManagerVertical = new GridLayoutManager(getContext(), 1);
        recyclerViewVertical.setLayoutManager(gridLayoutManagerVertical);

        GridViewEndlessScroll gridViewEndlessScrollVertical = new GridViewEndlessScroll(gridLayoutManagerVertical)
        {
            @Override
            public void onScrolledVertical(int offset)
            {
            }

            @Override
            public void onLoadMore(int current_page)
            {
                loadMore();
            }
        };

        recyclerViewVertical.addOnScrollListener(gridViewEndlessScrollVertical);

        esaphTagAdapterVertical = new EsaphTagAdapterVertical(EsaphTagFragment.this,
                new WeakReference[]{new WeakReference<View>(linearLayoutNoData)});

        recyclerViewVertical.setAdapter(esaphTagAdapterVertical);
        return rootView;
    }

    public void onStartNewHashtag() //Automatically select the Hashtag.
    {
        EsaphHashtag esaphHashtag = new EsaphHashtag(editTextHashtag.getText().toString(), null, 0);
        esaphHashtag.setCurrentViewType(EsaphHashtag.VIEWTYPE_NORMAL_HASHTAG);
        selectedHashtags.add(esaphHashtag);
        newCreatedHashtags.add(esaphHashtag);
        esaphTagAdapterVertical.insertNew(esaphHashtag);

        editTextHashtag.setText("");
        esaphTagAdapterVertical.getFilter().filter(editTextHashtag.getText());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        loadMore();
    }

    public EsaphTagAdapterVertical getEsaphTagAdapterVertical()
    {
        return esaphTagAdapterVertical;
    }

    private ExecutorService executorService;
    private AtomicBoolean obLock = new AtomicBoolean(false);
    private void loadMore()
    {
        if(!isAdded())
            return;

        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();

        if(!this.obLock.compareAndSet(false, true))
            return;



        executorService.execute(new AsyncLoadMoreHashTags(getContext(), this, esaphTagAdapterVertical, obLock));
    }

    public boolean containsInList(String value)
    {
        int size = selectedHashtags.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(selectedHashtags.get(counter).getHashtagName().equals(value))
            {
                return true;
            }
        }
        return false;
    }


    public void removeHashtagByName(EsaphHashtag esaphHashtag)
    {
        int size = selectedHashtags.size();
        for(int counter = 0; counter < size; counter++)
        {
            if(selectedHashtags.get(counter).getHashtagName().equals(esaphHashtag.getHashtagName()))
            {
                selectedHashtags.remove(counter);
                break;
            }
        }

        int sizeN = newCreatedHashtags.size();
        for(int counter = 0; counter < sizeN; counter++)
        {
            if(newCreatedHashtags.get(counter).getHashtagName().equals(esaphHashtag.getHashtagName()))
            {
                newCreatedHashtags.remove(counter);
                break;
            }
        }
    }

    public void handleSelectHashtagClick(EsaphHashtag value)
    {
        //There is must be added in list original.

        if(containsInList(value.getHashtagName()))
        {
            removeHashtagByName(value);
        }
        else
        {
            selectedHashtags.add(value);
        }
        esaphTagAdapterVertical.notifyDataSetChangeBypass();
    }

    public ArrayList<EsaphHashtag> getSelectedHashtags()
    {
        if(selectedHashtags == null)
        {
            return new ArrayList<>(); //Because setting it to null when fragment removed. Or even not openened the hastag fragment its, crashing than there.
        }
        return selectedHashtags;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recyclerViewVertical = null;
        linearLayoutNoData = null;
        esaphTagAdapterVertical = null;
        if(executorService != null)
        {
            executorService.shutdown();
        }
        executorService = null;
    }

    @Override
    public void onNoDataAvaiable()
    {
        if(isAdded())
        {
            executorService.execute(new RunnableSyncHashtags(getContext(), new RunnableSyncHashtags.HashtagsSynchFinished()
            {
                @Override
                public void onNewDataSynched()
                {
                    loadMore();
                }

                @Override
                public void onFailed()
                {
                }
            }));
        }
    }
}
