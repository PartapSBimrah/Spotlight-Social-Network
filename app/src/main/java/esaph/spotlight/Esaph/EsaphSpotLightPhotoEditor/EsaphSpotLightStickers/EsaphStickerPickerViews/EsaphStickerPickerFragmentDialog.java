package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.Background.AsyncLoadStickersFromServer;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLSticker;

public class EsaphStickerPickerFragmentDialog extends BottomSheetDialogFragment
{
    private static final String KEY_STICKER_SELECT_INTERFACE = "esaph.spotlight.parcel.interface.stickerselectlistener";
    private EsaphStickerPagerAdapter esaphStickerPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View viewNoData;

    public EsaphStickerPickerFragmentDialog()
    {
    }

    public static EsaphStickerPickerFragmentDialog getInstance(EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog onStickerSelectedListenerDialog)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphStickerPickerFragmentDialog.KEY_STICKER_SELECT_INTERFACE, onStickerSelectedListenerDialog);
        EsaphStickerPickerFragmentDialog esaphStickerPickerFragmentDialog = new EsaphStickerPickerFragmentDialog();
        esaphStickerPickerFragmentDialog.setArguments(bundle);

        return esaphStickerPickerFragmentDialog;
    }

    private EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog onStickerSelectedListenerDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            onStickerSelectedListenerDialog =
                    (EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog) bundle.getSerializable(EsaphStickerPickerFragmentDialog.KEY_STICKER_SELECT_INTERFACE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_esaph_sticker_picker_fragment_chat_camera, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewNoData = rootView.findViewById(R.id.linearLayoutNoSearchResults);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab != null)
                {
                    if(tab.getPosition() == tabLayout.getTabCount()) //-1?
                    {
                        new AsyncLoadStickersFromServer(getContext(), new AsyncLoadStickersFromServer.OnStickerPackSynchronizedListener() {
                            @Override
                            public void onStickerPackSynchronized() {
                                loadAllStickerPacks();
                            }
                        }).execute();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });

        esaphStickerPagerAdapter = new EsaphStickerPagerAdapter(getContext(),
                viewNoData,
                getChildFragmentManager());
        viewPager.setAdapter(esaphStickerPagerAdapter);

        loadAllStickerPacks();
    }

    private void loadAllStickerPacks()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<EsaphSpotLightStickerPack> list;

                SQLSticker sqlSticker = new SQLSticker(getContext());
                list = sqlSticker.getAllStickerPackLimiteOrderByTime();
                sqlSticker.close();

                final List<EsaphStickerViewBASEFragmentDialog> listBaseFragments = new ArrayList<>();
                int count = list.size();

                for(int counter = 0; counter < count; counter++)
                {
                    listBaseFragments.add(EsaphStickerViewBASEFragmentDialog.getInstance(list.get(counter), onStickerSelectedListenerDialog));
                }

                if(viewPager != null)
                {
                    viewPager.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(viewPager != null)
                            {
                                esaphStickerPagerAdapter.clear();
                                esaphStickerPagerAdapter.addData(listBaseFragments);

                                if(listBaseFragments.isEmpty())
                                {
                                    new AsyncLoadStickersFromServer(getContext(), new AsyncLoadStickersFromServer.OnStickerPackSynchronizedListener() {
                                        @Override
                                        public void onStickerPackSynchronized()
                                        {
                                            loadAllStickerPacks();
                                        }
                                    }).execute();
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
