package esaph.spotlight.navigation.kamera.PostEditingFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerViewBASEFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.globalActions.EsaphTextureVideoView;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Editing.BottomMiddleViewCameraEditingVideo;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphTagFragment;
import esaph.spotlight.services.UploadService.UploadService;

public class CameraEditorVideo extends EsaphGlobalCommunicationFragment implements EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog
{
    private BottomMiddleViewCameraEditingVideo bottomMiddleViewCameraEditingVideo;
    private short currentPreviewMode;
    private EsaphTextureVideoView esaphTextureVideoView;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private SwipeNavigation swipeNavigation;

    private Bitmap bitmapLastPic;
    private long millisShootTime;
    private File fileVideoToDisplay = null;

    public static CameraEditorVideo getInstance(File file,
                                                long millisShootTime)
    {
        Bundle bundle = new Bundle();
        bundle.putLong("", millisShootTime);
        bundle.putSerializable("", file);
        CameraEditorVideo cameraEditor = new CameraEditorVideo();
        cameraEditor.setArguments(bundle);
        return cameraEditor;
    }

    public void show(long millisShootTime)
    {
        this.millisShootTime = millisShootTime;
        this.currentPreviewMode = CMTypes.FPIC;
        setMiddleViewEditing();
        handlePreviewMode();
    }

    public void show(File file,
                     long millisShootTime)
    {
        this.millisShootTime = millisShootTime;
        this.currentPreviewMode = CMTypes.FVID;
        this.fileVideoToDisplay = file;
        setMiddleViewEditing();
        handlePreviewMode();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
        {

        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof SwipeNavigation)
        {
            swipeNavigation = (SwipeNavigation) context;
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        System.out.println("Camera editor was destroy onDestroyView");
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pauseAndSeekToZero();
            esaphTextureVideoView = null;
        }

        esaphLockAbleViewPager = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(getClass().getName(), "onResume");

        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.play();
        }

