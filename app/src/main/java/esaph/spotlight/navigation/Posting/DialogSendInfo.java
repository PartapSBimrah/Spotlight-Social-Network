package esaph.spotlight.navigation.Posting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.R;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.kamera.RunLoadDataForSending;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class DialogSendInfo extends BottomSheetDialogFragment
{
    private static final int StandardSpanCount = 1;
    private static final String extraListener = "esaph.spotlight.dialogsendinfo.receivers.interface";
    private ChooseReceiversListener chooseReceiversListener;

    private TextView textViewUploadPicture;
    private RelativeLayout relativeLayoutSelectedUsers;
    private TextView textViewSelectedUsers;

    private TextView textViewNoChatsMoreInfo;
    private TextView textViewNoData;
    private ImageView imageViewNoData;
    private List<SpotLightUser> selectedUsers = new ArrayList<>();
    private ArrayAdapterListFriends arrayAdapterListWatcher;
    private RecyclerView recylerViewFriends;
    private ImageView imageViewAddNewUsers;
    private EditText editTextSearching;

    private SwipeNavigation swipeNavigation;

    public DialogSendInfo()
    {
    }

    public static DialogSendInfo getInstance(ChooseReceiversListener chooseReceiversListener)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DialogSendInfo.extraListener, chooseReceiversListener);
        DialogSendInfo dialogSendInfo = new DialogSendInfo();
        dialogSendInfo.setArguments(bundle);
        return dialogSendInfo;
    }

    public interface ChooseReceiversListener extends Serializable
    {
        void onSendIt(JSONArray jsonArrayReceivers);
    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if(bottomSheet != null)
                {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof SwipeNavigation)
        {
            swipeNavigation = (SwipeNavigation) context;
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        stopSearching();
        editTextSearching = null;
        textViewNoChatsMoreInfo = null;
        textViewNoData = null;
        textViewSelectedUsers = null;
        textViewUploadPicture = null;
        imageViewAddNewUsers = null;
        imageViewNoData = null;
        recylerViewFriends = null;

        relativeLayoutSelectedUsers = null;
    }

    private final TextWatcher textWatcherMainInput = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            arrayAdapterListWatcher.getFilter().filter(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };

    private void startSearching()
    {
        Context context = getContext();
        if(context != null)
        {
            editTextSearching.addTextChangedListener(textWatcherMainInput);
            editTextSearching.setClickable(true);
            editTextSearching.setFocusable(true);
            editTextSearching.setFocusableInTouchMode(true);
            searchingActivated = true;
        }
    }

    private void stopSearching()
    {
        if(editTextSearching == null)
            return;

        editTextSearching.removeTextChangedListener(textWatcherMainInput);
        editTextSearching.setClickable(false);
        editTextSearching.setFocusable(false);
        editTextSearching.setFocusableInTouchMode(false);


        Activity activity = getActivity();
        if(activity != null)
        {
            if(arrayAdapterListWatcher.getOriginalCount() == 0)
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textViewNoChatsMoreInfo.setVisibility(View.VISIBLE);
                        textViewNoData.setVisibility(View.VISIBLE);
                        imageViewNoData.setVisibility(View.VISIBLE);
                    }
                });
            }
            else
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textViewNoChatsMoreInfo.setVisibility(View.GONE);
                        textViewNoData.setVisibility(View.GONE);
                        imageViewNoData.setVisibility(View.GONE);
                    }
                });
            }

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null)
            {
                view = new View(activity);
            }

            if(imm != null)
            {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        searchingActivated = false;
    }

    private boolean searchingActivated = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dialog_send_info, container, false);

        imageViewNoData = (ImageView) rootView.findViewById(R.id.nagivationGroupImageViewNoChats);
        textViewNoData = (TextView) rootView.findViewById(R.id.nagivationGroupTextViewNoChats);
        textViewNoChatsMoreInfo = (TextView) rootView.findViewById(R.id.textViewMain);

        recylerViewFriends = (RecyclerView) rootView.findViewById(R.id.momentsMainRecylerView);
        textViewUploadPicture = (TextView) rootView.findViewById(R.id.textViewSendPictureFinalUploadButton);

        editTextSearching = (EditText) rootView.findViewById(R.id.editTextDialogSendInfoSearch);
        imageViewAddNewUsers = (ImageView) rootView.findViewById(R.id.imageViewDialogSendInfoGetFriends);
        textViewSelectedUsers = (TextView) rootView.findViewById(R.id.textViewSelectedUsers);
        relativeLayoutSelectedUsers = (RelativeLayout) rootView.findViewById(R.id.backgroundSendDialogInfoAllUsersSelected);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            chooseReceiversListener = (ChooseReceiversListener) bundle.getSerializable(DialogSendInfo.extraListener);
        }

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        editTextSearching.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                {
                    startSearching();
                }
                else
                {
                    stopSearching();
                }
            }
        });

        textViewNoData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Intent intentShare = new Intent();
                intentShare.setAction(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.XX_txt_share_own_app) + " - " + SpotLightLoginSessionHandler.getLoggedUsername());
                intentShare.setType("text/plain");
                startActivity(intentShare);
            }
        });

        textViewUploadPicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                try
                {
                    JSONArray jsonArray = getWAMPData(); //WAMP DATA
                    chooseReceiversListener.onSendIt(jsonArray);
                    dismiss();
                }
                catch (Exception ec)
                {
                    System.out.println("Failed to get WAMP from post: " + ec);
                    Toast.makeText(getActivity(), getResources().getString(R.string.txt_alertPinFailedToDownloadDetails), Toast.LENGTH_LONG).show();
                }
            }
        });

        arrayAdapterListWatcher = new ArrayAdapterListFriends(getContext(),
                textViewNoData, textViewNoChatsMoreInfo, imageViewNoData, DialogSendInfo.this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DialogSendInfo.StandardSpanCount);

        Drawable drawableDivider = ContextCompat.getDrawable(recylerViewFriends.getContext(), R.drawable.divider_recyclerview);
        if(drawableDivider != null)
        {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recylerViewFriends.getContext(),
                    gridLayoutManager.getOrientation());
            dividerItemDecoration.setDrawable(drawableDivider);

            recylerViewFriends.addItemDecoration(dividerItemDecoration);
        }


        recylerViewFriends.setLayoutManager(gridLayoutManager);
        recylerViewFriends.setAdapter(arrayAdapterListWatcher);
        displayBottomSelectedUsers();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(isAdded())
                {
                    loadMore();
                }
            }
        }, 300);
    }

    private void loadMore()
    {
        if(searchingActivated) return;

        new Thread(new RunLoadDataForSending(getContext(), arrayAdapterListWatcher)).start();
    }

    private JSONArray getWAMPData() throws JSONException
    {
        JSONArray jsonArray = new JSONArray();

        JSONArray privateMemberData = getPostingAnWatcherData();

        if(privateMemberData != null)
        {
            jsonArray.put(privateMemberData);
        }

        return jsonArray;
    }

    public JSONArray getPostingAnWatcherData() throws JSONException
    {
        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < selectedUsers.size(); i++)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("REC_ID", selectedUsers.get(i).getUID());
            jsonObject.put("ST", ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    private void displayBottomSelectedUsers()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < selectedUsers.size(); i++)
        {
            stringBuilder.append(selectedUsers.get(i).getBenutzername());
            if(i+1 < selectedUsers.size())
            {
                stringBuilder.append(", ");
            }
        }

        if(selectedUsers.size() > 0)
        {
            textViewSelectedUsers.setText(stringBuilder.toString());
            allowSendPicture();
        }
        else
        {
            textViewSelectedUsers.setText("");
            disallowSendPicture();
        }
    }


    private void allowSendPicture()
    {
        relativeLayoutSelectedUsers.setVisibility(View.VISIBLE);

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(relativeLayoutSelectedUsers.getHeight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()))
                .setDuration(120);


        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                relativeLayoutSelectedUsers.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                relativeLayoutSelectedUsers.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();

        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void disallowSendPicture()
    {
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(relativeLayoutSelectedUsers.getHeight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()))
                .setDuration(120);

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                relativeLayoutSelectedUsers.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                relativeLayoutSelectedUsers.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                relativeLayoutSelectedUsers.setVisibility(View.INVISIBLE);
            }
        });
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }


    public List<SpotLightUser> getSelectedUsers() {
        return selectedUsers;
    }


    public void onRecyclerViewClick(int position)
    {
        Object object = arrayAdapterListWatcher.getItem(position);
        if(object instanceof SpotLightUser)
        {
            SpotLightUser spotLightUser = (SpotLightUser) object;
            if(selectedUsers.contains(spotLightUser))
            {
                selectedUsers.remove(spotLightUser);
            }
            else
            {
                selectedUsers.add(spotLightUser);
            }
            arrayAdapterListWatcher.notifyItemChanged(position);
        }

        displayBottomSelectedUsers();
    }
}
