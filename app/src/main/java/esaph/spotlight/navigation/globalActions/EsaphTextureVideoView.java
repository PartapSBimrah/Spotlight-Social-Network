package esaph.spotlight.navigation.globalActions;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.FileDescriptor;
import java.io.IOException;

public class EsaphTextureVideoView extends FrameLayout implements TextureView.SurfaceTextureListener
{
    // Indicate if logging is on
    public static final boolean LOG_ON = true;

    // Log tag
    private static final String TAG = EsaphTextureVideoView.class.getName();

    private MediaPlayer mMediaPlayer;

    private float mVideoHeight;
    private float mVideoWidth;

    private boolean mIsDataSourceSet;
    private boolean mIsViewAvailable;
    private boolean mIsVideoPrepared;
    private boolean mIsPlayCalled;

    private ScaleType mScaleType;
    private State mState;
    private TextureView mTextureView;

    public enum ScaleType
    {
        CENTER_CROP, TOP, BOTTOM
    }

    public enum State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    public EsaphTextureVideoView(Context context) {
        super(context);
        if (!isInEditMode()) {
            initView();
        }
    }

    public EsaphTextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initView();
        }
    }

    public EsaphTextureVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            initView();
        }
    }

    private void initView() {
        initPlayer();
        mTextureView = new TextureView(getContext());
        removeAllViews();
        addView(mTextureView);
        setScaleType(ScaleType.CENTER_CROP);
        mTextureView.setSurfaceTextureListener(this);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    private void updateTextureViewSize() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (mVideoWidth >= viewWidth && mVideoHeight >= viewHeight) {
            scaleX = mVideoWidth / viewWidth;
            scaleY = mVideoHeight / viewHeight;
        } else if (mVideoWidth <= viewWidth && mVideoHeight <= viewHeight) {
            scaleY = viewWidth / mVideoWidth;
            scaleX = viewHeight / mVideoHeight;
        } else if (viewWidth >= mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
        } else if (viewHeight >= mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
        }

        // Calculate pivot points, in our case crop from center
        int pivotPointX;
        int pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = (int) (viewWidth);
                pivotPointY = (int) (viewHeight);
                break;
            case CENTER_CROP:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
            default:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);
        mTextureView.setTransform(matrix);
    }

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mIsVideoPrepared = false;
        mIsPlayCalled = false;
        mState = State.UNINITIALIZED;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(String)
     */
    public void setDataSource(String path) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(path);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setDataSource(FileDescriptor afd)
    {
        Log.d(TAG, "setDataSource");
        initPlayer();
        try
        {
            mMediaPlayer.setDataSource(afd);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(android.content.Context, android.net.Uri)
     */
    public void setDataSource(Context context, Uri uri) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(context, uri);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(java.io.FileDescriptor)
     */
    public void setDataSource(AssetFileDescriptor afd) {
        initPlayer();

        try {
            long startOffset = afd.getStartOffset();
            long length = afd.getLength();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), startOffset, length);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void prepare() {
        try {
            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mVideoWidth = width;
                            mVideoHeight = height;
                            updateTextureViewSize();
                        }
                    }
            );
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mState = State.END;
                    log("Video has ended.");

                    if (mListener != null) {
                        mListener.onVideoEnd();
                    }
                }
            });

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mIsVideoPrepared = true;
                    if (mIsPlayCalled && mIsViewAvailable) {
                        log("Player is prepared and play() was called.");
                        play();
                    }

                    if (mListener != null) {
                        mListener.onVideoPrepared();
                    }
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    return false;
                }
            });

            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.


        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void play() {
        play(0);
    }

    public void play(int startPosition)
    {
        if(mState == State.END)
        {
            initPlayer();
        }

        if (!mIsDataSourceSet) {
            log("play() was called but data source was not set.");
            return;
        }

        mIsPlayCalled = true;

        if (!mIsVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.");
            return;
        }

        if (!mIsViewAvailable) {
            log("play() was called but view is not available yet, waiting.");
            return;
        }

        if (mState == State.PLAY) {
            log("play() was called but video is already playing.");
            return;
        }

        if (mState == State.PAUSE) {
            log("play() was called but video is paused, resuming.");
            mState = State.PLAY;
            mMediaPlayer.start();
            return;
        }

        mState = State.PLAY;
        mMediaPlayer.seekTo(startPosition);
        mMediaPlayer.start();
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (mState == State.PAUSE) {
            log("pause() was called but video already paused.");
            return;
        }

        if (mState == State.STOP) {
            log("pause() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("pause() was called but video already ended.");
            return;
        }

        mState = State.PAUSE;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    public void pauseAndSeekToZero()
    {
        stopFully();
    }

    public void stopFully()
    {
        if (mState == State.STOP) {
            log("pauseAndSeekToZero() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("pauseAndSeekToZero() was called but video already ended.");
            return;
        }

        mState = State.END;
        mMediaPlayer.stop();
        mMediaPlayer.release();

        mIsVideoPrepared = false;
        mMediaPlayer = null;
    }

    /**
     * @see android.media.MediaPlayer#setLooping(boolean)
     */
    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    /**
     * @see android.media.MediaPlayer#seekTo(int)
     */
    public void seekTo(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    /**
     * @see android.media.MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    /**
     * @see android.media.MediaPlayer#getCurrentPosition()
     */
    public int getCurrentPosition() { return mMediaPlayer.getCurrentPosition(); }

    static void log(String message) {
        if (LOG_ON) {
            Log.d(TAG, message);
        }
    }

    private MediaPlayerListener mListener;

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
     */
    public void setListener(MediaPlayerListener listener) {
        mListener = listener;
    }

    public interface MediaPlayerListener {

        public void onVideoPrepared();

        public void onVideoEnd();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);
        mIsViewAvailable = true;
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
            log("View is available and play() was called.");
            play();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
