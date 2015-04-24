package net.myconbook.android.updater;

import android.content.Context;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.Log;

import org.apache.http.client.ClientProtocolException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapUpdateState extends BaseState {
    public MapUpdateState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        Log.v("USM:MapUpdateState.run downloading maps from network for con: " + mGlobal.updateChecker.getConName());
        mGlobal.pushMessage(getString(R.string.downloading_maps));

        // Download
        String downloadPath = BuildConfig.DATA_PATH + mGlobal.updateChecker.getConName() + "/maps.zip";
        if (!update(downloadPath)) {
            return alert(new UpdaterAlertException("Error updating map files. Clear the application data and try again."));
        }

        getSharedPrefs().edit().putInt("currentMapVer", mGlobal.updateChecker.getMapVersion()).commit();

        return State.Success;
    }

    public boolean update(String downloadPath) {
        // Download the update zip
        byte[] byteArray;

        if (mGlobal.context == null) {
            Log.w("USM:MapUpdateState.update mContext is null");
            return false;
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            fetchFile(outStream, downloadPath);
            byteArray = outStream.toByteArray();
            outStream.close();
        } catch (ClientProtocolException e) {
            Log.e("MapDownloader.update error downloading map file", e);
            Log.c(e);
            return false;
        } catch (IOException e) {
            Log.e("MapDownloader.update error downloading map file", e);
            //Log.c(e);
            return false;
        }

        // Delete the existing files
        File fileDir = mGlobal.context.getDir("maps", Context.MODE_PRIVATE);
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
            Log.c(e);
            return false;
        }

        return true;
    }
}
