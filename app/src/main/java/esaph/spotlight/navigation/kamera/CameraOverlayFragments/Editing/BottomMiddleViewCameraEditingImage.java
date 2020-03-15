/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.CameraOverlayFragments.Editing;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCameraEffectPropertiesPreview.EsaphCameraEffectPropertiesPreview;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphDistanceTouchListener.EsaphDistanceTouchListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.BrushDrawingView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.EsaphTrashbinView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.ImageFilterView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.OnPhotoEditorListener;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.OnSaveBitmap;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditor;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoEditorView;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.PhotoFilter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack.ViewType;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphColorPicker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphDrawingShaderEffects.EsaphDrawingShaderRainbow;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.EsaphImageFaceDetector;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.EsaphFaceDetectorPicker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerPickerFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerViewBASEFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.EsaphLockAbleViewPager;
import esaph.spotlight.navigation.Posting.DialogSendInfo;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.EsaphBottomMiddleViewFragment;
import esaph.spotlight.navigation.kamera.PostEditingFragments.CameraEditorImage;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphTagFragment;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileyPickerFragment;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.EsaphSmileyViewBASEFragment;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.EsaphSmileyPickerView.Model.EsaphEmojie;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontFamilyChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontShaderChooser;
import esaph.spotlight.spots.SpotMaker.Adapters.SpotMakerAdapterFontStyleChooser;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;

public class BottomMiddleViewCameraEditingImage extends EsaphBottomMiddleViewFragment
{
    private DialogSendInfo dialogSendInfo;
    private boolean saveInLifeCloud = false;
    private PhotoEditor mPhotoEditor;

    private ImageView imageViewClosePreviewMode;
    private EsaphLockAbleViewPager esaphLockAbleViewPager;
    private EsaphEditingColorCircleImageView imageViewDrawLines;
    private RelativeLayout relativeLayoutEffectPropertiesRoot;
    private EsaphCameraEffectPropertiesPreview esaphCameraEffectPropertiesPreview;
    private ImageView imageViewUndo;
    private ImageView imageViewRedo;
    private ImageView imageViewAutoFaceCrop;
    private LinearLayout linearLayoutFaceCropView;
    private ImageView imageViewSavePicture;
    private ImageView imageViewSaveInLifeCloud;
    private TextView textViewLifeCloudSave;
    private ImageView imageViewAddHashtag;
    private LinearLayout linearLayoutEffects;
    private ImageView imageViewShaderEffects;
    private EsaphEditingColorCircleImageView imageViewPlaceText;
    private ImageView imageViewAddSmiley;
    private EsaphCircleImageView imageViewFilterEffekt;
    private EditText editTextBeschreibung;
    private ImageView imageViewEmojieSelect;
    private ImageView imageViewAddSticker;
    private EsaphColorPicker esaphColorPicker;
    private ImageFilterView mImageFilterView;
    private TextView textViewSendImage;
    private TextView textViewSpeichern;
    private TextView textViewHashtag;
    private TextView textViewEffects;
    private FrameLayout frameLayoutUnderOptions;
    private RecyclerView recyclerViewHorizontalChooseEffect;
    private EsaphTrashbinView esaphTrashbinView;

    private RelativeLayout relativeLayoutBottomExtras;


    private JSONObject jsonObjectTextsEffects = new JSONObject();


    private boolean isDrawing = false;

    private CameraEditorImage cameraEditorImage;
    private SwipeNavigation swipeNavigation;

    private EsaphTagFragment esaphTagFragment;

    public BottomMiddleViewCameraEditingImage()
    {
        // Required empty public constructor
    }

    public static BottomMiddleViewCameraEditingImage getInstance()
    {
        return new BottomMiddleViewCameraEditingImage();
    }

    private BottomSheetDialogFragment currentBottomSheetDialogFragment;
    public BottomSheetDialogFragment getCurrentBottomSheetDialog()
    {
        return currentBottomSheetDialogFragment;
    }

