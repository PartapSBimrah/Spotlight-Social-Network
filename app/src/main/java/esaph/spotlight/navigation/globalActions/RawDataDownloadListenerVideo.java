/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import java.io.File;

public interface RawDataDownloadListenerVideo
{
    void onVideoDownloaded(File file);
    void onVideoDownloadFailed(String PID);
}
