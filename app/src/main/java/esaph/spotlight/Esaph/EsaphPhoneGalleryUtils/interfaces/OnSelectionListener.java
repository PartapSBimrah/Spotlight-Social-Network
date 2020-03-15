package esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.interfaces;

import android.view.View;

import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.modals.Img;


public interface OnSelectionListener {
    void onClick(Img Img, View view, int position);

    void onLongClick(Img img, View view, int position);
}
