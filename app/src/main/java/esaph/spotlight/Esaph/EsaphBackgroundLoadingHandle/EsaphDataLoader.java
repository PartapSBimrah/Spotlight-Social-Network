package esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.WorkerThread;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public abstract class EsaphDataLoader implements Runnable
{
    private Handler handlerUI = new Handler(Looper.getMainLooper());
    private SoftReference<Context> weakReferenceContext;
    private SoftReference<DataBaseLoadWaiter> waiterSoftReference;
    private SoftReference<EsaphMomentsRecylerView> esaphMomentsRecylerViewSoftReference;
    private AtomicBoolean atomicBoolean;

    public EsaphDataLoader(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView, AtomicBoolean atomicBoolean)
    {
        this.weakReferenceContext = new SoftReference<Context>(context);
        this.waiterSoftReference = new SoftReference<DataBaseLoadWaiter>(dataBaseLoadWaiter);
        this.esaphMomentsRecylerViewSoftReference = new SoftReference<EsaphMomentsRecylerView>(esaphMomentsRecylerView);
        this.atomicBoolean = atomicBoolean;
    }

    public int[] getStartFrom()
    {
        return esaphMomentsRecylerViewSoftReference.get().getObjectCounts();
    }

    public SoftReference<Context> getSoftReferenceContext()
    {
        return weakReferenceContext;
    }

    @Override
    public void run()
    {
        handlerUI.post(new Runnable() {
            @Override
            public void run() {
                EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewSoftReference.get();
                if(esaphMomentsRecylerView != null)
                {
                    esaphMomentsRecylerView.addFooter();
                }
            }
        });
    }

    @WorkerThread
    public void publish(final List<Object> list)
    {
        handlerUI.post(new Runnable()
        {
            @Override
            public void run()
            {
                EsaphMomentsRecylerView esaphMomentsRecylerView = esaphMomentsRecylerViewSoftReference.get();
                if(esaphMomentsRecylerView != null)
                {
                    esaphMomentsRecylerView.removeFooter();
                }

                if(list.isEmpty())
                {
                    DataBaseLoadWaiter dataBaseLoadWaiter = waiterSoftReference.get();
                    if(dataBaseLoadWaiter != null)
                    {
                        dataBaseLoadWaiter.onNoDataAvaiable();
                    }
                }
                else
                {
                    if(esaphMomentsRecylerView != null)
                    {
                        esaphMomentsRecylerView.pushNewDataInAdapter(list);
                    }
                }

                atomicBoolean.set(false);
            }
        });
    }
}
