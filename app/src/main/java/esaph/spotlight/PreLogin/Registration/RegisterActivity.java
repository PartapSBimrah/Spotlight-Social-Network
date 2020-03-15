package esaph.spotlight.PreLogin.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import esaph.spotlight.Esaph.EsaphActivity;
import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.PreLogin.EmojiExcludeFilter;
import esaph.spotlight.PreLogin.LoginActivity;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.rechtliches.Datenschutz;
import esaph.spotlight.rechtliches.Haftungsauschluss;
import esaph.spotlight.spots.SpotMaker.EsaphSpotMakerListener;

public class RegisterActivity extends EsaphActivity implements EsaphSpotMakerListener
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String vorname = "VOR";
    public static final String nachname = "NACH";
    public static final String descriptionPlopp = "DescPlopp";
    private ImageView vornameCheck;
    private ImageView nachnameCheck;
    private EditText editTextVorname;
    private EditText editTextNachname;
    private TextView textViewAddDescription;
    private ImageView imageViewShowDoneDescription;
    private Button buttonWeiter;

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(RegisterActivity.this);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    private FirebaseAnalytics firebaseAnalytics;
    private EsaphDescriptionPloppMakerTextual descriptionMakerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        TextView textViewAlsoRegistred = (TextView) findViewById(R.id.textViewAlsoHaveAccount);
        textViewAlsoRegistred.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textViewAddDescription = findViewById(R.id.textViewCreateDescriptionPlopp);
        textViewAddDescription.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                descriptionMakerFragment = EsaphDescriptionPloppMakerTextual.show();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutRegisterVorname, descriptionMakerFragment)
                        .commit();
            }
        });

        imageViewShowDoneDescription = findViewById(R.id.imageViewShowDoneDescription);


        final ImageView buttonBack = (ImageView) findViewById(R.id.register_back_button);
        vornameCheck = (ImageView) findViewById(R.id.imageViewRegisterVorname);
        nachnameCheck = (ImageView) findViewById(R.id.imageViewRegisterNachname);
        buttonWeiter = (Button) findViewById(R.id.buttonWeiter);
        editTextVorname = (EditText) findViewById(R.id.registerVorname);
        editTextNachname = (EditText) findViewById(R.id.registerNachname);


        editTextVorname.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivity.this),
                new InputFilter.LengthFilter(20)});

        editTextNachname.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivity.this),
                new InputFilter.LengthFilter(20)});


        TextView textViewDatenschutz = (TextView) findViewById(R.id.registerDatenschutz);
        textViewDatenschutz.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(RegisterActivity.this, Datenschutz.class));
            }
        });

        TextView textViewHaftungsausschluss = (TextView) findViewById(R.id.registerHaftungsausschluss);
        textViewHaftungsausschluss.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(RegisterActivity.this, Haftungsauschluss.class));
            }
        });

        editTextVorname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!editTextVorname.getText().toString().isEmpty() && editTextVorname.getText().toString().length() >= 1)
                {
                    vornameCheck.setVisibility(View.VISIBLE);
                }
                else
                {
                    vornameCheck.setVisibility(View.GONE);
                    showErrorMessage(editTextVorname, getResources().getString(R.string.txt__error_Vorname));
                }

                displayButtonWeiterIfPolicyAllows();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextNachname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!editTextNachname.getText().toString().isEmpty() && editTextNachname.getText().toString().length() >= 1)
                {
                    nachnameCheck.setVisibility(View.VISIBLE);
                }
                else
                {
                    nachnameCheck.setVisibility(View.INVISIBLE);
                    showErrorMessage(editTextNachname, getResources().getString(R.string.txt__error_Nachname));
                }

                displayButtonWeiterIfPolicyAllows();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });


        buttonWeiter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                firebaseAnalytics.logEvent("RegisterActivity_WEITER", null);
                final String vornamest = editTextVorname.getText().toString();
                final String nachnamest = editTextNachname.getText().toString();
                if(!vornamest.isEmpty() && vornamest.length() <= 20 &&
                        !nachnamest.isEmpty() && nachnamest.length() <= 20 && chatTextMessagePloppDescription != null)
                {
                    Bundle registerBundle = new Bundle();
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivityEmailNickPassGb.class);
                    registerBundle.putString(RegisterActivity.vorname, vornamest);
                    registerBundle.putString(RegisterActivity.nachname, nachnamest);
                    registerBundle.putSerializable(RegisterActivity.descriptionPlopp, chatTextMessagePloppDescription);
                    intent.putExtras(registerBundle);
                    startActivity(intent);
                }
            }
        });


        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                firebaseAnalytics.logEvent("RegisterActivity_BACK", null);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        onActivityDispatchBackPressEvent();
    }


    private void displayButtonWeiterIfPolicyAllows()
    {
        if(editTextVorname.getText().toString().length() < 1
                || editTextNachname.getText().toString().length() < 1
        || chatTextMessagePloppDescription == null)
        {
            buttonWeiter.setClickable(false);
            buttonWeiter.setAlpha(0.5f);
        }
        else
        {
            buttonWeiter.setClickable(true);
            buttonWeiter.setAlpha(1.0f);
        }
    }

    private void setupDescriptionShowWhenDoneShowTextWhenNeedToDo()
    {
        if(chatTextMessagePloppDescription != null)
        {
            textViewAddDescription.setText(getResources().getString(R.string.txt_edit));
            imageViewShowDoneDescription.setVisibility(View.VISIBLE);
            EsaphGlobalImageLoader.with(getApplicationContext()).canvasMode(CanvasRequest.builder(imageViewShowDoneDescription, new EsaphDimension(
                    imageViewShowDoneDescription.getWidth(),
                    imageViewShowDoneDescription.getHeight()
            ), chatTextMessagePloppDescription));
        }
        else
        {
            imageViewShowDoneDescription.setVisibility(View.GONE);
            textViewAddDescription.setText(getResources().getString(R.string.txt_tell_somethingAboutYou));
        }

        displayButtonWeiterIfPolicyAllows();
    }

    private ChatTextMessage chatTextMessagePloppDescription;

    @Override
    public void onDoneEditingSent(ConversationMessage conversationMessage)
    {
        chatTextMessagePloppDescription = (ChatTextMessage) conversationMessage;
        setupDescriptionShowWhenDoneShowTextWhenNeedToDo();
    }


    private BubbleShowCase lastBubbleShowCase;
    private void showErrorMessage(View view, String Message)
    {
        if(lastBubbleShowCase != null)
        {
            lastBubbleShowCase.dismiss();
            lastBubbleShowCase = null;
        }

        lastBubbleShowCase = new BubbleShowCaseBuilder(RegisterActivity.this)
                .title(Message)
                .backgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                .textColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack))
                .targetView(view)
                .show();
    }

    @Override
    public boolean onActivityDispatchBackPressEvent()
    {
        EsaphGlobalCommunicationFragment esaphGlobalCommunicationFragment =
                (EsaphGlobalCommunicationFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutRegisterVorname);

        if(esaphGlobalCommunicationFragment != null && esaphGlobalCommunicationFragment.isAdded())
        {
            if(!esaphGlobalCommunicationFragment.onActivityDispatchedBackPressed())
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(descriptionMakerFragment)
                        .commit();
            }
        }
        else
        {
            super.onBackPressed();
        }

        return true;
    }

}
