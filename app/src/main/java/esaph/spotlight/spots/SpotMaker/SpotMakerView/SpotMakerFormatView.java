package esaph.spotlight.spots.SpotMaker.SpotMakerView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.spots.SpotMaker.SpotMakerView.Formats.SpotFormatEditListener;

public abstract class SpotMakerFormatView extends FrameLayout
{
    private Context context;

    public SpotMakerFormatView(Context context)
    {
        super(context);
        this.context = context;
        init();
    }

    public SpotMakerFormatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SpotMakerFormatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(21)
    public SpotMakerFormatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private SpotFormatEditListener spotFormatEditListener;
    public void setEditListener(SpotFormatEditListener spotFormatEditListener)
    {
        this.spotFormatEditListener = spotFormatEditListener;
    }

    public SpotFormatEditListener getSpotFormatEditListener() {
        return spotFormatEditListener;
    }

    public abstract ConversationMessage getSpotMessage(JSONObject jsonObject);

    public abstract void onValuesChanges(JSONObject jsonObject);

    public abstract int inflateLayout(Context context);

    public abstract void onSetupView(View view);

    private void init()
    {
        View view = inflate(context, inflateLayout(context), null);
        addView(view);
        onSetupView(view);
    }
}
