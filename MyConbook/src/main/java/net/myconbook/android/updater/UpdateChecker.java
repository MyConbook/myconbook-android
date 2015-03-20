package net.myconbook.android.updater;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import net.myconbook.android.model.ConVersionInfo;
import net.myconbook.android.model.UpdaterInfo;

import java.util.List;

/**
 * Updater checker.
 */
public class UpdateChecker {
    public UpdaterInfo mUpdaterInfo;
    public ConVersionInfo mCurrentConVersionInfo;

    private String mConName;

    public static UpdateChecker load(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String storedCheckerJson = sharedPrefs.getString("savedChecker", null);
        if (storedCheckerJson != null) {
            return new Gson().fromJson(storedCheckerJson, UpdateChecker.class);
        }

        return new UpdateChecker();
    }

    public void save(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit().putString("savedChecker", new Gson().toJson(this)).apply();
    }

    public List<UpdaterInfo.ConventionInfo> getCons() {
        if (mUpdaterInfo == null) {
            return null;
        }

        return mUpdaterInfo.cons;
    }

    public String getFirstCon() {
        if (mUpdaterInfo == null || mUpdaterInfo.cons == null || mUpdaterInfo.cons.isEmpty()) {
            return null;
        }

        return mUpdaterInfo.cons.get(0).path;
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

    public boolean isCurrentConInList() {
        if (mConName == null) return false;
        List<UpdaterInfo.ConventionInfo> cons = getCons();
        if (cons == null || cons.isEmpty()) return false;

        for (UpdaterInfo.ConventionInfo info : cons) {
            if (mConName.equals(info.path)) {
                return true;
            }
        }

        return false;
    }
}
