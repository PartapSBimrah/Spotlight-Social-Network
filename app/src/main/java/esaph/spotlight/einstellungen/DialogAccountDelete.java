package esaph.spotlight.einstellungen;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import esaph.spotlight.R;

public class DialogAccountDelete extends Dialog
{
    public DialogAccountDelete(@NonNull Context context)
    {
        super(context);
        setContentView(R.layout.dialog_delete_account);
        setCanceledOnTouchOutside(false);
    }
}
