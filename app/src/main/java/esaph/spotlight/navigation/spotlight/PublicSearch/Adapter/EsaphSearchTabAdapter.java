/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicSearch.Adapter;

import android.widget.BaseAdapter;

import java.util.List;

public abstract class EsaphSearchTabAdapter extends BaseAdapter
{
    public abstract void clearAll();
    public abstract void addAll(List<Object> list);
}
