package net.myconbook.android.updater;

public class EntryConChangeState extends BaseState {
    public EntryConChangeState(UpdateStateMachine.InternalState global) {
        super(global);
    }

    @Override
    public State run() {
        mGlobal.updateChecker.overrideConName(mGlobal.entryState.newCon == null ? mGlobal.updateChecker.getFirstCon() : mGlobal.entryState.newCon);
        mGlobal.force = true; // All updates are considered "forced" on a con change
        return State.ConVersionUpdate;
    }
}
