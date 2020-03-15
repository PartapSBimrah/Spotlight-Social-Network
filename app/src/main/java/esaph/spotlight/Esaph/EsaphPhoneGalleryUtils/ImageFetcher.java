package esaph.spotlight.Esaph.EsaphPhoneGalleryUtils;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import esaph.spotlight.Esaph.EsaphPhoneGalleryUtils.modals.Img;

public class ImageFetcher extends AsyncTask<Cursor, Void, ImageFetcher.ModelList>
{
    private final static Object lock = new Object();
    private ArrayList<Img> LIST = new ArrayList<>();
    private ArrayList<String> preSelectedUrls = new ArrayList<>();
    private SoftReference<InternGalleryLoaderListener> internGalleryLoaderListenerWeakReference;

    public ImageFetcher(InternGalleryLoaderListener internGalleryLoaderListener)
    {
        this.internGalleryLoaderListenerWeakReference = new SoftReference<>(internGalleryLoaderListener);
    }

    public interface InternGalleryLoaderListener
    {
        void onLoaded(ModelList img);
    }

    @Override
    protected ModelList doInBackground(Cursor... cursors) {
        Cursor cursor = cursors[0];
        try {
            if (cursor != null)
            {
                int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int contentUrl = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                cursor.moveToFirst();

                synchronized (ImageFetcher.lock)
                {
                    if(cursor.moveToFirst())
                    {
                        do {
                            Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contentUrl));
                            Img img = new Img("", "" + path, cursor.getString(data), "");
                            LIST.add(img);
                        }
                        while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelList(LIST, new ArrayList<Img>());
    }

    @Override
    protected void onPostExecute(ModelList modelList)
    {
        super.onPostExecute(modelList);
        InternGalleryLoaderListener internGalleryLoaderListener = internGalleryLoaderListenerWeakReference.get();
        if(internGalleryLoaderListener != null)
        {
            internGalleryLoaderListener.onLoaded(modelList);
        }
    }

    public class ModelList {
        ArrayList<Img> LIST = new ArrayList<>();
        ArrayList<Img> selection = new ArrayList<>();

        public ModelList(ArrayList<Img> LIST, ArrayList<Img> selection) {
            this.LIST = LIST;
            this.selection = selection;
        }

        public ArrayList<Img> getLIST() {
            return LIST;
        }

        public ArrayList<Img> getSelection() {
            return selection;
        }
    }

}
