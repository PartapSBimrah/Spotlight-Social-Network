/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public abstract class EsaphActivity extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public abstract boolean onActivityDispatchBackPressEvent();



    /*
    InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if(inputMethodManager != null && activity.getCurrentFocus() != null) //No focus, if visibility is set to View.GONE!
        {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
     */
}
