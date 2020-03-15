/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight;

import esaph.spotlight.einstellungen.EncryptUtils;

public class SocketResources
{
    private static final byte[] serverAddress = new byte[]{1, -43, -106, 103, 58, 113, 96, -35, -105, -112, 74, -8, 85, 37, 111, 104, 56, 29, 92, -85, -54, 61, 56, -38}; //esaph.de
    private static final byte[] serverAddressIP = new byte[]{}; //Ip
    private static final byte[] serverPortLRServer = new byte[]{85, -106, -59, 47, 35, 37, -108, 33, 52, 23, -83, 33, 73, -122, -84, 14, 59, -117, -39, 92}; //Login/Register
    private static final byte[] serverPortFServer = new byte[]{85, -106, -59, 46, 12, -69, 85, -82, 48, 22, 26, -23, 119, 33, -8, -70, -77, -11, 45, 36}; //Friend/Information Server
    private static final byte[] serverPortMsgServer = new byte[]{85, -106, -60, 39, 61, 18, 23, 93, 21, -83, -37, 94, 27, -24, -67, 34, 6, -120, 2, -94}; //Nachrichten Server
    private static final byte[] serverPortLifePostServer = new byte[]{85, -106, -60, 38, 18, -116, -42, -46, 17, -84, 108, -106, 37, 79, -23, -106, -114, -10, -10, -38}; //1031
    private static final byte[] serverPortLifeCloudServer = new byte[]{85, -106, -60, 37, 98, 47, -108, 67, 29, -82, -76, -50, 102, -90, 20, 75, 22, 117, -22, 82}; //1032

    private static final byte[] SSL_KEYSTOREPASS = new byte[]{81, -106, -107, 33, 98, 106, 98, -120, 50, 117, 3, 48, -123, 120, -50, 63, -70, 123, -113, 49, -50, -79, -61, 49, -33, 60};
    private static final byte[] SSL_TRUSTOREPASS = new byte[]{86, -98, -63, 38, 96, 31, 53, -115, 56, 39, 81, 69, -87, 62, -5, -98, 118, 6, -42, -5, 37, 82, -89, 85, -17, -20};

    protected static final String key = "duaafr23sc$456ASdhge!§hsthierwe";

    protected static String serverAddressEncrypted;
    protected static String serverAddressIPEncrypted;
    protected static int serverPortLRServerEncrypted = -1; //Login/Register
    protected static int serverPortFServerEncrypted = -1; //Friend/Information Server
    protected static int serverPortMsgServerEncrypted = -1; //Nachrichten Server
    protected static int serverPortLivePostServerEncrypted = -1; //1031
    protected static int serverPortLifeCloudServerEncrypted = -1; //1032

    protected static String SSL_KEYSTOREPASSEncrypted;
    protected static String SSL_TRUSTOREPASSEncrypted;

    public SocketResources()
    {
    }

/*
    public void printEncryptions()
    {
        try
        {
            EncryptUtils encryptUtils = new EncryptUtils();
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("esaph.de", encryptUtils.generateKey(SocketResources.key))));
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("1028", encryptUtils.generateKey(SocketResources.key))));
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("1029", encryptUtils.generateKey(SocketResources.key))));
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("1030", encryptUtils.generateKey(SocketResources.key))));
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("1031", encryptUtils.generateKey(SocketResources.key))));
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("1032", encryptUtils.generateKey(SocketResources.key))));

            System.out.println(Arrays.toString(encryptUtils.encryptMsg("50b605f02e", encryptUtils.generateKey(SocketResources.key)))); //key
            System.out.println(Arrays.toString(encryptUtils.encryptMsg("28612@1587", encryptUtils.generateKey(SocketResources.key)))); //trust
        }
        catch (Exception ec)
        {
            System.out.println("EX: " + ec);
        }
    }*/

    public String getServerAddress()
    {
        try
        {
            if(serverAddressEncrypted != null)
            {
                return serverAddressEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            String encrypted = encryptUtils.decryptMsg(serverAddress, encryptUtils.generateKey(key));
            serverAddressEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return "ERR";
        }
    }

    public String getSSLKeyStorePass() throws Exception
    {
        if(SSL_KEYSTOREPASSEncrypted != null)
        {
            return SSL_KEYSTOREPASSEncrypted;
        }

        EncryptUtils encryptUtils = new EncryptUtils();
        String encrypted = encryptUtils.decryptMsg(SocketResources.SSL_KEYSTOREPASS, encryptUtils.generateKey(SocketResources.key));
        SSL_KEYSTOREPASSEncrypted = encrypted;

        System.out.println("PASS KEYSTORE: " + SSL_KEYSTOREPASSEncrypted);

        return encrypted;
    }

    public String getSSLTrustStorePass() throws Exception
    {
        if(SSL_TRUSTOREPASSEncrypted != null)
        {
            return SSL_TRUSTOREPASSEncrypted;
        }

        EncryptUtils encryptUtils = new EncryptUtils();
        String encrypted = encryptUtils.decryptMsg(SocketResources.SSL_TRUSTOREPASS, encryptUtils.generateKey(SocketResources.key));
        SSL_TRUSTOREPASSEncrypted = encrypted;

        System.out.println("PASS: " + SSL_TRUSTOREPASSEncrypted);

        return encrypted;
    }

    public String getServerAdressIP()
    {
        try
        {
            if(serverAddressIPEncrypted != null)
            {
                return serverAddressIPEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            String encrypted = encryptUtils.decryptMsg(serverAddressIP, encryptUtils.generateKey(SocketResources.key));
            serverAddressIPEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return "ERR";
        }
    }

    public int getServerPortLRServer()
    {
        try
        {
            if(serverPortLRServerEncrypted > -1)
            {
                return serverPortLRServerEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            int encrypted = Integer.parseInt(encryptUtils.decryptMsg(serverPortLRServer, encryptUtils.generateKey(SocketResources.key)));
            serverPortLRServerEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return -1;
        }
    }

    public int getServerPortFServer()
    {
        try
        {
            if(serverPortFServerEncrypted > -1)
            {
                return serverPortFServerEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            int encrypted = Integer.parseInt(encryptUtils.decryptMsg(serverPortFServer, encryptUtils.generateKey(SocketResources.key)));
            serverPortFServerEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return -1;
        }
    }

    public int getServerPortMsgServer()
    {
        try
        {
            if(serverPortMsgServerEncrypted > -1)
            {
                return serverPortMsgServerEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            int encrypted = Integer.parseInt(encryptUtils.decryptMsg(serverPortMsgServer, encryptUtils.generateKey(SocketResources.key)));
            serverPortMsgServerEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return -1;
        }
    }

    public int getServerPortPServer()
    {
        try
        {
            if(serverPortLivePostServerEncrypted > -1)
            {
                return serverPortLivePostServerEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            int encrypted = Integer.parseInt(encryptUtils.decryptMsg(serverPortLifePostServer, encryptUtils.generateKey(SocketResources.key)));
            serverPortLivePostServerEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return -1;
        }
    }


    public int getServerPortLCServer()
    {
        try
        {
            if(serverPortLifeCloudServerEncrypted > -1)
            {
                return serverPortLifeCloudServerEncrypted;
            }

            EncryptUtils encryptUtils = new EncryptUtils();
            int encrypted = Integer.parseInt(encryptUtils.decryptMsg(serverPortLifeCloudServer, encryptUtils.generateKey(SocketResources.key)));
            serverPortLifeCloudServerEncrypted = encrypted;

            return encrypted;
        }
        catch (Exception ec)
        {
            return -1;
        }
    }
}
