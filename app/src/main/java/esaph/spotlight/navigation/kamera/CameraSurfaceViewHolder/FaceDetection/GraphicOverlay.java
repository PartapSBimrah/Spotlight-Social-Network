package esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import esaph.spotlight.navigation.kamera.CameraSurfaceViewHolder.FaceDetection.OverLayEffect.OverLayEffect;

public class GraphicOverlay extends View
{
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0F;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0F;
    private int mFacing = 0;
    private Set<Graphic> mGraphics = new HashSet<Graphic>();
    private Set<View> mGraphicsView = new HashSet<View>();

    public GraphicOverlay(Context context) {
        super(context);
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphicOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public GraphicOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void clear() {
        Object var1 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.clear();
        }

        this.postInvalidate();
    }

    public void add(Graphic graphic) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.add(graphic);
        }

        this.postInvalidate();
    }

    public void remove(Graphic graphic) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.remove(graphic);
        }

        this.postInvalidate();
    }

    public void setCameraInfo(int previewWidth, int previewHeight, int facing)
    {
        Object var4 = this.mLock;
        synchronized(this.mLock)
        {
            this.mPreviewWidth = previewWidth;
            this.mPreviewHeight = previewHeight;
            this.mFacing = facing;
        }

        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Object var2 = this.mLock;
        synchronized(this.mLock)
        {
            if (this.mPreviewWidth != 0 && this.mPreviewHeight != 0)
            {
                this.mWidthScaleFactor = (float)canvas.getWidth() / (float)this.mPreviewWidth;
                this.mHeightScaleFactor = (float)canvas.getHeight() / (float)this.mPreviewHeight;
            }

            Iterator var3 = this.mGraphics.iterator();

            while(var3.hasNext())
            {
                Graphic graphic = (Graphic)var3.next();
                graphic.draw(GraphicOverlay.this, canvas);
            }
        }
    }

    public abstract static class Graphic
    {
        private GraphicOverlay mOverlay;

        public Graphic()
        {
        }

        public abstract void draw(GraphicOverlay graphicOverlay, Canvas canvas);

        public void setmOverlay(GraphicOverlay mOverlay)
        {
            this.mOverlay = mOverlay;
        }

        public float scaleX(float horizontal)
        {
            if(mOverlay == null) return 0;
            return horizontal * this.mOverlay.mWidthScaleFactor;
        }

        public float scaleY(float vertical)
        {
            if(mOverlay == null) return 0;

            return vertical * this.mOverlay.mHeightScaleFactor;
        }

        public float translateX(float x)
        {
            if(mOverlay == null) return 0;

            return this.mOverlay.mFacing == 1 ? (float)this.mOverlay.getWidth() - this.scaleX(x) : this.scaleX(x);
        }

        public float translateY(float y)
        {
            return this.scaleY(y);
        }

        public void postInvalidate()
        {
            if(mOverlay == null) return;
            this.mOverlay.postInvalidate();
        }

        public GraphicOverlay getmOverlay() {
            return mOverlay;
        }
    }
}