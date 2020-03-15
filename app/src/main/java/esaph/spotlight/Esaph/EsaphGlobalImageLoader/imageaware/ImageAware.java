package esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface ImageAware {
	/**
	 * Returns width of image aware view. This value is used to define scale size for original image.
	 * Can return 0 if width is undefined.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	int getWidth();

	/**
	 * Returns height of image aware view. This value is used to define scale size for original image.
	 * Can return 0 if height is undefined.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	int getHeight();

	/**
	 * Returns wrapped Android {@link View View}. Can return <b>null</b> if no view is wrapped or view was
	 * collected by GC.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	View getWrappedView();

	/**
	 * Returns a flag whether image aware view is collected by GC or whatsoever. If so then ImageLoader pauseAndSeekToZero processing
	 * of task for this image aware view and fires
	 * View) ImageLoadingListener#onLoadingCancelled(String, View)} callback.<br />
	 * Mey be called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 *
	 * @return <b>true</b> - if view is collected by GC and ImageLoader should pauseAndSeekToZero processing this image aware view;
	 * <b>false</b> - otherwise
	 */
	boolean isCollected();

	/**
	 * Returns ID of image aware view. Point of ID is similar to Object's hashCode. This ID should be unique for every
	 * image view instance and should be the same for same instances. This ID identifies processing task in ImageLoader
	 * so ImageLoader won't process two image aware views with the same ID in one time. When ImageLoader get new task
	 * it cancels old task with this ID (if any) and starts new task.
	 * <p/>
	 * It's reasonable to return hash code of wrapped view (if any) to prevent displaying non-actual images in view
	 * because of view re-using.
	 */
	int getId();

	/**
	 * Sets image drawable into this image aware view.<br />
	 * Displays drawable in this image aware view
	 *Drawable) for empty Uri},
	 *Drawable) on loading} or
	 *Drawable) on loading fail}. These drawables can be specified in
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 *
	 * @return <b>true</b> if drawable was set successfully; <b>false</b> - otherwise
	 */
	boolean setImageDrawable(Drawable drawable);

	/**
	 * Sets image bitmap into this image aware view.<br />
	 * Displays loaded and decoded image {@link Bitmap} in this image view aware.
	 * Actually it's used only in
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 *
	 * @return <b>true</b> if bitmap was set successfully; <b>false</b> - otherwise
	 */
	boolean setImageBitmap(Bitmap bitmap);
}
