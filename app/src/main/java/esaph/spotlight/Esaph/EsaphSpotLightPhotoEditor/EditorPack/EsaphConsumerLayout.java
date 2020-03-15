package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EditorPack;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class EsaphConsumerLayout extends FrameLayout {
    public EsaphConsumerLayout(@NonNull Context context) {
        super(context);
    }

    public EsaphConsumerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EsaphConsumerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
