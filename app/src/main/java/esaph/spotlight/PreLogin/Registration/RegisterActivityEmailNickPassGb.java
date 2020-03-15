package esaph.spotlight.PreLogin.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

import android.os.HandlerThread;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.PreLogin.EmojiExcludeFilter;
import esaph.spotlight.R;
import esaph.spotlight.PreLogin.LoginActivity;
import esaph.spotlight.SocketResources;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;

public class RegisterActivityEmailNickPassGb extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private Spinner spinnerGeschlecht;
    private TextView textViewBirthday;
    private ImageView imageViewUsername;
    private ImageView imageViewEmail;
    private ImageView imageViewPassword;
    private ImageView imageViewPasswordRepeat;
    private ImageView imageViewAge;
    private long calendarBirthday;
    private boolean geschlechtSelected = false;
    private Button buttonRegister;
    private static final String mann = "male";
    private Handler handler;

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(RegisterActivityEmailNickPassGb.this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    final com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener onDateListener = new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            isBirthdayValid(calendar.getTimeInMillis());
            checkAllDataValid();
        }
    };


    private static final int minAge = 12;
    private static final int maxAge = 100;
    private static final String dateFormat = "yyyy-MM-dd";
    public void isBirthdayValid(long gbmillis)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Calendar now = Calendar.getInstance();
            Calendar gb = Calendar.getInstance();
            gb.setTimeInMillis(gbmillis);
            now.setTimeInMillis(System.currentTimeMillis());
            DateTime dt1 = new DateTime(sdf.format(gb.getTime()));
            DateTime dt2 = new DateTime(sdf.format(now.getTime()));

            if(gb.getTimeInMillis() < now.getTimeInMillis())
            {
                int jahre = Years.yearsBetween(dt1.withTimeAtStartOfDay(), dt2.withTimeAtStartOfDay()).getYears();
                if(jahre >= minAge && jahre <= maxAge)
                {
                    imageViewAge.setVisibility(View.VISIBLE);
                    textViewBirthday.setText(sdf.format(gb.getTime()));
                }
                else
                {
                    textViewBirthday.setText(sdf.format(gb.getTime()));
                    imageViewAge.setVisibility(View.INVISIBLE);
                    showErrorMessage(textViewBirthday, getResources().getString(R.string.txt__errorMinAlter));
                }
                calendarBirthday = gb.getTimeInMillis();
            }
            else
            {
                showErrorMessage(textViewBirthday, getResources().getString(R.string.txt__errorMinAlter));
                imageViewAge.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "isBirthdayValid() failed: " + ec);
        }
    }

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email_mobil);

        HandlerThread mHandlerThread = new HandlerThread("HT_RAENPG");
        mHandlerThread.start();
        handler = new Handler(mHandlerThread.getLooper());

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ImageView backButton = (ImageView) findViewById(R.id.register_back_button);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                finish();
            }
        });

        textViewBirthday = (TextView) findViewById(R.id.textViewBirthday);
        buttonRegister = (Button) findViewById(R.id.registerButtonWeiter);
        editTextUsername = (EditText) findViewById(R.id.editTextRegisterNick);
        editTextEmail = (EditText) findViewById(R.id.registerEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextRegisterPasswort);
        editTextPasswordRepeat = (EditText) findViewById(R.id.editTextRegisterPasswortRepeat);
        spinnerGeschlecht = (Spinner) findViewById(R.id.registerGeschlecht);

        editTextUsername.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivityEmailNickPassGb.this),
                new InputFilter.LengthFilter(20)});
        editTextEmail.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivityEmailNickPassGb.this)});
        editTextPassword.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivityEmailNickPassGb.this),
                new InputFilter.LengthFilter(20)});
        editTextPasswordRepeat.setFilters(new InputFilter[]{new EmojiExcludeFilter(RegisterActivityEmailNickPassGb.this),
                new InputFilter.LengthFilter(20)});

        imageViewUsername = (ImageView) findViewById(R.id.imageViewRegisterNick);
        imageViewEmail = (ImageView) findViewById(R.id.imageViewRegisterEmail);
        imageViewPassword = (ImageView) findViewById(R.id.imageViewRegisterPasswort);
        imageViewPasswordRepeat = (ImageView) findViewById(R.id.imageViewRegisterPasswortRepeat);
        imageViewAge = (ImageView) findViewById(R.id.imageViewRegisterBirthday);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivityEmailNickPassGb.this, R.layout.register_spinner_textview, getResources().getStringArray(R.array.arrayRegisterGeschlecht));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGeschlecht.setAdapter(dataAdapter);
        spinnerGeschlecht.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                geschlechtSelected = true;
                checkAllDataValid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                geschlechtSelected = false;
                checkAllDataValid();
            }
        });



        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                firebaseAnalytics.logEvent("R_FINISHING", null);
                if(imageViewUsername.getVisibility() == View.VISIBLE && imageViewEmail.getVisibility() == View.VISIBLE && imageViewPassword.getVisibility() == View.VISIBLE
                        && imageViewPasswordRepeat.getVisibility() == View.VISIBLE && imageViewAge.getVisibility() == View.VISIBLE && geschlechtSelected)
                {
                    Bundle registerBundle = getIntent().getExtras();
                    if(registerBundle != null)
                    {
                        final String geschlecht;
                        final String Vorname = registerBundle.getString(RegisterActivity.vorname, "ERR");
                        final String Nachname = registerBundle.getString(RegisterActivity.nachname, "ERR");
                        final ChatTextMessage chatTextMessage = (ChatTextMessage) registerBundle.getSerializable(RegisterActivity.descriptionPlopp);
                        if(Vorname.equals("ERR") || Nachname.equals("ERR"))
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                            alertDialog.setTitle(getResources().getString(R.string.txt__alert_BenutzerErstellenFehlerTitel));
                            alertDialog.setMessage(getResources().getString(R.string.txt__alert_BenutzerErstellenFehler));
                        }
                        else
                        {
                            if(spinnerGeschlecht.getSelectedItem().equals(mann))
                            {
                                geschlecht = "male";
                            }
                            else
                            {
                                geschlecht = "female";
                            }

                            new AsyncGetEmailState(RegisterActivityEmailNickPassGb.this, new EmailStateListener()
                            {
                                @Override
                                public void onEmailCanBeSent()
                                {
                                    if(chatTextMessage != null)
                                    {
                                        new RegisterUser(editTextUsername.getText().toString(),
                                                editTextPassword.getText().toString(),
                                                ""+calendarBirthday,
                                                editTextEmail.getText().toString(),
                                                Vorname,
                                                Nachname,
                                                geschlecht,
                                                Locale.getDefault().getLanguage(),
                                                chatTextMessage.getEsaphPloppInformationsJSON()).execute();
                                    }
                                }

                                @Override
                                public void onEmailLimitReached()
                                {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivityEmailNickPassGb.this);
                                    dialog.setTitle(getResources().getString(R.string.txt__alertFailedEmailTitle));
                                    dialog.setMessage(getResources().getString(R.string.txt__alertFailedEmailDetails));
                                    dialog.show();
                                }
                            }).execute();
                        }
                    }
                }
            }
        });

        editTextUsername.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                imageViewUsername.setVisibility(View.INVISIBLE);
                handler.removeCallbacks(checkUsernameLastReference);
                if(editTextUsername.getText().length() >= 2 && editTextUsername.getText().length() <= 20)
                {
                    handler.postDelayed(new Thread(new CheckUsername()), 500);
                }
                else
                {
                    imageViewUsername.setVisibility(View.INVISIBLE);

                }

                checkAllDataValid();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(editTextUsername.getText().length() >= 2 && editTextUsername.getText().length() <= 20)
                {

                }
                else
                {
                    showErrorMessage(editTextUsername, getResources().getString(R.string.txt__errorBenutzernameMinMax));
                }
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                imageViewEmail.setVisibility(View.INVISIBLE);
                handler.removeCallbacks(checkMailLastReference);
                if(isValidEmailAddress(editTextEmail.getText().toString()))
                {
                    handler.postDelayed(new Thread(new checkMail()), 500);
                }
                else
                {
                    imageViewEmail.setVisibility(View.INVISIBLE);
                }
                checkAllDataValid();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!isValidEmailAddress(editTextEmail.getText().toString()))
                {
                    showErrorMessage(editTextEmail, getResources().getString(R.string.txt__errorEmailUngültig));
                }
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                imageViewPassword.setVisibility(View.INVISIBLE);
                if(editTextPassword.getText().length() >= 10 && editTextPassword.getText().length() <= 20)
                {
                    imageViewPassword.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewPassword.setVisibility(View.INVISIBLE);
                }

                if(!editTextPassword.getText().toString().isEmpty() && editTextPassword.getText().toString().equals(editTextPasswordRepeat.getText().toString()))
                {
                    imageViewPasswordRepeat.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewPasswordRepeat.setVisibility(View.INVISIBLE);
                }

                checkAllDataValid();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });


        editTextPasswordRepeat.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imageViewPasswordRepeat.setVisibility(View.INVISIBLE);
                if(!editTextPassword.getText().toString().isEmpty() && editTextPassword.getText().toString().equals(editTextPasswordRepeat.getText().toString()))
                {
                    imageViewPasswordRepeat.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewPasswordRepeat.setVisibility(View.INVISIBLE);
                    showErrorMessage(editTextPassword, getResources().getString(R.string.txt__errorPasswortMatchingError));
                }

                checkAllDataValid();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        textViewBirthday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        onDateListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryChat));

                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
        });
    }



    public boolean isValidEmailAddress(String email)
    {
        if(email.length() <= 30)
        {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(email);
            return m.matches();
        }
        else
        {
            return false;
        }
    }


    private CheckUsername checkUsernameLastReference = null;
    private class CheckUsername implements Runnable
    {
        private String UsernameCheck = null;

        private CheckUsername()
        {
        }


        @Override
        public void run()
        {
            try
            {
                SocketResources socketResources = new SocketResources();
                JSONObject jsonObjectRegister = new JSONObject();
                jsonObjectRegister.put("LRC", "LRU");
                String CurrentInputUsername = editTextUsername.getText().toString();
                jsonObjectRegister.put("USRN", CurrentInputUsername);

                if(CurrentInputUsername.length() < 2 || CurrentInputUsername.length() > 20)
                {
                    return;
                }

                SSLSocket usernameSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), socketResources.getServerAddress(), socketResources.getServerPortLRServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(usernameSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(usernameSocket.getOutputStream()));
                writer.println(jsonObjectRegister.toString());
                writer.flush();
                UsernameCheck = reader.readLine();

                writer.close();
                reader.close();
                usernameSocket.close();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onPostExecute(UsernameCheck);
                    }
                });
            }
            catch (Exception ec)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onPostExecute(UsernameCheck);
                    }
                });
            }
        }


        private void onPostExecute(String result)
        {
            if(result != null)
            {
                if(result.equals("LRUT"))
                {
                    imageViewUsername.setVisibility(View.VISIBLE);
                }
                else
                {
                    showErrorMessage(editTextUsername, getResources().getString(R.string.txt__errorBenutzernameVergeben));
                    imageViewUsername.setVisibility(View.INVISIBLE);
                }
            }
        }
    }





    private checkMail checkMailLastReference = null;
    private class checkMail implements Runnable
    {
        private String emailFromServer;
        private checkMail()
        {
        }

        @Override
        public void run()
        {
            try
            {
                SocketResources socketResources = new SocketResources();
                JSONObject jsonObjectRegister = new JSONObject();
                jsonObjectRegister.put("LRC", "LRE");
                String mail = editTextEmail.getText().toString();
                if(!isValidEmailAddress(mail))
                {
                    return;
                }

                jsonObjectRegister.put("EMAIL", mail);

                SSLSocket mailSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), socketResources.getServerAddress(), socketResources.getServerPortLRServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(mailSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(mailSocket.getOutputStream()));
                writer.println(jsonObjectRegister.toString());
                writer.flush();
                emailFromServer = reader.readLine();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(emailFromServer);
                    }
                });

                writer.close();
                reader.close();
                mailSocket.close();
            }
            catch (Exception ec)
            {
                Log.e(getClass().getName(), "Exception: " + ec);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onPostExecute(null);
                    }
                });
            }
        }


        private void onPostExecute(String result)
        {
            if(result != null)
            {
                if(result.equals("LRET"))
                {
                    imageViewEmail.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewEmail.setVisibility(View.INVISIBLE);
                    showErrorMessage(editTextEmail, getResources().getString(R.string.txt__errorEmailVergeben));
                }
            }
        }
    }

    public static final String actionRegister = "esaph.spotlight.registration.succesfully";

    private class RegisterUser extends AsyncTask<String, Integer, String>
    {
        private JSONObject jsonObjectDescriptionPlopp;
        private String Username;
        private String Password;
        private String Birthday;
        private String Email;
        private String Vorname;
        private String Nachname;
        private String Geschlecht;
        private String Region;
        private ProgressDialog progressDialog;
        private boolean maxConn = false;

        public RegisterUser(String Username, String Password, String Birthday, String Email, String Vorname, String Nachname, String Geschlecht, String Region, JSONObject jsonObjectDescriptionPlopp)
        {
            this.jsonObjectDescriptionPlopp = jsonObjectDescriptionPlopp;
            this.Username = Username;
            this.Password = Password;
            this.Birthday = Birthday;
            this.Email = Email;
            this.Vorname = Vorname;
            this.Nachname = Nachname;
            this.Geschlecht = Geschlecht;
            this.Region = Region;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            buttonRegister.setClickable(false);
            this.progressDialog = new ProgressDialog(RegisterActivityEmailNickPassGb.this);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(getResources().getString(R.string.txt__alertRegister));
            this.progressDialog.setMessage(getResources().getString(R.string.txt__alertUnoMomento));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String register = null;
            try
            {
                SocketResources socketResources = new SocketResources();
                JSONObject jsonObjectRegister = new JSONObject();
                jsonObjectRegister.put("LRC", "LRR");
                jsonObjectRegister.put("USRN", this.Username);
                jsonObjectRegister.put("PW", this.Password);
                jsonObjectRegister.put("VORNAME", this.Vorname);
                jsonObjectRegister.put("NACHNAME", this.Nachname);
                jsonObjectRegister.put("EMAIL", this.Email);
                jsonObjectRegister.put("GESCHLECHT", this.Geschlecht);
                jsonObjectRegister.put("BIRTHDAY", this.Birthday);
                jsonObjectRegister.put("REGION", this.Region);
                jsonObjectRegister.put("DESPLOPP", this.jsonObjectDescriptionPlopp);

                SSLSocket registerSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), socketResources.getServerAddress(), socketResources.getServerPortLRServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(registerSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(registerSocket.getOutputStream()));

                writer.println(jsonObjectRegister.toString());
                writer.flush();
                register = reader.readLine();

                writer.close();
                reader.close();
                registerSocket.close();
                return register;
            }
            catch(EOFException eof)
            {
                maxConn = true;
                return register;
            }
            catch(Exception ec)
            {
                Log.e(getClass().getName(), "Exception: " + ec);
                return register;
            }
        }





        @Override
        protected void onPostExecute(String result)
        {
            buttonRegister.setClickable(true);

            this.progressDialog.cancel();

            if(maxConn)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivityEmailNickPassGb.this);
                dialog.setTitle(getResources().getString(R.string.txt__alertRegister));
                dialog.setMessage(getResources().getString(R.string.txt__alertMaxConnections));
                dialog.show();
            }
            else
            {
                if(result != null)
                {
                    if (result.equals("LRRT"))
                    {
                        CLPreferences preferences = new CLPreferences(getApplicationContext());
                        preferences.setUsername(this.Username);
                        preferences.setPassword(this.Password, this.Username);

                        Intent intentRegistered = new Intent(RegisterActivityEmailNickPassGb.this, LoginActivity.class);
                        intentRegistered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentRegistered.setAction(RegisterActivityEmailNickPassGb.actionRegister);
                        startActivity(intentRegistered);
                        finish();
                    }
                    else
                    {
                        if (result.equals("LRRF"))
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivityEmailNickPassGb.this);
                            dialog.setTitle(getResources().getString(R.string.txt__alertRegister));
                            dialog.setMessage(getResources().getString(R.string.txt__alertUserExists));
                            dialog.show();
                        }
                        else
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivityEmailNickPassGb.this);
                            dialog.setTitle(getResources().getString(R.string.txt__alertRegister));
                            dialog.setMessage(getResources().getString(R.string.txt__alertNeverHappens));
                            dialog.show();
                        }
                    }
                }
            }
        }
    }




    private void checkAllDataValid()
    {
        if(imageViewUsername.getVisibility() == View.VISIBLE && imageViewEmail.getVisibility() == View.VISIBLE && imageViewPassword.getVisibility() == View.VISIBLE
                && imageViewPasswordRepeat.getVisibility() == View.VISIBLE && imageViewAge.getVisibility() == View.VISIBLE && geschlechtSelected)
        {
            buttonRegister.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

            Glide.with(this).load(R.drawable.background_register_ok_button).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        buttonRegister.setBackground(resource);
                    }
                }
            });

        }
        else
        {
            buttonRegister.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryChat));
            Glide.with(this).load(R.drawable.background_login_button).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        buttonRegister.setBackground(resource);
                    }
                }
            });
        }
    }

    private BubbleShowCase lastBubbleShowCase;
    private void showErrorMessage(View view, String Message)
    {
        if(lastBubbleShowCase != null)
        {
            lastBubbleShowCase.dismiss();
            lastBubbleShowCase = null;
        }

        lastBubbleShowCase = new BubbleShowCaseBuilder(RegisterActivityEmailNickPassGb.this)
                .title(Message)
                .backgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                .textColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack))
                .targetView(view).show();
    }
}
