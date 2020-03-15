package esaph.spotlight.navigation.spotlight.Moments;

import androidx.annotation.WorkerThread;

public interface DataBaseLoadWaiter
{
    @WorkerThread
    public void onNoDataAvaiable();
}
