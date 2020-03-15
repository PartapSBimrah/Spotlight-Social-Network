package esaph.spotlight.einstellungen;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCloudStorageOptionView.EsaphStorageCloudOptionsView;
import esaph.spotlight.R;

public class SpotLightStoragePreferences extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private CLPreferences preferences;
    private EsaphStorageCloudOptionsView esaphStorageCloudOptionsView;
    private TextView textViewCurrentChoose;
    private SeekBar seekBar;

    @Override
    protected void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(SpotLightStoragePreferences.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotlight_storage_preferences);
        preferences = new CLPreferences(getApplicationContext());

        esaphStorageCloudOptionsView = (EsaphStorageCloudOptionsView) findViewById(R.id.esaphBackUpView);
        textViewCurrentChoose = (TextView) findViewById(R.id.textViewCurrentChoosenMaxLimit);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                textViewCurrentChoose.setText(getResources().getStringArray(R.array.spotlight_backup_options)[progress]);
                preferences.setSpotLightDiskSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        seekBar.setProgress(preferences.getSpotLightDiskSize());

        esaphStorageCloudOptionsView.showStatistics(getResources().getString(R.string.txt_storage_usage), "1",200, Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888));

        /*
        SQLChats sqlChats = new SQLChats(getApplicationContext());
        ConversationMessage conversationMessage = sqlChats.getLastConversationMessageOnlyImageAndVideo();
        if(conversationMessage != null)
        {
            new EsaphGlobalImageLoader(getApplicationContext())
                    .displayImage(StorageHandler.FOLDER__SPOTLIGHT,
                            conversationMessage.getIMAGE_ID(),
                            esaphStorageCloudOptionsView.getEsaphCircleImageView(),
                            conversationMessage.getType(),
                            new ProgressBar(getApplicationContext()),
                            new EsaphDimension(esaphStorageCloudOptionsView.getEsaphCircleImageView().getWidth(),
                                    esaphStorageCloudOptionsView.getEsaphCircleImageView().getHeight()),
                            null,
                            EsaphImageLoaderDisplayingAnimation.BLINK,
                            R.drawable.ic_no_image_circle);
        }
        sqlChats.close();*/


    }
}
