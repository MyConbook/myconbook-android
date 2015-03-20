package net.myconbook.android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.Log;
import net.myconbook.android.model.UpdaterInfo;
import net.myconbook.android.updater.State;
import net.myconbook.android.updater.UpdateChecker;
import net.myconbook.android.updater.UpdateStateMachine;

import java.util.List;

public class UpdateFragment extends Fragment {
    private AsyncFetchList mAsyncList;
    private AsyncFetchCon mAsyncCon;

    private UpdaterListener mListener;
    private SharedPreferences mSharedPrefs;
    private UpdateChecker mUpdateChecker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if (mUpdateChecker == null) {
            mUpdateChecker = UpdateChecker.load(getActivity());
        }
    }

    public void setUpdateListener(UpdaterListener listener) {
        mListener = listener;
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

    public boolean isUpdating() {
        return mAsyncList != null || mAsyncCon != null;
    }

    public String getConName() {
        return mUpdateChecker.getConName();
    }

    public UpdateChecker getUpdateChecker() {
        return mUpdateChecker;
    }

    private abstract class UpdaterTask <A> extends AsyncTask<A, String, UpdaterReturn> {
        protected UpdateStateMachine.InternalState resultState;

        protected UpdateStateMachine.UpdateMessageListener updateMessage = new UpdateStateMachine.UpdateMessageListener() {
            @Override
            public void onMessage(String string) {
                publishProgress(string);
            }
        };

        @Override
        protected void onProgressUpdate(String... values) {
            String lastMessage = values[0];
            mListener.onMessage(lastMessage);
        }

        @Override
        protected void onPostExecute(UpdaterReturn result) {
            Log.v("UpdateFragment.UpdaterTask.onPostExecute completed with result " + result);

            mUpdateChecker.save(getActivity());

            mAsyncList = null;
            mAsyncCon = null;

            switch (result) {
                case UpdateApp:
                    mListener.onNewerApp();
                    break;
                case Error:
                    mListener.onAlert(resultState.lastError.getMessage(), true);
                    break;
                case Alert:
                    mListener.onAlert(resultState.lastError.getMessage(), false);
                    break;
                case OK:
                    onOkAction();
                    break;
                default:
                    mListener.onNop();
                    break;
            }
        }

        protected abstract void onOkAction();
    }

    private class AsyncFetchList extends UpdaterTask<Boolean> {
        @Override
        protected void onPreExecute() {
            publishProgress("Fetching convention list...");
        }

        @Override
        protected UpdaterReturn doInBackground(Boolean... arg0) {
            boolean isForcingUpgrade = arg0[0];

            Log.v("UpdateFragment.AsyncFetchList.doInBackground forced: " + isForcingUpgrade);

            UpdateStateMachine.EntryState entryState = new UpdateStateMachine.EntryState(State.EntryRefreshConList, getActivity());
            entryState.updateChecker = mUpdateChecker;
            entryState.force = isForcingUpgrade;

            UpdateStateMachine stateMachine = new UpdateStateMachine();
            resultState = stateMachine.run(entryState);
            if (resultState.lastError != null) {
                return resultState.lastErrorWasAlert ? UpdaterReturn.Alert : UpdaterReturn.Error;
            }

            mUpdateChecker = resultState.updateChecker;

            // Warn to upgrade if newer version
            int curAppVer = BuildConfig.VERSION_CODE;
            int newAppVer = mUpdateChecker.getAppVersion();
            if (newAppVer > curAppVer) {
                // Recommend upgrade from Market
                Log.v("UpdateFragment.AsyncFetchList.doInBackground recommending upgrade from app ver " + curAppVer + " to " + newAppVer);
                return UpdaterReturn.UpdateApp;
            }

            return UpdaterReturn.OK;
        }

        @Override
        protected void onOkAction() {
            mListener.onListUpdate(resultState.updateChecker.getCons());
        }
    }

    private class AsyncFetchCon extends UpdaterTask<String> {
        @Override
        protected void onPreExecute() {
            publishProgress("Fetching convention data...");
        }

        @Override
        protected UpdaterReturn doInBackground(String... arg0) {
            String conName = arg0[0];
            Log.v("UpdateFragment.AsyncFetchCon.doInBackground fetching data for " + conName);

            UpdateStateMachine.EntryState entryState = new UpdateStateMachine.EntryState(State.EntryConChange, getActivity());
            entryState.newCon = conName;
            entryState.messageListener = updateMessage;
            entryState.updateChecker = mUpdateChecker;

            UpdateStateMachine stateMachine = new UpdateStateMachine();
            resultState = stateMachine.run(entryState);
            if (resultState.lastError != null) {
                return resultState.lastErrorWasAlert ? UpdaterReturn.Alert : UpdaterReturn.Error;
            }

            mUpdateChecker = resultState.updateChecker;

            return UpdaterReturn.OK;
        }

        @Override
        protected void onOkAction() {
            mListener.onConUpdate();
        }
    }

    /* === Callback === */

    public interface UpdaterListener {
        public void onNewerApp();

        public void onListUpdate(List<UpdaterInfo.ConventionInfo> cons);

        public void onConUpdate();

        public void onMessage(String message);

        public void onAlert(String message, boolean fatal);

        public void onNop();
    }

    /* === Actual update code === */

    public static boolean getShouldUpdate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdateTime = sp.getLong("lastUpdateTime", 0);
        long now = System.currentTimeMillis();
        Log.d("UpdateFragment.getShouldUpdate minutes since last fetchList: " + ((now - lastUpdateTime) / 1000f / 60f));

        return (lastUpdateTime + (60 * 60 * 1000)) < now;
    }

    public enum UpdaterReturn {
        None,
        OK,
        Error,
        Alert,
        UpdateApp
    }
}
