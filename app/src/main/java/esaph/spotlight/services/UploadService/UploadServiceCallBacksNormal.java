package esaph.spotlight.services.UploadService;

public interface UploadServiceCallBacksNormal
{
    void onPostFailedUpload(UploadPost uploadPost);
    void onPostUploading(UploadPost uploadPost);
    void onProgressUpdate(UploadPost uploadPost, int progress);
    void onPostUploadSuccess(UploadPost uploadPost, long PPID);
}
