package esaph.spotlight.navigation.spotlight.Chats;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.AdapterChats;
import esaph.spotlight.services.UploadService.UploadPost;

public class DialogShowPictureUploading extends Dialog
{
    private UploadPost uploadPostCurrent;
    private ProgressBar progressBarUploading;
    private ImageView imageViewExit;
    private ImageView imageViewDelete;
    private ImageView imageViewSaveOnPhone;
    private TextView textViewReceivers;
    private TextView textViewTryAgain;
    private SurfaceView surfaceView;
    private RoundedImageView roundedImageView;
    private AdapterChats adapterChats;
    private boolean surfaceUseable = false;
    private boolean invokedIsVisible = true;

    public DialogShowPictureUploading(Context context, UploadPost uploadPost, AdapterChats adapterChats)
    {
        super(context);
        if(context instanceof Activity)
        {
            setOwnerActivity((Activity) context);
        }
        this.uploadPostCurrent = uploadPost;
        this.adapterChats = adapterChats;
    }

    public void virtualCallResumeMediaPlayer()
    {
        checkIfSavedOnPhone();
        if(surfaceUseable && mMediaPlayer != null && !mMediaPlayer.isPlaying())
        {
            mMediaPlayer.setDisplay(surfaceView.getHolder());
            mMediaPlayer.start();
        }
    }

    public void virtualCallPauseMediaPlayer()
    {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mMediaPlayer != null)
        {
            if(mMediaPlayer.isPlaying())
            {
                mMediaPlayer.stop();
            }

            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private Bitmap bitmapLastPic = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_dialog_show_uploadpost_content);

        if(getWindow() != null)
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        imageViewExit = (ImageView) findViewById(R.id.imageViewCloseDialog);
        imageViewDelete = (ImageView) findViewById(R.id.imageViewDialogDelete);
        imageViewSaveOnPhone = (ImageView) findViewById(R.id.imageViewDialogSavePic);
        textViewReceivers = (TextView) findViewById(R.id.textViewDialogReceivers);
        textViewTryAgain = (TextView) findViewById(R.id.textViewDialogTryAgain);
        roundedImageView = (RoundedImageView) findViewById(R.id.imageViewDialogMainPreview);
        progressBarUploading = (ProgressBar) findViewById(R.id.progressBarDialogUploading);
        surfaceView = (SurfaceView) findViewById(R.id.textureViewDialogMainVideo);

        imageViewExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismiss();
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                surfaceUseable = true;
                try
                {
                    if(mMediaPlayer != null)
                    {
                        if(mMediaPlayer.isPlaying())
                        {
                            mMediaPlayer.stop();
                        }
                        mMediaPlayer.release();
                        mMediaPlayer = new MediaPlayer();
                    }

                    if(mainFileOfVideoCache != null)
                    {
                        surfaceView.requestFocus();
                        FileInputStream fis = new FileInputStream(mainFileOfVideoCache);
                        mMediaPlayer.setDataSource(fis.getFD());
                        mMediaPlayer.setLooping(true);
                        mMediaPlayer.setDisplay(surfaceView.getHolder());
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mMediaPlayer.prepareAsync();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                        {
                            @Override
                            public void onPrepared(MediaPlayer mp)
                            {
                                if(invokedIsVisible)
                                {
                                    setSurfaceViewRatio();
                                    mMediaPlayer.start();
                                }
                            }
                        });
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "prepareVideo(surfaceCreated()) failed: " + ec);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                surfaceUseable = false;
            }
        });

        if (progressBarUploading.getIndeterminateDrawable() != null)
        {
            progressBarUploading.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryChat), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        imageViewDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                SQLUploads sqlUploads = new SQLUploads(getContext());
                sqlUploads.removeSinglePostCompletly(uploadPostCurrent.getPID());
                sqlUploads.close();

                adapterChats.removeUploadPostItemByPID(uploadPostCurrent.getMESSAGE_ID());
                dismiss();
            }
        });


        imageViewSaveOnPhone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                savePictureOrVideo();
            }
        });


        textViewTryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                adapterChats.getUploadService().startNewUpload(uploadPostCurrent);
            }
        });


        SQLUploads sqlUploads = new SQLUploads(getContext());

        InputStream inputStream = null;
        try
        {
            if(uploadPostCurrent.getType() == (CMTypes.FPIC))
            {
                surfaceView.setVisibility(View.GONE);
                roundedImageView.setVisibility(View.VISIBLE);
                inputStream = new BufferedInputStream(new FileInputStream(sqlUploads.getFileToUploadHQ(uploadPostCurrent.getPID())));
            }
            else if(uploadPostCurrent.getType() == (CMTypes.FVID))
            {
                surfaceView.setVisibility(View.VISIBLE);
                roundedImageView.setVisibility(View.GONE);
                mainFileOfVideoCache = sqlUploads.getFileToUploadHQ(uploadPostCurrent.getPID());
            }

            bitmapLastPic = BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception ec)
        {
            dismiss();
        }
        finally
        {
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sqlUploads.close();
        }

        if(bitmapLastPic != null)
        {
            roundedImageView.setImageBitmap(bitmapLastPic);
        }

        textViewReceivers.setText(uploadPostCurrent.getPreviewForList());

        checkIfSavedOnPhone();
        prepareVideo();
    }


    private void setUploadAble()
    {
        progressBarUploading.setVisibility(View.GONE);
        textViewTryAgain.setClickable(true);
        textViewTryAgain.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryChat));
    }


    private void setUploading()
    {
        progressBarUploading.setVisibility(View.VISIBLE);
        textViewTryAgain.setClickable(false);
        textViewTryAgain.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkerGrey));
    }


    public void onPostFailedUpload(UploadPost uploadPost)
    {
        if(uploadPostCurrent.getPID().equals(uploadPost.getPID()))
        {
            setUploadAble();
        }
    }


    public void onPostUploading(String PID)
    {
        if(uploadPostCurrent.getPID().equals(PID))
        {
            setUploading();
        }
    }

    public void onPostUploadSuccess(UploadPost uploadPost, long PPID)
    {
        if(uploadPostCurrent.getMESSAGE_ID() == uploadPost.getMESSAGE_ID())
        {
            dismiss();
        }
    }


    private File mainFileOfVideoCache = null;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private void prepareVideo()
    {
        if(this.surfaceUseable && !mMediaPlayer.isPlaying())
        {
            Surface s = surfaceView.getHolder().getSurface();
            try
            {
                if(mMediaPlayer != null)
                {
                    mMediaPlayer.release();
                    mMediaPlayer = new MediaPlayer();
                }

                if(mainFileOfVideoCache != null)
                {
                    surfaceView.requestFocus();
                    mMediaPlayer.setDataSource(new FileInputStream(mainFileOfVideoCache).getFD());
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setDisplay(surfaceView.getHolder());
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            if(invokedIsVisible)
                            {
                                setSurfaceViewRatio();
                                mMediaPlayer.start();
                            }
                        }
                    });
                }

            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "prepareVideo() failed: " + ec);
            }
        }
    }


    private void checkIfSavedOnPhone()
    {
        File fileSavedLocation = null;
        if(uploadPostCurrent.getType() == (CMTypes.FPIC))
        {
            fileSavedLocation = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + getContext().getString(R.string.app_name)
                            + File.separator + getContext().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".jpg");
        }
        else if(uploadPostCurrent.getType() == (CMTypes.FVID))
        {
            fileSavedLocation = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + getContext().getString(R.string.app_name)
                            + File.separator + getContext().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".mp4");
        }

        if(fileSavedLocation != null && fileSavedLocation.exists())
        {
            stopSavingAnimation(true);
        }
    }



    public void savePictureOrVideo()
    {
        boolean success = false;
        try
        {
            System.out.println("MA DEBUG: " + mainFileOfVideoCache.getAbsolutePath());
            File fileDiRS = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + getOwnerActivity().getResources().getString(R.string.app_name));
            if(!fileDiRS.exists())
            {
                fileDiRS.mkdirs();
            }

            if(imageViewSaveOnPhone.getTag() != null && !imageViewSaveOnPhone.getTag().equals("F"))
            {
                if(imageViewSaveOnPhone.getTag().equals("C")) //Image saved, delete it again.
                {
                    if(uploadPostCurrent.getType() == (CMTypes.FPIC))
                    {
                        File fileSaveLocation = new File(
                                Environment.getExternalStorageDirectory()
                                        + File.separator + getOwnerActivity().getResources().getString(R.string.app_name)
                                        + File.separator + getOwnerActivity().getResources().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".jpg");

                        fileSaveLocation.delete();
                        getOwnerActivity().getContentResolver().delete(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.MediaColumns.DATA + "='" + fileSaveLocation.getAbsolutePath() + "'", null
                        );
                    }
                    else if(uploadPostCurrent.getType() == (CMTypes.FVID))
                    {
                        File fileVideo = new File(
                                Environment.getExternalStorageDirectory()
                                        + File.separator + getOwnerActivity().getResources().getString(R.string.app_name)
                                        + File.separator + getOwnerActivity().getResources().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".mp4");
                        fileVideo.delete();
                        getOwnerActivity().getContentResolver().delete(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.MediaColumns.DATA + "='" + fileVideo.getAbsolutePath() + "'", null
                        );
                    }

                    success = true;
                }
            }
            else
            {
                if(uploadPostCurrent.getType() == (CMTypes.FPIC))
                {
                    File fileSaveLocation = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator + getOwnerActivity().getResources().getString(R.string.app_name)
                                    + File.separator + getOwnerActivity().getResources().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".jpg");
                    if(fileSaveLocation.createNewFile())
                    {
                        FileOutputStream out = new FileOutputStream(fileSaveLocation);
                        this.bitmapLastPic.compress(Bitmap.CompressFormat.JPEG, EsaphGlobalValues.COMP_RATE_IMAGES, out);
                        out.close();

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATA, fileSaveLocation.getAbsolutePath());
                        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
                        getOwnerActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                        success = true;
                    }
                }
                else if(uploadPostCurrent.getType() == (CMTypes.FVID))
                {
                    File fileSaveLocation = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator + getOwnerActivity().getResources().getString(R.string.app_name)
                                    + File.separator + getOwnerActivity().getResources().getString(R.string.app_name) + "#" + uploadPostCurrent.getShootTime() + ".mp4");
                    if(fileSaveLocation.createNewFile())
                    {
                        FileInputStream fis = new FileInputStream(mainFileOfVideoCache);
                        try
                        {
                            OutputStream out = new FileOutputStream(fileSaveLocation);
                            try
                            {
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = fis.read(buf)) > 0)
                                {
                                    out.write(buf, 0, len);
                                }
                            }
                            finally
                            {
                                out.close();
                            }
                        }
                        finally
                        {
                            fis.close();
                        }

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Video.Media.DATA, fileSaveLocation.getAbsolutePath());
                        values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4");
                        getOwnerActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                        success = true;
                    }
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Konnte bild nicht speichern :(: " + ec);
            success = false;
        }
        finally
        {
            stopSavingAnimation(success);
        }
    }


    private void stopSavingAnimation(boolean success)
    {
        if(imageViewSaveOnPhone.getTag() == null)
        {
            if(success)
            {
                imageViewSaveOnPhone.setTag("C");
                Glide.with(getContext()).load(R.drawable.ic_image_saved_succesfully_to_gallery).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSaveOnPhone.setImageDrawable(resource);
                        }
                    }
                });
            }
            else
            {
                imageViewSaveOnPhone.setTag("F");
                Glide.with(getContext()).load(R.drawable.ic_save_image_failed).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSaveOnPhone.setImageDrawable(resource);
                        }
                    }
                });
            }
        }
        else
        {
            if(imageViewSaveOnPhone.getTag().equals("C"))
            {
                imageViewSaveOnPhone.setTag(null);
                Glide.with(getContext()).load(R.drawable.ic_save_picture).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSaveOnPhone.setImageDrawable(resource);
                        }
                    }
                });
            }
            else if(imageViewSaveOnPhone.getTag().equals("F"))
            {
                if(success)
                {
                    imageViewSaveOnPhone.setTag("C");
                    Glide.with(getContext()).load(R.drawable.ic_image_saved_succesfully_to_gallery).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                imageViewSaveOnPhone.setImageDrawable(resource);
                            }
                        }
                    });
                }
                else
                {
                    imageViewSaveOnPhone.setTag("F");
                    Glide.with(getContext()).load(R.drawable.ic_save_image_failed).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                imageViewSaveOnPhone.setImageDrawable(resource);
                            }
                        }
                    });
                }
            }
        }
    }


    private void setSurfaceViewRatio()
    {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = surfaceView.getWidth();
        int screenHeight = surfaceView.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        surfaceView.setLayoutParams(lp);
    }

}
