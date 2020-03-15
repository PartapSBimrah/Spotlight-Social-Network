/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.analytics.FirebaseAnalytics;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.PreLogin.Dialogs.DialogAcceptPreLoginLaw;
import esaph.spotlight.PreLogin.Dialogs.DialogPasswortVergessen;
import esaph.spotlight.PreLogin.Registration.AsyncGetEmailState;
import esaph.spotlight.PreLogin.Registration.EmailStateListener;
import esaph.spotlight.PreLogin.Registration.RegisterActivity;
import esaph.spotlight.PreLogin.Registration.RegisterActivityEmailNickPassGb;
import esaph.spotlight.R;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.rechtliches.Datenschutz;
import esaph.spotlight.rechtliches.Haftungsauschluss;
import esaph.spotlight.rechtliches.Impressum;

public class LoginActivity extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private EditText editTextUsername;
    private EditText editTextPassword;

    public static final String extra_ActionDontNeedLogin = "esaph.mainacitiy.login.session.dontNeedSID";

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(LoginActivity.this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    private FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        try
        {
            if(getIntent() != null && getIntent().getAction() != null)
            {
                if (getIntent().getAction().equals(RegisterActivityEmailNickPassGb.actionRegister))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle(getResources().getString(R.string.txt_alertEmailVerficationTitle));
                    dialog.setMessage(getResources().getString(R.string.txt_alertEmailVerification));
                    dialog.show();

                    if(!isFinishing() && editTextPassword != null && editTextUsername != null)
                    {
                        CLPreferences preferences = new CLPreferences(getApplicationContext());
                        editTextUsername.setText(preferences.getUsername());
                        editTextPassword.setText(preferences.getPasswordEncrypted());

                        new LoginAsync(LoginActivity.this,
                                editTextUsername,
                                editTextPassword,
                                new LoginAsync.LoginListener()
                                {
                                    @Override
                                    public void onLoginSuccess()
                                    {
                                        if(!isFinishing())
                                        {
                                            grantAccessToAccount();
                                        }
                                    }

                                    @Override
                                    public void onLoginFailed(String Error_Code)
                                    {

                                    }
                                }
                        ).execute();
                    }
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Normal App start.");
        }

        TextView textViewImpressum = (TextView) findViewById(R.id.loginImpressum);
        textViewImpressum.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(LoginActivity.this, Impressum.class));
            }
        });

        TextView textViewHaftung = (TextView) findViewById(R.id.textViewHaftungsausschluss);
        textViewHaftung.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(LoginActivity.this, Haftungsauschluss.class));
            }
        });

        final TextView textViewDatenschutz = (TextView) findViewById(R.id.logInDatenSchutz);
        textViewDatenschutz.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(LoginActivity.this, Datenschutz.class));
            }
        });

        TextView textViewPasswortVergessen = (TextView) findViewById(R.id.textViewLoginPasswortVergessen);
        textViewPasswortVergessen.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(!isFinishing())
                {
                    DialogPasswortVergessen dialogPasswortVergessen = new DialogPasswortVergessen(LoginActivity.this);
                    dialogPasswortVergessen.show();
                }
            }
        });

        editTextUsername = (EditText) findViewById(R.id.loginUsername);
        editTextPassword = (EditText) findViewById(R.id.loginPassword);

        editTextUsername.setFilters(new InputFilter[]{new EmojiExcludeFilter(LoginActivity.this)});
        editTextPassword.setFilters(new InputFilter[]{new EmojiExcludeFilter(LoginActivity.this)});

        CLPreferences preferences = new CLPreferences(getApplicationContext());
        editTextUsername.setText(preferences.getLastLoggedName());
        Button buttonLogin = (Button) findViewById(R.id.buttonLoginActivityLogin);
        Button buttonRegister = (Button) findViewById(R.id.buttonLoginActivityRegister);

        buttonLogin.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                firebaseAnalytics.logEvent("ButtonLogin", null);

                if (editTextUsername.getText().toString().length() != 0 && editTextUsername.getText().length() >= 1 && editTextUsername.getText().length() <= 20)
                {
                    if (editTextPassword.getText().length() != 0 && editTextPassword.getText().length() >= 10 && editTextPassword.getText().length() <= 20)
                    {
                        new LoginAsync(LoginActivity.this,
                                editTextUsername,
                                editTextPassword,
                                new LoginAsync.LoginListener()
                                {
                                    @Override
                                    public void onLoginSuccess()
                                    {
                                        if(!isFinishing())
                                        {
                                            grantAccessToAccount();
                                        }
                                    }

                                    @Override
                                    public void onLoginFailed(String Error_Code)
                                    {

                                    }
                                }
                        ).execute();
                    }
                    else
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                        dialog.setTitle(getResources().getString(R.string.txt__alertLogIn));
                        dialog.setMessage(getResources().getString(R.string.txt_alertWrongPassword));
                        dialog.show();
                    }
                }
            }
        });

        buttonRegister.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                openRegisterButton(v);
            }
        });
    }

    private void signIn()
    {
        final DialogAcceptPreLoginLaw hurensohndialog = new DialogAcceptPreLoginLaw(LoginActivity.this, new DialogAcceptPreLoginLaw.ShitAcceptingInterface()
        {
            @Override
            public void onAcceptedShitRules()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onDeclinedShitRules() {
            }
        });
        hurensohndialog.setCanceledOnTouchOutside(false);
        hurensohndialog.show();
    }

    private void openRegisterButton(final View v)
    {
        firebaseAnalytics.logEvent("ButtonRegisterManual", null);
        new AsyncGetEmailState(LoginActivity.this, new EmailStateListener()
        {
            @Override
            public void onEmailCanBeSent()
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void onEmailLimitReached()
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setTitle(getResources().getString(R.string.txt__alertFailedEmailTitle));
                dialog.setMessage(getResources().getString(R.string.txt__alertFailedEmailDetails));
                dialog.show();
            }
        }).execute();
    }


    private void openSignInButton(final View v)
    {
        firebaseAnalytics.logEvent("GoogleSignInButton", null);
        signIn();
    }

    private void grantAccessToAccount()
    {
        new ShowFriends(LoginActivity.this, null).execute();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null)
        {
            view = new View(this);
        }

        if(imm != null)
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isWifiConnected()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager != null)
        {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected())
            {
                return true;
            }
        }
        return false;
    }
}

