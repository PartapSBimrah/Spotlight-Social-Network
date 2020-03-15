package esaph.spotlight.navigation.spotlight.Chats.ListeChat;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class ProgressBarAnimation extends Animation
{
    private RoundCornerProgressBar progressBar;
    private float from;
    private float  to;

    public ProgressBarAnimation(RoundCornerProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}