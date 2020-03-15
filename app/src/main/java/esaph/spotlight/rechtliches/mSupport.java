/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.rechtliches;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import esaph.spotlight.R;

public class mSupport extends AppCompatActivity
{
    private static final String support_link = "https://esaph.jimdo.com/kontakt/";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_support);

        WebView webView = (WebView) findViewById(R.id.supportWebView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(mSupport.support_link);
    }
}
