package net.myconbook.android;

import android.content.Context;

import org.apache.http.client.ClientProtocolException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapDownloader {
    private Context mContext;
    private static final String mSavePath = "maps";
    private String mDownloadPath;

    public MapDownloader(Context context, String downloadPath) {
        mContext = context;
        mDownloadPath = downloadPath;
    }

    public boolean update() {
        // Download the update zip
        byte[] byteArray;

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            UrlFileFetcher.fetch(outStream, mDownloadPath);
            byteArray = outStream.toByteArray();
            outStream.close();
        } catch (ClientProtocolException e) {
            Log.e("MapDownloader.update error downloading map file", e);
            return false;
        } catch (IOException e) {
            Log.e("MapDownloader.update error downloading map file", e);
            return false;
        }

        // Delete the existing files
        File fileDir = mContext.getDir(mSavePath, Context.MODE_PRIVATE);
        for (File file : fileDir.listFiles()) {
            if (!file.delete()) {
                Log.w("MapDownloader.update unable to delete file " + file.getAbsolutePath());
            }
        }

        // Extract the new ones
        try {
            ByteArrayInputStream inStream = new ByteArrayInputStream(byteArray);
            ZipInputStream zis = new ZipInputStream(inStream);
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                String filename = ze.getName();
                File newFile = new File(fileDir, filename);
                FileOutputStream fout = new FileOutputStream(newFile);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
            inStream.close();
        } catch (IOException e) {
            Log.e("MapDownloader.update error extracting map files", e);
            return false;
        }

        return true;
    }
}