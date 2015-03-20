package net.myconbook.android.updater;

import com.google.gson.Gson;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.model.UpdaterInfo;
import net.myconbook.android.ui.UpdateFragment;

import java.io.IOException;

public class ListUpdateState extends BaseState {
    public ListUpdateState(UpdateStateMachine.InternalState internalState) {
        super(internalState);
    }

    @Override
    public State run() {
        String response;
        try {
            response = fetchString(BuildConfig.DATA_PATH + "info.json");
        } catch (IOException e) {
            return noalert(new UpdaterAlertException("Error fetching con list.", e));
        }

        mGlobal.updateChecker.mUpdaterInfo = new Gson().fromJson(response, UpdaterInfo.class);

        if (mGlobal.updateChecker.isCurrentConInList() && (mGlobal.force || UpdateFragment.getShouldUpdate(mGlobal.context))) {
            // Start a con update check too
            return State.ConVersionUpdate;
        } else {
            // Exit normally
            return State.Exit;
        }
    }
}
