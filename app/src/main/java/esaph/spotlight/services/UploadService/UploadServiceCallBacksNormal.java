/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.UploadService;

public interface UploadServiceCallBacksNormal
{
    void onPostFailedUpload(UploadPost uploadPost);
    void onPostUploading(UploadPost uploadPost);
    void onProgressUpdate(UploadPost uploadPost, int progress);
    void onPostUploadSuccess(UploadPost uploadPost, long PPID);
}
