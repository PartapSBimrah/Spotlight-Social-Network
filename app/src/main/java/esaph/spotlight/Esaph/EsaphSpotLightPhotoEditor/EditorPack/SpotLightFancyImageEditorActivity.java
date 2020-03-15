/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.Esaph.EsaphCameraEffectPropertiesPreview.EsaphCameraEffectPropertiesPreview;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphCircleImageView;
import esaph.spotlight.Esaph.EsaphDistanceTouchListener.EsaphDistanceTouchListener;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.utils.EsaphGlobalValues;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphColorPicker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphDrawingShaderEffects.EsaphDrawingShaderRainbow;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.EsaphImageFaceDetector;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.EsaphFaceDetectorPicker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerPickerFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerViews.EsaphStickerViewBASEFragmentDialog;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.R;
import esaph.spotlight.navigation.kamera.CameraOverlayFragments.Editing.EsaphEditingColorCircleImageView;
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

public class SpotLightFancyImageEditorActivity extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String SPOT_LIGHT_FANCY_IMAGE_EDITOR_EXTRA_URI = "esaph.spotlight.SpotLightFancyImageEditorActivity.extra.uri";
    private boolean saveInLifeCloud = false;
    private PhotoEditor mPhotoEditor;

    private ImageView imageViewClosePreviewMode;
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
    private ImageView imageViewAddSticker;
    private EsaphColorPicker esaphColorPicker;
    private TextView textViewSaveImage;
    private TextView textViewSpeichern;
    private TextView textViewHashtag;
    private TextView textViewEffects;
    private FrameLayout frameLayoutUnderOptions;
    private RecyclerView recyclerViewHorizontalChooseEffect;
    private EsaphTrashbinView esaphTrashbinView;
    private RelativeLayout relativeLayoutOperationLayoutRootView;
    private LinearLayout linearLayoutLeftEditingOptionBar;
    private SpotMakerEdittext currentSelectedSpotMakerEdittext;
    private EsaphFaceDetectorPicker esaphFaceDetectorPicker;


    private JSONObject jsonObjectTextsEffects = new JSONObject();


    private boolean isDrawing = false;
    private Uri imageUri;
    private Bitmap imageBitmap;

    @Override
    protected void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(SpotLightFancyImageEditorActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_light_fancy_image_editor);

        Intent intent = getIntent();
        if(intent != null)
        {
            imageUri = Uri.parse(intent.getStringExtra(SpotLightFancyImageEditorActivity.SPOT_LIGHT_FANCY_IMAGE_EDITOR_EXTRA_URI));
        }


        frameLayoutUnderOptions = findViewById(R.id.frameLayoutUnderOptions);
        recyclerViewHorizontalChooseEffect = findViewById(R.id.recylerViewEffektChoose);
        esaphTrashbinView = (EsaphTrashbinView) findViewById(R.id.esaphTrashbinView);
        linearLayoutLeftEditingOptionBar = (LinearLayout) findViewById(R.id.linearLayoutEditingItems);
        relativeLayoutOperationLayoutRootView = (RelativeLayout) findViewById(R.id.operationLayoutView);
        imageViewSavePicture = (ImageView) findViewById(R.id.imageViewSavePicturePreview);
        imageViewSaveInLifeCloud = (ImageView) findViewById(R.id.imageViewSaveLifeCloud);
        textViewLifeCloudSave = (TextView) findViewById(R.id.textViewViewSaveLifeCloud);
        imageViewAddHashtag = (ImageView) findViewById(R.id.imageViewChooseTag);
        imageViewAutoFaceCrop = (ImageView) findViewById(R.id.imageViewAutoFaceCrop);
        linearLayoutFaceCropView = (LinearLayout) findViewById(R.id.linearLyoutFaceCropView);
        textViewSaveImage = (TextView) findViewById(R.id.textViewSaveImage);
        imageViewShaderEffects = (ImageView) findViewById(R.id.imageViewShaderEffects);
        linearLayoutEffects = (LinearLayout) findViewById(R.id.linearLayoutShaderEffects);
        relativeLayoutEffectPropertiesRoot = (RelativeLayout) findViewById(R.id.relativLayoutEffectPropertiesPreview);
        esaphCameraEffectPropertiesPreview = (EsaphCameraEffectPropertiesPreview) findViewById(R.id.esaphEffectProperties);


        textViewSpeichern = (TextView) findViewById(R.id.textViewSavePictureStatus);
        textViewHashtag = (TextView) findViewById(R.id.textViewChoosenTag);
        textViewEffects = (TextView) findViewById(R.id.textViewShaderEffects);


        imageViewPlaceText = (EsaphEditingColorCircleImageView) findViewById(R.id.imageViewText);
        imageViewPlaceText.setDrawableBackgroundColor(Color.TRANSPARENT);

        imageViewAddSmiley = (ImageView) findViewById(R.id.imageViewSmiley);
        imageViewAddSticker = (ImageView) findViewById(R.id.imageViewAddSticker);
        imageViewDrawLines = (EsaphEditingColorCircleImageView) findViewById(R.id.imageViewDrawCanvas);
        imageViewDrawLines.setDrawableBackgroundColor(Color.TRANSPARENT);

        imageViewFilterEffekt = (EsaphCircleImageView) findViewById(R.id.imageViewFilterEffekt);
        imageViewClosePreviewMode = (ImageView) findViewById(R.id.imageViewRemovePicture);
        imageViewRedo = (ImageView) findViewById(R.id.imageViewRedo);
        imageViewUndo = (ImageView) findViewById(R.id.imageViewUndo);
        esaphColorPicker = (EsaphColorPicker) findViewById(R.id.viewColorPicker);





        SpotTextDefinitionBuilder.create(jsonObjectTextsEffects).resetToInitState(getApplicationContext());

        esaphTrashbinView.setImageViewClose(imageViewClosePreviewMode);
        mPhotoEditor = new PhotoEditor.Builder(getApplicationContext(), (PhotoEditorView) findViewById(R.id.mIdPhotoEditorView))
                .setPinchTextScalable(true)
                .setEsaphTrashbinViewBuilder(esaphTrashbinView)
                .build();

        imageViewClosePreviewMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                finish();
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

        textViewSaveImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                savePictureOrVideo();
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
                        if(!isFinishing())
                        {
                            getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
                            spotMakerEdittext.setFocusableInTouchMode(true);
                            spotMakerEdittext.requestFocus();

                            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
                if(!isFinishing() && colorPickerDisplayed)
                {
                    removeColorPicker(currentViewIgnore);
                }

                removeUnderLayoutOptions();
            }
        });

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
                showBottomFragment(EsaphStickerPickerFragmentDialog.getInstance(new EsaphStickerViewBASEFragmentDialog.OnStickerSelectedListenerDialog() {
                    @Override
                    public void onStickerSelected(EsaphSpotLightSticker esaphSpotLightSticker, Bitmap bitmap)
                    {
                        mPhotoEditor.addImage(new PhotoEditor.ImageObjectBuilder(bitmap));
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
                        if(!isFinishing())
                        {
                            mPhotoEditor.addEmoji(new PhotoEditor.SmileyObjectBuilder(null, esaphEmojie.getEMOJIE()));
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


        imageViewSavePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                switch (currentBottomExtrasMode)
                {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontalChooseEffect.setLayoutManager(layoutManager);

        currentPhotoFilter = PhotoFilter.NONE;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                float THUMBNAIL_SIZE = 1000f;
                InputStream input = null;
                try
                {
                    input = getContentResolver().openInputStream(imageUri);

                    BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
                    onlyBoundsOptions.inJustDecodeBounds = true;
                    onlyBoundsOptions.inDither=true;//optional
                    onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
                    BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
                    input.close();

                    if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                    {
                        return;
                    }

                    int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

                    double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
                    bitmapOptions.inDither = true; //optional
                    bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
                    input = getContentResolver().openInputStream(imageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
                    input.close();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(!isFinishing())
                            {
                                onBitmapReady(bitmap);
                            }
                        }
                    });
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Failed to load bitmap from uri to editor: " + ec);
                }
                finally
                {
                    if(input != null)
                    {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }


    private static int getPowerOfTwoForSampleRatio(double ratio)
    {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    public void onBitmapReady(Bitmap bitmap)
    {
        if(isFinishing())
            return;


        imageBitmap = bitmap;
        mPhotoEditor.getPhotoEditorView().getSource().setImageBitmap(bitmap);
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

        Context context = getApplicationContext();
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
        Context context = getApplicationContext();
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
        Context context = getApplicationContext();
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
                if(imageBitmap == null) return;

                Bitmap bitmapNew = Bitmap.createBitmap(imageBitmap);
                final List<Bitmap> faces = esaphFaceCropper.findFaces(bitmapNew);
                if(!faces.isEmpty())
                {
                    if(!isFinishing() && imageViewAutoFaceCrop != null)
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
                }
                else
                {
                    if(!isFinishing() && imageViewAutoFaceCrop != null)
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
                                    + File.separator + getResources().getString(R.string.app_name) + "#" + System.currentTimeMillis() + ".jpg");

                    fileSaveLocation.delete();
                    getContentResolver().delete(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.MediaColumns.DATA + "='" + fileSaveLocation.getAbsolutePath() + "'", null);

                    success = true;
                }
            }
            else
            {
                final File fileSaveLocation = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + getResources().getString(R.string.app_name)
                                + File.separator + getResources().getString(R.string.app_name) + "#" + System.currentTimeMillis() + ".jpg");

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

                                if(!isFinishing())
                                {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.DATA, fileSaveLocation.getAbsolutePath());
                                    values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
                                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
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
        Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.esaph_saving_on_internal_storage_animation);
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

    private BottomSheetDialogFragment currentBottomSheetDialogFragment;
    private void showBottomFragment(BottomSheetDialogFragment bottomSheetDialogFragment)
    {
        this.currentBottomSheetDialogFragment = bottomSheetDialogFragment;
        currentBottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getClass().getName());
    }

    @Override
    public void onBackPressed()
    {
        if(colorPickerDisplayed)
        {
            removeColorPicker(currentViewIgnore);
            removeUnderLayoutOptions();
        }
        else
        {
            super.onBackPressed();
        }
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
                            ContextCompat.getColor(getApplicationContext(),
                                    R.color.colorGreyImagePreview), PorterDuff.Mode.MULTIPLY));

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
        /*
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
        });*/

    }

    public void applyNewFilter(PhotoFilter photoFilter)
    {
        currentPhotoFilter = photoFilter;
        setNextFilterEffekt(photoFilter);
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    public EsaphImageFaceDetector getEsaphFaceCropper()
    {
        return esaphFaceCropper;
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
        Context context = getApplicationContext();
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
                Context context = getApplicationContext();
                if(context != null)
                {
                    imageViewSavePicture.setVisibility(View.GONE);
                    imageViewAddHashtag.setVisibility(View.GONE);
                    imageViewSaveInLifeCloud.setVisibility(View.GONE);
                    setUpSavedInLifeCloudIcon();

                    imageViewAddHashtag.setOnTouchListener(null);
                    imageViewSaveInLifeCloud.setOnTouchListener(null);
                }
                break;


            case DRAWING:
                imageViewSavePicture.setVisibility(View.VISIBLE);
                imageViewAddHashtag.setVisibility(View.VISIBLE);
                imageViewSaveInLifeCloud.setVisibility(View.VISIBLE);
                applyCurrentPathEffect(currentPathEffektEnum);
                break;
        }
    }


    private void resetBottomOptionsExtrasMode()
    {
        linearLayoutEffects.setVisibility(View.GONE);
        currentBottomExtrasMode = BottomExtrasMode.NORMAL;
        setBottomOptionsExtrasMode(currentBottomExtrasMode);
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
        frameLayoutUnderOptions.addView(LayoutInflater.from(getApplicationContext()).inflate(LAYOUT_ID, null, false));

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
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontStyleChooser(getApplicationContext(),
                                SpotTextFontStyle.options().preview(getApplicationContext()),
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
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontFamilyChooser(getApplicationContext(),
                                SpotTextFontFamlie.options().preview(getApplicationContext()),
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
                        recyclerViewHorizontalChooseEffect.setAdapter(new SpotMakerAdapterFontShaderChooser(getApplicationContext(),
                                SpotTextShader.options().preview(getApplicationContext()),
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
