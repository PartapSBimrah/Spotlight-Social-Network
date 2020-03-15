/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.UploadService;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;

public interface UploadServiceCallbacksLifeCloud
{
    void onPostFailedUpload(LifeCloudUpload lifeCloudUpload);
    void onPostUploading(LifeCloudUpload lifeCloudUpload);
    void onPostUploadSuccess(LifeCloudUpload lifeCloudUploadInternPid, LifeCloudUpload lifeCloudUploadPidServer);
}
