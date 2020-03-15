package esaph.spotlight.Esaph.EsaphGlobalImageLoader;

import android.content.Context;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageAware;
import esaph.spotlight.StorageManagment.StorageHandler;

public class ImageLoaderEngine
{
	private Executor taskExecutor;
	private Executor taskExecutorForCachedImages;
	private Executor taskDistributor;

	private final Map<Integer, String> cacheKeysForImageAwares = Collections
			.synchronizedMap(new HashMap<Integer, String>());
	private final Map<String, ReentrantLock> idLocks = new WeakHashMap<String, ReentrantLock>();

	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final AtomicBoolean networkDenied = new AtomicBoolean(false);
	private final AtomicBoolean slowNetwork = new AtomicBoolean(false);

	private final Object pauseLock = new Object();
	private Context context;

	public ImageLoaderEngine(Context context)
	{
		this.context = context;
		this.taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
		this.taskExecutorForCachedImages = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
		this.taskDistributor = Executors.newSingleThreadExecutor();
	}

	void submit(final LoadingAndDisplayBase loadingAndDisplayBase, final EsaphDimension esaphDimension)
	{
		taskDistributor.execute(new Runnable()
		{
			@Override
			public void run()
			{
				File image = StorageHandler.getFile(context,
						loadingAndDisplayBase.getBaseRequestBuilder().Folder,
						loadingAndDisplayBase.getBaseRequestBuilder().OBJECT_ID,
						esaphDimension,
						loadingAndDisplayBase.getBaseRequestBuilder().PREFIX);

				boolean isImageCachedOnDisk = StorageHandler.fileExists(image);

				if (isImageCachedOnDisk)
				{
					taskExecutorForCachedImages.execute(loadingAndDisplayBase);
				}
				else {
					taskExecutor.execute(loadingAndDisplayBase);
				}
			}
		});
	}

	public String getLoadingUriForView(ImageAware imageAware) {
		return cacheKeysForImageAwares.get(imageAware.getId());
	}

	void prepareDisplayTaskFor(ImageAware imageAware, String memoryCacheKey) {
		cacheKeysForImageAwares.put(imageAware.getId(), memoryCacheKey);
	}

	void cancelDisplayTaskFor(ImageAware imageAware) {
		cacheKeysForImageAwares.remove(imageAware.getId());
	}

	void denyNetworkDownloads(boolean denyNetworkDownloads) {
		networkDenied.set(denyNetworkDownloads);
	}

	void handleSlowNetwork(boolean handleSlowNetwork) {
		slowNetwork.set(handleSlowNetwork);
	}

	void pause() {
		paused.set(true);
	}

	void resume() {
		paused.set(false);
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}

	void fireCallback(Runnable r) {
		taskDistributor.execute(r);
	}

	ReentrantLock getLockForID(String ID) {
		ReentrantLock lock = idLocks.get(ID);
		if (lock == null) {
			lock = new ReentrantLock();
			idLocks.put(ID, lock);
		}
		return lock;
	}

	AtomicBoolean getPause() {
		return paused;
	}

	Object getPauseLock() {
		return pauseLock;
	}

	boolean isNetworkDenied() {
		return networkDenied.get();
	}

	boolean isSlowNetwork() {
		return slowNetwork.get();
	}
}
