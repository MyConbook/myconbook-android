package net.myconbook.android;

import com.google.gson.Gson;

import net.myconbook.android.model.ConVersionInfo;
import net.myconbook.android.model.UpdaterInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Updater checker.
 */
public class UpdateChecker {
    private UpdaterInfo mUpdaterInfo;
    private ConVersionInfo mCurrentConVersionInfo;

    private String mConName;

    public void fetchList() throws IOException {
        Log.v("UpdateChecker.fetchList downloading info");

        String response = fetch(BuildConfig.DATA_PATH + "info.json");
        mUpdaterInfo = new Gson().fromJson(response, UpdaterInfo.class);
        mCurrentConVersionInfo = null;
        mConName = null;
    }

    public void fetchCon(String conName) throws IOException {
        Log.v("UpdateChecker.fetchCon setting con " + conName);

        if (mUpdaterInfo == null || mUpdaterInfo.cons == null || mUpdaterInfo.cons.length == 0) {
            return;
        }

        if (conName != null) {
            // Ensure this con is in the set
            boolean hasCon = false;
            for (UpdaterInfo.ConventionInfo con : mUpdaterInfo.cons) {
                if (con.path.equals(conName)) {
                    hasCon = true;
                    break;
                }
            }

            if (!hasCon) {
                conName = null;
                Log.d("UpdateChecker.fetchCon old con was not found");
            }
        }

        if (conName == null) {
            conName = getFirstCon();
            Log.d("UpdateChecker.fetchCon setting con to first con " + conName);
        }

        Log.v("UpdateChecker.fetchCon downloading con version");

        String response = fetch(BuildConfig.DATA_PATH + conName + "/version.json");
        mCurrentConVersionInfo = new Gson().fromJson(response, ConVersionInfo.class);
        mConName = conName;
    }

    private static String fetch(String updateUrl) throws IOException {
        Log.v("UpdateChecker.fetch downloading file " + updateUrl);

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

        UrlFileFetcher.fetch(os, updateUrl);

        String response = os.toString();

        if (response.trim().equals(""))
            throw new NullPointerException("Server response was empty.");

        return response;
    }

    public UpdaterInfo.ConventionInfo[] getCons() {
        if (mUpdaterInfo == null) {
            return null;
        }

        return mUpdaterInfo.cons;
    }

    public String getFirstCon() {
        if (mUpdaterInfo == null || mUpdaterInfo.cons == null || mUpdaterInfo.cons.length == 0) {
            return null;
        }

        return mUpdaterInfo.cons[0].path;
    }

    public int getAppVersion() {
        if (mUpdaterInfo == null || mUpdaterInfo.versions == null) {
            return 0;
        }

        return mUpdaterInfo.versions.android;
    }

    public int getDbVersion() {
        if (mCurrentConVersionInfo == null) {
            return 0;
        }

        return mCurrentConVersionInfo.dbver;
    }

    public int getMapVersion() {
        if (mCurrentConVersionInfo == null) {
            return 0;
        }

        return mCurrentConVersionInfo.mapver;
    }

    public String getConName() {
        return mConName;
    }

    public void overrideConName(String name) {
        mConName = name;
    }
}
