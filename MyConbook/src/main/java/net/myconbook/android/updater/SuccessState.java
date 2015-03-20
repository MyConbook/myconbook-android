package net.myconbook.android.updater;

import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SuccessState extends BaseState {
    public SuccessState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        SharedPreferences.Editor sp = getSharedPrefs().edit();

        // Store last successful update time
        sp.putLong("lastUpdateTime", System.currentTimeMillis());

        // Persist the update checker
        String savedCheckerJson = new Gson().toJson(mGlobal.updateChecker);
        sp.putString("savedChecker", savedCheckerJson);

        sp.apply();

        return State.Exit;
    }
}
