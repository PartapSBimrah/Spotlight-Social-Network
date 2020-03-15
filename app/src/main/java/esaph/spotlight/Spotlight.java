package esaph.spotlight;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class Spotlight extends Application
{
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        Spotlight application = (Spotlight) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }
}
