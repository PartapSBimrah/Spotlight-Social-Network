package esaph.spotlight.rechtliches;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import esaph.spotlight.R;

public class Impressum extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        WebView webView = (WebView) findViewById(R.id.supportWebView);
        webView.loadUrl("file:///android_asset/Impressum.html");
    }
}