        esaphLockAbleViewPager.paralyse();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            if(esaphTextureVideoView != null)
            {
                esaphTextureVideoView.pause();
            }
        }
        catch (Exception ec)
        {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_camera_editor_video, container, false);
        esaphTextureVideoView = (EsaphTextureVideoView) rootView.findViewById(R.id.videoViewMainCamera);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) swipeNavigation.findViewById(R.id.mainNavigationVerticalSwipeViewPager);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        bottomMiddleViewCameraEditingVideo = BottomMiddleViewCameraEditingVideo.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void handlePreviewMode()
    {
        esaphLockAbleViewPager.paralyse();
        if(currentPreviewMode == CMTypes.FVID)
        {
            displayVideo();
        }
        else if(currentPreviewMode == CMTypes.FPIC)
        {
            esaphTextureVideoView.setVisibility(View.GONE);
        }
    }

    private void removeRecordedVideo()
    {
        if(esaphTextureVideoView != null)
        {
            esaphTextureVideoView.pauseAndSeekToZero();
            esaphTextureVideoView.setVisibility(View.GONE);
        }

        if(fileVideoToDisplay != null && fileVideoToDisplay.exists())
        {
            fileVideoToDisplay.delete();
        }
        fileVideoToDisplay = null;
    }

    private void displayVideo()
    {
        try
        {
            System.out.println("DATA SOURCE VIDEO: " + fileVideoToDisplay.getAbsolutePath());

            Uri uri = Uri.parse(fileVideoToDisplay.getAbsolutePath());
            esaphTextureVideoView.setScaleType(EsaphTextureVideoView.ScaleType.CENTER_CROP);
            esaphTextureVideoView.setDataSource(uri.getPath());
            esaphTextureVideoView.setLooping(true);
            esaphTextureVideoView.play();
            esaphTextureVideoView.setVisibility(View.VISIBLE);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "displayVideo() failed: " + ec);
        }
    }

    public void setMiddleViewEditing()
    {
        swipeNavigation.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutBottomOptionsCameraTools, bottomMiddleViewCameraEditingVideo)
                .commit();
    }

    public void preparePostAndStartUploading(final JSONArray WAMP, final String Beschreibung, final ArrayList<EsaphHashtag> selectingListHashtags)
    {
        try
        {
            SQLUploads sqlUploads = new SQLUploads(swipeNavigation);
            final String uniqueID = UUID.randomUUID().toString();

            Bitmap bitmapThumpnail = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try
            {
                retriever.setDataSource(fileVideoToDisplay.getAbsolutePath());
                bitmapThumpnail = retriever.getFrameAtTime();
            }
            catch (IllegalArgumentException ex)
            {
                Log.i(getClass().getName(), "failed to get frame from video" + ex);
            }
            catch (RuntimeException ex)
            {
                Log.i(getClass().getName(), "failed to get frame from video" + ex);
            }
            finally
            {
                try
                {
                    retriever.release();
                }
                catch (RuntimeException ex)
                {
                    Log.i(getClass().getName(), "failed to finally get frame from video" + ex);
                }
            }

            byte cachedVideo[] = new byte[(int) fileVideoToDisplay.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileVideoToDisplay));
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(cachedVideo);
            dis.close();
            bis.close();

            if(bitmapThumpnail != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamThumpnail = new ByteArrayOutputStream();
                bitmapThumpnail.compress(Bitmap.CompressFormat.JPEG, EsaphGlobalValues.COMP_RATE_IMAGES, byteArrayOutputStreamThumpnail);

                Bitmap bitmapCompressedThumpnail = BitmapFactory.decodeByteArray(byteArrayOutputStreamThumpnail.toByteArray(),
                        0, byteArrayOutputStreamThumpnail.toByteArray().length);

                StorageHandler.saveToResolutions(swipeNavigation, //Saving thumpnail.
                        bitmapCompressedThumpnail,
                        StorageHandler.getFile(
                                swipeNavigation,
                                StorageHandler.FOLDER__SPOTLIGHT,
                                uniqueID,
                                new EsaphDimension(bitmapCompressedThumpnail.getWidth(), bitmapCompressedThumpnail.getHeight()),
                                StorageHandler.IMAGE_PREFIX));

                File rawVideoFile = StorageHandler.getFileVideo(StorageHandler.FOLDER__SPOTLIGHT, swipeNavigation ,uniqueID);

                StorageHandler.saveVideoFile(swipeNavigation, cachedVideo, rawVideoFile);
                long Post_ID = sqlUploads.preparePostVideo(
                        uniqueID,
                        rawVideoFile,
                        System.currentTimeMillis(),
                        WAMP,
                        Beschreibung,
                        selectingListHashtags);

                if(swipeNavigation != null)
                {
                    Intent intentStartUploadService = new Intent(swipeNavigation, UploadService.class);
                    intentStartUploadService.setAction(UploadService.ACTION_TYPE_SPOTLIGHT_POST_UPLOAD);
                    intentStartUploadService.putExtra(UploadService.extraP_ID, Post_ID);
                    swipeNavigation.startService(intentStartUploadService);
                    swipeNavigation.setCameraViewMode();
                }
            }

            sqlUploads.close();
        }
        catch (Exception ec)
        {
            Toast.makeText(swipeNavigation, swipeNavigation.getResources().getString(R.string.txt_alertPostingPhotoNotSavedToUpdatePost), Toast.LENGTH_LONG).show();
            Log.i(getClass().getName(), "Fehler konnte bild nicht in den Cache speichern (Posting): " + ec);
        }
    }

    public int getVideoOrPicture()
    {
        return currentPreviewMode;
    }

    public long getShootTime()
    {
        return millisShootTime;
    }

    public Bitmap getBitmapLastPic()
    {
        return bitmapLastPic;
    }

    public File getFileVideoToDisplay()
    {
        return fileVideoToDisplay;
    }

    public EsaphLockAbleViewPager getEsaphLockAbleViewPager()
    {
        return esaphLockAbleViewPager;
    }

    public void clearAllData()
    {
        if(isAdded())
        {
            bottomMiddleViewCameraEditingVideo.clearAllData();
            startCheckLifecloudBackup();

            removeRecordedVideo();

            if(esaphTextureVideoView != null)
            {
                esaphTextureVideoView.pauseAndSeekToZero();
            }
        }
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return bottomMiddleViewCameraEditingVideo.onActivityDispatchedBackPressed();
    }

    public EsaphTagFragment getEsaphTagFragment()
    {
        return bottomMiddleViewCameraEditingVideo.getEsaphTagFragment();
    }


    public void prepareLifeCloudPostAndStartUploading(final LifeCloudUpload lifeCloudUpload)
    {
        try
        {
            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(swipeNavigation);

            Bitmap bitmapThumpnail = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try
            {
                retriever.setDataSource(fileVideoToDisplay.getAbsolutePath());
                bitmapThumpnail = retriever.getFrameAtTime();
            }
            catch (IllegalArgumentException ex)
            {
                Log.i(getClass().getName(), "failed to get frame from video" + ex);
            }
            catch (RuntimeException ex)
            {
                Log.i(getClass().getName(), "failed to get frame from video" + ex);
            }
            finally
            {
                try
                {
                    retriever.release();
                }
                catch (RuntimeException ex)
                {
                    Log.i(getClass().getName(), "failed to finally get frame from video" + ex);
                }
            }

            byte cachedVideo[] = new byte[(int) fileVideoToDisplay.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileVideoToDisplay));
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(cachedVideo);
            dis.close();
            bis.close();


            if(bitmapThumpnail != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamThumpnail = new ByteArrayOutputStream();
                bitmapThumpnail.compress(Bitmap.CompressFormat.JPEG, EsaphGlobalValues.COMP_RATE_IMAGES, byteArrayOutputStreamThumpnail);

                Bitmap bitmapCompressedThumpnail = BitmapFactory.decodeByteArray(byteArrayOutputStreamThumpnail.toByteArray(),
                        0, byteArrayOutputStreamThumpnail.toByteArray().length);

                StorageHandler.saveToResolutions(swipeNavigation, //Saving thumpnail.
                        bitmapCompressedThumpnail,
                        StorageHandler.getFile(
                                swipeNavigation,
                                StorageHandler.FOLDER__LIFECLOUD,
                                lifeCloudUpload.getCLOUD_PID(),
                                new EsaphDimension(bitmapCompressedThumpnail.getWidth(), bitmapCompressedThumpnail.getHeight()),
                                StorageHandler.IMAGE_PREFIX));

                File rawVideoFile = StorageHandler.getFileVideo(StorageHandler.FOLDER__LIFECLOUD, swipeNavigation, lifeCloudUpload.getCLOUD_PID());

                StorageHandler.saveVideoFile(swipeNavigation, cachedVideo, rawVideoFile);
                sqlLifeCloud.insertNewLifeCloudUpload(lifeCloudUpload);

                Activity activity = swipeNavigation;
                if(activity != null)
                {
                    Intent intent = new Intent(activity, UploadService.class);
                    intent.setAction(UploadService.ACTION_TYPE_LIFECLOUD_UPLOAD);
                    intent.putExtra(UploadService.extraP_ID, lifeCloudUpload.getCLOUD_PID());
                    activity.startService(intent);
                }
            }

            sqlLifeCloud.close();
        }
        catch (Exception ec)
        {
            Toast.makeText(swipeNavigation, swipeNavigation.getResources().getString(R.string.txt_alertPostingPhotoNotSavedToUpdatePost), Toast.LENGTH_LONG).show();
            Log.i(getClass().getName(), "Fehler konnte bild nicht in den Cache speichern (LifeCloud upload): " + ec);
        }
    }

    public void startCheckLifecloudBackup()
    {
        if(bottomMiddleViewCameraEditingVideo.isSaveInLifeCloud())
        {
            LifeCloudUpload lifeCloudUpload = new LifeCloudUpload(getEsaphTagFragment().getSelectedHashtags(),
                    bottomMiddleViewCameraEditingVideo.getEditTextBeschreibung().getText().toString(),
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    LifeCloudUpload.LifeCloudStatus.STATE_FAILED_NOT_UPLOADED,
                    currentPreviewMode,
                    LifeCloudUpload.EsaphLifeCloudTypeHelper.LIFECLOUD_TYPE_SPOTLIGHT_CAM);

            prepareLifeCloudPostAndStartUploading(lifeCloudUpload);
        }
    }

    @Override
    public void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap)
    {

    }
}
