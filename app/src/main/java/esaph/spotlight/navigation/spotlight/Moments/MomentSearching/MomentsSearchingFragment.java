package esaph.spotlight.navigation.spotlight.Moments.MomentSearching;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Background.SearchDataBase;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemMainMoments;

public class MomentsSearchingFragment extends Fragment
{
    private static final int total_column_count = 2;
    private RecyclerView recyclerView;
    private MomentsSearchingAdapter momentsSearchingAdapter;
    private EditText editText;
    private ImageView imageViewCancel;
    private LinearLayout linearLayoutNoSearchResults;


    public MomentsSearchingFragment()
    {
        // Required empty public constructor
    }

    public static MomentsSearchingFragment getInstance()
    {
        return new MomentsSearchingFragment();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        linearLayoutNoSearchResults = null;
        recyclerView = null;
        momentsSearchingAdapter = null;
        editText = null;
        imageViewCancel = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moments_searching, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerViewMomentsSearching);
        editText = rootView.findViewById(R.id.editTextSearchingMoments);
        imageViewCancel = rootView.findViewById(R.id.imageViewStopSearching);
        linearLayoutNoSearchResults = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoSearchResults);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        imageViewCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText.getText().toString().isEmpty())
                {
                    FragmentActivity activity = getActivity();
                    if(activity != null)
                    {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .remove(MomentsSearchingFragment.this)
                                .commit();
                    }
                }
                else
                {
                    editText.setText("");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().isEmpty())
                {
                    momentsSearchingAdapter.setSearchData(new ArrayList<SearchItemMainMoments>());
                    linearLayoutNoSearchResults.setVisibility(View.VISIBLE);
                }
                else
                {
                    startSearching(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        momentsSearchingAdapter = new MomentsSearchingAdapter(getContext());
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), MomentsSearchingFragment.total_column_count);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(momentsSearchingAdapter);
    }


    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private void startSearching(String toSearch)
    {
        executorService.submit(new SearchDataBase(getContext(), toSearch, new SearchDataBase.SearchDataBaseDoneListener()
        {
            @Override
            public void onSearchResult(List<SearchItemMainMoments> list)
            {
                if(isAdded())
                {
                    momentsSearchingAdapter.setSearchData(list);
                    if(list.isEmpty())
                    {
                        linearLayoutNoSearchResults.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        linearLayoutNoSearchResults.setVisibility(View.GONE);
                    }
                }
            }
        }));
    }
}
