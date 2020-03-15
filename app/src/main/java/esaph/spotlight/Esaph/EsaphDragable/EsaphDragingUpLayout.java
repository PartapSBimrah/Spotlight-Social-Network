package esaph.spotlight.Esaph.EsaphDragable;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EsaphDragingUpLayout extends LinearLayout
{
    public EsaphDragingUpLayout(Context context) {
        super(context);
    }

    public EsaphDragingUpLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EsaphDragingUpLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    public EsaphDragingUpLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
