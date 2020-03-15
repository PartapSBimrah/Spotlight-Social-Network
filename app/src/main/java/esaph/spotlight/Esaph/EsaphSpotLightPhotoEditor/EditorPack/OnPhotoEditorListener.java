/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.view.View;
import android.widget.EditText;

import esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects.SpotMakerEdittext;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 18/01/2017
 * <p>
 * This are the callbacks when any changes happens while editing the photo to make and custimization
 * on client side
 * </p>
 */
public interface OnPhotoEditorListener {

    /**
     * When user long press the existing text this event will trigger implying that user want to
     * edit the current {@link android.widget.TextView}
     *
     * @param spotMakerEdittext  view on which the long press occurs
     * @param text      current text set on the view
     * @param colorCode current color value set on view
     */
    void onEditTextChangeListener(SpotMakerEdittext spotMakerEdittext, String text, int colorCode);

    /**
     * This is a callback when user adds any view on the {@link PhotoEditorView} it can be
     * brush,text or sticker i.e bitmap on parent view
     *
     * @param viewType           enum which define type of view is added
     * @param numberOfAddedViews number of views currently added
     * @see ViewType
     */
    void onAddViewListener(ViewType viewType, View view, int numberOfAddedViews);


    /**
     * This is a callback when user remove any view on the {@link PhotoEditorView} it happens when usually
     * undo and redo happens or text is removed
     *
     * @param numberOfAddedViews number of views currently added
     * @deprecated Use {@link OnPhotoEditorListener#onRemoveViewListener(ViewType, int)} instead
     */
    @Deprecated
    void onRemoveViewListener(int numberOfAddedViews);


    /**
     * This is a callback when user remove any view on the {@link PhotoEditorView} it happens when usually
     * undo and redo happens or text is removed
     *
     * @param viewType           enum which define type of view is added
     * @param numberOfAddedViews number of views currently added
     */
    void onRemoveViewListener(ViewType viewType, int numberOfAddedViews);

    /**
     * A callback when user start dragging a view which can be
     * any of {@link ViewType}
     *
     * @param viewType enum which define type of view is added
     */
    void onStartViewChangeListener(ViewType viewType, SpotMakerEdittext edittext);

    void onStartViewChangeListener(ViewType viewType);


    /**
     * A callback when user pauseAndSeekToZero/up touching a view which can be
     * any of {@link ViewType}
     *
     * @param viewType enum which define type of view is added
     */
    void onStopViewChangeListener(ViewType viewType);


    void onViewTouchedOutsideBounds();
}
