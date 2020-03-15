package esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class ViewAware implements ImageAware
{
	protected Reference<View> viewRef;
	protected boolean checkActualViewSize;

	/**
	 * Constructor. <br />
	 * References {@link #ViewAware(View, boolean) VideoViewAware(imageViewAware, true)}.
	 *
	 * @param view {@link View View} to work with
	 */
	public ViewAware(View view) {
		this(view, true);
	}

	/**
	 * Constructor
	 *
	 * @param view                {@link View View} to work with
	 * @param checkActualViewSize <b>true</b> - then {@link #getWidth()} and {@link #getHeight()} will check actual
	 *                            size of View. It can cause known issues like
	 *                            <a href="https://github.com/nostra13/Android-Universal-Image-Loader/issues/376">this</a>.
	 *                            But it helps to save memory because memory cache keeps bitmaps of actual (less in
	 *                            general) size.
	 *                            <p/>
	 *                            <b>false</b> - then {@link #getWidth()} and {@link #getHeight()} will <b>NOT</b>
	 *                            consider actual size of View, just layout parameters. <br /> If you set 'false'
	 *                            it's recommended 'android:layout_width' and 'android:layout_height' (or
	 *                            'android:maxWidth' and 'android:maxHeight') are set with concrete values. It helps to
	 *                            save memory.
	 */
	public ViewAware(View view, boolean checkActualViewSize) {
		if (view == null) throw new IllegalArgumentException("view must not be null");

		this.viewRef = new WeakReference<View>(view);
		this.checkActualViewSize = checkActualViewSize;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Width is defined by target {@link View view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm (go by steps until get non-zero value):<br />
	 * 1) Get the actual drawn <b>getWidth()</b> of the View<br />
	 * 2) Get <b>layout_width</b>
	 */
	@Override
	public int getWidth() {
		View view = viewRef.get();
		if (view != null) {
			return view.getWidth();
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Height is defined by target {@link View view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm (go by steps until get non-zero value):<br />
	 * 1) Get the actual drawn <b>getHeight()</b> of the View<br />
	 * 2) Get <b>layout_height</b>
	 */
	@Override
	public int getHeight() {
		View view = viewRef.get();
		if (view != null) {
			return view.getHeight();
		}
		return 0;
	}

	@Override
	public View getWrappedView() {
		return viewRef.get();
	}

	@Override
	public boolean isCollected() {
		return viewRef.get() == null;
	}

	@Override
	public int getId() {
		View view = viewRef.get();
		return view == null ? super.hashCode() : view.hashCode();
	}

	@Override
	public boolean setImageDrawable(Drawable drawable) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			View view = viewRef.get();
			if (view != null) {
				setImageDrawableInto(drawable, view);
				return true;
			}
		} else {
		}
		return false;
	}

	@Override
	public boolean setImageBitmap(Bitmap bitmap) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			View view = viewRef.get();
			if (view != null) {
				setImageBitmapInto(bitmap, view);
				return true;
			}
		} else {
		}
		return false;
	}

	/**
	 * Should set drawable into incoming view. Incoming view is guaranteed not null.<br />
	 * This method is called on UI thread.
	 */
	protected abstract void setImageDrawableInto(Drawable drawable, View view);

	/**
	 * Should set Bitmap into incoming view. Incoming view is guaranteed not null.< br />
	 * This method is called on UI thread.
	 */
	protected abstract void setImageBitmapInto(Bitmap bitmap, View view);
}
