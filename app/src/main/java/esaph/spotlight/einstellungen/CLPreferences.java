/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.einstellungen;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class CLPreferences
{
    private static final String pref_username = "Username";
    private static final String pref_uid = "UID";
    private static final String pref_vorname = "ObjectC";
    private static final String pref_nachname = "ObjectD";
    private static final String pref_password = "Object";
    private static final String pref_countFriends = "CountFriends";
    private static final String pref_countGroupChat = "CountGroupChat";
    private static final String pref_countPersonal = "CountPersonal";
    private static final String pref_cam_orientation = "CamOrientation";
    private static final String preferenceID = "PR";
    private static final String pref_lastSelectedLocation = "LSL";
    private static final String pref_FcmToken = "FSAW"; //FCM TOKEN
    private static final String pref_newPinStatus = "PINSTATUS";
    private static final String pref_validAktuelleMomentHash = "VHAM";
    private static final String pref_validSavedMomentHash = "VHSM";
    private static final String pref_synchronized = "SYNC";
    private static final String pref_last_loggedUser = "LLU";
    private static final String pref_advertisment = "LastLoginTime";
    private static final String pref_flashOption = "FlashOption";
    private static final String pref_tutorial = "Tutorial";
    private static final String pref_tutorial_saving = "TutorialSaving";
    private static final String pref_demoModeEnable = "DM";
    private static final String pref_haptic_feedback = "HFB";
    private static final String pref_spotlight_disk_size = "SDS";
    private static final String pref_LastDescriptionsSpot = "LDSS";

    private static final String pref_camWidth = "prCW";
    private static final String pref_camHeight = "prCH";

    private static final String pass = "9E852B9FD9285BADE0AF5A77DFB870FA";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    public CLPreferences(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences(preferenceID, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }


    public void setUsername(String Username)
    {
        editor.putString(pref_username, Username);
        editor.apply();
    }

    public boolean displaySharingDialog()
    {
        return preferences.getBoolean(pref_tutorial, true);
    }

    public void setShouldDisplaySharingDialog(boolean need)
    {
        editor.putBoolean(pref_tutorial, need);
        editor.apply();
    }

    public void setTutorialSaving(boolean need)
    {
        editor.putBoolean(pref_tutorial_saving, need);
        editor.apply();
    }

    public boolean needTutorialSaving()
    {
        return preferences.getBoolean(pref_tutorial_saving, true);
    }

    /*
    public long isAdvertismentEnabled()
    {
        return preferences.getLong(pref_advertisment, 15422049L);
    }

    public void setAdvertismentGoalUpper()
    {
        editor.putLong(pref_advertisment, isAdvertismentEnabled() + 1);
        editor.apply();
    }

    public void setNewAdvertismentGoal(int werbunganzahl)
    {
        if(werbunganzahl < 20)
        {
            if(isAdvertismentEnabled() > 15422048L)
            {
                editor.putLong(pref_advertisment, 15422048L);
                editor.apply();
            }
            editor.putLong(pref_advertisment, isAdvertismentEnabled() - werbunganzahl);
            editor.apply();
        }
    }

    public void resetGoal()
    {
        editor.putLong(pref_advertisment, 15422048L);
        editor.apply();
    }
*/
    public void setFCMToken(String token)
    {
        try
        {
            if(!token.isEmpty() && !token.equals("NT"))
            {
                EncryptUtils encryptUtils = new EncryptUtils();
                editor.putString(pref_FcmToken, Arrays.toString(encryptUtils.encryptMsg(token, encryptUtils.generateKey(pass))));
                editor.apply();
            }
            else
            {
                editor.remove(pref_FcmToken);
                editor.apply();
            }

        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Cant set FCM Token: " + ec);
        }
    }



    public String getFCMToken()
    {
        try
        {
            EncryptUtils utils = new EncryptUtils();

            String stringArray = preferences.getString(pref_FcmToken, "NT");

            if (!stringArray.isEmpty() && !stringArray.equals("NT")) {
                String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
                byte[] array = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    array[i] = Byte.parseByte(split[i]);
                }
                return utils.decryptMsg(array, utils.generateKey(pass));
            }


            return "NT";
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getFCMToken() in preferences failed: " + ec);
            return "NT";
        }
    }

    public boolean isDemoMode()
    {
        return preferences.getBoolean(pref_demoModeEnable, false);
    }

    public synchronized void setUpUser(long UID, String username, String password, boolean isDemoMode)
    {
        editor.putBoolean(pref_demoModeEnable, isDemoMode);
        editor.putLong(pref_uid, UID);
        editor.putString(pref_username, username);
        setPassword(password, username);
        editor.apply();
    }

    public void setPassword(String Password, String Username)
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        try
        {
            editor.putString(pref_password, Arrays.toString(encryptUtils.encryptMsg(Password, encryptUtils.generateKey(pass))));
            editor.putString(pref_last_loggedUser, Username);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "setUpUserError: " + ec);
        }
    }

    public void logOut()
    {
        editor.remove(pref_demoModeEnable);
        editor.remove(pref_tutorial);
        editor.remove(pref_username);
        editor.remove(pref_uid);
        editor.remove(pref_vorname);
        editor.remove(pref_spotlight_disk_size);
        editor.remove(pref_nachname);
        editor.remove(pref_password);
        editor.remove(pref_countFriends);
        editor.remove(pref_countGroupChat);
        editor.remove(pref_countPersonal);
        editor.remove(pref_cam_orientation);
        editor.remove(preferenceID);
        editor.remove(pref_lastSelectedLocation);
        editor.remove(pref_FcmToken);
        editor.remove(pref_validAktuelleMomentHash);
        editor.remove(pref_validSavedMomentHash);
        editor.remove(pref_synchronized);
        editor.remove(pref_advertisment);
        editor.apply();
    }

    public void setNeedSynchronisation()
    {
        editor.putBoolean(pref_synchronized, false);
        editor.apply();
    }

    public void setAccountIsSynchronized()
    {
        editor.putBoolean(pref_synchronized, true);
        editor.apply();
    }

    public boolean isAccountSynchronized()
    {
        return this.preferences.getBoolean(pref_synchronized, false);
    }

    public String getLastLoggedName()
    {
        return this.preferences.getString(pref_last_loggedUser, "");
    }

    public int getCamOrientation()
    {
        return this.preferences.getInt(pref_cam_orientation, 0);
    }

    public void setCamOrientation(int cori)
    {
        this.editor.putInt(pref_cam_orientation, cori);
        this.editor.apply();
    }

    public String getUsername()
    {
        return this.preferences.getString(pref_username, "");
    }

    public long getUID()
    {
        return this.preferences.getLong(pref_uid, -1);
    }

    public String getPasswordEncrypted()
    {
        EncryptUtils encryptUtils = new EncryptUtils();
        try
        {
            String stringArray = this.preferences.getString(pref_password, "");

            if (stringArray != null && !stringArray.isEmpty()) {
                String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
                byte[] array = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    array[i] = Byte.parseByte(split[i]);
                }

                return encryptUtils.decryptMsg(array, encryptUtils.generateKey(pass));
            }

            return "";
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "getPasswordEncrypted() in preferences failed(): " + ec);
            return null;
        }
    }

    public String getVorname()
    {
        return this.preferences.getString(pref_vorname, "");
    }

    public String getNachname()
    {
        return this.preferences.getString(pref_nachname, "");
    }

    public void setNewFlashOption(String newFlashOption)
    {
        this.editor.putString(pref_flashOption, newFlashOption);
        this.editor.apply();
    }

    public String getFlashOption()
    {
        return this.preferences.getString(pref_flashOption, Camera.Parameters.FLASH_MODE_OFF);
    }

    public void setNewHapticFeedBack(boolean newHapticFeedBack)
    {
        this.editor.putBoolean(pref_haptic_feedback, newHapticFeedBack);
        this.editor.apply();
    }

    public boolean getHapticFeedBack()
    {
        return this.preferences.getBoolean(pref_haptic_feedback, true);
    }

    public void setSpotLightDiskSize(int diskSize)
    {
        this.editor.putInt(pref_spotlight_disk_size, diskSize);
        this.editor.apply();
    }

    public int getSpotLightDiskSize()
    {
        return this.preferences.getInt(pref_spotlight_disk_size, SpotLightMaxStorageSize.SIZE_UNLIMITED);
    }

    public void setLastEditedDescription(JSONObject jsonObject)
    {
        this.editor.putString(pref_LastDescriptionsSpot, jsonObject.toString());
        this.editor.apply();
    }

    public JSONObject getLastEditedDescription() throws JSONException
    {
        return new JSONObject(this.preferences.getString(pref_LastDescriptionsSpot, new JSONObject().toString()));
    }

    public void setCamWidth(int width)
    {
        this.editor.putInt(pref_camWidth, width);
        this.editor.apply();
    }

    public int getCamWidth()
    {
        return this.preferences.getInt(pref_camWidth, 0);
    }


    public void setCamHeight(int height)
    {
        this.editor.putInt(pref_camHeight, height);
        this.editor.apply();
    }

    public int getCamHeight()
    {
        return this.preferences.getInt(pref_camHeight, 0);
    }
}
