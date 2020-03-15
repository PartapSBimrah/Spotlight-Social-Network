package esaph.spotlight.PreLogin;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;
import esaph.spotlight.R;

public class EmojiExcludeFilter implements InputFilter
{
    private Context context;

    public EmojiExcludeFilter(Context context)
    {
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        for (int i = start; i < end; i++) {
            int type = Character.getType(source.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL)
            {
                Toast.makeText(context, context.getResources().getString(R.string.txt_smileys_not_allowed), Toast.LENGTH_SHORT).show();
                return "";
            }
        }
        return null;
    }
}