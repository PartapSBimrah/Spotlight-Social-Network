/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.PreLogin.Registration.DemoAccountHandler.RegisterDemoAccount;
import esaph.spotlight.PreLogin.Registration.RegisterActivity;
import esaph.spotlight.R;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.rechtliches.Datenschutz;
import esaph.spotlight.rechtliches.Haftungsauschluss;
import esaph.spotlight.rechtliches.Impressum;

public class WelcomeActivity extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private TextView textViewSkip;
    private TextView textViewLogin;
    private FirebaseAnalytics firebaseAnalytics;

    private TextView textViewImpressum;
    private TextView textViewDatenschutz;
    private TextView textViewHaftungsauschluss;

    @Override
    protected void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(WelcomeActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        textViewSkip = findViewById(R.id.textViewSkip);
        textViewLogin = findViewById(R.id.textViewLogin);


        textViewImpressum = findViewById(R.id.loginImpressum);
        textViewDatenschutz = findViewById(R.id.logInDatenSchutz);
        textViewHaftungsauschluss = findViewById(R.id.textViewHaftungsausschluss);

        textViewImpressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, Impressum.class));
            }
        });

        textViewDatenschutz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, Datenschutz.class));
            }
        });

        textViewHaftungsauschluss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, Haftungsauschluss.class));
            }
        });

        textViewSkip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firebaseAnalytics.logEvent("WelcomeActivity_SKIP", null);

                new RegisterDemoAccount(WelcomeActivity.this,
                        new RegisterDemoAccount.RegisterDemoAccountStateListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                new ShowFriends(
                                        WelcomeActivity.this,
                                        new ShowFriends.ShowFriendsDoneListener() {
                                            @Override
                                            public void onFriendsSynched()
                                            {
                                                try
                                                {
                                                    if(!isFinishing())
                                                    {
                                                        Intent mainIntent = new Intent(WelcomeActivity.this, SwipeNavigation.class);
                                                        mainIntent.setAction(SwipeNavigation.extra_ActionNeedLogin);
                                                        startActivity(mainIntent);
                                                        finish();
                                                    }
                                                }
                                                catch (Exception ec)
                                                {
                                                }
                                            }

                                            @Override
                                            public void onFailed()
                                            {
                                                try
                                                {
                                                    if(!isFinishing())
                                                    {
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(WelcomeActivity.this);
                                                        dialog.setTitle(getResources().getString(R.string.txt_failedSynchronisationTitel));
                                                        dialog.setMessage(getResources().getString(R.string.txt_failedSynchronisationDetails));
                                                        dialog.show();
                                                    }
                                                }
                                                catch (Exception ec)
                                                {
                                                }
                                            }
                                        }).execute();
                            }

                            @Override
                            public void onFailed()
                            {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(WelcomeActivity.this);
                                dialog.setTitle(getResources().getString(R.string.txt__alertRegister));
                                dialog.setMessage(getResources().getString(R.string.txt__alertNeverHappens));
                                dialog.show();
                            }
                        }).execute();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firebaseAnalytics.logEvent("WelcomeActivity_LOGIN", null);
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
            }
        });

        new BubbleShowCaseBuilder(WelcomeActivity.this)
                .title(getResources().getString(R.string.txt_SpotLightSkipButtonTutorial)) //Any title for the bubble view
                .backgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryChat))
                .showOnce("D3")
                .targetView(textViewSkip)
                .show();
    }
}