    public void clearAllData()
    {
        dialogSendInfo = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        saveInLifeCloud = false;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof SwipeNavigation)
        {
            swipeNavigation = (SwipeNavigation) context;
            cameraEditorImage = (CameraEditorImage)
                    swipeNavigation.getSupportFragmentManager().findFragmentById(R.id.cameraEditorImage);
        }
    }

    private EsaphFaceDetectorPicker esaphFaceDetectorPicker;
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        frameLayoutUnderOptions = null;
        recyclerViewHorizontalChooseEffect = null;
        esaphTrashbinView = null;
        textViewSpeichern = null;
        textViewHashtag = null;
        esaphFaceCropper = null;
        textViewLifeCloudSave = null;
        textViewEffects = null;
        imageViewUndo = null;
        imageViewRedo = null;
        linearLayoutEffects = null;
        imageViewShaderEffects = null;
        linearLayoutLeftEditingOptionBar = null;
        textViewSendImage = null;
        esaphFaceDetectorPicker = null;
        imageViewAutoFaceCrop = null;
        relativeLayoutBottomExtras = null;
        relativeLayoutOperationLayoutRootView = null;
        imageViewAddSmiley = null;
        imageViewFilterEffekt = null;
        imageViewPlaceText = null;
        esaphTagFragment = null;
        imageViewAddSticker = null;
        esaphColorPicker = null;
        imageViewDrawLines = null;
        imageViewSavePicture = null;
        editTextBeschreibung.setFilters(new InputFilter[] {});
        editTextBeschreibung.setText("");
        editTextBeschreibung = null;
        imageViewEmojieSelect = null;
        cameraEditorImage = null;
        currentBottomSheetDialogFragment = null;
    }

    private RelativeLayout relativeLayoutOperationLayoutRootView;
    private LinearLayout linearLayoutLeftEditingOptionBar;

    private SpotMakerEdittext currentSelectedSpotMakerEdittext;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).resetToInitState(getContext());

        Activity activity = getActivity();
        if(activity != null)
        {
            esaphTrashbinView.setImageViewClose(imageViewClosePreviewMode);
            mPhotoEditor = new PhotoEditor.Builder(getContext(), (PhotoEditorView) activity.findViewById(R.id.mIdPhotoEditorView))
                    .setPinchTextScalable(true)
                    .setEsaphTrashbinViewBuilder(esaphTrashbinView)
                    .build();
        }

        imageViewClosePreviewMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(swipeNavigation != null)
                {
                    swipeNavigation.onActivityDispatchBackPressEvent();
                }
            }
        });

        imageViewUndo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(mPhotoEditor != null)
                {
                    mPhotoEditor.undo();
                }
            }
        });

        imageViewRedo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(mPhotoEditor != null)
                {
                    mPhotoEditor.redo();
                }
            }
        });

        textViewSendImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                FragmentActivity activity = getActivity();
                if(activity != null && !activity.isFinishing())
                {
                    if(dialogSendInfo == null) dialogSendInfo = DialogSendInfo.getInstance(new DialogSendInfo.ChooseReceiversListener()
                    {
                        @Override
                        public void onSendIt(JSONArray jsonArrayReceivers)
                        {
                            cameraEditorImage.preparePostAndStartUploading(
                                jsonArrayReceivers,
                                editTextBeschreibung.getText().toString(),
                                    esaphTagFragment.getSelectedHashtags());
                        }
                    });

                    dialogSendInfo.show(activity.getSupportFragmentManager(), DialogSendInfo.class.getName());
                }
            }
        });

        mPhotoEditor.addOnPhotoEditorListener(new OnPhotoEditorListener()
        {
            @Override
            public void onEditTextChangeListener(final SpotMakerEdittext spotMakerEdittext, final String text, final int colorCode)
            {
                currentSelectedSpotMakerEdittext = spotMakerEdittext;
                displayColorPicker(imageViewPlaceText);
                esaphColorPicker.setOnColorChangeListener(new EsaphColorPicker.OnColorChangeListener()
                {
                    @Override
                    public void onColorChange(int selectedColor)
                    {
                        try {
                            SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).setTextColor(selectedColor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imageViewPlaceText.setDrawableBackgroundColor(selectedColor);
                        spotMakerEdittext.onValuesChanged(jsonObjectTextsEffects);
                    }
                });

                spotMakerEdittext.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Activity activity = getActivity();
                        if(activity != null)
                        {
                            activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
                            spotMakerEdittext.setFocusableInTouchMode(true);
                            spotMakerEdittext.requestFocus();

                            final InputMethodManager inputMethodManager = (InputMethodManager) activity
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);

                            if(inputMethodManager != null)
                            {
                                inputMethodManager.showSoftInput(spotMakerEdittext, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    }
                });
            }

            @Override
            public void onAddViewListener(ViewType viewType, View view, int numberOfAddedViews)
            {
                if(numberOfAddedViews > 0)
                {
                    imageViewRedo.setVisibility(View.VISIBLE);
                    imageViewUndo.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewRedo.setVisibility(View.GONE);
                    imageViewUndo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRemoveViewListener(int numberOfAddedViews)
            {
                if(numberOfAddedViews > 0)
                {
                    imageViewRedo.setVisibility(View.VISIBLE);
                    imageViewUndo.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewRedo.setVisibility(View.GONE);
                    imageViewUndo.setVisibility(View.GONE);
                    if(currentViewIgnore == imageViewPlaceText)
                    {
                        removeColorPicker(currentViewIgnore);
                        removeUnderLayoutOptions();
                    }
                }
            }

            @Override
            public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews)
            {
                if(numberOfAddedViews > 0)
                {
                    imageViewRedo.setVisibility(View.VISIBLE);
                    imageViewUndo.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageViewRedo.setVisibility(View.GONE);
                    imageViewUndo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onStartViewChangeListener(ViewType viewType, SpotMakerEdittext spotMakerEdittext)
            {
                currentSelectedSpotMakerEdittext = spotMakerEdittext;
                if(!colorPickerDisplayed)
                {
                    displayColorPicker(imageViewPlaceText);
                }
                applyUnderOptionsLayout(R.layout.layout_photoeditor_under_options_text);
            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {

            }

            @Override
            public void onStopViewChangeListener(ViewType viewType)
            {
            }

            @Override
            public void onViewTouchedOutsideBounds()
            {
                if(isAdded() && colorPickerDisplayed)
                {
                    removeColorPicker(currentViewIgnore);
                }

                removeUnderLayoutOptions();
            }
        });

        editTextBeschreibung.setText("");
        editTextBeschreibung.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(150)});

        imageViewShaderEffects.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                applyShaderEffect(getNextStruggleEnumShaderPathEffect(currentEsaphShaderEffect));
            }
        });

        imageViewSaveInLifeCloud.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                saveInLifeCloud = !saveInLifeCloud;
                setUpSavedInLifeCloudIcon();
            }
        });

        imageViewAddSticker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                showBottomFragment(EsaphStickerPickerFragmentDialog.getInstance(new EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog()
                {
                    @Override
                    public void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap)
                    {
                        mPhotoEditor.addImage(new PhotoEditor.ImageObjectBuilder(bitmap));
                        currentBottomSheetDialogFragment.dismiss();
                    }
                }));
            }
        });

        imageViewAutoFaceCrop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(esaphFaceDetectorPicker == null)
                    esaphFaceDetectorPicker = EsaphFaceDetectorPicker.getInstance(new EsaphFaceDetectorPicker.FaceDetectorPickerDataTransferInterface()
                    {
                        @Override
                        public List<EsaphSpotLightSticker> onTransferList()
                        {
                            return esaphFaceCropper.getListStickersTemporalySaved();
                        }
                    });
                showBottomFragment(esaphFaceDetectorPicker);
            }
        });

        imageViewFilterEffekt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                PhotoFilter photoFilter = getNextStruggleEnumPhotoFilter(currentPhotoFilter);
                applyNewFilter(photoFilter);
            }
        });

        imageViewFilterEffekt.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(final View v)
            {
                applyNewFilter(PhotoFilter.NONE);
                modeCount = modes.length;
                //showBottomFragment(EsaphFilterPicker.getInstance());
                return true;
            }
        });

        imageViewAddSmiley.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {

                showBottomFragment(EsaphSmileyPickerFragment.getInstance(new EsaphSmileyViewBASEFragment.OnSmileySelectListenerCameraEditor() {
                    @Override
                    public void onSmileySelected(EsaphEmojie esaphEmojie)
                    {
                        if(isAdded())
                        {
                            mPhotoEditor.addEmoji(new PhotoEditor.SmileyObjectBuilder(null, esaphEmojie.getEMOJIE()));
                            currentBottomSheetDialogFragment.dismiss();
                        }
                    }
                }));
            }
        });

        imageViewPlaceText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {

                if(currentViewIgnore == v) //Preventing when user want to exist this mode.
                {
                    removeColorPicker(currentViewIgnore);
                    return;
                }

                mPhotoEditor.addText(new PhotoEditor.TextObjectBuilder(null, getResources().getString(R.string.txt_tab_to_edit), Color.RED));
            }
        });

        imageViewAddHashtag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showBottomFragment(esaphTagFragment);
            }
        });


        imageViewSavePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                switch (currentBottomExtrasMode)
                {
                    case NORMAL:
                        savePictureOrVideo();
                        break;

                    case DRAWING:
                        applyCurrentPathEffect(getNextStruggleEnumPathEffect(currentPathEffektEnum));
                        break;
                }
            }
        });

        imageViewDrawLines.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                drawingClick();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontalChooseEffect.setLayoutManager(layoutManager);

        currentPhotoFilter = PhotoFilter.NONE;
        esaphTagFragment = EsaphTagFragment.getInstance(); //Only create one instance !
    }


    public void onBitmapReady()
    {
        if(!isAdded())
            return;


        mImageFilterView.setSourceBitmap(cameraEditorImage.getBitmapLastPic());
        setNextFilterEffekt(currentPhotoFilter);
        findFaces();
    }


    private BrushDrawingView.DrawingEffektEnum currentPathEffektEnum = BrushDrawingView.DrawingEffektEnum.NORMAL;
    private BrushDrawingView.DrawingEffektEnum[] modesPathEffect = BrushDrawingView.DrawingEffektEnum.values();
    private int modeCountPathEffects = modesPathEffect.length;

    private BrushDrawingView.DrawingEffektEnum getNextStruggleEnumPathEffect(BrushDrawingView.DrawingEffektEnum pathEffektEnum)
    {
        int nextModeOrdinal = (pathEffektEnum.ordinal() + 1) % modeCountPathEffects;
        return modesPathEffect[nextModeOrdinal];
    }

    private EsaphShaderEffects currentEsaphShaderEffect = EsaphShaderEffects.NONE;
    private EsaphShaderEffects[] modesShaderPathEffect = EsaphShaderEffects.values();
    private int modeCountShaderPathEffects = modesShaderPathEffect.length;

    private EsaphShaderEffects getNextStruggleEnumShaderPathEffect(EsaphShaderEffects esaphShaderEffects)
    {
        int nextModeOrdinal = (esaphShaderEffects.ordinal() + 1) % modeCountShaderPathEffects;
        return modesShaderPathEffect[nextModeOrdinal];
    }


    private void applyCurrentPathEffect(BrushDrawingView.DrawingEffektEnum pathEffektEnum)
    {
        currentPathEffektEnum = pathEffektEnum;

        Context context = getContext();
        if(context != null)
        {
            switch (currentPathEffektEnum)
            {
                case NORMAL:
                    mPhotoEditor.setBrushPathEffect(null);
                    break;

                case INTERUPTED_LINE:
                    mPhotoEditor.setBrushPathEffect(new DashPathEffect(new float[]{10,50}, CurrentPathValues.PHASE));
                    break;

                case DOTS:
                    mPhotoEditor.setBrushPathEffect(new DashPathEffect(new float[]{5,50}, CurrentPathValues.PHASE));
                    break;
            }
        }

        applyShaderEffect(currentEsaphShaderEffect);
        linearLayoutEffects.setVisibility(View.VISIBLE);
        setUpPathEffectIcons();
    }

    private void applyCurrentPathEffectValuesChange()
    {
        Context context = getContext();
        if(context != null)
        {
            switch (currentPathEffektEnum)
            {
                case NORMAL:
                    mPhotoEditor.setBrushPathEffect(null);
                    break;

                case INTERUPTED_LINE:
                    mPhotoEditor.setBrushPathEffect(new DashPathEffect(CurrentPathValues.DOTS, CurrentPathValues.PHASE));
                    break;

                case DOTS:
                    mPhotoEditor.setBrushPathEffect(new DashPathEffect(CurrentPathValues.DOTS, CurrentPathValues.PHASE));
                    break;
            }
        }
    }

    private static class CurrentPathValues
    {
        private static float[] DOTS = new float[]{0,0};
        private static float PHASE = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpPathEffectIcons()
    {
        Context context = getContext();
        if(context != null)
        {
            imageViewAddHashtag.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effect_option_point_length));
            imageViewSaveInLifeCloud.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effect_option_point_distance));

            imageViewAddHashtag.setOnTouchListener(new EsaphDistanceTouchListener().setDistanceTochInterface(new EsaphDistanceTouchListener.DistanceTochInterface()
            {
                @Override
                public void onMoving(float distance)
                {
                    int value = (int) (200f * distance);
                    CurrentPathValues.DOTS[0] = value;
                    applyCurrentPathEffectValuesChange();
                    esaphCameraEffectPropertiesPreview.invalidate();
                }

                @Override
                public void onTouchDown()
                {
                    esaphCameraEffectPropertiesPreview.setPaint(mPhotoEditor.getBrushDrawingView().getmDrawPaint());
                    esaphCameraEffectPropertiesPreview.setEsaphShader(mPhotoEditor.getBrushDrawingView().getShaderEffect());

                    relativeLayoutEffectPropertiesRoot
                            .animate()
                            .alpha(1.0f)
                            .setDuration(150)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(1.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(1.0f);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();
                }

                @Override
                public void onTouchUp()
                {
                    relativeLayoutEffectPropertiesRoot
                            .animate()
                            .alpha(0.0f)
                            .setDuration(150)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation)
                                {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.GONE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(0.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.GONE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(0.0f);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();
                }
            }));

            imageViewSaveInLifeCloud.setOnTouchListener(new EsaphDistanceTouchListener().setDistanceTochInterface(new EsaphDistanceTouchListener.DistanceTochInterface()
            {
                @Override
                public void onMoving(float distance)
                {
                    int value = (int) (200f * distance);
                    CurrentPathValues.DOTS[1] = value;
                    applyCurrentPathEffectValuesChange();
                    esaphCameraEffectPropertiesPreview.invalidate();
                }

                @Override
                public void onTouchDown()
                {
                    esaphCameraEffectPropertiesPreview.setPaint(mPhotoEditor.getBrushDrawingView().getmDrawPaint());
                    esaphCameraEffectPropertiesPreview.setEsaphShader(mPhotoEditor.getBrushDrawingView().getShaderEffect());

                    relativeLayoutEffectPropertiesRoot
                            .animate()
                            .alpha(1.0f)
                            .setDuration(150)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(1.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.VISIBLE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(1.0f);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();
                }

                @Override
                public void onTouchUp()
                {
                    relativeLayoutEffectPropertiesRoot
                            .animate()
                            .alpha(0.0f)
                            .setDuration(150)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation)
                                {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.GONE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(0.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation)
                                {
                                    relativeLayoutEffectPropertiesRoot.setVisibility(View.GONE);
                                    relativeLayoutEffectPropertiesRoot.setAlpha(0.0f);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            })
                            .start();
                }
            }));

            textViewHashtag.setText(getResources().getString(R.string.txt_laenge));
            textViewLifeCloudSave.setText(getResources().getString(R.string.txt_distance));
            textViewSpeichern.setText(getResources().getStringArray(R.array.array_stroke_types)[currentPathEffektEnum.ordinal()]);

            switch (currentPathEffektEnum)
            {
                case NORMAL:
                    imageViewSavePicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effect_line_normal));
                    break;

                case INTERUPTED_LINE:
                    imageViewSavePicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effect_interupted_line));
                    break;

                case DOTS:
                    imageViewSavePicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effekt_dot_line));
                    break;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_bottom_middle_view_camera_editing, container, false);

        frameLayoutUnderOptions = rootView.findViewById(R.id.frameLayoutUnderOptions);
        recyclerViewHorizontalChooseEffect = rootView.findViewById(R.id.recylerViewEffektChoose);
        esaphTrashbinView = (EsaphTrashbinView) rootView.findViewById(R.id.esaphTrashbinView);
        linearLayoutLeftEditingOptionBar = (LinearLayout) rootView.findViewById(R.id.linearLayoutEditingItems);
        relativeLayoutOperationLayoutRootView = (RelativeLayout) rootView.findViewById(R.id.operationLayoutView);
        imageViewSavePicture = (ImageView) rootView.findViewById(R.id.imageViewSavePicturePreview);
        imageViewSaveInLifeCloud = (ImageView) rootView.findViewById(R.id.imageViewSaveLifeCloud);
        textViewLifeCloudSave = (TextView) rootView.findViewById(R.id.textViewViewSaveLifeCloud);
        editTextBeschreibung = (EditText) rootView.findViewById(R.id.editTextMainBeschreibungPost);
        imageViewAddHashtag = (ImageView) rootView.findViewById(R.id.imageViewChooseTag);
        imageViewAutoFaceCrop = (ImageView) rootView.findViewById(R.id.imageViewAutoFaceCrop);
        linearLayoutFaceCropView = (LinearLayout) rootView.findViewById(R.id.linearLyoutFaceCropView);
        textViewSendImage = (TextView) rootView.findViewById(R.id.textViewSendImage);
        imageViewShaderEffects = (ImageView) rootView.findViewById(R.id.imageViewShaderEffects);
        relativeLayoutBottomExtras = (RelativeLayout) rootView.findViewById(R.id.relativLayoutBottomExtras);
        linearLayoutEffects = (LinearLayout) rootView.findViewById(R.id.linearLayoutShaderEffects);
        relativeLayoutEffectPropertiesRoot = (RelativeLayout) rootView.findViewById(R.id.relativLayoutEffectPropertiesPreview);
        esaphCameraEffectPropertiesPreview = (EsaphCameraEffectPropertiesPreview) rootView.findViewById(R.id.esaphEffectProperties);


        textViewSpeichern = (TextView) rootView.findViewById(R.id.textViewSavePictureStatus);
        textViewHashtag = (TextView) rootView.findViewById(R.id.textViewChoosenTag);
        textViewEffects = (TextView) rootView.findViewById(R.id.textViewShaderEffects);


        imageViewPlaceText = (EsaphEditingColorCircleImageView) rootView.findViewById(R.id.imageViewText);
        imageViewPlaceText.setDrawableBackgroundColor(Color.TRANSPARENT);

        imageViewAddSmiley = (ImageView) rootView.findViewById(R.id.imageViewSmiley);
        imageViewEmojieSelect = (ImageView) rootView.findViewById(R.id.imageViewEmojisCamera);
        imageViewAddSticker = (ImageView) rootView.findViewById(R.id.imageViewAddSticker);
        imageViewDrawLines = (EsaphEditingColorCircleImageView) rootView.findViewById(R.id.imageViewDrawCanvas);
        imageViewDrawLines.setDrawableBackgroundColor(Color.TRANSPARENT);

        imageViewFilterEffekt = (EsaphCircleImageView) rootView.findViewById(R.id.imageViewFilterEffekt);
        imageViewClosePreviewMode = (ImageView) rootView.findViewById(R.id.imageViewRemovePicture);
        imageViewRedo = (ImageView) rootView.findViewById(R.id.imageViewRedo);
        imageViewUndo = (ImageView) rootView.findViewById(R.id.imageViewUndo);


        Activity activity = getActivity();
        if(activity != null)
        {
            esaphLockAbleViewPager = (EsaphLockAbleViewPager) activity.findViewById(R.id.mainNavigationVerticalSwipeViewPager);
        }

        esaphColorPicker = (EsaphColorPicker) rootView.findViewById(R.id.viewColorPicker);
        mImageFilterView = (ImageFilterView) rootView.findViewById(R.id.imageFilterViewPreviewNextEffekt);

        return rootView;
    }

    private void drawingClick()
    {
        isDrawing = !isDrawing;
        if(isDrawing)
        {
            displayColorPicker(imageViewDrawLines);
            setBottomOptionsExtrasMode(BottomExtrasMode.DRAWING);
            mPhotoEditor.setBrushDrawingMode(true);
            mPhotoEditor.setBrushColor(Color.RED);

            esaphColorPicker.setOnColorChangeListener(new EsaphColorPicker.OnColorChangeListener()
            {
                @Override
                public void onColorChange(int selectedColor)
                {
                    imageViewDrawLines.setDrawableBackgroundColor(selectedColor);
                    if(mPhotoEditor != null)
                    {
                        mPhotoEditor.setBrushColor(selectedColor);
                    }
                }
            });
        }
        else
        {
            imageViewDrawLines.setDrawableBackgroundColor(Color.TRANSPARENT);
            mPhotoEditor.setBrushDrawingMode(false);
            removeColorPicker(imageViewDrawLines);
        }
    }

    private EsaphImageFaceDetector esaphFaceCropper;
    private void findFaces()
    {
        if(esaphFaceCropper == null)
            esaphFaceCropper = new EsaphImageFaceDetector();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Bitmap bitmapNew = Bitmap.createBitmap(cameraEditorImage.getBitmapLastPic());
                    final List<Bitmap> faces = esaphFaceCropper.findFaces(bitmapNew);
                    if(!faces.isEmpty())
                    {
                        Activity activity = getActivity();
                        if(activity != null && imageViewAutoFaceCrop != null)
                        {
                            imageViewAutoFaceCrop.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(imageViewAutoFaceCrop != null)
                                    {
                                        imageViewAutoFaceCrop.setImageBitmap(faces.get(0));
                                        imageViewAutoFaceCrop.setClickable(true);
                                        linearLayoutFaceCropView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }

                        esaphFaceCropper.saveFacesTemp(getContext(), faces);
                    }
                    else
                    {
                        Activity activity = getActivity();
                        if(activity != null && imageViewAutoFaceCrop != null)
                        {
                            imageViewAutoFaceCrop.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(imageViewAutoFaceCrop != null)
                                    {
                                        imageViewAutoFaceCrop.setImageBitmap(null);
                                        imageViewAutoFaceCrop.setClickable(false);
                                        linearLayoutFaceCropView.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "findFaces() failed: " + ec);
                }
            }
        }).start();


    }

    public void savePictureOrVideo()
    {
        boolean success = false;
        startSavingAnimation();
        try
        {
            File fileDiRS = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + getResources().getString(R.string.app_name));
            if(!fileDiRS.exists())
            {
                fileDiRS.mkdirs();
            }

            if(imageViewSavePicture.getTag() != null && !imageViewSavePicture.getTag().equals("F"))
            {
                if(imageViewSavePicture.getTag().equals("C")) //Image saved, delete it again.
                {
                    File fileSaveLocation = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator + getResources().getString(R.string.app_name)
                                    + File.separator + getResources().getString(R.string.app_name) + "#" + cameraEditorImage.getShootTime() + ".jpg");

                    fileSaveLocation.delete();
                    getActivity().getContentResolver().delete(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.MediaColumns.DATA + "='" + fileSaveLocation.getAbsolutePath() + "'", null
                    );

                    success = true;
                }
            }
            else
            {
                final File fileSaveLocation = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + getResources().getString(R.string.app_name)
                                + File.separator + getResources().getString(R.string.app_name) + "#" + cameraEditorImage.getShootTime() + ".jpg");
                if(fileSaveLocation.createNewFile())
                {
                    final FileOutputStream out = new FileOutputStream(fileSaveLocation);

                    mPhotoEditor.saveAsBitmap(new OnSaveBitmap()
                    {
                        @Override
                        public void onBitmapReady(Bitmap saveBitmap)
                        {
                            try
                            {
                                saveBitmap.compress(Bitmap.CompressFormat.JPEG, EsaphGlobalValues.COMP_RATE_IMAGES, out);
                                out.close();

                                Activity activity = getActivity();
                                if(activity != null)
                                {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.DATA, fileSaveLocation.getAbsolutePath());
                                    values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
                                    activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                                }
                            }
                            catch (Exception ec)
                            {
                            }
                        }

                        @Override
                        public void onFailure(Exception e)
                        {
                        }
                    });

                    success = true;
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


    private void startSavingAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.esaph_saving_on_internal_storage_animation);
        a.reset();
        imageViewSavePicture.setClickable(false);
        imageViewSavePicture.clearAnimation();
        imageViewSavePicture.startAnimation(a);
    }


    private void stopSavingAnimation(boolean success)
    {
        imageViewSavePicture.setClickable(true);
        imageViewSavePicture.clearAnimation();

        if(imageViewSavePicture.getTag() == null)
        {
            if(success)
            {
                imageViewSavePicture.setTag("C");
                Glide.with(this).load(R.drawable.ic_image_saved_succesfully_to_gallery).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSavePicture.setImageDrawable(resource);
                        }
                    }
                });
            }
            else
            {
                imageViewSavePicture.setTag("F");
                Glide.with(this).load(R.drawable.ic_save_image_failed).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSavePicture.setImageDrawable(resource);
                        }
                    }
                });
            }
        }
        else
        {
            if(imageViewSavePicture.getTag().equals("C"))
            {
                imageViewSavePicture.setTag(null);
                Glide.with(this).load(R.drawable.ic_save_picture).into(new SimpleTarget<Drawable>()
                {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            imageViewSavePicture.setImageDrawable(resource);
                        }
                    }
                });
            }
            else if(imageViewSavePicture.getTag().equals("F"))
            {
                if(success)
                {
                    imageViewSavePicture.setTag("C");
                    Glide.with(this).load(R.drawable.ic_image_saved_succesfully_to_gallery).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                imageViewSavePicture.setImageDrawable(resource);
                            }
                        }
                    });
                }
                else
                {
                    imageViewSavePicture.setTag("F");
                    Glide.with(this).load(R.drawable.ic_save_image_failed).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                imageViewSavePicture.setImageDrawable(resource);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onAnimateScrolling(float alpha)
    {
    }

    private void showBottomFragment(BottomSheetDialogFragment bottomSheetDialogFragment)
    {
        this.currentBottomSheetDialogFragment = bottomSheetDialogFragment;
        currentBottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getClass().getName());
    }

    @Override
    public boolean onActivityDispatchedBackPressed()
    {
        if(colorPickerDisplayed)
        {
            removeColorPicker(currentViewIgnore);
            removeUnderLayoutOptions();
            return true;
        }

        return false;
    }

    public final ViewPager.OnPageChangeListener onHorizontalSwipeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            float negative = 1-positionOffset;

            if(position == 0)
            {
                imageViewClosePreviewMode.setAlpha(negative*2);
                relativeLayoutOperationLayoutRootView.setAlpha(negative*2);

                imageViewClosePreviewMode.setTranslationX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-100.0f * (positionOffset * 6)), getResources().getDisplayMetrics()));
                relativeLayoutOperationLayoutRootView.setTranslationX(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-500.0f * (positionOffset)), getResources().getDisplayMetrics()));

            }
            else if(position == 1)
            {
                imageViewClosePreviewMode.setAlpha(positionOffset*2);
                relativeLayoutOperationLayoutRootView.setAlpha(positionOffset*2);

                imageViewClosePreviewMode.setTranslationX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-100.0f * (negative * 6)), getResources().getDisplayMetrics()));
                relativeLayoutOperationLayoutRootView.setTranslationX(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-500.0f * (negative)), getResources().getDisplayMetrics()));

            }
        }

        @Override
        public void onPageSelected(int position)
        {
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    @Override
    public void onStart()
    {
        super.onStart();
        if(isVisible())
        {
            esaphLockAbleViewPager.addOnPageChangeListener(this.onHorizontalSwipeListener);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        esaphLockAbleViewPager.removeOnPageChangeListener(this.onHorizontalSwipeListener);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        editTextBeschreibung.clearFocus();
    }


    protected void animateViewCollapse(View viewIngore, final View child)
    {
        if (child instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) child).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) child).getChildAt(i);
                animateViewCollapse(viewIngore, innerView);
            }
        }
        else
        {
            if(viewIngore.getId() != child.getId())
            {
                child.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .translationX(-linearLayoutLeftEditingOptionBar.getWidth())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                child.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                child.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .start();
            }
            else
            {

            }
        }
    }


    protected void animateViewShow(final View child)
    {
        if (child instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) child).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) child).getChildAt(i);
                animateViewShow(innerView);
            }
        }
        else
        {
            child.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .translationX(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                            child.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            child.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {
                            child.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {
                        }
                    }).start();
        }
    }

    private EsaphEditingColorCircleImageView currentViewIgnore;
    public void displayColorPicker(EsaphEditingColorCircleImageView viewIngore)
    {
        if(colorPickerDisplayed) removeColorPicker(currentViewIgnore);

        if(esaphColorPicker != null && linearLayoutLeftEditingOptionBar != null)
        {
            currentViewIgnore = viewIngore;

            linearLayoutLeftEditingOptionBar.animate()
                    .setDuration(300)
                    .translationX(esaphColorPicker.getWidth())
                    .start();

            animateViewCollapse(viewIngore, linearLayoutLeftEditingOptionBar);
            viewIngore.setPadding(DisplayUtils.dp2px(12),
                    DisplayUtils.dp2px(12),
                    DisplayUtils.dp2px(12),
                    DisplayUtils.dp2px(12));

            viewIngore.setColorFilter(
                    new PorterDuffColorFilter(
                            ContextCompat.getColor(getContext(),
                                    R.color.colorGreyImagePreview),PorterDuff.Mode.MULTIPLY));

            esaphColorPicker.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .translationX(0).setListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    esaphColorPicker.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    esaphColorPicker.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    esaphColorPicker.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }
            }).start();

            colorPickerDisplayed = true;
        }
    }

    private boolean colorPickerDisplayed = false;

    public void removeColorPicker(EsaphEditingColorCircleImageView viewRemove)
    {
        if(esaphColorPicker != null && linearLayoutLeftEditingOptionBar != null)
        {
            linearLayoutLeftEditingOptionBar.animate()
                    .setDuration(300)
                    .translationX(0)
                    .start();
            animateViewShow(linearLayoutLeftEditingOptionBar);

            viewRemove.setPadding(DisplayUtils.dp2px(10),
                    DisplayUtils.dp2px(10),
                    DisplayUtils.dp2px(10),
                    DisplayUtils.dp2px(10));

            viewRemove.setDrawableBackgroundColor(Color.TRANSPARENT);

            viewRemove.clearColorFilter();

            esaphColorPicker.animate().setDuration(300)
                    .translationX(-esaphColorPicker.getWidth()).setListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    esaphColorPicker.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    esaphColorPicker.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }
            }).start();
        }

        mPhotoEditor.removeAllCursors();
        colorPickerDisplayed = false;
        resetBottomOptionsExtrasMode();
        currentViewIgnore = null;
    }

    private PhotoFilter currentPhotoFilter;
    private PhotoFilter[] modes = PhotoFilter.values();
    private int modeCount = modes.length;

    private PhotoFilter getNextStruggleEnumPhotoFilter(PhotoFilter photoFilter)
    {
        int nextModeOrdinal = (photoFilter.ordinal() + 1) % modeCount;
        return modes[nextModeOrdinal];
    }

    private void setNextFilterEffekt(PhotoFilter photoFilterLast)
    {
        mImageFilterView.setFilterEffect(getNextStruggleEnumPhotoFilter(photoFilterLast));
        mImageFilterView.saveBitmap(new OnSaveBitmap()
        {
            @Override
            public void onBitmapReady(final Bitmap saveBitmap)
            {
                if(saveBitmap != null && imageViewFilterEffekt != null)
                {
                    imageViewFilterEffekt.setImageBitmap(saveBitmap);
                }
            }

            @Override
            public void onFailure(Exception e)
            {
            }
        });
    }

    public void applyNewFilter(PhotoFilter photoFilter)
    {
        currentPhotoFilter = photoFilter;
        setNextFilterEffekt(photoFilter);
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    public boolean isSaveInLifeCloud()
    {
        return saveInLifeCloud;
    }

    private enum EsaphShaderEffects
    {
        NONE,
        EFFECT_RAINBOW,
    }

    private void applyShaderEffect(EsaphShaderEffects esaphShaderEffects)
    {
        currentEsaphShaderEffect = esaphShaderEffects;
        Context context = getContext();
        if(context != null)
        {
            switch (esaphShaderEffects)
            {
                case NONE:
                    mPhotoEditor.setBrushShaderEffect(null);
                    textViewEffects.setText(getResources().getStringArray(R.array.array_effects)[esaphShaderEffects.ordinal()]);
                    imageViewShaderEffects.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_path_effects_shader_no_effect));
                    break;

                case EFFECT_RAINBOW:
                    textViewEffects.setText(getResources().getStringArray(R.array.array_effects)[esaphShaderEffects.ordinal()]);
                    mPhotoEditor.setBrushShaderEffect(new EsaphDrawingShaderRainbow((int)mPhotoEditor.getPhotoEditorView().getHeight()));
                    imageViewShaderEffects.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_effect_shader_rainbow));
                    break;
            }
        }
    }

    private enum BottomExtrasMode
    {
        NORMAL,
        DRAWING
    }

    private BottomExtrasMode currentBottomExtrasMode = BottomExtrasMode.NORMAL;

    @SuppressLint("ClickableViewAccessibility")
    private void setBottomOptionsExtrasMode(BottomExtrasMode bottomOptionsExtrasMode)
    {
        currentBottomExtrasMode = bottomOptionsExtrasMode;

        switch (bottomOptionsExtrasMode)
        {
            case NORMAL:
                Context context = getContext();
                if(context != null)
                {
                    imageViewSavePicture.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_picture));
                    textViewSpeichern.setText(getResources().getString(R.string.txt_Speichern));

                    imageViewAddHashtag.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tag_choose));
                    textViewHashtag.setText(getResources().getString(R.string.txt_hashtag));

                    setUpSavedInLifeCloudIcon();

                    imageViewAddHashtag.setOnTouchListener(null);
                    imageViewSaveInLifeCloud.setOnTouchListener(null);
                }
                break;


            case DRAWING:
                applyCurrentPathEffect(currentPathEffektEnum);
                break;
        }

        relativeLayoutBottomExtras.animate()
                .setDuration(100)
                .translationY(editTextBeschreibung.getHeight())
                .start();
    }


    private void resetBottomOptionsExtrasMode()
    {
        linearLayoutEffects.setVisibility(View.GONE);
        currentBottomExtrasMode = BottomExtrasMode.NORMAL;
        setBottomOptionsExtrasMode(currentBottomExtrasMode);
        relativeLayoutBottomExtras.animate()
                .setDuration(100)
                .translationY(0)
                .start();
    }


    private void setUpSavedInLifeCloudIcon()
    {
        if(saveInLifeCloud)
        {
            Glide.with(this).load(R.drawable.ic_heart_saved).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        imageViewSaveInLifeCloud.setImageDrawable(resource);
                    }
                }
            });
            textViewLifeCloudSave.setText(getResources().getString(R.string.txt_gesichert));
        }
        else
        {
            Glide.with(this).load(R.drawable.ic_heart_save).into(new SimpleTarget<Drawable>()
            {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        imageViewSaveInLifeCloud.setImageDrawable(resource);
                    }
                }
            });
            textViewLifeCloudSave.setText(getResources().getString(R.string.txt_lifecloud));
        }
    }

    private void applyUnderOptionsLayout(@LayoutRes int LAYOUT_ID)
    {
        if(frameLayoutUnderOptions.getChildCount() > 0) return;

        frameLayoutUnderOptions.setVisibility(View.VISIBLE);
        recyclerViewHorizontalChooseEffect.setAdapter(null);
        frameLayoutUnderOptions.removeAllViews();
        frameLayoutUnderOptions.addView(LayoutInflater.from(getContext()).inflate(LAYOUT_ID, null, false));

        if(LAYOUT_ID == R.layout.layout_photoeditor_under_options_text)
        {
            frameLayoutUnderOptions.findViewById(R.id.imageViewFontStyle).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        recyclerViewHorizontalChooseEffect.setVisibility(View.VISIBLE);
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontStyleChooser(getContext(),
                                SpotTextFontStyle.options().preview(getContext()),
                                new SpotMakerAdapterFontStyleChooser.SpotMakerAdapterFontStyleClickListener()
                                {
                                    @Override
                                    public void onStyleClicked(int FONT_STYLE)
                                    {
                                        try
                                        {
                                            SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).setFontStyle(FONT_STYLE);
                                            currentSelectedSpotMakerEdittext.onValuesChanged(jsonObjectTextsEffects);
                                        }
                                        catch (Exception ec)
                                        {

                                        }
                                    }
                                }));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            frameLayoutUnderOptions.findViewById(R.id.imageViewFontFamilie).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        recyclerViewHorizontalChooseEffect.setVisibility(View.VISIBLE);
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontFamilyChooser(getContext(),
                                SpotTextFontFamlie.options().preview(getContext()),
                                new SpotMakerAdapterFontFamilyChooser.SpotMakerAdapterFontFamilyClickListener() {
                                    @Override
                                    public void onFamilySelected(String FONT_FAMILY) {
                                        try
                                        {
                                            SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).setFontFamily(FONT_FAMILY);
                                            currentSelectedSpotMakerEdittext.onValuesChanged(jsonObjectTextsEffects);
                                        }
                                        catch (Exception ec)
                                        {

                                        }
                                    }
                                }
                        ));
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "FrameLayoutUnderOptions fontfamilie: " + ec);
                    }
                }
            });

            frameLayoutUnderOptions.findViewById(R.id.imageViewFontShaders).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        recyclerViewHorizontalChooseEffect.setVisibility(View.VISIBLE);
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontShaderChooser(getContext(),
                                SpotTextShader.options().preview(getContext()),
                                new SpotMakerAdapterFontShaderChooser.SpotMakerAdapterFontShaderChooserClickListener()
                                {
                                    @Override
                                    public void onShaderClicked(JSONObject jsonObjectShader)
                                    {
                                        try
                                        {
                                            SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).setTextShader(SpotTextDefinitionBuilder.getTextShader(jsonObjectShader));
                                            currentSelectedSpotMakerEdittext.onValuesChanged(jsonObjectTextsEffects);
                                        } catch (Exception e) {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }

                                    @Override
                                    public void onShaderLongClicked()
                                    {
                                        try
                                        {

                                        }
                                        catch (Exception e)
                                        {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }

                                    @Override
                                    public void onRemoveAllShaders()
                                    {
                                        try
                                        {
                                            SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).removeTextShader();
                                            currentSelectedSpotMakerEdittext.onValuesChanged(jsonObjectTextsEffects);
                                        }
                                        catch (Exception e)
                                        {
                                            System.out.println("Da ist was schief: " + e);
                                        }
                                    }
                                }));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void removeUnderLayoutOptions()
    {
        frameLayoutUnderOptions.setVisibility(View.GONE);
        frameLayoutUnderOptions.removeAllViews();
        recyclerViewHorizontalChooseEffect.setAdapter(null);
        recyclerViewHorizontalChooseEffect.setVisibility(View.GONE);
    }
}
