package net.myconbook.android.updater;

import com.google.gson.Gson;

import net.myconbook.android.BuildConfig;
import net.myconbook.android.model.ConVersionInfo;

import java.io.IOException;

public class ConVersionUpdateState extends BaseState {
    public ConVersionUpdateState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        String response = null;
        try {
            response = fetchString(BuildConfig.DATA_PATH + mGlobal.updateChecker.getConName() + "/version.json");
        } catch (IOException e) {
            return noalert(new UpdaterAlertException("Error fetching con info.", e));
        }

        mGlobal.updateChecker.mCurrentConVersionInfo = new Gson().fromJson(response, ConVersionInfo.class);

        return State.DbCheck;
    }
}
