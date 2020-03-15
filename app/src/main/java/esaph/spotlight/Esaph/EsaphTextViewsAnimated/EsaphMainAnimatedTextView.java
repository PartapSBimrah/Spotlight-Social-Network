package esaph.spotlight.Esaph.EsaphTextViewsAnimated;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.animation.Animation;

public abstract class EsaphMainAnimatedTextView extends AppCompatTextView
{
    public EsaphMainAnimatedTextView(Context context) {
        this(context, null);
    }

    public EsaphMainAnimatedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EsaphMainAnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setAnimationListener(Animation.AnimationListener listener);

    public abstract void setProgress(float progress);

    public abstract void animateText(CharSequence text);
}
