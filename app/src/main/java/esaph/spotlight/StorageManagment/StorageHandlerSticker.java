package esaph.spotlight.StorageManagment;

import android.graphics.Bitmap;

public class StorageHandlerSticker
{
    private static final int STICKER_MAX_WIDTH = 512;
    private static final int STICKER_MAX_HEIGHT = 512;

    public static Bitmap scaleSticker(Bitmap bitmap)
    {
        if(bitmap != null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(width == StorageHandlerSticker.STICKER_MAX_WIDTH && height == StorageHandlerSticker.STICKER_MAX_HEIGHT)
            {
                return bitmap;
            }

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1)
            {
                width = StorageHandlerSticker.STICKER_MAX_WIDTH;
                height = (int) (width / bitmapRatio);
            }
            else
            {
                height = StorageHandlerSticker.STICKER_MAX_HEIGHT;
                width = (int) (height * bitmapRatio);
            }
            Bitmap temp = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return temp;
        }
        return null;
    }

    /*

    public static void saveSticker(Context context, Bitmap bitmap, File file)
    {
        FileOutputStream outputStream = null;

        try
        {
            Bitmap bitmapScaled = scaleBitmap(bitmap);
            outputStream = new FileOutputStream(file);
            bitmapScaled.compress(Bitmap.CompressFormat.JPEG, EsaphGlobalValues.COMP_RATE_STICKER, outputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (Exception ec)
                {
                    Log.i(context.getClass().getName(), "saveLifeCloudImageWithCompression() failed closing stream: " + ec);
                }
            }
        }
    }*/

    /*

    public static void updateInternPidWithServerPid(Context context,
                                                    File fileIntern,
                                                    File serverId)
    {
        try
        {
            String fileName = fileIntern.getName();

            String newFileName = fileName.replace(fileIntern.getName(), serverId.getName());

            File newFile = new File(fileIntern.getParent(), newFileName);

            if (fileExists(newFile))
            {
                context.deleteFile(newFileName);
            }

            if(!fileIntern.renameTo(newFile))
            {
                context.deleteFile(fileIntern.getName());
            }
            else
            {
                System.out.println("Trying to update RENAMED: " + fileIntern.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
*/

}
