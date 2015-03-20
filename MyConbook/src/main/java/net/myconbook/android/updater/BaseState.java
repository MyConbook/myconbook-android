package net.myconbook.android.updater;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.myconbook.android.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

abstract class BaseState {
    protected UpdateStateMachine.InternalState mGlobal;

    public BaseState(UpdateStateMachine.InternalState global) {
        mGlobal = global;
    }

    public abstract State run();

    protected SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mGlobal.context);
    }

    protected static String fetchString(String updateUrl) throws IOException {
        Log.v("USM:BaseState.fetchString downloading file " + updateUrl);

        OutputStream os = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int oneByte) throws IOException {
                this.string.append((char) oneByte);
            }

            public String toString() {
                return this.string.toString();
            }
        };

        fetchFile(os, updateUrl);
        String response = os.toString();

        if (response.trim().equals(""))
            throw new NullPointerException("Server response was empty.");

        return response;
    }

    protected static void fetchFile(OutputStream outStream, String urlToDownload) throws IOException {
        URL url = new URL(urlToDownload);
        URLConnection connection = url.openConnection();
        connection.connect();

        InputStream input = new BufferedInputStream(url.openStream());

        byte data[] = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            outStream.write(data, 0, count);
        }

        outStream.flush();
        outStream.close();
        input.close();
    }

    protected State alert(UpdaterAlertException e) {
        mGlobal.lastError = e;
        mGlobal.lastErrorWasAlert = true;
        return State.Exit;
    }

    protected State noalert(UpdaterAlertException e) {
        // Nonfailing alert state when not in an initial force state
        Log.w("UAE:BaseState.noalert triggered", e);
        if (mGlobal.entryState.force) {
            mGlobal.lastError = e;
            mGlobal.lastErrorWasAlert = true;
        } else {
            mGlobal.lastError = null;
            mGlobal.lastErrorWasAlert = false;
        }
        return State.Exit;
    }

    protected State error(UpdaterAlertException e) {
        mGlobal.lastError = e;
        mGlobal.lastErrorWasAlert = false;
        return State.Exit;
    }
}
