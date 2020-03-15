package esaph.spotlight;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class EsaphSSLSocket
{
    public static SSLSocket getSSLInstance(Context context, String ServerAddress, int ServerPort)
    {
        try
        {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            InputStream keyStoreStream = context.getResources().openRawResource(R.raw.client);
            keyStore.load(keyStoreStream, new SocketResources().getSSLKeyStorePass().toCharArray());

            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.clienttruststore);
            trustStore.load(trustStoreStream, new SocketResources().getSSLTrustStorePass().toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, new SocketResources().getSSLKeyStorePass().toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            sslContext.init(kmf.getKeyManagers(), trustManagers, null);

            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket sslSocket = ((SSLSocket)factory.createSocket(ServerAddress, ServerPort));
            sslSocket.setSoTimeout(10000);
            return sslSocket;
        }
        catch (Exception ec)
        {
            Log.i("EsaphSSLSocket", "EsaphSSLSocket getSSLInstance failed: " + ec);
            return null;
        }
    }
}

