package esaph.spotlight;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.einstellungen.SpotLightMaxStorageSize;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class AppStartPutzKolonne implements Runnable
{
    private Context context;
    public AppStartPutzKolonne(Context context)
    {
        this.context = context;
    }

    private long getTimeMinus24Hours()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        return calendar.getTimeInMillis();
    }

    @Override
    public void run()
    {
        try
        {
            StorageHandlerProfilbild.dropCacheFolder(this.context);
            StorageHandler.dropTempFiles(this.context);

            SQLUploads sqlUploads = new SQLUploads(this.context); //Hashtag handling is there.
            sqlUploads.removePreparedPostsFromDataBaseAndStorage();
            sqlUploads.close();

            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(this.context);
            sqlLifeCloud.removePostsPassedDeadline(getTimeMinus24Hours());
            sqlLifeCloud.close();

            SQLChats sqlChats = new SQLChats(this.context); //Hashtag handling is there too.
            sqlChats.removePostsPassedDeadline(getTimeMinus24Hours()); //Removing saved pictures too.
            sqlChats.close();

            SQLGroups sqlMemorys = new SQLGroups(this.context);
            sqlMemorys.removePostsPassedDeadline(getTimeMinus24Hours());
            sqlMemorys.close();

            File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "." + this.context.getResources().getString(R.string.app_name) + File.separator + "UC" + File.separator + "Current"
                    + File.separator);

            if (fileDir.isDirectory())
            {
                String[] children = fileDir.list();
                if(children != null)
                {
                    for (int i = 0; i < children.length; i++)
                    {
                        new File(fileDir, children[i]).delete();
                    }
                }
            }

            freeDiskByLimit();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Die Putzkolonne hat keine lust mehr decissioooon but i want it all, i wanna eat the whole cake: " + ec);
        }
    }


    private void freeDiskByLimit()
    {
        CLPreferences preferences  = new CLPreferences(context);
        int mode = preferences.getSpotLightDiskSize();

        if(mode >= SpotLightMaxStorageSize.SIZE_50_MB) //50mb
        {
            int round = 0;
            long CURRENT_SIZE = StorageHandler.getSizeOfFolder(new File(context.getFilesDir(), StorageHandler.FOLDER__SPOTLIGHT));
            long CURRENT_SIZE_LIMIT = SpotLightMaxStorageSize.getAsBytes(mode);
            SQLChats sqlChatsRemove = new SQLChats(context);
            System.out.println("HANDLING DISK SIZE current Size Limit: " + CURRENT_SIZE_LIMIT);

            while(CURRENT_SIZE > CURRENT_SIZE_LIMIT) //Querying database.
            {
                System.out.println("HANDLING DISK SIZE round: " + round);
                List<ConversationMessage> list = sqlChatsRemove.getImagesAndVidsBackOldest(round);
                round += list.size();

                for(int counter = 0; counter < list.size(); counter++) //Delete image and video data.
                {
                    CURRENT_SIZE -= StorageHandler.removeImageData(StorageHandler.FOLDER__SPOTLIGHT,
                            context,
                            list.get(counter).getIMAGE_ID());
                    System.out.println("HANDLING DISK SIZE current Disk size: " + CURRENT_SIZE);
                }
            }

            sqlChatsRemove.close();
        }
    }
}
