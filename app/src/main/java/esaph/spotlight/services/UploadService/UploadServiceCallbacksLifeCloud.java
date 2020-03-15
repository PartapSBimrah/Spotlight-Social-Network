package esaph.spotlight.services.UploadService;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;

public interface UploadServiceCallbacksLifeCloud
{
    void onPostFailedUpload(LifeCloudUpload lifeCloudUpload);
    void onPostUploading(LifeCloudUpload lifeCloudUpload);
    void onPostUploadSuccess(LifeCloudUpload lifeCloudUploadInternPid, LifeCloudUpload lifeCloudUploadPidServer);
}
