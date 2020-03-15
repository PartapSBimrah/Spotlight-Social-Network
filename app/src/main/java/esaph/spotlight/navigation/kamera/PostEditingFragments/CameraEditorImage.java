package esaph.spotlight.navigation.kamera.PostEditingFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.OnSaveBitmap;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditorView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerViewBASEFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.EsaphGlobalCommunicationFragment;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Editing.BottomMiddleViewCameraEditingImage;
import esaph.spotlight.navigation.kamera.ImageTakenListener;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.services.UploadService.UploadService;

public class CameraEditorImage extends EsaphGlobalCommunicationFragment implements EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog, ImageTakenListener
{
    private BottomMiddleViewCameraEditingImage bottomMiddleViewCameraEditing;
    private PhotoEditorView photoEditorViewMainPreview;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private SwipeNavigation swipeNavigation;

    private Bitmap bitmapLastPic;
    private long millisShootTime;

    public void show()
    {
        setMiddleViewEditing();
        handlePreviewMode();
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
        esaphLockAbleViewPager = null;
        photoEditorViewMainPreview = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(getClass().getName(), "onResume");
        esaphLockAbleViewPager.paralyse();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_camera_editor_image, container, false);
        photoEditorViewMainPreview = (PhotoEditorView) rootView.findViewById(R.id.mIdPhotoEditorView);
        esaphLockAbleViewPager = (EsaphLockAbleViewPager) swipeNavigation.findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        bottomMiddleViewCameraEditing = BottomMiddleViewCameraEditingImage.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void handlePreviewMode()
    {
        esaphLockAbleViewPager.paralyse();

        esaphLockAbleViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(photoEditorViewMainPreview != null)
                {
                    return photoEditorViewMainPreview.dispatchTouchEvent(event); //Returning true if an element is clicked from view. so Viewpager is blocking.
                }

                return false;
            }
        });
    }

    private void removeShootenPicture()
    {
        System.out.println("Tracing remove-removeShootenPictureCalled");
        photoEditorViewMainPreview.getSource().setImageBitmap(null);
        bitmapLastPic = null;
        Log.i(getClass().getName(), "Altes Bild wurde erfolgreich entfernt.");
    }

    public void clearAllData()
    {
        if(isAdded())
        {
            bottomMiddleViewCameraEditing.clearAllData();

            startCheckLifecloudBackup();
            removeShootenPicture();

            PhotoEditor photoEditor = photoEditorViewMainPreview.getPhotoEditor();
            if(photoEditor != null)
            {
                photoEditor.clearAllViews();
                photoEditor.clearHelperBox();
            }
        }
    }

    private void setMiddleViewEditing()
    {
        FragmentActivity activity = getActivity();
        if(activity != null)
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutBottomOptionsCameraTools, bottomMiddleViewCameraEditing)
                    .commit();
        }
    }

    public BottomMiddleViewCameraEditingImage getBottomMiddleViewCameraEditing()
    {
        return bottomMiddleViewCameraEditing;
    }

    public void preparePostAndStartUploading(final JSONArray WAMP, final String Beschreibung, final ArrayList<EsaphHashtag> selectingListHashtags)
    {
        try
        {
            SQLUploads sqlUploads = new SQLUploads(swipeNavigation);
            final String uniqueID = UUID.randomUUID().toString();

            if(!photoEditorViewMainPreview.getPhotoEditor().isCacheEmpty())
            {
                photoEditorViewMainPreview.getPhotoEditor().saveAsBitmap(new OnSaveBitmap()
                {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap)
                    {
                        if(saveBitmap != null)
                        {
                            File rawImageFile = StorageHandler.getFile(
                                    swipeNavigation,
                                    StorageHandler.FOLDER__SPOTLIGHT,
                                    uniqueID,
                                    new EsaphDimension(saveBitmap.getWidth(), saveBitmap.getHeight()),
                                    StorageHandler.IMAGE_PREFIX);

                            StorageHandler.saveToResolutionsWithCompression(swipeNavigation, saveBitmap, rawImageFile,
                                    EsaphGlobalValues.COMP_RATE_IMAGES);

                            SQLUploads sqlUploads = new SQLUploads(swipeNavigation);
                            long Post_ID = sqlUploads.preparePostImage(uniqueID,
                                    rawImageFile,
                                    System.currentTimeMillis(),
                                    WAMP,
                                    Beschreibung,
                                    selectingListHashtags);
                            sqlUploads.close();

                            if(swipeNavigation != null)
                            {
                                Intent intentStartUploadService = new Intent(swipeNavigation, UploadService.class);
                                intentStartUploadService.setAction(UploadService.ACTION_TYPE_SPOTLIGHT_POST_UPLOAD);
                                intentStartUploadService.putExtra(UploadService.extraP_ID, Post_ID);
                                swipeNavigation.startService(intentStartUploadService);
                                swipeNavigation.setCameraViewMode();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e)
                    {
                    }
                });
            }
            else
            {
                if(bitmapLastPic != null)
                {
                    File rawImageFile = StorageHandler.getFile(
                            swipeNavigation,
                            StorageHandler.FOLDER__SPOTLIGHT,
                            uniqueID,
                            new EsaphDimension(bitmapLastPic.getWidth(), bitmapLastPic.getHeight()),
                            StorageHandler.IMAGE_PREFIX);

                    StorageHandler.saveToResolutionsWithCompression(swipeNavigation, bitmapLastPic, rawImageFile,
                            EsaphGlobalValues.COMP_RATE_IMAGES);

                    long Post_ID = sqlUploads.preparePostImage(uniqueID,
                            rawImageFile,
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
            }

            sqlUploads.close();
        }
        catch (Exception ec)
        {
            Toast.makeText(swipeNavigation, swipeNavigation.getResources().getString(R.string.txt_alertPostingPhotoNotSavedToUpdatePost), Toast.LENGTH_LONG).show();
            Log.i(getClass().getName(), "Fehler konnte bild nicht in den Cache speichern (Posting): " + ec);
        }
    }

    public long getShootTime()
    {
        return millisShootTime;
    }

    public Bitmap getBitmapLastPic()
    {
        return bitmapLastPic;
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        return bottomMiddleViewCameraEditing.onActivityDispatchedBackPressed();
    }

    public void prepareLifeCloudPostAndStartUploading(final LifeCloudUpload lifeCloudUpload)
    {
        try
        {
            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(swipeNavigation);

            if(!photoEditorViewMainPreview.getPhotoEditor().isCacheEmpty())
            {
                photoEditorViewMainPreview.getPhotoEditor().saveAsBitmap(new OnSaveBitmap()
                {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap)
                    {
                        Activity activity = swipeNavigation;
                        if(saveBitmap != null && activity != null)
                        {
                            File rawImageFile = StorageHandler.getFile(
                                    activity,
                                    StorageHandler.FOLDER__LIFECLOUD,
                                    lifeCloudUpload.getCLOUD_PID(),
                                    new EsaphDimension(saveBitmap.getWidth(), saveBitmap.getHeight()),
                                    StorageHandler.IMAGE_PREFIX);

                            StorageHandler.saveToResolutionsWithCompression(swipeNavigation, saveBitmap, rawImageFile,
                                    EsaphGlobalValues.COMP_RATE_IMAGES);

                            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(swipeNavigation);
                            sqlLifeCloud.insertNewLifeCloudUpload(lifeCloudUpload);
                            sqlLifeCloud.close();

                            Intent intent = new Intent(activity, UploadService.class);
                            intent.setAction(UploadService.ACTION_TYPE_LIFECLOUD_UPLOAD);
                            intent.putExtra(UploadService.extraP_ID, lifeCloudUpload.getCLOUD_PID());
                            activity.startService(intent);
                        }
                    }

                    @Override
                    public void onFailure(Exception e)
                    {
                    }
                });
            }
            else
            {
                Activity activity = swipeNavigation;
                if(bitmapLastPic != null && activity != null)
                {
                    File rawImageFile = StorageHandler.getFile(
                            activity,
                            StorageHandler.FOLDER__LIFECLOUD,
                            lifeCloudUpload.getCLOUD_PID(),
                            new EsaphDimension(bitmapLastPic.getWidth(), bitmapLastPic.getHeight()),
                            StorageHandler.IMAGE_PREFIX);

                    StorageHandler.saveToResolutionsWithCompression(swipeNavigation, bitmapLastPic, rawImageFile,
                            EsaphGlobalValues.COMP_RATE_IMAGES);

                    sqlLifeCloud.insertNewLifeCloudUpload(lifeCloudUpload);

                    if(activity != null)
                    {
                        Intent intent = new Intent(activity, UploadService.class);
                        intent.setAction(UploadService.ACTION_TYPE_LIFECLOUD_UPLOAD);
                        intent.putExtra(UploadService.extraP_ID, lifeCloudUpload.getCLOUD_PID());
                        activity.startService(intent);
                    }
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
        /*
        if(bottomMiddleViewCameraEditing.isSaveInLifeCloud())
        {
            LifeCloudUpload lifeCloudUpload = new LifeCloudUpload(getEsaphTagFragment().getSelectedHashtags(),
                    bottomMiddleViewCameraEditing.getEditTextBeschreibung().getText().toString(),
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    LifeCloudUpload.LifeCloudStatus.STATE_FAILED_NOT_UPLOADED,
                    CMTypes.FPIC,
                    LifeCloudUpload.EsaphLifeCloudTypeHelper.LIFECLOUD_TYPE_SPOTLIGHT_CAM);

            prepareLifeCloudPostAndStartUploading(lifeCloudUpload);
        }*/
    }

    @Override
    public void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap)
    {
        photoEditorViewMainPreview.getPhotoEditor().addImage(new PhotoEditor.ImageObjectBuilder(bitmap));
    }

    @Override
    public void onImageReady(Bitmap bitmap)
    {
        bitmapLastPic = bitmap;
        photoEditorViewMainPreview.getSource().setImageBitmap(bitmap);
        bottomMiddleViewCameraEditing.onBitmapReady();
    }

    @Override
    public void onImageTaken() {

    }

    @Override
    public void onImageTakenFailed() {

    }
}
