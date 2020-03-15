package esaph.spotlight.rechtliches;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import esaph.spotlight.R;

public class Haftungsauschluss extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haftungsauschluss);

        WebView webView = (WebView) findViewById(R.id.supportWebView);
        webView.loadUrl("file:///android_asset/Haftungsausschluss.html");

    }
}
