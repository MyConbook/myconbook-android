package net.myconbook.android.updater;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.Log;
import net.myconbook.android.ui.UpdateFragment;

public class AppUpgradeCheckState extends BaseState {
    public AppUpgradeCheckState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        // Get app version from OS
        int curAppVer = BuildConfig.VERSION_CODE;
        int latestAppVer = getSharedPrefs().getInt("newestAppVer", 0);
        if (curAppVer > latestAppVer) {
            Log.v("USM:AppUpgradeCheckState.run package is newer than last run. " + curAppVer + " over " + latestAppVer);
            getSharedPrefs().edit().putInt("newestAppVer", curAppVer).apply();
            mGlobal.force = true;
        }

        // If we're not forcing an upgrade, and it's not time to fetchList, don't continue
        if (!mGlobal.force && !UpdateFragment.getShouldUpdate(mGlobal.context)) {
            return State.Exit;
        }

        return State.ListUpdate;
    }
}
