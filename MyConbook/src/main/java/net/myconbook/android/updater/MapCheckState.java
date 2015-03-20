package net.myconbook.android.updater;

import net.myconbook.android.Log;

public class MapCheckState extends BaseState {
    public MapCheckState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        // Check maps version
        int newMapVer = mGlobal.updateChecker.getMapVersion();
        int curMapVer = getSharedPrefs().getInt("currentMapVer", -1);
        if (mGlobal.force || (newMapVer > curMapVer)) {
            Log.v("USM:MapCheckState.run upgrading map from " + curMapVer + " to " + newMapVer + " forced: " + mGlobal.force);
            return State.MapUpdate;
        }

        return State.Success;
    }
}
