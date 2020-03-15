package esaph.spotlight.Esaph.EsaphAudioRecorder;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

public class EsaphAudioRecorderSampler
{
    private long startMillis;
    private MediaRecorder mediaRecorder;
    private boolean mIsRecording;
    private CalculateVolumeListener mVolumeListener;
    private Handler handler = new Handler();

    private final Runnable updater = new Runnable()
    {
        @Override
        public void run()
        {
            handler.postDelayed(this, 1);
            int maxAmplitude = (int) (mediaRecorder.getMaxAmplitude() * 0.01);

            if(maxAmplitude > 0)
            {
                if(maxAmplitude < 100)
                {
                    for (int i = 0; i < mVisualizerViews.size(); i++)
                    {
                        mVisualizerViews.get(i).receive(maxAmplitude);
                    }
                }
            }
        }
    };

    private List<EsaphAudioRecorderVisualizerView> mVisualizerViews = new ArrayList<>();

    public EsaphAudioRecorderSampler()
    {
        initAudioRecord();
    }

    public void link(EsaphAudioRecorderVisualizerView visualizerView)
    {
        mVisualizerViews.add(visualizerView);
    }

    public void setVolumeListener(CalculateVolumeListener volumeListener)
    {
        mVolumeListener = volumeListener;
    }

    public boolean isRecording()
    {
        return mIsRecording;
    }

    private void initAudioRecord()
    {
        mediaRecorder = new MediaRecorder();
    }

    private FileDescriptor fileDescriptor = null;
    public void startRecording(final FileDescriptor fileDescriptor)
    {
        EsaphAudioRecorderSampler.this.fileDescriptor = fileDescriptor;
        mIsRecording = true;

        try
        {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(fileDescriptor);
            mediaRecorder.prepare();
            mediaRecorder.start();
            startMillis = System.currentTimeMillis();
            handler.post(updater);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "startRecording() failed: " + ec);
        }
    }

    public long stopRecording()
    {
        if (mediaRecorder != null && mIsRecording)
        {
            try
            {
                mediaRecorder.stop();
                mediaRecorder.release();
            }
            catch (Exception ec)
            {

            }

            mediaRecorder = null;
            //No initalization needed, object is recreated every time.
        }

        mIsRecording = false;
        handler.removeCallbacks(updater);
        fileDescriptor = null;


        if (mVisualizerViews != null && !mVisualizerViews.isEmpty())
        {
            for (int i = 0; i < mVisualizerViews.size(); i++)
            {
                mVisualizerViews.get(i).receive(0);
            }
        }

        return System.currentTimeMillis() - startMillis;
    }

    public interface CalculateVolumeListener
    {
        void onCalculateVolume(int volume);
    }


    public long getStartMillis() {
        return startMillis;
    }
}
