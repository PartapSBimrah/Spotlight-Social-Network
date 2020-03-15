package esaph.spotlight.navigation.globalActions;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;
import android.widget.ImageView;

public class EsaphImageViewAscpectRatio extends AppCompatImageView
{
    public EsaphImageViewAscpectRatio(Context context) {
        super(context);
    }

    public EsaphImageViewAscpectRatio(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EsaphImageViewAscpectRatio(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }


}
