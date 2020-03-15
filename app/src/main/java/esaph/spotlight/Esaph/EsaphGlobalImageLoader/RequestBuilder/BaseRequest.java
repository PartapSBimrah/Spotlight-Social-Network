package esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder;

import android.widget.ProgressBar;

import java.util.concurrent.locks.ReentrantLock;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.Callback.EsaphGlobalDownloadListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ViewAware;

public abstract class BaseRequest
{
    public final ReentrantLock reentrantLock;
    public final ViewAware viewAware;
    public final String Folder;
    public final String PREFIX;
    public final String OBJECT_ID;
    public final ProgressBar progressBar;
    public final EsaphGlobalDownloadListener esaphGlobalDownloadListener;

    public BaseRequest(ReentrantLock reentrantLock,
                       ViewAware viewAware,
                       String Folder,
                       String PID,
                       String PREFIX,
                       ProgressBar progressBar,
                       EsaphGlobalDownloadListener esaphGlobalDownloadListener)
    {
        this.reentrantLock = reentrantLock;
        this.viewAware = viewAware;
        this.Folder = Folder;
        this.PREFIX = PREFIX;
        this.esaphGlobalDownloadListener = esaphGlobalDownloadListener;
        this.OBJECT_ID = PID;
        this.progressBar = progressBar;
    }
}
