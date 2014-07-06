package net.myconbook.android.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.DbLoader;
import net.myconbook.android.Log;
import net.myconbook.android.MapDownloader;
import net.myconbook.android.UpdateChecker;
import net.myconbook.android.UrlFileFetcher;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UpdateFragment extends Fragment {
    public static final int FINISH_NONE = 0;
    public static final int FINISH_LIST_OK = 1;
    public static final int FINISH_CON_OK = 2;
    public static final int FINISH_ERROR = 3;
    public static final int FINISH_STARTED = 10;

    private AsyncFetchList mAsyncList;
    private AsyncFetchCon mAsyncCon;

    private Handler mOnEventHandler;
    private String mLastMessage;
    public UpdateChecker mUpdateChecker;
    private SharedPreferences mSharedPrefs;
    private boolean isForcingUpgrade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if (mUpdateChecker == null) {
            String storedCheckerJson = mSharedPrefs.getString("savedChecker", null);
            if (storedCheckerJson != null) {
                mUpdateChecker = new Gson().fromJson(storedCheckerJson, UpdateChecker.class);
            }
        }
    }

    public void setUpdaterEvent(Handler event) {
        mOnEventHandler = event;
    }

    public void startListUpdate(boolean forceRefresh) {
        if (!isAdded() || mAsyncList != null) {
            return;
        }

        Log.v("UpdateFragment.startListUpdate forced: " + forceRefresh);

        // Start updater
        mAsyncList = new AsyncFetchList();
        mAsyncList.execute(forceRefresh);
    }

    public void startConUpdate(String conName) {
        if (!isAdded() || mAsyncCon != null) {
            return;
        }

        Log.v("UpdateFragment.startConUpdate name: " + conName);

        // Start updater
        mAsyncCon = new AsyncFetchCon();
        mAsyncCon.execute(conName);
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public boolean isUpdating() {
        return mAsyncList != null || mAsyncCon != null;
    }

    public String getConName() {
        if (mUpdateChecker == null) {
            return null;
        }

        return mUpdateChecker.getConName();
    }

    private void askToUpdateApp() {
        Log.v("UpdateFragment.askToUpdateApp asking to update the application");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setMessage("A new version of MyConbook is available. Do you want to go to the Market to upgrade?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("market://details?id=" + BuildConfig.PACKAGE_NAME));

                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            showError("Could not launch the Android Market.", false);
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void showError(String message, final boolean fatal) {
        Log.w("UpdateFragment.showError displaying error dialog");

        if (getActivity() == null) {
            Log.w("UpdateFragment.showError activity is null");
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(message).setCancelable(false);

        if (!fatal) {
            alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mAsyncList = new AsyncFetchList();
                    mAsyncList.execute(true);
                    dialog.dismiss();
                }
            });
        }

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mOnEventHandler.sendEmptyMessage(0);
            }
        }).show();
    }

    private class AsyncFetchList extends AsyncTask<Boolean, String, UpdaterReturn> {
        private UpdaterAlertException exception;

        @Override
        protected void onPreExecute() {
            publishProgress("Fetching convention list...");
        }

        @Override
        protected UpdaterReturn doInBackground(Boolean... arg0) {
            isForcingUpgrade = arg0[0];

            Log.v("UpdateFragment.AsyncFetchList.doInBackground forced: " + isForcingUpgrade);

            // Get app version from OS
            Log.v("UpdateFragment.AsyncFetchList.doInBackground checking application version");

            // Check to see if we should force a database upgrade
            int curAppVer = BuildConfig.VERSION_CODE;
            int latestAppVer = mSharedPrefs.getInt("newestAppVer", 0);
            if (curAppVer > latestAppVer) {
                Log.v("UpdateFragment.AsyncFetchList.doInBackground package is newer than last run. " + curAppVer + " over " + latestAppVer);
                mSharedPrefs.edit().putInt("newestAppVer", curAppVer).commit();
                isForcingUpgrade = true;
            }

            // If we're not forcing an upgrade, and it's not time to fetchList, don't continue
            if (!isForcingUpgrade && !getShouldUpdate() && mUpdateChecker != null) {
                return UpdaterReturn.OK;
            }

            // Download con list file
            Log.v("UpdateFragment.AsyncFetchList.doInBackground downloading con list file");

            String previousCon = null;
            if (mUpdateChecker != null) {
                previousCon = mUpdateChecker.getConName();
            }

            try {
                mUpdateChecker = new UpdateChecker();
                try {
                    mUpdateChecker.fetchList();
                } catch (ClientProtocolException e) {
                    Log.w("UpdateFragment.AsyncFetchList.doInBackground request error", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                } catch (NullPointerException e) {
                    Log.w("UpdateFragment.AsyncFetchList.doInBackground server response was empty", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                } catch (IOException e) {
                    Log.w("UpdateFragment.AsyncFetchList.doInBackground error retrieving list file", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                } catch (JsonSyntaxException e) {
                    Log.w("UpdateFragment.AsyncFetchList.doInBackground error parsing JSON", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                }
            } catch (UpdaterAlertException e) {
                exception = e;

                if (e.getIsFatal())
                    return UpdaterReturn.Error;
                else
                    return UpdaterReturn.Alert;
            }

            if (previousCon != null) {
                mUpdateChecker.overrideConName(previousCon);
            }

            // Warn to upgrade if newer version
            int newAppVer = mUpdateChecker.getAppVersion();
            if (newAppVer > curAppVer) {
                // Recommend upgrade from Market
                Log.v("UpdateFragment.AsyncFetchList.doInBackground recommending upgrade from app ver " + curAppVer + " to " + newAppVer);
                return UpdaterReturn.UpdateApp;
            }

            // Persist the update checker
            String savedCheckerJson = new Gson().toJson(mUpdateChecker);
            mSharedPrefs.edit().putString("savedChecker", savedCheckerJson).commit();

            return UpdaterReturn.OK;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mLastMessage = values[0];
            mOnEventHandler.sendMessage(mOnEventHandler.obtainMessage(FINISH_STARTED, mLastMessage));
        }

        @Override
        protected void onPostExecute(UpdaterReturn result) {
            Log.v("UpdateFragment.AsyncFetchList.onPostExecute completed with result " + result);

            mLastMessage = null;

            switch (result) {
                case UpdateApp:
                    askToUpdateApp();
                    mOnEventHandler.sendEmptyMessage(FINISH_NONE);
                    break;
                case Error:
                    if (exception != null)
                        showError(exception.getMessage(), true);

                    mOnEventHandler.sendEmptyMessage(FINISH_ERROR);
                    break;
                case Alert:
                    if (exception != null)
                        showError(exception.getMessage(), false);

                    mOnEventHandler.sendEmptyMessage(FINISH_LIST_OK);
                    break;
                case OK:
                    mOnEventHandler.sendEmptyMessage(FINISH_LIST_OK);
                    break;
                default:
                    mOnEventHandler.sendEmptyMessage(FINISH_NONE);
                    break;
            }

            mAsyncList = null;
        }
    }

    private class AsyncFetchCon extends AsyncTask<String, String, UpdaterReturn> {
        private UpdaterAlertException exception;
        private UpdateMessage updateMessage = new UpdateMessage() {
            @Override
            public void message(String string) {
                publishProgress(string);
            }
        };

        @Override
        protected void onPreExecute() {
            publishProgress("Fetching convention data...");
        }

        @Override
        protected UpdaterReturn doInBackground(String... arg0) {
            String conName = arg0[0];

            try {
                Log.v("UpdateFragment.AsyncFetchCon.doInBackground fetching data for " + conName);

                if (!conName.equals(mUpdateChecker.getConName())) {
                    Log.v("UpdateFragment.AsyncFetchCon.doInBackground forcing upgrade due to con change");
                    isForcingUpgrade = true;
                }

                try {
                    mUpdateChecker.fetchCon(conName);
                } catch (IOException e) {
                    Log.w("UpdateFragment.AsyncFetchCon.doInBackground error retrieving con data", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                } catch (JsonSyntaxException e) {
                    Log.w("UpdateFragment.AsyncFetchList.doInBackground error parsing JSON", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error checking for updates.");
                }

                // Check database version
                Log.v("UpdateFragment.AsyncFetchCon.doInBackground checking database version");
                try {
                    // Initialize DB if it doesn't exist
                    if (!initDb() || isForcingUpgrade) {
                        // Force an update
                        Log.v("UpdateFragment.AsyncFetchCon.doInBackground forcing DB upgrade");
                        updateDb(conName, updateMessage);
                    } else {
                        // Check version then update
                        int newDbVer = mUpdateChecker.getDbVersion();
                        int curDbVer = getCurrentDbVer();
                        if (newDbVer > curDbVer) {
                            Log.v("UpdateFragment.AsyncFetchCon.doInBackground upgrading database from " + curDbVer + " to " + newDbVer);
                            updateDb(conName, updateMessage);
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.e("UpdateFragment.AsyncFetchCon.doInBackground error updating database", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error updating database.", true);
                } catch (IOException e) {
                    Log.e("UpdateFragment.AsyncFetchCon.doInBackground error updating database", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error updating database.", true);
                } catch (NullPointerException e) {
                    Log.e("UpdateFragment.AsyncFetchCon.doInBackground null pointer exception", e);
                    Log.c(e);
                    throw new UpdaterAlertException("Error updating database.", true);
                }

                // Check maps version
                Log.v("UpdateFragment.AsyncFetchCon.doInBackground checking maps version");
                int newMapVer = mUpdateChecker.getMapVersion();
                int curMapVer = mSharedPrefs.getInt("currentMapVer", -1);
                if (isForcingUpgrade || (newMapVer > curMapVer)) {
                    Log.v("UpdateFragment.AsyncFetchCon.doInBackground upgrading map from " + curMapVer + " to " + newMapVer + " forced: " + isForcingUpgrade);
                    updateMap(conName, updateMessage);
                    mSharedPrefs.edit().putInt("currentMapVer", newMapVer).commit();
                }

                // Store last successful update time
                mSharedPrefs.edit().putLong("lastUpdateTime", System.currentTimeMillis()).commit();

                // Persist the update checker
                String savedCheckerJson = new Gson().toJson(mUpdateChecker);
                mSharedPrefs.edit().putString("savedChecker", savedCheckerJson).commit();

                // Nothing left to do
                isForcingUpgrade = false;
            } catch (UpdaterAlertException e) {
                exception = e;

                if (e.getIsFatal())
                    return UpdaterReturn.Error;
                else
                    return UpdaterReturn.Alert;
            }

            return UpdaterReturn.OK;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mLastMessage = values[0];
            mOnEventHandler.sendMessage(mOnEventHandler.obtainMessage(FINISH_STARTED, mLastMessage));
        }

        @Override
        protected void onPostExecute(UpdaterReturn result) {
            Log.v("UpdateFragment.AsyncFetchCon.onPostExecute completed with result " + result);

            mLastMessage = null;

            switch (result) {
                case Error:
                    if (exception != null)
                        showError(exception.getMessage(), true);

                    mOnEventHandler.sendEmptyMessage(FINISH_ERROR);
                    break;
                case Alert:
                    if (exception != null)
                        showError(exception.getMessage(), false);

                    mOnEventHandler.sendEmptyMessage(FINISH_CON_OK);
                    break;
                case OK:
                    mOnEventHandler.sendEmptyMessage(FINISH_CON_OK);
                    break;
                default:
                    mOnEventHandler.sendEmptyMessage(FINISH_NONE);
                    break;
            }

            mAsyncCon = null;
        }
    }

    /* === Actual update code === */

    public boolean getShouldUpdate() {
        long lastUpdateTime = mSharedPrefs.getLong("lastUpdateTime", 0);
        long now = System.currentTimeMillis();
        Log.d("UpdateFragment.getShouldUpdate minutes since last fetchList: " + ((now - lastUpdateTime) / 1000f / 60f));

        return (lastUpdateTime + (60 * 60 * 1000)) < now;
    }

    /* === App === */

    private boolean initDb() throws UpdaterAlertException {
        Log.v("UpdateFragment.initDb checking existing database");

        File f = getActivity().getDatabasePath(DbLoader.DATABASE_NAME);

        if (!f.exists()) {
            // Create
            Log.v("UpdateFragment.initDb creating blank database");

            try {
                getActivity().openOrCreateDatabase(DbLoader.DATABASE_NAME, 0, null).close();
            } catch (SQLiteException e) {
                Log.e("UpdateFragment.initDb error creating blank database", e);
                Log.c(e);
                throw new UpdaterAlertException("Error creating database file. Restart the application and try again.");
            }

            return false;
        }

        return true;
    }

    /* === Database === */

    private int getCurrentDbVer() {
        SQLiteDatabase db = getActivity().openOrCreateDatabase(DbLoader.DATABASE_NAME, 0, null);
        DbLoader loader = new DbLoader(db);
        db.close();

        if (!loader.isValid()) {
            Log.w("UpdateFragment.getCurrentDbVer database not valid");
            return -1;
        }

        return loader.getDbVersion();
    }

    private void updateDb(String conName, UpdateMessage updateProgress) throws IOException {
        Log.v("UpdateFragment.updateDb downloading database from network for con: " + conName);
        updateProgress.message("Downloading new database...");

        // Delete old
        getActivity().deleteDatabase(DbLoader.DATABASE_NAME);

        // Download
        File f = getActivity().getDatabasePath(DbLoader.DATABASE_NAME);
        OutputStream outStream = new FileOutputStream(f);
        UrlFileFetcher.fetch(outStream, BuildConfig.DATA_PATH + conName + "/conbook.sqlite");
    }

    /* === Map === */

    private void updateMap(String conName, UpdateMessage updateProgress) throws UpdaterAlertException {
        Log.v("UpdateFragment.updateMap downloading maps from network for con: " + conName);
        updateProgress.message("Downloading new maps...");

        // Download
        String downloadPath = BuildConfig.DATA_PATH + conName + "/maps.zip";
        MapDownloader downloader = new MapDownloader(getActivity(), downloadPath);
        if (!downloader.update()) {
            throw new UpdaterAlertException("Error updating map files. Clear the application data and try again.");
        }
    }

    /* === Return methods === */

    private abstract static class UpdateMessage {
        public abstract void message(String string);
    }

    public enum UpdaterReturn {
        None,
        OK,
        Error,
        Alert,
        UpdateApp
    }

    /* === Exceptions === */

    public class UpdaterAlertException extends Exception {
        private static final long serialVersionUID = -339763661511521013L;
        private String _message;
        private boolean _isFatal;

        public UpdaterAlertException(String message) {
            _message = message;
        }

        public UpdaterAlertException(String message, boolean fatal) {
            _message = message;
            _isFatal = fatal;
        }

        public String getMessage() {
            return _message;
        }

        public boolean getIsFatal() {
            return _isFatal;
        }
    }
}
