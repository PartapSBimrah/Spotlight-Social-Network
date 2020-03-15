package esaph.spotlight.rechtliches;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import esaph.spotlight.R;

public class Datenschutz extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datenschutz);
        WebView webView = (WebView) findViewById(R.id.supportWebView);
        webView.loadUrl("file:///android_asset/Datenschutzerklaerung.html");
    }
}
