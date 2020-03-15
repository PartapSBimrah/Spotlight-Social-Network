package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.graphics.Bitmap;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @since 8/8/2018
 * TextShaderBuilder Class to apply multiple save options
 */
public class SaveSettings {
    private boolean isTransparencyEnabled;
    private boolean isClearViewsEnabled;

    boolean isTransparencyEnabled() {
        return isTransparencyEnabled;
    }

    boolean isClearViewsEnabled() {
        return isClearViewsEnabled;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    public static class Builder {
        private boolean isTransparencyEnabled = true;
        private boolean isClearViewsEnabled = true;

        /**
         * Define a flag to enable transparency while saving image
         *
         * @param transparencyEnabled true if enabled
         * @return TextShaderBuilder
         * @see BitmapUtil#removeTransparency(Bitmap)
         */
        public Builder setTransparencyEnabled(boolean transparencyEnabled) {
            isTransparencyEnabled = transparencyEnabled;
            return this;
        }

        /**
         * Define a flag to clear the view after saving the image
         *
         * @param clearViewsEnabled true if you want to clear all the views on {@link PhotoEditorView}
         * @return TextShaderBuilder
         */
        public Builder setClearViewsEnabled(boolean clearViewsEnabled) {
            isClearViewsEnabled = clearViewsEnabled;
            return this;
        }

        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
