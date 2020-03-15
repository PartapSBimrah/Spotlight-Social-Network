package esaph.spotlight.services.OtherWorkers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class UploadNewProfilbild extends Worker
{
    public static final String paramFilePath = "esaph.spotlight.worker.upload.profilbild.param.filepath";
    private Context context;

    public UploadNewProfilbild(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork()
    {
        File outputFile = null;
        try
        {
            String path = getInputData().getString(UploadNewProfilbild.paramFilePath);
            if(path == null)
                return Result.failure();

            String Path = Uri.parse(path).getPath();
            if(Path == null)
                return Result.failure();


            File outputDir = context.getCacheDir(); // context being the Activity pointer
            outputFile = File.createTempFile("TempImageProfilFile", "pbtf", outputDir);

            Bitmap bitmapOriginal = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(path));
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream); //Compressing profilbild.
            fileOutputStream.close();



            SocketResources resources = new SocketResources();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLUPB");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SSLSocket sslSocket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            long length = StorageHandlerProfilbild.fileLength(outputFile);
            writer.println(length);
            writer.flush();

            if (reader.readLine().equals("1"))
            {
                byte[] originalBytes = new byte[(int) length];
                InputStream inputStream = new BufferedInputStream(new FileInputStream(outputFile));
                OutputStream outputStream = sslSocket.getOutputStream();

                int count;
                while ((count = inputStream.read(originalBytes)) > 0)
                {
                    outputStream.write(originalBytes, 0, count);
                    outputStream.flush();
                }

                inputStream.close();

                if(reader.readLine().equals("1"))
                {
                    File dts = StorageHandlerProfilbild.getFile(context,
                            Long.toString(SpotLightLoginSessionHandler.getLoggedUID()),
                            true);

                    copy(outputFile, dts); //There its saves it. To folder.
                    reader.close();
                    writer.close();
                    sslSocket.close();
                    Data data = Data.EMPTY;
                    return Result.success(data);
                }
            }

            reader.close();
            writer.close();
            sslSocket.close();
            return Result.failure();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "UploadNewProfilbild Exception: " + ec);
            return Result.failure();
        }
        finally
        {
            if(outputFile != null)
            {
                outputFile.delete();
            }
        }
    }

    public static void copy(File src, File dst) throws IOException
    {
        if(dst.exists()) dst.delete();

        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
