/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.R;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;

public class PhotoEditor implements BrushViewChangeListener, MultiTouchListener.OnMultiTouchListener
{
    private static final short CURRENT_SELECTION_KEY = 1;

    private static final String TAG = "PhotoEditor";
    private final LayoutInflater mLayoutInflater;
    private Context context;
    private PhotoEditorView parentView;
    private ImageView imageView;
    private EsaphTrashbinView esaphTrashbinView;
    private BrushDrawingView brushDrawingView;
    private List<View> addedViews;
    private List<View> redoViews;
    private boolean isTextPinchZoomable;
    private Typeface mDefaultTextTypeface;
    private Typeface mDefaultEmojiTypeface;


    private PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.esaphTrashbinView = builder.esaphTrashbinViewBuilder;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        brushDrawingView.setBrushViewChangeListener(this);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }


    private List<OnPhotoEditorListener> onPhotoEditorListenerList = new ArrayList<>();

    public OnPhotoEditorListener getmOnPhotoEditorListener() {
        return mOnPhotoEditorListener;
    }

    private OnPhotoEditorListener mOnPhotoEditorListener = new OnPhotoEditorListener()
    {
        @Override
        public void onEditTextChangeListener(SpotMakerEdittext spotMakerEdittext, String text, int colorCode) {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onEditTextChangeListener(spotMakerEdittext, text, colorCode);
                }
            }
        }

        @Override
        public void onAddViewListener(ViewType viewType, View view, int numberOfAddedViews) {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onAddViewListener(viewType, view, numberOfAddedViews);
                }
            }
        }

        @Override
        public void onRemoveViewListener(int numberOfAddedViews) {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onRemoveViewListener(numberOfAddedViews);
                }
            }
        }

        @Override
        public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onRemoveViewListener(viewType, numberOfAddedViews);
                }
            }
        }

        @Override
        public void onStartViewChangeListener(ViewType viewType) //Any view get touched, enable trash symbol
        {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onStartViewChangeListener(viewType);
                }
            }
        }

        @Override
        public void onStartViewChangeListener(ViewType viewType, SpotMakerEdittext edittext)
        {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onStartViewChangeListener(viewType, edittext);
                }
            }
        }

        @Override
        public void onStopViewChangeListener(ViewType viewType) //Any view pauseAndSeekToZero get touched, disable trash symbol
        {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onStopViewChangeListener(viewType);
                }
            }
        }

        @Override
        public void onViewTouchedOutsideBounds()
        {
            for(OnPhotoEditorListener onPhotoEditorListener : onPhotoEditorListenerList)
            {
                if(onPhotoEditorListener != null)
                {
                    onPhotoEditorListener.onViewTouchedOutsideBounds();
                }
            }
        }
    };

    public void removeAllCursors()
    {
        List<EditText> list = new ArrayList<>();
        for(View view : addedViews)
        {
            if(view instanceof ViewGroup)
            {
                list.addAll(getEdittextViews((ViewGroup) view));
            }
        }

        for(EditText editText : list)
        {
            editText.setFocusable(false);
        }
    }

    private List<EditText> getEdittextViews(ViewGroup viewGroup)
    {
        List<EditText> list = new ArrayList<>();
        for(int counter = 0; counter < viewGroup.getChildCount(); counter++)
        {
            View view = viewGroup.getChildAt(counter);
            if(view instanceof ViewGroup)
            {
                list.addAll(getEdittextViews((ViewGroup) view));
            }
            else if(view instanceof EditText)
            {
                list.add((EditText) view);
            }
        }

        return list;
    }

    public static class EditObjectBuilder
    {
        private boolean isMoveAble;
        private boolean centerInHorizontal = false;
        private boolean centerInVertical = false;

        public EditObjectBuilder(boolean isMoveAble) {
            this.isMoveAble = isMoveAble;
        }

        public EditObjectBuilder setCenterInVertical(boolean centerInVertical) {
            this.centerInVertical = centerInVertical;
            return this;
        }

        public EditObjectBuilder setCenterInHorizontal(boolean centerInHorizontal) {
            this.centerInHorizontal = centerInHorizontal;
            return this;
        }

        public EditObjectBuilder setCenter()
        {
            return setCenterInVertical(true).setCenterInVertical(true);
        }

        public boolean isCenterInHorizontal() {
            return centerInHorizontal;
        }

        public boolean isCenterInVertical() {
            return centerInVertical;
        }

        public boolean isMoveAble() {
            return isMoveAble;
        }
    }

    public static class TextObjectBuilder extends EditObjectBuilder
    {
        private String hint;
        private int colorCode;
        private Typeface typeface;

        public TextObjectBuilder(@Nullable Typeface typeface, String hint, int colorCode)
        {
            super(true);
            this.typeface = typeface;
            this.hint = hint;
            this.colorCode = colorCode;
        }

        public TextObjectBuilder setMoveAble(boolean value)
        {
            super.isMoveAble = value;
            return this;
        }

        @Override
        public TextObjectBuilder setCenterInHorizontal(boolean centerInHorizontal) {
            super.setCenterInHorizontal(centerInHorizontal);
            return this;
        }

        @Override
        public TextObjectBuilder setCenterInVertical(boolean centerInVertical) {
            super.setCenterInVertical(centerInVertical);
            return this;
        }

        @Override
        public TextObjectBuilder setCenter() {
            super.setCenter();
            return this;
        }

        public TextObjectBuilder setColorCode(int colorCode) {
            this.colorCode = colorCode;
            return this;
        }

        public TextObjectBuilder setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public int getColorCode() {
            return colorCode;
        }

        public Typeface getTypeface() {
            return typeface;
        }

        public String getHint() {
            return hint;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(@NonNull TextObjectBuilder textObjectBuilder)
    {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        final SpotMakerEdittext textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setHint(textObjectBuilder.getHint());
        textInputTv.setTextColor(textObjectBuilder.getColorCode());
        textInputTv.setHintTextColor(textObjectBuilder.getColorCode());

        if (textObjectBuilder.getTypeface() != null)
        {
            textInputTv.setTypeface(textObjectBuilder.getTypeface());
        }

        MultiTouchListener multiTouchListener = getMultiTouchListener(textInputTv);
        multiTouchListener.setMoveAble(textObjectBuilder.isMoveAble());
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl()
        {
            @Override
            public void onClick()
            {
                if(!textInputTv.hasFocus())
                {
                    selectOnlyOneView(textRootView);

                    String textInput = textInputTv.getText().toString();
                    int currentTextColor = textInputTv.getCurrentTextColor();
                    if (mOnPhotoEditorListener != null)
                    {
                        mOnPhotoEditorListener.onEditTextChangeListener(textInputTv, textInput, currentTextColor);
                    }
                }
            }

            @Override
            public void onLongClick()
            {
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
/*
        if(textObjectBuilder.isCenterInHorizontal())
        {
            textRootView.setTranslationX(parentView.getMainViewCenterX() / (textRootView.getWidth() / 2f));
        }

        if(textObjectBuilder.isCenterInVertical())
        {
            textRootView.setTranslationY(parentView.getMainViewCenterY() / (textRootView.getHeight() / 2f));
        }*/

        addViewToParent(textRootView, textInputTv, ViewType.TEXT);
    }

    public void editText(View view, String inputText, int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    public void editText(View view, Typeface textTypeface, String inputText, int colorCode) {
        EditText inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (textTypeface != null) {
                inputTextView.setTypeface(textTypeface);
            }
            inputTextView.setTextColor(colorCode);
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }


    public static class SmileyObjectBuilder extends EditObjectBuilder
    {
        private String emojie;
        private Typeface typeface;

        public SmileyObjectBuilder(@Nullable Typeface typeface, String emojie)
        {
            super(true);
            this.typeface = typeface;
            this.emojie = emojie;
        }

        public SmileyObjectBuilder setMoveAble(boolean value)
        {
            super.isMoveAble = value;
            return this;
        }

        @Override
        public SmileyObjectBuilder setCenterInHorizontal(boolean centerInHorizontal) {
            super.setCenterInHorizontal(centerInHorizontal);
            return this;
        }

        @Override
        public SmileyObjectBuilder setCenterInVertical(boolean centerInVertical) {
            super.setCenterInVertical(centerInVertical);
            return this;
        }

        @Override
        public SmileyObjectBuilder setCenter() {
            super.setCenter();
            return this;
        }

        public SmileyObjectBuilder setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Typeface getTypeface() {
            return typeface;
        }

        public String getEmojie() {
            return emojie;
        }
    }



    public void addEmoji(SmileyObjectBuilder smileyObjectBuilder) {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.EMOJI);
        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);

        if (smileyObjectBuilder.getTypeface() != null) {
            emojiTextView.setTypeface(smileyObjectBuilder.getTypeface());
        }
        emojiTextView.setTextSize(56);
        emojiTextView.setText(smileyObjectBuilder.getEmojie());
        MultiTouchListener multiTouchListener = getMultiTouchListener(emojiTextView);
        multiTouchListener.setMoveAble(smileyObjectBuilder.isMoveAble());
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {

            }

            @Override
            public void onLongClick() {
            }
        });
        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, emojiTextView, ViewType.EMOJI);
    }


    public static class ImageObjectBuilder extends EditObjectBuilder
    {
        private Bitmap bitmap;
        private EsaphSpotLightSticker esaphSpotLightSticker;

        public ImageObjectBuilder(Bitmap bitmap)
        {
            super(true);
            this.bitmap = bitmap;
        }

        public ImageObjectBuilder setEsaphSpotLightSticker(EsaphSpotLightSticker esaphSpotLightSticker) {
            this.esaphSpotLightSticker = esaphSpotLightSticker;
            return this;
        }

        public ImageObjectBuilder setMoveAble(boolean value)
        {
            super.isMoveAble = value;
            return this;
        }

        @Override
        public ImageObjectBuilder setCenterInHorizontal(boolean centerInHorizontal) {
            super.setCenterInHorizontal(centerInHorizontal);
            return this;
        }

        @Override
        public ImageObjectBuilder setCenterInVertical(boolean centerInVertical) {
            super.setCenterInVertical(centerInVertical);
            return this;
        }

        @Override
        public ImageObjectBuilder setCenter() {
            super.setCenter();
            return this;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public EsaphSpotLightSticker getEsaphSpotLightSticker() {
            return esaphSpotLightSticker;
        }
    }

    public void addImage(ImageObjectBuilder imageObjectBuilder)
    {
        final View imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);

        if(imageObjectBuilder.getEsaphSpotLightSticker() != null && stickerAddListener != null)
        {
            stickerAddListener.onStickerAdd(imageObjectBuilder, imageObjectBuilder.getEsaphSpotLightSticker());
        }

        imageView.setImageBitmap(imageObjectBuilder.getBitmap());

        MultiTouchListener multiTouchListener = getMultiTouchListener(imageView);
        multiTouchListener.setMoveAble(imageObjectBuilder.isMoveAble());
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl()
        {
            @Override
            public void onClick()
            {
            }

            @Override
            public void onLongClick()
            {
            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(imageRootView, imageView, ViewType.IMAGE);
    }


    private StickerAddListener stickerAddListener;

    public void setStickerAddListener(StickerAddListener stickerAddListener) {
        this.stickerAddListener = stickerAddListener;
    }

    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private void addViewToParent(View rootView, View viewMainContentOfType, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, viewMainContentOfType, addedViews.size());
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    @NonNull
    private MultiTouchListener getMultiTouchListener(View rootMaintElementOfLayout)
    {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                esaphTrashbinView,
                rootMaintElementOfLayout,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);

        multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    /**
     * Get root view by its type i.e image,text and emoji
     *
     * @param viewType image,text or emoji
     * @return rootview
     */
    private View getLayout(final ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                EditText txtText = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
                break;
            case EMOJI:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_emojie, null);
                TextView txtTextEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.setTag(viewType);
            //viewUndo(finalRootView, viewType); for deleting!
        }
        return rootView;
    }

    /**
     * Enable/Disable drawing mode to draw on {@link PhotoEditorView}
     *
     * @param brushDrawingMode true if mode is enabled
     */
    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    public Boolean getBrushDrawableMode()
    {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    public void setBrushPathEffect(PathEffect pathEffect)
    {
        if (brushDrawingView != null)
            brushDrawingView.setPathEffect(pathEffect);
    }

    public void setBrushShaderEffect(EsaphShader shaderEffect)
    {
        brushDrawingView.setShaderEffect(shaderEffect);
    }

    /**
     * set the eraser size
     * <br></br>
     * <b>Note :</b> Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }

    void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserColor(color);
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushEraserSize(float)
     */
    public float getEraserSize() {
        return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushSize(float)
     */
    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    public BrushDrawingView getBrushDrawingView() {
        return brushDrawingView;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushColor(int)
     */
    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    /**
     * <p>
     * Its enables eraser mode after that whenever user drags on screen this will erase the existing
     * paint
     * <br>
     * <b>Note</b> : This eraser will work on paint views only
     * <p>
     */
    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    /*private void viewUndo() {
        if (addedViews.size() > 0) {
            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (mOnPhotoEditorListener != null)
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
        }
    }*/

    private void viewUndo(View removedView, ViewType viewType) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                redoViews.add(removedView);
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
                    mOnPhotoEditorListener.onRemoveViewListener(viewType, addedViews.size());
                }
            }
        }
    }

    /**
     * Undo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to undo
     */
    public boolean undo() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), addedViews.size());
                }
            }
        }
        return addedViews.size() != 0;
    }

    /**
     * Redo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to redo
     */
    public boolean redo() {
        if (redoViews.size() > 0) {
            View redoView = redoViews.get(redoViews.size() - 1);
            if (redoView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo();
            } else {
                redoViews.remove(redoViews.size() - 1);
                parentView.addView(redoView);
                addedViews.add(redoView);
            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), redoView, addedViews.size());
            }
        }
        return redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    /**
     * Removes all the edited operations performed {@link PhotoEditorView}
     * This will also clear the undo and redo stack
     */
    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (addedViews.contains(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }
        addedViews.clear();
        redoViews.clear();
        clearBrushAllViews();
    }

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    public void clearHelperBox() {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View childAt = parentView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
        }
    }

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect {@link CustomEffect.Builder#setParameter(String, Object)}
     */
    public void setFilterEffect(CustomEffect customEffect) {
        parentView.setFilterEffect(customEffect);
    }

    /**
     * Set pre-define filter available
     *
     * @param filterType type of filter want to apply {@link PhotoEditor}
     */
    public void setFilterEffect(PhotoFilter filterType) {
        parentView.setFilterEffect(filterType);
    }


    @Override
    public void onRemoveViewListener(View removedView)
    {
        deleteView(removedView);
    }

    /**
     * A callback to save the edited image asynchronously
     */
    public interface OnSaveListener {

        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        void onSuccess(@NonNull String imagePath);

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        void onFailure(@NonNull Exception exception);
    }


    /**
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     * @deprecated Use {@link #saveAsFile(String, OnSaveListener)} instead
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @Deprecated
    public void saveImage(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, onSaveListener);
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, new SaveSettings.Builder().setTransparencyEnabled(false).build(), onSaveListener);
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options {@link SaveSettings}
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final SaveSettings saveSettings,
                           @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Exception>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        parentView.setDrawingCacheEnabled(false);
                    }

                    @SuppressLint("MissingPermission")
                    @Override
                    protected Exception doInBackground(String... strings) {
                        // Create a media file name
                        File file = new File(imagePath);
                        try {
                            FileOutputStream out = new FileOutputStream(file, false);
                            if (parentView != null) {
                                parentView.setDrawingCacheEnabled(true);
                                Bitmap drawingCache = saveSettings.isTransparencyEnabled()
                                        ? BitmapUtil.removeTransparency(parentView.getDrawingCache())
                                        : parentView.getDrawingCache();
                                drawingCache.compress(Bitmap.CompressFormat.PNG, 100, out);
                            }
                            out.flush();
                            out.close();
                            Log.d(TAG, "Filed Saved Successfully");
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Failed to save File");
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        super.onPostExecute(e);
                        if (e == null) {
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveListener.onSuccess(imagePath);
                        } else {
                            onSaveListener.onFailure(e);
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }

    /**
     * Save the edited image as bitmap
     *
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        saveAsBitmap(new SaveSettings.Builder().setTransparencyEnabled(false).setClearViewsEnabled(false).build(), onSaveBitmap);
    }

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings   builder for multiple save options {@link SaveSettings}
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final SaveSettings saveSettings,
                             @NonNull final OnSaveBitmap onSaveBitmap)
    {
        parentView.saveFilter(new OnSaveBitmap()
        {
            @Override
            public void onBitmapReady(Bitmap saveBitmap)
            {
                new AsyncTask<String, String, Bitmap>()
                {
                    @Override
                    protected void onPreExecute()
                    {
                        super.onPreExecute();
                        clearHelperBox();
                        if(parentView != null)
                        {
                            parentView.setDrawingCacheEnabled(true);
                        }
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings)
                    {
                        if (parentView != null)
                        {
                            parentView.buildDrawingCache(true);
                            return BitmapUtil.removeTransparency(parentView.getDrawingCache());
                        }
                        else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap)
                    {
                        super.onPostExecute(bitmap);

                        if(parentView != null)
                        {
                            parentView.destroyDrawingCache();
                            parentView.setDrawingCacheEnabled(false);
                        }

                        if (bitmap != null)
                        {
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveBitmap.onBitmapReady(bitmap);
                        } else {
                            onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    public void addOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener)
    {
        onPhotoEditorListenerList.add(onPhotoEditorListener);
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    public boolean isCacheEmpty() {
        return addedViews.size() == 0 && redoViews.size() == 0;
    }


    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (redoViews.size() > 0) {
            redoViews.remove(redoViews.size() - 1);
        }
        addedViews.add(brushDrawingView);
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, brushDrawingView, addedViews.size());
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.size() > 0) {
            View removeView = addedViews.remove(addedViews.size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                parentView.removeView(removeView);
            }
            redoViews.add(removeView);
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
            mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
        }
    }

    @Override
    public void onStartDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }


    /**
     * TextShaderBuilder pattern to define {@link PhotoEditor} Instance
     */
    public static class Builder {

        private Context context;
        private PhotoEditorView parentView;
        private ImageView imageView;
        private EsaphTrashbinView esaphTrashbinViewBuilder;
        private BrushDrawingView brushDrawingView;
        private Typeface textTypeface;
        private Typeface emojiTypeface;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView {@link PhotoEditorView}
         */
        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView;
            imageView = photoEditorView.getSource();
            brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        public Builder setEsaphTrashbinViewBuilder(EsaphTrashbinView esaphTrashbinViewBuilder) {
            this.esaphTrashbinViewBuilder = esaphTrashbinViewBuilder;
            return this;
        }

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        /**
         * set false to disable pinch to zoom on text insertion.By deafult its true
         *
         * @param isTextPinchZoomable flag to make pinch to zoom
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
            this.isTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        /**
         * @return build PhotoEditor instance
         */
        public PhotoEditor build() {
            PhotoEditor photoEditor = new PhotoEditor(this);
            this.parentView.setEditor(photoEditor);
            return photoEditor;
        }
    }

    private View viewSelected;
    private void selectOnlyOneView(View viewToSelect)
    {
        if(viewSelected != null && viewSelected == viewToSelect)
            return;

        viewSelected = viewToSelect;
    }

    public PhotoEditorView getPhotoEditorView() {
        return parentView;
    }

    private void deleteView(View view)
    {
        Object o = view.getTag();
        if(o instanceof ViewType)
        {
            viewUndo(view, (ViewType) o);
        }
    }
}
