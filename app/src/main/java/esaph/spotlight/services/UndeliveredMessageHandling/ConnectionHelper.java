package esaph.spotlight.services.UndeliveredMessageHandling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHelper
{
    public static long lastNoConnectionTs = -1;
    public static boolean isOnline = true;

    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm =(ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    public static boolean isConnectedOrConnecting(Context context)
    {
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

}
